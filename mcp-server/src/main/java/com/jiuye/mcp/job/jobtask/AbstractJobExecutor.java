package com.jiuye.mcp.job.jobtask;

import com.jiuye.mcp.server.model.job.JobDefineEntity;
import com.jiuye.mcp.server.model.job.JobExecResult;

/**
 *
 * @author kevin
 * @date 2018-10-16
 */
public abstract class AbstractJobExecutor {

    public abstract String getJobType();

    public abstract JobExecResult execute(JobDefineEntity job);

    /**
     * 匹配jobType
     * @param job
     * @return
     */
    public boolean match(JobDefineEntity job){
        return job.getJobType().equals(this.getJobType());
    }
}
