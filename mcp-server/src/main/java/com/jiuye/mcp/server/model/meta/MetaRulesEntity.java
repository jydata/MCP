package com.jiuye.mcp.server.model.meta;

import java.io.Serializable;

/**
 * DS 规则实体
 *
 * @author zhaopeng
 * @date 2018-11-16
 */
public class MetaRulesEntity implements Serializable{

    private static final long serialVersionUID = -4444320905388049529L;

    private long ruleId;

    private String ruleName;

    private String ruleType;

    /**
     * 规则后缀
     */
    private String ruleSuffix;

    private String rulePrefix;

    private String ruleColumn;

    private int dbFlag;

    private int beginIndex;

    private int endIndex;

    private String ruleStatus;

    private String comment;

    private String createUser;

    private String createTime;

    private String updateUser;

    private String updateTime;

    public long getRuleId() {
        return ruleId;
    }

    public void setRuleId(long ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getRuleSuffix() {
        return ruleSuffix;
    }

    public void setRuleSuffix(String ruleSuffix) {
        this.ruleSuffix = ruleSuffix;
    }

    public String getRulePrefix() {
        return rulePrefix;
    }

    public void setRulePrefix(String rulePrefix) {
        this.rulePrefix = rulePrefix;
    }

    public String getRuleColumn() {
        return ruleColumn;
    }

    public void setRuleColumn(String ruleColumn) {
        this.ruleColumn = ruleColumn;
    }

    public int getDbFlag() {
        return dbFlag;
    }

    public void setDbFlag(int dbFlag) {
        this.dbFlag = dbFlag;
    }

    public int getBeginIndex() {
        return beginIndex;
    }

    public void setBeginIndex(int beginIndex) {
        this.beginIndex = beginIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public String getRuleStatus() {
        return ruleStatus;
    }

    public void setRuleStatus(String ruleStatus) {
        this.ruleStatus = ruleStatus;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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
}
