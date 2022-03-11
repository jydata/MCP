package com.jiuye.mcp.utils;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import org.redisson.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author jepson
 */
public class MySQLBinLogUtil {
    private final static Logger log = LoggerFactory.getLogger(MySQLBinLogUtil.class);


    /**
     * 配置当前binlog位置  保障断点还原
     *
     * @param redissonClient
     * @param client
     * @param jobId
     * @param recentBinlog
     * @param firstBinlogFilename
     * @param firstPos
     */
    public static void configBinaryLogStatus(RedissonClient redissonClient, BinaryLogClient client, Long jobId, String recentBinlog, String firstBinlogFilename, Long firstPos) {

        RBucket<String> latestPos = redissonClient.getBucket("mcp_job_" + jobId + "_latestpos");

        if (latestPos.get() != null) {

            RQueue<String> messageQueue = redissonClient.getQueue("mcp_job_message_queue"); //有序的
            if (messageQueue.isExists()) { //存在

                Map<Double, String> binlogMap = new LinkedHashMap<Double, String>();

                messageQueue.parallelStream().forEach(e -> {
                    JSONObject jsonObject = JSON.parseObject(e); //解析
                    String[] posArr = jsonObject.getString("key").split("_");
                    if (posArr[0].equals(jobId.toString()) && !jsonObject.getString("type").equals("alter")) { //匹配jobId 且不是alter
                        //相同的key 会覆盖
                        binlogMap.put(Double.valueOf(posArr[1].replaceAll("mysql-bin.", "") + "." + posArr[2]), posArr[1] + "_" + posArr[2]);

                    }
                });

               /* for (String message : messageQueue.readAll()) {

                    JSONObject jsonObject = JSON.parseObject(message); //解析
                    String[] posArr = jsonObject.getString("key").split("_");
                    if (posArr[0].equals(jobId.toString()) && !jsonObject.getString("type").equals("alter")) { //匹配jobId 且不是alter
                        //相同的key 会覆盖
                        binlogMap.put(Double.valueOf(posArr[1].replaceAll("mysql-bin.", "") + "." + posArr[2]),posArr[1]+"_"+posArr[2]);

                    }
                }*/

                if (binlogMap.size() > 0) {

                    //取最小值
                    Set<Double> keySet = binlogMap.keySet();
                    Object[] keyObject = keySet.toArray();
                    Arrays.sort(keyObject);

                    //取出最小key对应的value
                    String binlogFileNameAndPos = binlogMap.get(keyObject[0]);
                    String[] binlogFileNameAndPosArr = binlogFileNameAndPos.split("_");

                    client.setBinlogFilename(binlogFileNameAndPosArr[0]);
                    client.setBinlogPosition(Long.valueOf(binlogFileNameAndPosArr[1]));
                    log.info("MySQL latest binlogfile: " + binlogFileNameAndPosArr[0] + " ,pos: " + Long.valueOf(binlogFileNameAndPosArr[1]) + ",from mcp_job_poslist key.");


                } else {//不存在 就直接读取latestpos
                    String posArr[] = latestPos.get().split("_");
                    client.setBinlogFilename(posArr[1]);
                    client.setBinlogPosition(Long.valueOf(posArr[2]));
                    log.info("MySQL latest binlogfile: " + posArr[1] + " ,pos: " + posArr[2] + ",from " + "mcp_job_" + jobId + "_latestpos key.");

                }

            } else { //不存在 就直接读取latestpos

                String posArr[] = latestPos.get().split("_");
                client.setBinlogFilename(posArr[1]);
                client.setBinlogPosition(Long.valueOf(posArr[2]));
                log.info("MySQL latest binlogfile: " + posArr[1] + " ,pos: " + posArr[2] + ",from " + "mcp_job_" + jobId + "_latestpos key.");

            }


        } else {

            if (recentBinlog.equals("1")) {
                log.info("MySQL binlogfile and pos is latest.");
            } else {
                client.setBinlogFilename(firstBinlogFilename);
                client.setBinlogPosition(firstPos);
                log.info("MySQL first binlogfile: " + firstBinlogFilename + " ,pos: " + firstPos);
            }


        }


    }


    /**
     * 添加消息
     *
     * @param messageQueue
     * @param message
     */
    public static boolean addMessage(RQueue<String> messageQueue, String message) {
        try {
            //按顺序追加插入
            return messageQueue.add(message);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("addMessage: " + ex);
            return false;
        }

    }

    /**
     * 回撤消息
     *
     * @param messageQueue
     * @param message
     */
    public static void removeMessage(RQueue<String> messageQueue, String message) {
        messageQueue.remove(message);
    }

