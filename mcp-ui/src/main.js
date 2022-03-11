// import Vue from "vue";
import App from "./App";
import router from "./config/routes";

import {isDebug} from "config/config";
const loginUrl="#/login";
import store from "./store";

// Axios

//babelpolyfill
import babelpolyfill from 'babel-polyfill';
// Vuex
// import Vuex from "vuex";

// Vue.use(ElementUI);
Vue.use(babelpolyfill);
// Vue.use(Vuex);

import moment from 'moment'//导入文件
Vue.prototype.$moment = moment;//赋值使用

import Mixin from './mixin.js';
import  './directive.js';

require('./assets/base-less/global.less');
require('./assets/base-less/font.less');

axios.defaults.timeout = 120000;
axios.interceptors.request.use(function (config) {
  if (config.method.toUpperCase() === 'GET' && config.url !== undefined) {
    if (config.params == undefined) {
      config.params = {};
    }
    config.params.__preventCache = new Date().getTime(); //__preventCache：防止缓存，来了相同请求返回不同结果，不会调用缓存的东西
  }
  // 放置在请求头里传到后端验证
  config.headers['token'] = localStorage.getItem('user_token');
  config.headers['mcp_user'] = localStorage.getItem('userName');
  if (!isDebug) {
    config.withCredentials = true;
  }
  return config;
}, function (error) {
  return Promise.reject(error);
});
//返回状态判断
axios.interceptors.response.use((response) => { // 只需要成功的200
    return response;
}, (error) => {
  if(error.message.indexOf('timeout')>-1){
    ELEMENT.Message.error(error.message);
  }else{
    if (error.response.status * 1 === 401) {
      this.$router.push({path: '/'});
      window.location.href = loginUrl;
    } else if (error.response.status * 1 === 400) {
      ELEMENT.Message.error(error.response.data.message);
    } else if(error.response.status * 1 === 666){
      if(localStorage.getItem("user_token") != null){
        alert("Login information is invalid !");
      }
      localStorage.removeItem("userName");
      localStorage.removeItem("user_token");
      location.href = loginUrl;
    } else if(error.response.status * 1 === 999){
      localStorage.removeItem("userName");
      localStorage.removeItem("user_token");
      alert("Multiple logins on account !");
      location.href = loginUrl;
    } else if(error.response.status * 1 === 504){
      document.body.innerHTML='<h1 style="text-align: center;line-height: 100px;">504 Gateway Time-out</h1>'
    } else {
      ELEMENT.Message.error(error.response.data.message);
    }
  }
  //loading.close();
  //return Promise.reject(error);
});
new Vue({
    el: '#app',
    mixins: [Mixin],
    router,
    store,
    render: h => h(App),
  data: {
  eventHub: new Vue()
}
});

// Security check
router.beforeEach((to, from, next) => {
  window.scroll(0, 0);
next();
});

// Fix the nodelist.forEach property does't support issue
(function () {
  if (typeof NodeList.prototype.forEach === "function") return false;
  NodeList.prototype.forEach = Array.prototype.forEach;
})();
