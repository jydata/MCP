package com.jiuye.mcp.server.service.meta.impl;

import com.jiuye.mcp.datasource.PhoenixDataSourceV2;
import com.jiuye.mcp.druid.phoenix.parser.PhoenixStatementParser;
import com.jiuye.mcp.exception.BizException;
import com.jiuye.mcp.exception.InvalidRequestException;
import com.jiuye.mcp.job.log.JobLogger;
import com.jiuye.mcp.param.enums.ApplicationErrorCode;
import com.jiuye.mcp.param.enums.DeleteStatusEnum;
import com.jiuye.mcp.param.enums.MetaRuleTypeEnum;
import com.jiuye.mcp.server.dao.job.JobDefinitionMapper;
import com.jiuye.mcp.server.dao.meta.*;
import com.jiuye.mcp.server.dao.sys.SysDictMapper;
import com.jiuye.mcp.server.model.ValidateResult;
import com.jiuye.mcp.server.model.meta.*;
import com.jiuye.mcp.server.service.meta.IDdlService;
import com.jiuye.mcp.utils.DateUtil;
import com.jiuye.mcp.utils.SpringBeanFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.phoenix.schema.ColumnNotFoundException;
import org.apache.phoenix.schema.TableNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhonggang
 */
@Service
public class DdlServiceImpl implements IDdlService {

    private static final Logger logger = LoggerFactory.getLogger(DdlServiceImpl.class.getName());

    @Autowired
    private MetaConnLinksMapper metaConnLinksMapper;
    @Autowired
    private JobDefinitionMapper jobDefinitionMapper;
    @Autowired
    private MetaDdlRulesMapper metaDdlRulesMapper;
    @Autowired
    private MetaDatarouteMapper metaDatarouteMapper;
    @Autowired
    private MetaRulesMapper metaRulesMapper;
    @Autowired
    private MetaMysqlColumnsMapper metaMysqlColumnsMapper;
    @Autowired
    private MetaTargetDdlMapper metaTargetDdlMapper;
    @Autowired
    private MetaMysqlTablesMapper metaMysqlTablesMapper;
    @Autowired
    private MetaMysqlDatabasesMapper metaMysqlDatabasesMapper;
    @Autowired
    private MetaTargetSchemaMapper metaTargetSchemaMapper;
    @Autowired
    private SysDictMapper sysDictMapper;

    private static final String PRIMARY = "PRIMARY";
    private static final String PRIMARY_KEY = "PRIMARY KEY";
    private static final String WMS = "wms";

