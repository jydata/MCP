package com.jiuye.mcp.job.jobtask;

import com.jiuye.mcp.job.executor.QuartzManager;
import com.jiuye.mcp.job.log.JobLogger;
import com.jiuye.mcp.param.enums.JobTypeEnum;
import com.jiuye.mcp.server.model.SourceRunnerParam;
import com.jiuye.mcp.server.model.job.JobDefineEntity;
import com.jiuye.mcp.server.model.job.JobExecResult;
import com.jiuye.mcp.server.runner.impl.MySQLFullSourceRunnerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Full synchronization job
 *
 * @author kevin
 * @date 2018-10-16
 */
public class FullSyncExecutor extends AbstractJobExecutor{

    private static final Logger logger = LoggerFactory.getLogger(FullSyncExecutor.class.getName());

    @Override
    public String getJobType() {
        return JobTypeEnum.FULL.getCode();
    }

    @Override
    public JobExecResult execute(JobDefineEntity job) {
        logger.info(">>>>>>FullSyncExecutor is running<<<<<<");
        JobExecResult execResult = new JobExecResult();

        // 根据routeId查询源端和终端数据库连接信息
        Long routeId = job.getRouteId();
        SourceRunnerParam dbParam = QuartzManager.dataSourceSyncService.querySrcAndTarLinkByRouteId(job);
        String agent = QuartzManager.jobProjectMapper.queryAgent(job.getProjectId());
        if (null == dbParam){
            execResult.setSuccess(false);
            execResult.setLog("FullSyncExecutor metadata route invalid");
            JobLogger.log("FullSyncExecutor metadata route invalid.");
        }else {
            dbParam.setJobId(job.getJobId());
            dbParam.setJobName(job.getJobName());
            dbParam.setAgent(agent);
//            dbParam.setBusStartTime(job.getBusStartTime());
//            dbParam.setBusEndTime(job.getBusEndTime());
            JobLogger.log("<br>FullSyncExecutor params, routeId:{}, sourceId:{}, targetId:{}.", routeId, dbParam.getSourceId(), dbParam.getTargetId());

            // 根据routeId查询源端同步表
            /*List<String> tableList = QuartzManager.dataSourceSyncService.querySyncTableByJobId(job.getJobId(), job.getSrcDb(), job.getSrcTable());
            if (null != tableList && tableList.size() > 0){
                Map<String, String> syncTablesMap = new HashMap<>();
                for (String dbTable : tableList){
                    // TODO... whereClause暂时没做
                    syncTablesMap.put(dbTable, null);
                }
                dbParam.setFullSyncTablesMap(syncTablesMap);
            }*/

            JobLogger.log("FullSyncExecutor source runner execute begin.");
            // 传递routeId,源端/终端 数据库连接信息（ip,port,url,driver,username,password）
            QuartzManager.fullSyncRunner.fromSourceFull(dbParam);
            if (waitForFullJobExecutorEnd(dbParam.getJobId())) {
                execResult.setSuccess(true);
                logger.info("FullSyncExecutor source runner execute end.");
                JobLogger.log("FullSyncExecutor source runner execute end.");
            } else {
                execResult.setSuccess(false);
                logger.info("FullSyncExecutor source runner execute Failed.");
                JobLogger.log("FullSyncExecutor source runner execute Failed.");
            }
        }

        return execResult;
    }

    /**
     * 等待Full job执行完成
     *
     * @param jobId
     * @return
     * @author dove
     * @time 2020-03-17
     */
    private boolean waitForFullJobExecutorEnd(Long jobId) {
        boolean flag = true;
        try {
            for (; ; ) {
                if (QuartzManager.threadPoolExecutorMap.get(jobId) == null) {
                    logger.info("Thread pool executor is execute over.");
                    flag = true;
                    break;
                }
                if (QuartzManager.threadPoolExecutorMap.get(jobId).isShutdown()) {
                    QuartzManager.threadPoolExecutorMap.remove(jobId);
                    logger.info("Thread pool executor had been shutdown.");
                    flag = false;
                    break;
                }
                try {
                    Thread.sleep(30000);
                    logger.info("Wait For Full Job Running ... Job id: {}", jobId);
                } catch (InterruptedException e) {
                }
            }
        } catch (Throwable e) {
            return false;
        }
        return flag;
    }
}
