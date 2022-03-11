package com.jiuye.mcp.server.service.meta;

import com.jiuye.mcp.server.model.meta.DBTableInfoEntity;
import com.jiuye.mcp.server.model.meta.MetaDdlRulesEntity;

import java.util.List;

/**
 * 
 * @author kevin
 * 
 */
public interface IDdlRulesService {

    /**
     * 根据参数查询ddl与规则映射
     *
     * @param routeId
     * @param tarSchemaId
     * @return
     */
    List<MetaDdlRulesEntity> queryByRouteIdAndTarSchemaId(long routeId, long tarSchemaId);

    /**
     * 根据jobId查询ddl与规则映射
     *
     * @param jobId
     * @return
     */
    List<MetaDdlRulesEntity> queryByJobId(long jobId);

    /**
     * 根据参数查询终端表数量
     * @param routeId
     * @param targetSchemaId
     * @return
     */
    int queryTargetTablesByJob(long routeId, long targetSchemaId);

    /**
     * 根据参数查询数据库、表信息-增量
     *
     * @param routeId
     * @param tarSchemaId
     * @return
     */
    List<DBTableInfoEntity> queryDbAndTable(long routeId, long tarSchemaId);

    /**
     * 根据参数查询数据库、表信息-全量
     *
     * @param routeId
     * @param tarSchemaId
     * @return
     */
    List<DBTableInfoEntity> queryFullDbTableList(long routeId, long tarSchemaId);

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
}
