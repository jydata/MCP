package com.jiuye.mcp.agent.dao.sys;

import com.jiuye.mcp.agent.model.SysAgentsEntity;
import org.springframework.stereotype.Component;

/**
 * @author jepson
 * @date 2019/6/20 2:53 PM
 */
@Component
public interface SysDataInfoMapper {

    /**
     * 保存agent 维护
     *
     * @param sysAgentsEntity
     */
    void insertAgent(SysAgentsEntity sysAgentsEntity);


    void updateAgentWithRunning(SysAgentsEntity sysAgentsEntity);

    void updateAgentWithStopped(SysAgentsEntity sysAgentsEntity);

    Integer selectExists(SysAgentsEntity sysAgentsEntity);

}
