package com.jiuye.mcp.server.service.job.impl;

import com.jiuye.mcp.server.dao.job.JobSchedulerLogMapper;
import com.jiuye.mcp.server.model.job.JobSchedulerLogEntity;
import com.jiuye.mcp.server.model.job.JobSchedulerProjectEntity;
import com.jiuye.mcp.server.service.job.IJobSchedulerLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaopeng
 * @date 2018-12-19
 */
@Service
public class JobSchedulerLogServiceImpl implements IJobSchedulerLogService {

    @Autowired
    private JobSchedulerLogMapper jobSchedulerLogMapper;

    @Override
    public List<JobSchedulerLogEntity> queryLog(JobSchedulerProjectEntity searchInfo){
        List<Long> list = searchInfo.getJobList();
        List<JobSchedulerLogEntity> result = new ArrayList<>();
        JobSchedulerLogEntity entity = searchInfo.getEntity();
        for (Long jobId : list){
            entity.setJobId(jobId);
            List<JobSchedulerLogEntity> queryInfo = jobSchedulerLogMapper.queryLog(entity);
            if(null != queryInfo) {
                for(JobSchedulerLogEntity logInfo : queryInfo){
                    result.add(logInfo);
                }
            }
        }
        return result;
    }

    @Override
    public List<JobSchedulerLogEntity> queryLogByStatus(String status) {
        List<JobSchedulerLogEntity> result = new ArrayList<>();

        String jobStatusRunning = "running";
        String jobStatusFail = "fail";
        if (jobStatusRunning.equals(status)) {
            result = jobSchedulerLogMapper.queryLogRunning();
            if (null != result) {
                for (JobSchedulerLogEntity logInfo : result) {
                    logInfo.setDuration(String.valueOf((System.currentTimeMillis() - logInfo.getHandleTime().getTime()) / 1000));
                }
            }
        }

        if (jobStatusFail.equals(status)) {
            result = jobSchedulerLogMapper.queryLogFail();
            if (null != result) {
                for (JobSchedulerLogEntity logInfo : result) {
                    logInfo.setDuration(String.valueOf((System.currentTimeMillis() - logInfo.getHandleTime().getTime()) / 1000));
                }
            }
        }
        return result;
    }
}
