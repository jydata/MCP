package com.jiuye.mcp.server.model.job;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author jepson
 * @date 2018/12/20 10:40 AM
 */
public class JobAlterSqlEntity implements Serializable {

    private static final long serialVersionUID = -4130712650743914981L;

    /**
     * 主键ID
     */
    private Long jobId;

    /**
     * 数据路由ID
     */
    private Long routeId;


    /**
     * 源端dbname
     */
    private String srcSchemaName;

    /**
     * 源端table
     */
    private String srcTableName;

    /**
     * 终端dbname
     */
    private String targetSchemaName;

    /**
     * 终端table
     */
    private String targetTableName;

    /**
     * 源端sql
     */
    private String srcAlterSql;

    /**
     * 终端sql
     */
    private String targetAlterSql;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 修改人
     */
    private String updateUser;


    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public String getSrcSchemaName() {
        return srcSchemaName;
    }

    public void setSrcSchemaName(String srcSchemaName) {
        this.srcSchemaName = srcSchemaName;
    }

    public String getSrcTableName() {
        return srcTableName;
    }

    public void setSrcTableName(String srcTableName) {
        this.srcTableName = srcTableName;
    }

    public String getTargetSchemaName() {
        return targetSchemaName;
    }

    public void setTargetSchemaName(String targetSchemaName) {
        this.targetSchemaName = targetSchemaName;
    }

    public String getTargetTableName() {
        return targetTableName;
    }

    public void setTargetTableName(String targetTableName) {
        this.targetTableName = targetTableName;
    }

    public String getSrcAlterSql() {
        return srcAlterSql;
    }

    public void setSrcAlterSql(String srcAlterSql) {
        this.srcAlterSql = srcAlterSql;
    }

    public String getTargetAlterSql() {
        return targetAlterSql;
    }

    public void setTargetAlterSql(String targetAlterSql) {
        this.targetAlterSql = targetAlterSql;
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


}
