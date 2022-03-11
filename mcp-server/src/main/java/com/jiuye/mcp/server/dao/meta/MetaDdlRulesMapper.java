/**
 * Copyright (c) 2016, Jiuye SCM and/or its affiliates. All rights reserved.
 *
 */
package com.jiuye.mcp.server.dao.meta;


import com.jiuye.mcp.server.model.meta.MetaDdlRulesEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 * @author kevin
 *
 */
@Component
public interface MetaDdlRulesMapper {

    /**
     * 根据参数查询ddl与规则映射
     *
     * @param routeId
     * @param tarSchemaId
     * @return
     */
    List<MetaDdlRulesEntity> queryByRouteIdAndTarSchemaId(@Param("routeId") Long routeId, @Param("tarSchemaId") Long tarSchemaId);

    /**
     * 根据参数查询ddl与规则映射
     *
     * @param jobId
     * @return
     */
    List<MetaDdlRulesEntity> queryByJobId(@Param("jobId") Long jobId);

    /**
     * 根据参数查询终端表数量
     * @param routeId
     * @param targetSchemaId
     * @return
     */
    int queryTargetTablesByJob(@Param(value = "routeId") Long routeId, @Param(value = "targetSchemaId") Long targetSchemaId);

    /**
     * 根据参数查询ddl与规则映射列表
     *
     * @param entity
     * @return
     */
    List<MetaDdlRulesEntity> query(MetaDdlRulesEntity entity);

    /**
     * 保存
     *
     * @param entity
     * @return
     */
    int save(MetaDdlRulesEntity entity);

    /**
     * 批量保存
     *
     * @param list
     * @return
     */
    int saveBatch(List<MetaDdlRulesEntity> list);

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    int update(MetaDdlRulesEntity entity);

    /**
     * 查询规则名称及数量
     *
     * @param entity
     * @return
     */
    MetaDdlRulesEntity queryRuleName(MetaDdlRulesEntity entity);

    /**
     * 查询路由、src_schema_name、表数量
     * @param routeId
     * @return
     */
    List<MetaDdlRulesEntity> queryTableInfoCount(Long routeId);

}
