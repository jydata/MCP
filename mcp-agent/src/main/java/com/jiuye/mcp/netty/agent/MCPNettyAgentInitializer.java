package com.jiuye.mcp.netty.agent;

import com.jiuye.mcp.protobuf.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Statement;

/**
 * @author jepson
 */
@Component
public class MCPNettyAgentInitializer extends ChannelInitializer<SocketChannel> {


    @Autowired
    private MCPNettyAgentHandler mcpNettyAgentHandler;


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                //入参说明: 读超时时间、写超时时间、所有类型的超时时间、时间格式
                .addLast(new IdleStateHandler(3, 0, 0))

                // google Protobuf 解码和编码，应和服务端一致
                .addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufDecoder(Message.MessageBase.getDefaultInstance()))
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                .addLast(new ProtobufEncoder())

                //业务逻辑实现类
                .addLast("mcpNettyAgentHandler", mcpNettyAgentHandler)
        ;
    }
}
