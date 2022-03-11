package com.jiuye.mcp.server.service.home.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.jiuye.mcp.netty.util.NettySocketChannelHolder;
import com.jiuye.mcp.server.dao.home.HomePageMapper;
import com.jiuye.mcp.server.dao.home.HomeReportMapper;
import com.jiuye.mcp.server.dao.job.JobDefinitionMapper;
import com.jiuye.mcp.server.dao.sys.SysAgentMapper;
import com.jiuye.mcp.server.model.home.*;
import com.jiuye.mcp.server.model.job.JobDefineEntity;
import com.jiuye.mcp.server.model.meta.SysAgentsEntity;
import com.jiuye.mcp.server.service.home.IHomePageService;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.log4j.Logger;
import org.redisson.api.RList;
import org.redisson.api.RListMultimap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jiuye.mcp.utils.DateUtil;

import java.io.*;
import java.util.*;

/**
 *
 * @author zp
 * @date 2018/12/11 0011
 */
@Service
public class HomePageServiceImpl implements IHomePageService {

    private static final Logger logger = Logger.getLogger(HomePageServiceImpl.class.getName());

    @Autowired
    private HomePageMapper homePageMapper;
    @Autowired
    private HomeReportMapper homeReportMapper;
    @Autowired
    private JobDefinitionMapper jobDefinitionMapper;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    SysAgentMapper sysAgentMapper;

    @Override
    public List<HomeCommonEntity> queryJobs() {

        List<HomeCommonEntity> homeList = homePageMapper.queryJobs();

        List<String> statusList = new ArrayList<>(Arrays.asList("init", "running", "wait", "success", "fail"));
        List<HomeCommonEntity> list = new ArrayList<>();
        for(String status : statusList){
            HomeCommonEntity entity = new HomeCommonEntity();
            entity.setName(status);
            entity.setValue(0L);
            list.add(entity);
        }
        for(HomeCommonEntity entityTemp : list){
            for(HomeCommonEntity valEntity : homeList){
                if(entityTemp.getName().equals(valEntity.getName())){
                    entityTemp.setValue(valEntity.getValue());
                }
            }
        }
        return list;
    }

    @Override
    public List<HomeCommonEntity> queryTechDatas() {
        // 获取源端、终端、路由的数量
        List<HomeCommonEntity> list = homePageMapper.queryTechDatas();

        // 获取agents数量
        List<String> agentList = NettySocketChannelHolder.getAgentIds();

        HomeCommonEntity homeCommonEntity = new HomeCommonEntity();
        homeCommonEntity.setName("Agents");
        homeCommonEntity.setValue((long)agentList.size());
        list.add(homeCommonEntity);

        return list;
    }

    @Override
    public List<HomeFinenessStatisticEntity> queryTableCounts() {
        List<HomeFinenessStatisticEntity> list = homeReportMapper.queryFinenessStatis();

        return list;
    }

    /**
     * agentN
     * |- T
     * |- T-1
     * |- ...
     * 每台agent节点日志目录结构:
     * /var/log/mcp/agent/history/mcp-agent.yyyy-MM-dd.log
     *
     * @return
     */
    @Override
    public List<HomeSyncAgentErrorLogEntity> queryAgentErrorSqlCounts() {

        // todo 找logPath的常量 ？
        String logPath = "/var/log/mcp";

        Date nowDate = new Date();

        List<HomeSyncAgentErrorLogEntity> agentErrorLogEntityList = new ArrayList<>();
        List<SysAgentsEntity> sysAgentsEntities = sysAgentMapper.queryAgents();

        for (SysAgentsEntity sysAgent : sysAgentsEntities) {
            HomeSyncAgentErrorLogEntity agentErrorLogEntity = getErrorSqlCountByAgent(sysAgent.getHostname(), nowDate, logPath);
            agentErrorLogEntityList.add(agentErrorLogEntity);
        }

        return agentErrorLogEntityList;
    }

    private HomeSyncAgentErrorLogEntity getErrorSqlCountByAgent(String agentName, Date nowDate, String logPath) {
        // 如果无法ping通hostname，返回0，0，0
        if (pingHostName(agentName) < 1) {
            logger.info("Could not ping to host: " + agentName);
            return new HomeSyncAgentErrorLogEntity(agentName, 0, 0, 0);
        }
        Map<Integer, String> mapTime = new HashMap<Integer, String>();
        Map<Integer, Integer> mapCounts = new HashMap<Integer, Integer>();
        int constantSeven = 7;
        for (int i = 0; i < constantSeven; i++) {
            String time = DateUtil.getFormatDate(DateUtil.addDay(nowDate, -1 * i), DateUtil.DF_YYYY_MM_DD);
            mapTime.put(i, time);
        }
        for (int i = 0; i < constantSeven; i++) {
            int errorCounts = getErrorSqlCountsByDate(agentName, logPath, mapTime.get(i));
            mapCounts.put(i, errorCounts);
        }
        // 1.获取当天Error Sql Counts
        int todayErrorCounts = mapCounts.get(0);

        // 2.获取昨天Error Sql Counts
        int yesterErrorCounts = mapCounts.get(1);

        // 3.获取七天内Error Sql Counts
        int sevenDayErrorCounts = 0;
        for (int i : mapCounts.values()) {
            sevenDayErrorCounts = sevenDayErrorCounts + i;
        }
        return new HomeSyncAgentErrorLogEntity(agentName, todayErrorCounts, yesterErrorCounts, sevenDayErrorCounts);
    }

