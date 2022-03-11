/**
 * Created by zhangxiao on 2018/12/12.
 */
import Http from "@utils/Http";

// Job
export const jobCount = (params) => Http.setPromise(`GET`,`/mcp/home/jobs`,params);
//tech metadata
export const techCount = (params) => Http.setPromise(`GET`,`/mcp/home/tech_metadata`,params);
//Sync data streaming
export const syncCount = (params) => Http.setPromise(`GET`,`/mcp/home/sync_data`,params);
// Agent table list
export const agentTable = (params) => Http.setPromise(`GET`,`/mcp/home/error_sql_counts`,params);
// Mysql echarts bar
export const mysqlEchartsBar = (params) => Http.setPromise(`GET`,`/mcp/home/table_counts`,params);
// Hot table list
export const hotTbale = (params) => Http.setPromise(`GET`,`/mcp/home/hot_tables`,params);
// sync agent line
export const syncAgentLine = (params) => Http.setPromise(`GET`,`/mcp/home/sync_agent_data`,params);
// sync job line
export const syncJobLine = (params) => Http.setPromise(`GET`,`/mcp/home/sync_job_data`,params);
