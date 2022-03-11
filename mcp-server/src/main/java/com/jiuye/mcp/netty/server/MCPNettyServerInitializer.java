package com.jiuye.mcp.netty.server;

import com.jiuye.mcp.protobuf.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import io.netty.channel.socket.SocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jepson
 */
@Component
public class MCPNettyServerInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private MCPNettyServerHandler mcpNettyServerHandler;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                //入参说明: 读超时时间、写超时时间、所有类型的超时时间、时间格式
                .addLast(new IdleStateHandler(0, 0, 0))

                // google Protobuf 编解码， 和客户端一致
                /**
                 ProtobufEncoder：用于对Probuf类型序列化。
                 ProtobufVarint32LengthFieldPrepender：用于在序列化的字节数组前加上一个简单的包头，只包含序列化的字节长度。
                 ProtobufVarint32FrameDecoder：用于decode前解决半包和粘包问题（利用包头中的包含数组长度来识别半包粘包）
                 ProtobufDecoder：反序列化指定的Probuf字节数组为protobuf类型
                 */
                .addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufDecoder(Message.MessageBase.getDefaultInstance()))
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                .addLast(new ProtobufEncoder())
                //业务逻辑实现类
                .addLast("mcpNettyServerHandler", mcpNettyServerHandler);
    }
}
