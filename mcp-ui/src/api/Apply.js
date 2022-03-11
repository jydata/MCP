/**
 * Created by zhuangwei on 2018/8/27.
 */
import Http from "@utils/Http";

// 获取枚举列表
export const queryDataLink = (params) => Http.setPromise(`GET`, `/mcp/metadata/query_connections`, params);
export const insertDataLink = (params) => Http.setPromise(`POST`, `/mcp/metadata/add_connection`, params);
export const testDataLink = (params) => Http.setPromise(`POST`, `/mcp/metadata/test_connection`, params);
