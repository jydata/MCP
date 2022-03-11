package com.jiuye.mcp.agent.model;


import java.io.Serializable;

/**
 * @author jepson
 * @date 2018/9/29 5:10 PM
 */
public class SourceEntity implements Serializable{

    private static final long serialVersionUID = 1978098455090779615L;

    private Long sourceId;
    private String ip;
    private String port;
    private String username;
    private String password;

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

}
