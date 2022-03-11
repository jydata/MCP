/**
 * Created by zhuangwei on 2018/6/22.
 */
import Http from "@utils/Http";

// 获取左侧菜单
// export const menus = (params) => Http.setPromise(`GET`,`/static/json/menu.json`,params);
export const menus = (params) => Http.setPromise(`GET`,`/static/json/newMenu.json`,params);
export const userMenus = (params) => Http.setPromise(`GET`,`/static/json/menu1.json`,params);

// 获取左侧部分菜单
// export const menusPart = (params) => Http.setPromise(`GET`,`/static/json/menu1.json`,params);

