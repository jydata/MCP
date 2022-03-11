package com.jiuye.mcp.job.executor;

import com.jiuye.mcp.job.jobtask.DDLBatchTransformExecutor;
import com.jiuye.mcp.job.jobtask.FullSyncExecutor;
import com.jiuye.mcp.job.jobtask.IncrementSyncExecutor;
import com.jiuye.mcp.job.jobtask.MetadataSyncExecutor;
import com.jiuye.mcp.job.log.JobLogFileManager;
import com.jiuye.mcp.job.log.JobLogger;
import com.jiuye.mcp.param.enums.JobSchedulerEnum;
import com.jiuye.mcp.param.enums.JobStatusEnum;
import com.jiuye.mcp.param.enums.JobTypeEnum;
import com.jiuye.mcp.server.model.job.JobDefineEntity;
import com.jiuye.mcp.server.model.job.JobExecResult;
import com.jiuye.mcp.server.model.job.JobProjectEntity;
import com.jiuye.mcp.server.model.job.JobSchedulerLogEntity;
import com.jiuye.mcp.server.model.meta.MetaDatarouteEntity;
import com.jiuye.mcp.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

/**
 * job executor
 *
 * @author kevin
 * @date 2018-10-16
 */
@Component
public class JobExecutor {

    private static final Logger logger = LoggerFactory.getLogger(JobExecutor.class.getName());

