<template>
  <section>
    <!--头部-->
    <div class="header">
      <div class="title">
        <el-breadcrumb separator-class="el-icon-arrow-right">
          <el-breadcrumb-item>User List</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
    </div>

    <!--内容-->
    <div class="container pd20">
      <div class="search">
        <div class="left">
          <el-input size="medium"  v-model="userName" placeholder="Search User" style="width: 220px" @keyup.enter.native="queryUser" clearable></el-input>
          <el-button class="ml5" size="small" icon="el-icon-search" @click="queryUser"></el-button>
        </div>
        <div class="right">
          <el-button size="mini" style="height: 36px;" @click="goUser" v-if="isUser">Add User</el-button>
        </div>
      </div>
      <div class="table shadow">
        <el-table empty-text="No data" :data="userList">
          <el-table-column label="#" type="index"></el-table-column>
          <el-table-column label="Role" prop="userRole">
            <template slot-scope="scope">
              <span>{{scope.row.userRole === "0" ? 'Administrator' : 'User'}}</span>
            </template>
          </el-table-column>
          <el-table-column label="User Name" prop="userName">
          </el-table-column>
          <el-table-column label="Email" prop="email">
          </el-table-column>
          <el-table-column label="Phone" prop="phone" :formatter="setPhone">
          </el-table-column>
          <el-table-column width="100" label="Status" prop="userStatus">
            <template slot-scope="scope">
              <el-switch
                :disabled="scope.row.userRole === '0'|| !isUser"
                active-color="#3d82ca"
                inactive-color="#bbb"
                v-model="scope.row.userStatus"
                @change="changeSwitchFun(scope.row)"
                active-value="0"
                inactive-value="1">
              </el-switch>
            </template>
          </el-table-column>
          <el-table-column width="80" label="Actions">
            <template slot-scope="scope">
              <i class="icon-edit mr20" title="Edit User" @click="goUser(scope.row)"></i>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </section>
</template>

<script>
  import * as Code from '@config/code';
  import * as Tips from '@config/tips';
  import * as User from '@api/User';
    export default {
        name: "UserList",
      data(){
          return{
            userName: '',
            userList:[],
            managerList: [],
            isUser: '',
            userArr: '',
          }
      },
      created(){
          this.getManagerInfoFun();
      },
      methods:{
        getManagerInfoFun() {
          User.getManagerInfo({
              userName: localStorage.getItem("userName")
          }).then(res => {
            if (res.code === Code.SUCCESS
            ) {
              this.managerList = res.items;
              if(this.managerList.userRole === '1'){
                this.isUser = false;
              }else{
                this.isUser = true;
              }
              this.queryUser()
            }
            else {
              this.$message({message: res.message, type: 'error', center: true});
            }
          })
        },


        goUser(row){
          this.$router.push({
            path: 'AddUser',
            query:{
              userId: row.userId,
              userName: row.userName,
              email:row.email,
              phone:row.phone,
              role: row.userRole,
              operator: this.managerList.userRole
            }
          })
        },

        queryUser(){
          let item = this.userName;
          this.userList = []
          if(this.managerList.userRole === '1'){
            if(localStorage.getItem("userName").indexOf(this.userName,0) !== -1 || this.userName === '') this.userList.push(this.managerList)
          }else {
            item = this.userName;
            User.queryList({
              userName: item
            }).then(res => {
              if (res.code === Code.SUCCESS
              ) {
                this.userList = [];
                for(let list of res.items){
                  if(list.userName === localStorage.getItem("userName")){
                    this.userList.unshift(list);
                  }
                  if(list.userRole === '1'){
                    this.userList.push(list);
                  }
                }

              }
              else {
                this.$message({message: res.message, type: 'error', center: true});
              }
            }).catch(() => {
            })
          }

        },

        // 修改状态按钮
        changeSwitchFun(row) {
          this.$confirm('Are you sure disable this user?', 'Message', {
            confirmButtonText: 'Confirm',
            cancelButtonText: 'Cancel',
          }).then(() => {
            let params = {
              userId: row.userId,
              userName: row.userName,
              userStatus: row.userStatus,
              updateUser: row.userName
            }
            User.updateStatus(params).then(res => {
              if (res.code === Code.SUCCESS) {
                this.$message({message: Tips.UPDATE_SUCCESS, type: 'success'});
                this.queryUser()
              } else {
                this.$message({message: res.message, type: 'error'});
              }
            })
          }).catch(() => {
            row.userStatus = row.userStatus === "1" ? '0' : '1'
          })
        },

        //手机号显示中间设置***
        setPhone(row, column) {
          let d = row[column.property];
          if (d === undefined) {
            return "";
          } else {
            d = d.substr(0, 3) + '****' + d.substr(7);
            return d;
          }
        },
      }
    }
</script>

<style lang="less" scoped>
  a{cursor: pointer;}
  .search{
    display: flex;
    justify-content: space-between;
    .left{}
    .right{}
  }
</style>
