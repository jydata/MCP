package com.jiuye.mcp.server.model.job;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * JobDefine
 *
 * @author kevin
 * @date 2018-08-20
 */
public class JobDefineEntity implements Serializable {

    private static final long serialVersionUID = 3290878201479727107L;

    /**
     * 主键ID
     */
    private Long jobId;
    /**
     * 名称
     */
    private String jobName;
    /**
     * 类别(increment:增量,full:全量,column:列名,metadata:元数据)
     */
    private String jobType;
    /**
     * 项目Id
     */
    private Long projectId;
    /**
     * 项目名
     */
    private String projectName;
    /**
     * 组Id
     */
    private Long groupId;
    /**
     * 组名称
     */
    private String groupName;
    /**
     * Cron表达式
     */
    private String cron;
    /**
     * 数据路由ID
     */
    private Long routeId;
    /**
     * 路由名称
     */
    private String routeName;
    /**
     * 源端数据库
     */
    private String srcDb;
    /**
     * 源端数据表
     */
    private String srcTable;
    /**
     * 源端id
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
     * 终端库id
     */
    private Long targetSchemaId;
    /**
     * 终端库名称
     */
    private String schemaName;
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
     * 是否使用最新的binlog
     */
    private String recentBinlog;
    /**
     * 最新Binlog文件名称
     */
    private String recentBinlogName;
    /**
     * 最新Binlog文件位置
     */
    private Long recentBinlogPosition;
    /**
     * 是否启用(0:不启用,1:启用)
     */
    private int enable;
    /**
     * 状态(init:初始,wait:等待执行,running:运行中,fail:失败,success:成功)
     */
    private String status;
    /**
     * 进度
     */
    private String progressRate;
    /**
     * 删除标记
     */
    private int deleteFlag;
    /**
     * 创建时间
     */
    private Timestamp createTime;
    /**
     * 创建人
     */
    private String createUser;
    /**
     * 修改时间
     */
    private Timestamp updateTime;
    /**
     * 修改人
     */
    private String updateUser;
    /**
     * 花费时间 秒
     */
    private String duration;
    /**
     * schedule类型
     */
    private String scheduleType;

    private List<JobSyncTableEntity> syncList;

    public JobDefineEntity() {

    }

    /**
     * 主键ID
     */
    public Long getJobId() {
        return this.jobId;
    }

    /**
     * 主键ID
     *
     * @param jobId
     */
    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    /**
     * 名称
     */
    public String getJobName() {
        return this.jobName;
    }

    /**
     * 名称
     *
     * @param jobName
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * 类别(increment:增量,full:全量,column:列名)
     */
    public String getJobType() {
        return this.jobType;
    }

    /**
     * 类别(increment:增量,full:全量,column:列名)
     *
     * @param jobType
     */
    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSrcDb() {
        return srcDb;
    }

    public void setSrcDb(String srcDb) {
        this.srcDb = srcDb;
    }

    public String getSrcTable() {
        return srcTable;
    }

    public void setSrcTable(String srcTable) {
        this.srcTable = srcTable;
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

    public String getRecentBinlog() {
        return recentBinlog;
    }

    public void setRecentBinlog(String recentBinlog) {
        this.recentBinlog = recentBinlog;
    }

    public String getRecentBinlogName() {
        return recentBinlogName;
    }

    public void setRecentBinlogName(String recentBinlogName) {
        this.recentBinlogName = recentBinlogName;
    }

    public Long getRecentBinlogPosition() {
        return recentBinlogPosition;
    }

    public void setRecentBinlogPosition(Long recentBinlogPosition) {
        this.recentBinlogPosition = recentBinlogPosition;
    }

    /**
     * 是否启用(0:不启用,1:启用)
     */
    public int getEnable() {
        return this.enable;
    }

    /**
     * 是否启用(0:不启用,1:启用)
     *
     * @param enable
     */
    public void setEnable(int enable) {
        this.enable = enable;
    }

    /**
     * 状态(init:初始,wait:等待执行,running:运行中,fail:失败,success:成功)
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * 状态(init:初始,wait:等待执行,running:运行中,fail:失败,success:成功)
     *
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
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

    /**
     * 修改时间
     */
    public Timestamp getUpdateTime() {
        return this.updateTime;
    }

    public int getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(int deleteFlag) {
        this.deleteFlag = deleteFlag;
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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getTargetSchemaId() {
        return targetSchemaId;
    }

    public void setTargetSchemaId(Long targetSchemaId) {
        this.targetSchemaId = targetSchemaId;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    /**
     * 花费时间 秒
     */
    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     * job类型
     */
    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<JobSyncTableEntity> getSyncList() {
        return syncList;
    }

    public void setSyncList(List<JobSyncTableEntity> syncList) {
        this.syncList = syncList;
    }
}