    /**
     * 判断mcp是否存在表
     *
     * @param param
     * @return
     */
    @Override
    public boolean existTables(List<Long> param) {
        for (long srcId : param) {
            int result = metaMysqlTablesMapper.count(srcId, null, null);
            if (result >= 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据sourceId查询对应的源端db
     *
     * @param sourceId
     * @return
     */
    @Override
    public List<String> querySourceDb(long sourceId) {
        List<String> sourceDBList = metaMysqlDatabasesMapper.querySchemaName(sourceId);
        if (null == sourceDBList || sourceDBList.size() <= 0) {
            return null;
        }

        sourceDBList.add(0, "All");
        return sourceDBList;
    }

    /**
     * 查询所有路由信息
     *
     * @param targetId
     * @param targetSchemaId
     * @return
     */
    @Override
    public List<MetaDatarouteEntity> queryRouteInfo(Long targetId, Long targetSchemaId) {
        List<MetaDatarouteEntity> routeList = metaDatarouteMapper.queryRouteInfoList(targetId);
        if (null == routeList || routeList.size() <= 0) {
            return null;
        }

        List<MetaDatarouteEntity> filterRouteList = new ArrayList<>();
        for (MetaDatarouteEntity routeInfo : routeList) {
            Long srcId = routeInfo.getSourceId();
            Long routeId = routeInfo.getRouteId();
            /**
             * 1、根据sourceId查询对应的源端db
             * 2、如果1返回为null,从路由列表移除
             */
            if (null == querySourceDb(srcId)) {
                filterRouteList.add(routeInfo);
                continue;
            }

            List<MetaDatarouteEntity> childrenList = new ArrayList<>();
            MetaDatarouteEntity routeEntity = new MetaDatarouteEntity();
            BeanUtils.copyProperties(routeInfo, routeEntity);

            // 根据routeId查询meta_ddl_rules表里src_schema_name下的count(src_table_name)
            List<MetaDdlRulesEntity> metaDdlRulesEntityList = metaDdlRulesMapper.queryTableInfoCount(routeId);
            MetaMysqlTablesEntity metaMysqlTablesEntity = new MetaMysqlTablesEntity();
            for (MetaDdlRulesEntity ddlInfo : metaDdlRulesEntityList) {
                String srcSchemaName = ddlInfo.getSrcSchemaName();
                routeEntity.setDbName(srcSchemaName);

                metaMysqlTablesEntity.setTableSchema(srcSchemaName);
                metaMysqlTablesEntity.setSrcId(srcId);
                // 根据srcId、srcSchemaName查询，对应表的数量
                MetaMysqlTablesEntity tableSrcInfo = metaMysqlTablesMapper.querySrcTableCount(metaMysqlTablesEntity);
                if (tableSrcInfo != null && ddlInfo.getTableCount() == tableSrcInfo.getTableCount()) {
                    MetaDdlRulesEntity metaDdlRulesEntity = new MetaDdlRulesEntity();
                    metaDdlRulesEntity.setRouteId(routeId);
                    metaDdlRulesEntity.setSrcSchemaName(srcSchemaName);
                    /**
                     * 根据routeId、srcSchemaName查询规则名称、数量
                     * DDL页面展示以db展现，对于同一个库不同表使用不同规则的情况，展示规则使用最多的规则
                     */
                    MetaDdlRulesEntity rulesEntity = metaDdlRulesMapper.queryRuleName(metaDdlRulesEntity);
                    if (null != rulesEntity) {
                        routeEntity.setRuleName(rulesEntity.getRuleName());
                    }

                    if (!childrenList.contains(routeEntity)) {
                        childrenList.add(routeEntity);
                    }
                } else {
                    routeInfo.setChildren(null);
                }
            }
            routeInfo.setChildren(childrenList);
        }

        // 过滤路由中没有源端数据库信息的记录
        if (null != filterRouteList && filterRouteList.size() > 0) {
            routeList.removeAll(filterRouteList);
        }

        return routeList;
    }

    /**
     * 根据表名称查询对应的元数据信息
     *
     * @param sourceId
     * @param schema
     * @param table
     * @return
     */
    @Override
    public List<DBTableEntity> queryTableMeta(Long sourceId, String schema, String table) {
        MetaMysqlTablesEntity metaMysqlTablesEntity = new MetaMysqlTablesEntity();
        metaMysqlTablesEntity.setSrcId(sourceId);
        metaMysqlTablesEntity.setTableSchema(schema);
        metaMysqlTablesEntity.setTableName(table);

        List<DBTableEntity> list = new ArrayList<>();
        MetaMysqlTablesEntity result = metaMysqlTablesMapper.query(metaMysqlTablesEntity);
        DBTableEntity dbTableEntity = new DBTableEntity();
        dbTableEntity.setSourceName(metaConnLinksMapper.queryDBTable(sourceId));
        dbTableEntity.setDatabase(result.getTableSchema());
        dbTableEntity.setTableName(result.getTableName());
        dbTableEntity.setEngine(result.getEngine());
        String tableCollation = result.getTableCollation();
        if (StringUtils.isNoneEmpty(tableCollation)) {
            dbTableEntity.setCharset(result.getTableCollation().split("_")[0]);
        } else {
            dbTableEntity.setCharset("");
        }
        dbTableEntity.setTableRows(result.getTableRows());
        dbTableEntity.setTableComment(result.getTableComment());
        list.add(dbTableEntity);

        return list;
    }

    /**
     * 查询所有数据库列表
     *
     * @param info
     * @return
     */
    @Override
    public List<MetaMysqlColumnsEntity> querySchemaInfo(String info) {
        Map param = new HashMap(16);
        if (StringUtils.isNoneEmpty(info)) {
            String[] buf = info.split("#");
            param.put("table_schema", buf[0]);
            param.put("table_name", buf[1]);
            param.put("src_id", buf[2]);
        }

        return metaMysqlColumnsMapper.queryAllColumns(param);
    }

    /**
     * 查询所有数据库列表
     * sourceName
     * DB
     * Table
     *
     * @param targetId
     * @return
     */
    @Override
    public List<DBTableInfoEntity> queryDBTableList(Long targetId) {
        // 根据终端id查询所有路由信息
        List<MetaDatarouteEntity> entities = metaDatarouteMapper.queryRouteInfoList(targetId);
        if (null == entities) {
            return null;
        }

        List<DBTableInfoEntity> list = new ArrayList<>();
        entities.parallelStream().forEach(route -> {
            DBTableInfoEntity dbtableEntity = new DBTableInfoEntity();
            dbtableEntity.setId(route.getSourceId() + "#" + route.getRouteId());
            dbtableEntity.setLabel(route.getSourceName());

            List<MetaMysqlTablesEntity> tableList = metaMysqlTablesMapper.queryList(route.getSourceId());
            // 过滤没有表的库
            if (tableList == null || tableList.size() <= 0) {
                return;
            }

            dbtableEntity.setIsleaf(false);
            // 从源端表获取所有数据库
            Set<String> schemaSet = new HashSet<>();
            tableList.parallelStream().forEach(schema -> {
                String tableSchema = schema.getTableSchema();
                schemaSet.add(tableSchema);
            });
            if (null == schemaSet || schemaSet.size() <= 0) {
                return;
            }

            List<DBTableInfo> dblist = new ArrayList<>();
            // 得到database
            schemaSet.forEach(schema -> {
                DBTableInfo dbTableInfo = new DBTableInfo();
                dbTableInfo.setIsleaf(false);
                dbTableInfo.setId(route.getSourceName() + "#" + schema);
                dbTableInfo.setLabel(schema);

                List<DBTableInfo> leafList = new ArrayList<>();
                // 获取库下面所有表信息，作为叶子节点
                tableList.forEach(table -> {
                    String tableSchema = table.getTableSchema();
                    String tableName = table.getTableName();

                    DBTableInfo leafInfo = new DBTableInfo();
                    leafInfo.setIsleaf(true);
                    leafInfo.setId(schema + "#" + tableName);
                    leafInfo.setLabel(tableName);
                    if (null != tableSchema && schema.equals(tableSchema)) {
                        leafList.add(leafInfo);
                    }
                });

                dbTableInfo.setChildren(leafList);
                dblist.add(dbTableInfo);
            });
            dbtableEntity.setChildren(dblist);

            // 过滤没有表的数据库
            if (null != dblist && dblist.size() > 0) {
                list.add(dbtableEntity);
            }
        });

        return list;
    }

    /**
     * 单表生成phoenix建表SQL(Table, Index)
     *
     * @param list
     * @param flag     索引字段标识( 0-未勾选需要二级索引的字段， 1-勾选了需要二级索引字段)
     * @param routeId
     * @param schemaId
     * @param id
     * @param ruleName
     * @param srcDb
     * @return
     */
    @Override
    public MetaTargetDdlEntity genSingleTableSqlInfo(List<MetaMysqlColumnsEntity> list, String flag, long routeId, long schemaId, long id, String ruleName, String srcDb) {
        // 校验数据库是否有其他作业正在运行
        boolean result = checkJobRunning(routeId, 0L, 0L, srcDb);
        if (!result) {
            throw new BizException(ApplicationErrorCode.JOB_RUNNING.getCode(), ApplicationErrorCode.JOB_RUNNING.getMessage());
        }

        MetaRulesEntity rule = queryRuleByName(ruleName);
        if (null == rule) {
            return null;
        }

        // 获取list中的建表语句字段信息(单表-list中仅一个)
        MetaMysqlColumnsEntity colEntity = list.get(0);
        String tableSchema = colEntity.getTableSchema();
        String tableName = colEntity.getTableName();

        String ruleType = rule.getRuleType();
        String rulePrefix = rule.getRulePrefix();
        String ruleColumn = rule.getRuleColumn();
        String ruleSuffix = rule.getRuleSuffix();
        if (MetaRuleTypeEnum.MERGE_ADD_COLUMN.getCode().equals(ruleType) && rule.getDbFlag() == 1) {
            // 第三种规则，新增列，校验截取DB的长度
            if (!(rule.getBeginIndex() < srcDb.length() &&
                    rule.getEndIndex() <= srcDb.length() &&
                    rule.getBeginIndex() != rule.getEndIndex())) {
                throw new BizException(ApplicationErrorCode.TRANSFORM_ERROR.getCode(), ApplicationErrorCode.TRANSFORM_ERROR.getMessage());
            }
        } else if (MetaRuleTypeEnum.RESTORE.getCode().equals(ruleType)) {
            // 规则四，规则后缀不能为空，表名是否包含规则后缀
            if (StringUtils.isBlank(ruleSuffix)) {
                throw new BizException(ApplicationErrorCode.MISS_RULE_SUFFIX.getCode(), ApplicationErrorCode.MISS_RULE_SUFFIX.getMessage());
            }
            if (!tableName.contains(ruleSuffix)) {
                throw new BizException(ApplicationErrorCode.TALE_RULE_SUFFIX_NULL.getCode(), ApplicationErrorCode.TALE_RULE_SUFFIX_NULL.getMessage());
            }
        }

        // 根据routeId查询路由信息
        MetaDatarouteEntity dbEntity = metaDatarouteMapper.queryRouteById(routeId);
        // 根据schemaId查询schema信息
        MetaTargetSchemaEntity schemaEntity = metaTargetSchemaMapper.queryBySchemaId(schemaId);
        String schemaName = schemaEntity.getSchemaName();

        // 根据list中的库名、表名查询字段信息
        String tableInfo = tableSchema + "#" + tableName + "#" + dbEntity.getSourceId();
        List<MetaMysqlColumnsEntity> columnList = querySchemaInfo(tableInfo);

        // 校验表字段和关键字
        // 3:keyword
        List<String> keyWordList = sysDictMapper.queryDistinctName(3);
        // 4:unresolvable type
        List<String> typeList = sysDictMapper.queryDistinctName(4);
        ValidateResult validateResult = checkColumnInfo(columnList, keyWordList, typeList, 1);
        if (!validateResult.isFlag()) {
            throw new BizException(ApplicationErrorCode.TRANSFORM_ERROR.getCode(), validateResult.getMessage());
        }

        String tarTableName = tableName;
        if (MetaRuleTypeEnum.MERGE_ADD_COLUMN.getCode().equals(ruleType)) {
            // 针对规则三，添加字段用于标记库名，生成列，并作为联合主键
            // 将新增字段放置在schemaList中的第二个字段位置
            columnList.add(1, new MetaMysqlColumnsEntity(dbEntity.getSourceId(), tableSchema, tableName, ruleColumn));
        } else if (MetaRuleTypeEnum.RESTORE.getCode().equals(ruleType)) {
            // 规则四，截取表名后缀
            tarTableName = tableName.substring(0, tableName.indexOf(ruleSuffix));
        }

        // 生成表的普通表Table SQL
        String tableSql = generateTableSql(columnList, schemaName, ruleType, rulePrefix, tarTableName);
        // 生成表的索引表Index SQL, 去除索引2019-07-25
        /*String indexSql = "";
        if (flag.equals("1") || (flag.equals("0") && PRIMARY_KEY.equals(tableSql))) {
            indexSql = generateIndexSql(list, tableSql, schemaName, flag, ruleType, rulePrefix);
        }

        return new MetaTargetDdlEntity(id, 0L, dbEntity.getRouteName(), schemaId, tableSchema, tableName, schemaName, tableName.toUpperCase(), tableName.toUpperCase() + "_IDX", tableSql, indexSql);*/
        return new MetaTargetDdlEntity(id, 0L, dbEntity.getRouteName(), schemaId, tableSchema, tableName, schemaName, tarTableName.toUpperCase(), null, tableSql, null);
    }

    /**
     * 获取建表DDL信息
     *
     * @param entity
     * @throws Exception
     */
    @Override
    public MetaTargetDdlEntity queryDDLInfo(MetaTargetDdlEntity entity) {
        List<MetaTargetDdlEntity> list = metaTargetDdlMapper.queryList(entity);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 单表保存MySQL对应的HBase SQL
     *
     * @param sqlEntity
     * @param userName
     * @return
     * @throws BizException
     */
    @Override
    public MetaTargetDdlEntity saveHBaseSQL(MetaTargetDdlEntity sqlEntity, String userName) throws BizException {
        String formatDate = DateUtil.getFormatDate(DateUtil.DF_YYYYMMDD_HHMMSS);
        sqlEntity.setId(0);
        sqlEntity.setCreateUser(userName);
        sqlEntity.setExecuteFlag(0);
        sqlEntity.setCreateTime(formatDate);
        sqlEntity.setUpdateUser(userName);
        sqlEntity.setUpdateTime(formatDate);
        sqlEntity.setDdlSql(sqlEntity.getDdlSql().replace("unsigned", ""));

        // save
        int resNum = metaTargetDdlMapper.save(sqlEntity);
        if (resNum <= 0) {
            throw new BizException(ApplicationErrorCode.CREATE_ERROR.getCode(), ApplicationErrorCode.CREATE_ERROR.getMessage());
        }

        return sqlEntity;
    }

    /**
     * 批量保存MySQL对应的HBase SQL
     *
     * @param tarDdlList
     * @param userName
     * @return
     * @throws BizException
     */
    private List<MetaTargetDdlEntity> batchSaveTargetDdl(List<MetaTargetDdlEntity> tarDdlList, String userName) throws BizException {
        String formatDate = DateUtil.getFormatDate(DateUtil.DF_YYYYMMDD_HHMMSS);
        if (null != tarDdlList && tarDdlList.size() > 0) {
            tarDdlList.parallelStream().forEach(tarDdlEntity -> {
                tarDdlEntity.setId(0);
                tarDdlEntity.setCreateUser(userName);
                tarDdlEntity.setExecuteFlag(0);
                tarDdlEntity.setCreateTime(formatDate);
                tarDdlEntity.setUpdateUser(userName);
                tarDdlEntity.setUpdateTime(formatDate);
                tarDdlEntity.setDdlSql(tarDdlEntity.getDdlSql().replace("unsigned", ""));
            });
        }

        int saveNum = metaTargetDdlMapper.saveBatch(tarDdlList);
        if (saveNum <= 0) {
            throw new BizException(ApplicationErrorCode.CREATE_ERROR.getCode(), ApplicationErrorCode.CREATE_ERROR.getMessage());
        }

        return tarDdlList;
    }

    /**
     * 单表保存返回实体类
     *
     * @param sqlEntity
     * @param userName
     * @param srcDb
     * @param routeId
     * @return
     */
    @Override
    public MetaTargetDdlEntity saveTargetTableSQL(MetaTargetDdlEntity sqlEntity, String userName, String srcDb, long routeId) {
        boolean result = checkJobRunning(routeId, 0L, 0L, srcDb);
        if (!result) {
            throw new BizException(ApplicationErrorCode.JOB_RUNNING.getCode(), ApplicationErrorCode.JOB_RUNNING.getMessage());
        }

        String formatDate = DateUtil.getFormatDate(DateUtil.DF_YYYYMMDD_HHMMSS);
        sqlEntity.setId(0);
        sqlEntity.setCreateUser(userName);
        sqlEntity.setCreateTime(formatDate);
        sqlEntity.setUpdateUser(userName);
        sqlEntity.setUpdateTime(formatDate);
        sqlEntity.setDdlSql(sqlEntity.getDdlSql().replace("unsigned", ""));

        // phoenix场合才进行database、table解析
        if (!sqlEntity.getDdlSql().contains("USING iceberg")) {
            String phoenixTable = getDdlTableName(sqlEntity.getDdlSql());
            String[] dbTbArray = phoenixTable.split("\\.");
            if (dbTbArray.length > 2 || dbTbArray.length == 0) {
                throw new RuntimeException("Phoenix Table Parse Exception.");
            }
            if (dbTbArray.length == 1) {
                sqlEntity.setTargetDbName(phoenixTable.split("\\.")[0]);
            }
            if (dbTbArray.length == 2) {
                sqlEntity.setTargetDbName(phoenixTable.split("\\.")[0]);
                sqlEntity.setTargetTableName(phoenixTable.split("\\.")[1]);
            }
        }

        sqlEntity.setModifyDdlFlag(1);
        // save
        int resNum = metaTargetDdlMapper.save(sqlEntity);
        if (resNum <= 0) {
            throw new BizException(ApplicationErrorCode.CREATE_ERROR.getCode(), ApplicationErrorCode.CREATE_ERROR.getMessage());
        }

        MetaTargetDdlEntity sqlInfoEntity = metaTargetDdlMapper.queryList(sqlEntity).get(0);
        return sqlInfoEntity;
    }

    /**
     * 根据输入的SQL处理解析获取 db.tb
     *
     * @param inputSql
     * @return
     */
    private String getDdlTableName(String inputSql) {
        PhoenixStatementParser parser = new PhoenixStatementParser(inputSql);
        String tableName = parser.getSQLCreateTableParser().parseCreateTable().getTableSource().toString();
        return tableName;
    }

    /**
     * HBase SQL单表同步
     *
     * @param list
     * @param srcId
     * @param ruleName
     * @param srcDb
     * @param routeId
     * @param targetSchemaId
     * @return
     * @throws SQLException
     */
    @Override
    public ValidateResult execSQL(List<MetaTargetDdlEntity> list, long srcId, String ruleName, String srcDb, long routeId, long targetSchemaId) throws SQLException {
        if (null == list && list.size() <= 0) {
            return new ValidateResult(false, "DDL synchronization parameter error ！");
        }

        MetaTargetDdlEntity targetDdlEntity = metaTargetDdlMapper.queryById(list.get(0).getId());
        String srcDbName = targetDdlEntity.getSrcDbName();
        String srcTableName = targetDdlEntity.getSrcTableName();
        String tarDbName = targetDdlEntity.getTargetDbName();
        String tarTableName = targetDdlEntity.getTargetTableName();
        String ddlSql = targetDdlEntity.getDdlSql();

        // 查询meta table是否存在
        int count = metaMysqlTablesMapper.count(srcId, srcDbName, srcTableName);
        if (count == 0) {
            return new ValidateResult(false, "The table is not saved by conversion ！");
        }

        ValidateResult validateResult = null;
        // 校验是否需要建表(针对wms库),若不匹配数据库中建表信息则创建
        if (srcDbName.contains(WMS)) {
            validateResult = checkTargetDdlExist(tarDbName, tarTableName, targetDdlEntity.getSchemaId(), ddlSql);
            if (!validateResult.isFlag()) {
                return validateResult;
            }
        }

        Connection conn = null;
        Statement stmt = null;
        try {
            // 获取终端连接
            conn = SpringBeanFactory.getBean(PhoenixDataSourceV2.class).phoenixJdbcConn();
            stmt = conn.createStatement();

            /**
             * 校验能否进行同步
             * 1.校验是否有作业在同步表
             * 2.校验ddl规则是否
             *
             * syncFlag 1:单表，2：批量
             */
            validateResult = checkSyncDDL(stmt, tarDbName, 0L, srcId, srcDb, srcTableName, routeId, 1);
            if (!validateResult.isFlag()) {
                return validateResult;
            }

            // 保存对应的映射关系到meta_ddl_rules
            validateResult = saveDdlRules(ruleName, targetDdlEntity, routeId, srcDb, targetSchemaId);
            if (!validateResult.isFlag()) {
                return validateResult;
            }

            // 先清除stmt对象的sql对象列表
            stmt.clearBatch();
            // 通过phoenix连接，执行sql建表
            if (StringUtils.isNotEmpty(ddlSql)) {
                stmt.execute(ddlSql);
            }

            // 终端建表成功后,更新ddl表同步标记
            int resNum = metaTargetDdlMapper.updateExecuteFlag(targetDdlEntity);
            if (resNum <= 0) {
                throw new InvalidRequestException(ApplicationErrorCode.UPDATE_ERROR.getCode(), ApplicationErrorCode.UPDATE_ERROR.getMessage());
            }
        } catch (SQLException e) {
            logger.error("Single table synchronization error", e);
            // 恢复被删除的表数据
            stmt.clearBatch();
            MetaTargetDdlEntity tarDdlEntity = metaTargetDdlMapper.query(targetDdlEntity);
            if (null == tarDdlEntity) {
                return new ValidateResult(false, "Check for keywords in SQL execution statements and selected rules！");
            }
            // 当SQL语句中存在关键词，删除原有表，但建立新表失败，恢复原有表
            if (StringUtils.isNotBlank(tarDdlEntity.getDdlSql())) {
                stmt.execute(tarDdlEntity.getDdlSql());
            }

            return new ValidateResult(false, "Check for keywords in SQL execution statements and selected rules！");
        } catch (Exception exe) {
            logger.error("Single table synchronization error", exe);
            return new ValidateResult(false, "Check for keywords in SQL execution statements and selected rules！");
        } finally {
            try {
                if (null != stmt) {
                    stmt.close();
                }
                if (null != conn) {
                    conn.close();
                }
            } catch (SQLException se) {
                logger.error("Close database connection failed！", se);
            }
        }

        return new ValidateResult(true, "Successful synchronization");
    }

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
    @Override
    public ValidateResult batchExecSQL(Long routeId, Long jobId, Long srcId, String srcDb, Long targetId, Long targetSchemaId, Long ruleId, String userName) {
        if (srcDb == null || StringUtils.isEmpty(srcDb)) {
            return new ValidateResult(false, "Parameter error, source library cannot be empty ！");
        }

        MetaRulesEntity param = new MetaRulesEntity();
        param.setRuleId(ruleId);
        param.setRuleStatus(DeleteStatusEnum.NO.getCode());
        MetaRulesEntity rule = metaRulesMapper.query(param);
        if (null == rule) {
            return new ValidateResult(false, "Rule not queried！");
        }

        // 根据终端查询数据库、表结构
        List<DBTableInfoEntity> sourceNameDbTableList = queryDBTableList(targetId);
        if (null == sourceNameDbTableList || sourceNameDbTableList.size() <= 0) {
            return new ValidateResult(false, "Target parameter error！");
        }

        // 匹配路由的数据库、表
        DBTableInfoEntity dbTableInfoEntity = new DBTableInfoEntity();
        for (DBTableInfoEntity entity : sourceNameDbTableList) {
            if (entity.getId().split("#")[1].equals(String.valueOf(routeId))) {
                dbTableInfoEntity = entity;
                break;
            }
        }
        List<DBTableInfo> dbTableList = dbTableInfoEntity.getChildren();
        if (null == dbTableList && dbTableList.size() <= 0) {
            return new ValidateResult(false, "No table exists under the source！");
        }

        String[] srcDbList = null;
        if (!srcDb.contains("#")) {
            // 源端单库或ALL操作
            srcDbList = new String[]{srcDb};
        } else {
            // wmsb01,wmsb02,wmsb03
            srcDbList = srcDb.split("#");
        }

        for (String sourceDb : srcDbList) {
            // 批量转换
            List<MetaTargetDdlEntity> ddlList = new ArrayList<>();
            if ("All".equals(sourceDb)) {
                ddlList = batchGenerateDdlSQL(dbTableInfoEntity, rule, routeId, targetSchemaId);
            } else {
                for (DBTableInfo dbTableInfo : dbTableList) {
                    // 匹配当前db
                    if (dbTableInfo.getLabel().equals(sourceDb)) {
                        DBTableInfoEntity infoEntity = new DBTableInfoEntity();
                        List<DBTableInfo> dbTableEntityList = new ArrayList<>();
                        dbTableEntityList.add(dbTableInfo);
                        infoEntity.setChildren(dbTableEntityList);
                        // 生成ddl
                        ddlList = batchGenerateDdlSQL(infoEntity, rule, routeId, targetSchemaId);
                    }
                }
            }
            if (null == ddlList && ddlList.size() <= 0) {
                JobLogger.log("DDL transform fail, {} - batch conversion ddl sql is empty.", sourceDb);
                continue;
            }

            // 批量保存target ddl
            List<MetaTargetDdlEntity> tarDdlList = batchSaveTargetDdl(ddlList, userName);
            if (null == tarDdlList && tarDdlList.size() <= 0) {
                return new ValidateResult(false, "DDL batch save failed！");
            }

            ValidateResult execResult = null;
            try {
                // 第一二种规则或者第三种规则且第一个库正常执行，否则只保存映射关系
                if ((MetaRuleTypeEnum.MERGE_ADD_COLUMN.getCode().equals(rule.getRuleType()) && srcDbList[0].equals(sourceDb))
                        || !MetaRuleTypeEnum.MERGE_ADD_COLUMN.getCode().equals(rule.getRuleType())) {
                    // 批量同步、保存映射关系
                    execResult = multiExecSQL(tarDdlList, rule, jobId, srcId, sourceDb, routeId, targetSchemaId, userName);
                } else {
                    // 仅保存映射关系
                    execResult = multiSaveSQLRules(tarDdlList, rule, routeId, targetSchemaId);
                }

                if (!execResult.isFlag()) {
                    return execResult;
                }
            } catch (SQLException e) {
                logger.error("Batch sync error", e);
                return new ValidateResult(false, "Batch sync error！");
            }
        }

        return new ValidateResult(true, null);
    }

    /**
     * 批量保存ddl rules 关系
     *
     * @param ruleName
     * @param targetDdlEntity
     * @param routeId
     * @param srcDb
     * @param targetSchemaId
     * @return
     */
    private ValidateResult saveDdlRules(String ruleName, MetaTargetDdlEntity targetDdlEntity, long routeId, String srcDb, long targetSchemaId) {
        ValidateResult validateResult = new ValidateResult(true, null);

        MetaRulesEntity ruleEntity = queryRuleByName(ruleName);
        if (null == ruleEntity) {
            return new ValidateResult(false, "No rules were queried !");
        }
        String ruleType = ruleEntity.getRuleType();
        String rulePrefix = ruleEntity.getRulePrefix();

        // 规则三、前缀名称
        String prefixName = "";
        if (!MetaRuleTypeEnum.DEFAULT.getCode().equals(ruleType) && StringUtils.isNotEmpty(rulePrefix)) {
            prefixName = rulePrefix.toUpperCase() + "_";
        }

        List<MetaDdlRulesEntity> metaDdlRulesEntityList = new ArrayList<>();
        MetaDdlRulesEntity ddlRulesEntity = new MetaDdlRulesEntity();
        ddlRulesEntity.setRouteId(routeId);
        ddlRulesEntity.setSrcSchemaName(srcDb);
        ddlRulesEntity.setSrcTableName(targetDdlEntity.getSrcTableName());
        ddlRulesEntity.setTargetSchemaId(targetSchemaId);
        ddlRulesEntity.setTargetSchemaName(targetDdlEntity.getTargetDbName());
        ddlRulesEntity.setTargetTableName(getTargetTableNameByTargetDdl(targetDdlEntity, prefixName));
        ddlRulesEntity.setRuleId(ruleEntity.getRuleId());
        ddlRulesEntity.setRuleName(ruleName);
        ddlRulesEntity.setRuleColumn(ruleEntity.getRuleColumn().toUpperCase());
        ddlRulesEntity.setRuleType(ruleType);
        ddlRulesEntity.setRulePrefix(rulePrefix);
        ddlRulesEntity.setDbFlag(ruleEntity.getDbFlag());
        if (MetaRuleTypeEnum.MERGE_ADD_COLUMN.getCode().equals(ruleType) && ruleEntity.getDbFlag() == 1) {
            ddlRulesEntity.setSrcSchemaColumn(targetDdlEntity.getSrcDbName().substring(ruleEntity.getBeginIndex(), ruleEntity.getEndIndex()).toUpperCase());
        }
        ddlRulesEntity.setBeginIndex(ruleEntity.getBeginIndex());
        ddlRulesEntity.setEndIndex(ruleEntity.getEndIndex());
        ddlRulesEntity.setCreateUser(ruleEntity.getCreateUser());
        metaDdlRulesEntityList.add(ddlRulesEntity);

        // 批量保存
        int saveFlag = metaDdlRulesMapper.saveBatch(metaDdlRulesEntityList);
        if (saveFlag == 0) {
            validateResult = new ValidateResult(false, "DDL save rule failed");
        }

        return validateResult;
    }

    /**
     * create by dove
     * 获取目标表名，若手工修改过(modifyddlflag == 1),直接引用metaTargetDdl的目标表名
     *
     * @param targetDdlEntity
     * @param prefixName
     * @return
     */
    private String getTargetTableNameByTargetDdl(MetaTargetDdlEntity targetDdlEntity, String prefixName) {
        if (targetDdlEntity.getModifyDdlFlag() == 1) {
            return targetDdlEntity.getTargetTableName();
        }
        return (prefixName + targetDdlEntity.getTargetTableName()).toUpperCase();
    }
    /**
     * 根据ruleName查询规则
     *
     * @param ruleName
     * @return
     */
    private MetaRulesEntity queryRuleByName(String ruleName) {
        MetaRulesEntity param = new MetaRulesEntity();
        param.setRuleName(ruleName);
        param.setRuleStatus(DeleteStatusEnum.NO.getCode());

        return metaRulesMapper.query(param);
    }

    /**
     * 校验终端是否存在表
     *
     * @param tarDbName
     * @param tarTableName
     * @param schemaId
     * @param ddlSql
     * @return
     */
    private ValidateResult checkTargetDdlExist(String tarDbName, String tarTableName, Long schemaId, String ddlSql) {
        Map<String, String> sqlMap = new HashMap();
        List<MetaTargetDdlEntity> sqlInfoEntities = metaTargetDdlMapper.querySqlList(schemaId, tarTableName.toUpperCase());
        for (MetaTargetDdlEntity sqlInfo : sqlInfoEntities) {
            sqlMap.put(sqlInfo.getTargetDbName() + "#" + sqlInfo.getTargetTableName(), sqlInfo.getDdlSql());
        }

        if (null != sqlMap && sqlMap.size() > 0 && sqlMap.get(tarDbName + "#" + tarTableName).equals(ddlSql)) {
            return new ValidateResult(true, "The table already exists in the library ！");
        }

        return new ValidateResult(true, null);
    }

    /**
     * (批量同步)基于MySQL对应的HBase SQL 进行相应的表创建
     *
     * @param ruleEntity
     * @param jobId
     * @param srcId
     * @param srcDb
     * @param routeId
     * @param targetSchemaId
     * @param userName
     * @return
     * @throws SQLException
     */
    private ValidateResult multiExecSQL(List<MetaTargetDdlEntity> tarDdlList, MetaRulesEntity ruleEntity, long jobId, long srcId, String srcDb, long routeId, Long targetSchemaId, String userName) throws SQLException {
        // 记录出现关键字问题的SQL
        String sqlKeyWord = "";
        // 记录ddl建表语句在ddlList中的位置
        int ddlIndex = 0;

        // 校验是否需要建表(针对wms库),若不匹配数据库中建表信息则创建,避免查数据、删表、建表操作
        Map<String, String> sqlMap = new HashMap<>();
        if (srcDb.contains(WMS)) {
            List<MetaTargetDdlEntity> existsTarDdlList = metaTargetDdlMapper.querySqlList(targetSchemaId, null);
            existsTarDdlList.forEach(tarDdl -> {
                sqlMap.put(tarDdl.getTargetDbName() + "#" + tarDdl.getTargetTableName(), tarDdl.getDdlSql());
            });

            List<MetaTargetDdlEntity> cloneDDLList = new ArrayList<>();
            tarDdlList.forEach(ddlEntity -> {
                String temp = sqlMap.get(ddlEntity.getTargetDbName() + "#" + ddlEntity.getTargetTableName());
                if (temp != null && temp.equals(ddlEntity.getDdlSql())) {
                    cloneDDLList.add(ddlEntity);
                }
            });

            if (null != cloneDDLList && cloneDDLList.size() > 0) {
                tarDdlList.removeAll(cloneDDLList);
            }
        }

        Connection conn = null;
        Statement stmt = null;
        try {
            conn = SpringBeanFactory.getBean(PhoenixDataSourceV2.class).phoenixJdbcConn();
            stmt = conn.createStatement();

            // 校验能否同步, syncFlag 1:单表，2：批量
            ValidateResult result = checkSyncDDL(stmt, tarDdlList.get(0).getTargetDbName(), jobId, srcId, srcDb, null, routeId, 2);
            if (!result.isFlag()) {
                // 记录表是否存在表，表中是否有数据
                JobLogger.log("DDL transform fail, {}", result.getMessage());
            } else {
                sqlKeyWord = result.getMessage() + "\n";
            }

            // 清空当前statement对象的sql命令列表（sql类型要一致）
            stmt.clearBatch();

            String ruleType = ruleEntity.getRuleType();
            String rulePrefix = ruleEntity.getRulePrefix();
            String prefixName = "";
            if (!MetaRuleTypeEnum.DEFAULT.getCode().equals(ruleType) && StringUtils.isNotEmpty(rulePrefix)) {
                prefixName = rulePrefix.toUpperCase() + "_";
            }

            List<MetaDdlRulesEntity> metaDdlRulesEntityList = new ArrayList<>();
            for (MetaTargetDdlEntity sqlInfoEntity : tarDdlList) {
                String srcDbName = sqlInfoEntity.getSrcDbName();
                String srcTableName = sqlInfoEntity.getSrcTableName();
                String tarDbName = sqlInfoEntity.getTargetDbName();
                String tarTableName = sqlInfoEntity.getTargetTableName();
                String logDbInfo = srcDbName + "." + srcTableName + "->" + tarDbName + "." + tarTableName;
                try {
                    ddlIndex += 1;
                    //保存对应的映射关系到meta_ddl_rules
                    MetaDdlRulesEntity metaDdlRulesEntity = new MetaDdlRulesEntity();
                    metaDdlRulesEntity.setRouteId(routeId);
                    metaDdlRulesEntity.setSrcSchemaName(srcDbName);
                    metaDdlRulesEntity.setSrcTableName(srcTableName);
                    metaDdlRulesEntity.setTargetSchemaId(targetSchemaId);
                    metaDdlRulesEntity.setTargetSchemaName(tarDbName);
                    // 根据规则生成的终端表名称
                    metaDdlRulesEntity.setTargetTableName(getTargetTableNameByTargetDdl(sqlInfoEntity, prefixName));
                    metaDdlRulesEntity.setRuleId(ruleEntity.getRuleId());
                    metaDdlRulesEntity.setRuleName(ruleEntity.getRuleName());
                    metaDdlRulesEntity.setRuleColumn(ruleEntity.getRuleColumn().toUpperCase());
                    metaDdlRulesEntity.setRuleType(ruleType);
                    metaDdlRulesEntity.setRulePrefix(rulePrefix);
                    metaDdlRulesEntity.setDbFlag(ruleEntity.getDbFlag());
                    if (MetaRuleTypeEnum.MERGE_ADD_COLUMN.getCode().equals(ruleType) && ruleEntity.getDbFlag() == 1) {
                        metaDdlRulesEntity.setSrcSchemaColumn(srcDbName.substring(ruleEntity.getBeginIndex(), ruleEntity.getEndIndex()).toUpperCase());
                    }
                    metaDdlRulesEntity.setBeginIndex(ruleEntity.getBeginIndex());
                    metaDdlRulesEntity.setEndIndex(ruleEntity.getEndIndex());
                    metaDdlRulesEntity.setCreateUser(userName);
                    metaDdlRulesEntityList.add(metaDdlRulesEntity);

                    // 执行table sql,index sql
                    if (StringUtils.isNotBlank(sqlInfoEntity.getDdlSql())) {
                        stmt.execute(sqlInfoEntity.getDdlSql());
                    }

                    // 修改ddl同步信息
                    metaTargetDdlMapper.updateExecuteFlag(sqlInfoEntity);
                } catch (ColumnNotFoundException cnfe) {
                    logger.error("Errors in table-building of different rules in the same database and table！", cnfe);
                    JobLogger.log("DDL transform fail, [{}], Errors in table-building of different rules in the same database and table.", logDbInfo);
                    continue;
                } catch (SQLException e) {
                    logger.error("Check for keywords in SQL execution statements and selected rules！", e);
                    JobLogger.log("DDL transform fail, [{}], Check for keywords in SQL execution statements and selected rules.", logDbInfo);

                    // 恢复被删除的表数据
                    stmt.clearBatch();
                    for (MetaTargetDdlEntity sqlInfo : tarDdlList) {
                        MetaTargetDdlEntity sql = metaTargetDdlMapper.query(sqlInfo);
                        if (sql != null && StringUtils.isNotBlank(sql.getDdlSql())) {
                            stmt.execute(sql.getDdlSql());
                        }
                    }

                    MetaTargetDdlEntity sqlDDL = tarDdlList.get(ddlIndex - 1);
                    sqlKeyWord = sqlKeyWord + sqlDDL.getDdlSql() + "\n";
                    // 去除SQL关键字问题的映射规则信息
                    metaDdlRulesEntityList.remove(metaDdlRulesEntityList.get(metaDdlRulesEntityList.size() - 1));
                    continue;
                } catch (Exception exe) {
                    logger.error("Batch sync error", exe);
                    JobLogger.log("DDL transform fail, [{}]", logDbInfo);
                    continue;
                }
            }

            // 批量执行ddl rules
            int saveFlag = metaDdlRulesMapper.saveBatch(metaDdlRulesEntityList);
            if (saveFlag == 0) {
                return new ValidateResult(false, "DDL save rule failed");
            }
        } catch (ColumnNotFoundException cnfe) {
            logger.error("Errors in table-building of different rules in the same database and table！", cnfe);

            return new ValidateResult(false, "Check for keywords in SQL execution statements and selected rules！");
        } catch (SQLException e) {
            logger.error("Batch sync error", e);

            // 恢复被删除的表数据
            stmt.clearBatch();
            for (MetaTargetDdlEntity sqlInfoEntity : tarDdlList) {
                MetaTargetDdlEntity tarDdlEntity = metaTargetDdlMapper.query(sqlInfoEntity);
                if (tarDdlEntity != null && StringUtils.isNotEmpty(tarDdlEntity.getDdlSql())) {
                    stmt.execute(tarDdlEntity.getDdlSql());
                }
            }

            MetaTargetDdlEntity sqlDDL = tarDdlList.get(ddlIndex - 1);
            return new ValidateResult(false, "Check for keywords in SQL execution statements and selected rules！" + sqlDDL.getSrcDbName() + sqlDDL.getSrcTableName() + sqlDDL.getDdlSql());
        } catch (Exception exe) {
            logger.error("Batch sync error", exe);
            return new ValidateResult(false, "Check for keywords in SQL execution statements and selected rules！");
        } finally {
            try {
                if (null != stmt) {
                    stmt.close();
                }
                if (null != conn) {
                    conn.close();
                }
            } catch (SQLException se) {
                logger.error("Close connection exception", se);
            }
        }

        return new ValidateResult(true, "Successful synchronization" + sqlKeyWord);
    }

    /**
     * 保存第三种规则且不是第一个库下的规则映射关系
     *
     * @param ddlList
     * @param ruleEntity
     * @param routeId
     * @param targetSchemaId
     * @return
     * @throws SQLException
     */
    private ValidateResult multiSaveSQLRules(List<MetaTargetDdlEntity> ddlList, MetaRulesEntity ruleEntity, Long routeId, Long targetSchemaId) throws SQLException {
        String ruleType = ruleEntity.getRuleType();
        String rulePrefix = ruleEntity.getRulePrefix();
        String prefixName = "";
        if (!rulePrefix.equals("")) {
            prefixName = rulePrefix.toUpperCase() + "_";
        }

        List<MetaDdlRulesEntity> metaDdlRulesEntityList = new ArrayList<>();
        for (MetaTargetDdlEntity sqlInfoEntity : ddlList) {
            //保存对应的映射关系到meta_ddl_rules
            MetaDdlRulesEntity metaDdlRulesEntity = new MetaDdlRulesEntity();
            metaDdlRulesEntity.setRouteId(routeId);
            metaDdlRulesEntity.setSrcSchemaName(sqlInfoEntity.getSrcDbName());
            metaDdlRulesEntity.setSrcTableName(sqlInfoEntity.getSrcTableName());
            metaDdlRulesEntity.setTargetSchemaId(targetSchemaId);
            metaDdlRulesEntity.setTargetSchemaName(sqlInfoEntity.getTargetDbName());
            // 根据规则生成的终端表名称
            metaDdlRulesEntity.setTargetTableName(getTargetTableNameByTargetDdl(sqlInfoEntity, prefixName));
            metaDdlRulesEntity.setRuleId(ruleEntity.getRuleId());
            metaDdlRulesEntity.setRuleName(ruleEntity.getRuleName());
            metaDdlRulesEntity.setRuleColumn(ruleEntity.getRuleColumn().toUpperCase());
            metaDdlRulesEntity.setRuleType(ruleType);
            metaDdlRulesEntity.setRulePrefix(rulePrefix);
            metaDdlRulesEntity.setDbFlag(ruleEntity.getDbFlag());
            if (ruleEntity.getDbFlag() == 1) {
                metaDdlRulesEntity.setSrcSchemaColumn(sqlInfoEntity.getSrcDbName().substring(ruleEntity.getBeginIndex(), ruleEntity.getEndIndex()).toUpperCase());
            }
            metaDdlRulesEntity.setBeginIndex(ruleEntity.getBeginIndex());
            metaDdlRulesEntity.setEndIndex(ruleEntity.getEndIndex());
            metaDdlRulesEntity.setCreateUser(ruleEntity.getCreateUser());
            metaDdlRulesEntityList.add(metaDdlRulesEntity);

            // 修改ddl同步信息
            int resNum = metaTargetDdlMapper.updateExecuteFlag(sqlInfoEntity);
            if (resNum <= 0) {
                return new ValidateResult(false, ApplicationErrorCode.UPDATE_ERROR.getMessage());
            }
        }

        int saveFlag = metaDdlRulesMapper.saveBatch(metaDdlRulesEntityList);
        if (saveFlag == 0) {
            return new ValidateResult(false, "DDL save rule failed");
        }

        return new ValidateResult(true, "Successful synchronization");
    }

    /**
     * 校验能否同步ddl
     *
     * @param stmt
     * @param targetDbName
     * @param jobId
     * @param srcId
     * @param srcDb
     * @param srcTable
     * @param routeId
     * @param syncFlag
     * @return
     */
    private ValidateResult checkSyncDDL(Statement stmt, String targetDbName, long jobId, long srcId, String srcDb, String srcTable, long routeId, int syncFlag) {
        ValidateResult validateResult = new ValidateResult(true, null);
        StringBuffer errorMsg = new StringBuffer();

        // 校验ddl作业是否正在运行
        boolean result = checkJobRunning(routeId, jobId, srcId, srcDb);
        if (!result) {
            return new ValidateResult(false, ApplicationErrorCode.JOB_RUNNING.getMessage());
        }

        // 校验increment/full作业是否正在运行
        boolean flag = checkIncreAndFullJobRunning(routeId, jobId);
        if (!flag) {
            return new ValidateResult(false, ApplicationErrorCode.JOB_INCREORFULL_RUNNING.getMessage());
        }

        MetaDdlRulesEntity ddlRulesEntity = new MetaDdlRulesEntity();
        ddlRulesEntity.setRouteId(routeId);
        if (srcDb.equals("All")) {
            ddlRulesEntity.setSrcSchemaName(null);
        } else {
            ddlRulesEntity.setSrcSchemaName(srcDb);
        }
        ddlRulesEntity.setSrcTableName(srcTable);
        ddlRulesEntity.setTargetSchemaName(targetDbName);

        //查看meta_ddl_rules表是否存在规则，不存在则直接返回，否则 查询终端表是否存在或表中是否存在数据
        List<MetaDdlRulesEntity> ddlRulesList = metaDdlRulesMapper.query(ddlRulesEntity);
        if (null == ddlRulesList || ddlRulesList.size() <= 0) {
            return validateResult;
        }

        // 校验所有终端表是否存在及表中是否存在数据
        validateResult = checkTargetTableExist(stmt, targetDbName, ddlRulesList, syncFlag);
        if (!validateResult.isFlag() && syncFlag == 1) {
            // 单表直接返回
            return validateResult;
        } else if (syncFlag == 2) {
            // 批量记录日志
            errorMsg.append(validateResult.getMessage());
        }

        // 删除终端存在的没有数据的表，后面重新创建
        validateResult = dropTargetTableNoData(stmt, ddlRulesList);
        if (!validateResult.isFlag() && syncFlag == 1) {
            return validateResult;
        } else if (syncFlag == 2) {
            errorMsg.append(validateResult.getMessage());
        }

        // 批量存在表、删除表失败 信息
        if (null != errorMsg && errorMsg.length() > 0) {
            validateResult = new ValidateResult(false, errorMsg.toString());
        }

        // 校验ddl规则
        return validateResult;
    }

    /**
     * 校验job是否正在运行
     *
     * @param routeId
     * @param jobId
     * @param srcId
     * @param srcDb
     * @return
     */
    private boolean checkJobRunning(long routeId, long jobId, long srcId, String srcDb) {
        if (srcDb == null) {
            return false;
        }

        List<String> srcDbList = new ArrayList<>();
        if (srcDb.equals("All")) {
            srcDbList = metaMysqlDatabasesMapper.querySchemaName(srcId);
            srcDbList.add("All");
        } else {
            srcDbList.add(srcDb);
        }

        // srcDbList数据库正在运行的job个数(不包含jobId)
        int runningCount = jobDefinitionMapper.checkJobRunning(routeId, jobId, srcDbList);
        if (runningCount == 0) {
            return true;
        }
        return false;
    }

    /**
     * 查看increment/full job运行状态
     *
     * @param routeId
     * @return
     */
    private boolean checkIncreAndFullJobRunning(long routeId, long jobId) {
        if (routeId == 0) {
            return false;
        }

        //正在运行的increment/full job个数
        int runningCount = jobDefinitionMapper.checkIncreAndFullJobRunning(routeId, jobId);
        if (runningCount == 0) {
            return true;
        }
        return false;
    }

    /**
     * 校验终端表是否存在及表中是否存在数据
     *
     * @param stmt
     * @param targetSchemaName
     * @param ddlRulesList
     * @param syncFlag         1:单表, 2:批量
     * @return
     */
    private ValidateResult checkTargetTableExist(Statement stmt, String targetSchemaName, List<MetaDdlRulesEntity> ddlRulesList, int syncFlag) {
        ValidateResult validateResult = new ValidateResult(true, null);
        StringBuffer sqlBuff = new StringBuffer();

        StringBuffer existDataBuff = new StringBuffer();
        String srcTableName = "";
        String targetTableName = "";

        List<MetaDdlRulesEntity> skipList = new ArrayList<>();
        for (MetaDdlRulesEntity ddlEntity : ddlRulesList) {
            srcTableName = ddlEntity.getSrcTableName();
            targetTableName = ddlEntity.getTargetTableName().toUpperCase();

            sqlBuff.setLength(0);
            // 拼接查询sql
            sqlBuff.append("select * from ")
                    .append(targetSchemaName.toUpperCase()).append(".")
                    .append(targetTableName)
                    .append(" limit 1");

            try {
                // 执行查询sql，验证表中是否已经存在数据
                ResultSet resultSet = stmt.executeQuery(sqlBuff.toString());
                if (resultSet.next()) {
                    // 假如存在+有数据，提示已经有数据，记录下来，后面手动处理
                    if (syncFlag == 1) {
                        existDataBuff.append("There is data in the table[" + targetSchemaName + "." + srcTableName + "], the data cannot be synchronized.");
                    } else {
                        existDataBuff.append("There is data in the table[" + targetSchemaName + "." + srcTableName + "], the data cannot be synchronized.<br>");
                    }

                    skipList.add(ddlEntity);
                    continue;
                }
            } catch (TableNotFoundException tnfe) {
                logger.info("No table exists in terminal Library ！", tnfe);
            } catch (SQLException e) {
                logger.error("Unable to connect terminal", e);
                // TODO..不能直接返回（直接返回会把之后的表全部删除重建），记录日志，跳出当前循环即可
                skipList.add(ddlEntity);
                existDataBuff.append("[" + targetSchemaName + "." + srcTableName + "], Unable to connect terminal.");
                continue;
//                return new ValidateResult(false, "Unable to connect terminal");
            }
        }

        // skip have data table
        if (null != skipList && skipList.size() > 0) {
            ddlRulesList.removeAll(skipList);
        }

        // message
        if (StringUtils.isNotEmpty(existDataBuff.toString())) {
            validateResult = new ValidateResult(false, existDataBuff.toString());
        }
        return validateResult;
    }

    /**
     * 删除没有数据的终端表
     *
     * @param stmt
     * @param ddlRulesList
     * @return
     */
    private ValidateResult dropTargetTableNoData(Statement stmt, List<MetaDdlRulesEntity> ddlRulesList) {
        ValidateResult validateResult = new ValidateResult(true, null);

        try {
            StringBuffer sqlBuff = new StringBuffer();
            stmt.clearBatch();

            for (MetaDdlRulesEntity rulesEntity : ddlRulesList) {
                sqlBuff.setLength(0);
                String ruleType = rulesEntity.getRuleType();
                try {
                    sqlBuff.append("drop table ").append(rulesEntity.getTargetSchemaName().toUpperCase())
                            .append(".").append(rulesEntity.getTargetTableName().toUpperCase());

                    // execute
                    stmt.execute(sqlBuff.toString());
                } catch (TableNotFoundException tnfe) {
                    logger.info("No table exists in terminal Library ！ Operator sql : {}", sqlBuff.toString());
                }
            }
        } catch (SQLException e) {
            logger.error("Check rule error", e);
            validateResult = new ValidateResult(false, ApplicationErrorCode.DROP_TABLE_ERROR.getMessage());
        }

        return validateResult;
    }

    /**
     * 批量转换SQL
     *
     * @param dbTableInfoEntity
     * @param rule
     * @param routeId
     * @param schemaId
     * @return
     */
    private List<MetaTargetDdlEntity> batchGenerateDdlSQL(DBTableInfoEntity dbTableInfoEntity, MetaRulesEntity rule, Long routeId, Long schemaId) {
        String ruleType = rule.getRuleType();
        String rulePrefix = rule.getRulePrefix();
        String ruleSuffix = rule.getRuleSuffix();
        String ruleColumn = rule.getRuleColumn();

        // 规则四，规则后缀不能为空
        if (MetaRuleTypeEnum.RESTORE.getCode().equals(ruleType) && StringUtils.isBlank(ruleSuffix)) {
            throw new BizException(ApplicationErrorCode.MISS_RULE_SUFFIX.getCode(), ApplicationErrorCode.MISS_RULE_SUFFIX.getMessage());
        }

        List<DBTableInfo> dbtableList = dbTableInfoEntity.getChildren();
        if (dbtableList == null || dbtableList.size() <= 0) {
            return null;
        }

        // 获取路由信息,将公用的对象，变量，方法抽取出来
        MetaDatarouteEntity dbEntity = metaDatarouteMapper.queryRouteById(routeId);
        if (null == dbEntity) {
            return null;
        }
        // 获取终端信息
        MetaTargetSchemaEntity schemaEntity = metaTargetSchemaMapper.queryBySchemaId(schemaId);
        if (null == schemaEntity) {
            return null;
        }

        // 3:keyword
        List<String> keyWordList = sysDictMapper.queryDistinctName(3);
        // 4:unresolvable type
        List<String> typeList = sysDictMapper.queryDistinctName(4);

        List<MetaTargetDdlEntity> targetDdlList = new ArrayList();
        Long sourceId = dbEntity.getSourceId();
        String schemaName = schemaEntity.getSchemaName();
        dbtableList.parallelStream().forEach(dbTableInfo -> {
            // 校验截取DB的长度
            if (MetaRuleTypeEnum.MERGE_ADD_COLUMN.getCode().equals(ruleType) && rule.getDbFlag() == 1) {
                if (!(rule.getEndIndex() <= dbTableInfo.getLabel().length() && rule.getBeginIndex() != rule.getEndIndex())) {
                    throw new BizException(ApplicationErrorCode.TRANSFORM_ERROR.getCode(), ApplicationErrorCode.TRANSFORM_ERROR.getMessage());
                }
            }

            List<DBTableInfo> tableList = dbTableInfo.getChildren();
            if (tableList != null && tableList.size() > 0) {
                for (DBTableInfo tableEntity : tableList) {
                    String id = tableEntity.getId();
                    String srcDbName = id.split("#")[0];
                    String srcTableName = tableEntity.getLabel();

                    String tableInfo = srcDbName + "#" + srcTableName + "#" + sourceId;
                    List<MetaMysqlColumnsEntity> columnList = querySchemaInfo(tableInfo);

                    // 规则四，表名是否包含规则后缀
                    if (MetaRuleTypeEnum.RESTORE.getCode().equals(ruleType) && !srcTableName.contains(ruleSuffix)) {
                        JobLogger.log("DDL transform fail, {} do not contain rule suffixes.", tableInfo);
                        logger.error("DDL transform fail, {} do not contain rule suffixes.", tableInfo);
                        continue;
                    }

                    // 预先判断是否存在主键，避免在规则三情况下新增字段为PRIMARY KEY
                    boolean pkFlag = false;
                    String noPriTableName = "";
                    for (MetaMysqlColumnsEntity column : columnList) {
                        if (column.getIndexName() != null && column.getIndexName().indexOf(PRIMARY) > -1) {
                            pkFlag = true;
                            break;
                        } else {
                            noPriTableName = column.getTableSchema() + "：" + column.getTableName();
                        }
                    }
                    // 若该表没有主键,则直接跳过
                    if (!pkFlag) {
                        JobLogger.log("DDL transform fail, {} lacks primary key.", noPriTableName);
                        logger.error(noPriTableName + " lacks primary key !");
                        continue;
                    }

                    // 校验表字段和关键字
                    ValidateResult validateResult = checkColumnInfo(columnList, keyWordList, typeList, 2);
                    if (!validateResult.isFlag()) {
                        // 关键字、数据类型校验失败，记录日志，跳过
                        JobLogger.log("DDL transform fail, {}", validateResult.getMessage());
                        continue;
                    }

                    String tarTableName = srcTableName;
                    if (MetaRuleTypeEnum.MERGE_ADD_COLUMN.getCode().equals(ruleType)) {
                        // 针对规则三，添加字段用于标记库名，生成列，并作为联合主键
                        // 将新增字段放置在schemaList中的第二个字段位置
                        columnList.add(1, new MetaMysqlColumnsEntity(sourceId, id.split("#")[0], id.split("#")[1], ruleColumn));
                    } else if (MetaRuleTypeEnum.RESTORE.getCode().equals(ruleType)) {
                        // 规则四，截取表名后缀
                        tarTableName = srcTableName.substring(0, srcTableName.indexOf(ruleSuffix));
                    }

                    // 生成表SQL
                    String tableSql = generateTableSql(columnList, schemaName, ruleType, rulePrefix, tarTableName);

                    List<MetaMysqlColumnsEntity> colList = new ArrayList<>();
                    MetaMysqlColumnsEntity cEntity = new MetaMysqlColumnsEntity();
                    cEntity.setTableName(srcTableName);
                    cEntity.setTableSchema(id.split("#")[0]);
                    colList.add(cEntity);
                    // 生成索引sql, 去除索引2019-07-25
                    /*String indexSql = generateIndexSql(colList, tableSql, schemaName, "0", ruleType, rulePrefix);

                    targetDdlList.add(new MetaTargetDdlEntity(0L, routeId, dbEntity.getRouteName(), schemaId, srcDbName, label, schemaName, label.toUpperCase(), label.toUpperCase() + "_IDX", tableSql, indexSql));*/
                    targetDdlList.add(new MetaTargetDdlEntity(0L, routeId, dbEntity.getRouteName(), schemaId, srcDbName, srcTableName, schemaName, tarTableName.toUpperCase(), null, tableSql, null));
                }
            }
        });

        return targetDdlList;
    }

    /**
     * 生成建表TableSQL
     *
     * @param list
     * @param schema_name
     * @param ruleType
     * @param rulePrefix
     * @return
     */
    private String generateTableSql(List<MetaMysqlColumnsEntity> list, String schema_name, String ruleType, String rulePrefix, String tarTableName) {
        // 根据DDL语句解析成SQL
        StringBuffer commonSqlBuff = parseCommonSqlByDdl(list, schema_name, ruleType, rulePrefix, tarTableName);

        // 根据解析后的sql，生成主键
        StringBuffer primaryKeySqlBuff = parsePrimaryKeySqlByDdl(commonSqlBuff);

        // 处理联合主键
        String sqlInfo = parseUnionPrimaryKey(primaryKeySqlBuff);

        return sqlInfo;
    }

    /**
     * 根据DDL语句解析成SQL
     *
     * @param list
     * @param schema_name
     * @param ruleType
     * @param rulePrefix
     * @return
     */
    private StringBuffer parseCommonSqlByDdl(List<MetaMysqlColumnsEntity> list, String schema_name, String ruleType, String rulePrefix, String tarTableName) {
        StringBuffer sqlStr = new StringBuffer();
        sqlStr.append("CREATE TABLE IF NOT EXISTS ").append(schema_name.toUpperCase()).append(".");
        // 规则二、三时加大写规则前缀
        if ((MetaRuleTypeEnum.MERGE_TABLE.getCode().equals(ruleType) || MetaRuleTypeEnum.MERGE_ADD_COLUMN.getCode().equals(ruleType)) &&
                StringUtils.isNotBlank(rulePrefix)) {
            sqlStr.append(rulePrefix.toUpperCase()).append("_");
        }

        String tableName = list.get(0).getTableName();
        if (MetaRuleTypeEnum.RESTORE.getCode().equals(ruleType)) {
            // 规则四，截取表名后缀
            tableName = tarTableName;
        }
        sqlStr.append(tableName.toUpperCase()).append(" (").append("\r\n");

        int size = list.size();
        for (int i = 0; i < size; i++) {
            MetaMysqlColumnsEntity colEntity = list.get(i);
            String dataType = colEntity.getDataType();
            String columnType = colEntity.getColumnType();
            sqlStr.append(colEntity.getColumnName()).append(" ");

            switch (dataType){
                case "int":
                    sqlStr.append(columnType.replace("int", "bigint"));
                    break;
                case "bit":
                    sqlStr.append("integer(4)");
                    break;
                case "char":
                case "varchar":
                case "text":
                case "longtext":
                case "mediumtext":
                case "tinytext":
                case "enum":
                    sqlStr.append("varchar");
                    break;
                case "datetime":
                    sqlStr.append("timestamp");
                    break;
                case "blob":
                case "longblob":
                case "tinyblob":
                case "mediumblob":
                case "json":
                    sqlStr.append("varbinary");
                    break;
                default:
                    sqlStr.append(columnType.split(" ")[0]);
                    break;
            }

            // 字段添加主键,终端只有(联合)主键不为空,其他必须为空
            if (null != colEntity.getIndexName() && colEntity.getIndexName().contains(PRIMARY)) {
                sqlStr.append(" NOT NULL PRIMARY KEY,").append("\r\n");
            } else {
                if (i == (size - 1)) {
                    sqlStr.append(",\r\n");
                } else {
                    sqlStr.append(",\r\n");
                }
            }
        }

        return sqlStr;
    }

    /**
     * 生成primary key
     *
     * @param commonSqlBuffer
     * @return
     */
    private StringBuffer parsePrimaryKeySqlByDdl(StringBuffer commonSqlBuffer) {
        /**
         * 若不包含主键，则添加一个。
         * 单表转换由于前端控制，所以不会进入该判断。
         * 多表转换时有flag标记，为false不会进入该判断
         */
        String str = commonSqlBuffer.toString();
        if (!str.contains(PRIMARY)) {
            StringBuffer sql = new StringBuffer();

            String secondLine = "";
            // 得到建表语句第一行，其他行
            String firstLine = str.substring(0, str.indexOf(","));
            String otherLine = str.substring(str.indexOf(",") + 1);

            sql.append(firstLine);
            // 若建表语句第一行包含')'(数据类型有精确位数)，则将其作为主键，否则将第二个字段作为主键
            if (firstLine.contains(")")) {
                if (!firstLine.contains("NOT NULL")) {
                    sql.append(" NOT NULL PRIMARY KEY,");
                } else {
                    sql.append(" PRIMARY KEY,");
                }

                sql.append(otherLine);
            } else {
                secondLine = otherLine.substring(0, otherLine.indexOf(","));
                sql.append(", ").append(secondLine);

                if (!secondLine.contains("NOT NULL")) {
                    sql.append(" NOT NULL PRIMARY KEY,");
                } else {
                    sql.append(" PRIMARY KEY,");
                }

                sql.append(otherLine.substring(otherLine.indexOf(",") + 1));
            }

            String ddlSql = "";
            // 3:下标减1，;减1，)减1
            if (sql.lastIndexOf(",") == sql.length() - 3) {
                ddlSql = sql.substring(0, sql.length() - 3);
                sql = new StringBuffer();
                sql.append(ddlSql).append("\r\n");
            }
            sql.append(") ").append("SALT_BUCKETS = ").append("12").append(", COMPRESSION='SNAPPY'").append("\r\n");

            commonSqlBuffer = sql;
        } else {
            String ddlSql = "";
            // 存在\r\n,所以是length - 3
            if (commonSqlBuffer.lastIndexOf(",") == commonSqlBuffer.length() - 3) {
                ddlSql = commonSqlBuffer.substring(0, commonSqlBuffer.length() - 3);
                commonSqlBuffer = new StringBuffer();
                commonSqlBuffer.append(ddlSql).append("\r\n");
            }

            commonSqlBuffer.append(") ").append("SALT_BUCKETS = ").append("12").append(", COMPRESSION='SNAPPY'").append("\r\n");
        }

        return commonSqlBuffer;
    }

    /**
     * 根据primary key sql处理联合主键
     *
     * @param sqlBuff
     * @return
     */
    private String parseUnionPrimaryKey(StringBuffer sqlBuff) {
        // 处理联合主键
        String[] sqlArr = sqlBuff.toString().split("\r\n");
        // 确认是否为联合主键，统计主键包含的key个数
        int count = 0;
        // 获取联合主键的column name
        StringBuffer str = new StringBuffer();
        for (String sqlInfo : sqlArr) {
            if (sqlInfo.contains(PRIMARY_KEY)) {
                count++;
                str.append(sqlInfo.split(" ")[0]).append(", ");
            }
        }

        StringBuffer ddlSQL = new StringBuffer();
        if (count >= 2) {
            // CONSTRAINT pk PRIMARY KEY代替PRIMARY KEY
            ddlSQL.append(sqlBuff.substring(0, sqlBuff.toString().lastIndexOf(")")).replaceAll(PRIMARY_KEY, " "));
            ddlSQL.append(", CONSTRAINT pk PRIMARY KEY (")
                    .append(str.substring(0, str.lastIndexOf(","))).append(")\r\n)")
                    .append(sqlBuff.substring(sqlBuff.toString().lastIndexOf(")") + 1));
        } else {
            // 若主键为单一key，返回
            ddlSQL = sqlBuff;
        }

        return ddlSQL.toString();
    }

    /**
     * 生成建表对应的二级索引表IndexSQL
     *
     * @param list
     * @param tableSql
     * @param schema_name
     * @param flag
     * @param ruleType
     * @param rulePrefix
     * @return
     */
    private String generateIndexSql(List<MetaMysqlColumnsEntity> list, String tableSql, String schema_name, String flag, String ruleType, String rulePrefix) {
        StringBuffer sqlStr = new StringBuffer();
        sqlStr.append("CREATE INDEX IF NOT EXISTS ");

        // 对规则二、三在建表时加规则前缀
        boolean condition = (MetaRuleTypeEnum.MERGE_TABLE.getCode().equals(ruleType) || MetaRuleTypeEnum.MERGE_ADD_COLUMN.getCode().equals(ruleType)) &&
                StringUtils.isNotBlank(rulePrefix);
        if (condition) {
            // 包含前缀
            sqlStr.append(rulePrefix.toUpperCase()).append("_")
                    .append(list.get(0).getTableName().toUpperCase()).append("_IDX \r\n ON ")
                    .append(schema_name.toUpperCase()).append(".")
                    .append(rulePrefix.toUpperCase()).append("_")
                    .append(list.get(0).getTableName().toUpperCase()).append(" ( ").append("\r\n");
        } else {
            sqlStr.append(list.get(0).getTableName().toUpperCase()).append("_IDX \r\n ON ")
                    .append(schema_name.toUpperCase()).append(".")
                    .append(list.get(0).getTableName().toUpperCase()).append(" ( ").append("\r\n");
        }

        String[] sqlInfo = tableSql.split("\r\n");

        /**
         * list中字段是索引建表字段，生成建表字段时根据list的库名、表名重新查询所有字段
         * 获取list中的字段，添加的索引建表语句，若flag为0，则默认为主键字段
         */
        if (list.size() == 0) {
            sqlStr.append(getDDL(sqlInfo));
        } else {
            if (flag.equals("1")) {
                for (int j = 0; j < list.size(); j++) {
                    MetaMysqlColumnsEntity colEntity = list.get(j);
                    sqlStr.append(colEntity.getColumnName());

                    if (j == (list.size() - 1)) {
                        sqlStr.append("\r\n");
                    } else {
                        sqlStr.append(", \r\n");
                    }
                }
            } else {
                sqlStr.append(getDDL(sqlInfo));
            }
        }

        sqlStr.append(" )").append("\r\n");

        return sqlStr.toString();
    }

    /**
     * 获取主键字段
     * <p>
     * 建表语句已经生成，所以规则三添加的字段已被确认为联合主键
     *
     * @param sqlInfo
     * @return
     */
    private static String getDDL(String[] sqlInfo) {
        // sqlInfo指的是建表语句按\r\n分割后的数组
        int count = 0;
        StringBuffer sqlStr = new StringBuffer();
        StringBuffer str = new StringBuffer();

        // 得到分割后的主键字段(单一/联合)
        for (String item : sqlInfo) {
            if (item.contains("PRIMARY KEY (")) {
                count++;
                str.append(item.substring(item.indexOf("(") + 1, item.indexOf(")") + 1));
            } else if (item.contains(PRIMARY_KEY) && !item.contains("PRIMARY KEY (")) {
                count++;
                str.append(item.split(" ")[0]).append(",");
            }
        }

        if (count > 0) {
            String[] indexList = str.toString().substring(0, str.toString().length() - 1).split(",");

            for (int i = 0; i < indexList.length; i++) {
                sqlStr.append(indexList[i]);

                if (i == (indexList.length - 1)) {
                    sqlStr.append("\r\n");
                } else {
                    sqlStr.append(", \r\n");
                }
            }
        }

        return sqlStr.toString();
    }

    /**
     * 校验列名是否包含特殊字符、系统关键字，列类型是否包含phoenix不能解析的类型
     *
     * @param list
     * @param keyWordList
     * @param typeList
     * @param syncFlag    1:单表, 2:批量
     * @return
     */
    private ValidateResult checkColumnInfo(List<MetaMysqlColumnsEntity> list, List<String> keyWordList, List<String> typeList, int syncFlag) {
        ValidateResult validateResult = new ValidateResult(true, null);
        if (null == list) {
            return validateResult;
        }

        StringBuffer errorBuff = new StringBuffer();
        for (MetaMysqlColumnsEntity columnEntity : list) {
            String columnName = columnEntity.getColumnName();
            String columnInfo = columnEntity.getTableSchema() + "." + columnEntity.getTableName() + "." + columnName;
            // 校验是否为特殊字符
            boolean symbol = checkSymbol(columnName);
            if (symbol) {
                if (syncFlag == 1) {
                    errorBuff.append("Column name[" + columnInfo + "] contain special characters.");
                } else {
                    errorBuff.append("Column name[" + columnInfo + "] contain special characters.<br>");
                }
            }

            // 校验是否为系统关键词
            if (keyWordList.contains(columnName.toLowerCase())) {
                if (syncFlag == 1) {
                    errorBuff.append("Column name[" + columnInfo + "] are system keywords.");
                } else {
                    errorBuff.append("Column name[" + columnInfo + "] are system keywords.<br>");
                }
            }

            // 校验phoenix不能解析的字段类型
            String columnType = columnEntity.getColumnType();
            if (typeList.contains(columnType.toLowerCase())) {
                columnInfo = columnInfo + "(" + columnType + ")";
                if (syncFlag == 1) {
                    errorBuff.append("Phoenix cannot parse data types[" + columnInfo + "].");
                } else {
                    errorBuff.append("Phoenix cannot parse data types[" + columnInfo + "].<br>");
                }
            }
        }

        if (StringUtils.isNotEmpty(errorBuff.toString())) {
            return new ValidateResult(false, errorBuff.toString());
        }

        return validateResult;
    }

    /**
     * 验证特殊字符
     *
     * @param str 待检测的字符串
     * @return 验证成功返回true，验证失败返回false
     */
    public boolean checkSymbol(String str) {
        String regex = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        return m.find();
    }

}
