package com.jiuye.mcp.netty.server;


import com.jiuye.mcp.protobuf.Message;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author jepson
 */
@Service("mcpServer")
public class MCPNettyServer {

    private final static Logger logger = LoggerFactory.getLogger(MCPNettyServer.class);

    /**
     * private static EventLoopGroup boss = new NioEventLoopGroup(1, new DefaultThreadFactory("mcp-server-boss", true));
     * private static EventLoopGroup work = new NioEventLoopGroup(4, new DefaultThreadFactory("mcp-server-work", true));
     */
    private static EventLoopGroup boss = new NioEventLoopGroup();
    private static EventLoopGroup work = new NioEventLoopGroup();
    private static ServerBootstrap b = new ServerBootstrap();

    @Value("${mcp.netty.server.port}")
    private int mcpServerPort;


    @Autowired
    private MCPNettyServerInitializer mcpNettyServerInitializer;

    public void run() {
        try {
            b.group(boss, work);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(mcpNettyServerInitializer);
            b.childOption(ChannelOption.SO_KEEPALIVE, true);

            // 服务器绑定端口监听
            ChannelFuture f = b.bind(mcpServerPort).sync();
            f.channel().closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    // 关闭EventLoopGroup，释放掉所有资源包括创建的线程
                    work.shutdownGracefully();
                    boss.shutdownGracefully();
                    logger.info(channelFuture.channel().toString() + " 链路关闭!");
                }
            });

            logger.info("MCP Server started successfully,port: {}", mcpServerPort);

            // 监听服务器关闭监听
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("Server netty run error.", e);
        }
    }


    /**
     * 发送消息
     *
     * @param message
     */
    public String sendMsg(Message.MessageBase message, NioSocketChannel socketChannel) {
        try {
            ChannelFuture future = socketChannel.writeAndFlush(message);
      /*      future.addListener(

                    (ChannelFutureListener) channelFuture ->
                            log.info("Netty Server发送消息成功={}", message.toString())
            );*/
            return "SendMessageSucessful";
        } catch (Exception ex) {
            return "SendMessageFail: " + ex.toString();
        }
    }
}
