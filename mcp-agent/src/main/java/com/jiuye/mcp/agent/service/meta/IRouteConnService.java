package com.jiuye.mcp.agent.service.meta;

import com.jiuye.mcp.agent.model.McpRouteConnEntity;

import java.util.List;

/**
 * @author jepson
 * @date 2018/9/29 4:56 PM
 */
public interface IRouteConnService {

    void getRouteConnInfo(List<McpRouteConnEntity> mcpRouteConnEntityList) throws Exception;
}
