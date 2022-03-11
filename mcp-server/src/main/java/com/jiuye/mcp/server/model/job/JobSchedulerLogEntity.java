/**
 * Copyright (c) 2016, Jiuye SCM and/or its affiliates. All rights reserved.
 */
package com.jiuye.mcp.server.model.job;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * JobSchedulerLog
 *
 * @author kevin
 * @date 2018-08-20
 */
public class JobSchedulerLogEntity implements Serializable {

    private static final long serialVersionUID = 3914044715004233477L;

    /**
     * 主键ID
     */
    private Long logId;
    /**
     * 作业ID
     */
    private Long jobId;
    /**
     * 作业名称
     */
    private String jobName;
    /**
     * 类别
     */
    private String jobType;
    /**
     * 项目ID
     */
    private Long projectId;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 执行器组ID
     */
    private Long groupId;
    /**
     * 执行器组名称
     */
    private String groupName;
    /**
     * Agent存活
     */
    private String agent;
    /**
     * Cron表达式
     */
    private String cron;
    /**
     * 数据路由ID
     */
    private Long routeId;
    /**
     * 数据路由名称
     */
    private String routeName;
    /**
     * 源端ID
     */
    private Long sourceId;
    /**
     * 源端名称
     */
    private String sourceName;
    /**
     * 终端ID
     */
    private Long targetId;
    /**
     * 终端名称
     */
    private String targetName;
    /**
     * 终端schemaId
     */
    private Long targetSchemaId;
    /**
     * 规则ID
     */
    private Long ruleId;
    /**
     * 规则名称
     */
    private String ruleName;
    /**
     * 业务开始时间
     */
    private Timestamp busStartTime;
    /**
     * 业务结束时间
     */
    private Timestamp busEndTime;
    /**
     * Binlog文件名称
     */
    private String binlogName;
    /**
     * Binlog文件位置
     */
    private Long binlogPosition;
    /**
     * 本次调度器地址
     */
    private String schedAddress;
    /**
     * 调度器
     */
    private String schedHandler;
    /**
     * 调度器参数
     */
    private String schedParam;
    /**
     * 调度时间
     */
    private Timestamp triggerTime;
    /**
     * 调度结果
     */
    private Integer triggerCode;
    /**
     * 调度日志
     */
    private String triggerMsg;
    /**
     * 执行时间
     */
    private Timestamp handleTime;
    /**
     * 执行结束时间
     */
    private Timestamp handleEndTime;
    /**
     * 执行结果
     */
    private Integer handleCode;
    /**
     * 执行日志
     */
    private String handleMsg;
    /**
     * 进度
     */
    private String progressRate;
    /**
     * 花费时间 秒
     */
    private String duration;
    /**
     * schedule类型
     */
    private String scheduleType;

    /**
     * job 状态
     */
    private String status;

    private int fromLineNum;

    public JobSchedulerLogEntity() {

    }

    /**
     * 主键ID
     */
    public Long getLogId() {
        return this.logId;
    }

