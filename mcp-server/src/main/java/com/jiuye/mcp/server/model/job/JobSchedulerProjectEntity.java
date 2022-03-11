package com.jiuye.mcp.server.model.job;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhaopeng
 * @date 2018-12-10
 */
public class JobSchedulerProjectEntity implements Serializable {

    private static final long serialVersionUID = 1898265864781096610L;

    List<Long> jobList;
    JobSchedulerLogEntity entity;

    public List<Long> getJobList() {
        return jobList;
    }

    public void setJobList(List<Long> jobList) {
        this.jobList = jobList;
    }

    public JobSchedulerLogEntity getEntity() {
        return entity;
    }

    public void setEntity(JobSchedulerLogEntity entity) {
        this.entity = entity;
    }
}
