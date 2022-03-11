<template>
  <section>
    <!--头部-->
    <div class="header">
      <div class="title">
        <el-breadcrumb separator-class="el-icon-arrow-right">
          <el-breadcrumb-item>My Profile</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
    </div>

    <!--内容-->
    <div class="container pd20">
      <div class="user-list middle shadow mt0">
        <ul>
          <li>
            <span class="label">User Name： </span>
            <span class="deepblue">{{userList.userName}}</span>
          </li>
          <li>
            <span class="label">Email： </span>
            <span class="deepblue">{{userList.email}}</span>
          </li>
          <li>
            <span class="label">Phone： </span>
            <span class="deepblue" :formatter="setPhone(userList.phone)">{{this.phone}}</span>
          </li>
          <li>
            <span class="label">Role： </span>
              <template slot-scope>
                <span class="deepblue">{{userList.userRole === "0" ? 'Administrator' : 'User'}}</span>
              </template>
          </li>
        </ul>
      </div>
    </div>
  </section>
</template>

<script>
  import * as Code from '@config/code';
  import * as Tips from '@config/tips';
  import * as User from '@api/User';
    export default {
        name: "MyProfile",
      data(){
          return{
            userList: {},
            phone: '',
          }
      },
      created(){
          this.queryProfile();
      },
      methods:{
        queryProfile(){
          User.queryList({
            userName: localStorage.getItem("userName")
          }).then(res => {
            if (res.code === Code.SUCCESS) {
              this.userList= res.items[0];
            }
            else {
              this.$message({message: res.message, type: 'error', center: true});
            }
          })
        },

        //手机号显示中间设置***
        setPhone(item) {
          if (item === undefined) {
            return "";
          } else {
            this.phone = item.substr(0, 3) + '****' + item.substr(7);
            return this.phone;
          }
        },
      }
    }
</script>

<style lang="less" scoped>
.user-list{
  height: 100%;
  width: 100%;
  ul{
    margin: 50px;
    width: 30%;
    height: 50%;
    li{
      margin: 15px;
      .label{
        text-align: right;
        display: inline-block;
        margin-right: 30px;
        width: 100px}
    }
  }
}
</style>
