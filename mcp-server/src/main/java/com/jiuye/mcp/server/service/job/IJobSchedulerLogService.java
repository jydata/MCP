package com.jiuye.mcp.server.service.job;

import com.jiuye.mcp.server.model.job.JobSchedulerLogEntity;
import com.jiuye.mcp.server.model.job.JobSchedulerProjectEntity;

import java.util.List;

/**
 * @author zhaopeng
 * @date 2018-12-19
 */
public interface IJobSchedulerLogService {

    /**
     * 查询日志列表
     *
     * @param searchInfo
     * @return
     */
    List<JobSchedulerLogEntity> queryLog(JobSchedulerProjectEntity searchInfo);

    /**
     * 根据调度状态
     *
     * @param status
     * @return
     */
    List<JobSchedulerLogEntity> queryLogByStatus(String status);
}
