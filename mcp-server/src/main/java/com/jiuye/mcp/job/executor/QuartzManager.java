package com.jiuye.mcp.job.executor;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.BinaryLogClient.EventListener;
import com.github.shyiko.mysql.binlog.BinaryLogClient.LifecycleListener;
import com.jiuye.mcp.context.config.PropertiesConfig;
import com.jiuye.mcp.job.jobtask.CommonJobEntry;
import com.jiuye.mcp.job.log.JobLogFileManager;
import com.jiuye.mcp.job.log.JobLogger;
import com.jiuye.mcp.netty.util.NettySocketChannelHolder;
import com.jiuye.mcp.server.dao.job.JobDefinitionMapper;
import com.jiuye.mcp.server.dao.job.JobProjectMapper;
import com.jiuye.mcp.server.dao.job.JobSchedulerLogMapper;
import com.jiuye.mcp.server.dao.meta.MetaDatarouteMapper;
import com.jiuye.mcp.server.dao.meta.MetaRulesMapper;
import com.jiuye.mcp.server.model.ValidateResult;
import com.jiuye.mcp.server.model.job.JobDefineEntity;
import com.jiuye.mcp.server.model.job.JobSchedulerLogEntity;
import com.jiuye.mcp.server.runner.impl.MySQLFullSourceRunnerImpl;
import com.jiuye.mcp.server.runner.impl.MySQLFullSourceSyncRunnerImpl;
import com.jiuye.mcp.server.runner.impl.MySQLIncrementSourceRunnerImpl;
import com.jiuye.mcp.server.service.meta.IDataSourceSyncService;
import com.jiuye.mcp.server.service.meta.IDdlService;
import com.jiuye.mcp.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 定时任务管理类
 *
 * @author kevin
 * @date 2018-10-24
 */
