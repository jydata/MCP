package com.jiuye.mcp.server.model.job;

import com.alibaba.fastjson.JSONObject;
import com.jiuye.mcp.server.model.meta.MetaDdlRulesEntity;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.redisson.api.RQueue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jepson
 * @date 2019/1/7 1:48 PM
 */
public class MySQLIncrementSourceEntity implements Serializable {

    private static final long serialVersionUID = -3408746869062817383L;

    /**
     * alter语句
     */
    String alterSql;

    /**
     * 消息数据
     */
    StringBuffer message = new StringBuffer();
    /**
     * 消息数据的头部
     */
    StringBuffer messageHeader = new StringBuffer();
    /**
     * 消息数据的内容
     */
    StringBuffer messageData = new StringBuffer();

    /**
     * 前缀_后缀
     */
    StringBuffer partitionPK = new StringBuffer();
    /**
     * 前缀  源端id.database.table
     */
    StringBuffer pkPrefix = new StringBuffer();
    /**
     * 后缀  table的主键(联合主键)的值拼接 按-拼接
     */
    StringBuffer pkSuffix = new StringBuffer();

    /**
     * 从维护的表中取得的 table的主键(联合主键)的值的map
     */
    Map<Integer, String> pkSuffixMap = new LinkedHashMap<Integer, String>();
    /**
     * 单一行的列值的数组
     */
    String[] columnValueArr;

    /**
     * sql类型
     */
    List<String> sqlTypeList = new ArrayList<String>();
    /**
     * 未发送成功的消息key存放
     */
    List<String> pkSurplusList = new ArrayList<String>();
    /**
     * 未发送成功的消息message存放
     */
    List<String> messageSurplusList = new ArrayList<String>();

    /**
     * 分区下标 就是agentid  存放的下标
     */
    int agentIndex;

    /**
     * agentid
     */
    String agentId;
    /**
     * server--agent 通道channel
     */
    NioSocketChannel socketChannel;

    /**
     * 指定agent
     */
    List<String> assignAgentList = new ArrayList<String>();
    /**
     * 当前agent
     */
    List<String> currentAgentList;

    /**
     * 发送消息结果
     */
    String sendMessageResult;
    /**
     * 存储发送全部agent的结果
     */
    List<Boolean> sendResultList = new ArrayList<Boolean>();

    /**
     * 临时消息
     */
    String tmpMessage;
    /**
     * json格式的消息
     */
    JSONObject messageJson;


    /**
     * 规则
     */
    List<MetaDdlRulesEntity> ddlRulesList;
    Map<String, String> ddlRulesMap = new LinkedHashMap<String, String>();
    Map<String, String> ddlRuleColumnMap = new LinkedHashMap<String, String>();

    /**
     * 作业ID
     */
    Long jobId;
    /**
     * 路由ID
     */
    Long routeId;
    /**
     * 源端ID
     */
    Long sourceId;


    /**
     * SQL类型
     */
    String type;
    /**
     * 列index
     */
    String colindex;

    /**
     * 源端JDBC的参数
     */
    String host;
    Integer port;
    String userName;
    String userPassword;

    /**
     * 当前binlog文件名称和pos位置
     */
    String binLogFile;
    Long binLogPos;

    /**
     * 是否最新binlog
     */
    String recentBinlog;
    String firstBinlogFilename;
    Long firstPos;

    /**
     * 源端db.table
     */
    String srcDbTableName;
    /**
     * 终端db.table
     */
    String targetDbTableName;
    /**
     * 终端schema
     */
    Long tarSchemaId;

    /**
     * redis 消息队列
     */
    RQueue<String> messageQueue;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getAlterSql() {
        return alterSql;
    }

    public void setAlterSql(String alterSql) {
        this.alterSql = alterSql;
    }

    public StringBuffer getMessage() {
        return message;
    }

    public void setMessage(StringBuffer message) {
        this.message = message;
    }

    public StringBuffer getMessageHeader() {
        return messageHeader;
    }

    public void setMessageHeader(StringBuffer messageHeader) {
        this.messageHeader = messageHeader;
    }

    public StringBuffer getMessageData() {
        return messageData;
    }

    public void setMessageData(StringBuffer messageData) {
        this.messageData = messageData;
    }

    public StringBuffer getPartitionPK() {
        return partitionPK;
    }

    public void setPartitionPK(StringBuffer partitionPK) {
        this.partitionPK = partitionPK;
    }

    public StringBuffer getPkPrefix() {
        return pkPrefix;
    }

    public void setPkPrefix(StringBuffer pkPrefix) {
        this.pkPrefix = pkPrefix;
    }

    public StringBuffer getPkSuffix() {
        return pkSuffix;
    }

    public void setPkSuffix(StringBuffer pkSuffix) {
        this.pkSuffix = pkSuffix;
    }

