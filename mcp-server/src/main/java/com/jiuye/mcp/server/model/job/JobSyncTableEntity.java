package com.jiuye.mcp.server.model.job;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * JobSyncTable
 *
 * @author kevin
 * @date 2018-08-20
 */
public class JobSyncTableEntity implements Serializable {

    private static final long serialVersionUID = 9218975502182656920L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 作业ID
     */
    private Long jobId;
    /**
     * Schema名称
     */
    private String schemaName;
    /**
     * 数据库名称
     */
    private String dbName;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 删除标记
     */
    private int deleteFlag;
    /**
     * 列名
     */
    private String columnName;
    /**
     * 开始时间
     */
    private String beginTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 过滤条件
     */
    private String whereClause;
    /**
     * DdlRuleId
     */
    private Long ddlRuleId;

    /**
     * 创建时间
     */
    private Timestamp createTime;
    /**
     * 创建人
     */
    private String createUser;

    public JobSyncTableEntity() {

    }

    /**
     * 主键ID
     */
    public Long getId() {
        return this.id;
    }

    /**
     * 主键ID
     *
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 作业ID
     */
    public Long getJobId() {
        return this.jobId;
    }

    /**
     * 作业ID
     *
     * @param jobId
     */
    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    /**
     * Schema名称
     */
    public String getSchemaName() {
        return this.schemaName;
    }

    /**
     * Schema名称
     *
     * @param schemaName
     */
    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    /**
     * 数据库名称
     */
    public String getDbName() {
        return this.dbName;
    }

    /**
     * 数据库名称
     *
     * @param dbName
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * 表名
     */
    public String getTableName() {
        return this.tableName;
    }

    /**
     * 表名
     *
     * @param tableName
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(int deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getWhereClause() {
        return whereClause;
    }

    public void setWhereClause(String whereClause) {
        this.whereClause = whereClause;
    }

    public Long getDdlRuleId() {
        return ddlRuleId;
    }

    public void setDdlRuleId(Long ddlRuleId) {
        this.ddlRuleId = ddlRuleId;
    }

    /**
     * 创建时间
     */
    public Timestamp getCreateTime() {
        return this.createTime;
    }

    /**
     * 创建时间
     *
     * @param createTime
     */
    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    /**
     * 创建人
     */
    public String getCreateUser() {
        return this.createUser;
    }

    /**
     * 创建人
     *
     * @param createUser
     */
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

}
