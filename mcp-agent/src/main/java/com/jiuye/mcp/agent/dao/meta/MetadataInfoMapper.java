package com.jiuye.mcp.agent.dao.meta;

import com.jiuye.mcp.agent.model.McpRouteConnEntity;
import com.jiuye.mcp.agent.model.MySQLSourceColumnEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * column mapper接口
 *
 * @author jepson
 * @date 2018/9/11 4:11 PM
 */
@Component
public interface MetadataInfoMapper {

    List<MySQLSourceColumnEntity> getMySQLColumnAll();

    List<McpRouteConnEntity> getMcpRouteConnAll();

}
