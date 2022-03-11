package com.jiuye.mcp.server.runner.impl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.jiuye.mcp.exception.BizException;
import com.jiuye.mcp.job.executor.QuartzManager;
import com.jiuye.mcp.netty.server.MCPNettyServer;
import com.jiuye.mcp.netty.util.NettySocketChannelHolder;
import com.jiuye.mcp.protobuf.MessageType;
import com.jiuye.mcp.server.dao.job.JobSyncTableMapper;
import com.jiuye.mcp.server.model.SourceRunnerParam;
import com.jiuye.mcp.server.model.job.JobSyncTableEntity;
import com.jiuye.mcp.server.model.meta.MetaDdlRulesEntity;
import com.jiuye.mcp.server.runner.ISourceRunner;
import com.jiuye.mcp.server.service.meta.impl.DdlRulesServiceImpl;
import com.jiuye.mcp.server.util.DecryptUtil;
import com.jiuye.mcp.utils.MessageBuilder;
import io.netty.channel.socket.nio.NioSocketChannel;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ArrayBlockingQueue;



/**
 * @author
 * @date
 * @modified
 */
@Component
public class MySQLFullSourceSyncRunnerImpl implements ISourceRunner {

    private static final Logger logger = LoggerFactory.getLogger(MySQLFullSourceSyncRunnerImpl.class.getName());

    @Autowired
    private MCPNettyServer mcpNettyServer;
    @Autowired
    private DdlRulesServiceImpl ddlRulesServiceImpl;
    @Autowired
    private JobSyncTableMapper jobSyncTableMapper;

    /**
     * 白名单 需要同步的表 第三种规则的
     */
    Map<String, String> ddlRulesMap = new LinkedHashMap<String, String>();
    Map<String, String> ddlRuleColumnMap = new LinkedHashMap<String, String>();

    /**
     * 指定分配AGENT
     */
    List<String> currentAssignAgentList = new ArrayList<String>();

    /**
     * 同步表计数
     */
    int currentSyncTableSize = 1;

    /**
     * Json解析
     */
    private static final JsonFactory jsonFactory = new JsonFactory();

    @Override
    public BinaryLogClient fromSourceIncre(SourceRunnerParam param) throws Exception {
        return null;
    }

