package com.jiuye.mcp.server.dao.job;

import com.jiuye.mcp.server.model.job.JobProjectEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * JobProjectMapper
 *
 * @author zg
 * @date 2018-11-28
 */
@Component
public interface JobProjectMapper {

    /**
     * 保存新增项目
     * @param jobProjectEntity
     * @return
     */
    int addProject(JobProjectEntity jobProjectEntity);

    /**
     * 加载所有项目名称
     * @return
     */
    List<JobProjectEntity> loadProjectName();

    /**
     * 更新已经存在项目
     * @param jobProjectEntity
     * @return
     */
    int updateProject(JobProjectEntity jobProjectEntity);

    /**
     * 删除项目
     * @param projectId
     * @return
     */
    int deleteProject(long projectId);

    /**
     * 查询项目
     * @param projectName
     * @return
     */
    List<JobProjectEntity> queryProject(String projectName);

    /**
     * 根据projectId查询agent
     * @param projectId
     * @return
     */
    String queryAgent(long projectId);

    /**
     * 根据id查询project
     * @param projectId
     * @return
     */
    JobProjectEntity queryProjectById(long projectId);

    /**
     * 更新对应project的job失败次数
     * @param projectId
     * @param failNum
     */
    void updateProjectFailNum(@Param(value = "projectId") long projectId, @Param(value = "failNum") int failNum);
}
