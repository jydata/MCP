package com.jiuye.mcp.netty.agent;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * netty agent
 *
 * @author jepson
 */
@Service("mcpAgent")
public class MCPNettyAgent {

    private final static Logger logger = LoggerFactory.getLogger(MCPNettyAgent.class);

    @Value("${mcp.netty.server.host}")
    private String mcpServerHost;

    @Value("${mcp.netty.server.port}")
    private int mcpServerPort;

    @Value("${mcp.netty.agent.port}")
    private int mcpAgentPort;

    @Autowired
    private MCPNettyAgentInitializer mcpNettyAgentInitializer;

    private ExecutorService executor;

    /**
     * 通过nio方式来接收连接和处理连接
     */
    private EventLoopGroup group = new NioEventLoopGroup();

    /**
     * Netty创建全部都是实现自AbstractBootstrap。 客户端的是Bootstrap，服务端的则是 ServerBootstrap。
     **/
    public void run() {
        doConnect(new Bootstrap(), group, 1);
    }

    /**
     * 重连
     */
    public void doConnect(Bootstrap bootstrap, EventLoopGroup eventLoopGroup, int poolSize) {
        ChannelFuture f = null;
        try {
            if (bootstrap != null) {
                bootstrap.group(eventLoopGroup)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(mcpNettyAgentInitializer);

                for (int i = 0; i < poolSize; i++) {
                    f = bootstrap.connect(mcpServerHost, mcpServerPort)
                            .addListener((ChannelFuture futureListener) -> {
                                if (!futureListener.isSuccess()) {
                                    logger.info("Disconnect from the server! Ready to try reconnecting after 10s!");
                                    eventLoopGroup.schedule(
                                            () -> doConnect(new Bootstrap(), eventLoopGroup, 1), 10, TimeUnit.SECONDS
                                    );
                                }
                            });
                }

                // 监听客户端关闭监听
                //f.channel().closeFuture().sync();
                //f.channel().closeFuture();
                logger.info("MCP Agent started successfully using multithreading size :{}", poolSize);
            }
        } catch (Exception ex) {
            logger.error("MCPNettyAgent connect server error={}", ex);
        } finally {
            //不优雅关闭，主要应对server挂，agent端自动连接
            //eventLoopGroup.shutdownGracefully();
        }
    }


}
