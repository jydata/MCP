package com.jiuye.mcp.agent.runner.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.jiuye.mcp.agent.model.SourceEntity;
import com.jiuye.mcp.agent.runner.ITargetRunner;
import com.jiuye.mcp.agent.service.meta.impl.McpRouteConnServiceImpl;
import com.jiuye.mcp.agent.service.meta.impl.MetaDataColumnServiceImpl;
import com.jiuye.mcp.netty.agent.MCPNettyAgentHandler;
import com.jiuye.mcp.netty.util.ColumnPK;
import com.jiuye.mcp.protobuf.Message;
import com.jiuye.mcp.protobuf.MessageType;
import com.jiuye.mcp.utils.MessageBuilder;
import com.jiuye.mcp.utils.MySQLBinLogUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.phoenix.jdbc.PhoenixConnection;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author jepson
 * @date 2018/9/28 1:05 PM
 */
@Service("phoenixTargetRunnerImpl")
@ChannelHandler.Sharable
public class PhoenixTargetRunnerImpl implements ITargetRunner {

    private final static Logger logger = LoggerFactory.getLogger(PhoenixTargetRunnerImpl.class.getName());

    @Autowired
    private ColumnPK columnPK;
    @Autowired
    private MetaDataColumnServiceImpl metaDataColumnService;
    @Autowired
    private McpRouteConnServiceImpl mcpRouteConnService;
    @Autowired
    private MCPNettyAgentHandler mcpNettyAgentHandler;

    private final String MIGRATIONDBA = "migration_dba";
    /**
     * 主键消息
     */
    private Message.MessageBase columnPKMessage;

    /**
     * 消息记录数和主体
     */
    private ConcurrentHashMap<Long, Integer> monitorMessageRecord = new ConcurrentHashMap<Long, Integer>();
    private ConcurrentHashMap<Long, String> monitorMessageMain = new ConcurrentHashMap<Long, String>();

    /**
     * 初始记录数
     */
    private Integer record = 0;

    /**
     * sql的次数
     */
    private int batchSize = 0;

    /**
     * 多少次sql提交
     */
    //@Value("${phoenix.batch.size}")
    private int commitSize;

    /**
     * 格式化时间
     */
    private FastDateFormat timeFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    /**
     * 当前时间
     */
    private String currentTime;

    /**
     * agent的计数器：累计当前agent处理的dba归档数据。
     */
    private static final AtomicLong atomicLong = new AtomicLong(0);
    /**
     * 计数器达到一定数量打印一次
     */
    private static final int atomicLongInfoSize = 10000;
    /**
     * SQL提交HBase，同时计数
     *
     * @param conn                   phoenix
     * @param ctx                    netty
     * @param jobId
     * @param redissonClient
     * @param key
     * @param messageJson
     * @param messageQueue
     * @param removeFailMessageQueue
     * @throws Exception
     */
    @Override
    public void toTarget(Connection conn, ChannelHandlerContext ctx, Long jobId,
                         RedissonClient redissonClient,
                         String key, JSONObject messageJson, RQueue<String> messageQueue, RQueue<String> removeFailMessageQueue, String messageType) throws Exception {
        String parseSQL = "";
//        PhoenixConnection phoenixConn = null;
        try {
            String type = messageJson.getString("type");
            parseSQL = singleJSONParse(messageJson, type, ctx);

            //执行
            if (parseSQL.equals(MIGRATIONDBA)) {
                // 数据归档操作的信息记录，此处暂时剔除，仅作为调试使用。dba的一次大的归档操作记录2亿条*2的日志还是挺大的，有 atomicLong 累加器做归档数量的记录。
                //Long routeId = messageJson.getLong("routeId")
                //String srcDbTableName = messageJson.getString("srcDbTableName")
                //String targetDbTableName = messageJson.getString("targetDbTableName")
                //logger.info("routeId:{}, srcDbTableName:{}, targetDbTableName:{}, migration_dba is not null", routeId, srcDbTableName, targetDbTableName)
                return;
            } else if (StringUtils.isNotEmpty(parseSQL)) {
                PhoenixConnection phoenixConn = (PhoenixConnection) conn;
                Statement st = phoenixConn.createStatement();
                st.executeUpdate(parseSQL);
//                PreparedStatement stmt = conn.prepareStatement(parseSQL);
//                stmt.execute();
            } else {
                logger.info(messageJson.getString("targetAlterSql"));
                // alert sql在server端已执行，直接返回，避免commit报NullPointException
                return;
            }

           /* //消息从messageQueue移除 所有agent更新pos到一个key
            if (!"fullsync".equals(type)) {
                MySQLBinLogUtil.removePos(redissonClient, messageQueue, removeFailMessageQueue, key, messageJson.toString(), jobId,messageType);
            }*/

            //所有agent更新pos到对应的key
            if (!"fullsync".equals(type)) {
                MySQLBinLogUtil.updatePos(redissonClient, key, mcpNettyAgentHandler.mcpNettyAgentId, jobId, messageType);
            }

            // 计数
            record = monitorMessageRecord.get(mcpNettyAgentHandler.jobId);
            if (record == null) {
                monitorMessageRecord.put(mcpNettyAgentHandler.jobId, 1);
                monitorMessageMain.put(mcpNettyAgentHandler.jobId,
                        String.format("{\"jobid\":%s,\"routeid\":%s,\"agentid\":%s}",
                                mcpNettyAgentHandler.jobId, mcpNettyAgentHandler.routeId, mcpNettyAgentHandler.mcpNettyAgentId)
                );
            } else {
                record++;
                monitorMessageRecord.put(mcpNettyAgentHandler.jobId, record);
            }

            // 批量提交
            batchSize ++;
            if (batchSize % commitSize == 0) {
                batchSize = 0;
                conn.commit();

                /* 当messageQueue量大 该方法耗时大 故先注释
                if (!type.equals("fullsync")) {
                    MySQLBinLogUtil.removePosFail(messageQueue, removeFailMessageQueue);
                }
                */
                logger.info("Phoenix Batch size[{}] commit success.", commitSize);
            }
        } catch (Exception ex) {
            // 原始JSON 解析SQL 异常  打印
            logger.error("Batch commit success and error:\n{}\n{}\n{}", messageJson, parseSQL, ex);

            conn.commit();
            //conn.close();
        }
    }

