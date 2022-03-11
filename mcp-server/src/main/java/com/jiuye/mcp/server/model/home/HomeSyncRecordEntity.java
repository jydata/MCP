package com.jiuye.mcp.server.model.home;


import java.io.Serializable;

/**
 * @author zhaopeng
 * @date 2018-12-27
 */
public class HomeSyncRecordEntity implements Serializable {

    private static final long serialVersionUID = 532419961500615569L;

    /**
     * 作业id
     */
    private long jobid;
    /**
     * 路由id
     */
    private long routeid;
    /**
     * 路由名称
     */
    private String routeName;
    /**
     * agent id
     */
    private long agentid;
    /**
     * 记录数
     */
    private long record;

    public long getJobid() {
        return jobid;
    }

    public void setJobid(long jobid) {
        this.jobid = jobid;
    }

    public long getRouteid() {
        return routeid;
    }

    public void setRouteid(long routeid) {
        this.routeid = routeid;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public long getAgentid() {
        return agentid;
    }

    public void setAgentid(long agentid) {
        this.agentid = agentid;
    }

    public long getRecord() {
        return record;
    }

    public void setRecord(long record) {
        this.record = record;
    }
}
