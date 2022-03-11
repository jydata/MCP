package com.jiuye.mcp.job.jobtask;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.jiuye.mcp.job.executor.QuartzManager;
import com.jiuye.mcp.job.log.JobLogger;
import com.jiuye.mcp.param.enums.JobTypeEnum;
import com.jiuye.mcp.server.model.SourceRunnerParam;
import com.jiuye.mcp.server.model.job.JobDefineEntity;
import com.jiuye.mcp.server.model.job.JobExecResult;
import com.jiuye.mcp.server.runner.impl.MySQLIncrementSourceRunnerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MySQL increment synchronization job
 *
 * @author kevin
 * @date 2018-10-16
 */
public class IncrementSyncExecutor extends AbstractJobExecutor{

    private static final Logger logger = LoggerFactory.getLogger(IncrementSyncExecutor.class.getName());

    @Override
    public String getJobType() {
        return JobTypeEnum.INCREMENT.getCode();
    }

    @Override
    public JobExecResult execute(JobDefineEntity job) {
        logger.info(">>>>>>IncrementSyncExecutor is running<<<<<<");
        JobExecResult execResult = new JobExecResult();

        // 根据routeId查询源端和终端数据库连接信息
        Long routeId = job.getRouteId();
        SourceRunnerParam dbParam = QuartzManager.dataSourceSyncService.querySrcAndTarLinkByRouteId(job);
        String agent = QuartzManager.jobProjectMapper.queryAgent(job.getProjectId());
        if (null == dbParam){
            execResult.setSuccess(false);
            execResult.setLog("IncrementSyncExecutor metadata route invalid");
            JobLogger.log("IncrementSyncExecutor metadata route invalid.");
        }else {
            dbParam.setJobId(job.getJobId());
            dbParam.setJobName(job.getJobName());
            dbParam.setAgent(agent);
            dbParam.setRecentBinlog(job.getRecentBinlog());
            dbParam.setBinlogName(job.getBinlogName());
            dbParam.setBinlogPosition(job.getBinlogPosition());
            dbParam.setBusStartTime(job.getBusStartTime());
            dbParam.setBusEndTime(job.getBusEndTime());
            JobLogger.log("IncrementSyncExecutor params, routeId:{}, sourceId:{}, targetId:{}.", routeId, dbParam.getSourceId(), dbParam.getTargetId());

            // 根据routeId查询源端同步表(暂不用传)
            /*List<String> tableList = QuartzManager.dataSourceSyncService.querySyncTableByRouteId(routeId);
            dbParam.setSrcTableList(tableList);*/

            JobLogger.log("IncrementSyncExecutor source runner execute begin.");
            // 传递routeId,源端/终端 数据库连接信息（ip,port,url,driver,username,password）
            MySQLIncrementSourceRunnerImpl runner = QuartzManager.mysqlRunner;
            BinaryLogClient client = runner.fromSourceIncre(dbParam);
            // TODO...clientMap莫名被初始数据了，runner.fromSourceIncre(dbParam);
            QuartzManager.clientMap.put(job.getJobId(), client);
            JobLogger.log("IncrementSyncExecutor source runner execute running.");
            try {
                client.connect();
                // 创建链接等待10秒
                Thread.sleep(10000);
            } catch (Exception e) {
                logger.error("MySQL Client Connection Failed!");
            }
            if (client.isConnected()) {
                return null;
            }
        }

        return execResult;
    }

}
