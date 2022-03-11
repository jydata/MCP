/**
 * Copyright (c) 2016, Jiuye SCM and/or its affiliates. All rights reserved.
 */
package com.jiuye.mcp.server.dao.meta;

import com.jiuye.mcp.server.model.job.JobSyncTableEntity;
import com.jiuye.mcp.server.model.meta.MetaMysqlDatabasesEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 * @author kevin
 *
 */
@Component
public interface MetaMysqlDatabasesMapper {

    /**
     * 根据linkId查询要同步的schema_name的数量
     *
     * @param srcId
     * @return
     */
    int count(long srcId);

    /**
     *
     * 根据sourceId查询对应的源端db
     *
     * @param sourceId
     * @return
     */
    List<String> querySchemaName(long sourceId);

    /**
     * 批量保存DB
     *
     * @param list
     * @return
     */
    int saveBatch(@Param("list") List<MetaMysqlDatabasesEntity> list);

    /**
     * delete dbs
     *
     * @param srcId
     * @param syncList
     * @return
     */
    int delete(@Param("srcId") long srcId, @Param("syncList") List<JobSyncTableEntity> syncList);

}
