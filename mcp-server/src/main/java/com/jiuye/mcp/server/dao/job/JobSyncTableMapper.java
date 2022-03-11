package com.jiuye.mcp.server.dao.job;

import com.jiuye.mcp.server.model.job.JobSyncTableEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author kevin
 *
 */
@Component
public interface JobSyncTableMapper {

    /**
     * 查询列表
     *
     * @param entity
     * @return
     */
    List<JobSyncTableEntity> query(JobSyncTableEntity entity);

    /**
     * query full sync table
     *
     * @param jobId
     * @param srcDb
     * @param srcTable
     * @return
     */
    List<String> querySyncTableByJobId(@Param("jobId")Long jobId, @Param("srcDb")String srcDb, @Param("srcTable")String srcTable);


    /**
     * 保存
     *
     * @param entity
     * @return
     */
    JobSyncTableEntity save(JobSyncTableEntity entity);

    /**
     * 新增jobSyncTables数据
     *
     * @param list
     * @return
     */
    int saveBatch(List<JobSyncTableEntity> list);

    /**
     * 跟新
     * @param entity
     * @return
     */
    int update(JobSyncTableEntity entity);

    /**
     * 更新jobSyncTables数据,使deleteFlag为0
     *
     * @param jobId
     * @param srcDb
     * @param srcTable
     * @return
     */
    int updateJobSyncTables(@Param(value = "jobId") long jobId, @Param(value = "srcDb") String srcDb, @Param(value = "srcTable") String srcTable);

    /**
     * 根据jobId删除
     *
     * @param jobId
     * @return
     */
    int deleteByJobId(@Param(value = "jobId") long jobId);

}
