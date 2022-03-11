package com.jiuye.mcp.server.runner.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import com.github.shyiko.mysql.binlog.utils.Column;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jiuye.mcp.exception.BizException;
import com.jiuye.mcp.job.executor.QuartzManager;
import com.jiuye.mcp.netty.server.MCPNettyServer;
import com.jiuye.mcp.netty.util.ColumnPK;
import com.jiuye.mcp.netty.util.NettySocketChannelHolder;
import com.jiuye.mcp.protobuf.MessageType;
import com.jiuye.mcp.server.model.SourceRunnerParam;
import com.jiuye.mcp.server.model.job.MySQLIncrementSourceEntity;
import com.jiuye.mcp.server.model.meta.MetaDdlRulesEntity;
import com.jiuye.mcp.server.runner.ISourceRunner;
import com.jiuye.mcp.server.service.job.impl.JobDefinitionServiceImpl;
import com.jiuye.mcp.server.service.meta.IDataSourceSyncService;
import com.jiuye.mcp.server.service.meta.IDdlRulesService;
import com.jiuye.mcp.server.util.DecryptUtil;
import com.jiuye.mcp.server.util.PartitionUtil;
import com.jiuye.mcp.utils.MessageBuilder;
import com.jiuye.mcp.utils.MySQLBinLogUtil;
import com.jiuye.mcp.utils.PhoenixUtil;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static com.jiuye.mcp.server.util.PartitionUtil.murmur2;
import static com.jiuye.mcp.server.util.PartitionUtil.toPositive;


/**
 * @author
 * @date
 * @modified
 */
@Component
public class MySQLIncrementSourceRunnerImpl implements ISourceRunner {

    private static final Logger logger = LoggerFactory.getLogger(MySQLIncrementSourceRunnerImpl.class.getName());

    @Autowired
    RedissonClient redissonClient;
    @Autowired
    private MCPNettyServer mcpNettyServer;
    @Autowired
    private PhoenixUtil phoenixUtil;
    @Autowired
    private PhoenixTargetAlterParseImpl phoenixTargetAlterParse;
    @Autowired
    private ColumnPK columnPK;
    @Autowired
    private JobDefinitionServiceImpl jobDefinitionServiceImpl;
    @Autowired
    private IDataSourceSyncService dataSourceSyncService;
    @Autowired
    private IDdlRulesService ddlRulesServiceImpl;

    public BinaryLogClient.EventListener eventListener;
    public BinaryLogClient.LifecycleListener lifeListener;

    /* XA事务功能支持撤销1
    public static final String XABEGIN = "XA BEGIN";
    public static final String XASTART = "XA START";
    public static final String XAEND = "XA END";
    public static final String XACOMMIT = "XA COMMIT";
    public static final String XAROLLBACK = "XA ROLLBACK";*/

    @Override
    public BinaryLogClient fromSourceIncre(SourceRunnerParam param) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        MySQLIncrementSourceEntity increEntity = new MySQLIncrementSourceEntity();

        //初始化参数获取
        increEntity.setJobId(param.getJobId());
        increEntity.setRouteId(param.getRouteId());
        increEntity.setSourceId(param.getSourceId());

        increEntity.setHost(param.getSrcIp());
        increEntity.setPort(Integer.valueOf(param.getSrcPort()));
        increEntity.setUserName(param.getSrcUsername());
        increEntity.setUserPassword(param.getSrcPassword());

        increEntity.setRecentBinlog(param.getRecentBinlog());
        increEntity.setFirstBinlogFilename(param.getBinlogName());
        increEntity.setFirstPos(param.getBinlogPosition());
        increEntity.setTarSchemaId(param.getTarSchemaId());

        // 按指定agent
        List<String> activeAgents = QuartzManager.getActiveAgents(10, param.getAgent());
        if (null == activeAgents || activeAgents.size() <= 0) {
            // 10分钟还没有active的agent启动，update job running -> init
            throw new BizException("No active agent");
        }
        increEntity.setAssignAgentList(activeAgents);

        // 取所有agent的所有的channel
        // increEntity.setAssignAgentList(NettySocketChannelHolder.getAgentIds());
        logger.info("Assign agents: " + increEntity.getAssignAgentList().toString());

        // 获取最新路由的规则对应的表映射关系: 1.白名单  2.源端终端
        List<MetaDdlRulesEntity> ddlRuleList = ddlRulesServiceImpl.queryByJobId(increEntity.getJobId());
        for (MetaDdlRulesEntity ddlRule : ddlRuleList) {
            String srcSchemaTableName = ddlRule.getSrcSchemaName().toLowerCase() + "." + ddlRule.getSrcTableName().toLowerCase();
            String tarSchemaTableName = ddlRule.getTargetSchemaName().toLowerCase() + "." + ddlRule.getTargetTableName().toLowerCase();

            increEntity.getDdlRulesMap().put(srcSchemaTableName, tarSchemaTableName);

            // 规则三 新增列名称是作为联合主键的 取值来源于Db name，可以截断或不截断
            if ("Merge table and add column".equals(ddlRule.getRuleType())) {
                increEntity.getDdlRuleColumnMap().put(srcSchemaTableName, ddlRule.getRuleColumn() + "." + ddlRule.getSrcSchemaColumn());
            }
        }
        if (increEntity.getDdlRulesMap().size() == 0) {
            logger.error("Sync tables size = 0,the job{id=} isn't allowed work!\n", increEntity.getJobId());
            return null;
        }