    /**
     * 刷新 Connection
     *
     * @param conn
     */
    public void flushConn(Connection conn) {
        if (conn == null) {
            return;
        }
        if (batchSize == 0) {
            return;
        }
        try {
            conn.commit();
        } catch (SQLException e) {
            logger.error("[SQLException]Phoenix commit error when flush Connection: {}",e);
        }
        logger.info("Phoenix Batch size[{}] commit success.", batchSize);
        // After Commit success. reset batchSize
        batchSize = 0;
    }

    /**
     * 单个完整的消息JSON解析为Phoenix SQL
     *
     * @param messageJson
     * @param ctx
     * @return
     */
    public String singleJSONParse(JSONObject messageJson, String type, ChannelHandlerContext ctx) {
        String resultSQL = "";
        String[] ddlRuleColumnArr;
        String unionPKColumnName = "";
        String unionPKColumnValue = "";

        StringBuffer colName = new StringBuffer();
        StringBuffer deleteSql = new StringBuffer();
        StringBuffer insertUpdateSql = new StringBuffer();
        ConcurrentHashMap<String, ConcurrentHashMap<Integer, String>> singleDBTableColPK = new ConcurrentHashMap<String, ConcurrentHashMap<Integer, String>>();

        try {
            // 解析json数据 提取
            Long routeId = messageJson.getLong("routeId");
            String srcDbTableName = messageJson.getString("srcDbTableName");
            String targetDbTableName = messageJson.getString("targetDbTableName");
            String ddlRuleColumn = messageJson.getString("ddlRuleColumn");

            String srcDbName = srcDbTableName.split("\\.")[0];
            String srcTableName = srcDbTableName.split("\\.")[1];

            // 根据消息路由id 获取source
            SourceEntity sourceEntity = mcpRouteConnService.getRouteSourceJDBC().get(routeId);
            Long sourceId = sourceEntity.getSourceId();

            String srcIdDbTableKey = sourceId + "." + srcDbName + "." + srcTableName;
            // alter 语法
            if ("alter".equals(type)) {
                //TODO 假如alter语句频繁较多，这块也会频繁去查询更新 影响性能，但是一般在业务低谷，维护时间维护就行
                //更新最新的列名称
                metaDataColumnService.updateColumnWithTable(sourceEntity.getIp(),
                        Integer.valueOf(sourceEntity.getPort()),
                        sourceEntity.getUsername(),
                        sourceEntity.getPassword(),
                        srcDbName, srcTableName, srcIdDbTableKey);

                resultSQL = "";
            } else {
                //拼接字段名称
                String[] colIndexArr = messageJson.getString("colindex").substring(1, messageJson.getString("colindex").length() - 1).replaceAll(" ", "").split(",");
                //获取该srcId.db.table的列list信息
                ConcurrentHashMap<Integer, String> colNameMap = metaDataColumnService.getDbTableColName().get(srcIdDbTableKey);
                //dml语法
                if ("insert".equals(type) || "update".equals(type) || "fullsync".equals(type)) {
                    //TODO PHOENIX UPSERT语法

                    //判断colIndex 和 colNameMap 数量是否相等  colNameMap == null
                    //TODO binlog数据列 == 维护的列数，有风险，如rename字段修改
                    if (null == colNameMap || colIndexArr.length - colNameMap.size() != 0) {
                        logger.warn("Column index array length not equal column name map size. ");
                        //更新最新的列名称
                        metaDataColumnService.updateColumnWithTable(sourceEntity.getIp(),
                                Integer.valueOf(sourceEntity.getPort()),
                                sourceEntity.getUsername(),
                                sourceEntity.getPassword(),
                                srcDbName, srcTableName, srcIdDbTableKey);

                        //TODO 当前keymap 转成string 发送给server端 更新最新
                        if (null == colNameMap) {
                            singleDBTableColPK.clear();
                            singleDBTableColPK.put(srcIdDbTableKey, metaDataColumnService.getDbTableColPK().get(srcIdDbTableKey));

                            //列PK map 转行为string ，封装成message
                           /* columnPKMessage = MessageBuilder.commonMessage(
                                    SpringBeanFactory.getBean("mcpNettyAgentId", String.class),
                                    MessageType.MessageTypeBase.COLUMN_PK,
                                    columnPK.mapConvertString(singleDBTableColPK)

                            );*/

                            columnPKMessage = MessageBuilder.commonMessage(
                                    mcpNettyAgentHandler.mcpNettyAgentId,
                                    MessageType.MessageTypeBase.COLUMN_PK,
                                    columnPK.mapConvertString(singleDBTableColPK)

                            );

                            ctx.writeAndFlush(columnPKMessage);
                        }

                        //再次获取该db.table的列list信息
                        colNameMap = metaDataColumnService.getDbTableColName().get(srcIdDbTableKey);
                    }

                    //列名称
                    colName.setLength(0);
                    colName.append("(");
                    for (int i = 0; i < colIndexArr.length; i++) {
                        colName.append(colNameMap.get(Integer.valueOf(colIndexArr[i]))).append(",");
                    }

                    if (ddlRuleColumn != null) {
                        //联合主键的新增列名称
                        ddlRuleColumnArr = ddlRuleColumn.split("\\.");
                        unionPKColumnName = ddlRuleColumnArr[0];
                        unionPKColumnValue = ddlRuleColumnArr[1];
                        //最后位置 添加列 联合主键
                        colName.append(unionPKColumnName);
                    } else {
                        //删除最后1个标点符号,
                        colName.deleteCharAt(colName.length() - 1);
                    }
                    colName.append(")");

                    //取字段值,拼接upsert sql
                    String colValueJson = messageJson.getString("colvalue");
                    String colValue = getValueFromJson(colNameMap, colValueJson, colIndexArr.length);
                    insertUpdateSql.setLength(0);
                    if (ddlRuleColumn != null) {
                        insertUpdateSql.append("UPSERT INTO ")
                                .append(targetDbTableName)
                                .append(colName)
                                .append(" values").append(colValue.substring(0, colValue.length() - 1) + ",\'" + unionPKColumnValue + "\')");
                    } else {
                        insertUpdateSql.append("UPSERT INTO ")
                                .append(targetDbTableName)
                                .append(colName)
                                .append(" values").append(colValue);
                    }

                    resultSQL = insertUpdateSql.toString();

                    if(colValue.equalsIgnoreCase(MIGRATIONDBA)){
                        resultSQL = MIGRATIONDBA;
                        counterMigrationDba();
                    }
                } else if ("delete".equals(type)) {
                    //TODO PHOENIX DELETE语法
                    // 1.取出 主键的pos,columnNma
                    ConcurrentHashMap<String, ConcurrentHashMap<Integer, String>> dbTableColPK = metaDataColumnService.getDbTableColPK();
                    ConcurrentHashMap<Integer, String> colPKMap = dbTableColPK.get(srcIdDbTableKey);

                    // 2.取出列值 数组
                    String colValueJson = messageJson.getString("colvalue");
                    JSONObject jsonParser = JSON.parseObject(colValueJson, Feature.OrderedField);

                    // 3.拼接delete SQL
                    // 必然加where  因为同步表必须有主键或联合主键
                    deleteSql.append("DELETE FROM ")
                            .append(targetDbTableName).append(" WHERE ");
                    for (Integer p : colPKMap.keySet()) {
                        deleteSql.append(colPKMap.get(p))
                                .append("=")
                                .append(getJson(jsonParser, String.valueOf(p)))
                                .append(" and ");
                    }
                    // 删除最后一个 and
                    resultSQL = deleteSql.toString().substring(0, deleteSql.toString().length() - 5);

                    // rule3 追加: 联合主键的新增列名称作为删除字段
                    if (ddlRuleColumn != null) {
                        ddlRuleColumnArr = ddlRuleColumn.split("\\.");
                        unionPKColumnName = ddlRuleColumnArr[0];
                        unionPKColumnValue = ddlRuleColumnArr[1];
                        resultSQL = resultSQL + " and " + unionPKColumnName + "=\'" + unionPKColumnValue + "\'";
                    }

                    // 如果属于dba归档删除，过滤
                    if (getValueFromJson(colNameMap, colValueJson, colIndexArr.length).equalsIgnoreCase(MIGRATIONDBA)) {
                        resultSQL = MIGRATIONDBA;
                        counterMigrationDba();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("singleJSONParse: " + ex + "\n" + messageJson.toString());
            resultSQL = "";
        }

        return resultSQL;
    }

    /**
     * 计数器
     */
    private void counterMigrationDba() {
        atomicLong.getAndIncrement();
        if (atomicLong.get() % atomicLongInfoSize == 0) {
            logger.info("migration_dba counts : " + atomicLong.get());
        }
    }

    private String getValueFromJson(ConcurrentHashMap<Integer, String> colNameMap, String columnValue, int columnIndexSize) {

        JSONObject jsonParser = JSON.parseObject(columnValue, Feature.OrderedField);
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        // logger.info("beign to get exitMarkDba.... ")
        boolean exitMarkDba = colNameMap.values().stream().filter(str -> str.equalsIgnoreCase(MIGRATIONDBA)).count() == 1;
        // logger.info("exitMarkDba: "+ exitMarkDba)
        for (int i = 0; i < columnIndexSize; i++) {
            String colJsonValue = getJson(jsonParser, String.valueOf(i));
            // dba 数据归档: 1.含有 MIGRATIONDBA 字段 ; 2. MIGRATIONDBA 的值非空

            // logger.info("colNameMap.get(String.valueOf(i+1)): "+ colNameMap.get(i))
            // logger.info("colNameMap.get(String.valueOf(i+1)).equalsIgnoreCase(MIGRATIONDBA) : "+ colNameMap.get(i).equalsIgnoreCase(MIGRATIONDBA))
            if (exitMarkDba && colNameMap.get(i).equalsIgnoreCase(MIGRATIONDBA) && "1".equals(colJsonValue)) {
                return MIGRATIONDBA;
            }
            builder.append(colJsonValue).append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append(")");
        // (v1,v2,v3,'v4')
        return builder.toString().replace("\"", "'");
    }

    private String getJson(JSONObject jsonParser, String colIndex) {
        String str;
        try {
            Object obj = jsonParser.get(colIndex);
            if(obj !=null && obj instanceof  String){
                str = "'" + obj.toString() + "'";
            }else{
                str = obj.toString();
            }
        } catch (NullPointerException e) {
            str = null;
        }
        return str;
    }


    /**
     * 从0秒开始 每隔3秒 调度一次
     *
     * @throws Exception
     */
    @Scheduled(cron = "0/3 * * * * ?")
    public void cronScheduleMessageBatchCommit() throws Exception {
        try {
            currentTime = timeFormat.format(new Date());
            for (Long id : monitorMessageRecord.keySet()) {
                //只有数据>0 这样时间是断层的
                if (monitorMessageRecord.get(id) != null && monitorMessageRecord.get(id) > 0) {
                    mcpNettyAgentHandler.monitorRecordMap.put(currentTime,
                            monitorMessageMain.get(id).replace("}", ",\"record\":" + monitorMessageRecord.get(id) + "}"));

                    //java.util.ConcurrentModificationException
                    monitorMessageRecord.put(id, 0);
                }
            }

            /*if (mcpNettyAgentHandler.conn != null) {
                mcpNettyAgentHandler.conn.commit();
                log.info("Monitor and Phoenix messages batch time[{}] commit success.", currentTime);
            } else {
                log.info("Monitor messages batch time[{}] commit success.", currentTime);
             }*/
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("cronScheduleMessageBatchCommit:" + ex);
        }
    }

    public void setCommitSize(int commitSize) {
        this.commitSize = commitSize;
    }
}
