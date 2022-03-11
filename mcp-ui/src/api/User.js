/**
 * Created by kim on 2018/10/19.
 */
import Http from "@utils/Http";

// user management
//根据查询条件查询用户数据信息
export const queryList = (params) => Http.setPromise(`POST`, `/mcp/user/query_list`, params);
export const save = (params) => Http.setPromise(`POST`, `/mcp/user/save`, params);
export const update = (params) => Http.setPromise(`POST`, `/mcp/user/update`, params);
export const updateStatus = (params) => Http.setPromise(`POST`, `/mcp/user/update_status`, params);
// 动态获取登录用户信息
export const getManagerInfo = (params) => Http.setPromise(`POST`, `/mcp/user/query`, params);
// 检查密码、重置密码
export const resetPassword =(params) => Http.setPromise(`POST`, `/mcp/user/reset_password`, params);
export const updatePassword = (params) => Http.setPromise(`POST`, `/mcp/user/update_password`, params);
// 根据用户权限信息获取页面信息、重置密码按钮实现功能
export const getManagerList = (params) => Http.setPromise(`GET`, `/mcp/user/query_manager_list`, params);
// export const formatPassword = (params) => Http.setPromise(`POST`, `/mcp/user/format_password`, params);
// 登录页面
export const login = (params) => Http.setPromise(`POST`, `/mcp/user/login`, params);
// export const searchUserLinks = (params) => Http.setPromise(`POST`, `/mcp/user/search_redis`, params);
export const logout = (params) => Http.setPromise(`GET`, `/mcp/user/logout`, params);
