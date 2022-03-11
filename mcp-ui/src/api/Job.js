/**
 * Created by yss on 2018/8/28.
 */
import Http from "@utils/Http";

// job
export const getJobs = (params) => Http.setPromise(`GET`, `/mcp/job/jobs`, params);
export const queJobs = (params) => Http.setPromise(`POST`, `/mcp/job/query_jobs`, params);
export const queryJobNames = (params) => Http.setPromise(`GET`, `/mcp/job/query_job_names`, params);
export const queryDBTree = (params) => Http.setPromise(`GET`, `/mcp/job/query_tree_list`, params);
export const queryTree = (params) => Http.setPromise(`GET`, `/mcp/job/query_tree`, params);
export const insertJob = (params) => Http.setPromise(`POST`, `/mcp/job/add_job`, params);
export const queryAgents = (params) => Http.setPromise(`GET`, `/mcp/job/query_agents`, params);
export const checkEnvIncrement = (params) => Http.setPromise(`POST`, `/mcp/job/check_env_increment`, params);
export const checkEnvColumn = (params) => Http.setPromise(`POST`, `/mcp/job/check_env_column`, params);
export const checkEnvMeta = (sourceId) => Http.setPromise(`POST`, `/mcp/job/check_env_metadata?sourceId=${sourceId}`, sourceId);
export const checkEnvDDL = (params,id) => Http.setPromise(`POST`, `/mcp/job/check_env_ddl?routeId=${id}`, params);
export const updateJobEnable = (params) => Http.setPromise(`PUT`, `/mcp/job/update`, params);
export const queryBinlog = (params) => Http.setPromise(`GET`, `/mcp/job/query_binlog`, params);
export const trigger = (params) => Http.setPromise(`PUT`, `/mcp/job/trigger_save`, params);
export const triggerJob = (jobId, params) => Http.setPromise(`POST`, `/mcp/job/trigger/${jobId}`, params);
export const triggerJobById = (params,id) => Http.setPromise(`PUT`, `/mcp/job/trigger?jobId=${id}`, params);
export const pauseJob = (jobId, params) => Http.setPromise(`POST`, `/mcp/job/pause/${jobId}`, params);
export const pauseJobById = (params,id) => Http.setPromise(`PUT`, `/mcp/job/pause?jobId=${id}`, params);
export const resumeJob = (jobId, params) => Http.setPromise(`POST`, `/mcp/job/resume/${jobId}`, params);
export const resumeJobById = (params,id) => Http.setPromise(`PUT`, `/mcp/job/resume?jobId=${id}`, params);
export const killJobById = (params,id) => Http.setPromise(`PUT`, `/mcp/job/kill?LogId=${id}`, params);
export const removeMetaJobById = (params,id) => Http.setPromise(`PUT`, `/mcp/job/remove?jobId=${id}`, params);
export const killAllJobById = (params,id) => Http.setPromise(`PUT`, `/mcp/job/configure?jobId=${id}`, params);

// job scheduler log
export const queryLogs = (params) => Http.setPromise(`GET`, `/mcp/joblog/logs`, params);
export const queryById = (logId, params) => Http.setPromise(`GET`, `/mcp/joblog/logs/${logId}`, params);
export const queJobLogById = (params) => Http.setPromise(`POST`, `/mcp/joblog/loginfo`, params);
export const queloglistById = (params) => Http.setPromise(`POST`, `/mcp/joblog/query_log`, params);

// increment list
export const queryDbTable = (params) => Http.setPromise(`GET`, `/mcp/ddl_rule/query_db_table`, params);
// full list
export const queryFullDbTable = (params) => Http.setPromise(`GET`, `/mcp/ddl_rule/query_full_db_table`, params);
