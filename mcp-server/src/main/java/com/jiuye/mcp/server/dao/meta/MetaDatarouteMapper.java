/**
 * Copyright (c) 2016, Jiuye SCM and/or its affiliates. All rights reserved.
 *
 */
package com.jiuye.mcp.server.dao.meta;

import com.jiuye.mcp.server.model.SourceRunnerParam;
import com.jiuye.mcp.server.model.meta.MetaDatarouteEntity;
import com.jiuye.mcp.server.model.meta.MetaMysqlTablesEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhaopeng
 * @date 2018-11-14
 */
@Component
public interface MetaDatarouteMapper {

    /**
     * 路由列表
     *
     * @param entity
     * @return
     */
    List<MetaDatarouteEntity> queryRouteList(MetaDatarouteEntity entity);

    /**
     * 查询路由信息
     *
     * @param entity
     * @return
     */
//    MetaDatarouteEntity queryRoute(MetaDatarouteEntity entity);

    /**
     * 根据路由名称查询数量
     *
     * @param entity
     * @return
     */
    int queryRouteInfo(MetaDatarouteEntity entity);

    /**
     * 保存
     *
     * @param entity
     * @return
     */
    int saveRouteInfo(MetaDatarouteEntity entity);

    /**
     * 更新状态
     *
     * @param entity
     * @return
     */
    int updateRouteStatus(MetaDatarouteEntity entity);

    /**
     * 更新路由名称
     *
     * @param entity
     * @return
     */
    int updateRouteName(MetaDatarouteEntity entity);

    /**
     * 根据routeId查询路由信息
     *
     * @param routeId
     * @return
     */
    MetaDatarouteEntity queryRouteById(long routeId);

    /**
     * 根据targetId查询路由列表
     *
     * @param targetId
     * @return
     */
    List<MetaDatarouteEntity> queryRouteInfoList(Long targetId);

    /**
     * 根据routeId查询已同步的树信息
     *
     * @param routeId
     * @return
     */
    List<MetaMysqlTablesEntity> querySynTreeByRouteId(long routeId);

    /**
     * 查询所有的routeId和routeName
     *
     * @return
     */
    List<MetaDatarouteEntity> queryRouteId();

    /**
     * 查询路由源端和终端数据库连接信息
     *
     * @param routeId
     * @return
     */
    SourceRunnerParam querySrcAndTarLinkByRouteId(Long routeId);

}
