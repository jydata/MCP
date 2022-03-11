package com.jiuye.mcp.netty.server;


import com.jiuye.mcp.netty.util.ColumnPK;
import com.jiuye.mcp.netty.util.NettySocketChannelHolder;
import com.jiuye.mcp.protobuf.Message;
import com.jiuye.mcp.protobuf.MessageType;
import com.jiuye.mcp.server.runner.impl.MySQLFullSourceRunnerImpl;
import com.jiuye.mcp.server.runner.impl.MySQLIncrementSourceRunnerImpl;
import com.jiuye.mcp.utils.MessageBuilder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 处理客户端返回的数据
 *
 * @author jepson
 */
@Service("mcpNettyServerHandler")
@ChannelHandler.Sharable
public class MCPNettyServerHandler extends ChannelInboundHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(MCPNettyServerHandler.class);

    /**
     * 空闲次数
     */
    private int idle_count = 1;

    @Autowired
    private ColumnPK columnPK;

    private boolean putStatus;

    @Autowired
    private MySQLFullSourceRunnerImpl mySQLFullSourceRunnerImpl;
    private MySQLIncrementSourceRunnerImpl mySQLIncrementSourceRunnerImpl;

    /**
     * 建立连接时，发送一条消息
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("连接的客户端地址{}", ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
 /*       if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (IdleState.READER_IDLE.equals(event.state())) { // 如果读通道处于空闲状态，说明没有接收到心跳命令
                log.info("已经5秒没有接收到客户端的信息了");
                *//*if (idle_count > 1) {
                    log.info("\n关闭这个不活跃的channel!");
                    ctx.channel().close();

                }*//*

            }else if (IdleState.WRITER_IDLE.equals(event.state())) {//写


            } else if (IdleState.ALL_IDLE.equals(event.state())) {//全部

            }
        } */
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            // 如果是protobuf类型的数据
            if (msg instanceof Message.MessageBase) {
                Message.MessageBase msgBase = (Message.MessageBase) msg;

                if (msgBase.getType().equals(MessageType.MessageTypeBase.AUTH)) {
                    // 保存客户端与 Channel 之间的关系
                    // putStatus = NettySocketChannelHolder.put(msgBase.getAgentId()+":"+((NioSocketChannel) ctx.channel()).remoteAddress().getPort(), (NioSocketChannel) ctx.channel());
                    putStatus = NettySocketChannelHolder.put(msgBase.getAgentId(), (NioSocketChannel) ctx.channel());

                    if (putStatus) {
                        logger.info("Server端接收到 Agent[" + msgBase.getAgentId() + ":" + ((NioSocketChannel) ctx.channel()).remoteAddress().getPort() + "]的AUTH认证信息， channel:" + (NioSocketChannel) ctx.channel());
                    } else {
                        //已存在   服务端拒绝客户端连接
                        ctx.writeAndFlush(
                                MessageBuilder.commonMessage(
                                        msgBase.getAgentId(),
                                        MessageType.MessageTypeBase.SERVER_REJECT_AGENT_CONNECTION,
                                        "SERVER REJECT AGENT CONNECTION"));
                        logger.info("Server拒绝 Agent[" + msgBase.getAgentId() + "]连接! \n已经存在该AgentId! 请重新设置，保证唯一!");
                    }
                } else if (msgBase.getType().equals(MessageType.MessageTypeBase.COLUMN_PK)) {
                    // 接收到列的PK信息
                    columnPK.stringConvertMap(((Message.MessageBase) msg).getData());
                    logger.info("Server端接收到 Agent[" + msgBase.getAgentId() + ":" + ((NioSocketChannel) ctx.channel()).remoteAddress().getPort() + "]的COLUMN_PK表主键信息！");
                } else if (msgBase.getType().equals(MessageType.MessageTypeBase.PING)) {
                    // log.info("第" + count + "次，Server端接收到 Agent[" +  msgBase.getAgentId()+":"+((NioSocketChannel) ctx.channel()).remoteAddress().getPort() + "]的PING心跳信息! ");
                    // 回应
                    ctx.writeAndFlush(
                            MessageBuilder.commonMessage(
                                    msgBase.getAgentId(),
                                    MessageType.MessageTypeBase.PONG,
                                    "pong"));
                }
            } else {
                logger.info("未知数据!" + msg);
                return;
            }
        } catch (Exception e) {
            logger.error("Server netty read channel data from agent error.", e);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 移除channel
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettySocketChannelHolder.remove((NioSocketChannel) ctx.channel());
        logger.error("AgentId:{} Connection is closed.", ((NioSocketChannel) ctx.channel()).remoteAddress().getAddress() + ":" + ((NioSocketChannel) ctx.channel()).remoteAddress().getPort());
    }
}