public class QuartzManager implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(QuartzManager.class.getName());

    /**
     * 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。
     */
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static Scheduler scheduler = null;

    public void setScheduler(Scheduler scheduler) {
        QuartzManager.scheduler = scheduler;
    }

    /**
     * log thread map
     */
    public static Map<Long, Thread> threadMap = new ConcurrentHashMap<>(64);
    /**
     * job runner map
     */
    public static Map<Long, BinaryLogClient> clientMap = new ConcurrentHashMap<>(64);
    /**
     * event listener map
     */
    public static Map<Long, EventListener> eventMap = new ConcurrentHashMap<>(64);
    /**
     * life listener map
     */
    public static Map<Long, LifecycleListener> lifeMap = new ConcurrentHashMap<>(64);
    /**
     * thread pool executor map
     */
    public static Map<Long, ExecutorService> threadPoolExecutorMap = new ConcurrentHashMap<>(64);

    /**
     * config properties
     */
    public static PropertiesConfig config;

    /**
     * dao
     */
    public static MetaDatarouteMapper metaDatarouteMapper;
    public static MetaRulesMapper metaRulesMapper;
    public static JobProjectMapper jobProjectMapper;
    public static JobDefinitionMapper jobDefinitionMapper;
    public static JobSchedulerLogMapper jobSchedulerLogMapper;

    public static IDataSourceSyncService dataSourceSyncService;
    public static IDdlService ddlService;
    /**
     * job
     */
    public static MySQLIncrementSourceRunnerImpl mysqlRunner;
    public static MySQLFullSourceRunnerImpl fullRunner;
    public static MySQLFullSourceSyncRunnerImpl fullSyncRunner;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        QuartzManager.config = applicationContext.getBean(PropertiesConfig.class);

        QuartzManager.metaDatarouteMapper = applicationContext.getBean(MetaDatarouteMapper.class);
        QuartzManager.metaRulesMapper = applicationContext.getBean(MetaRulesMapper.class);
        QuartzManager.jobProjectMapper = applicationContext.getBean(JobProjectMapper.class);
        QuartzManager.jobDefinitionMapper = applicationContext.getBean(JobDefinitionMapper.class);
        QuartzManager.jobSchedulerLogMapper = applicationContext.getBean(JobSchedulerLogMapper.class);

        QuartzManager.dataSourceSyncService = applicationContext.getBean(IDataSourceSyncService.class);
        QuartzManager.ddlService = applicationContext.getBean(IDdlService.class);

        QuartzManager.mysqlRunner = applicationContext.getBean(MySQLIncrementSourceRunnerImpl.class);
        QuartzManager.fullRunner = applicationContext.getBean(MySQLFullSourceRunnerImpl.class);
        QuartzManager.fullSyncRunner = applicationContext.getBean(MySQLFullSourceSyncRunnerImpl.class);
    }

    public void init() throws Exception {
        // 初始化logPath
        JobLogFileManager.initPath(config.getLogPath());
    }

    public void destroy() {
        JobTriggerPool.stop();

        // 清空threadMap
//		threadMap.clear();

        // 清空job runner map
        clientMap.clear();
        logger.info(">>>>>>destroy()<<<<<<");
    }

    /**
     * 使用StdSchedulerFactory调度工厂初始化调度器
     */
    public static void Init() {
        SchedulerFactory schedulerfactory = new StdSchedulerFactory();
        try {
            scheduler = schedulerfactory.getScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * check exists
     *
     * @param jobName
     * @param jobGroup
     * @return
     * @throws SchedulerException
     */
    public static boolean checkExists(String jobName, String jobGroup) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        return scheduler.checkExists(triggerKey);
    }

    /**
     * 添加作业
     */
    public static boolean addJob(Long jobId, String jobGroup, String cronExpression) throws SchedulerException {
        return addJob(String.valueOf(jobId), jobGroup, cronExpression);
    }

    /**
     * 添加作业
     * cron调度
     */
    public static boolean addJob(String jobName, String jobGroup, String cronExpression) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        JobKey jobKey = new JobKey(jobName, jobGroup);

        // check exists
        if (checkExists(jobName, jobGroup)) {
            logger.info(">>>>>>QuartzManager addJob fail, job already exist, jobName:{}, jobGroup:{}<<<<<<", jobName, jobGroup);
            return false;
        }

        // withMisfireHandlingInstructionDoNothing 不触发立即执行，等待下次调度；
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
        JobDetail jobDetail = JobBuilder.newJob(CommonJobEntry.class).withIdentity(jobKey).build();
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();
        Date date = scheduler.scheduleJob(jobDetail, cronTrigger);

        logger.info(">>>>>>QuartzManager addJob success, jobDetail:{}, cronTrigger:{}, date:{}<<<<<<", jobDetail, cronTrigger, date);
        return true;
    }

    /**
     * 只执行一次的job
     *
     * @param jobId
     * @param delaySecond 延迟时间：秒
     */
    public static boolean addExecuteOnceJob(Long jobId, String jobGroup, int delaySecond) throws SchedulerException {
        return addExecuteOnceJob(String.valueOf(jobId), jobGroup, delaySecond);
    }

    /**
     * 只执行一次的job
     *
     * @param jobName
     * @param delaySecond 延迟时间：秒
     */
    public static boolean addExecuteOnceJob(String jobName, String jobGroup, int delaySecond) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        if (checkExists(jobName, jobGroup)) {
            logger.info(">>>>>>QuartzManager addExecuteOnceJob fail, job already exist, jobGroup:{}, jobName:{}<<<<<<", jobGroup, jobName);
            return false;
        }

        // 指定时间开始调度
        if (delaySecond < 0) {
            delaySecond = 0;
        }

