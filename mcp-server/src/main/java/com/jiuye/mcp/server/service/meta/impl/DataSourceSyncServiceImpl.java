package com.jiuye.mcp.server.service.meta.impl;

import com.jiuye.mcp.exception.BizException;
import com.jiuye.mcp.exception.InvalidRequestException;
import com.jiuye.mcp.job.log.JobLogger;
import com.jiuye.mcp.param.enums.ApplicationErrorCode;
import com.jiuye.mcp.param.enums.MetadataErrorCode;
import com.jiuye.mcp.server.dao.job.JobSyncTableMapper;
import com.jiuye.mcp.server.dao.meta.*;
import com.jiuye.mcp.server.model.SourceRunnerParam;
import com.jiuye.mcp.server.model.job.JobDefineEntity;
import com.jiuye.mcp.server.model.job.JobSyncTableEntity;
import com.jiuye.mcp.server.model.meta.*;
import com.jiuye.mcp.server.service.meta.IDataSourceSyncService;
import com.jiuye.mcp.server.util.JDBCManager;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author zhaopeng
 * @date 2018-11-20
 */
@Service
public class DataSourceSyncServiceImpl implements IDataSourceSyncService {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceSyncServiceImpl.class.getName());

    @Autowired
    private MetaConnLinksMapper metaConnLinksMapper;
    @Autowired
    private JobSyncTableMapper jobSyncTableMapper;
    @Autowired
    private MetaMysqlColumnsMapper metaMysqlColumnsMapper;
    @Autowired
    private MetaMysqlStatisticsMapper metaMysqlStatisticsMapper;
    @Autowired
    private MetaMysqlTablesMapper metaMysqlTablesMapper;
    @Autowired
    private MetaMysqlDatabasesMapper metaMysqlDatabasesMapper;
    @Autowired
    private MetaDatarouteMapper metaDatarouteMapper;

    /**
     * 加载数据库Tree数据
     *
     * @param entity
     * @return
     */
    @Override
    public DBTableInfoEntity queryDBTree(MetaConnLinksEntity entity) {
        DBTableInfoEntity dbTableInfoEntity = null;
        List<DBTableInfo> treeList = new ArrayList<>();

        String sql = "SELECT TABLE_SCHEMA as schemaName, TABLE_NAME as tableName from `TABLES` WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_SCHEMA NOT IN ('mysql', 'information_schema', 'sys', 'performance_schema')";
        Connection conn = null;
        PreparedStatement preStatement = null;
        ResultSet rs = null;
        Map<String, List<String>> schemaMap = new LinkedHashMap<>();
        try {
            dbTableInfoEntity = new DBTableInfoEntity();
            conn = JDBCManager.factory(JDBCManager.MYSQL).getConnection(entity, JDBCManager.INFORMATION_SCHEMA);
            preStatement = conn.prepareStatement(sql);
            rs = preStatement.executeQuery();

            while (rs.next()) {
                String schemaName = rs.getString("schemaName");
                String tableName = rs.getString("tableName");

                if (schemaMap.containsKey(schemaName)) {
                    List<String> tableNameList = schemaMap.get(schemaName);
                    tableNameList.add(tableName);
                    schemaMap.put(schemaName, tableNameList);
                } else {
                    List<String> tableNameList = new ArrayList<>();
                    tableNameList.add(tableName);
                    schemaMap.put(schemaName, tableNameList);
                }
            }

            Iterator<Map.Entry<String, List<String>>> dbIterator = schemaMap.entrySet().iterator();
            while (dbIterator.hasNext()) {
                Map.Entry<String, List<String>> dbInfo = dbIterator.next();
                String dbName = dbInfo.getKey();
                DBTableInfo dbEntity = new DBTableInfo(false, dbName, dbName);
                List<String> tableList = dbInfo.getValue();

                List<DBTableInfo> tableInfoList = new ArrayList<DBTableInfo>();
                for (String tableName : tableList) {
                    tableInfoList.add(new DBTableInfo(true, dbName + "." + tableName, tableName));
                }
                dbEntity.setChildren(tableInfoList);

                treeList.add(dbEntity);
            }

            dbTableInfoEntity.setId(entity.getLinkId().toString());
            dbTableInfoEntity.setLabel(entity.getLinkName());
            dbTableInfoEntity.setChildren(treeList);
        } catch (SQLException e) {
            logger.error("QueryDBTree is exception!", e);
            throw new BizException(MetadataErrorCode.EXECUTEQUERY_SQLEXCEPTION.getCode(), e.getMessage());
        } finally {
            // close connection
            closeConnection(conn, preStatement, rs);
        }

        return dbTableInfoEntity;
    }

    /**
     * 加载数据库Tree数据
     *
     * @param routeId
     * @return
     */
    @Override
    public DBTableInfoEntity queryDBTree(Long routeId) {
        MetaConnLinksEntity entity = metaConnLinksMapper.querySourceLinkByRouteId(routeId);
        if (null == entity) {
            return null;
        }

        return queryDBTree(entity);
    }

    /**
     * 统计连接的数据库数据信息
     *
     * @param entity
     * @return
     */
    @Override
    public LinkedHashMap<String, String> calcDBData(MetaConnLinksEntity entity) {
        if (null == entity) {
            throw new BizException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        LinkedHashMap<String, String> dataMap = new LinkedHashMap<>();
        String sql = "SELECT COUNT(DISTINCT TABLE_SCHEMA) as schemaCount, COUNT(TABLE_NAME) as tableCount, MAX(TABLE_ROWS) as maxRows, SUM(TABLE_ROWS) as totalRows  from `TABLES` where TABLE_TYPE='BASE TABLE' AND TABLE_SCHEMA NOT IN ('mysql', 'information_schema', 'sys', 'performance_schema')";
        Connection conn = null;
        PreparedStatement preStatement = null;
        ResultSet rs = null;
        try {
            conn = JDBCManager.factory(JDBCManager.MYSQL).getConnection(entity, JDBCManager.INFORMATION_SCHEMA);
            preStatement = conn.prepareStatement(sql);
            rs = preStatement.executeQuery();
            while (rs.next()) {
                dataMap.put("schemaCount", rs.getString("schemaCount"));
                dataMap.put("tableCount", rs.getString("tableCount"));
                dataMap.put("maxRows", rs.getString("maxRows"));
                dataMap.put("totalRows", rs.getString("totalRows"));
            }
        } catch (SQLException e) {
            logger.error("CalcDBData is exception!", e);
            throw new BizException(MetadataErrorCode.EXECUTEQUERY_SQLEXCEPTION.getCode(), e.getMessage());
        } finally {
            // close connection
            closeConnection(conn, preStatement, rs);
        }

        return dataMap;
    }

    /**
     * 加载表信息
     *
     * @param entity
     * @param dataList
     * @return
     */
    @Override
    public List<MetaMysqlTablesEntity> loadTableInfo(MetaConnLinksEntity entity, String[] dataList) {
        if (null == entity || null == dataList) {
            throw new BizException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        List<MetaMysqlTablesEntity> list = new ArrayList<>();
        MetaMysqlTablesEntity infoEntity = new MetaMysqlTablesEntity();

        String sync_flag = "0";
        long linkID = entity.getLinkId();
        String DBName = dataList[0];
        String tableName = dataList[1];
        int count = metaMysqlTablesMapper.count(linkID, DBName, tableName);
        if (count > 0) {
            sync_flag = "1";
        }

        String sql = "SELECT t.TABLE_SCHEMA as tableSchema, t.TABLE_NAME as tableName, t.`ENGINE` as 'ENGINE', s.DEFAULT_CHARACTER_SET_NAME as CHARSET, t.TABLE_ROWS as tableRows, t.TABLE_COMMENT as 'COMMENT' from `TABLES` t LEFT JOIN SCHEMATA s on t.TABLE_SCHEMA = s.SCHEMA_NAME WHERE t.TABLE_SCHEMA = '" + DBName + "'  AND t.TABLE_NAME = '" + tableName + "'";
        Connection conn = null;
        PreparedStatement preStatement = null;
        ResultSet rs = null;
        try {
            conn = JDBCManager.factory(JDBCManager.MYSQL).getConnection(entity, JDBCManager.INFORMATION_SCHEMA);
            preStatement = conn.prepareStatement(sql);
            rs = preStatement.executeQuery();
            while (rs.next()) {
                infoEntity.setTableSchema(rs.getString("tableSchema"));
                infoEntity.setTableName(rs.getString("tableName"));
                infoEntity.setEngine(rs.getString("ENGINE"));
                infoEntity.setTableType(rs.getString("CHARSET"));
                if (null != rs.getString("tableRows")) {
                    infoEntity.setTableRows(Long.valueOf(rs.getString("tableRows")));
                } else {
                    infoEntity.setTableRows(Long.valueOf("0"));
                }
                infoEntity.setTableComment(rs.getString("COMMENT"));
                infoEntity.setSyncFlag(sync_flag);

                list.add(infoEntity);
            }
        } catch (SQLException e) {
            logger.error("LoadTableInfo is exception!", e);
            throw new BizException(MetadataErrorCode.EXECUTEQUERY_SQLEXCEPTION.getCode(), e.getMessage());
        } finally {
            // close connection
            closeConnection(conn, preStatement, rs);
        }

        return list;
    }


    /**
     * 加载列信息
     * 查询出来的Key为MUL时可能不准确，多字段联合时只有一个字段有MUL
     *
     * @param entity
     * @param dataList
     * @return
     */
    @Override
    public List<MetaMysqlColumnsEntity> loadColumnInfo(MetaConnLinksEntity entity, String[] dataList) {
        if (null == entity || null == dataList) {
            throw new BizException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        List<MetaMysqlColumnsEntity> list = new ArrayList<>();

        String DBName = dataList[0];
        String tableName = dataList[1];
        String sql = "SELECT COLUMN_NAME, ORDINAL_POSITION, COLUMN_TYPE, IS_NULLABLE, COLUMN_KEY, COLUMN_DEFAULT, COLUMN_COMMENT FROM `COLUMNS` WHERE TABLE_SCHEMA = '" + DBName + "' and TABLE_NAME = '" + tableName + "'";
        Connection conn = null;
        PreparedStatement preStatement = null;
        ResultSet rs = null;
        try {
            conn = JDBCManager.factory(JDBCManager.MYSQL).getConnection(entity, JDBCManager.INFORMATION_SCHEMA);
            preStatement = conn.prepareStatement(sql);
            rs = preStatement.executeQuery();
            while (rs.next()) {
                MetaMysqlColumnsEntity infoEntity = new MetaMysqlColumnsEntity();
                infoEntity.setColumnName(rs.getString("COLUMN_NAME"));
                infoEntity.setOrdinalPosition(Long.valueOf(rs.getString("ORDINAL_POSITION")));
                infoEntity.setColumnType(rs.getString("COLUMN_TYPE"));
                infoEntity.setIsNullable(rs.getString("IS_NULLABLE"));
                infoEntity.setColumnKey(rs.getString("COLUMN_KEY"));
                infoEntity.setColumnDefault(rs.getString("COLUMN_DEFAULT"));
                infoEntity.setColumnComment(rs.getString("COLUMN_COMMENT"));
                list.add(infoEntity);
            }
        } catch (SQLException e) {
            logger.error("LoadColumnInfo is exception!", e);
            throw new BizException(MetadataErrorCode.EXECUTEQUERY_SQLEXCEPTION.getCode(), e.getMessage());
        } finally {
            // close connection
            closeConnection(conn, preStatement, rs);
        }

        return list;
    }

    /**
     * 加载table样例信息
     *
     * @param entity
     * @param dataList
     * @return
     */
    @Override
    public List<LinkedHashMap> loadExample(MetaConnLinksEntity entity, String[] dataList) {
        if (null == entity || null == dataList) {
            throw new BizException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        List<LinkedHashMap> list = new ArrayList<>();
        String dbName = dataList[0];
        String tableName = dataList[1];

        List<MetaMysqlColumnsEntity> columnsList = loadColumnInfo(entity, dataList);
        //取表的列名，放入List
        List<String> columnNameList = new ArrayList<>();
        for (MetaMysqlColumnsEntity columnInfo : columnsList) {
            String column = columnInfo.getColumnName();
            columnNameList.add(column);
        }

        String sql = "select * from " + tableName + " limit 20";
        Connection conn = null;
        PreparedStatement preStatement = null;
        ResultSet rs = null;
        try {
            conn = JDBCManager.factory(JDBCManager.MYSQL).getConnection(entity, dbName);
            preStatement = conn.prepareStatement(sql);
            rs = preStatement.executeQuery();

            while (rs.next()) {
                LinkedHashMap<String, String> dataMap = new LinkedHashMap<>();
                for (String columnName : columnNameList) {
                    dataMap.put(columnName, rs.getString(columnName));
                }
                list.add(dataMap);
            }
        } catch (SQLException e) {
            logger.error("LoadExample is exception!", e);
            throw new BizException(MetadataErrorCode.EXECUTEQUERY_SQLEXCEPTION.getCode(), e.getMessage());
        } finally {
            // close connection
            closeConnection(conn, preStatement, rs);
        }

        return list;
    }

    /**
     * 加载DDL信息
     *
     * @param entity
     * @param dataList
     * @return
     */
    @Override
    public String loadDDLInfo(MetaConnLinksEntity entity, String[] dataList) {
        if (null == entity || null == dataList) {
            throw new BizException(ApplicationErrorCode.INVALID_ARGUMENTS.getCode(), ApplicationErrorCode.INVALID_ARGUMENTS.getMessage());
        }

        String ddlInfo = null;

        String dbName = dataList[0];
        String tableName = dataList[1];

        String sql = "show create table  " + tableName;
        Connection conn = null;
        PreparedStatement preStatement = null;
        ResultSet rs = null;
        try {
            conn = JDBCManager.factory(JDBCManager.MYSQL).getConnection(entity, dbName);
            preStatement = conn.prepareStatement(sql);
            rs = preStatement.executeQuery();
            while (rs.next()) {
                ddlInfo = rs.getString("Create Table");
            }
        } catch (SQLException e) {
            logger.error("LoadDDLInfo is exception!", e);
            throw new BizException(MetadataErrorCode.EXECUTEQUERY_SQLEXCEPTION.getCode(), e.getMessage());
        } finally {
            // close connection
            closeConnection(conn, preStatement, rs);
        }

        return ddlInfo;
    }

    /**
     * 获得同步比例
     *
     * @param sourceId
     * @return
     */
    @Override
    public int syncRation(long sourceId) {
        MetaConnLinksEntity entity = metaConnLinksMapper.queryLinkBySourceId(sourceId);
        int syncCount = 0;

        String sql = "select count(*) as result from `SCHEMATA` WHERE SCHEMA_NAME NOT IN ('mysql', 'information_schema', 'sys', 'performance_schema')";
        Connection conn = null;
        PreparedStatement preStatement = null;
        ResultSet rs = null;
        try {
            conn = JDBCManager.factory(JDBCManager.MYSQL).getConnection(entity, JDBCManager.INFORMATION_SCHEMA);
            preStatement = conn.prepareStatement(sql);
            rs = preStatement.executeQuery();
            while (rs.next()) {
                syncCount = rs.getInt("result");
            }

            int count = metaMysqlDatabasesMapper.count(entity.getLinkId());
            if (count == 0) {
                return 0;
            } else if (count == syncCount) {
                return 2;
            }
        } catch (SQLException e) {
            logger.error("SyncRation is exception!", e);
            throw new BizException(MetadataErrorCode.ACCESS_ERROR.getCode(), MetadataErrorCode.ACCESS_ERROR.getMessage());
        } finally {
            // close connection
            closeConnection(conn, preStatement, rs);
        }

        return 1;
    }

    /**
     * 查询路由源端和终端数据库连接信息
     *
     * @param jobDefineEntity
     * @return
     */
    @Override
    public SourceRunnerParam querySrcAndTarLinkByRouteId(JobDefineEntity jobDefineEntity) {
        SourceRunnerParam sourceRunnerParam = metaDatarouteMapper.querySrcAndTarLinkByRouteId(jobDefineEntity.getRouteId());
        sourceRunnerParam.setTarSchemaId(jobDefineEntity.getTargetSchemaId());
        MetaConnLinksEntity linkEntity = metaConnLinksMapper.queryTargetLinkByRouteId(jobDefineEntity.getRouteId());
        if (null != linkEntity) {
            sourceRunnerParam.setTarDbName(linkEntity.getDbName());
        }

        return sourceRunnerParam;
    }

    /**
     * 元数据同步---数据库建表
     *
     * @param job
     * @throws Exception
     */
    @Override
    public void loadMetadataForJob(JobDefineEntity job) throws Exception {
        // 1、先根据src删除对应的数据库、表、列、索引对应的元数据信息
        long srcId = job.getSourceId();
        JobLogger.log("Query meta connection information, srcId={}.", srcId);
        MetaConnLinksEntity linkEntity = metaConnLinksMapper.queryLinkBySourceId(srcId);
        if (null == linkEntity) {
            throw new BizException(MetadataErrorCode.CONNECTION_NULL_ERROR.getMessage());
        }

        // query sync table list
        JobSyncTableEntity param = new JobSyncTableEntity();
        param.setJobId(job.getJobId());
        JobLogger.log("Query meta sync table information, jobId={}.", job.getJobId());
        List<JobSyncTableEntity> syncList = jobSyncTableMapper.query(param);
        if (null == syncList || syncList.size() <= 0) {
            throw new BizException(ApplicationErrorCode.SYNC_TABLE_NULL.getMessage());
        }

        JobLogger.log("Delete if exists old source data information.");
        metaMysqlDatabasesMapper.delete(srcId, syncList);
        metaMysqlTablesMapper.delete(srcId, syncList);
        metaMysqlColumnsMapper.deleteBatch(srcId, syncList);
        metaMysqlStatisticsMapper.delete(srcId, syncList);

        // 2、重新从源端加载元数据信息
        JobLogger.log("Open the source database connection.");
        Connection conn = JDBCManager.factory(JDBCManager.MYSQL).getConnection(linkEntity, JDBCManager.INFORMATION_SCHEMA);

        // 加载-数据库
        JobLogger.log("Source database - load database information begin.");
        loadDatabase(conn, srcId, syncList);
        JobLogger.log("Source database - load database information end.");

        // 加载-表
        JobLogger.log("Source database - load database table information begin.");
        loadTables(conn, srcId, syncList);
        JobLogger.log("Source database - load database table information end.");

        // 加载-列
        JobLogger.log("Source database - load database column information begin.");
        loadColumns(conn, srcId, syncList);
        JobLogger.log("Source database - load database column information end.");

        // 加载-索引
        JobLogger.log("Source database - load database index information begin.");
        loadIndexs(conn, srcId, syncList);
        JobLogger.log("Source database - load database index information end.");

        if (conn != null) {
            JobLogger.log("Close the database connection.");
            conn.close();
        }

        int resNum = metaConnLinksMapper.updateDBLinkFlag(linkEntity);
        if (resNum <= 0) {
            throw new InvalidRequestException(ApplicationErrorCode.UPDATE_ERROR.getCode(),
                    ApplicationErrorCode.UPDATE_ERROR.getMessage());
        }
        JobLogger.log("Update connection information flag.");
    }

    /**
     * 加载-数据库信息写到mysql
     *
     * @param conn
     * @throws SQLException
     */
    private void loadDatabase(Connection conn, long srcId, List<JobSyncTableEntity> syncList) throws SQLException {
        List<MetaMysqlDatabasesEntity> dbList = new ArrayList<>();

        StringBuffer sqlBuff = new StringBuffer();
        sqlBuff.append("select CATALOG_NAME, SCHEMA_NAME, DEFAULT_CHARACTER_SET_NAME, DEFAULT_COLLATION_NAME, SQL_PATH from `SCHEMATA` WHERE SCHEMA_NAME in (");
        int syncSize = syncList.size();
        for (int i = 0; i < syncSize; i++) {
            JobSyncTableEntity sync = syncList.get(i);
            sqlBuff.append("'").append(sync.getDbName()).append("'");
            if (i != syncSize - 1) {
                sqlBuff.append(" , ");
            }
        }
        sqlBuff.append(")").append(" AND SCHEMA_NAME NOT IN ('mysql', 'information_schema', 'sys', 'performance_schema')");

        PreparedStatement preStatement = conn.prepareStatement(sqlBuff.toString());
        ResultSet rs = preStatement.executeQuery();
        while (rs.next()) {
            MetaMysqlDatabasesEntity dbInfoEntity = new MetaMysqlDatabasesEntity();
            dbInfoEntity.setSrcId(srcId);
            dbInfoEntity.setCatalogName(rs.getString("CATALOG_NAME"));
            dbInfoEntity.setSchemaName(rs.getString("SCHEMA_NAME"));
            dbInfoEntity.setDefaultCharacterSetName(rs.getString("DEFAULT_CHARACTER_SET_NAME"));
            dbInfoEntity.setDefaultCollationName(rs.getString("DEFAULT_COLLATION_NAME"));
            dbInfoEntity.setSqlPath(rs.getString("SQL_PATH"));
            dbList.add(dbInfoEntity);
        }

        // close connection
        closeConnection(preStatement, rs);

        if (null != dbList && dbList.size() > 0) {
            int resNum = metaMysqlDatabasesMapper.saveBatch(dbList);
            if (resNum <= 0) {
                throw new InvalidRequestException(ApplicationErrorCode.CREATE_ERROR.getCode(),
                        ApplicationErrorCode.CREATE_ERROR.getMessage());
            }
        }

        dbList = null;
    }

    /**
     * 加载-表信息
     *
     * @param conn
     * @throws SQLException
     */
    private void loadTables(Connection conn, long srcId, List<JobSyncTableEntity> syncList) throws SQLException {
        List<MetaMysqlTablesEntity> tableList = new ArrayList<>();

        StringBuffer sqlBuff = new StringBuffer();
        sqlBuff.append("select TABLE_CATALOG, TABLE_SCHEMA, TABLE_NAME, TABLE_TYPE, ENGINE, VERSION, ROW_FORMAT, TABLE_ROWS, AVG_ROW_LENGTH,")
                .append("DATA_LENGTH, MAX_DATA_LENGTH, INDEX_LENGTH, DATA_FREE, AUTO_INCREMENT, CREATE_TIME, UPDATE_TIME, CHECK_TIME,")
                .append("TABLE_COLLATION, CHECKSUM, CREATE_OPTIONS, TABLE_COMMENT from `TABLES` WHERE ");
        int syncSize = syncList.size();
        for (int i = 0; i < syncSize; i++) {
            JobSyncTableEntity sync = syncList.get(i);
            sqlBuff.append("(TABLE_TYPE = 'BASE TABLE' AND TABLE_SCHEMA = '").append(sync.getDbName()).append("'")
                    .append(" AND TABLE_NAME = '").append(sync.getTableName()).append("')");
            if (i != syncSize - 1) {
                sqlBuff.append(" or ");
            }
        }

        PreparedStatement preStatement = conn.prepareStatement(sqlBuff.toString());
        ResultSet rs = preStatement.executeQuery();
        while (rs.next()) {
            MetaMysqlTablesEntity tableEntity = new MetaMysqlTablesEntity();
            tableEntity.setSrcId(Long.valueOf(String.valueOf(srcId)));
            tableEntity.setTableCatalog(rs.getString("TABLE_CATALOG"));
            tableEntity.setTableSchema(rs.getString("TABLE_SCHEMA"));
            tableEntity.setTableName(rs.getString("TABLE_NAME"));
            tableEntity.setTableType(rs.getString("TABLE_TYPE"));
            tableEntity.setEngine(rs.getString("ENGINE"));
            tableEntity.setVersion(rs.getLong("VERSION"));
            tableEntity.setRowFormat(rs.getString("ROW_FORMAT"));
            tableEntity.setTableRows(rs.getLong("TABLE_ROWS"));
            tableEntity.setAvgRowLength(rs.getLong("AVG_ROW_LENGTH"));
            tableEntity.setDataLength(rs.getLong("DATA_LENGTH"));
            tableEntity.setMaxDataLength(rs.getLong("MAX_DATA_LENGTH"));
            tableEntity.setIndexLength(rs.getLong("INDEX_LENGTH"));
            tableEntity.setDataFree(rs.getLong("DATA_FREE"));
            tableEntity.setAutoIncrement(rs.getLong("AUTO_INCREMENT"));
            tableEntity.setCreateTime(rs.getTimestamp("CREATE_TIME"));
            tableEntity.setUpdateTime(rs.getTimestamp("UPDATE_TIME"));
            tableEntity.setCheckTime(rs.getTimestamp("CHECK_TIME"));
            tableEntity.setTableCollation(rs.getString("TABLE_COLLATION"));
            tableEntity.setChecksum(rs.getLong("CHECKSUM"));
            tableEntity.setCreateOptions(rs.getString("CREATE_OPTIONS"));
            tableEntity.setTableComment(rs.getString("TABLE_COMMENT"));
            tableList.add(tableEntity);
        }

        // close connection
        closeConnection(preStatement, rs);

        if (null != tableList && tableList.size() > 0) {
            int size = tableList.size();
            int factor = 1000;
            if ((size / factor) > 0) {
                int loop = size / factor + 1;
                int endIndex = 0;
                List<MetaMysqlTablesEntity> subList = null;
                for (int i = 0; i < loop; i++) {
                    if (i == (loop - 1)) {
                        endIndex = size;
                    } else {
                        endIndex = (i + 1) * factor;
                    }

                    subList = tableList.subList(i * factor, endIndex);
                    int resNum = metaMysqlTablesMapper.saveBatch(subList);
                    if (resNum <= 0) {
                        throw new InvalidRequestException(ApplicationErrorCode.CREATE_ERROR.getCode(),
                                ApplicationErrorCode.CREATE_ERROR.getMessage());
                    }
                }
            } else {
                int resNum = metaMysqlTablesMapper.saveBatch(tableList);
                if (resNum <= 0) {
                    throw new InvalidRequestException(ApplicationErrorCode.CREATE_ERROR.getCode(),
                            ApplicationErrorCode.CREATE_ERROR.getMessage());
                }
            }
        }

        tableList = null;
    }

    /**
     * 加载-列信息
     *
     * @param conn
     * @throws SQLException
     */
    private void loadColumns(Connection conn, long srcId, List<JobSyncTableEntity> syncList) throws Exception {
        List<MetaMysqlColumnsEntity> colList = new LinkedList<>();

        StringBuffer sqlBuff = new StringBuffer();
        sqlBuff.append("select TABLE_CATALOG, TABLE_SCHEMA, TABLE_NAME, COLUMN_NAME, ORDINAL_POSITION, COLUMN_DEFAULT, IS_NULLABLE, DATA_TYPE, ")
                .append("CHARACTER_MAXIMUM_LENGTH, CHARACTER_OCTET_LENGTH, NUMERIC_PRECISION, NUMERIC_SCALE, DATETIME_PRECISION, CHARACTER_SET_NAME,")
                .append("COLLATION_NAME, COLUMN_TYPE, COLUMN_KEY, EXTRA, PRIVILEGES, COLUMN_COMMENT, GENERATION_EXPRESSION from `COLUMNS` WHERE ");
        int syncSize = syncList.size();
        for (int i = 0; i < syncSize; i++) {
            JobSyncTableEntity sync = syncList.get(i);
            sqlBuff.append("(TABLE_SCHEMA = '").append(sync.getDbName()).append("'")
                    .append(" AND TABLE_NAME = '").append(sync.getTableName()).append("')");
            if (i != syncSize - 1) {
                sqlBuff.append(" or ");
            }
        }

        PreparedStatement preStatement = conn.prepareStatement(sqlBuff.toString());
        ResultSet rs = preStatement.executeQuery();
        while (rs.next()) {
            MetaMysqlColumnsEntity colEntity = new MetaMysqlColumnsEntity();
            colEntity.setSrcId(srcId);
            colEntity.setTableCatalog(rs.getString("TABLE_CATALOG"));
            colEntity.setTableSchema(rs.getString("TABLE_SCHEMA"));
            colEntity.setTableName(rs.getString("TABLE_NAME"));
            colEntity.setColumnName(rs.getString("COLUMN_NAME"));
            colEntity.setOrdinalPosition(rs.getLong("ORDINAL_POSITION"));
            colEntity.setColumnDefault(rs.getString("COLUMN_DEFAULT"));
            colEntity.setIsNullable(rs.getString("IS_NULLABLE"));
            colEntity.setDataType(rs.getString("DATA_TYPE"));
            colEntity.setCharacterMaximumLength(rs.getLong("CHARACTER_MAXIMUM_LENGTH"));
            colEntity.setCharacterOctetLength(rs.getLong("CHARACTER_OCTET_LENGTH"));
            colEntity.setNumericPrecision(rs.getLong("NUMERIC_PRECISION"));
            colEntity.setNumericScale(rs.getLong("NUMERIC_SCALE"));
            colEntity.setDatetimePrecision(rs.getLong("DATETIME_PRECISION"));
            colEntity.setCharacterSetName(rs.getString("CHARACTER_SET_NAME"));
            colEntity.setCollationName(rs.getString("COLLATION_NAME"));
            colEntity.setColumnType(rs.getString("COLUMN_TYPE"));
            colEntity.setColumnKey(rs.getString("COLUMN_KEY"));
            colEntity.setExtra(rs.getString("EXTRA"));
            colEntity.setPrivileges(rs.getString("PRIVILEGES"));
            colEntity.setColumnComment(new String(rs.getBytes("COLUMN_COMMENT"), "UTF-8"));
            colEntity.setGenerationExpression(rs.getString("GENERATION_EXPRESSION"));
            colList.add(colEntity);
        }

        // close connection
        closeConnection(preStatement, rs);

        if (null != colList && colList.size() > 0) {
            int size = colList.size();
            int factor = 1000;
            if ((size / factor) > 0) {
                int loop = size / factor + 1;
                int endIndex = 0;
                List<MetaMysqlColumnsEntity> subList = null;
                for (int i = 0; i < loop; i++) {
                    if (i == (loop - 1)) {
                        endIndex = size;
                    } else {
                        endIndex = (i + 1) * factor;
                    }

                    subList = colList.subList(i * factor, endIndex);
                    int resNum = metaMysqlColumnsMapper.saveBatch(subList);
                    if (resNum <= 0) {
                        throw new InvalidRequestException(ApplicationErrorCode.CREATE_ERROR.getCode(),
                                ApplicationErrorCode.CREATE_ERROR.getMessage());
                    }
                }
            } else {
                int resNum = metaMysqlColumnsMapper.saveBatch(colList);
                if (resNum <= 0) {
                    throw new InvalidRequestException(ApplicationErrorCode.CREATE_ERROR.getCode(),
                            ApplicationErrorCode.CREATE_ERROR.getMessage());
                }
            }
        }

        colList = null;
    }

    /**
     * 加载-索引信息
     *
     * @param conn
     * @throws SQLException
     */
    private void loadIndexs(Connection conn, long srcId, List<JobSyncTableEntity> syncList) throws SQLException {
        List<MetaMysqlStatisticsEntity> idxList = new ArrayList<>();

        StringBuffer sqlBuff = new StringBuffer();
        sqlBuff.append("select TABLE_CATALOG, TABLE_SCHEMA, TABLE_NAME, NON_UNIQUE, INDEX_SCHEMA, INDEX_NAME, SEQ_IN_INDEX, COLUMN_NAME,")
                .append("COLLATION, CARDINALITY, SUB_PART, PACKED, NULLABLE, INDEX_TYPE, COMMENT, INDEX_COMMENT from `STATISTICS` WHERE ");
        int syncSize = syncList.size();
        for (int i = 0; i < syncSize; i++) {
            JobSyncTableEntity sync = syncList.get(i);
            sqlBuff.append("(TABLE_SCHEMA = '").append(sync.getDbName()).append("'")
                    .append(" AND TABLE_NAME = '").append(sync.getTableName()).append("')");
            if (i != syncSize - 1) {
                sqlBuff.append(" or ");
            }
        }

        PreparedStatement preStatement = conn.prepareStatement(sqlBuff.toString());
        ResultSet rs = preStatement.executeQuery();
        while (rs.next()) {
            MetaMysqlStatisticsEntity idxEntity = new MetaMysqlStatisticsEntity();
            idxEntity.setSrcId(srcId);
            idxEntity.setTableCatalog(rs.getString("TABLE_CATALOG"));
            idxEntity.setTableSchema(rs.getString("TABLE_SCHEMA"));
            idxEntity.setTableName(rs.getString("TABLE_NAME"));
            idxEntity.setNonUnique(rs.getString("NON_UNIQUE"));
            idxEntity.setIndexSchema(rs.getString("INDEX_SCHEMA"));
            idxEntity.setIndexName(rs.getString("INDEX_NAME"));
            idxEntity.setSeqInIndex(rs.getLong("SEQ_IN_INDEX"));
            idxEntity.setColumnName(rs.getString("COLUMN_NAME"));
            idxEntity.setCollation(rs.getString("COLLATION"));
            idxEntity.setCardinality(rs.getLong("CARDINALITY"));
            idxEntity.setSubPart(rs.getLong("SUB_PART"));
            idxEntity.setPacked(rs.getString("PACKED"));
            idxEntity.setNullable(rs.getString("NULLABLE"));
            idxEntity.setIndexType(rs.getString("INDEX_TYPE"));
            idxEntity.setComment(rs.getString("COMMENT"));
            idxEntity.setIndexComment(rs.getString("INDEX_COMMENT"));
            idxList.add(idxEntity);
        }

        // close connection
        closeConnection(preStatement, rs);

        if (null != idxList && idxList.size() > 0) {
            int size = idxList.size();
            int factor = 1000;
            if ((size / factor) > 0) {
                int loop = size / factor + 1;
                int endIndex = 0;
                List<MetaMysqlStatisticsEntity> subList = null;
                for (int i = 0; i < loop; i++) {
                    if (i == (loop - 1)) {
                        endIndex = size;
                    } else {
                        endIndex = (i + 1) * factor;
                    }

                    subList = idxList.subList(i * factor, endIndex);
                    int saveNum = metaMysqlStatisticsMapper.saveBatch(subList);
                    if (saveNum <= 0) {
                        throw new InvalidRequestException(ApplicationErrorCode.CREATE_ERROR.getCode(),
                                ApplicationErrorCode.CREATE_ERROR.getMessage());
                    }
                }
            } else {
                int saveNum = metaMysqlStatisticsMapper.saveBatch(idxList);
                if (saveNum <= 0) {
                    throw new InvalidRequestException(ApplicationErrorCode.CREATE_ERROR.getCode(),
                            ApplicationErrorCode.CREATE_ERROR.getMessage());
                }
            }
        }

        idxList = null;
    }

    /**
     * 从源端更新列信息到mcp元库中
     *
     * @param conn
     * @param dbName
     * @param tableName
     * @param srcId
     * @throws MySQLIntegrityConstraintViolationException
     * @throws SQLException
     */
    @Override
    public void loadTablePreColumns(Connection conn, Long srcId, String dbName, String tableName) throws Exception {
        //先删除 单表
        metaMysqlColumnsMapper.delete(srcId, dbName, tableName);

        //查询最新列 再保存
        List<MetaMysqlColumnsEntity> colList = new ArrayList<>();
        String sql = "select TABLE_CATALOG, TABLE_SCHEMA, TABLE_NAME, COLUMN_NAME, ORDINAL_POSITION, COLUMN_DEFAULT, IS_NULLABLE, DATA_TYPE,"
                + "CHARACTER_MAXIMUM_LENGTH, CHARACTER_OCTET_LENGTH, NUMERIC_PRECISION, NUMERIC_SCALE, DATETIME_PRECISION, CHARACTER_SET_NAME,"
                + "COLLATION_NAME, COLUMN_TYPE, COLUMN_KEY, EXTRA, PRIVILEGES, COLUMN_COMMENT, GENERATION_EXPRESSION from information_schema.COLUMNS "
                + "WHERE TABLE_SCHEMA = '" + dbName + "' and TABLE_NAME='" + tableName + "'";

        PreparedStatement preStatement = conn.prepareStatement(sql);
        ResultSet rs = preStatement.executeQuery();
        while (rs.next()) {
            MetaMysqlColumnsEntity colEntity = new MetaMysqlColumnsEntity();
            colEntity.setSrcId(srcId.intValue());
            colEntity.setTableCatalog(rs.getString("TABLE_CATALOG"));
            colEntity.setTableSchema(rs.getString("TABLE_SCHEMA"));
            colEntity.setTableName(rs.getString("TABLE_NAME"));
            colEntity.setColumnName(rs.getString("COLUMN_NAME"));
            colEntity.setOrdinalPosition(rs.getLong("ORDINAL_POSITION"));
            colEntity.setColumnDefault(rs.getString("COLUMN_DEFAULT"));
            colEntity.setIsNullable(rs.getString("IS_NULLABLE"));
            colEntity.setDataType(rs.getString("DATA_TYPE"));
            colEntity.setCharacterMaximumLength(rs.getLong("CHARACTER_MAXIMUM_LENGTH"));
            colEntity.setCharacterOctetLength(rs.getLong("CHARACTER_OCTET_LENGTH"));
            colEntity.setNumericPrecision(rs.getLong("NUMERIC_PRECISION"));
            colEntity.setNumericScale(rs.getLong("NUMERIC_SCALE"));
            colEntity.setDatetimePrecision(rs.getLong("DATETIME_PRECISION"));
            colEntity.setCharacterSetName(rs.getString("CHARACTER_SET_NAME"));
            colEntity.setCollationName(rs.getString("COLLATION_NAME"));
            colEntity.setColumnType(rs.getString("COLUMN_TYPE"));
            colEntity.setColumnKey(rs.getString("COLUMN_KEY"));
            colEntity.setExtra(rs.getString("EXTRA"));
            colEntity.setPrivileges(rs.getString("PRIVILEGES"));
            colEntity.setColumnComment(rs.getString("COLUMN_COMMENT"));
            colEntity.setGenerationExpression(rs.getString("GENERATION_EXPRESSION"));
            colList.add(colEntity);
        }

        // close connection
        closeConnection(preStatement, rs);

        if (null != colList && colList.size() > 0) {
            int resNum = metaMysqlColumnsMapper.saveBatch(colList);
            if (resNum <= 0) {
                throw new InvalidRequestException(ApplicationErrorCode.CREATE_ERROR.getCode(), ApplicationErrorCode.CREATE_ERROR.getMessage());
            }

            colList.clear();
        }
    }

    /**
     * 根据routeId查询终端同步表
     *
     * @param jobId
     * @param srcDb
     * @param srcTable
     * @return
     */
    @Override
    public List<String> querySyncTableByJobId(Long jobId, String srcDb, String srcTable) {
        return jobSyncTableMapper.querySyncTableByJobId(jobId, srcDb, srcTable);
    }

    /**
     * close jdbc connection
     *
     * @param conn
     * @param pstmt
     * @param rs
     */
    private void closeConnection(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            closeConnection(pstmt, rs);

            if (null != conn) {
                conn.close();
            }
        } catch (SQLException e) {
            logger.error("JDBC connection close is exception!", e);
            throw new BizException(MetadataErrorCode.CONNECTION_CLOSE_ERROR.getCode(), MetadataErrorCode.CONNECTION_CLOSE_ERROR.getMessage());
        }
    }

    /**
     * close jdbc connection
     *
     * @param pstmt
     * @param rs
     * @throws SQLException
     */
    private void closeConnection(PreparedStatement pstmt, ResultSet rs) throws SQLException {
        if (null != rs) {
            rs.close();
        }

        if (null != pstmt) {
            pstmt.close();
        }
    }

}