    public Map<Integer, String> getPkSuffixMap() {
        return pkSuffixMap;
    }

    public void setPkSuffixMap(Map<Integer, String> pkSuffixMap) {
        this.pkSuffixMap = pkSuffixMap;
    }

    public String[] getColumnValueArr() {
        return columnValueArr;
    }

    public void setColumnValueArr(String[] columnValueArr) {
        this.columnValueArr = columnValueArr;
    }

    public List<String> getSqlTypeList() {
        return sqlTypeList;
    }

    public void setSqlTypeList(List<String> sqlTypeList) {
        this.sqlTypeList = sqlTypeList;
    }

    public List<String> getPkSurplusList() {
        return pkSurplusList;
    }

    public void setPkSurplusList(List<String> pkSurplusList) {
        this.pkSurplusList = pkSurplusList;
    }

    public List<String> getMessageSurplusList() {
        return messageSurplusList;
    }

    public void setMessageSurplusList(List<String> messageSurplusList) {
        this.messageSurplusList = messageSurplusList;
    }

    public int getAgentIndex() {
        return agentIndex;
    }

    public void setAgentIndex(int agentIndex) {
        this.agentIndex = agentIndex;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public NioSocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(NioSocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public List<String> getAssignAgentList() {
        return assignAgentList;
    }

    public void setAssignAgentList(List<String> assignAgentList) {
        this.assignAgentList = assignAgentList;
    }

    public List<String> getCurrentAgentList() {
        return currentAgentList;
    }

    public void setCurrentAgentList(List<String> currentAgentList) {
        this.currentAgentList = currentAgentList;
    }

    public String getSendMessageResult() {
        return sendMessageResult;
    }

    public void setSendMessageResult(String sendMessageResult) {
        this.sendMessageResult = sendMessageResult;
    }

    public List<Boolean> getSendResultList() {
        return sendResultList;
    }

    public void setSendResultList(List<Boolean> sendResultList) {
        this.sendResultList = sendResultList;
    }

    public String getTmpMessage() {
        return tmpMessage;
    }

    public void setTmpMessage(String tmpMessage) {
        this.tmpMessage = tmpMessage;
    }

    public JSONObject getMessageJson() {
        return messageJson;
    }

    public void setMessageJson(JSONObject messageJson) {
        this.messageJson = messageJson;
    }

    public List<MetaDdlRulesEntity> getDdlRulesList() {
        return ddlRulesList;
    }

    public void setDdlRulesList(List<MetaDdlRulesEntity> ddlRulesList) {
        this.ddlRulesList = ddlRulesList;
    }

    public Map<String, String> getDdlRulesMap() {
        return ddlRulesMap;
    }

    public void setDdlRulesMap(Map<String, String> ddlRulesMap) {
        this.ddlRulesMap = ddlRulesMap;
    }

    public Map<String, String> getDdlRuleColumnMap() {
        return ddlRuleColumnMap;
    }

    public void setDdlRuleColumnMap(Map<String, String> ddlRuleColumnMap) {
        this.ddlRuleColumnMap = ddlRuleColumnMap;
    }

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

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getBinLogFile() {
        return binLogFile;
    }

    public void setBinLogFile(String binLogFile) {
        this.binLogFile = binLogFile;
    }

    public Long getBinLogPos() {
        return binLogPos;
    }

    public void setBinLogPos(Long binLogPos) {
        this.binLogPos = binLogPos;
    }

    public String getRecentBinlog() {
        return recentBinlog;
    }

    public void setRecentBinlog(String recentBinlog) {
        this.recentBinlog = recentBinlog;
    }

    public String getFirstBinlogFilename() {
        return firstBinlogFilename;
    }

    public void setFirstBinlogFilename(String firstBinlogFilename) {
        this.firstBinlogFilename = firstBinlogFilename;
    }

    public Long getFirstPos() {
        return firstPos;
    }

    public void setFirstPos(Long firstPos) {
        this.firstPos = firstPos;
    }

    public String getSrcDbTableName() {
        return srcDbTableName;
    }

    public void setSrcDbTableName(String srcDbTableName) {
        this.srcDbTableName = srcDbTableName;
    }

    public String getTargetDbTableName() {
        return targetDbTableName;
    }

    public void setTargetDbTableName(String targetDbTableName) {
        this.targetDbTableName = targetDbTableName;
    }

    public Long getTarSchemaId() {
        return tarSchemaId;
    }

    public void setTarSchemaId(Long tarSchemaId) {
        this.tarSchemaId = tarSchemaId;
    }

    public RQueue<String> getMessageQueue() {
        return messageQueue;
    }

    public void setMessageQueue(RQueue<String> messageQueue) {
        this.messageQueue = messageQueue;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColindex() {
        return colindex;
    }

    public void setColindex(String colindex) {
        this.colindex = colindex;
    }

}

