<template>
  <div style="height: 46px;min-height: 46px;width: 100%">
    <header class="main-header clearfix">
      <div class="mcp-logo" @click="handleClose">
        <a href="/#/console/dashboard">
          <img src="../assets/img/logo-mcp-5.png" height="46" alt="logo">
        </a>
      </div>

      <!--下拉菜单-->
      <div class="navbar-menu">
          <div class="menu-info" v-for="menu in menus">
            <div :class="['menu-title',{'bgColor':menu.focus}]" @click="showMenu(menu)">
              <span>{{menu.menuName}}</span>
              <i class="icon-sort-desc ml5"></i>
            </div>
            <ul class="menu-flag" v-show="menu.focus">
              <li class="menu-item" v-for="submenu in menu.childrens">
                <a @click="openTab(submenu, menu)">{{submenu.menuName}}</a>
              </li>
            </ul>
          </div>
      </div>

      <!-- Navbar Right Menu -->
      <div class="navbar-user-info">
      <div class="user-info mr20">
        <div :class="['user-title',{'bgColor':focus}]" @click="showUser">
          <!--<i class="icon-bell mr10"></i>-->
          <span>{{userName}}</span>
          <i class="icon-sort-desc ml5"></i>
        </div>
        <div class="user-flag" v-show="focus">
          <a @click="openUser">My Profile</a>
          <a @click="openPassword">Change Password</a>
          <a href="javascript: void(0);" @click="logout()">Sign out</a>
        </div>
      </div>
    </div>
    </header>
    <!-- add dialog -->
    <el-dialog title="Change Password" :close-on-click-modal="false" :visible.sync="updatePasswordDialog" width="30%" height="60%" >
      <el-form label-width="160px" :model="updateForm">
        <!--<el-input v-model="connForm.routeId" type="hidden"></el-input>-->
        <el-form-item label="Username：">
          <el-input v-model="updateForm.userName" style="width: 90%" :disabled="true"  clearable></el-input>
        </el-form-item>
        <el-form-item label="Old Password：">
          <el-input v-model="updateForm.oldPassword" type="password" style="width: 90% " clearable></el-input>
        </el-form-item>
        <el-form-item label="New Password：">
          <el-input v-model="updateForm.password" type="password" style="width: 90% " clearable></el-input>
        </el-form-item>
        <el-form-item label="Confirm Password：">
          <el-input v-model="updateForm.confirmPassword" type="password" style="width: 90% " clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button @click="savePasswordFun()">Confirm</el-button>
          <el-button @click="closePasswordgFun()" >Cancel</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
  import {menu, menuUrl} from '@config/config';
  import * as Base from '@api/Base';
  import {apiUrl, loginUrl, logoutUrl} from '@config/config'
  import * as Code from '@config/code';
  import * as User from '@api/User';

  export default {
    name: 'mainHeader',
    data() {
      return {
        focus:'',
        menus: [],
        userName: '',
        loading: false,
        updatePasswordDialog: false,
        userList: {},
        updateForm: {
          userName: '',
          oldPassword: '',
          password: '',
          confirmPassword: ''
        }
      }
    },
    created() {
      this.getManagerInfoFun();
    },
    mounted() {
      this.userName = localStorage.getItem('userName')
    },
    methods: {
      //点击选项框以外的区域，关闭选项框
      handleClose(){
        this.menus.forEach(val=>{ this.$set(val,'focus',false)})
        this.focus=false
      },
      handleUserClose(){
        this.focus = false;
      },

      showMenu(menu){
        this.focus = false;
        if(menu.focus){
          menu.focus = false;
        }else{
          this.handleClose()
          menu.focus = true;
        }
      },
      showUser(){
        this.handleClose()
        if(this.focus){
          this.focus = false;
        }else {
          this.focus = true
        }
      },
      openUser(){
        this.$router.push('/user/MyProfile')
        this.focus = false;
      },
      openPassword(){
        this.$router.push('/user/ChangePassword')
        this.focus = false;
      },
      openTab(obj, menu) {
        if(obj.menuName === 'DS Routing'){
          location.href = `/#${obj.menuUrl}?v=${new Date().getTime()}`;
        }else {
          this.$router.push({
            path: obj.menuUrl
          });
        }
        menu.focus=false
      },
      getManagerInfoFun(){
        let params = {
          userName: localStorage.getItem("userName"),
        }
        User.getManagerInfo(params).then(res => {
          if (res.code === Code.SUCCESS) {
            if(res.items){
              this.userList = res.items;
              this.getMenus();
            }else{
              this.$message({message: "Please login again",type: 'error'});
              this.$router.push({
                path:'/login'
              })
            }

          }else {
            this.$message({message: "No data",type: 'error'});
          }
        })
      },
      getMenus() {
        if (this.userList.userRole === `0`) {
          Base.menus().then((res) => {
            if (res.code === Code.SUCCESS) {
              this.menus = res.items;
              this.handleClose()
            } else {
              this.$message({message: res.message, type: 'error'});
            }
          })
        }else{
          Base.userMenus().then((res) => {
            if (res.code === Code.SUCCESS) {
              this.menus = res.items;
            } else {
              this.$message({message: res.message, type: 'error'});
            }
          })
        }
      },
      logout(){
        let param = localStorage.getItem('user_token');
        User.logout({"token": param}).then(res => {
          localStorage.removeItem('userName');
          localStorage.removeItem('user_token');
          if (res.code === Code.SUCCESS) {
            this.$router.push({path: '/login'});
          } else {
            this.$message({message: res.message, type: 'error'});
            this.$router.push({path: '/login'});
          }
        });
      }
    },
  }
</script>

<style lang="less" scoped>
  .main-header{
    position: fixed;width: 100%;z-index: 999;
    background: #205081;
    padding: 0;
    height: 46px;
    display: flex;
  }


  .navbar-menu{flex:1;display: flex;justify-content: flex-start;color: #fff;}


  .menu-info, .user-info{
    line-height:46px;
    color: #fff;
    cursor: pointer;
    margin-left:20px;
  }
  .user-flag{
    position: fixed;
    right: 20px;
  }
  .user-title{
    /*margin-right: 59px;*/
    padding: 0 20px;
  }
  .menu-title, .user-title{
    padding-left:20px;
    padding-right:40px;
    position: relative;
    &:hover{
      background-color: #296ca3
    }
    .icon-sort-desc{
      right: 20px;
      top:6px;
      position: absolute;
    }
  }
.menu-flag, .user-flag{
  padding: 5px 0;
  background-color: #fff;
  box-shadow: #bbb 0 0 6px;
  z-index:1000;
  position: absolute;
  a{
    color: #586069;display: block;padding: 6px 30px 6px 20px;line-height: 22px;
    &:hover{background: #5391d0;color:#fff}
  }
}
  .bgColor{background-color: #2c6daf}
</style>