    /**
     * 移除且更新最新的pos位置
     *
     * @param redissonClient
     * @param messageQueue
     * @param removeFailMessageQueue
     * @param pk
     * @param message
     * @param jobId
     */
    public static void removePos(RedissonClient redissonClient, RQueue<String> messageQueue, RQueue<String> removeFailMessageQueue, String pk, String message, Long jobId, String messageType) {

        //屏蔽redis中间存储
        /*
        //移除不成功
        if (!messageQueue.remove(message)) {
            removeFailMessageQueue.add(message);
            log.info("[{}] not contains!", pk);

        } else { //成功 确保再删除一次
            removeFailMessageQueue.remove(message);
        }*/

        //更新最新的
        if (messageType.equals("normal")) {
            RBucket<String> latestpos = redissonClient.getBucket("mcp_job_" + jobId + "_latestpos");
            latestpos.set(pk);
            // log.info("Remove and update pos: "+ binLogFilePosAndPKStr);

        }


    }


    /**
     * 定期移除 失败移除的message
     * TODO messageQueue量大时 remove操作耗时严重,故先skip
     *
     * @param messageQueue
     * @param removeFailMessageQueue
     */
    public static void removePosFail(RQueue<String> messageQueue, RQueue<String> removeFailMessageQueue) {

        removeFailMessageQueue
                .parallelStream()
                .forEach(e -> {
                    if (messageQueue.remove(e)) { //强有力删除保证
                        removeFailMessageQueue.remove(e);
                    }

                });

    }


    /**
     * 配置当前binlog位置  保障断点还原
     * 从维护的1个map多个agent，找最小的值
     *
     * @param redissonClient
     * @param client
     * @param jobId
     * @param recentBinlog
     * @param firstBinlogFilename
     * @param firstPos
     */
    public static void configBinaryLogStatusFromRMap(RedissonClient redissonClient, BinaryLogClient client, Long jobId, String recentBinlog, String firstBinlogFilename, Long firstPos, List<String> agentList) {

        RMap latestPosMap = redissonClient.getMap("mcp_job_" + jobId + "_latestpos");

        if (latestPosMap.isExists()) {//存在 必然数量>0，取出最小值

            Map<Double, String> binlogMap = new LinkedHashMap<Double, String>();
            String[] posArr = null;
            for (Object key : latestPosMap.keySet()) {
                posArr = latestPosMap.get(key).toString().split("_");
                log.info("redis key:{}, \tpasArr[1]:{}, \tposArr[2]:{}", key.toString(), posArr[1], posArr[2]);
                // check agent is exist
                if (agentList.contains(key.toString())) {
                    binlogMap.put(Double.valueOf(posArr[1].split("\\.")[1] + "." + posArr[2]), posArr[1] + "_" + posArr[2]);
                    log.info("add active agent:{}, \tpasArr[1]:{}, \tposArr[2]:{}", key.toString(), posArr[1], posArr[2]);
                }
            }

            //取最小值
            Set<Double> keySet = binlogMap.keySet();
            Object[] keyObject = keySet.toArray();
            Arrays.sort(keyObject);

            //取出最小key对应的value
            String binlogFileNameAndPos = binlogMap.get(keyObject[0]);
            String[] binlogFileNameAndPosArr = binlogFileNameAndPos.split("_");

            client.setBinlogFilename(binlogFileNameAndPosArr[0]);
            client.setBinlogPosition(Long.valueOf(binlogFileNameAndPosArr[1]));
            log.info("MySQL latest binlogfile: " + binlogFileNameAndPosArr[0] + " ,pos: " + Long.valueOf(binlogFileNameAndPosArr[1]) + ",from mcp_job_poslist key.");


        } else {//不存在

            //是否最新binlog
            if (recentBinlog.equals("1")) {
                log.info("MySQL binlogfile and pos is latest.");
            } else {//指定位置
                client.setBinlogFilename(firstBinlogFilename);
                client.setBinlogPosition(firstPos);
                log.info("MySQL first binlogfile: " + firstBinlogFilename + " ,pos: " + firstPos);
            }

        }

    }


    /**
     * 各个agent 记录自己的pos 相同的key,value会覆盖
     *
     * @param redissonClient
     * @param pk
     * @param agentId
     * @param jobId
     * @param messageType
     */
    public static void updatePos(RedissonClient redissonClient, String pk, String agentId, Long jobId, String messageType) {

        //更新最新的
        if (messageType.equals("normal")) {
            RMap latestposMap = redissonClient.getMap("mcp_job_" + jobId + "_latestpos");
            latestposMap.put(agentId, pk);
            //log.info("{} pos: {}",agentId,pk);
        }

    }


}
