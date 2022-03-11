package com.jiuye.mcp.job.jobtask;

import com.jiuye.mcp.job.executor.QuartzManager;
import com.jiuye.mcp.job.log.JobLogger;
import com.jiuye.mcp.param.enums.JobTypeEnum;
import com.jiuye.mcp.server.model.job.JobDefineEntity;
import com.jiuye.mcp.server.model.job.JobExecResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Metadata synchronization job
 *
 * @author zg
 * @date 2018-12-8
 */
public class MetadataSyncExecutor extends AbstractJobExecutor{

    private static final Logger logger = LoggerFactory.getLogger(MetadataSyncExecutor.class.getName());

    @Override
    public String getJobType() {
        return JobTypeEnum.METADATA.getCode();
    }

    @Override
    public JobExecResult execute(JobDefineEntity job) {
        JobExecResult execResult = new JobExecResult();

        try {
            JobLogger.log("MetadataSyncExecutor load metadate begin.");
            // 先删除元数据，再重新加载
            QuartzManager.dataSourceSyncService.loadMetadataForJob(job);
            JobLogger.log("MetadataSyncExecutor load metadate end.");

            execResult.setSuccess(true);
        } catch (Exception e) {
            execResult.setSuccess(false);
            execResult.setLog("MetadataSyncExecutor Exception");
            logger.error("MetadataSyncExecutor Exception:", e);

            // 更新日志记录
            JobLogger.log("MetadataSyncExecutor execute exception. {}", e.getMessage());
        }

        return execResult;
    }
}
