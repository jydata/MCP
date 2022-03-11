package com.jiuye.mcp.server.model.meta;

import java.io.Serializable;
import java.util.List;

/**
 * 路由信息实体
 *
 * @author zp
 * @date 2018-09-25
 */
public class MetaDatarouteEntity implements Serializable {

    private static final long serialVersionUID = -4946285733858186715L;

    private Long routeId;

    /**
     * 路由名
     */
    private String routeName;
    /**
     * 源端
     */
    private Long sourceId;
    private String sourceName;

    /**
     * 终端
     */
    private Long targetId;
    private String targetName;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 修改时间
     */
    private String updateTime;
    /**
     * 路由数据状态
     */
    private String routeStatus;

    private String createUser;

    private String updateUser;

    private String ruleName;

    private String dbName;

    private List<MetaDatarouteEntity> children;

    /**
     * 标识是路由列表还是其余下拉列表：1-路由列表 || 2-其余下拉列表
     */
    private String flag;

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getRouteStatus() {
        return routeStatus;
    }

    public void setRouteStatus(String routeStatus) {
        this.routeStatus = routeStatus;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
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

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public List<MetaDatarouteEntity> getChildren() {
        return children;
    }

    public void setChildren(List<MetaDatarouteEntity> children) {
        this.children = children;
    }
}
