package com.jiuye.mcp.server.dao.sys;

import com.jiuye.mcp.server.model.meta.SysAgentsEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * sys_agent mapper
 *
 * @author dove
 * @date 2019-07-01
 */
@Component
public interface SysAgentMapper {

    /**
     * 查询各状态的agent
     *
     * @return
     */
    List<SysAgentsEntity> queryAgents();
}
