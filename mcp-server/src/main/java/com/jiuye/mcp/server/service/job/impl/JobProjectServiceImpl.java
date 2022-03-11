package com.jiuye.mcp.server.service.job.impl;

import com.jiuye.mcp.server.dao.job.JobDefinitionMapper;
import com.jiuye.mcp.server.dao.job.JobGroupMapper;
import com.jiuye.mcp.server.dao.job.JobProjectMapper;
import com.jiuye.mcp.server.model.job.JobProjectEntity;
import com.jiuye.mcp.server.service.job.IJobProjectService;
import com.jiuye.mcp.utils.DateUtil;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * JobProjectServiceImpl
 *
 * @author zg
 * @date 2018-11-28
 */
@Service("jobProjectService")
public class JobProjectServiceImpl implements IJobProjectService {

    @Autowired
    private JobProjectMapper jobProjectMapper;
    @Autowired
    private JobGroupMapper jobGroupMapper;
    @Autowired
    private JobDefinitionMapper jobDefinitionMapper;
    @Autowired
    RedissonClient redissonClient;


    /**
     * 新增或更新project
     * @param jobProjectEntity
     * @return
     */
    @Override
    public int editProject(JobProjectEntity jobProjectEntity, String userName){
        int count = 0;

        String formatDate = DateUtil.getFormatDate(DateUtil.DF_YYYYMMDD_HHMMSS);
        if(jobProjectEntity.getProjectId() != 0){
            // 1. modify project meta (MySQL)
            jobProjectEntity.setUpdateUser(userName);
            jobProjectEntity.setUpdateTime(formatDate);
            count = jobProjectMapper.updateProject(jobProjectEntity);
            // 2. modify redis posi
            // 2.1 remove agent: remove redis posi data
            String agentStr = jobProjectEntity.getProjectAgent();
            List updateAgentList = Arrays.asList(agentStr.split(","));
            List<String> jobIdList = jobDefinitionMapper.queryIncrementByProjectId(jobProjectEntity.getProjectId());
            for (String jobId : jobIdList) {
                RMap latestPosMap = redissonClient.getMap("mcp_job_" + jobId + "_latestpos");
                for (Object key : latestPosMap.keySet()) {
                    if (!updateAgentList.contains(key.toString())) {
                        latestPosMap.remove(key);
                    }
                }
            }
            // 2.2 add agent: do nothing
        }else {
            jobProjectEntity.setCreateUser(userName);
            jobProjectEntity.setDeleteFlag(1);
            jobProjectEntity.setCreateTime(formatDate);
            count = jobProjectMapper.addProject(jobProjectEntity);
        }

        return count;
    }

    /**
     * 加载project name
     * @return
     */
    @Override
    public List<JobProjectEntity> loadProjectName(){
        return  jobProjectMapper.loadProjectName();
    }

    /**
     * 删除project
     * @param projectId
     * @return
     */
    @Override
    public int deleteProject(long projectId){
        jobProjectMapper.deleteProject(projectId);
        jobGroupMapper.deleteGroupByProjectId(projectId);
        jobDefinitionMapper.deleteJobByProjectId(projectId);

        return 1;
    }

    /**
     * 查询project
     * @param projectName
     * @return
     */
    @Override
    public List<JobProjectEntity> queryByName(String projectName){
        return jobProjectMapper.queryProject(projectName);
    }
}