    /**
     * 作业执行路由
     * @param jobId
     */
    public static void trigger(long jobId) {
        logger.info(">>>>>>JobExecutor trigger jobId:{}<<<<<<", jobId);
        // Retry number of retryCount from property files
        int failRetryCount = 0;
        String retryCountStr = QuartzManager.config.getRetryCount();
        if (StringUtils.isNotBlank(retryCountStr)){
            Integer retryCount = Integer.valueOf(retryCountStr);
            if (retryCount > 0){
                failRetryCount = retryCount;
            }
        }

        // 根据jobId查询作业信息
        JobDefineEntity jobDefine = QuartzManager.jobDefinitionMapper.queryById(jobId);
        if(null == jobDefine){
            return;
        }

        // 根据projectId查询项目信息
        JobProjectEntity projectEntity = QuartzManager.jobProjectMapper.queryProjectById(jobDefine.getProjectId());
        if (null == projectEntity){
            return;
        }
        // 更新project总执行数
        projectEntity.setExecuteNum(projectEntity.getExecuteNum() + 1);
        QuartzManager.jobProjectMapper.updateProject(projectEntity);

        // 日志信息
        JobSchedulerLogEntity logEntity = new JobSchedulerLogEntity();
        BeanUtils.copyProperties(jobDefine, logEntity);

        // 路由信息
        Long routeId = 0L;
        if(jobDefine.getRouteId() != 0L && jobDefine.getRouteId() != null) {
            routeId = jobDefine.getRouteId();
        }
        MetaDatarouteEntity routeEntity = QuartzManager.metaDatarouteMapper.queryRouteById(routeId);
        if(null != routeEntity){
            logEntity.setRouteName(routeEntity.getRouteName());
        }

        Timestamp currTime = DateUtil.getCurrentTime();

        logEntity.setProjectName(projectEntity.getProjectName());
        logEntity.setAgent(projectEntity.getProjectAgent());
        logEntity.setTriggerTime(currTime);
        logEntity.setTriggerCode(JobSchedulerEnum.SUCCESS.getCode());
        logEntity.setHandleCode(JobSchedulerEnum.INIT.getCode());
        // 添加日志记录
        int saveNum = QuartzManager.jobSchedulerLogMapper.save(logEntity);
        if (saveNum != 1){
            return;
        }

        Long logId = logEntity.getLogId();
        // 维护线程
        QuartzManager.threadMap.put(logId, Thread.currentThread());

        // 创建日志文件目录
        String fileName = JobLogFileManager.generateFileName(currTime, logId);
        JobLogFileManager.jobFileHolder.set(fileName);
        // 写入日志
        JobLogger.log("Job execute start. Param:jobId:{}, failRetryCount:{}.", jobId, failRetryCount);

        String jobType = jobDefine.getJobType();
        // 更新日志、作业的状态
        logEntity.setHandleTime(DateUtil.getCurrentTime());
        logEntity.setHandleCode(JobSchedulerEnum.RUNNING.getCode());
        QuartzManager.jobSchedulerLogMapper.updateLog(logEntity);
        jobDefine.setStatus(JobStatusEnum.RUNNING.getCode());
        QuartzManager.jobDefinitionMapper.update(jobDefine);
        try {
            if (JobTypeEnum.INCREMENT.getCode().equals(jobType)){
                // 增量同步
                logger.info(">>>>>>JobExecutor trigger increment<<<<<<");
                JobLogger.log("MySQL increment synchronization job init.");

                IncrementSyncExecutor incrementExecutor = new IncrementSyncExecutor();
                JobExecResult jobExecResult = incrementExecutor.execute(jobDefine);
                if (jobExecResult == null){
                    JobLogger.log("Increment synchronization job running...");
                } else {
                    // 失败重试
                    for(int i = 0; i < failRetryCount; i++){
                        JobLogger.log("Increment synchronization job retry {}, total {}.", i, failRetryCount);
                        jobExecResult = incrementExecutor.execute(jobDefine);
                        if(jobExecResult == null){
                            JobLogger.log("Increment synchronization job running...");
                            //跳出整个循环，switch,for,while等
                            break;
                        }
                    }

                    //job执行失败
                    if(jobExecResult != null){
                        logEntity.setHandleCode(JobSchedulerEnum.FAIL.getCode());
                        logEntity.setHandleMsg(jobExecResult.getLog());
                        JobLogger.log("Increment synchronization job fail.");
                    }
                }
            } else if (JobTypeEnum.FULL.getCode().equals(jobType)){
                // 全量同步
                logger.info(">>>>>>JobExecutor trigger full<<<<<<");
                JobLogger.log("Full synchronization job init.");

                FullSyncExecutor fullSyncExecutor = new FullSyncExecutor();
                JobExecResult execResult = fullSyncExecutor.execute(jobDefine);
                if (!execResult.isSuccess()){
                    // 失败重试
                    for(int i = 0; i < failRetryCount; i++){
                        JobLogger.log("Full synchronization job retry {}, total {}.", i, failRetryCount);
                        execResult = fullSyncExecutor.execute(jobDefine);
                        if(execResult.isSuccess()){
                            logEntity.setHandleCode(JobSchedulerEnum.SUCCESS.getCode());
                            JobLogger.log("Full synchronization job success.");
                            break;//跳出整个循环，switch,for,while等
                        }
                    }

                    //job执行失败
                    if(!execResult.isSuccess()){
                        logEntity.setHandleCode(JobSchedulerEnum.FAIL.getCode());
                        logEntity.setHandleMsg(execResult.getLog());
                        JobLogger.log("Full synchronization job fail.");
                    }
                } else {
                    logEntity.setHandleCode(JobSchedulerEnum.SUCCESS.getCode());
                    JobLogger.log("Full synchronization job success.");
                }
            } else if(JobTypeEnum.METADATA.getCode().equals(jobType)){
                // 元数据同步
                logger.info(">>>>>>JobExecutor trigger metadata<<<<<<");
                JobLogger.log("Metadata synchronization job init.");

                MetadataSyncExecutor metadataSyncExecutor = new MetadataSyncExecutor();
                JobExecResult metadataResult = metadataSyncExecutor.execute(jobDefine);
                if (!metadataResult.isSuccess()){
                    // 失败重试
                    for(int i = 0; i < failRetryCount; i++){
                        JobLogger.log("Metadata synchronization job retry {}, total {}.", i, failRetryCount);
                        metadataResult = metadataSyncExecutor.execute(jobDefine);
                        if(metadataResult.isSuccess()){
                            logEntity.setHandleCode(JobSchedulerEnum.SUCCESS.getCode());
                            JobLogger.log("Metadata synchronization job success.");
                            break;//跳出整个循环，switch,for,while等
                        }
                    }

                    //job执行失败
                    if(!metadataResult.isSuccess()){
                        logEntity.setHandleCode(JobSchedulerEnum.FAIL.getCode());
                        logEntity.setHandleMsg(metadataResult.getLog());
                        JobLogger.log("Metadata synchronization job fail.");
                    }
                } else {
                    logEntity.setHandleCode(JobSchedulerEnum.SUCCESS.getCode());
                    JobLogger.log("Metadata synchronization job success.");
                }
            } else if (JobTypeEnum.DDL.getCode().equals(jobType)){
                // DDL批量转换
                logger.info(">>>>>>JobExecutor trigger DDL<<<<<<");
                JobLogger.log("DDL batch Transformation job init.");

                DDLBatchTransformExecutor ddlExecutor = new DDLBatchTransformExecutor();
                JobExecResult columnResult = ddlExecutor.execute(jobDefine);
                if (!columnResult.isSuccess()){
                    // 失败重试
                    for(int i = 0; i < failRetryCount; i++){
                        JobLogger.log("DDL synchronization job retry {}, total {}.", i, failRetryCount);
                        columnResult = ddlExecutor.execute(jobDefine);
                        if(columnResult.isSuccess()){
                            logEntity.setHandleCode(JobSchedulerEnum.SUCCESS.getCode());
                            JobLogger.log("DDL batch Transformation job success.");
                            break;//跳出整个循环，switch,for,while等
                        }
                    }

                    //job执行失败
                    if(!columnResult.isSuccess()){
                        logEntity.setHandleCode(JobSchedulerEnum.FAIL.getCode());
                        logEntity.setHandleMsg(columnResult.getLog());
                        JobLogger.log("DDL batch Transformation job fail.");
                    }
                } else {
                    logEntity.setHandleCode(JobSchedulerEnum.SUCCESS.getCode());
                    JobLogger.log("DDL batch Transformation success.");
                }
            }
        } catch (Exception e) {
            logger.error(">>>>>>JobExecutor DDL batch Transformation is error<<<<<<", e);

            logEntity.setHandleCode(JobSchedulerEnum.FAIL.getCode());
            logEntity.setHandleMsg(e.getMessage());
            // 记录异常日志
            JobLogger.log("JobExecutor is error:{}.", e.getMessage());
        }

        // 同步更新
        synchronized(JobExecutor.class){
            //更新job_define表,调度时间和状态
            JobDefineEntity entity = new JobDefineEntity();
            JobProjectEntity jobProjectEntity = QuartzManager.jobProjectMapper.queryProjectById(jobDefine.getProjectId());
            entity.setJobId(jobId);
            switch (logEntity.getHandleCode()){
                case 200:
                    entity.setStatus(JobStatusEnum.SUCCESS.getCode());
                    jobProjectEntity.setSuccessNum(jobProjectEntity.getSuccessNum() + 1);
                    logEntity.setHandleCode(JobSchedulerEnum.SUCCESS.getCode());
                    logEntity.setHandleEndTime(DateUtil.getCurrentTime());
                    logger.info(jobId + "-----------------------" + JobSchedulerEnum.SUCCESS.getCode() + "====" + jobProjectEntity.getExecuteNum());
                    break;
                case 500:
                    entity.setStatus(JobStatusEnum.FAIL.getCode());
                    jobProjectEntity.setFailNum(jobProjectEntity.getFailNum() + 1);
                    logEntity.setHandleCode(JobSchedulerEnum.FAIL.getCode());
                    logEntity.setHandleEndTime(DateUtil.getCurrentTime());
                    logger.info(jobId + "-----------------------" + JobSchedulerEnum.FAIL.getCode() + "====" + jobProjectEntity.getExecuteNum());
                    break;
                default:
                    entity.setStatus(JobStatusEnum.RUNNING.getCode());
                    logEntity.setHandleCode(JobSchedulerEnum.RUNNING.getCode());
                    logger.info(jobId + "-----------------------" + JobSchedulerEnum.RUNNING.getCode() + "====" + jobProjectEntity.getExecuteNum());
                    break;
            }

            QuartzManager.jobDefinitionMapper.update(entity);
            //更新project执行次数信息
            QuartzManager.jobProjectMapper.updateProject(jobProjectEntity);
            QuartzManager.jobSchedulerLogMapper.updateLog(logEntity);
        }
    }

}
