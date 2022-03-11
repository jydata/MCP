/**
 * Copyright (c) 2016, Jiuye SCM and/or its affiliates. All rights reserved.
 */
package com.jiuye.mcp.server.dao.meta;

import com.jiuye.mcp.server.model.job.JobSyncTableEntity;
import com.jiuye.mcp.server.model.meta.MetaMysqlTablesEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 * @author stevenl
 *
 */
@Component
public interface MetaMysqlTablesMapper {

    /**
     * 查询表的元数据信息
     *
     * @param entity
     * @return
     */
    MetaMysqlTablesEntity query(MetaMysqlTablesEntity entity);

    /**
     * 查询源端表数据
     *
     * @param entity
     * @return
     */
    MetaMysqlTablesEntity querySrcTableCount(MetaMysqlTablesEntity entity);

    /**
     * 根据srcId查询table_schema、table_name
     *
     * @param srcId
     * @return
     */
    List<MetaMysqlTablesEntity> queryList(long srcId);

    /**
     * 根据参数查询表的数量
     *
     * @param srcId
     * @param tableSchema
     * @param tableName
     * @return
     */
    int count(@Param(value = "srcId") long srcId, @Param(value = "tableSchema") String tableSchema, @Param(value = "tableName") String tableName);

    /**
     * 批量保存table
     *
     * @param list
     * @return
     */
    int saveBatch(List<MetaMysqlTablesEntity> list);

    /**
     * delete tables
     *
     * @param srcId
     * @param syncList
     * @return
     */
    int delete(@Param("srcId") long srcId, @Param("syncList") List<JobSyncTableEntity> syncList);
}