    @Override
    public void fromSourceFull(SourceRunnerParam param) {
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(
                1,
                5,
                5,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<Runnable>(10));

        // 作业ID
        Long jobId = param.getJobId();
        // 路由ID 源端ID
        Long routeId = param.getRouteId();

        // 获取最新路由的规则对应的表映射关系
        List<MetaDdlRulesEntity> ddlRulesList = ddlRulesServiceImpl.queryByJobId(jobId);
        for (MetaDdlRulesEntity r : ddlRulesList) {
            String srcSchemaTableName = r.getSrcSchemaName().toLowerCase() + "." + r.getSrcTableName().toLowerCase();
            String tarSchemaTableName = r.getTargetSchemaName().toLowerCase() + "." + r.getTargetTableName().toLowerCase();
            ddlRulesMap.put(srcSchemaTableName, tarSchemaTableName);

            //规则三 新增列名称是作为联合主键的 取值来源于Db name，可以截断或不截断
            if (r.getRuleType().equals("Merge table and add column")) {
                ddlRuleColumnMap.put(srcSchemaTableName, r.getRuleColumn() + "." + r.getSrcSchemaColumn());
            }
        }
        if (ddlRulesMap.size() == 0) {
            logger.error("Sync tables size={},the job{id=} isn't allowed work!\n", 0, jobId);
            return;
        }

        // 当前指定的和现在存活的交集
        currentAssignAgentList.clear();
        // 按指定agent
        List<String> activeAgents = QuartzManager.getActiveAgents(10, param.getAgent());
        if (null == activeAgents || activeAgents.size() <= 0) {
            // 10分钟还没有active的agent启动，update job running -> init
            throw new BizException("No active agent");
        }
        currentAssignAgentList.addAll(activeAgents);

        //同步
        if (currentAssignAgentList.size() == 0) {
            logger.warn("Assign agents are all dead.");
        } else {
            executorService.execute(() -> {
            // 添加管理
            QuartzManager.threadMap.put(jobId,Thread.currentThread());
            // 源端JDBC
            String host = param.getSrcIp();
            Integer port = Integer.valueOf(param.getSrcPort());
            String userName = param.getSrcUsername();
            String userPassword = param.getSrcPassword();
            String srcDbName = param.getSrcDbName();
            boolean executFlag;
            logger.info("Assign agents size: " + currentAssignAgentList.size());
            String srcDbTableName, targetDbTableName, dbName, tableName, timeColumnName, beginTime, endTime;
            StringBuffer where = new StringBuffer();

            // key: db.table  value: where  全量列同步表+where条件
            JobSyncTableEntity syncTableEntity = new JobSyncTableEntity();
            syncTableEntity.setJobId(jobId);
            List<JobSyncTableEntity> syncTableList = jobSyncTableMapper.query(syncTableEntity);
            try {
                //循环同步数据
                for (JobSyncTableEntity syncTable : syncTableList) {
                    where.setLength(0);
                    dbName = syncTable.getDbName();
                    tableName = syncTable.getTableName();
                    timeColumnName = syncTable.getColumnName();
                    beginTime = syncTable.getBeginTime();
                    endTime = syncTable.getEndTime();

                    //oms bmssite_prd contract csp 单库
                    if (dbName.equals("omsprd_bak")) {
                        //omsprd业务线 几张大表是分表
                        srcDbTableName = dbName.replaceAll("_bak", "") + "." + tableName;

                        //去除备份表后缀字符
                        String srcDbTableNameBak = dbName.replaceAll("_bak", "") + "." + tableName.replaceAll("_bak", "");
                        targetDbTableName = ddlRulesMap.get(srcDbTableNameBak);

                        //重新修正 源端mysql jdbc
                        host = "172.20.24.206";
                        port = 3306;
                        userName = "mcp";
                        //"jybigdata123456"
                        userPassword = "FYo8+hNd5zk/FWHxQ0A9B0qqxF8No7xRJUx5opKDAC63bi8tw3J8ND2udNY1qBnzB1DJhD/ukoh/P720W3PbuZv0iFAgBtNqHlK+8BEZiRaW3pB24iPnkzjcAtqWUGsRsoC0LkN9jOKBKCmGSW8hmqM1T2QWvP8J/cvc0/WLLyz/q1IhMzt3dChHGOFRjlLqzNLjA4z4MMaGiKMGml1odd0/pFVMV+bHUy7pxdCSj0uwuzhNRPW+a4E33yPm3AQ2tQid/OEnlZ6j7W4T8TN/jOrAqJ0H6YKyEtXBsQ8br/8YpzM0NifB4h0JBg1sNIn9ZLRiLqVnr1EmFzNgeSxLdg==";
                    } else {
                        //源端 终端 where条件
                        srcDbTableName = dbName + "." + tableName;
                        targetDbTableName = ddlRulesMap.get(srcDbTableName);
                    }

                    // where
                    if (StringUtil.isNotEmpty(timeColumnName)) {
                        where.append(timeColumnName).append(" >='")
                                .append(beginTime).append("' and ")
                                .append(timeColumnName).append(" <='")
                                .append(endTime).append("' ");
                    }

                    //批量抽取数据
                    executFlag = fetchMySQLData(jobId, routeId, srcDbTableName, targetDbTableName, where.toString(), host, port, srcDbName, userName, userPassword);

                    //累计表数量
                    currentSyncTableSize++;
                    if (!executFlag) {
                        logger.error("Current pool executor's task is Interrupted.");
                        return;
                    }
                }
                logger.info("Current pool executor's task is successed.");
                QuartzManager.threadPoolExecutorMap.remove(jobId);
            } catch (Exception ex) {
                logger.error(ex.toString());
            }
            });

            logger.info("Add Full Job Successed....");
            QuartzManager.threadPoolExecutorMap.put(jobId, executorService);
        }

    }

