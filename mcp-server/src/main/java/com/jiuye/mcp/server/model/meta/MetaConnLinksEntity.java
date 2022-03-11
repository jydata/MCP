package com.jiuye.mcp.server.model.meta;

import java.io.Serializable;

/**
 * 数据库连接信息实体
 *
 * @author zp
 * @date 2018-08-20
 */
public class MetaConnLinksEntity implements Serializable {

    private static final long serialVersionUID = -4946285733858186715L;

    private Long linkId;
    /**
     * 连接名
     */
    private String linkName;
    /**
     * 服务器地址
     */
    private String url;
    /**
     * 数据库驱动
     */
    private String driver;
    /**
     * 服务器地址
     */
    private String ip;
    /**
     * 端口号
     */
    private String port;
    /**
     * 数据库名称
     */
    private String dbName;
    /**
     * 用户
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 数据端ID
     */
    private int datasourceId;
    /**
     * 数据端类型;0:源端，1：终端
     */
    private int datasourceType;
    /**
     * 是否已经在MCP元数据库生成了库、表、列、索引等基础表标识；1：是，0：否
     */
    private int executeFlag;
    private String createUser;
    private String createTime;
    private String updateUser;
    private String updateTime;

    /**
     * 要添加的数据源
     */
    private String datasourceChoice;

    /**
     * 源/终端区分
     */
    private String dsType;

    public Long getLinkId() {
        return linkId;
    }

    public void setLinkId(Long linkId) {
        this.linkId = linkId;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(int datasourceId) {
        this.datasourceId = datasourceId;
    }

    public int getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(int datasourceType) {
        this.datasourceType = datasourceType;
    }

    public int getExecuteFlag() {
        return executeFlag;
    }

    public void setExecuteFlag(int executeFlag) {
        this.executeFlag = executeFlag;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getDatasourceChoice() {
        return datasourceChoice;
    }

    public void setDatasourceChoice(String datasourceChoice) {
        this.datasourceChoice = datasourceChoice;
    }

    public String getDsType() {
        return dsType;
    }

    public void setDsType(String dsType) {
        this.dsType = dsType;
    }
}
