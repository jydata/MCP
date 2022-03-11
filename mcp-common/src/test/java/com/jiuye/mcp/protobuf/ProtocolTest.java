package com.jiuye.mcp.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * Created by jepson ON 2018/9/3 5:10 PM.
 */
public class ProtocolTest {

    public static void main(String[] args) throws InvalidProtocolBufferException {

        Message.MessageBase protocol = Message.MessageBase.newBuilder()
                .setAgentId("123456")
                .setType(MessageType.MessageTypeBase.AUTH)
                .setData("你好 mcp!")
                .build();
        byte[] encode = encode(protocol); //编码
        Message.MessageBase parseFrom = decode(encode); //解码

        //验证是否一致
        System.out.println(protocol.getData().equals(parseFrom.getData()));

    }



     /*
     * 编码
     * @param protocol
     * @return
     */

    public static byte[] encode(Message.MessageBase protocol){
        return protocol.toByteArray() ;
    }
     /*
     * 解码
     * @param bytes
     * @return
     * @throws InvalidProtocolBufferException
     */
    public static Message.MessageBase decode(byte[] bytes) throws InvalidProtocolBufferException {
        return Message.MessageBase.parseFrom(bytes);
    }

}