    /**
     * 批量抽取数据 专门针对大数据集
     *
     * @param jobId
     * @param routeId
     * @param srcDbTableName
     * @param targetDbTableName
     * @param where
     * @param host
     * @param port
     * @param userName
     * @param userPassword
     */
    private boolean fetchMySQLData(Long jobId, Long routeId, String srcDbTableName, String targetDbTableName, String where, String host, Integer port, String srcDbName, String userName, String userPassword) {
        boolean flag = true;
        StringBuffer messageHeader = new StringBuffer();
        StringBuffer messageJson = new StringBuffer();

        //初始化行数据量
        Long rowCount = 0L;
        //消息头部
        messageHeader.setLength(0);
        messageHeader.append("{");
        messageHeader.append("\"jobId\":").append(jobId).append(",")
                .append("\"routeId\":").append(routeId).append(",")
                .append("\"srcDbTableName\":\"").append(srcDbTableName).append("\"")
                .append(",\"targetDbTableName\":\"").append(targetDbTableName).append("\"")
                .append(",\"type\":\"").append("fullsync").append("\"");

        //规则三  转换大写
        if (ddlRuleColumnMap.containsKey(srcDbTableName)) {
            messageHeader.append(",\"ddlRuleColumn\":\"").append(ddlRuleColumnMap.get(srcDbTableName).toUpperCase()).append("\"");
        }

        //抽取的sql
        String sql = String.format("select * from %s", srcDbTableName);
        if (StringUtils.isNotEmpty(where)) {
            sql += String.format(" where %s", where);
        }
        logger.info(currentSyncTableSize + ": " + srcDbTableName + ",begin to select: " + sql);

        ByteArrayOutputStream buffer;
        JsonGenerator json;
        try {
            // 抽取数据
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + srcDbName + "?zeroDateTimeBehavior=convertToNull&autoReconnect=true&useSSL=false&tinyInt1isBit=false", userName, DecryptUtil.getInstance().decrypt(userPassword));
            PreparedStatement ps = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ps.setFetchSize(Integer.MIN_VALUE);
            ps.setFetchDirection(ResultSet.FETCH_REVERSE);
            ResultSet rs = ps.executeQuery();

            // 列数量
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int tableColumnCount = rsMetaData.getColumnCount();
            // 构建列索引
            String colindex = getColumnIndex(tableColumnCount);
            messageHeader.append(",\"colindex\":\"(").append(colindex).append(")\"");

            // 循环拼接
            while (rs.next()) {
                //拼接列值
                buffer = new ByteArrayOutputStream();
                json = jsonFactory.createGenerator(buffer);
                // 解决编码问题
                json.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII,true);
                json.writeStartObject();

                for (int i = 1; i <= tableColumnCount; i++) {
                    Object obj = rs.getObject(i);
                    if (obj != null) {
                        String columnType = rsMetaData.getColumnTypeName(i).toUpperCase();
                        if (("BIT".equals(columnType) || "TINYINT".equals(columnType)) &&
                                "Boolean".equals(obj.getClass().getSimpleName())) {
                            if (Boolean.valueOf(obj.toString())) {
                                json.writeObjectField(String.valueOf(i - 1), 1);
                            } else {
                                json.writeObjectField(String.valueOf(i - 1), 0);
                            }

                            continue;
                        }
                    }
                    json.writeObjectField(String.valueOf(i - 1), getValue(obj));
                }
                //组装message
                json.writeEndObject();
                json.flush();
                messageJson.setLength(0);
                messageJson.append(messageHeader.toString())
                        .append(",\"colvalue\":")
                        .append(buffer.toString())
                        .append("}");

                //发送
                sendFullSyncMessage(messageJson.toString());

                //记录条数
                rowCount++;

                // 线程终止
                if (Thread.currentThread().isInterrupted()) {
                    flag = false;
                    break;
                }
                //休眠10s
                /*if (rowCount % 300000 == 0) {
                    Thread.sleep(10000);
                    logger.info("Send agent row count:" + rowCount + ", Sleep 10s.");
                }*/
            }

