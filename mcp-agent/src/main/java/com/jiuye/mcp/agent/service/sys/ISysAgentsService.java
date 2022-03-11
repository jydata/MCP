package com.jiuye.mcp.agent.service.sys;

import java.sql.Timestamp;

/**
 * @author jepson
 * @date 2019/6/20 6:25 PM
 */
public interface ISysAgentsService {


    /**
     * 注册agent
     *
     * @param agentId
     * @param agentName
     * @param agentPort
     * @param hostname
     * @param startTime
     */
    void saveAgentWithRunning(Long agentId, String agentName, Integer agentPort, String hostname, Timestamp startTime);

    /**
     * 取消注册agent
     *
     * @param agentId
     * @param agentPort
     * @param endTime
     */
    void updateAgentWithStopped(Long agentId, Integer agentPort, Timestamp endTime);
}
