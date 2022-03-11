package com.jiuye.mcp.agent.runner.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * @author jepson
 * @date 2019/1/14 1:09 PM
 */
public class PhoenixTargetRunnerImplTest {

    private final static Logger logger = LoggerFactory.getLogger(PhoenixTargetRunnerImplTest.class);

    static PhoenixTargetRunnerImpl phoenixTargetRunnerImpl = new PhoenixTargetRunnerImpl();

    static ArrayList txt2List(File file) {
        ArrayList<String> result = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
                if (!s.startsWith("--")) {
                    result.add(s);
                }
            }
            br.close();
        } catch (Exception e) {
            logger.error("Read txt to List error.", e);
        }
        return result;
    }


    public static void main(String[] args) {

        File fullTime = new File("mcp-server/src/main/resources/full.time");
        ArrayList<String> fullTimeList = txt2List(fullTime);//我的txt文本存放目录，根据自己的路径修改即可
        for (String fullTimeStr : fullTimeList) {
            String beginTime = fullTimeStr.split(",")[0];
            String endTime = fullTimeStr.split(",")[1];
            System.out.println(beginTime);
            System.out.println(endTime);
        }

        // System.out.println(0%100);

  /*      //创建配置
        Config config = new Config();
        config.setCodec(new org.redisson.client.codec.StringCodec());
        config.useSingleServer().setAddress("redis://192.168.17.38:6379");

        //config.setPassword("password")//设置密码
        config.useSingleServer().setConnectionPoolSize(500);//设置对于master节点的连接池中连接数最大为500
        config.useSingleServer().setIdleConnectionTimeout(10000);//如果当前连接池里的连接数量超过了最小空闲连接数，而同时有连接空闲时间超过了该数值，那么这些连接将会自动被关闭，并从连接池里去掉。时间单位是毫秒。
        config.useSingleServer().setConnectTimeout(30000);//同任何节点建立连接时的等待超时。时间单位是毫秒。
        config.useSingleServer().setTimeout(30000);//等待节点回复命令的时间。该时间从命令发送成功时开始计时。
        config.useSingleServer().setPingTimeout(30000);
        config.useSingleServer().setReconnectionTimeout(30000);//当与某个节点的连接断开时，等待与其重新建立连接的时间间隔。时间单位是毫秒。

        //创建客户端(发现创建RedissonClient非常耗时，基本在2秒-4秒左右)
        RedissonClient redissonClient = Redisson.create(config);



        RListMultimap<String, String> monitorRecordMap =  redissonClient.getListMultimap("monitorRecord");
        monitorRecordMap.get("2019-01-23 13:20:00");*/



        /*//6.获取queue
        RQueue<String>  messageQueue = redissonClient.getQueue("mcp_job_message_queue");
        RQueue<String>  removeFailMessageQueue = redissonClient.getQueue("mcp_job_poslist_removefail");
        */


        String message1 = "{\"RequestName\":\"RequestOrdersStation\",\"DelveryCode\":\"NFCM\",\"Sign\":\"ebd84decdbbeb7340e83362fd9c5e24e\",\"TimeStamp\":\"1546933098283\",\"Content\":{\"GetProvice\":\"广东省\",\"GetCity\":\"广州市\",\"GetCounty\":\"天河区\",\"GetAddress\":\"五山街道五山路华南理工大学南秀村38栋902\"}}";
        message1.replaceAll(",|\'|\"|\t|\n|\r|\r\n|\\\\", " ");

        JSON.parseObject(message1, Feature.OrderedField);
        //phoenixTargetRunnerImpl.singleJSONParse(JSON.parseObject(message1, Feature.OrderedField),null,null);

/*        if(messageQueue.contains(JSON.parseObject(message1, Feature.OrderedField).toString())){
            System.out.println("1");
        }
        */

  /*     Random rand = new Random();
       for(int i=0;i<10000;i++){
           System.out.println(rand.nextInt(3));
       }*/

    }

}