    /**
     * ping agent 查看是否能ping通，返回ping成功次数
     *
     * @param hostName
     * @return
     */
    private int pingHostName(String hostName) {
        String pingTimes = "5";
        int success = 0;
//        String[] cmds = {"/usr/bin/ping", "-c", pingTimes, hostName};
        String[] cmds = {"ping", "-c", pingTimes, hostName};
        try {

            Process pro = Runtime.getRuntime().exec(cmds);
            pro.waitFor();
            InputStream in = pro.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = read.readLine()) != null) {
                if (line.contains("icmp_seq") && line.contains("ttl=") && line.contains("time")) {
                    success++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Ping HostName Error ", e);
            success = 0;
        }
        return success;
    }

    /**
     * 远程ping每台agent节点的日志是否有异常信息，前提背景：
     * 1.日志格式/var/log/mcp/agent/history/mcp-agent.yyyy-MM-dd.log 严格要求
     * 2.异常的SQL会在agent端打印upsert语句，可以让service以"UPSERT INTO"的形式找到
     *
     * @param logPath 日志根目录
     * @param date    日期 yyyy-MM-dd
     * @return
     */
    private int getErrorSqlCountsByDate(String agentName, String logPath, String date) {
        int lineCount = 0;
        String filePath = logPath + "/agent/history/mcp-agent." + date + ".log";
        try {
            String[] cmds = {"/usr/bin/ssh", "-n", agentName, " cat " + filePath + " | grep 'UPSERT INTO'| wc -l "};
            Process pro = Runtime.getRuntime().exec(cmds);
            pro.waitFor();
            InputStream in = pro.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(in));
            String line;
            if ((line = read.readLine()) != null) {
                lineCount = Integer.valueOf(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("File Read IO Exception! FilePath:" + filePath, e);
            lineCount = 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error("File Read InterruptedException! FilePath:" + filePath, e);
            lineCount = 0;
        }
        return lineCount;
    }

    private long getEndTime(long currentTime) {
        long endTime = currentTime;
        int val = Long.valueOf(FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(currentTime).substring(17, 19)).intValue();
        int flag = (val + 3) % 3;
        //用于计算当前之前最近的取值时间
        switch (flag) {
            case 0:
                endTime = currentTime;
                break;
            case 1:
                endTime = currentTime - 1000;
                break;
            case 2:
                endTime = currentTime - 2000;
                break;
        }
        return endTime;
    }

    /**
     * 获取 从endTime时间开始，过去 mapSize * 3000 ms 时间端内所有时间集合
     *
     * @param endTime 结束时间
     * @return
     */
    private List<String> getValTimeList(long endTime) {
        int mapSize = 500;

        List<String> valTimeList = new ArrayList<>();
        FastDateFormat format = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
        String timeFormat;
        for (int i = 0; i < mapSize; i++) {
            timeFormat = format.format(endTime);
            valTimeList.add(timeFormat);
            endTime -= 3000;
        }
        return valTimeList;
    }

    /**
     * 获取所有job的 jobid:jobName 的映射关系
     *
     * @return
     */
    private Map<Long, String> getJobNameMap() {
        List<JobDefineEntity> jobIdList = jobDefinitionMapper.query(null);
        Map<Long, String> jobNameMap = new HashMap<>();
        if (jobIdList != null && jobIdList.size() > 0) {
            for (JobDefineEntity valJob : jobIdList) {
                jobNameMap.put(valJob.getJobId(), valJob.getJobName());
            }
        }
        return jobNameMap;
    }

    @Override
    public List<HomeSyncTimeEnity> querySyncJobData() {
        RListMultimap<String, String> map = redissonClient.getListMultimap("monitorRecord");

        // 2019-06-12 17:26:12 Testing on dev
        /*Calendar calendar = Calendar.getInstance();
        calendar.set(2019, 05, 12, 17, 26, 12);
        long currentTime = calendar.getTimeInMillis();*/
        long currentTime = System.currentTimeMillis();
        //截取时间格式当前时间的秒数
        long endTime = getEndTime(currentTime);

        //构建一个包含取值时间的List，用于后续map的取值
        List<String> valTimeList = getValTimeList(endTime);

        // 获取 jobId:jobName
        Map<Long, String> jobNameMap = getJobNameMap();

        List<HomeSyncTimeEnity> timeList = new ArrayList<>();
        HomeSyncRecordEntity entity;
        HomeSyncJobEntity jobEntityInfo;
        HomeSyncTimeEnity timeEnityInfo;

        for (String time : valTimeList) {
            Map<Long, Long> jobMap = new Hashtable<>();
            RList<String> valList = map.get(time);

            String subTime = time.substring(11, 19);
            //计算map中同一个key下，job对应的Record sum值
            for (String homeSyncRecord : valList) {
                entity = JSON.parseObject(homeSyncRecord, HomeSyncRecordEntity.class);
                long jobId = entity.getJobid();
                if (jobNameMap.containsKey(jobId)) {
                    jobMap.put(jobId, getSumRecord(jobMap, entity, jobId));
                }
            }

            List<HomeSyncJobEntity> jobList = new ArrayList<>();
            timeEnityInfo = new HomeSyncTimeEnity();
            timeEnityInfo.setTime(subTime);
            for (Map.Entry<Long, Long> mapEntry : jobMap.entrySet()) {
                jobEntityInfo = new HomeSyncJobEntity();
                Long jobId = mapEntry.getKey();
                jobEntityInfo.setJobId(jobId);
                jobEntityInfo.setJobName(jobNameMap.get(jobId));
                jobEntityInfo.setRecordSum(mapEntry.getValue());
                jobList.add(jobEntityInfo);
            }
            timeEnityInfo.setJobList(jobList);
            timeList.add(timeEnityInfo);
        }

        List<HomeSyncTimeEnity> list = Lists.reverse(timeList);

        return list;
    }

    private long getSumRecord(Map<Long, Long> map, HomeSyncRecordEntity entity, long objId) {
        long sumResult;
        if (map.containsKey(objId)) {
            long sum = map.get(objId);
            long record = entity.getRecord();
            sumResult = sum + record;
        } else {
            sumResult = entity.getRecord();
        }
        return sumResult;
    }

    @Override
    public List<HomeSyncTimeEnity> querySyncAgentData() {
        RListMultimap<String, String> map = redissonClient.getListMultimap("monitorRecord");
        List<SysAgentsEntity> sysAgentsEntities = sysAgentMapper.queryAgents();

        Map<Long, String> sysAgentsMap = new HashMap<>();
        for (SysAgentsEntity sysAgent : sysAgentsEntities) {
            sysAgent.getAgentId();
            sysAgent.getAgentName();
            sysAgentsMap.put(sysAgent.getAgentId(), sysAgent.getAgentName());
        }
        // 2019-06-12 17:26:12 Testing on dev
        /*Calendar calendar = Calendar.getInstance();
        calendar.set(2019, 05, 12, 17, 26, 12);
        long currentTime = calendar.getTimeInMillis();*/
        long currentTime = System.currentTimeMillis();

        long endTime = getEndTime(currentTime);

        List<String> valTimeList = getValTimeList(endTime);

        List<HomeSyncTimeEnity> timeList = new ArrayList<>();
        HomeSyncRecordEntity entity;
        HomeSyncAgentEntity agentEntityInfo;
        HomeSyncTimeEnity timeEnityInfo;

        for (String time : valTimeList) {
            Map<Long, Long> agentMap = new Hashtable<>();
            RList<String> valList = map.get(time);

            String subTime = time.substring(11, 19);
            //计算map中同一个key下，job对应的Record sum值
            for (String homeSyncRecord : valList) {
                entity = JSON.parseObject(homeSyncRecord, HomeSyncRecordEntity.class);
                long agentId = entity.getAgentid();
                agentMap.put(agentId, getSumRecord(agentMap, entity, agentId));
            }

            List<HomeSyncAgentEntity> agentList = new ArrayList<>();
            timeEnityInfo = new HomeSyncTimeEnity();
            timeEnityInfo.setTime(subTime);
            for (Map.Entry<Long, Long> mapEntry : agentMap.entrySet()) {
                agentEntityInfo = new HomeSyncAgentEntity();
                Long jobId = mapEntry.getKey();
                agentEntityInfo.setAgentId(jobId);
                agentEntityInfo.setAgentName(sysAgentsMap.get(jobId));
                agentEntityInfo.setRecordSum(mapEntry.getValue());
                agentList.add(agentEntityInfo);
            }
            timeEnityInfo.setAgentList(agentList);
            timeList.add(timeEnityInfo);
        }

        List<HomeSyncTimeEnity> list = Lists.reverse(timeList);

        return list;
    }

    @Override
    public List<HomeTableCountEntity> queryHotTables(String fineness) {
        List<HomeTableCountEntity> list = homePageMapper.queryHotTables(fineness);
        return list;
    }
}
