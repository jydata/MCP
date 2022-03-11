package com.jiuye.mcp.server.service.meta;

import com.jiuye.mcp.server.model.ValidateResult;
import com.jiuye.mcp.server.model.meta.DictEntity;
import com.jiuye.mcp.server.model.meta.MetaConnLinksEntity;

import java.util.List;



/**
 * @author kim
 * @date 2018-11-14
 */

public interface IMetaConnLinksService {

    /**
     * 查询所有数据库连接
     *
     * @return
     */
    List<MetaConnLinksEntity> queryDBLinkPage(MetaConnLinksEntity entity);

    /**
     * 测试连接
     *
     * @param entity
     * @return
     * @throws Exception
     */
    ValidateResult testConnection(MetaConnLinksEntity entity) throws Exception;


    /**
     * 添加数据库连接
     *
     * @param entity
     * @param operator
     * @throws Exception
     */
    void addConnection(MetaConnLinksEntity entity, String operator) throws Exception;

    /**
     * 加载数据端列表
     *
     * @param entity
     * @return
     */
    List<DictEntity> loadOptions(DictEntity entity);

    /**
     * 检查环境-DDL
     * @param routeId
     * @return
     */
    ValidateResult checkEvnDDL(Long routeId);

}
