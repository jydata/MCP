package com.jiuye.mcp.server.service.job;

import com.jiuye.mcp.server.model.job.*;

import java.util.List;

/**
 * IJobGroupService
 *
 * @author zg
 * @date 2018-11-28
 */
public interface IJobGroupService {

    /**
     * 查询指定group
     * @param projectId
     * @return
     */
    List<JobGroupEntity> queryGroupInfo(Long projectId);

}
