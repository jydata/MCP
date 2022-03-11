/**
 * Copyright (c) 2016, Jiuye SCM and/or its affiliates. All rights reserved.
 *
 */
package com.jiuye.mcp.server.service.meta;

import com.jiuye.mcp.server.model.meta.MetaTargetSchemaEntity;
import com.jiuye.mcp.server.model.meta.TargetSchemaInfoEntity;

import java.util.List;

/**
 * 
 * @author kevin
 * 
 */
public interface IMetaTargetSchemaService {

    /**
     * 加载Target Schema信息
     *
     * @param entity
     * @return
     */
    List<MetaTargetSchemaEntity> queryList(MetaTargetSchemaEntity entity);

    /**
     * 查询终端信息
     * @return
     */
    List<TargetSchemaInfoEntity> queryList();

    /**
     * 新增Schema信息
     *
     * @param schemaList
     * @param operator
     * @throws Exception
     */
    void saveSchemaInfo(List<MetaTargetSchemaEntity> schemaList, String operator) throws Exception;

    /**
     * 在终端生成Schema信息
     *
     * @param list
     * @param operator
     * @throws Exception
     */
    void createSchema(List<MetaTargetSchemaEntity> list, String operator) throws Exception;

}
