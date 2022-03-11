/**
 * Copyright (c) 2016, Jiuye SCM and/or its affiliates. All rights reserved.
 */
package com.jiuye.mcp.server.dao.meta;

import com.jiuye.mcp.server.model.job.JobSyncTableEntity;
import com.jiuye.mcp.server.model.meta.MetaMysqlColumnsEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 *
 * @author kevin
 *
 */
@Component
public interface MetaMysqlColumnsMapper {

    /**
     * 根据src_id、table_schema、table_name查询所有列信息
     * @param param
     * @return
     */
    List<MetaMysqlColumnsEntity> queryAllColumns(Map param);

    /**
     * 根据routeId、db table查询列信息
     *
     * @param routeId
     * @param dbTableList
     * @return
     */
    List<MetaMysqlColumnsEntity> queryColumns(@Param("routeId") Long routeId, @Param("dbTableList") List<MetaMysqlColumnsEntity> dbTableList);

    /**
     * 批量保存列
     * @param list
     * @return
     */
    int saveBatch(List<MetaMysqlColumnsEntity> list);

    /**
     * delete columns
     *
     * @param srcId
     * @param schemaName
     * @param tableName
     * @return
     */
    int delete(@Param("srcId") long srcId, @Param("schemaName") String schemaName, @Param("tableName") String tableName);

    /**
     * delete columns
     *
     * @param srcId
     * @param syncList
     * @return
     */
    int deleteBatch(@Param("srcId") long srcId, @Param("syncList") List<JobSyncTableEntity> syncList);

}
