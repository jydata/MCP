package com.jiuye.mcp.job.jobtask;

import com.jiuye.mcp.job.executor.QuartzManager;
import com.jiuye.mcp.job.log.JobLogger;
import com.jiuye.mcp.param.enums.JobTypeEnum;
import com.jiuye.mcp.server.model.ValidateResult;
import com.jiuye.mcp.server.model.job.JobDefineEntity;
import com.jiuye.mcp.server.model.job.JobExecResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DDLBatchTransformExecutor job
 *
 * @author kevin
 * @date 2018-10-16
 */
public class DDLBatchTransformExecutor extends AbstractJobExecutor{

    private static final Logger logger = LoggerFactory.getLogger(DDLBatchTransformExecutor.class.getName());

    @Override
    public String getJobType() {
        return JobTypeEnum.DDL.getCode();
    }

    @Override
    public JobExecResult execute(JobDefineEntity job) {
        JobExecResult execResult = new JobExecResult();

        /**
         * 1、根据routeId查询路由信息，数据库连接信息
         * 2、同步所有列名信息
         */
        try {
            // 根据routeId查询源端数据库连接信息
            Long routeId = job.getRouteId();
            JobLogger.log("DDLBatchTransformExecutor load metadate begin.");

            ValidateResult ddlResult = QuartzManager.ddlService.batchExecHBaseSQL(routeId, job.getJobId(), job.getSourceId(), job.getSrcDb(), job.getTargetId(), job.getTargetSchemaId(), job.getRuleId(), job.getCreateUser());
            JobLogger.log("DDLBatchTransformExecutor load metadate end.");
            //将ddl作业执行结果返回
            execResult.setLog(ddlResult.getMessage());
            if(ddlResult.isFlag()){
                execResult.setSuccess(true);
                JobLogger.log("DDLBatchTransformExecutor true. {}", ddlResult.getMessage());
            }else {
                execResult.setSuccess(false);
                JobLogger.log("DDLBatchTransformExecutor false. {}", ddlResult.getMessage());
            }
        } catch (Exception e) {
            execResult.setSuccess(false);
            execResult.setLog("DDLBatchTransformExecutor Exception");
            logger.error("DDLBatchTransformExecutor Exception:", e);

            // 更新日志记录
            JobLogger.log("DDLBatchTransformExecutor execute exception. ", e.getMessage());
        }

        return execResult;
    }
}
