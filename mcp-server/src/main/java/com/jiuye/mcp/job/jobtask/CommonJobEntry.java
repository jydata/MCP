package com.jiuye.mcp.job.jobtask;

import com.jiuye.mcp.job.executor.JobTriggerPool;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 作业公共入口
 *
 * @author kevin
 * @date 2018-10-16
 */
public class CommonJobEntry implements Job{

    private static final Logger logger = LoggerFactory.getLogger(FullSyncExecutor.class.getName());

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long jobId = Long.parseLong(context.getJobDetail().getKey().getName());
        logger.info(">>>>>>CommonJobEntry execute jobId:{}<<<<<<", jobId);

        // 线程池trigger job
        JobTriggerPool.trigger(jobId);
    }
}
