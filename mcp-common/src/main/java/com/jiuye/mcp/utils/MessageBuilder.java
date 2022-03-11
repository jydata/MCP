package com.jiuye.mcp.utils;


import com.jiuye.mcp.protobuf.Message;
import com.jiuye.mcp.protobuf.MessageType;

/**
 * @author jepson
 */
public class MessageBuilder {

    //server to agent heart beat
     public static final Message.MessageBase HEART_PONG=  Message.MessageBase.newBuilder().setAgentId("999999999").setType(MessageType.MessageTypeBase.PONG).setData("pong").build();





    /**
     * 通用消息
     *
     * @param agentId
     * @param type
     * @param data
     * @return
     */
    public static Message.MessageBase commonMessage(String agentId, MessageType.MessageTypeBase type,String data){

        Message.MessageBase.Builder msg = Message.MessageBase.newBuilder();
        msg.setAgentId(agentId);
        msg.setType(type);
        msg.setData(data);
        return msg.build();
    }

}
