/**
 * Copyright (c) 2016, Jiuye SCM and/or its affiliates. All rights reserved.
 *
 */
package com.jiuye.mcp.server.dao.job;

import com.jiuye.mcp.server.model.job.JobAlterSqlEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 
 * @author jepson
 * 
 */
@Component
public interface JobAlterSqlMapper {

    /**
     * 查询终端ddl
     *
     * @param routeId
     * @param srcDbName
     * @param srcTableName
     * @return
     */
    String queryTargetOperateDDL(@Param("routeId") Long routeId, @Param("srcDbName") String srcDbName, @Param("srcTableName") String srcTableName);

    /**
     * job的alter sql保存
     * @param qzAlterEntity
     */
    void save(JobAlterSqlEntity qzAlterEntity);

}
