package com.jiuye.mcp.context;

import com.jiuye.mcp.job.executor.QuartzManager;
import com.jiuye.mcp.param.enums.DeleteFlagEnum;
import com.jiuye.mcp.param.enums.JobStatusEnum;
import com.jiuye.mcp.param.enums.JobTypeEnum;
import com.jiuye.mcp.server.model.job.JobDefineEntity;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kevin
 * @date 2019-07-26 15:03
 */
@Component
public class ApplicationStartedRunner implements ApplicationRunner{

    private final static Logger logger = LoggerFactory.getLogger(ApplicationStartedRunner.class);

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Mcp server is started");
        restoreRunningJob();
    }

    /**
     * restore jobs before server stops
     */
    private void restoreRunningJob() {
        List<String> status = new ArrayList<>();
        status.add(JobStatusEnum.RUNNING.getCode());
        status.add(JobStatusEnum.WAIT.getCode());
        List<JobDefineEntity> runningJobs = QuartzManager.jobDefinitionMapper.queryRestoreJob(DeleteFlagEnum.NO.getCode(), status);
        if (null == runningJobs || runningJobs.size() <= 0){
            return;
        }

        // update running job status to fail, then trigger job
        QuartzManager.jobDefinitionMapper.updateRunning2Fail();
        QuartzManager.jobSchedulerLogMapper.updateRunning2Fail();
        Map<Long, Integer> projectNumMap = new HashMap<>();
        try {
            for (JobDefineEntity entity : runningJobs) {
                String jobType = entity.getJobType();
                if ((jobType.equals(JobTypeEnum.METADATA.getCode()) && entity.getRouteId() == 0) ||
                        jobType.equals(JobTypeEnum.INCREMENT.getCode()) ||
                        jobType.equals(JobTypeEnum.FULL.getCode()) ||
                        jobType.equals(JobTypeEnum.DDL.getCode())) {
                    // restore job trigger
                    QuartzManager.addExecuteOnceJob(entity.getJobId(), entity.getGroupName(), 5);
                }
                // obtain fail num for project
                if (projectNumMap.containsKey(entity.getProjectId())) {
                    projectNumMap.put(entity.getProjectId(), projectNumMap.get(entity.getProjectId()) + 1);
                } else {
                    projectNumMap.put(entity.getProjectId(), 1);
                }
            }
        } catch (SchedulerException e) {
            logger.error("restoreRunningJob error:", e);
        }

        // batch update project's fail num
        for (Map.Entry<Long, Integer> entry : projectNumMap.entrySet()) {
            QuartzManager.jobProjectMapper.updateProjectFailNum(entry.getKey(), entry.getValue());
        }
    }
}
