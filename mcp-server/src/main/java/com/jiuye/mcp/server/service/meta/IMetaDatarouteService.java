package com.jiuye.mcp.server.service.meta;

import com.jiuye.mcp.server.model.job.JobSchemaEntity;
import com.jiuye.mcp.server.model.meta.MetaDatarouteEntity;

import java.util.List;

/**
 * @author zhaopeng
 * @date 2018-11-14
 */
public interface IMetaDatarouteService {

    /**
     * 查询所有路由列表
     *
     * @param entity
     * @return
     */
    List<MetaDatarouteEntity> queryDBRoutePage(MetaDatarouteEntity entity);

    /**
     * 查询Job路由
     * @return
     */
    List<JobSchemaEntity> queryJobRoutes();

    /**
     * 新增路由信息
     *
     * @param entity
     * @throws Exception
     */
    void addRoute(MetaDatarouteEntity entity) throws Exception;

    /**
     * 检查路由信息
     *
     * @param entity
     * @return
     * @throws Exception
     */
    int checkRoute(MetaDatarouteEntity entity) throws Exception;

    /**
     * 修改数据路由状态信息
     *
     * @param entity
     * @param param
     * @throws Exception
     */
    void updateRouteStatus(MetaDatarouteEntity entity, String param) throws Exception;

    /**
     * 修改路由名称
     *
     * @param entity
     * @param param
     * @throws Exception
     */
    void updateRouteName(MetaDatarouteEntity entity, String param) throws Exception;

}
