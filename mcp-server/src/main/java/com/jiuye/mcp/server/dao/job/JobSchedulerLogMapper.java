package com.jiuye.mcp.server.dao.job;

import com.jiuye.mcp.server.model.job.JobSchedulerLogEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhaopeng
 * @date 2018-12-19
 */
@Component
public interface JobSchedulerLogMapper {

    /**
     * 查询日志信息列表
     *
     * @param entity
     * @return
     */
    List<JobSchedulerLogEntity> queryLog(JobSchedulerLogEntity entity);

    /**
     * 通过jobId及handleCode查询指定状态的Log信息
     *
     * @param entity
     * @return
     */
    JobSchedulerLogEntity queryLogInfo(JobSchedulerLogEntity entity);

    /**
     * 更新日志信息
     *
     * @param entity
     * @return
     */
    int updateLog(JobSchedulerLogEntity entity);

    /*int updateRunning2Fail(@Param("list") List<Long> jobIds);*/

    /**
     * 更新running(300) -> fail(500)
     * @return
     */
    int updateRunning2Fail();

    /**
     * 根据jobId查询日志信息
     *
     * @param jobId
     * @return
     */
    JobSchedulerLogEntity queryFreshLog(Long jobId);

    /**
     * 保存
     *
     * @param entity
     * @return
     */
    int save(JobSchedulerLogEntity entity);


    /**
     * 查看running 的调度作业
     *
     * @return
     */
    List<JobSchedulerLogEntity> queryLogRunning();

    /**
     * 查看fail 的调度作业
     *
     * @return
     */
    List<JobSchedulerLogEntity> queryLogFail();
}
