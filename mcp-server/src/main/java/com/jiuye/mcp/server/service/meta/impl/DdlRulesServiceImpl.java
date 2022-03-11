package com.jiuye.mcp.server.service.meta.impl;

import com.jiuye.mcp.server.dao.meta.MetaDdlRulesMapper;
import com.jiuye.mcp.server.dao.meta.MetaMysqlColumnsMapper;
import com.jiuye.mcp.server.model.meta.DBTableInfo;
import com.jiuye.mcp.server.model.meta.DBTableInfoEntity;
import com.jiuye.mcp.server.model.meta.MetaDdlRulesEntity;
import com.jiuye.mcp.server.model.meta.MetaMysqlColumnsEntity;
import com.jiuye.mcp.server.service.meta.IDdlRulesService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author kevin
 */
@Service
public class DdlRulesServiceImpl implements IDdlRulesService {

    private static final Logger logger = Logger.getLogger(DdlRulesServiceImpl.class.getName());

    @Autowired
    private MetaDdlRulesMapper ddlRulesMapper;
    @Autowired
    private MetaMysqlColumnsMapper mysqlColumnsMapper;

    @Override
    public List<MetaDdlRulesEntity> queryByRouteIdAndTarSchemaId(long routeId, long tarSchemaId) {
        return ddlRulesMapper.queryByRouteIdAndTarSchemaId(routeId, tarSchemaId);
    }

    @Override
    public List<MetaDdlRulesEntity> queryByJobId(long jobId) {
        return ddlRulesMapper.queryByJobId(jobId);
    }

    @Override
    public int queryTargetTablesByJob(long routeId, long targetSchemaId) {
        return ddlRulesMapper.queryTargetTablesByJob(routeId, targetSchemaId);
    }

    /**
     * 根据参数查询数据库、表信息-增量
     * db
     * -table
     *
     * @param routeId
     * @param tarSchemaId
     * @return
     */
    @Override
    public List<DBTableInfoEntity> queryDbAndTable(long routeId, long tarSchemaId) {
        List<MetaDdlRulesEntity> ddlRuleList = ddlRulesMapper.queryByRouteIdAndTarSchemaId(routeId, tarSchemaId);
        List<DBTableInfoEntity> treeList = new ArrayList<>();

        if (null != ddlRuleList && ddlRuleList.size() > 0) {
            Map<String, List<String>> dbTableMap = new LinkedHashMap<>();

            ddlRuleList.forEach(ddlRule -> {
                Long id = ddlRule.getId();
                String srcDbName = ddlRule.getSrcSchemaName();
                // 1#maxwell
                String srcTableName = id + "#" + ddlRule.getSrcTableName();

                if (dbTableMap.containsKey(srcDbName)) {
                    List<String> tableNameList = dbTableMap.get(srcDbName);
                    tableNameList.add(srcTableName);
                    dbTableMap.put(srcDbName, tableNameList);
                } else {
                    List<String> tableNameList = new ArrayList<>();
                    tableNameList.add(srcTableName);
                    dbTableMap.put(srcDbName, tableNameList);
                }
            });

            Iterator<Map.Entry<String, List<String>>> dbIterator = dbTableMap.entrySet().iterator();
            while (dbIterator.hasNext()) {
                Map.Entry<String, List<String>> dbInfo = dbIterator.next();
                String dbName = dbInfo.getKey();
                DBTableInfoEntity dbEntity = new DBTableInfoEntity(dbName, dbName);
                List<String> tableList = dbInfo.getValue();

                List<DBTableInfo> tableInfoList = new ArrayList<DBTableInfo>();
                tableList.forEach(tableName -> {
                    tableInfoList.add(new DBTableInfo(true, tableName, tableName.split("#")[1]));
                });

                // table all
                if (null != tableInfoList && tableInfoList.size() > 0) {
                    tableInfoList.add(0, new DBTableInfo(true, "All", "All"));
                }
                dbEntity.setChildren(tableInfoList);

                treeList.add(dbEntity);
            }

            // db all
            treeList.add(0, new DBTableInfoEntity(true, "All", "All"));
        }

        return treeList;
    }

    /**
     * 根据参数查询数据库、表信息-全量
     * db
     * -table
     * -column
     *
     * @param routeId
     * @param tarSchemaId
     * @return
     */
    @Override
    public List<DBTableInfoEntity> queryFullDbTableList(long routeId, long tarSchemaId) {
        // 查询所有的db、table
        List<DBTableInfoEntity> dbTableList = queryDbAndTable(routeId, tarSchemaId);
        if (null == dbTableList || dbTableList.size() <= 0) {
            return dbTableList;
        }

        // 查询所有db、table
        List<MetaMysqlColumnsEntity> dbTables = new ArrayList<>();
        // db table
        dbTableList.remove(0);
        dbTableList.parallelStream().forEach(dbTable -> {
            String dbName = dbTable.getLabel();

            List<DBTableInfo> tableList = dbTable.getChildren();
            if (null != tableList && tableList.size() > 0) {
                // remove All
                tableList.remove(0);
                tableList.parallelStream().forEach(table -> {
                    String tableName = table.getLabel();

                    // 根据db、table查询列中为时间类型的列信息
                    MetaMysqlColumnsEntity columnsEntity = new MetaMysqlColumnsEntity();
                    columnsEntity.setTableSchema(dbName);
                    columnsEntity.setTableName(tableName);
                    dbTables.add(columnsEntity);
                });
            }
        });

        // 根据routeId、db table查询所有列信息
        List<MetaMysqlColumnsEntity> timeColumnList = mysqlColumnsMapper.queryColumns(routeId, dbTables);

        // db table
        dbTableList.parallelStream().forEach(dbTable -> {
            String dbName = dbTable.getLabel();

            List<DBTableInfo> tableList = dbTable.getChildren();
            if (null != tableList && tableList.size() > 0) {
                tableList.parallelStream().forEach(table -> {
                    String tableName = table.getLabel();
                    if (tableName.equals("All")) {
                        return;
                    }

                    // 根据db、table匹配时间类型的列信息
                    List<DBTableInfo> tableColumnList = table.getChildren();
                    if (null == tableColumnList) {
                        tableColumnList = new ArrayList<>();
                    }
                    int index = 0;
                    for (MetaMysqlColumnsEntity column : timeColumnList) {
                        String tableSchema = column.getTableSchema();
                        String colTableName = column.getTableName();
                        if (tableSchema.equals(dbName) && colTableName.equals(tableName)) {
                            index++;
                            String columnName = column.getColumnName();
                            tableColumnList.add(new DBTableInfo(true, String.valueOf(index), columnName));
                        }
                    }

                    if (index > 0) {
                        // set table is leaf
                        table.setIsleaf(false);
                        // set column name
                        table.setChildren(tableColumnList);
                    }
                });
            }
        });

        return dbTableList;
    }

    @Override
    public int save(MetaDdlRulesEntity entity) {
        return ddlRulesMapper.save(entity);
    }

    @Override
    public int saveBatch(List<MetaDdlRulesEntity> list) {
        return ddlRulesMapper.saveBatch(list);
    }

    @Override
    public int update(MetaDdlRulesEntity entity) {
        return ddlRulesMapper.update(entity);
    }
}
