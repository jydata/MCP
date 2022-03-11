package com.jiuye.mcp.server.runner.impl;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.jiuye.mcp.netty.server.MCPNettyServer;
import com.jiuye.mcp.netty.util.NettySocketChannelHolder;
import com.jiuye.mcp.protobuf.MessageType;
import com.jiuye.mcp.server.model.SourceRunnerParam;
import com.jiuye.mcp.server.model.meta.MetaDdlRulesEntity;
import com.jiuye.mcp.server.runner.ISourceRunner;
import com.jiuye.mcp.server.service.meta.impl.DdlRulesServiceImpl;
import com.jiuye.mcp.server.util.DecryptUtil;
import com.jiuye.mcp.utils.MessageBuilder;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.util.*;


/**
 * @author
 * @date
 * @modified
 */
@Component
public class MySQLFullSourceRunnerImpl implements ISourceRunner {
    private static final Logger logger = LoggerFactory.getLogger(MySQLFullSourceRunnerImpl.class);

    @Autowired
    private MCPNettyServer mcpNettyServer;

    @Autowired
    private DdlRulesServiceImpl ddlRulesServiceImpl;


    /**
     * 白名单 需要同步的表 第三种规则的
     */
    Map<String, String> fullSyncTablesMap = new LinkedHashMap<String, String>();
    List<MetaDdlRulesEntity> ddlRulesList;
    Map<String, String> ddlRulesMap = new LinkedHashMap<String, String>();
    Map<String, String> ddlRuleColumnMap = new LinkedHashMap<String, String>();

    /**
     * 指定分配AGENT
     */
    ArrayList<String> assignAgentList = new ArrayList<String>();
    ArrayList<String> currentAssignAgentList = new ArrayList<String>();
    String agentId;
    Random rand = new Random();
    /**
     * server--agent 通道channel
     */
    NioSocketChannel socketChannel;
    String sendMessageResult;


    /**
     * 消息数据
     */
    StringBuffer messageHeader = new StringBuffer();
    /**
     * 消息数据
     */
    StringBuffer message = new StringBuffer();
    StringBuffer colindex = new StringBuffer();
    StringBuffer colvalue = new StringBuffer();

    //表列数量
    Integer tableColumnCount;
    //同步表计数
    int currentSyncTableSize = 1;

    /**
     * jdbc
     */
    String sql;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String beginTime, endTime;

    /**
     * 配置文件
     */
    File syncTablefile = new File("/opt/software/mcp/mcp-server-1.0.0/conf/idss.sql");
    File fullTime = new File("/opt/software/mcp/mcp-server-1.0.0/conf/full.time");

    @Override
    public BinaryLogClient fromSourceIncre(SourceRunnerParam param) throws Exception {
        return null;
    }