//		Date startTime = DateBuilder.nextGivenSecondDate(null, delaySecond);
        Date startTime = new Date();
        JobDetail job = JobBuilder.newJob(CommonJobEntry.class).withIdentity(jobName, jobGroup).build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .startAt(startTime)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withRepeatCount(0)
                        .withMisfireHandlingInstructionIgnoreMisfires())
                .build();

        Date ft = scheduler.scheduleJob(job, trigger);
        logger.info(">>>>>>Will run on : " + DateUtil.getFormatDate(ft, DateUtil.DF_YYYYMMDD_HHMMSS) + "<<<<<<");

        return true;
    }

    public static boolean recheduleJob(String jobName, String jobGroup, String cronExpression) throws SchedulerException {
        if (checkExists(jobName, jobGroup)) {
            logger.info(">>>>>>QuartzManager rescheduleJob fail, job not exists, JobGroup:{}, JobName:{}<<<<<<", jobGroup, jobName);
            return false;
        }

        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        CronTrigger oTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        if (null != oTrigger) {
            String oCron = oTrigger.getCronExpression();
            if (oCron.equals(cronExpression)) {
                return true;
            }

            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
            oTrigger = oTrigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();

            // recheduler
            scheduler.rescheduleJob(triggerKey, oTrigger);
        } else {
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
            CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();

            JobKey jobKey = new JobKey(jobName, jobGroup);
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);

            // Trigger fresh
            HashSet<Trigger> triggerSet = new HashSet<Trigger>();
            triggerSet.add(cronTrigger);

            scheduler.scheduleJob(jobDetail, triggerSet, true);
        }

        logger.info(">>>>>>recheduleJob success, JobGroup:{}, JobName:{}<<<<<<", jobGroup, jobName);
        return true;
    }

    /**
     * 修改作业
     */
    public static boolean modifyJobTime(String jobName, String jobGroup, String time) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);

        boolean flag = false;
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        String oldTime = trigger.getCronExpression();
        if (!oldTime.equalsIgnoreCase(time)) {
            // 先删除
            removeJob(jobName, jobGroup);
            // 后添加
            addJob(jobName, jobGroup, time);
            flag = true;
        }

        return flag;
    }

    /**
     * 删除Job
     *
     * @throws SchedulerException
     */
    public static boolean removeJob(Long jobId, String jobGroup) throws SchedulerException {
        return removeJob(String.valueOf(jobId), jobGroup);
    }

    /**
     * 删除Job
     *
     * @throws SchedulerException
     */
    public static boolean removeJob(String jobName, String jobGroup) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);

        boolean flag = false;
        if (checkExists(jobName, jobGroup)) {
            //通过jobId查询最新的logId
            String fileName = getLogPathName(Long.valueOf(jobName));
            JobLogFileManager.jobFileHolder.set(fileName);

            // 移除触发器
            flag = scheduler.unscheduleJob(triggerKey);
            logger.info(">>>>>>removeJob, triggerKey:{}, flag [{}]<<<<<<", triggerKey, flag);
            JobLogger.log(">>>>>>removeJob, triggerKey:{}, flag [{}]<<<<<<", triggerKey, flag);
        }

        return flag;
    }

    /**
     * 暂停Job
     *
     * @param jobId
     * @param jobGroup
     * @return
     * @throws SchedulerException
     */
    public static boolean pauseJob(Long jobId, String jobGroup) throws SchedulerException {
        return pauseJob(String.valueOf(jobId), jobGroup);
    }

    /**
     * 暂停Job
     *
     * @param jobName
     * @param jobGroup
     * @return
     * @throws SchedulerException
     */
    public static boolean pauseJob(String jobName, String jobGroup) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);

        boolean flag = false;
        if (checkExists(jobName, jobGroup)) {
            //通过jobId查询最新的logId
            String fileName = getLogPathName(Long.valueOf(jobName));
            JobLogFileManager.jobFileHolder.set(fileName);

            scheduler.pauseTrigger(triggerKey);
            flag = true;
            logger.info(">>>>>>pauseJob success, triggerKey:{}<<<<<<", triggerKey);
            JobLogger.log(">>>>>>pauseJob success, triggerKey:{}<<<<<<", triggerKey);
        }

        return flag;
    }

    /**
     * 重新启用Job
     *
     * @param jobId
     * @param jobGroup
     * @return
     * @throws SchedulerException
     */
    public static boolean resumeJob(Long jobId, String jobGroup) throws SchedulerException {
        return resumeJob(String.valueOf(jobId), jobGroup);
    }

    /**
     * 重新启用Job
     *
     * @param jobName
     * @param jobGroup
     * @return
     * @throws SchedulerException
     */
    public static boolean resumeJob(String jobName, String jobGroup) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);

        boolean flag = false;
        if (checkExists(jobName, jobGroup)) {
            //通过jobId查询最新的logId
            String fileName = getLogPathName(Long.valueOf(jobName));
            JobLogFileManager.jobFileHolder.set(fileName);

            scheduler.resumeTrigger(triggerKey);
            flag = true;
            logger.info(">>>>>>resumeJob success, triggerKey:{}<<<<<<", triggerKey);
            JobLogger.log(">>>>>>resumeJob success, triggerKey:{}<<<<<<", triggerKey);
        }

        return flag;
    }

    /**
     * 暂停增量Job
     *
     * @param jobId
     * @return
     * @throws SchedulerException
     */
    public static ValidateResult pauseIncrementJob(Long jobId) throws IOException {
        //通过jobId查询最新的logId
        String fileName = getLogPathName(jobId);
        JobLogFileManager.jobFileHolder.set(fileName);

        //获取增量runnerMap,得到对应的runner和jobId的关系
        BinaryLogClient sourceRunnerClient = clientMap.get(jobId);
        if (null == sourceRunnerClient) {
            return new ValidateResult(false, "BinaryLogClient not obtained!");
        }
        // 根据jobId查询作业信息
        JobLogger.log("Pause increment job, obtain binarylog client is success");

        // event unregister
        EventListener event = QuartzManager.eventMap.get(jobId);
        if (null == event) {
            return new ValidateResult(false, "EventListener not obtained, Please kill running job!");
        }
        // lefe listener unregister
        LifecycleListener lifeListener = lifeMap.get(jobId);
        if (null == lifeListener) {
            return new ValidateResult(false, "LifecycleListener not obtained, Please kill running job!");
        }

        sourceRunnerClient.unregisterEventListener(event);
        sourceRunnerClient.unregisterLifecycleListener(lifeListener);
        sourceRunnerClient.disconnect();//暂停增量job作业
        logger.info("++++++++++++increment pauseIncrementJob++++++++++++++");
        if (!sourceRunnerClient.isConnected()) {
            JobLogger.log("Pause increment job is success");
            logger.info(">>>>>>pauseIncrementJob success, triggerKey:{}<<<<<<", jobId);

            String binlogFilename = sourceRunnerClient.getBinlogFilename();
            long binlogPosition = sourceRunnerClient.getBinlogPosition();

            //更新job_define表中的binlog信息
            JobDefineEntity jobDefine = new JobDefineEntity();
            jobDefine.setJobId(jobId);
            jobDefine.setRecentBinlogName(binlogFilename);
            jobDefine.setRecentBinlogPosition(binlogPosition);
            int result = jobDefinitionMapper.updateRecentBinlog(jobDefine);
            if (result <= 0) {
                return new ValidateResult(false, "Update log information failed!");
            }

            return new ValidateResult(true, null);
        } else {
            //没有关闭成功处理
            JobLogger.log("Pause increment job is failed");
            return new ValidateResult(false, "Pause incremental job failed!");
        }
    }

    /**
     * 重新启用增量job
     *
     * @param jobId
     * @return
     * @throws SchedulerException
     */
    public static boolean resumeIncrementJob(Long jobId) throws IOException {
        String fileName = getLogPathName(jobId);
        JobLogFileManager.jobFileHolder.set(fileName);

        BinaryLogClient sourceRunnerClient = clientMap.get(jobId);
        if (sourceRunnerClient.getEventListeners().size() == 0) {
            EventListener event = eventMap.get(jobId);
            sourceRunnerClient.registerEventListener(event);
        }
        if (sourceRunnerClient.getLifecycleListeners().size() == 0) {
            LifecycleListener lifeListener = lifeMap.get(jobId);
            sourceRunnerClient.registerLifecycleListener(lifeListener);
        }
        executorService.submit(() -> {
            try {
                sourceRunnerClient.connect(20000);
            } catch (Exception e) {
                // TODO:  继续优化异常处理逻辑
                logger.error("Handling event exceptions，{}", e);
            }
        });

        JobLogger.log("Resume increment job success !");
        return true;

    }

    /**
     * 根据LogId kill job线程
     *
     * @param logId
     * @return
     * @throws SchedulerException
     */
    public static boolean killJob(Long jobId, Long logId) throws SchedulerException {
        boolean flag = false;
        if (threadMap.isEmpty()) {
            return flag;
        }

        if (threadMap.containsKey(logId)) {
            String fileName = getLogPathName(jobId);
            JobLogFileManager.jobFileHolder.set(fileName);

            Thread logThread = threadMap.get(logId);
            if (null != logThread) {
                while (!logThread.isInterrupted()) {
                    logger.info("LogId:{} is killed.", logId);
                    JobLogger.log("LogId:{} is killed.", logId);
                    logThread.interrupt();
                    try {
                        logThread.join();
                    } catch (InterruptedException e) {
                        logger.error(logId + "--InterruptedException", e);
                    }

                    flag = true;
                }
            }

            // remove thread map
            if (flag) {
                threadMap.remove(logId);
            }
        }

        return flag;
    }

    public static boolean removeFullJob(long jobId, long logId) {
        boolean threadFlag = false;
        boolean flag = false;
        // 1. Kill Thread
        Thread logThread = threadMap.get(logId);
        if (null != logThread) {
            while (!threadFlag && !logThread.isInterrupted()) {
                logThread.interrupt();
                logger.info("LogId:{} is killed.", logId);
                threadFlag = true;
            }
        }
        // remove thread map
        if (threadFlag) {
            threadMap.remove(logId);
        }

        // 2. shutdown executor pool
        if (threadFlag) {
            ExecutorService fullJobExe = QuartzManager.threadPoolExecutorMap.get(jobId);
            if (fullJobExe != null && !fullJobExe.isShutdown()) {
                fullJobExe.shutdownNow();
                flag = true;
            }
        }
        return flag;
    }

    public static boolean killIncrementJob(long jobId, long logId) throws SchedulerException, IOException {
        boolean killFlag = false;
        // kill job
        killFlag = killJob(jobId, logId);
        if (!killFlag) {
            return killFlag;
        }

        //获取增量runnerMap,得到对应的runner和jobId的关系
        if (clientMap.containsKey(jobId)) {
            BinaryLogClient sourceRunnerClient = clientMap.get(jobId);
            if (null != sourceRunnerClient) {
                // event unregister
                EventListener event = QuartzManager.eventMap.get(jobId);
                LifecycleListener lifeListener = lifeMap.get(jobId);
                if (null != event && null != lifeListener) {
                    sourceRunnerClient.unregisterEventListener(event);
                    sourceRunnerClient.unregisterLifecycleListener(lifeListener);
                    sourceRunnerClient.disconnect();//暂停增量job作业
                }
            }
        }

        return killFlag;
    }

    /**
     * 获取日志文件名称
     *
     * @param jobId
     * @return
     */
    public static String getLogPathName(Long jobId) {
        JobSchedulerLogEntity logEntity = jobSchedulerLogMapper.queryFreshLog(jobId);
        String fileName = "";
        if (null != logEntity) {
            fileName = JobLogFileManager.generateFileName(new Date(logEntity.getTriggerTime().getTime()), logEntity.getLogId());
        } else {
            fileName = JobLogFileManager.generateFileName(DateUtil.getCurrentTime(), jobId);
        }

        return fileName;
    }


    /**
     * 获取存活的agent集合
     * 循环10次，每次休息60秒，直到有返回值
     *
     * @param agents
     * @return
     */
    public static List<String> getActiveAgents(int loopNum, String agents) {
        if (StringUtils.isBlank(agents)) {
            return null;
        }

        List<String> list = null;
        try {
            for (int i = 1; i <= loopNum; i++) {
                list = getAgents(agents);
                if (null != list && list.size() > 0) {
                    break;
                } else {
                    logger.info("No active agent, Ready to try getting after 30s.");
                    // 先休息30s
                    Thread.sleep(30 * 1000);
                }
            }
        } catch (InterruptedException e) {
            logger.info("Get active agent error.", e);
        }

        return list;
    }

    /**
     * 根据agents, 获取存活的agent集合
     *
     * @param agents
     * @return
     */
    private static List<String> getAgents(String agents) {
        List<String> agentList = new ArrayList<>();
        String[] agentArr = null;
        if (StringUtils.isNotBlank(agents) && agents.contains(",")) {
            agentArr = agents.split(",");
        } else {
            agentArr = new String[]{agents};
        }

        for (String agent : agentArr) {
            if (NettySocketChannelHolder.get(agent) != null) {
                agentList.add(agent);
            }
        }

        return agentList;
    }
}
