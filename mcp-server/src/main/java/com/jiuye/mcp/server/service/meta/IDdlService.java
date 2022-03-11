package com.jiuye.mcp.server.service.meta;

import com.jiuye.mcp.server.model.ValidateResult;
import com.jiuye.mcp.server.model.meta.*;

import java.sql.SQLException;
import java.util.List;

/**
 * @author kevin
 */
public interface IDdlService {

    /**
     * 判断mcp是否存在表
     *
     * @param param
     * @return
     */
    boolean existTables(List<Long> param);

    /**
     * 根据sourceId查询对应的源端db
     * @param sourceId
     * @return
     */
    List<String> querySourceDb(long sourceId);


    /**
     * 返回路由页面传过来的路由信息
     *
     * @param targetId
     * @param targetSchemaId
     * @return
     */
    List<MetaDatarouteEntity> queryRouteInfo(Long targetId, Long targetSchemaId);

    /**
     * 根据对应信息查询元数据
     *
     * @param sourceId
     * @param schema
     * @param table
     * @return
     */
    List<DBTableEntity> queryTableMeta(Long sourceId, String schema, String table);

    /**
     * 加载表的Schema信息
     *
     * @param param
     * @return
     */
    List<MetaMysqlColumnsEntity> querySchemaInfo(String param);

    /**
     * 加载已配置的库以及各自库下的表的集合
     *
     * @param targetId
     * @return
     */
    List<DBTableInfoEntity> queryDBTableList(Long targetId);

    /**
     * 单表生成phoenix表的创建SQL(Table, Index)
     *
     * @param list
     * @param flag
     * @param routeId
     * @param schemaId
     * @param id
     * @param ruleName
     * @param srcDb
     * @return
     */
    MetaTargetDdlEntity genSingleTableSqlInfo(List<MetaMysqlColumnsEntity> list, String flag, long routeId, long schemaId, long id, String ruleName, String srcDb);

    /**
     * 获取建表DDL信息
     *
     * @param entity
     * @return
     */
    MetaTargetDdlEntity queryDDLInfo(MetaTargetDdlEntity entity);


    /**
     * 生成phoenix表对应的二级索引表SQL
     *
     * @param entity
     * @param userName
     * @return
     * @throws Exception
     */
    MetaTargetDdlEntity saveHBaseSQL(MetaTargetDdlEntity entity, String userName) throws Exception;

    /**
     * 单表保存返回实体类
     *
     * @param sqlInfoEntity
     * @param userName
     * @param srcDb
     * @param routeId
     * @return
     */
    MetaTargetDdlEntity saveTargetTableSQL(MetaTargetDdlEntity sqlInfoEntity, String userName, String srcDb, long routeId);

    /**
     * 单表同步终端DDL
     *
     * @param param
     * @param srcId
     * @param ruleName
     * @param srcDb
     * @param routeId
     * @param targetSchemaId
     * @return
     * @throws SQLException
     */
    ValidateResult execSQL(List<MetaTargetDdlEntity> param, long srcId, String ruleName, String srcDb, long routeId, long targetSchemaId) throws SQLException;


    /**
     * ddl的job入口
     *
     * @param routeId
     * @param jobId
     * @param srcId
     * @param srcDb
     * @param targetId
     * @param targetSchemaId
     * @param ruleId
     * @param userName
     * @return
     */
    ValidateResult batchExecSQL(Long routeId, Long jobId, Long srcId, String srcDb, Long targetId, Long targetSchemaId, Long ruleId, String userName);

}
