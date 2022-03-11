package com.jiuye.mcp.server.service.job;

import com.jiuye.mcp.server.model.job.JobProjectEntity;

import java.util.List;

/**
 * IJobProjectService
 *
 * @author zg
 * @date 2018-11-28
 */
public interface IJobProjectService {

    /**
     * 新增或更新project
     *
     * @param jobProjectEntity
     * @param userName
     * @return
     */
    int editProject(JobProjectEntity jobProjectEntity, String userName);

    /**
     * 加载project name
     * @return
     */
    List<JobProjectEntity> loadProjectName();

    /**
     * 删除project
     * @param projectId
     * @return
     */
    int deleteProject(long projectId);

    /**
     * 查询project
     * @param projectName
     * @return
     */
    List<JobProjectEntity> queryByName(String projectName);

}