            //表结束休眠
            int restTime = (int) Math.ceil(rowCount / 300000) + 1;
            logger.info(srcDbTableName + ", select total count (" + rowCount + ") to end! Sleep time(s):" + restTime);
            Thread.sleep(restTime * 1000);
        } catch (Exception ex) {
            logger.error("Fetch mysql data error.", ex);
            flag = false;
        }
        return flag;
    }

    /**
     * 列数量构建索引
     *
     * @param tableColumnCount
     */
    private String getColumnIndex(Integer tableColumnCount) {
        StringBuffer colindex = new StringBuffer();
        for (int i = 0; i < tableColumnCount; i++) {
            colindex.append(i + ",");
        }
        colindex.setLength(colindex.length() - 1);

        return colindex.toString();
    }

    public static Object getValue(Object r) {
        StringBuffer sb = new StringBuffer();

        if (r == null) {
            return null;
        }
        switch (r.getClass().getSimpleName()) {
            case "String":
            case "byte[]":
                sb.append("" + r.toString().replaceAll(",|\'|\"|\t|\n|\r|\r\n|\\\\|:|\\{|}", " ") + "").append("");
                break;
            case "Integer":
            case "BigInteger":
            case "Long":
            case "Float":
            case "Double":
            case "BigDecimal":

            case "Boolean":
                return r;

            case "Date":
            case "Time":
            case "Timestamp":
                sb.append(r);
                break;
        }
        return sb.toString();
    }

    /**
     * 主要列的值 字符串和时间类 两边加单引号
     *
     * @param columnValue
     * @return
     */
    private String getColumnValue(Object columnValue) {
        if (columnValue == null) {
            columnValue = "null,";
        } else {
            switch (columnValue.getClass().getSimpleName()) {
                case "String":
                case "byte[]":
                    //去除逗号 单引号
                    columnValue = "'" + columnValue.toString().replaceAll(",|\'|\"|\t|\r|\n|\r\n|\\\\|:|\\{|}", " ") + "'" + ",";
                    break;

                case "Integer":
                case "BigInteger":
                case "Long":
                case "Float":
                case "Double":
                case "BigDecimal":

                case "Boolean":
                    columnValue = columnValue.toString() + ",";
                    break;

                case "Date":
                case "Time":
                case "Timestamp":
                    columnValue = "'" + columnValue.toString() + "'" + ",";
                    break;
            }
        }

        return columnValue.toString();
    }

    /**
     * 发送数据给agent
     *
     * @param message
     */
    private void sendFullSyncMessage(String message) {
        Random rand = new Random();
        // 随机数取下标,获取agentid
        String agentId = currentAssignAgentList.get(rand.nextInt(currentAssignAgentList.size()));
        NioSocketChannel socketChannel = NettySocketChannelHolder.get(agentId);

        if (socketChannel == null) {
            logger.warn("Current agent[{}] socket channel is null.", agentId);
        } else {
            String sendMessageResult = mcpNettyServer.sendMsg(
                    MessageBuilder.commonMessage(
                            agentId,
                            MessageType.MessageTypeBase.PUSH_DATA,
                            message),
                    socketChannel);

            if (!sendMessageResult.equals("SendMessageSucessful")) {
                logger.error("Fail send to agent {}.\n{}", agentId, sendMessageResult);
            } else {
                //logger.info("Success send to agent {}.\n{}", agentId, message);
            }
        }
    }

}

