package com.jiuye.mcp.netty.util;

import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.jiuye.mcp.server.util.PartitionUtil.murmur2;
import static com.jiuye.mcp.server.util.PartitionUtil.toPositive;

/**
 * Sever和Agent的Channel维护
 * @author jepson
 */
public class NettySocketChannelHolder {

    /**
     * concurrentHashmap以解决多线程冲突
     */
    public static final Map<String, NioSocketChannel> CHANNELMAP = new ConcurrentHashMap<>(64);

    /**
     * 存放 agentid 和 channel
     *
     * @param id
     * @param socketChannel
     */
    public static boolean put(String id, NioSocketChannel socketChannel) {


        if(CHANNELMAP.containsValue(socketChannel)){
            return false;
        } else{

            CHANNELMAP.put(id, socketChannel);
            return true;
        }

       /* //判断id是否存在，保证唯一性
        if (CHANNELMAP.containsKey(id)) {
            return false;
        } else {
            CHANNELMAP.put(id, socketChannel);
            return true;
        }*/

    }

    /**
     * 获取 CHANNELMAP
     *
     * @return
     */
    public static Map<String, NioSocketChannel> getChannelMap() {
        return CHANNELMAP;
    }


    /**
     * 根据agentID 获取对应channel
     *
     * @param id
     * @return
     */
    public static NioSocketChannel get(String id) {
        //判断是否存在
        if (CHANNELMAP.containsKey(id)) {
            return CHANNELMAP.get(id);
        } else {
            return null;
        }

    }

    /**
     * 移除
     *
     * @param nioSocketChannel
     */
    public static void remove(NioSocketChannel nioSocketChannel) {
        CHANNELMAP.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == nioSocketChannel)
                .forEach(entry -> CHANNELMAP.remove(entry.getKey()));
    }

    /**
     * 根据下标取 agent
     *
     * @param index
     * @return
     */
    public static String getAgentIdByIndex(int index) {

        int count = 0;
        String agentId="";
        for (String id : CHANNELMAP.keySet()) {

            if (count == index) {
                agentId = id;
            }
            count++;
        }
        return agentId;

    }


    /**
     * 获取所有通道
     *
     * @return
     */
    public static List<NioSocketChannel> getChannels() {

        List<NioSocketChannel> c = new ArrayList<NioSocketChannel>();
        for (String id : CHANNELMAP.keySet()) {
            c.add(CHANNELMAP.get(id));
        }
        return c;


    }

    /**
     * 获取所有通道ID
     *
     * @return
     */
    public static ArrayList<String> getAgentIds() {
        ArrayList<String> c = new ArrayList<String>();
        for (String id : CHANNELMAP.keySet()) {
            c.add(id);
        }
        return c;
    }


    /**
     * 根据channel取 agent id
     * 假如存在就返回实际id,反之999999999
     *
     * @param c
     * @return
     */
    public static String getAgentIdByChannel(NioSocketChannel c) {
        String agentId = "999999999";
        for (String id : CHANNELMAP.keySet()) {
            if (CHANNELMAP.get(id) == c) {
                agentId = id;
                break;
            }
        }
        return agentId;
    }


}