    @Override
    public void fromSourceFull(SourceRunnerParam param) {
        //作业ID
        Long jobId;
        //路由ID 源端ID
        Long routeId;
        //Long sourceId;

        //源端JDBC
        String host;
        Integer port;
        String userName;
        String userPassword;

        String srcDbTableName;
        String where;
        String targetDbTableName;
        Long tarSchemaId;


        jobId = param.getJobId();
        routeId = param.getRouteId();
        //sourceId = param.getSourceId();

        host = param.getSrcIp();
        port = Integer.valueOf(param.getSrcPort());
        userName = param.getSrcUsername();
        userPassword = param.getSrcPassword();

        //key: db.table  value: where  全量列同步表+where条件
        fullSyncTablesMap = param.getFullSyncTablesMap();
        tarSchemaId = param.getTarSchemaId();

        //获取最新路由的规则对应的表映射关系
        ddlRulesList = ddlRulesServiceImpl.queryByRouteIdAndTarSchemaId(routeId, tarSchemaId);
        for (MetaDdlRulesEntity r : ddlRulesList) {

            ddlRulesMap.put(r.getSrcSchemaName().toLowerCase() + "." + r.getSrcTableName().toLowerCase(),
                    r.getTargetSchemaName().toLowerCase() + "." + r.getTargetTableName().toLowerCase());


            //规则三 新增列名称是作为联合主键的 取值来源于Db name，可以截断或不截断
            if (r.getRuleType().equals("Merge table and add column")) {
                ddlRuleColumnMap.put(r.getSrcSchemaName().toLowerCase() + "." + r.getSrcTableName().toLowerCase(), r.getRuleColumn() + "." + r.getSrcSchemaColumn());
            }
        }
        logger.info("fullSyncTablesMap:\n" + fullSyncTablesMap.keySet().toString());

        if (ddlRulesMap.size() == 0) {
            logger.error("Sync tables size={},the job{id=} isn't allowed work!\n", 0, jobId);
            return;
        }

        assignAgentList.clear();
        currentAssignAgentList.clear();

        // 当前指定的和现在存活的交集
        for (String agent : param.getAgent().split(",")) {
            assignAgentList.add(agent);
        }
        for (String id : assignAgentList) {
            if (NettySocketChannelHolder.get(id) != null) {
                currentAssignAgentList.add(id);
            }
        }

        //需要同步表的列表
        ArrayList<String> syncTableList = txt2List(syncTablefile);

        //同步的时间范围
        ArrayList<String> fullTimeList = txt2List(fullTime);
        for (String fullTimeStr : fullTimeList) {
            String[] fullTimeArr = fullTimeStr.split(",");
            beginTime = fullTimeArr[0];
            endTime = fullTimeArr[1];
        }

        //同步
        if (currentAssignAgentList.size() == 0) {
            logger.warn("Assign agents are all dead.");
        } else {
            logger.info("Assign agents size: " + currentAssignAgentList.size());

            try {
                String[] dbTableCreateTimeArr;

                //循环同步数据
                for (String dbTableCreateTime : syncTableList) {
                    //db table id cretime四个字段
                    dbTableCreateTimeArr = dbTableCreateTime.split(",");

                    //oms bmssite_prd contract csp 单库
                    if (dbTableCreateTimeArr[0].equals("omsprd") || dbTableCreateTimeArr[0].equals("bmssite_prd") || dbTableCreateTimeArr[0].equals("contract") || dbTableCreateTimeArr[0].equals("csp")) {
                        //源端 终端 where条件
                        srcDbTableName = dbTableCreateTimeArr[0] + "." + dbTableCreateTimeArr[1];
                        targetDbTableName = ddlRulesMap.get(srcDbTableName);
                        where = dbTableCreateTimeArr[3] + " >='" + beginTime + "' and " + dbTableCreateTimeArr[3] + " <='" + endTime + "' ";

                        //批量抽取数据
                        fetchMySQLData(jobId, routeId, srcDbTableName, targetDbTableName, where, host, port, userName, userPassword);

                        //累计表数量
                        currentSyncTableSize++;

                    } else if (dbTableCreateTimeArr[0].equals("omsprd_bak")) {

                        //omsprd业务线 几张大表是分表
                        String srcDbTableNameBak = dbTableCreateTimeArr[0].replaceAll("_bak", "") + "." + dbTableCreateTimeArr[1];

                        //去除备份表后缀字符
                        srcDbTableName = dbTableCreateTimeArr[0].replaceAll("_bak", "") + "." + dbTableCreateTimeArr[1].replaceAll("_bak", "");
                        targetDbTableName = ddlRulesMap.get(srcDbTableName);
                        //where = "";
                        where = dbTableCreateTimeArr[3] + " >='" + beginTime + "' and " + dbTableCreateTimeArr[3] + " <='" + endTime + "' ";

                        //重新修正 源端mysql jdbc
                        host = "172.20.24.206";
                        port = 3306;
                        userName = "mcp";
                        //"jybigdata123456"
                        userPassword = "FYo8+hNd5zk/FWHxQ0A9B0qqxF8No7xRJUx5opKDAC63bi8tw3J8ND2udNY1qBnzB1DJhD/ukoh/P720W3PbuZv0iFAgBtNqHlK+8BEZiRaW3pB24iPnkzjcAtqWUGsRsoC0LkN9jOKBKCmGSW8hmqM1T2QWvP8J/cvc0/WLLyz/q1IhMzt3dChHGOFRjlLqzNLjA4z4MMaGiKMGml1odd0/pFVMV+bHUy7pxdCSj0uwuzhNRPW+a4E33yPm3AQ2tQid/OEnlZ6j7W4T8TN/jOrAqJ0H6YKyEtXBsQ8br/8YpzM0NifB4h0JBg1sNIn9ZLRiLqVnr1EmFzNgeSxLdg==";

                        //批量抽取数据
                        fetchMySQLData(jobId, routeId, srcDbTableNameBak, targetDbTableName, where, host, port, userName, userPassword);

                        //累计表数量
                        currentSyncTableSize++;

                    } else if (dbTableCreateTimeArr[0].equals("wms")) {

                        //wms业务线是分库
                        String[] wmsArr = "wmsb01,wmsb02,wmsb03,wmsb04,wmsb05,wmsb06,wmsb07,wmsb08,wmsb09,wmsb10,wmsb11,wmsb27,wmsb31,wmsb32,wmsb33,wmsb34,wmsb35,wmsb36,wmsb37,wmsb38,wmsb39,wmsb40".split(",");
                        for (String wmsdbName : wmsArr) {

                            srcDbTableName = wmsdbName + "." + dbTableCreateTimeArr[1];
                            targetDbTableName = ddlRulesMap.get(srcDbTableName);
                            where = dbTableCreateTimeArr[3] + " >='" + beginTime + "' and " + dbTableCreateTimeArr[3] + " <='" + endTime + "' ";

                            //批量抽取数据
                            fetchMySQLData(jobId, routeId, srcDbTableName, targetDbTableName, where, host, port, userName, userPassword);

                            //累计表数量
                            currentSyncTableSize++;
                        }
                    }

                }
            } catch (Exception ex) {
                logger.error(ex.toString());
            }

        }

    }


