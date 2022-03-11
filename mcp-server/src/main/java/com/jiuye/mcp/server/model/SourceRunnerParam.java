package com.jiuye.mcp.server.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;

/**
 * source runner param entity
 *
 * @author kevin
 * @date 2018-10-25
 */
public class SourceRunnerParam implements Serializable {

    private static final long serialVersionUID = 1182093715830346789L;

    /**
     * 作业ID
     */
    private Long jobId;
    /**
     * 作业名称
     */
    private String jobName;
    /**
     * Agent存活
     */
    private String agent;
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
     * 源端数据库驱动
     */
    private String srcDriver;
    /**
     * 源端服务器地址
     */
    private String srcIp;
    /**
     * 源端端口号
     */
    private String srcPort;
    /**
     * 源端数据库名称
     */
    private String srcDbName;
    /**
     * 源端用户
     */
    private String srcUsername;
    /**
     * 源端密码
     */
    private String srcPassword;
    /**
     * 源端数据端ID
     */
    private int srcDatasourceId;
    /**
     * 源端数据端名称
     */
    private String srcDatasourceName;
    /**
     * 源端同步表
     */
    private Map<String, String> fullSyncTablesMap;

    /**
     * 终端ID
     */
    private Long targetId;
    /**
     * 终端名称
     */
    private String targetName;
    /**
     * 终端数据库驱动
     */
    private String tarDriver;
    /**
     * 终端服务器地址
     */
    private String tarIp;
    /**
     * 终端端口号
     */
    private String tarPort;
    /**
     * 终端数据库名称
     */
    private String tarDbName;
    /**
     * 终端用户
     */
    private String tarUsername;
    /**
     * 终端密码
     */
    private String tarPassword;
    /**
     * 终端数据端ID
     */
    private int tarDatasourceId;
    /**
     * 终端数据端名称
     */
    private String tarDatasourceName;
    /**
     * 终端SchemaId
     */
    private Long tarSchemaId;

    /**
     * 业务开始时间
     */
    private Timestamp busStartTime;
    /**
     * 业务结束时间
     */
    private Timestamp busEndTime;
    /**
     * 是否使用最新的binlog
     */
    private String recentBinlog;
    /**
     * Binlog文件名称
     */
    private String binlogName;
    /**
     * Binlog文件位置
     */
    private Long binlogPosition;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

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

    public String getSrcDriver() {
        return srcDriver;
    }

    public void setSrcDriver(String srcDriver) {
        this.srcDriver = srcDriver;
    }

    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }

    public String getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(String srcPort) {
        this.srcPort = srcPort;
    }

    public String getSrcDbName() {
        return srcDbName;
    }

    public void setSrcDbName(String srcDbName) {
        this.srcDbName = srcDbName;
    }

    public String getSrcUsername() {
        return srcUsername;
    }

    public void setSrcUsername(String srcUsername) {
        this.srcUsername = srcUsername;
    }

    public String getSrcPassword() {
        return srcPassword;
    }

    public void setSrcPassword(String srcPassword) {
        this.srcPassword = srcPassword;
    }

    public int getSrcDatasourceId() {
        return srcDatasourceId;
    }

    public void setSrcDatasourceId(int srcDatasourceId) {
        this.srcDatasourceId = srcDatasourceId;
    }

    public String getSrcDatasourceName() {
        return srcDatasourceName;
    }

    public void setSrcDatasourceName(String srcDatasourceName) {
        this.srcDatasourceName = srcDatasourceName;
    }

    public Map<String, String> getFullSyncTablesMap() {
        return fullSyncTablesMap;
    }

    public void setFullSyncTablesMap(Map<String, String> fullSyncTablesMap) {
        this.fullSyncTablesMap = fullSyncTablesMap;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getTarDriver() {
        return tarDriver;
    }

    public void setTarDriver(String tarDriver) {
        this.tarDriver = tarDriver;
    }

    public String getTarIp() {
        return tarIp;
    }

    public void setTarIp(String tarIp) {
        this.tarIp = tarIp;
    }

    public String getTarPort() {
        return tarPort;
    }

    public void setTarPort(String tarPort) {
        this.tarPort = tarPort;
    }

    public String getTarDbName() {
        return tarDbName;
    }

    public void setTarDbName(String tarDbName) {
        this.tarDbName = tarDbName;
    }

    public String getTarUsername() {
        return tarUsername;
    }

    public void setTarUsername(String tarUsername) {
        this.tarUsername = tarUsername;
    }

    public String getTarPassword() {
        return tarPassword;
    }

    public void setTarPassword(String tarPassword) {
        this.tarPassword = tarPassword;
    }

    public int getTarDatasourceId() {
        return tarDatasourceId;
    }

    public void setTarDatasourceId(int tarDatasourceId) {
        this.tarDatasourceId = tarDatasourceId;
    }

    public String getTarDatasourceName() {
        return tarDatasourceName;
    }

    public void setTarDatasourceName(String tarDatasourceName) {
        this.tarDatasourceName = tarDatasourceName;
    }

    public Long getTarSchemaId() {
        return tarSchemaId;
    }

    public void setTarSchemaId(Long tarSchemaId) {
        this.tarSchemaId = tarSchemaId;
    }

    public Timestamp getBusStartTime() {
        return busStartTime;
    }

    public void setBusStartTime(Timestamp busStartTime) {
        this.busStartTime = busStartTime;
    }

    public Timestamp getBusEndTime() {
        return busEndTime;
    }

    public void setBusEndTime(Timestamp busEndTime) {
        this.busEndTime = busEndTime;
    }

    public String getRecentBinlog() {
        return recentBinlog;
    }

    public void setRecentBinlog(String recentBinlog) {
        this.recentBinlog = recentBinlog;
    }

    public String getBinlogName() {
        return binlogName;
    }

    public void setBinlogName(String binlogName) {
        this.binlogName = binlogName;
    }

    public Long getBinlogPosition() {
        return binlogPosition;
    }

    public void setBinlogPosition(Long binlogPosition) {
        this.binlogPosition = binlogPosition;
    }
}
