package com.jiuye.mcp.netty.agent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.jiuye.mcp.MCPAgent;
import com.jiuye.mcp.agent.dao.meta.MetadataInfoMapper;
import com.jiuye.mcp.agent.model.SourceEntity;
import com.jiuye.mcp.agent.model.TargetEntity;
import com.jiuye.mcp.agent.runner.impl.PhoenixTargetRunnerImpl;
import com.jiuye.mcp.agent.service.meta.impl.McpRouteConnServiceImpl;
import com.jiuye.mcp.agent.service.meta.impl.MetaDataColumnServiceImpl;
import com.jiuye.mcp.agent.service.sys.impl.SysAgentsServiceImpl;
import com.jiuye.mcp.datasource.PhoenixDataSourceV2;
import com.jiuye.mcp.netty.util.ColumnPK;
import com.jiuye.mcp.protobuf.Message;
import com.jiuye.mcp.protobuf.MessageType;
import com.jiuye.mcp.utils.DateUtil;
import com.jiuye.mcp.utils.MessageBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.apache.phoenix.jdbc.PhoenixConnection;
import org.redisson.api.RListMultimap;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * mcp agent
 *
 * @author jepson
 */
@Service("mcpNettyAgentHandler")
@ChannelHandler.Sharable
public class MCPNettyAgentHandler extends ChannelInboundHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(MCPNettyAgentHandler.class);

    @Autowired
    private MCPNettyAgent mcpNettyAgent;

    @Autowired
    private MetadataInfoMapper metadataInfoMapper;

    @Autowired
    private PhoenixDataSourceV2 phoenixDataSource;
    public Connection conn;

    @Autowired
    private MetaDataColumnServiceImpl metaDataColumnService;
    //private ConcurrentHashMap<String, ConcurrentHashMap<Integer, String>> dbTableColName;
    private ConcurrentHashMap<String, ConcurrentHashMap<Integer, String>> dbTableColPK;

    @Autowired
    private McpRouteConnServiceImpl mcpRouteConnService;
    private ConcurrentHashMap<Long, SourceEntity> routeSourceJDBC;
    private ConcurrentHashMap<Long, TargetEntity> routeTargetDataSourceName;

    /**
     * agent id
     */
    public String mcpNettyAgentId;

    /**
     * auth pong
     */
    private Message.MessageBase heartAuth;
    private Message.MessageBase heartPing;

    @Autowired
    private ColumnPK columnPK;
    private Message.MessageBase columnPKMessage;

    @Autowired
    private PhoenixTargetRunnerImpl phoenixTargetRunnerImpl;

    public Long jobId;
    public Long routeId;
    private String key;

    private String datasourceName;
    private TargetEntity targetEntity;
    private JSONObject messageJson;

    @Autowired
    private RedissonClient redissonClient;
    private RQueue<String> messageQueue;
    private RQueue<String> removeFailMessageQueue;

    public RListMultimap<String, String> monitorRecordMap;

    private Boolean surplusMessage = false;
    private ChannelHandlerContext surplusCtx;
    private JSONObject surplusMessageJson = null;

    private Long receiveMessageCount = 0L;

    @Autowired
    private SysAgentsServiceImpl sysAgentsServiceImpl;

    @Value("${mcp.agent.name}")
    private String mcpAgentName;

    @Value("${mcp.netty.agent.port}")
    private int mcpAgentPort;

    /**
     * 建立连接时
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        try {
            monitorRecordMap = redissonClient.getListMultimap("monitorRecord");

            // 1.db table index coulmn pk映射
            metaDataColumnService.getColumnInfo(metadataInfoMapper.getMySQLColumnAll());
            // dbTableColName = metaDataColumnService.getDbTableColName();
            dbTableColPK = metaDataColumnService.getDbTableColPK();

            // 2.路由 源端 终端 映射
            mcpRouteConnService.getRouteConnInfo(metadataInfoMapper.getMcpRouteConnAll());
            routeSourceJDBC = mcpRouteConnService.getRouteSourceJDBC();
            routeTargetDataSourceName = mcpRouteConnService.getRouteTargetDataSourceName();

            // 3.启动时就开始初始化phoenix conn,而不是等第一条数据过来再去触发
            conn = phoenixDataSource.phoenixJdbcConn();
            phoenixTargetRunnerImpl.setCommitSize(((PhoenixConnection) conn).getMutateBatchSize());

            // 4.取的agent id ,构建 auth  ping ，发送
            //mcpNettyAgentId = SpringBeanFactory.getBean("mcpNettyAgentId", String.class);
            mcpNettyAgentId = MCPAgent.agentId;

           /* heartAuth = MessageBuilder.commonMessage(
                    mcpNettyAgentId,
                    MessageType.MessageTypeBase.AUTH,
                    "Agent[" + mcpNettyAgentId + ":" + ((NioSocketChannel) ctx.channel()).localAddress().getPort() + "] : connect to the server for the first time for auth."
            );
            heartPing = MessageBuilder.commonMessage(
                    mcpNettyAgentId,
                    MessageType.MessageTypeBase.PING,
                    "Agent[" + mcpNettyAgentId + ":" + ((NioSocketChannel) ctx.channel()).localAddress().getPort() + "] : this is ping."
            );
*/
            heartAuth = MessageBuilder.commonMessage(
                    mcpNettyAgentId,
                    MessageType.MessageTypeBase.AUTH,
                    "Agent[" + mcpNettyAgentId + "] : connect to the server for the first time for auth."
            );
            heartPing = MessageBuilder.commonMessage(
                    mcpNettyAgentId,
                    MessageType.MessageTypeBase.PING,
                    "Agent[" + mcpNettyAgentId + "] : this is ping."
            );

            // 5.列PK map 转行为string ，封装成message
            columnPKMessage = MessageBuilder.commonMessage(
                    mcpNettyAgentId,
                    MessageType.MessageTypeBase.COLUMN_PK,
                    columnPK.mapConvertString(dbTableColPK)
            );


            //屏蔽agent启动去redis消费数据
   /*         //6.获取queue
            messageQueue = redissonClient.getQueue("mcp_job_message_queue");
            removeFailMessageQueue = redissonClient.getQueue("mcp_job_poslist_removefail");

            if (messageQueue != null && messageQueue.size() > 0) {
                surplusMessage = true;
                surplusCtx = ctx;
            }

            //7.另外线程消费 残留
            *//*
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(() -> {
                cronScheduleMessageSurplus();
            });*//*

            //7.先阻塞消费redis的剩余的
            log.info("Start consumer surplus message.");
            cronScheduleMessageSurplus();*/

            //8.发送认证 和 列PK 信息
            ctx.writeAndFlush(heartAuth);
            ctx.writeAndFlush(columnPKMessage);
            ctx.fireChannelActive();
            //log.info("Agent[" + mcpNettyAgentId + ":" + ((NioSocketChannel) ctx.channel()).localAddress().getPort() + "] : connect to the server for the first time for auth.");
            logger.info("Agent[" + mcpNettyAgentId + "] : connect to the server for the first time for auth.");


            // 9.注册agent
            sysAgentsServiceImpl.saveAgentWithRunning(
                    Long.valueOf(mcpNettyAgentId),
                    mcpAgentName,
                    mcpAgentPort,
                    MCPAgent.hostname,
                    DateUtil.getCurrentTime()
            );

            super.channelActive(ctx);

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("channelActive: " + ex);
        }
    }


    /**
     * 关闭连接时
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("The connection with mcpNettyServer has been closed.");

        final EventLoop eventLoop = ctx.channel().eventLoop();
        mcpNettyAgent.doConnect(new Bootstrap(), eventLoop, 1);
        super.channelInactive(ctx);
    }

    /**
     * 心跳请求处理 每3秒发送一次心跳请求;
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object obj) throws Exception {
        if (obj instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) obj;
            if (IdleState.READER_IDLE.equals(event.state())) {
                // 如果读通道处于空闲状态,就发送心跳命令
                // logger.info("MCP agent[{}] send ping to server.", mcpNettyAgentId + ":" + ((NioSocketChannel) ctx.channel()).localAddress().getPort());
                // 当读取通道处于空闲状态，刷新Connection(Commit)
                phoenixTargetRunnerImpl.flushConn(conn);
                ctx.channel().writeAndFlush(heartPing);
            } else if (IdleState.WRITER_IDLE.equals(event.state())) {
                // 写
            } else if (IdleState.ALL_IDLE.equals(event.state())) {
                // 全部
            }
        } else {
            super.userEventTriggered(ctx, obj);
        }
    }

    /**
     * 业务逻辑处理
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 如果不是protobuf类型的数据
        if (!(msg instanceof Message.MessageBase)) {
            logger.info("Unknown data={}", msg);
            return;
        }

        // 得到protobuf的数据
        Message.MessageBase msgBase;
        try {
            // 得到protobuf的数据
            msgBase = (Message.MessageBase) msg;

            if (msgBase.getType().equals(MessageType.MessageTypeBase.PONG)) {
                // logger.info("MCP agent[{}] receive the server {}.\n", mcpNettyAgentId + ":" + ((NioSocketChannel) ctx.channel()).localAddress().getPort(), msgBase.getData());

            } else if (msgBase.getType().equals(MessageType.MessageTypeBase.SERVER_REJECT_AGENT_CONNECTION)) {
                logger.info("Check cluster agent id is to be unique.");
                // 关闭
                ctx.close();
                // agent 关闭
                System.exit(0);
            } else if (msgBase.getType().equals(MessageType.MessageTypeBase.PUSH_DATA)) {
                //logger.info(msgBase.getData());
                /**
                 * TODO 根据消息来的routeId-->源端和终端 ，
                 * 根据源端，找到JDBC链接信息，以防列名称信息更新
                 * 根据终端类型 选择对应的phoenixTargetRunnerImpl 、cassandraTargetRunnerImpl等等
                 */
                messageJson = JSON.parseObject(msgBase.getData(), Feature.OrderedField);

                // 发送
                sendToTarget(ctx, messageJson, "normal");
                receiveMessageCount++;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("channelRead: " + ex + "\n" + messageJson.toString());
        } finally {
            ReferenceCountUtil.release(msg);
            //ctx.fireChannelRead(msg);
        }
    }


    /**
     * 发送消息至终端
     *
     * @param ctx
     * @param messageJson
     */
    public void sendToTarget(ChannelHandlerContext ctx, JSONObject messageJson, String messageType) {
        try {
            jobId = messageJson.getLong("jobId");
            routeId = messageJson.getLong("routeId");
            if (messageJson.containsKey("key")) {
                key = messageJson.getString("key");
            } else {
                // 全量数据同步 json不包含key元素
                key = null;
            }

            // 2.路由 源端 终端 映射
            targetEntity = routeTargetDataSourceName.get(routeId);
            if (targetEntity == null) {
                // 重新加载路由等信息(新增路由)
                mcpRouteConnService.getRouteConnInfo(metadataInfoMapper.getMcpRouteConnAll());
                routeSourceJDBC = mcpRouteConnService.getRouteSourceJDBC();
                routeTargetDataSourceName = mcpRouteConnService.getRouteTargetDataSourceName();
                targetEntity = routeTargetDataSourceName.get(routeId);
                logger.warn("The configured target DataSource name can't be found, again update!");
            }

            if (targetEntity != null) {
                datasourceName = targetEntity.getDatasourceName();

                // 重新启动连接
                if (receiveMessageCount != 0 && receiveMessageCount % 1000000 == 0) {
                    if (conn != null) {
                        conn.commit();
                    }
                    conn = phoenixDataSource.phoenixJdbcConn();
                    phoenixTargetRunnerImpl.setCommitSize(((PhoenixConnection) conn).getMutateBatchSize());
                    logger.info("Phoenix jdbc reconnect!");
                }

                switch (datasourceName.toLowerCase()) {
                    case "phoenix":
                        phoenixTargetRunnerImpl.toTarget(conn, ctx, jobId, redissonClient, key, messageJson, messageQueue, removeFailMessageQueue, messageType);
                        break;

                    default:
                        phoenixTargetRunnerImpl.toTarget(conn, ctx, jobId, redissonClient, key, messageJson, messageQueue, removeFailMessageQueue, messageType);
                        break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("sendToTarget: " + ex + "\n" + messageJson.toString());
        }
    }

    /**
     * 获取数据完成
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        /* super.channelReadComplete(ctx);*/
        ctx.flush();
    }

    /**
     * 捕捉到异常
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (conn != null) {
            conn.commit();
            // conn.close();
        }

        cause.printStackTrace();
        ctx.close();
    }


    public void cronScheduleMessageSurplus() {
        try {
            // 第一次启动时做
            if (surplusMessage) {
                // agent恢复后消费所属自己的message
                messageQueue.parallelStream().forEach(e -> {
                    // 解析
                    surplusMessageJson = JSON.parseObject(e, Feature.OrderedField);
                    if (surplusMessageJson.getString("agentId").equals(mcpNettyAgentId)) {
                        sendToTarget(surplusCtx, surplusMessageJson, "surplus");
                        receiveMessageCount++;
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("cronScheduleMessageSurplus: " + ex + "\n" + surplusMessageJson.toString());
        }
    }

}
