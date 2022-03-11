package com.jiuye.mcp.server.model.home;

import java.io.Serializable;

/**
 * @author dove
 */
public class HomeSyncAgentEntity implements Serializable {
    private static final long serialVersionUID = 532419961500615569L;
    /**
     * agentId
     */
    private long agentId;
    /**
     * agent名称
     */
    private String agentName;
    /**
     * agent对应的record总和
     */
    private long recordSum;

    public long getAgentId() {
        return agentId;
    }

    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public long getRecordSum() {
        return recordSum;
    }

    public void setRecordSum(long recordSum) {
        this.recordSum = recordSum;
    }
}
