package com.jiuye.mcp.server.service.meta;

import com.jiuye.mcp.server.model.SourceRunnerParam;
import com.jiuye.mcp.server.model.job.JobDefineEntity;
import com.jiuye.mcp.server.model.meta.DBTableInfoEntity;
import com.jiuye.mcp.server.model.meta.MetaConnLinksEntity;
import com.jiuye.mcp.server.model.meta.MetaMysqlColumnsEntity;
import com.jiuye.mcp.server.model.meta.MetaMysqlTablesEntity;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author zhaopeng
 * @date 2018-11-20
 */
public interface IDataSourceSyncService {

    /**
     * DS Tree数据加载
     *
     * @param entity
     * @return
     */
    DBTableInfoEntity queryDBTree(MetaConnLinksEntity entity);

    /**
     * 根据routeId查询信息
     * @param routeId
     * @return
     */
    DBTableInfoEntity queryDBTree(Long routeId);

    /**
     * DS Sync 数据库信息统计
     *
     * @param entity
     * @return
     */
    LinkedHashMap<String, String> calcDBData(MetaConnLinksEntity entity);

    /**
     * 查询数据库Table信息
     *
     * @param entity
     * @param dataList
     * @return
     */
    List<MetaMysqlTablesEntity> loadTableInfo(MetaConnLinksEntity entity, String[] dataList);

    /**
     * 查询Column信息
     *
     * @param entity
     * @param dataList
     * @return
     */
    List<MetaMysqlColumnsEntity> loadColumnInfo(MetaConnLinksEntity entity, String[] dataList);

    /**
     * 查询table样例信息
     *
     * @param entity
     * @param dataList
     * @return
     */
    List<LinkedHashMap> loadExample(MetaConnLinksEntity entity, String[] dataList);

    /**
     * 查询DDL信息
     *
     * @param entity
     * @param dataList
     * @return
     */
    String loadDDLInfo(MetaConnLinksEntity entity, String[] dataList);

    /**
     * 获得mysql与mcp的schema同步比例
     *
     * @param sourceId
     * @return
     */
    int syncRation(long sourceId);

    /**
     * 从源端更新列信息到mcp元库中
     *
     * @param conn
     * @param srcId
     * @param dbName
     * @param tableName
     * @throws Exception
     */
    void loadTablePreColumns(Connection conn, Long srcId, String dbName, String tableName) throws Exception;

    /**
     * 元数据同步---数据库建表
     *
     * @param job
     * @throws Exception
     */
    void loadMetadataForJob(JobDefineEntity job) throws Exception;

    /**
     * 查询路由源端和终端数据库连接信息
     *
     * @param jobDefineEntity
     * @return
     */
    SourceRunnerParam querySrcAndTarLinkByRouteId(JobDefineEntity jobDefineEntity);

    /**
     * 根据routeId查询终端同步表
     *
     * @param jobId
     * @param srcDb
     * @param srcTable
     * @return
     */
    List<String> querySyncTableByJobId(Long jobId, String srcDb, String srcTable);

}
