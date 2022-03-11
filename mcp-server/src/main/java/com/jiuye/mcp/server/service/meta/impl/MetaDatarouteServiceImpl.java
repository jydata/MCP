package com.jiuye.mcp.server.service.meta.impl;

import com.jiuye.mcp.server.dao.meta.MetaDatarouteMapper;
import com.jiuye.mcp.server.dao.meta.MetaTargetSchemaMapper;
import com.jiuye.mcp.server.model.job.JobSchemaEntity;
import com.jiuye.mcp.server.model.meta.MetaDatarouteEntity;
import com.jiuye.mcp.server.model.meta.MetaTargetSchemaEntity;
import com.jiuye.mcp.server.service.meta.IMetaDatarouteService;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhaopeng
 * @date 2018-11-14
 */
@Service
public class MetaDatarouteServiceImpl implements IMetaDatarouteService {

    private static final Logger logger = LoggerFactory.getLogger(MetaDatarouteServiceImpl.class.getName());

    @Autowired
    private MetaDatarouteMapper metaDatarouteMapper;
    @Autowired
    private MetaTargetSchemaMapper metaTargetSchemaMapper;

    /**
     * 查询路由列表
     *
     * @return
     */
    @Override
    public List<MetaDatarouteEntity> queryDBRoutePage(MetaDatarouteEntity entity) {
        return metaDatarouteMapper.queryRouteList(entity);
    }

    /**
     * 查询路由
     * @return
     */
    @Override
    public List<JobSchemaEntity> queryJobRoutes(){
        List<JobSchemaEntity> jobSchemaEntities = new ArrayList<>();

        MetaDatarouteEntity param = new MetaDatarouteEntity();
        param.setRouteStatus("1");
        List<MetaDatarouteEntity> list = metaDatarouteMapper.queryRouteList(param);
        for(MetaDatarouteEntity entity : list){
            JobSchemaEntity jobSchemaEntity = querySchemaByRouteId(entity.getRouteId());
            if(jobSchemaEntity.getChildren().size()>0){
                jobSchemaEntities.add(jobSchemaEntity);
            }
        }

        return jobSchemaEntities;
    }

    /**
     * 根据路由id查询对应的schema
     * @param routeId
     * @return
     */
    private JobSchemaEntity querySchemaByRouteId(long routeId){
        JobSchemaEntity jobSchemaEntity = new JobSchemaEntity();
        jobSchemaEntity.setId(routeId);
        MetaDatarouteEntity metaDatarouteEntity = metaDatarouteMapper.queryRouteById(routeId);
        jobSchemaEntity.setLabel(metaDatarouteEntity.getRouteName());
        List<JobSchemaEntity> jobList = new ArrayList<>();

        List<MetaTargetSchemaEntity> list = metaTargetSchemaMapper.querySchemaByRouteId(routeId);
        for (MetaTargetSchemaEntity entity : list){
            JobSchemaEntity schemaEntity = new JobSchemaEntity();
            schemaEntity.setId(entity.getSchemaId());
            schemaEntity.setLabel(entity.getSchemaName());
            jobList.add(schemaEntity);
        }

        jobSchemaEntity.setChildren(jobList);
        return jobSchemaEntity;
    }

    /**
     * 新增路由信息
     */
    @Override
    public void addRoute(MetaDatarouteEntity entity) throws Exception {
        metaDatarouteMapper.saveRouteInfo(entity);
    }

    /**
     *检测路由
     */
    @Override
    public int checkRoute(MetaDatarouteEntity entity) throws Exception {
        return metaDatarouteMapper.queryRouteInfo(entity);
    }

    /**
     * 修改数据路由状态信息
     *
     * @param entity
     */
    @Override
    public void updateRouteStatus(MetaDatarouteEntity entity, String param) throws Exception {
        entity.setUpdateTime(FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(new Date()));
        entity.setUpdateUser(param);

        metaDatarouteMapper.updateRouteStatus(entity);
    }

    /**
     * 修改路由名称
     */
    @Override
    public void updateRouteName(MetaDatarouteEntity entity, String param) throws Exception {
        entity.setUpdateTime(FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(new Date()));
        entity.setUpdateUser(param);

        metaDatarouteMapper.updateRouteName(entity);
    }

}



