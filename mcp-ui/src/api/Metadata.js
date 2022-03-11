/**
 * Created by ZP on 2018/8/28.
 */
import Http from "@utils/Http";

// db link management
export const getDBLinks = (params) => Http.setPromise(`GET`, `/mcp/metadata/query_connections`, params);
export const testDBLink = (params) => Http.setPromise(`POST`, `/mcp/metadata/test_connection`, params);
export const insertDBLink = (params) => Http.setPromise(`POST`, `/mcp/metadata/save_connection`, params);
export const syncMetadata = (params) => Http.setPromise(`POST`, `/mcp/metadata/sync_metadata`, params);
export const loadOptions = (params) => Http.setPromise(`POST`, `/mcp/metadata/load_options`, params);

// db route management
export const getDBRoutes = (params) => Http.setPromise(`GET`, `/mcp/metadata/query_routes`, params);
export const loadRouteOptions = (params) => Http.setPromise(`POST`, `/mcp/metadata/load_route_options`, params);
export const saveDBRoute = (params) => Http.setPromise(`POST`, `/mcp/metadata/save_route`, params);
export const updateRouteStatus = (params) => Http.setPromise(`POST`, `/mcp/metadata/update_route_status`, params);

// db schema management
export const getSchemaList = (params) => Http.setPromise(`GET`, `/mcp/metadata/query_schemas`, params);
export const getDBRouteInfo = (params) => Http.setPromise(`GET`, `/mcp/metadata/query_route_info`, params);
export const saveSchemaInfo = (params) => Http.setPromise(`POST`, `/mcp/metadata/save_schemas`, params);
export const createSchemaInfo = (params) => Http.setPromise(`POST`, `/mcp/metadata/create_schemas`, params);

// table structure
export const getRouteList = (params) => Http.setPromise(`GET`, `/mcp/metadata/query_valid_route`, params);
export const loadTree = (flag, params) => Http.setPromise(`POST`, `/mcp/metadata/query_dbs/${flag}`, params);
/*export const dbschemaList = (params) => Http.setPromise(`POST`, `/mcp/metadata/query_tables`, params);*/
export const tableStructureInfo = (params) => Http.setPromise(`GET`, `/mcp/metadata/query_columns`, params);
export const getDDLInfo = (params) => Http.setPromise(`POST`, `/mcp/metadata/query_ddl_info`, params);

export const generateSQL = (flag, route_id, schema_id, params) => Http.setPromise(`POST`, `/mcp/metadata/generate_sql/${flag}/${route_id}/${schema_id}`, params);
export const batchGenerateSQL = (route_id, schema_id, params) => Http.setPromise(`POST`, `/mcp/metadata/batch_generate_sql/${route_id}/${schema_id}`, params);
export const saveSQL = (params) => Http.setPromise(`POST`, `/mcp/metadata/save_sql`, params);

// hbase table management
export const getHBaseSQLInfos = (params) => Http.setPromise(`GET`, `/mcp/metadata/query_hbase_sqls`, params);
export const multiExecSQL = (params) => Http.setPromise(`POST`, `/mcp/metadata/multi_exec_sql`, params);
