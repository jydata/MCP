package com.jiuye.mcp.job.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author kevin
 */
public class JobTriggerPool {

    private static final Logger logger = LoggerFactory.getLogger(JobTriggerPool.class.getName());

    private static JobTriggerPool triggerPool = new JobTriggerPool();

    private ThreadPoolExecutor executors = new ThreadPoolExecutor(
            20,
            200,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(100000),
            new ThreadPoolExecutor.CallerRunsPolicy());

    public void addJobTrigger(final long jobId) {
        executors.execute(new Runnable() {
            @Override
            public void run() {
                // 作业执行路由
                JobExecutor.trigger(jobId);
            }
        });
    }

    public void shutdown(){
        executors.shutdownNow();
        logger.info(">>>>>>JobTriggerPool executors shutdown success<<<<<<");
    }

    public static void trigger(long jobId){
        triggerPool.addJobTrigger(jobId);
    }

    public static void stop(){
        triggerPool.shutdown();
    }
}
