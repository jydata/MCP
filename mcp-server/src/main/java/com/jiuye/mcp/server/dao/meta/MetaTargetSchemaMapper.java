/**
 * Copyright (c) 2016, Jiuye SCM and/or its affiliates. All rights reserved.
 *
 */
package com.jiuye.mcp.server.dao.meta;

import com.jiuye.mcp.server.model.meta.MetaTargetSchemaEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 
 * @author kevin
 * 
 */
@Component
public interface MetaTargetSchemaMapper {

    /**
     * 根据targetSchemaId查询终端schema名称
     *
     * @param targetSchemaId
     * @return
     */
    String queryName(long targetSchemaId);

    /**
     * query schema list
     *
     * @param entity
     * @return
     */
    List<MetaTargetSchemaEntity> queryList(MetaTargetSchemaEntity entity);

    /**
     * 根据schemaId查询终端信息
     * @param schemaId
     * @return
     */
    MetaTargetSchemaEntity queryBySchemaId(long schemaId);

    /**
     * 根据schemaName查询数量
     *
     * @param entity
     * @return
     */
    int queryCount(MetaTargetSchemaEntity entity);

    /**
     * 根据routeId查询对应的schema
     *
     * @param routeId
     * @return
     */
    List<MetaTargetSchemaEntity> querySchemaByRouteId(long routeId);

    /**
     * 保存元数据终端schema
     *
     * @param entity
     * @return
     */
    int save(MetaTargetSchemaEntity entity);

    /**
     * 更新元数据终端schema
     *
     * @param entity
     * @return
     */
    int update(MetaTargetSchemaEntity entity);

}