        //Redis 队列
        increEntity.setMessageQueue(redissonClient.getQueue("mcp_job_message_queue"));

        logger.info("Start to incremenet data.");
        BinaryLogClient client = new BinaryLogClient(increEntity.getHost(), increEntity.getPort(), increEntity.getUserName(), DecryptUtil.getInstance().decrypt(increEntity.getUserPassword()));
        // 在线程中启动事件监听
        executorService.submit(() -> {
            // 事件监听
            client.registerEventListener(eventListener = new BinaryLogClient.EventListener() {
                /* XA事务功能支持撤销2
                HashMap<String, Queue<Event>> eventBufferHashMapXA = new HashMap();
                boolean xaFlag = false;
                Queue<Event> queue;*/

                @Override
                public void onEvent(Event event) {
                    // 维护
                    QuartzManager.eventMap.put(increEntity.getJobId(), eventListener);

                    EventData data = event.getData();

                    /* XA事务功能支持撤销3
                    // 在XA 事务中，且不属于 QueryEvent 事件，那么将此 event 丢进队列中
                    if (xaFlag && !(data instanceof QueryEventData)) {
                        queue.offer(event);
                        return;
                    }*/

                    if (data instanceof QueryEventData) {
                        increEntity.setBinLogFile(client.getBinlogFilename());
                        increEntity.setBinLogPos(client.getBinlogPosition());

                        // alter语句
                        if (((QueryEventData) data).getSql().toLowerCase().contains("alter")) {
                            increEntity.getMessageHeader().append(",\"type\":\"").append("alter").append("\"");
                            //缺.tablename
                            increEntity.setSrcDbTableName(((QueryEventData) data).getDatabase());

                            deleteAndAlterToSend(increEntity, "alter", ((QueryEventData) data).getSql().replaceAll("\n|\r|\r\n|\t", " "));
                        }

                        /* XA事务功能支持撤销4
                        // XA START / XA BEGIN
                        if (((QueryEventData) data).getSql().toUpperCase().startsWith(XABEGIN) || ((QueryEventData) data).getSql().toUpperCase().startsWith(XASTART)) {
                            queue = new LinkedList<>();
                            xaFlag = true;
                            String sql = ((QueryEventData) data).getSql();
                            String xAid = getXAid(sql);
                            logger.info("[Dove][XA START/BEGIN] XAid: " + xAid + ". Job Id:"+increEntity.getJobId());
                        }
                        // XA END
                        if (((QueryEventData) data).getSql().toUpperCase().startsWith(XAEND)) {
                            xaFlag = false;
                            String sql = ((QueryEventData) data).getSql();
                            String xAid = getXAid(sql);
                            eventBufferHashMapXA.put(xAid, queue);
                            logger.info("[Dove][XA END] XAid: " + xAid + ". The size of event queue is: " + queue.size() + ". The size of eventBufferHashMapXA is: " + eventBufferHashMapXA.size() + ". Job Id:"+increEntity.getJobId());
                            queue = null;
                        }
                        // XA COMMIT
                        if (((QueryEventData) data).getSql().toUpperCase().startsWith(XACOMMIT)) {
                            String sql = ((QueryEventData) data).getSql();
                            String xAid = getXAid(sql);
                            Queue<Event> xaQueue = eventBufferHashMapXA.remove(xAid);
                            dealXaTransaction(xaQueue);
                            logger.info("[Dove][XA COMMIT] XAid: " + xAid + ". The size of eventBufferHashMapXA is: " + eventBufferHashMapXA.size() + ". Job Id:"+increEntity.getJobId());
                        }
                        // XA ROLLBACK 丢球
                        if (((QueryEventData) data).getSql().toUpperCase().startsWith(XAROLLBACK)) {
                            String sql = ((QueryEventData) data).getSql();
                            String xAid = getXAid(sql);
                            int size = eventBufferHashMapXA.remove(xAid).size();
                            logger.info("[Dove][XA ROLLBACK] XAid: " + xAid + ". The size of eventBufferHashMapXA is: " + eventBufferHashMapXA.size() + ". Job Id:"+increEntity.getJobId());
                        }*/
                    } else if (data instanceof RowsQueryEventData) {

                    } else if (data instanceof TableMapEventData) {
                        dealTableMapData(data);
                    } else if (data instanceof WriteRowsEventData) {
                        increEntity.setType(",\"type\":\"insert\"");
                        increEntity.setColindex(",\"colindex\":\"" + ((WriteRowsEventData) data).getColumnIndex() + "\"");

                        // Insert
                        dmlToSend(increEntity, ((WriteRowsEventData) data).getRows());
                    } else if (data instanceof UpdateRowsEventData) {
                        increEntity.setType(",\"type\":\"update\"");
                        increEntity.setColindex(",\"colindex\":\"" + ((UpdateRowsEventData) data).getColumnIndex() + "\"");

                        // Update
                        dmlToSend(increEntity, ((UpdateRowsEventData) data).getNewRows());
                    } else if (data instanceof DeleteRowsEventData) {
                        increEntity.setType(",\"type\":\"delete\"");
                        increEntity.setColindex(",\"colindex\":\"" + ((DeleteRowsEventData) data).getColumnIndex() + "\"");

                        // Delete
                        dmlToSend(increEntity, ((DeleteRowsEventData) data).getRows());
                    } else if (data instanceof XidEventData) {
                        // 清空
                        clearIncre(increEntity);
                    }
                }
                /* XA事务功能支持撤销5
                private String getXAid(String sql) {
                    // add by dove at 20190917 , fix XA transaction Bug
                    // XA START X'31302e32372e392e322e746d313536383531353533373035333139393539',X'31302e32372e392e322e746d323134343737',1096044365
                    return sql.split(",")[0].split(" ")[2].replace("X'", "").replace("'", "");
                }
                public void dealXaTransaction(Queue<Event> xaQueue){
                    Event event ;
                    while(true){
                        event = xaQueue.poll();
                        // XA event 结束
                        if(event == null){
                            return;
                        }
                        EventData data = event.getData();
                        if (data instanceof TableMapEventData) {
                            dealTableMapData(data);
                        } else if (data instanceof WriteRowsEventData) {
                            increEntity.setType(",\"type\":\"insert\"");
                            increEntity.setColindex(",\"colindex\":\"" + ((WriteRowsEventData) data).getColumnIndex() + "\"");

                            // Insert
                            dmlToSend(increEntity, ((WriteRowsEventData) data).getRows());
                        } else if (data instanceof UpdateRowsEventData) {
                            increEntity.setType(",\"type\":\"update\"");
                            increEntity.setColindex(",\"colindex\":\"" + ((UpdateRowsEventData) data).getColumnIndex() + "\"");

                            // Update
                            dmlToSend(increEntity, ((UpdateRowsEventData) data).getNewRows());
                        } else if (data instanceof DeleteRowsEventData) {
                            increEntity.setType(",\"type\":\"delete\"");
                            increEntity.setColindex(",\"colindex\":\"" + ((DeleteRowsEventData) data).getColumnIndex() + "\"");

                            // Delete
                            dmlToSend(increEntity, ((DeleteRowsEventData) data).getRows());
                        }
                    }
                }*/

                public void dealTableMapData(EventData data){
                    // database table
                    increEntity.getMessageHeader().setLength(0);

                    // 源端 终端table name
                    increEntity.setSrcDbTableName(((TableMapEventData) data).getDatabase().toLowerCase()
                            + "." + ((TableMapEventData) data).getTable().toLowerCase());

                    increEntity.setTargetDbTableName(
                            increEntity.getDdlRulesMap().get(increEntity.getSrcDbTableName())
                    );

                    // 判断是否拼接
                    if (!increEntity.getMessageHeader().toString().contains("srcDbTableName")) {
                        increEntity.getMessageHeader().append(",\"srcDbTableName\":\"").append(increEntity.getSrcDbTableName()).append("\"");

                        if (increEntity.getTargetDbTableName() == null) {
                            increEntity.getMessageHeader().append(",\"targetDbTableName\":\"").append("").append("\"");
                        } else {
                            increEntity.getMessageHeader().append(",\"targetDbTableName\":\"").append(increEntity.getTargetDbTableName()).append("\"");
                        }

                        // 规则三
                        if (increEntity.getDdlRuleColumnMap().containsKey(increEntity.getSrcDbTableName())) {
                            increEntity.getMessageHeader().append(",\"ddlRuleColumn\":\"").append(increEntity.getDdlRuleColumnMap().get(increEntity.getSrcDbTableName())).append("\"");
                        }
                    }

                    // 获取前缀
                    getPKSuffixMap(increEntity);
                }
            });

            // 生命周期监听
            client.registerLifecycleListener(lifeListener = new BinaryLogClient.LifecycleListener() {
                @Override
                public void onConnect(BinaryLogClient client) {
                    QuartzManager.lifeMap.put(increEntity.getJobId(), lifeListener);
                    logger.info("MySQL Binlog connect is successful.");
                }

                @Override
                public void onCommunicationFailure(BinaryLogClient client, Exception ex) {
                    client.unregisterEventListener(eventListener);
                    client.unregisterLifecycleListener(lifeListener);
                    try {
                        client.disconnect();
                        logger.error("MySQL Binlog communication fail and Close it!", ex);
                        executorService.shutdown();
                        executorService.shutdownNow();
                    } catch (IOException e) {
                        logger.error("MySQL Client Close Failed!", e);
                    }
                }

                @Override
                public void onEventDeserializationFailure(BinaryLogClient client, Exception ex) {
                    logger.error("MySQL Binlog event deserialization fail", ex);
                }

                @Override
                public void onDisconnect(BinaryLogClient client) {
//                    QuartzManager.lifeMap.remove(increEntity.getJobId());
                    logger.warn("MySQL Binlog is disconnect");
                }
            });

            // 设置server id
            // client.setServerId(toPositive(murmur2(increEntity.getHost().getBytes()))); //对increEntity.getHost()值处理,和主库的server id不一致
            // 对increEntity.getHost()值处理,和主库的server id不一致
            client.setServerId(toPositive(murmur2(increEntity.getJobId().toString().getBytes())));

            //测试
/*            client.setBinlogFilename("mysql-bin.002776");
            client.setBinlogPosition(636364786);*/

            // 配置当前位置 单点的作废
            //MySQLBinLogUtil.configBinaryLogStatus(redissonClient, client, increEntity.getJobId(), increEntity.getRecentBinlog(), increEntity.getFirstBinlogFilename(), increEntity.getFirstPos());

            // 配置当前位置
            MySQLBinLogUtil.configBinaryLogStatusFromRMap(redissonClient, client, increEntity.getJobId(), increEntity.getRecentBinlog(), increEntity.getFirstBinlogFilename(), increEntity.getFirstPos(), increEntity.getAssignAgentList());

            client.setKeepAlive(true);
            client.setKeepAliveInterval(30000);
            client.setHeartbeatInterval(5000);
            client.setConnectTimeout(60000);
        });

