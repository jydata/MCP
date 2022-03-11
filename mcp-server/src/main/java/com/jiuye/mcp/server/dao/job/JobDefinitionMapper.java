package com.jiuye.mcp.server.dao.job;

import com.jiuye.mcp.server.model.job.JobDefineEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhaopeng
 * @date 2018-11-28
 */
@Component
public interface JobDefinitionMapper {

    /**
     * 查询作业信息
     *
     * @param entity
     * @return
     */
    List<JobDefineEntity> query(JobDefineEntity entity);

    /**
     * 查询作业对应的源端、终端信息
     *
     * @param entity
     * @return
     */
    List<JobDefineEntity> queryJob(JobDefineEntity entity);

    /**
     * 查询指定状态list的作业信息
     *
     * @param deleteFlag
     * @param status
     * @return
     */
    List<JobDefineEntity> queryRestoreJob(@Param("deleteFlag") int deleteFlag, @Param("list") List<String> status);

    /**
     * 根据id查询job
     *
     * @param jobId
     * @return
     */
    JobDefineEntity queryById(@Param("jobId") Long jobId);

    /**
     * 验证job唯一
     *
     * @param entity
     * @return
     */
    JobDefineEntity queryByName(JobDefineEntity entity);

    /**
     * 保存
     *
     * @param entity
     * @return
     */
    int save(JobDefineEntity entity);

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    int update(JobDefineEntity entity);

    /**
     * update the running status job to {status}
     *
     * @return
     */
    int updateRunning2Fail();

    /**
     * 更新recentBinlog
     *
     * @param entity
     * @return
     */
    int updateRecentBinlog(JobDefineEntity entity);

    /**
     * 删除项目
     *
     * @param projectId
     * @return
     */
    int deleteJobByProjectId(long projectId);


    /**
     * 查看作业运行个数
     *
     * @param routeId
     * @param jobId
     * @param srcDbList
     * @return
     */
    int checkJobRunning(@Param("routeId") long routeId, @Param("jobId") long jobId, @Param("srcDbList") List<String> srcDbList);

    /**
     * 查看increment/full作业运行个数
     *
     * @param routeId
     * @return
     */
    int checkIncreAndFullJobRunning(@Param("routeId") long routeId, @Param("jobId") long jobId);

    /**
     * 根据projectid 查询增量job
     *
     * @param projectId
     * @return
     */
    List<String> queryIncrementByProjectId(long projectId);
}
