import Http from "@utils/Http";

// project
export const queryProject = (params) => Http.setPromise(`GET`, `/mcp/project/query`, params);
export const queryName = (params) => Http.setPromise(`GET`, `/mcp/project/query_name`, params);
export const eidtProject = (params) => Http.setPromise(`POST`, `/mcp/project/edit`, params);
export const delProject = (projectId) => Http.setPromise(`DELETE`, `/mcp/project/delete?projectId=${projectId}`);
export const queryAgent = (params) => Http.setPromise(`GET`, `/mcp/job/query_agents`, params);
// group
export const queryGroup = (params) => Http.setPromise(`GET`, `/mcp/group/query`, params);
// job table list
export const jobTableList = (params) => Http.setPromise(`GET`, `/mcp/joblog/queryJobSchedulerLog`, params);