        return client;
    }

    @Override
    public void fromSourceFull(SourceRunnerParam param) throws Exception {
    }

    private void clearIncre(MySQLIncrementSourceEntity increEntity) {
        increEntity.getMessageHeader().setLength(0);
        increEntity.getMessageData().setLength(0);
        increEntity.getMessage().setLength(0);
        increEntity.getPartitionPK().setLength(0);
        increEntity.getPkPrefix().setLength(0);
        increEntity.getPkSuffix().setLength(0);
        increEntity.setSrcDbTableName("");
        increEntity.setTargetDbTableName("");
        increEntity.setType("");
        increEntity.setColindex("");
    }

    /**
     * 去源端查询查询 更新 columnPK 和 pkSuffixMap
     * routeId-->sourceId-->MySQL JDBC -->源端 information_schema.columns 表实时查询是否有,假如没有，就放一个空结构 占位
     */
    private void getPKSuffixMap(MySQLIncrementSourceEntity increEntity) {
        increEntity.getPkPrefix().setLength(0);
        // 如 184.test.stu
        increEntity.getPkPrefix().append(increEntity.getSourceId() + "." + increEntity.getSrcDbTableName());
        // 获取null 大部分1个 少部分>=1个
        increEntity.setPkSuffixMap(columnPK.getDbTableColPK().get(increEntity.getPkPrefix().toString()));

        if (increEntity.getPkSuffixMap() == null) {
            Connection conn = null;
            Statement stmt = null;
            ResultSet rs;

            ConcurrentHashMap<Integer, String> colPKUpdate = new ConcurrentHashMap<Integer, String>();
            try {
                // 注册 JDBC 驱动
                Class.forName("com.mysql.jdbc.Driver");
                // 打开链接
                conn = DriverManager.getConnection("jdbc:mysql://" + increEntity.getHost() + ":" + increEntity.getPort() + "/", increEntity.getUserName(), DecryptUtil.getInstance().decrypt(increEntity.getUserPassword()));
                // 执行查询
                stmt = conn.createStatement();
                String sql;
                sql = "SELECT\n" +
                        " concat_ws('.',table_schema,table_name) as db_table," +
                        " ordinal_position -1 as column_pos,column_name,column_key" +
                        " FROM" +
                        " INFORMATION_SCHEMA.COLUMNS" +
                        " WHERE TABLE_SCHEMA ='" + increEntity.getSrcDbTableName().split("\\.")[0] + "'   and TABLE_NAME='" + increEntity.getSrcDbTableName().split("\\.")[1] + "' and column_key='PRI'";
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    colPKUpdate.put(rs.getInt(2), rs.getString(3));
                }

                // colPKUpdate 有可能是空结构 占位，以防二次查询 消耗性能
                columnPK.getDbTableColPK().put(increEntity.getPkPrefix().toString(), colPKUpdate);
                increEntity.setPkSuffixMap(colPKUpdate);

                // 完成后关闭
                rs.close();
                stmt.close();
                conn.close();

                // logger.info("Update mysql[" + increEntity.getPkPrefix().toString() + "] pk info is ok.");
            } catch (SQLException se) {
                // 处理 JDBC 错误
                logger.error("Connect jdbc error.", se);
            } catch (Exception e) {
                // 处理 Class.forName 错误
                logger.error("Load driver error.", e);
            } finally {
                // 关闭资源
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                } catch (SQLException se2) {
                }// 什么都不做
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            }
        }
    }


    /**
     * delete alter  发送
     */
    private void deleteAndAlterToSend(MySQLIncrementSourceEntity increEntity, String sqlType, String srcAlterSql) {
        if ("alter".equals(sqlType)) {
            // 解析成phoenix sql
            increEntity.setAlterSql(phoenixTargetAlterParse.alterSQLParse(increEntity.getDdlRulesMap(), increEntity.getSrcDbTableName(), srcAlterSql));
            increEntity.setSrcDbTableName(phoenixTargetAlterParse.getSrcDbTableName());
            increEntity.setTargetDbTableName(phoenixTargetAlterParse.getTargetDbTableName());
            // partitionPK
            increEntity.getPartitionPK().setLength(0);
            // 如 test.stu_
            increEntity.getPartitionPK()
                    .append(increEntity.getSrcDbTableName())
                    .append("_");

            // 拼接message
            increEntity.getMessageHeader().append(",\"srcDbTableName\":\"").append(increEntity.getSrcDbTableName()).append("\"");

            increEntity.getMessageData().setLength(0);
            increEntity.getMessageData().append(",\"targetDbTableName\":\"").append(increEntity.getTargetDbTableName()).append("\"")
                    .append(",\"srcAlterSql\":\"").append(srcAlterSql).append("\"")
                    .append(",\"targetAlterSql\":\"").append(increEntity.getAlterSql()).append("\"")
                    .append(",\"key\":\"").append(increEntity.getJobId() + "_" + increEntity.getBinLogFile() + "_" + increEntity.getBinLogPos().toString() + "_" + increEntity.getPartitionPK()).append("\"")
                    .append("}");

            increEntity.getMessage().setLength(0);
            increEntity.getMessage()
                    .append("{\"jobId\":").append(increEntity.getJobId()).append(",")
                    .append("\"routeId\":").append(increEntity.getRouteId())
                    .append(increEntity.getMessageHeader())
                    .append(increEntity.getMessageData());
        } else { //delete
           /* increEntity.getPartitionPK().setLength(0);
            increEntity.getPkSuffix().setLength(0);
            increEntity.getPartitionPK()
                    .append(increEntity.getPkPrefix())
                    .append("_")
                    .append(increEntity.getPkSuffix()); //如 test.stu_

            increEntity.getMessageData().setLength(0);
            increEntity.getMessageData().append("}");
            increEntity.getMessage().setLength(0);
            increEntity.getMessage()
                    .append("{\"jobId\":").append(increEntity.getJobId()).append(",")
                    .append("\"routeId\":").append(increEntity.getRouteId())
                    .append(increEntity.getMessageHeader())
                    .append(",\"key\":\"").append(increEntity.getJobId() + "_" + increEntity.getBinLogFile() + "_" + increEntity.getBinLogPos().toString() + "_" + increEntity.getPartitionPK()).append("\"")
                    .append(increEntity.getMessageData());*/
        }

        getPreChannelToSend(increEntity, sqlType);
    }

    private static final JsonFactory jsonFactory = new JsonFactory();

    private String messageDataToJson(MySQLIncrementSourceEntity increEntity, Serializable[] data) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        JsonGenerator json = null;

        String[] columnValueArr = new String[increEntity.getColindex().split(",").length - 1];
        try {
            // logger.info("[Dove] Create a JsonGenerator from jsonFactory by ByteArrayOutputStream!")

            json = jsonFactory.createGenerator(buffer);
            // 数据编码格式
            json.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII,true);
            json.writeStartObject();

            // logger.info("[Dove] create a JsonGenerator success!")
            // logger.info("[Dove] JsonGenerator is null ? : " + (json == null))

            int i = 0;
            // logger.info("[Dove] data Size : " + data.length)
            for (Object r : data) {
                //logger.info("[Dove] data[i]: " + r)
                Object value = Column.getValue(r);
                //logger.info("[Dove] info col's value:" + value)

                json.writeObjectField(String.valueOf(i), value);
                if (value instanceof String) {
                    columnValueArr[i] = "'" + value + "'";
                } else {
                    columnValueArr[i] = String.valueOf(value);
                }
                i++;
            }

            json.writeEndObject();
            json.flush();
            increEntity.setColumnValueArr(columnValueArr);
        } catch (IOException e) {
            logger.error(" messageDataToJson() + IO Exception : " + e.getMessage());
        }

        return buffer.toString();
    }

    private void dmlToSend(MySQLIncrementSourceEntity increEntity, List<Serializable[]> rows) {
        // 非白名单，不执行dml解析
        if (!increEntity.getDdlRulesMap().containsKey(increEntity.getSrcDbTableName())) {
            return;
        }
        // 拆分消息 发送
        for (Serializable[] data : rows) {
            // 去除前后()，然后按逗号分隔，无风险，因为com.github.shyiko.mysql.binlog.utils.Column.java文件已经处理
//            increEntity.setColumnValueArr(row.substring(1, row.length() - 1).split(","))

            // todo get messageData
            String row = messageDataToJson(increEntity, data);

            //logger.info("[Dove] json row : " + row);

            // 1.取PK
            if (increEntity.getPkSuffixMap().size() > 0) {
                increEntity.getPkSuffix().setLength(0);
                JsonObject jsonObj = new JsonParser().parse(row).getAsJsonObject();

                // todo get PK value
                for (Integer id : increEntity.getPkSuffixMap().keySet()) {
                    // 根据下标id，取值 拼接
                    increEntity.getPkSuffix().append(String.valueOf(jsonObj.get(String.valueOf(id))).replace("\"","'")).append("-");
                }
                // 删除最后一个-
                increEntity.getPkSuffix().deleteCharAt(increEntity.getPkSuffix().length() - 1);
            }

            increEntity.getPartitionPK().setLength(0);
            // 如 有主键表 1.test.stu_'jepson'  或 无主键表 test.e_
            increEntity.getPartitionPK()
                    .append(increEntity.getPkPrefix())
                    .append("_")
                    .append(increEntity.getPkSuffix());

            // 2.拼接消息
            increEntity.getMessageData().setLength(0);
            increEntity.getMessageData().append(",\"colvalue\":").append(row).append("}");

            increEntity.getMessage().setLength(0);
            increEntity.getMessage()
                    .append("{\"jobId\":").append(increEntity.getJobId()).append(",")
                    .append("\"routeId\":").append(increEntity.getRouteId())
                    .append(increEntity.getMessageHeader())
                    .append(increEntity.getType())
                    .append(increEntity.getColindex())
                    .append(",\"key\":\"").append(increEntity.getJobId() + "_" + increEntity.getBinLogFile() + "_" + increEntity.getBinLogPos().toString() + "_" + increEntity.getPartitionPK()).append("\"")
                    .append(increEntity.getMessageData());

            //3.准备获取channel
            getPreChannelToSend(increEntity, "dml");
        }
    }

    /**
     * 预获取channel
     *
     * @param sqlType
     */
    private void getPreChannelToSend(MySQLIncrementSourceEntity increEntity, String sqlType) {
        // 3.判断当前channel通道个数是否为0
        if (NettySocketChannelHolder.getChannelMap().size() == 0) {
            // 没有可用的agent对应的channel，就存放
            logger.warn("There is no living agent!");

            // 发送未成功的 表过滤
            if (increEntity.getDdlRulesMap().containsKey(increEntity.getSrcDbTableName())) {
                increEntity.getSqlTypeList().add(sqlType);
                increEntity.getPkSurplusList().add(increEntity.getPartitionPK().toString());
                increEntity.getMessageSurplusList().add(increEntity.getMessage().toString());
            } else {
                clearIncre(increEntity);
            }
        } else {
            // 先判断 pkList/messageList 是否有数据需要发送
            if (increEntity.getPkSurplusList().size() > 0) {
                IntStream.range(0, increEntity.getPkSurplusList().size()).
                        forEach(i -> {
                            //剩余消息，发送成功就移除，失败就继续存放
                            getChannelToSend(increEntity, increEntity.getSqlTypeList().get(0), increEntity.getPkSurplusList().get(0), increEntity.getMessageSurplusList().get(0), "surplus");
                        });
            }

            // 表过滤
            if (increEntity.getDdlRulesMap().containsKey(increEntity.getSrcDbTableName())) {
                //正常消息，发送失败才会添加到list
                getChannelToSend(increEntity, sqlType, increEntity.getPartitionPK().toString(), increEntity.getMessage().toString(), "normal");
            } else {
                clearIncre(increEntity);
            }
        }
    }


    /**
     * 获取channel发送
     *
     * @param sqlType
     * @param partitionPK
     * @param messageStr
     * @param messageType
     */
    private void getChannelToSend(MySQLIncrementSourceEntity increEntity, String sqlType, String partitionPK, String messageStr, String messageType) {
        if ("delete".equals(sqlType) || "alter".equals(sqlType)) {
            sendMessageToAllAgent(increEntity, sqlType, partitionPK, messageStr, messageType);
        } else {
            //dml
            // 根据pk 将hash(DB-TABLE-PK)%agent个数 取模，最终取得agentId和对应socketChannel
            // 取的下标
            increEntity.setAgentIndex(PartitionUtil.partition(partitionPK, increEntity.getAssignAgentList().size()));
            if (increEntity.getAgentIndex() == 999999999) {
                increEntity.getSqlTypeList().add(sqlType);
                increEntity.getPkSurplusList().add(partitionPK);
                increEntity.getMessageSurplusList().add(messageStr);
            } else {
                increEntity.setAgentId(increEntity.getAssignAgentList().get(increEntity.getAgentIndex()));
                // 等价 NettySocketChannelHolder.getChannels().get(partition)
                increEntity.setSocketChannel(NettySocketChannelHolder.get(increEntity.getAgentId()));

                // 没有agent channel
                if (increEntity.getSocketChannel() == null) {
                    increEntity.setCurrentAgentList((List<String>) increEntity.getAssignAgentList());
                    // 移除当前失效的
                    increEntity.getCurrentAgentList().remove(increEntity.getAgentId());
                    logger.info("Agent[{}] is invaild, and removed.Again get channel to send.", increEntity.getAgentId());

                    againGetChannelToSend(increEntity, sqlType, partitionPK, messageStr, messageType, increEntity.getCurrentAgentList());
                } else {
                    //取到对应的channel  发送消息
                    sendDMLMessage(increEntity, sqlType, partitionPK, messageStr, messageType);
                }
            }
        }
    }

    /**
     * 再一次获取channel发送
     *
     * @param partitionPK
     * @param messageStr
     * @param type
     * @param surplusCurrentAgentList 剩余的当前指定的agent列表
     */
    private void againGetChannelToSend(MySQLIncrementSourceEntity increEntity, String sqlType, String partitionPK, String messageStr, String type, List<String> surplusCurrentAgentList) {
        switch (surplusCurrentAgentList.size()) {
            case 0:
                increEntity.getSqlTypeList().add(sqlType);
                increEntity.getPkSurplusList().add(partitionPK);
                increEntity.getMessageSurplusList().add(messageStr);

                logger.warn("surplus agent size:{}", 0);
                break;
            case 1:
                increEntity.setAgentId(surplusCurrentAgentList.get(0));
                increEntity.setSocketChannel(NettySocketChannelHolder.get(increEntity.getAgentId()));
                if (increEntity.getSocketChannel() == null) {

                    increEntity.getSqlTypeList().add(sqlType);
                    increEntity.getPkSurplusList().add(partitionPK);
                    increEntity.getMessageSurplusList().add(messageStr);

                    logger.warn("surplus agent size:{},but not found.", 1);
                    break;
                } else {
                    sendDMLMessage(increEntity, sqlType, partitionPK, messageStr, type);
                    break;
                }
            default:
                increEntity.setAgentIndex(PartitionUtil.partition(partitionPK, surplusCurrentAgentList.size()));
                increEntity.setAgentId(surplusCurrentAgentList.get(increEntity.getAgentIndex()));
                increEntity.setSocketChannel(NettySocketChannelHolder.get(increEntity.getAgentId()));

                if (increEntity.getSocketChannel() == null) {
                    //移除当前失效的
                    increEntity.getCurrentAgentList().remove(increEntity.getAgentId());
                    //迭代
                    againGetChannelToSend(increEntity, sqlType, partitionPK, messageStr, type, increEntity.getCurrentAgentList());
                    break;
                } else {
                    sendDMLMessage(increEntity, sqlType, partitionPK, messageStr, type);
                    break;
                }
        }
    }


    /**
     * 发送DML消息
     *
     * @param partitionPK
     * @param messageStr
     * @param messageType
     */
    private void sendDMLMessage(MySQLIncrementSourceEntity increEntity, String sqlType, String partitionPK, String messageStr, String messageType) {
        // 拼接agentid到消息里
        messageStr = messageStr.replaceFirst("\\{", "{\"agentId\":" + increEntity.getAgentId().split(":")[0] + ",");

        /*测试
        String testMessage = "{\"agentId\":1736,\"jobId\":746,\"routeId\":129,\"srcDbTableName\":\"wmsb02.wms_do_items\",\"targetDbTableName\":\"dw.wms_do_items\",\"ddlRuleColumn\":\"warehouseno.b02\",\"type\":\"update\",\"colindex\":\"{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32}\",\"key\":\"746_mysql-bin.002812_333258669_201.wmsb02.wms_do_items_101870238\",\"colvalue\":\"(101870238,'B02','Z310009938119',1,1100000612,1700005353,'LF200g黑芝麻汤圆  速冻',1.0000,1.0000,0.0000,'件','B02NORMAL',null,null,0.2300,864.0000,null,null,'0',null,'03002','03002',null,null,null,null,'系统管理员','admin','2019-01-23 09:59:48.0',null,'admin','2019-01-23 09:59:48.0',null)\"}";
        increEntity.setSendMessageResult(
                mcpNettyServer.sendMsg(
                        MessageBuilder.commonMessage(
                                increEntity.getAgentId(),
                                MessageType.MessageTypeBase.PUSH_DATA,
                                testMessage),
                        increEntity.getSocketChannel()
                )
        );*/

        // 过滤表
        if (!messageStr.contains("wms_po_box_code") && !messageStr.contains("sys_sequencenumber")) {
            // 屏蔽redis中间存储
            // 先添加消息
            // if(MySQLBinLogUtil.addMessage(increEntity.getMessageQueue(), messageStr)){
            increEntity.setSendMessageResult(
                    mcpNettyServer.sendMsg(
                            MessageBuilder.commonMessage(
                                    increEntity.getAgentId(),
                                    MessageType.MessageTypeBase.PUSH_DATA,
                                    messageStr),
                            increEntity.getSocketChannel()
                    ));
            // 打印
            // logger.info(increEntity.getAgentId() + " : " + partitionPK + " : " + messageStr);
            // logger.info(increEntity.getAgentId() + " : " + partitionPK);

            if ("SendMessageSucessful".equals(increEntity.getSendMessageResult())) {
                if ("surplus".equals(messageType)) {
                    increEntity.getSqlTypeList().remove(0);
                    increEntity.getPkSurplusList().remove(0);
                    increEntity.getMessageSurplusList().remove(0);
                }
            } else {
                // 失败就抛错异常 存放
                logger.error(increEntity.getSendMessageResult());

                if ("normal".equals(messageType)) {
                    increEntity.getSqlTypeList().add(sqlType);
                    increEntity.getPkSurplusList().add(partitionPK.toString());
                    increEntity.getMessageSurplusList().add(increEntity.getMessage().toString());
                    logger.info("SurplusList : " + partitionPK + " : " + messageStr);
                }

                // 屏蔽redis中间存储
                // 再回撤消息
                // MySQLBinLogUtil.removeMessage(increEntity.getMessageQueue(), messageStr);

            }

       /* }else{
            logger.error("message can't insert redis. error message:{}",messageStr);
        }*/

        }
    }


    /**
     * 发送消息到所有指定的agent
     *
     * @param sqlType
     * @param partitionPK
     * @param messageStr
     * @param messageType
     */
    private void sendMessageToAllAgent(MySQLIncrementSourceEntity increEntity, String sqlType, String partitionPK, String messageStr, String messageType) {
        try {
            increEntity.getSendResultList().clear();

            for (String id : increEntity.getAssignAgentList()) {
                if (NettySocketChannelHolder.get(id) != null) {
                    // 拼接agentid到消息里
                    increEntity.setTmpMessage(messageStr);
                    increEntity.setTmpMessage(increEntity.getTmpMessage().replaceFirst("\\{", "{\"agentId\":" + id + ","));
                    increEntity.setSendMessageResult(mcpNettyServer.sendMsg(
                            MessageBuilder.commonMessage(
                                    id,
                                    MessageType.MessageTypeBase.PUSH_DATA,
                                    increEntity.getTmpMessage()),
                            NettySocketChannelHolder.get(id))
                    );

                    /**
                     * delete:
                     *  发送指定的所有agent
                     * alter:
                     *  发送指定的所有agent
                     *  假如发送成功，那么PhoenixTargetRunnerImpl.singleJSONParse方法的alter更新 保证列名称是最新
                     *  假如发送失败，那么PhoenixTargetRunnerImpl.singleJSONParse方法的insert/update更新 保证列名称是最新
                     */
                    if (!"SendMessageSucessful".equals(increEntity.getSendMessageResult())) {
                        increEntity.getSendResultList().add(false);
                        // 失败就抛错异常
                        logger.error(increEntity.getSendMessageResult());
                    } else {
                        //成功
                        increEntity.getSendResultList().add(true);

                        // 屏蔽redis中间存储
                        // MySQLBinLogUtil.addMessage(increEntity.getMessageQueue(), increEntity.getTmpMessage());
                    }
                } else {
                    increEntity.getSendResultList().add(false);
                    logger.warn("Agent[{}] cann't get channel.", id);
                }
            }

            // 没有成功的则记录
            if (!increEntity.getSendResultList().contains(true)) {
                // 只有normal正常发送 没有任何一个成功的 才添加到残留
                if ("normal".equals(messageType)) {
                    // 无论是delete 还是 alter 都存放在 Surplus
                    increEntity.getSqlTypeList().add(sqlType);
                    increEntity.getPkSurplusList().add(partitionPK.toString());
                    increEntity.getMessageSurplusList().add(increEntity.getMessage().toString());
                    logger.info("SurplusList : " + partitionPK.toString() + " : " + increEntity.getMessage().toString());
                }
            } else {
                // 只有surplus发送 只要有一个成功的 就从残留去除
                if ("surplus".equals(messageType)) {
                    increEntity.getSqlTypeList().remove(0);
                    increEntity.getPkSurplusList().remove(0);
                    increEntity.getMessageSurplusList().remove(0);
                }

                // 无论是normal 还是 surplus发送alter成功后 都处理以下
                if ("alter".equals(sqlType)) {
                    // 1.然后phoenix jdbc执行
                    increEntity.setMessageJson(JSON.parseObject(messageStr, Feature.OrderedField));

                    // 执行phoenix alter SQL
                    for (String sql : increEntity.getMessageJson().getString("targetAlterSql").split(";")) {
                        phoenixUtil.upsertDeleteAlterSql(sql);
                    }

                    // 2.mcp元数据库 更新
                    Connection conn = DriverManager.getConnection("jdbc:mysql://" + increEntity.getHost() + ":" + increEntity.getPort() + "/", increEntity.getUserName(), DecryptUtil.getInstance().decrypt(increEntity.getUserPassword()));
                    dataSourceSyncService.loadTablePreColumns(conn, increEntity.getSourceId(),
                            increEntity.getMessageJson().getString("srcDbTableName").split("\\.")[0],
                            increEntity.getMessageJson().getString("srcDbTableName").split("\\.")[1]);

                    // 3.mcp记录alter操作   job_qz_alter
                    jobDefinitionServiceImpl.qzAlter(increEntity.getJobId(), increEntity.getRouteId(),
                            increEntity.getMessageJson().getString("srcDbTableName").split("\\.")[0],
                            increEntity.getMessageJson().getString("srcDbTableName").split("\\.")[1],
                            increEntity.getMessageJson().getString("targetDbTableName").split("\\.")[0],
                            increEntity.getMessageJson().getString("targetDbTableName").split("\\.")[1],
                            increEntity.getMessageJson().getString("srcAlterSql"), increEntity.getMessageJson().getString("targetAlterSql")
                            , "", "");
                }
            }
        } catch (Exception ex) {
            logger.error(ex.toString());
        }
    }


}
