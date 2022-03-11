package com.jiuye.mcp.agent.model;


import java.io.Serializable;

/**
 * mcp的路由id
 * 源端id  jdbc实体
 * 终端id  datasource_name
 *
 * @author jepson
 * @date 2018/9/29 4:25 PM
 */
public class McpRouteConnEntity implements Serializable{

    private static final long serialVersionUID = 4151644962001375673L;

    private Long routeId;
    private Long sourceId;
    private String ip;
    private String port;
    private String username;
    private String password;

    Long targetId;
    String datasourceName;

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }


}
