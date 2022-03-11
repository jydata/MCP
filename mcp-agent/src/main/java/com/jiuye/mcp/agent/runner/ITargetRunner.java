package com.jiuye.mcp.agent.runner;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;

import java.sql.Connection;

/**
 * @author jepson
 * @date 2018/9/28 11:12 AM
 */
public interface ITargetRunner {

    /**
     * channelRead
     *
     * @throws Exception
     */
    void toTarget(
            Connection conn,
            ChannelHandlerContext ctx,
            Long jobId,
            RedissonClient redissonClient,
            String pk,
            JSONObject messageJson,
            RQueue<String> messageQueue,
            RQueue<String> removeFailMessageQueue, String messageType) throws Exception;


}


