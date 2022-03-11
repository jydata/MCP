/**
 * Created by zhangxiao on 2018/11/21.
 */
import Http from "@utils/Http";

// DS Connections
export const getDSLink = (params) => Http.setPromise(`GET`, `/mcp/conn/query`, params);
export const getLoads = (params) => Http.setPromise(`POST`, `/mcp/conn/load_options`, params);
export const saveDSLink = (params) => Http.setPromise(`POST`, `/mcp/conn/save`, params);
export const testDSLink = (params) => Http.setPromise(`POST`, `/mcp/conn/test`, params);

//DSMetadataSync
export const getDBTree = (params) => Http.setPromise(`POST`, `/mcp/sync/query_dstree`, params);
export const getCount = (params) => Http.setPromise(`POST`, `/mcp/sync/calc_db_info`, params);
export const getMetaTable = (params,dataList) => Http.setPromise(`POST`, `/mcp/sync/query_table?dataList=${dataList}`, params);
export const getMetaColumn = (params,dataList) => Http.setPromise(`POST`, `/mcp/sync/query_column?dataList=${dataList}`, params);
export const getTableDDL = (params,dataList) => Http.setPromise(`POST`, `/mcp/sync/load_ddl?dataList=${dataList}`, params);
export const getSample = (params,dataList) => Http.setPromise(`POST`, `/mcp/sync/query_example?dataList=${dataList}`, params);
export const syncRatio = (params,sourceId) => Http.setPromise(`POST`, `/mcp/sync/sync_ratio?sourceId=${sourceId}`, params);
export const batchSyncdata = (params) => Http.setPromise(`POST`, `/mcp/sync/batch_sync`, params);

//DS Routing
export const getDSRoutes = (params) => Http.setPromise(`GET`, `/mcp/route/query`, params);
export const saveRouteName = (params) => Http.setPromise(`POST`, `/mcp/route/update_route_name`, params);
export const querySchemalists = (params) => Http.setPromise(`POST`, `/mcp/route/query_schema`, params);
export const saveSchemalists = (params) => Http.setPromise(`POST`, `/mcp/route/save_schema`, params);
export const createSchemalists = (params) => Http.setPromise(`POST`, `/mcp/route/create_schema`, params);
export const updateRouterStatus = (params) => Http.setPromise(`POST`, `/mcp/route/update_status`, params);
export const updateRouterName = (params) => Http.setPromise(`POST`, `/mcp/route/update_name`, params);
export const saveRoute = (params) => Http.setPromise(`POST`, `/mcp/route/save`, params);
export const existTables = (params) => Http.setPromise(`GET`, `/mcp/route/exist_table`, params);
export const queryRoute = (params) => Http.setPromise(`GET`, `/mcp/route/job_routes`, params);

//DS Rules
export const getRules = (params) => Http.setPromise(`GET`, `/mcp/rule/query_list`, params);
export const getRuleType = (params) => Http.setPromise(`GET`, `/mcp/rule/query_type`, params);
export const saveRules = (params) => Http.setPromise(`POST`, `/mcp/rule/save`, params);
export const delRules = (params) => Http.setPromise(`POST`, `/mcp/rule/delete`, params);

//DS Generate Target DDL
export const queryTarget = (params) => Http.setPromise(`GET`, `/mcp/ddl/query_target_info`, params);
export const getSchema = (params) => Http.setPromise(`GET`, `/mcp/ddl/query_db_trees`, params);
export const getRule= (params) => Http.setPromise(`GET`, `/mcp/ddl/query_rules`, params);
export const getDatabase = (params) => Http.setPromise(`GET`, `/mcp/ddl/query_source_db`, params);
export const getRoutes = (params) => Http.setPromise(`GET`, `/mcp/ddl/query_routes`, params);
export const getMetadata = (params) => Http.setPromise(`GET`, `/mcp/ddl/query_table_meta`, params);
export const getColumns = (params) => Http.setPromise(`GET`, `/mcp/ddl/query_columns`, params);
export const getDDL = (params) => Http.setPromise(`POST`, `/mcp/ddl/query_ddl_info`, params);
export const getBinlog = (routeId,srcDbName,srcTableName) => Http.setPromise(`GET`, `/mcp/ddl/query_binlogddl_info/${routeId}/${srcDbName}/${srcTableName}`);
export const transformDDL = (params,routeId,schemaId,ruleName) => Http.setPromise(`POST`, `/mcp/ddl/batch_generate_sql/${routeId}/${schemaId}/${ruleName}`, params);
export const transformSingleDDL = (params,flag,routeId,schemaId,id,ruleName,srcDb) => Http.setPromise(`POST`, `/mcp/ddl/generate_sql/${flag}/${routeId}/${schemaId}/${id}/${ruleName}/${srcDb}`, params);
export const syncDDL = (routeId,ruleName) => Http.setPromise(`POST`, `/mcp/ddl/multi_exec_sql/${routeId}/${ruleName}`);
export const syncSingleDDL = (params,srcId,ruleName,srcDb,routeId,schemaId) => Http.setPromise(`POST`, `/mcp/ddl/exec_sql/${srcId}/${ruleName}/${srcDb}/${routeId}/${schemaId}`, params);
export const saveDDL = (params,srcDb,routeId) => Http.setPromise(`POST`, `/mcp/ddl/save_sql/${srcDb}/${routeId}`, params);

