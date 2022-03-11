package com.jiuye.mcp.agent.service.sys.impl;

import com.jiuye.mcp.agent.dao.sys.SysDataInfoMapper;
import com.jiuye.mcp.agent.model.SysAgentsEntity;
import com.jiuye.mcp.agent.service.sys.ISysAgentsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

/**
 * @author jepson
 * @date 2019/6/20 6:26 PM
 */
@Service
public class SysAgentsServiceImpl implements ISysAgentsService {
    private final static Logger logger = LoggerFactory.getLogger(SysAgentsServiceImpl.class);

    @Autowired
    private SysDataInfoMapper sysDataInfoMapper;

    /**
     * running保存
     *
     * @param agentId
     * @param agentName
     * @param agentPort
     * @param hostname
     * @param startTime
     */
    @Override
    public void saveAgentWithRunning(Long agentId, String agentName, Integer agentPort, String hostname, Timestamp startTime) {
        SysAgentsEntity agentEntity = new SysAgentsEntity();
        agentEntity.setAgentId(agentId);
        agentEntity.setAgentPort(agentPort);

        try {
            if (sysDataInfoMapper.selectExists(agentEntity) == 0) {
                agentEntity.setAgentName(agentName);
                agentEntity.setAgentStatus("running");
                agentEntity.setHostname(hostname);
                agentEntity.setStartTime(startTime);
                sysDataInfoMapper.insertAgent(agentEntity);
            } else {
                agentEntity.setAgentStatus("running");
                agentEntity.setStartTime(startTime);
                sysDataInfoMapper.updateAgentWithRunning(agentEntity);
            }
        } catch (Exception ex) {
            logger.error("Save agent with running error.", ex);
        }

    }

    /**
     * stopped保存
     *
     * @param agentId
     * @param agentPort
     * @param endTime
     */
    @Override
    public void updateAgentWithStopped(Long agentId, Integer agentPort, Timestamp endTime) {
        SysAgentsEntity agentEntity = new SysAgentsEntity();
        agentEntity.setAgentId(agentId);
        agentEntity.setAgentPort(agentPort);

        try {
            if (sysDataInfoMapper.selectExists(agentEntity) != 0) {
                agentEntity.setAgentStatus("stopped");
                agentEntity.setEndTime(endTime);
                sysDataInfoMapper.updateAgentWithStopped(agentEntity);
            }
        } catch (Exception ex) {
            logger.error("Update agent with stopped error.", ex);
        }

    }

}
