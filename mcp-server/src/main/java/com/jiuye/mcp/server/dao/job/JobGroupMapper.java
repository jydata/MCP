package com.jiuye.mcp.server.dao.job;

import com.jiuye.mcp.server.model.job.JobGroupEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * JobProjectMapper
 *
 * @author zg
 * @date 2018-11-28
 */
@Component
public interface JobGroupMapper {

    /**
     * 查询指定group
     * @param projectId
     * @return
     */
    List<JobGroupEntity> queryGroupInfo(long projectId);

    /**
     * 检查group是否存在
     * @param entity
     * @return
     */
    int checkGroup(JobGroupEntity entity);

    /**
     * 新增group
     * @param jobGroup
     * @return
     */
    int save(JobGroupEntity jobGroup);

    /**
     * 删除组
     * @param projectId
     * @return
     */
    int deleteGroupByProjectId(long projectId);
}
