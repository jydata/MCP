/**
 * Copyright (c) 2016, Jiuye SCM and/or its affiliates. All rights reserved.
 *
 */
package com.jiuye.mcp.server.dao.meta;

import com.jiuye.mcp.server.model.meta.MetaConnLinksEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zg
 * @date 2018-11-14
 */
@Component
public interface MetaConnLinksMapper {

    /**
     * 查询连接信息列表
     *
     * @param entity
     * @return
     */
    List<MetaConnLinksEntity> queryLinkList(MetaConnLinksEntity entity);

    /**
     * 根据linkId查询连接信息
     *
     * @param linkId
     * @return
     */
    MetaConnLinksEntity queryLinkInfo(Long linkId);

    /**
     * 根据linkId查询连接名称
     *
     * @param srcId
     * @return
     */
    String queryDBTable(Long srcId);

    /**
     * 根据routeId查询源端连接实体
     *
     * @param routeId
     * @return
     */
    MetaConnLinksEntity querySourceLinkByRouteId(Long routeId);

    /**
     * 根据routeId查询终端连接实体
     *
     * @param routeId
     * @return
     */
    MetaConnLinksEntity queryTargetLinkByRouteId(Long routeId);

    /**
     * 根据源端id查询连接实体
     *
     * @param sourceId
     * @return
     */
    MetaConnLinksEntity queryLinkBySourceId(long sourceId);

    /**
     * 保存
     *
     * @param entity
     * @return
     */
    int saveConn(MetaConnLinksEntity entity);

    /**
     * update db link flag
     *
     * @param entity
     * @return
     */
    int updateDBLinkFlag(MetaConnLinksEntity entity);

}