    /**
     * txt文件转换为list
     *
     * @param file
     * @return
     */
    private ArrayList txt2List(File file) {
        ArrayList<String> result = new ArrayList<String>();
        try {
            //构造一个BufferedReader类来读取文件
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s = null;
            while ((s = br.readLine()) != null) {
                //使用readLine方法，一次读一行
                if (!s.startsWith("--")) {
                    result.add(s);
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 列数量构建索引
     *
     * @param tableColumnCount
     */
    private void getColumnIndex(Integer tableColumnCount) {
        colindex.setLength(0);
        for (int i = 0; i < tableColumnCount; i++) {
            colindex.append(i + ",");
        }

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
    private void fetchMySQLData(Long jobId, Long routeId, String srcDbTableName, String targetDbTableName, String where, String host, Integer port, String userName, String userPassword) {
        try {
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
            sql = String.format("select * from %s", srcDbTableName);
            if (where != null && !where.equals("")) {
                sql += String.format(" where %s", where);
            }
            logger.info(currentSyncTableSize + ": " + srcDbTableName + ",begin to select: " + sql);

            //抽取数据
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/mysql?zeroDateTimeBehavior=convertToNull&useSSL=false&tinyInt1isBit=false&autoReconnect=true", userName, DecryptUtil.getInstance().decrypt(userPassword));
            //解决大数据集抽取
            ps = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ps.setFetchSize(Integer.MIN_VALUE);
            ps.setFetchDirection(ResultSet.FETCH_REVERSE);
            rs = ps.executeQuery();

            //列数量
            colindex.setLength(0);
            tableColumnCount = rs.getMetaData().getColumnCount();
            getColumnIndex(tableColumnCount);
            colindex.setLength(colindex.length() - 1);
            messageHeader.append(",\"colindex\":\"(").append(colindex.toString()).append(")\"");

            //循环拼接
            while (rs.next()) {

                //拼接列值
                colvalue.setLength(0);
                for (int i = 1; i <= tableColumnCount; i++) {
                    colvalue.append(getColumnValue(rs.getObject(i)));
                }
                colvalue.setLength(colvalue.length() - 1);

                //组装message
                message.setLength(0);
                message.append(messageHeader.toString())
                        .append(",\"colvalue\":\"(")
                        .append(colvalue.toString())
                        .append(")\"").append("}");
                //发送
                sendFullSyncMessage(message.toString());

                //记录条数
                rowCount++;
                //休眠5s
                if (rowCount % 500000 == 0) {
                    Thread.sleep(5000);
                    logger.info("rowCount:" + rowCount + ", is sleep 5s.");
                }
            }
            //表结束休眠
            logger.info(srcDbTableName + ",select to end! Sleep time(s):" + ((int) Math.ceil(rowCount / 50000) + 1));
            Thread.sleep(((int) Math.ceil(rowCount / 50000) + 1) * 1000);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * 发送数据给agent
     *
     * @param message
     */
    private void sendFullSyncMessage(String message) {
        //随机数取下标,获取agentid
        agentId = currentAssignAgentList.get(rand.nextInt(currentAssignAgentList.size()));
        socketChannel = NettySocketChannelHolder.get(agentId);

        if (socketChannel == null) {
            logger.warn("Current agent[{}] socket channel is null.", agentId);

        } else {
            sendMessageResult = mcpNettyServer.sendMsg(
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

