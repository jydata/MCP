package com.jiuye.mcp.server.model.meta;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author kevin
 */
public class MetaDdlRulesEntity implements Serializable {

    private static final long serialVersionUID = -3633821641041101457L;

    private Long id;
    /**
     * 路由ID
     */
    private Long routeId;
    /**
     * 源端schema名称
     */
    private String srcSchemaName;
    /**
     * 源端表名
     */
    private String srcTableName;
    /**
     * 终端SchemaId
     */
    private Long targetSchemaId;
    /**
     * 终端Schema名称
     */
    private String targetSchemaName;
    /**
     * 终端table名称
     */
    private String targetTableName;
    /**
     * 规则ID
     */
    private Long ruleId;
    /**
     * 规则名称
     */
    private String ruleName;
    /**
     * 规则类型
     */
    private String ruleType;
    /**
     * 规则前缀
     */
    private String rulePrefix;
    /**
     * 规则新增列名
     */
    private String ruleColumn;
    /**
     * 截取后的字段
     */
    private String srcSchemaColumn;
    /**
     * 判断index是否有效: 1：启用，0：禁用
     */
    private Integer dbFlag;
    /**
     * index开始位置
     */
    private Integer beginIndex;
    /**
     * index结束位置
     */
    private Integer endIndex;
    /**
     * 过滤条件
     */
    private String whereClause;
    /**
     * 创建人
     */
    private String createUser;
    /**
     * 创建时间
     */
    private Timestamp createTime;
    /**
     * 修改人
     */
    private String updateUser;
    /**
     * 修改时间
     */
    private Timestamp updateTime;
    /**
     * src_schema_name下的count（src_table_name）数
     */
    private int tableCount;
    /**
     * rules count
     */
    private int ruleCount;

    public MetaDdlRulesEntity() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 路由ID
     */
    public Long getRouteId() {
        return this.routeId;
    }

    /**
     * 路由ID
     *
     * @param routeId
     */
    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    /**
     * 源端schema名称
     */
    public String getSrcSchemaName() {
        return this.srcSchemaName;
    }

    /**
     * 源端schema名称
     *
     * @param srcSchemaName
     */
    public void setSrcSchemaName(String srcSchemaName) {
        this.srcSchemaName = srcSchemaName;
    }

    /**
     * 源端表名
     */
    public String getSrcTableName() {
        return this.srcTableName;
    }

    /**
     * 源端表名
     *
     * @param srcTableName
     */
    public void setSrcTableName(String srcTableName) {
        this.srcTableName = srcTableName;
    }

    /**
     * 终端SchemaId
     */
    public Long getTargetSchemaId() {
        return this.targetSchemaId;
    }

    /**
     * 终端SchemaId
     *
     * @param targetSchemaId
     */
    public void setTargetSchemaId(Long targetSchemaId) {
        this.targetSchemaId = targetSchemaId;
    }

    /**
     * 终端Schema名称
     */
    public String getTargetSchemaName() {
        return this.targetSchemaName;
    }

    /**
     * 终端Schema名称
     *
     * @param targetSchemaName
     */
    public void setTargetSchemaName(String targetSchemaName) {
        this.targetSchemaName = targetSchemaName;
    }

    public String getTargetTableName() {
        return targetTableName;
    }

    public void setTargetTableName(String targetTableName) {
        this.targetTableName = targetTableName;
    }

    /**
     * 规则ID
     */
    public Long getRuleId() {
        return this.ruleId;
    }

    /**
     * 规则ID
     *
     * @param ruleId
     */
    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    /**
     * 规则名称
     */
    public String getRuleName() {
        return this.ruleName;
    }

    /**
     * 规则名称
     *
     * @param ruleName
     */
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    /**
     * 规则类型
     */
    public String getRuleType() {
        return this.ruleType;
    }

    /**
     * 规则类型
     *
     * @param ruleType
     */
    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    /**
     * 规则前缀
     */
    public String getRulePrefix() {
        return this.rulePrefix;
    }

    /**
     * 规则前缀
     *
     * @param rulePrefix
     */
    public void setRulePrefix(String rulePrefix) {
        this.rulePrefix = rulePrefix;
    }

    /**
     * 规则新增列名
     */
    public String getRuleColumn() {
        return this.ruleColumn;
    }

    /**
     * 规则新增列名
     *
     * @param ruleColumn
     */
    public void setRuleColumn(String ruleColumn) {
        this.ruleColumn = ruleColumn;
    }

    /**
     * 截取后的字段
     *
     * @return
     */
    public String getSrcSchemaColumn() {
        return srcSchemaColumn;
    }

    /**
     * 截取后的字段
     *
     * @return
     */
    public void setSrcSchemaColumn(String srcSchemaColumn) {
        this.srcSchemaColumn = srcSchemaColumn;
    }

    /**
     * 判断index是否有效: 1：启用，0：禁用
     */
    public Integer getDbFlag() {
        return this.dbFlag;
    }

    /**
     * 判断index是否有效: 1：启用，0：禁用
     *
     * @param dbFlag
     */
    public void setDbFlag(Integer dbFlag) {
        this.dbFlag = dbFlag;
    }

    /**
     * index开始位置
     */
    public Integer getBeginIndex() {
        return this.beginIndex;
    }

    /**
     * index开始位置
     *
     * @param beginIndex
     */
    public void setBeginIndex(Integer beginIndex) {
        this.beginIndex = beginIndex;
    }

    /**
     * index结束位置
     */
    public Integer getEndIndex() {
        return this.endIndex;
    }

    /**
     * index结束位置
     *
     * @param endIndex
     */
    public void setEndIndex(Integer endIndex) {
        this.endIndex = endIndex;
    }

    /**
     * 过滤条件
     */
    public String getWhereClause() {
        return this.whereClause;
    }

    /**
     * 过滤条件
     *
     * @param whereClause
     */
    public void setWhereClause(String whereClause) {
        this.whereClause = whereClause;
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
     * 修改人
     */
    public String getUpdateUser() {
        return this.updateUser;
    }

    /**
     * 修改人
     *
     * @param updateUser
     */
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    /**
     * 修改时间
     */
    public Timestamp getUpdateTime() {
        return this.updateTime;
    }

    /**
     * 修改时间
     *
     * @param updateTime
     */
    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * src_schema_name下的count（src_table_name）数
     *
     * @return
     */
    public int getTableCount() {
        return tableCount;
    }

    /**
     * src_schema_name下的count（src_table_name）数
     *
     * @param tableCount
     */
    public void setTableCount(int tableCount) {
        this.tableCount = tableCount;
    }

    public int getRuleCount() {
        return ruleCount;
    }

    public void setRuleCount(int ruleCount) {
        this.ruleCount = ruleCount;
    }
}
