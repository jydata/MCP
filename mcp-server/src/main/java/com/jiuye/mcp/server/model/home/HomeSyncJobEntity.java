package com.jiuye.mcp.server.model.home;

import java.io.Serializable;

/**
 * @author dove
 * @date 2019-06-19
 */
public class HomeSyncJobEntity implements Serializable {
    private static final long serialVersionUID = 532419961500615569L;

    /**
     * jobId
     */
    private long jobId;
    /**
     * job名称
     */
    private String jobName;
    /**
     * job对应的record总和
     */
    private long recordSum;

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public long getRecordSum() {
        return recordSum;
    }

    public void setRecordSum(long recordSum) {
        this.recordSum = recordSum;
    }
}
