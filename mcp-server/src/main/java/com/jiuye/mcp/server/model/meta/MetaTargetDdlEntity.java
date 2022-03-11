package com.jiuye.mcp.server.model.meta;

import java.io.Serializable;

/**
 * MySQL到HBase的SQL语句映射信息实体类
 *
 * @author zp
 * @date 2018-08-20
 */
public class MetaTargetDdlEntity implements Serializable {

    private static final long serialVersionUID = 1720888525222913088L;

    private long id;
    /**
     * 路由ID
     */
    private long routeId;
    /**
     * 路由名称
     */
    private String routeName;
    /**
     * SchemaID
     */
    private long schemaId;
    /**
     * 源端库名
     */
    private String srcDbName;
    /**
     * 源端表名
     */
    private String srcTableName;
    /**
     * 终端库名/schema名
     */
    private String targetDbName;
    /**
     * 终端索引表名
     */
    private String targetTableName;
    /**
     * 终端索引表名
     */
    private String indexTableName;
    /**
     * 建表语句
     */
    private String ddlSql;
    /**
     * 索引表语句
     */
    private String indexSql;
    /**
     * 是否已经在终端生成标识；1：是，0：否
     */
    private Integer executeFlag;
    /**
     * 是否删除标识；1：未删除(启用)，0：删除(禁用)
     */
    private Integer deleteFlag;
    /**
     * 是否手工修改过表结构
     */
    private Integer modifyDdlFlag;
    /**
     * 创建人
     */
    private String createUser;
    /**
     * 修改人
     */
    private String updateUser;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 修改时间
     */
    private String updateTime;

    public MetaTargetDdlEntity() {
    }

    public MetaTargetDdlEntity(long id, long routeId, String routeName, long schemaId, String srcDbName, String srcTableName, String targetDbName, String targetTableName, String indexTableName, String ddlSql, String indexSql) {
        this.id = id;
        this.routeId = routeId;
        this.routeName = routeName;
        this.schemaId = schemaId;
        this.srcDbName = srcDbName;
        this.srcTableName = srcTableName;
        this.targetDbName = targetDbName;
        this.targetTableName = targetTableName;
        this.indexTableName = indexTableName;
        this.ddlSql = ddlSql;
        this.indexSql = indexSql;
        this.modifyDdlFlag = 0;
    }

    public Integer getModifyDdlFlag() {
        return modifyDdlFlag;
    }

    public void setModifyDdlFlag(Integer modifyDdlFlag) {
        this.modifyDdlFlag = modifyDdlFlag;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRouteId() {
        return routeId;
    }

    public void setRouteId(long routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public long getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(long schemaId) {
        this.schemaId = schemaId;
    }

    public String getSrcDbName() {
        return srcDbName;
    }

    public void setSrcDbName(String srcDbName) {
        this.srcDbName = srcDbName;
    }

    public String getSrcTableName() {
        return srcTableName;
    }

    public void setSrcTableName(String srcTableName) {
        this.srcTableName = srcTableName;
    }

    public String getTargetDbName() {
        return targetDbName;
    }

    public void setTargetDbName(String targetDbName) {
        this.targetDbName = targetDbName;
    }

    public String getTargetTableName() {
        return targetTableName;
    }

    public void setTargetTableName(String targetTableName) {
        this.targetTableName = targetTableName;
    }

    public String getIndexTableName() {
        return indexTableName;
    }

    public void setIndexTableName(String indexTableName) {
        this.indexTableName = indexTableName;
    }

    public String getDdlSql() {
        return ddlSql;
    }

    public void setDdlSql(String ddlSql) {
        this.ddlSql = ddlSql;
    }

    public String getIndexSql() {
        return indexSql;
    }

    public void setIndexSql(String indexSql) {
        this.indexSql = indexSql;
    }

    public Integer getExecuteFlag() {
        return executeFlag;
    }

    public void setExecuteFlag(Integer executeFlag) {
        this.executeFlag = executeFlag;
    }

    public Integer getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
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
}
