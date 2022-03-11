package com.jiuye.mcp.agent.service.meta.impl;

import com.jiuye.mcp.agent.model.McpRouteConnEntity;
import com.jiuye.mcp.agent.model.SourceEntity;
import com.jiuye.mcp.agent.model.TargetEntity;
import com.jiuye.mcp.agent.service.meta.IRouteConnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 路由id 源端jdbc实体 map
 * 路由id 终端id 所属的数据源名称(Phoenix、Cassandra) map
 *
 * @author jepson
 * @date 2018/9/29 4:57 PM
 */
@Service
public class McpRouteConnServiceImpl implements IRouteConnService {

    private final static Logger logger = LoggerFactory.getLogger(McpRouteConnServiceImpl.class);

    private ConcurrentHashMap<Long, SourceEntity> routeSourceJDBC = new ConcurrentHashMap<Long, SourceEntity>();
    private ConcurrentHashMap<Long, TargetEntity> routeTargetDataSourceName = new ConcurrentHashMap<Long, TargetEntity>();


    private SourceEntity sourceJDBC;
    private TargetEntity targetDataSourceName;


    public ConcurrentHashMap<Long, SourceEntity> getRouteSourceJDBC() {
        return routeSourceJDBC;
    }

    public void setRouteSourceJDBC(ConcurrentHashMap<Long, SourceEntity> routeSourceJDBC) {
        this.routeSourceJDBC = routeSourceJDBC;
    }

    public ConcurrentHashMap<Long, TargetEntity> getRouteTargetDataSourceName() {
        return routeTargetDataSourceName;
    }

    public void setRouteTargetDataSourceName(ConcurrentHashMap<Long, TargetEntity> routeTargetDataSourceName) {
        this.routeTargetDataSourceName = routeTargetDataSourceName;
    }

    /**
     * 生成 sourceJDBC targetDataSourceName 实体类
     *
     * @param mcpRouteConnEntityList
     */
    @Override
    public void getRouteConnInfo(List<McpRouteConnEntity> mcpRouteConnEntityList) {

        for (McpRouteConnEntity rc : mcpRouteConnEntityList) {
            sourceJDBC = new SourceEntity();
            targetDataSourceName = new TargetEntity();

            sourceJDBC.setSourceId(rc.getSourceId());
            sourceJDBC.setIp(rc.getIp());
            sourceJDBC.setPort(rc.getPort());
            sourceJDBC.setUsername(rc.getUsername());
            sourceJDBC.setPassword(rc.getPassword());

            targetDataSourceName.setTargetId(rc.getTargetId());
            targetDataSourceName.setDatasourceName(rc.getDatasourceName());

            routeSourceJDBC.put(rc.getRouteId(), sourceJDBC);
            routeTargetDataSourceName.put(rc.getRouteId(), targetDataSourceName);

        }

    }


}
