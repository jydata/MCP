package com.jiuye.mcp.server.service.job.impl;

import com.jiuye.mcp.server.dao.job.JobGroupMapper;
import com.jiuye.mcp.server.model.job.*;
import com.jiuye.mcp.server.service.job.IJobGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * JobGroupServiceImpl
 *
 * @author zg
 * @date 2018-11-28
 */
@Service("jobGroupService")
public class JobGroupServiceImpl implements IJobGroupService {

    @Autowired
    private JobGroupMapper jobGroupMapper;

    /**
     * 查询指定group
     * @param projectId
     * @return
     */
    public List<JobGroupEntity> queryGroupInfo(Long projectId){

        return jobGroupMapper.queryGroupInfo(projectId);
    }


}
