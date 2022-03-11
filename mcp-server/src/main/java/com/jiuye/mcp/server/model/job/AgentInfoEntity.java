package com.jiuye.mcp.server.model.job;

import java.io.Serializable;

/**
 * agent info
 *
 * @author kevin
 * @date 2018-10-26
 */
public class AgentInfoEntity implements Serializable {

    private static final long serialVersionUID = -3369977911552149081L;

    private String agentId;

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }
}