    /**
     * 主键ID
     *
     * @param logId
     */
    public void setLogId(Long logId) {
        this.logId = logId;
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
     * 作业名称
     */
    public String getJobName() {
        return this.jobName;
    }

    /**
     * 作业名称
     *
     * @param jobName
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * 类别
     */
    public String getJobType() {
        return this.jobType;
    }

    /**
     * 类别
     *
     * @param jobType
     */
    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    /**
     * 项目ID
     */
    public Long getProjectId() {
        return this.projectId;
    }

    /**
     * 项目ID
     *
     * @param projectId
     */
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    /**
     * 项目名称
     */
    public String getProjectName() {
        return this.projectName;
    }

    /**
     * 项目名称
     *
     * @param projectName
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * 执行器组ID
     */
    public Long getGroupId() {
        return this.groupId;
    }

    /**
     * 执行器组ID
     *
     * @param groupId
     */
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    /**
     * 执行器组名称
     */
    public String getGroupName() {
        return this.groupName;
    }

    /**
     * 执行器组名称
     *
     * @param groupName
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * Agent存活
     */
    public String getAgent() {
        return this.agent;
    }

    /**
     * Agent存活
     *
     * @param agent
     */
    public void setAgent(String agent) {
        this.agent = agent;
    }

    /**
     * Cron表达式
     */
    public String getCron() {
        return this.cron;
    }

    /**
     * Cron表达式
     *
     * @param cron
     */
    public void setCron(String cron) {
        this.cron = cron;
    }

    /**
     * 数据路由ID
     */
    public Long getRouteId() {
        return this.routeId;
    }

    /**
     * 数据路由ID
     *
     * @param routeId
     */
    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    /**
     * 数据路由名称
     */
    public String getRouteName() {
        return this.routeName;
    }

    /**
     * 数据路由名称
     *
     * @param routeName
     */
    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    /**
     * 源端ID
     */
    public Long getSourceId() {
        return this.sourceId;
    }

    /**
     * 源端ID
     *
     * @param sourceId
     */
    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    /**
     * 源端名称
     */
    public String getSourceName() {
        return this.sourceName;
    }

    /**
     * 源端名称
     *
     * @param sourceName
     */
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    /**
     * 终端ID
     */
    public Long getTargetId() {
        return this.targetId;
    }

    /**
     * 终端ID
     *
     * @param targetId
     */
    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    /**
     * 终端名称
     */
    public String getTargetName() {
        return this.targetName;
    }

    /**
     * 终端名称
     *
     * @param targetName
     */
    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    /**
     * 终端schemaId
     */
    public Long getTargetSchemaId() {
        return this.targetSchemaId;
    }

    /**
     * 终端schemaId
     *
     * @param targetSchemaId
     */
    public void setTargetSchemaId(Long targetSchemaId) {
        this.targetSchemaId = targetSchemaId;
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
     * 业务开始时间
     */
    public Timestamp getBusStartTime() {
        return this.busStartTime;
    }

    /**
     * 业务开始时间
     *
     * @param busStartTime
     */
    public void setBusStartTime(Timestamp busStartTime) {
        this.busStartTime = busStartTime;
    }

    /**
     * 业务结束时间
     */
    public Timestamp getBusEndTime() {
        return this.busEndTime;
    }

    /**
     * 业务结束时间
     *
     * @param busEndTime
     */
    public void setBusEndTime(Timestamp busEndTime) {
        this.busEndTime = busEndTime;
    }

    /**
     * Binlog文件名称
     */
    public String getBinlogName() {
        return this.binlogName;
    }

    /**
     * Binlog文件名称
     *
     * @param binlogName
     */
    public void setBinlogName(String binlogName) {
        this.binlogName = binlogName;
    }

    /**
     * Binlog文件位置
     */
    public Long getBinlogPosition() {
        return this.binlogPosition;
    }

    /**
     * Binlog文件位置
     *
     * @param binlogPosition
     */
    public void setBinlogPosition(Long binlogPosition) {
        this.binlogPosition = binlogPosition;
    }

    /**
     * 本次调度器地址
     */
    public String getSchedAddress() {
        return this.schedAddress;
    }

    /**
     * 本次调度器地址
     *
     * @param schedAddress
     */
    public void setSchedAddress(String schedAddress) {
        this.schedAddress = schedAddress;
    }

    /**
     * 调度器
     */
    public String getSchedHandler() {
        return this.schedHandler;
    }

    /**
     * 调度器
     *
     * @param schedHandler
     */
    public void setSchedHandler(String schedHandler) {
        this.schedHandler = schedHandler;
    }

    /**
     * 调度器参数
     */
    public String getSchedParam() {
        return this.schedParam;
    }

    /**
     * 调度器参数
     *
     * @param schedParam
     */
    public void setSchedParam(String schedParam) {
        this.schedParam = schedParam;
    }

    /**
     * 调度时间
     */
    public Timestamp getTriggerTime() {
        return this.triggerTime;
    }

    /**
     * 调度时间
     *
     * @param triggerTime
     */
    public void setTriggerTime(Timestamp triggerTime) {
        this.triggerTime = triggerTime;
    }

    /**
     * 调度结果
     */
    public Integer getTriggerCode() {
        return this.triggerCode;
    }

    /**
     * 调度结果
     *
     * @param triggerCode
     */
    public void setTriggerCode(Integer triggerCode) {
        this.triggerCode = triggerCode;
    }

    /**
     * 调度日志
     */
    public String getTriggerMsg() {
        return this.triggerMsg;
    }

    /**
     * 调度日志
     *
     * @param triggerMsg
     */
    public void setTriggerMsg(String triggerMsg) {
        this.triggerMsg = triggerMsg;
    }

    /**
     * 执行时间
     */
    public Timestamp getHandleTime() {
        return this.handleTime;
    }

    /**
     * 执行时间
     *
     * @param handleTime
     */
    public void setHandleTime(Timestamp handleTime) {
        this.handleTime = handleTime;
    }

    /**
     * 执行结束时间
     */
    public Timestamp getHandleEndTime() {
        return this.handleEndTime;
    }

    /**
     * 执行结束时间
     *
     * @param handleEndTime
     */
    public void setHandleEndTime(Timestamp handleEndTime) {
        this.handleEndTime = handleEndTime;
    }

    /**
     * 执行结果
     */
    public Integer getHandleCode() {
        return this.handleCode;
    }

    /**
     * 执行结果
     *
     * @param handleCode
     */
    public void setHandleCode(Integer handleCode) {
        this.handleCode = handleCode;
    }

    /**
     * 执行日志
     */
    public String getHandleMsg() {
        return this.handleMsg;
    }

    /**
     * 执行日志
     *
     * @param handleMsg
     */
    public void setHandleMsg(String handleMsg) {
        this.handleMsg = handleMsg;
    }

    /**
     * 进度
     */
    public String getProgressRate() {
        return this.progressRate;
    }

    /**
     * 进度
     *
     * @param progressRate
     */
    public void setProgressRate(String progressRate) {
        this.progressRate = progressRate;
    }

    /**
     * 花费时间 秒
     *
     * @return
     */
    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     * schedule类型
     *
     * @return
     */
    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public int getFromLineNum() {
        return fromLineNum;
    }

    public void setFromLineNum(int fromLineNum) {
        this.fromLineNum = fromLineNum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
