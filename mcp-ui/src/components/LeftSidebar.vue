<template>
  <aside class="main-sidebar" style="background:#24292e">
    <section class="sidebar">
      <!-- 左侧列表开始 -->
      <ul class="sidebar-menu">
        <template v-for="menu in menus">
          <li>
            <a href="#">
              <i :class="menu.menuIconClass" class="mr5"></i>
              <span>{{menu.menuName}}</span>
              <span class="pull-right-container">
                <i class="fa fa-angle-left pull-right"></i>
              </span>
            </a>
            <ul class="treeview-menu menu-open" v-show='$route.path.indexOf(menu.menuUrl)>=0'>
              <router-link tag="li" :to="openTab(submenu.menuUrl)" activeClass="active"
                           v-for="submenu in menu.childrens" :key="submenu.menuId">
                <a href="javascript: void(0);"><i :class="submenu.menuIconClass"></i>{{submenu.menuName}}</a>
              </router-link>
            </ul>
          </li>
        </template>
      </ul>
      <!-- 左侧列表结束 -->
    </section>
  </aside>

</template>

<script>
  import {menu, menuUrl} from '@config/config';
  import routes from '@config/routes';
  import * as Code from '@config/code';
  import * as User from '@api/User';
  import * as Base from '@api/Base';
  import path from 'path'
  export default {
    name: 'leftSidebar',
    data() {

      return {
        menus: [],
        routes: routes,
        dataUser:{},
        _userName: '',
      }
    },
    created() {
      // this.getMenus();
      this.getManagerInfoFun();
    },
    mounted() {
      this._userName = localStorage.getItem("userName")
    },
    methods: {
      getManagerInfoFun() {
        User.getManagerInfo({
          param: {
            pageSize: "1",
            currentPage: "1"
          },
          info: {
            userName: localStorage.getItem("userName"),
          }
        }).then(res => {
          if (res.code === Code.SUCCESS) {
            this.dataUser = res.items.list[0];
            this.getMenus();
          } else {
            this.$message({message: res.message, type: 'error'});
          }
        })
      },
      getMenus() {
        if (this.dataUser.userRole === `0`) {
          Base.menus().then((res) => {
            if (res.code === Code.SUCCESS) {
              this.menus = res.items;
            } else {
              this.$message({message: res.message, type: 'error'});
            }
          })
        } else {
          Base.menusPart().then((res) => {
            if (res.code === Code.SUCCESS) {
              this.menus = res.items;
            } else {
              this.$message({message: res.message, type: 'error'});
            }
          })
        }
      },
      openTab(url) {
        return path.resolve(url);
      }
    }
  }
</script>

<style lang="less" scoped>
  .sidebar-menu{background: #24292e;
    li {
      font-size: 12px;
      .treeview-menu {
        background: #2c3b41;
        li {
          .level-menu {
            background: #fff;
            width: 6px;
            height: 6px;
            margin: 0 10px 0 18px;
            display: inline-block;
            position: relative;
            top: -1.6px;}
          &:hover { .level-menu {background: #fff;} } }
        li.active {
          .level-menu {background: #fff;} }
      }
    }
  }
  .skin-blue .sidebar-menu > li.active > a:hover{ border-left-color:#0a568c }
  .skin-blue .sidebar-menu > li.active > a{ border-left-color:#2c3b41 }
</style>
