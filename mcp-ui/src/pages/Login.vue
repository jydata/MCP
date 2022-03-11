<template>
  <div class="login-box">
    <div class="header">
      <div class="mcp-logo" style="padding: 0;" >
        <a href="/">
          <img src="../assets/img/logo-mcp-5.png" height="46px" alt="logo">
        </a>
      </div>
      <!--<span  class="mcp-text">MCP</span>-->
    </div>
    <div class="main">
        <div class="container">
          <div class="text-area">
            <h1 style="font-size: 28px;">MySQL Convert to Phoenix(HBase)</h1>
            <p style="font-size: 16px;color: #333;padding-top: 40px;width: 80%;line-height: 32px;word-break: break-word">Real-time data synchronization middleware based on Netty's distributed architecture. Professional because of focus.</p>
          </div>
          <div class="container-lg">
            <div class="mcp-title">
              <span>Sign to MCP</span>
            </div>
            <div class="login-area">
              <el-form :model="userForm" :rules="rules" ref="userForm" size="medium" label-width="80px" label-position="top">
                <el-form-item prop="userName" label="Username" >
                  <el-input type="text" v-model="userForm.userName" clearable></el-input>
                </el-form-item>
                <el-form-item prop="password" label="Password" style="padding-top: 20px">
                  <el-input type="password"   v-model="userForm.password"  @keyup.enter.native="submitForm('userForm')" clearable></el-input>
                </el-form-item>
                <div class="login-bottom">
                  <el-button :loading="loading"  @click="submitForm('userForm')" >Sign In</el-button>
                  <!--<el-button size="small" type="primary" @click="resetForm('userForm')" >取 消</el-button>-->
                </div>
              </el-form>
            </div>
          </div>
      </div>
    </div>
    <div class="footer">
        <ul class="ft-area">
          <li class="f12 pt10 gray">© 2018 MCP, JYdata</li>
          <li>
            <a href="https://github.com/jydata/MCP" target="_blank">
              <i class="icon-github" title="Github"></i>
            </a>
          </li>
        </ul>
    </div>
  </div>
  <!--<el-form-item label="记住密码:" prop="conPassword">-->
  <!--<el-checkbox-group>-->
  <!--<el-checkbox label="" name="type" v-model="userForm.conPassword"></el-checkbox>-->
  <!--</el-checkbox-group>-->
  <!--&lt;!&ndash;    <el-input v-model="userForm.conPassword"></el-input>&ndash;&gt;-->
  <!--</el-form-item>-->


</template>

<script>
  import {page, pageSize, pageSizes} from '@config/config';
  import * as Code from '@config/code';
  import * as Tips from '@config/tips';
  import * as User from '@api/User';

  export default {
    data() {
      var checkUserName = (rule, value, callback) => {
        if (value === '' || value.trim().length <= 0) {
          return callback(new Error('Username is required'));
        } else if(value.lastIndexOf(" ") !== -1){
          return callback(new Error('Username cannot contain spaces'));
        }else {
          callback();
        }
      };
      var checkPassword = (rule, value, callback) => {
        if (!value) {
          return callback(new Error('Password is required'));
        } else {
          callback();
        }
      };
      return {
        userForm: {
          userName: '',
          password: '',
          conPassword: ''
        },
        rules: {
          userName: [{validator: checkUserName, trigger: 'blur'}],
          password: [{validator: checkPassword, trigger: 'blur'}],
        },
        loading:false
      };
    },
    methods: {
      submitForm(formName) {
        this.$refs[formName].validate((valid) => {
            if (valid) {
              this.loading=true
              let userName = this.userForm.userName;
              let encPwd = this.getmd5(this.userForm.password);
              let param = {
                userName: userName,
                password: encPwd
              };
              User.login(param).then(res => {
                if (res.code === Code.SUCCESS) {
                  //debugger;
                  localStorage.setItem('user_token', res.items);
                  localStorage.setItem('userName', userName);
                  this.$router.push({path: '/console/dashboard'});
                } else if (res.code === Code.ERROR) {
                  this.$message({message: res.message, type: 'error'});
                  this.$router.push({path: '/login'});
                } else {
                  this.$message({message: res.message, type: 'error'});
                  this.$router.push({path: '/login'});
                }
                this.loading=false
              }).catch(err=>{
                this.loading=false
              })
            } else {
              return false;
            }
        });
      },
      resetForm(formName) {
        this.userForm.userName = '';
        this.userForm.password = '';
      },
      //MD5加密
      getmd5(str) {
        return hex_md5(str);
      }
    },
    directives: {
      autoHeight: {
        update(el, binding, vnode) {
          const vm = vnode.context, $el = vm.$el, $srcoll = '.el-table__body-wrapper', $modules = '.role-search-area';
          let height = $el.clientHeight - $($modules).height() - 110;
          $(el).find($srcoll).css({maxHeight: height});
        }
      }
    }
  }
</script>

<style lang="less" scoped>
  .login-box{margin: 0;padding: 0;width: 100%;height: 100%;display: flex;flex-direction: column;
    .header{background-color: #205081;height: 46px;}

    .main{flex: 1;display: flex;vertical-align: middle;justify-content: center;overflow: auto;
      .container{
        margin: auto;
        display: flex;
        justify-content: space-around;
        flex-wrap: wrap;
        width: 80%;
        .text-area{width:60%;margin:5% auto;}
        .container-lg{margin-left: 40px;
          .mcp-title{width: 100%;height: 40px;text-align: center;font-size: 20px;}
          .login-area{background-color: #fafbfc;padding: 40px;
            .el-input__inner{background-color: #faffbd;}
            .login-bottom{margin-top: 70px;
              .el-button{width: 290px;height: 50px;background-color: #0b7fad;color: #fff;border-radius: 8px;font-size:14px;margin-left: 0;} } } } }
    }
  }

  .el-form-item{margin-bottom: 20px}
</style>
