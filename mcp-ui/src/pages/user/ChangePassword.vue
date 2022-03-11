<template>
    <section>
      <!--头部-->
      <div class="header">
        <div class="title">
          <el-breadcrumb separator-class="el-icon-arrow-right">
            <el-breadcrumb-item>Change Password</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </div>

      <!--内容-->
      <div class="container gray">
        <div class="table shadow mt0">
          <el-form label-position="top" :rules="rules" :model="userForm" ref="userForm">
            <el-form-item label="User Name" >
              <el-input v-model="userForm.userName" readonly="true"></el-input>
            </el-form-item>
            <el-form-item label="Old Password" prop="oldPassword">
              <el-input type="password" v-model="userForm.oldPassword" clearable></el-input>
            </el-form-item>
            <el-form-item label="New Password" prop="newPassword">
              <el-input type="password" v-model="userForm.newPassword" clearable></el-input>
            </el-form-item>
            <el-form-item label="Confirm Password" prop="confirmPassword">
              <el-input type="password" v-model="userForm.confirmPassword" clearable></el-input>
            </el-form-item>
          </el-form>
          <el-button @click="savePassword('userForm')">Save</el-button>
        </div>
      </div>
    </section>
</template>

<script>
  import * as Code from '@config/code';
  import * as Tips from '@config/tips';
  import * as User from '@api/User';
    export default {
        name: "ChangePassword",
      data(){
        let passwordReg = /^\S{6,16}$/;
        let checkOldPassword = (rule, value, callback) => {
          if (!value) {
            return callback(new Error('Old password is required.'));
          }else{
            callback();
          }
        };
        let checkNewPassword = (rule, value, callback) => {
          if (!value) {
            return callback(new Error('New password is required.'));
          } else if (this.userForm.oldPassword === this.userForm.newPassword) {
            return callback(new Error('The new password is not the same as the old one.'));
          } else if(!passwordReg.test(value)) {
            return callback(new Error('The password should be 6-16 non-null characters.'));
          }else{
            callback();
          }
        };
        let confirmPassword = (rule, value, callback) => {
          if (!value) {
            return callback(new Error('Confirm password is required.'));
          } else if (!passwordReg.test(value)) {
            return callback(new Error('The password should be 6-16 non-null characters.'));
          } else if (value !== this.userForm.newPassword) {
            return callback(new Error('The two passwords you entered do\'t match.'));
          } else {
            callback();
          }
        }
        return{
          userForm:{
            userName: localStorage.getItem("userName"),
            oldPassword: '',
            newPassword: '',
            confirmPassword: ''
          },
          rules:{
            oldPassword: [{required: true, validator: checkOldPassword, trigger: 'blur'}],
            newPassword: [{required: true, validator: checkNewPassword, trigger: 'blur'}],
            confirmPassword: [{required: true, validator: confirmPassword, trigger: 'blur'}],
          }
        }
      },
      methods:{
        getmd5(str){
          return hex_md5(str);
        },

        savePassword(formName){
          this.$refs[formName].validate((valid) => {
            if (valid) {
              User.updatePassword({
                userName: this.userForm.userName,
                oldPassword: this.getmd5(this.userForm.oldPassword),
                confirmPassword: this.getmd5(this.userForm.newPassword),
              }).then(res => {
                if (res.code === Code.SUCCESS) {
                  this.$message({message: Tips.MODIFY_SUCCESS, type: 'success'});
                  this.$router.push('/login');
                } else {
                  this.$message({message: res.message, type: 'error', center: true});
                }
              }).catch(() => {
              })
            }
          })
        },
      }
    }
</script>

<style lang="less" scoped>
  .el-form{padding: 20px;margin-left: 20%;
    .el-input, .el-select{width: 50%}
  }
  .table {
    position: relative;
    .el-button {
      position: absolute;
      right: 20px;
      bottom: 15px;
    }
  }
  .subtitle{background-color: #f7f7f7;height: 35px;line-height: 35px;padding-left: 15px}
</style>
