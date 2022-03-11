<template>
  <section>
    <!--头部-->
    <div class="header">
      <div class="title">
        <el-breadcrumb separator-class="el-icon-arrow-right">
          <el-breadcrumb-item :to="title.from.url">{{title.from.name}}</el-breadcrumb-item>
          <el-breadcrumb-item>{{title.to.name}}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
    </div>

    <!--内容-->
    <div class="container gray">
      <div class="table shadow mt0">
        <div class="subtitle">User Detail</div>
        <el-form label-position="top" :rules="rules" ref="userData" :model="userData">
          <el-form-item label="User Name" prop="userName">
            <el-input maxlength=20 v-model="userData.userName" :readonly="isEdit"></el-input>
          </el-form-item>
          <el-form-item label="Email" prop="email">
            <el-input v-model="userData.email"clearable></el-input>
          </el-form-item>
          <el-form-item label="Phone" prop="phone">
            <el-input v-model="userData.phone"clearable></el-input>
          </el-form-item>
          <el-form-item label="Role" prop="role">
            <el-select v-model="userData.role" placeholder="Please select" :disabled="isOwner">
              <el-option label="Administrator" value="0"></el-option>
              <el-option label="User" value="1"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="Password" prop="password" v-if="isAdd">
            <el-input type="password" v-model="userData.password" class="yellow" clearable></el-input>
          </el-form-item>
          <el-form-item label="Confirm Password" prop="confirmPassword" v-if="isAdd">
            <el-input type="password" v-model="userData.confirmPassword" class="yellow" clearable></el-input>
          </el-form-item>
        </el-form>
        <div class="button">
          <el-button @click="resetPassword" v-show="isReset">Reset Password</el-button>
          <el-button @click="saveUser('userData')" v-show="isAdd">Save</el-button>
          <el-button @click="editUser('userData')" v-show="isEdit">Save</el-button>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
  import * as Code from '@config/code';
  import * as Tips from '@config/tips';
  import * as User from '@api/User';
  export default {
    name: "AddUser",
    data(){
      let reg = /^[a-zA-Z]+[\w]*$/;
      let emailReg = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
      let passwordReg = /^\S{6,16}$/;
      let phoneReg = /^1[35678]\d{9}$/;
      // 检查用户名是否为空

      let checkUserName = (rule, value, callback) => {
        if (value === '' || value.trim().length <= 0) {
          return callback(new Error('Username is required.'));
        } else if (value.lastIndexOf(" ") !== -1) {
          return callback(new Error('Username cannot contain spaces.'));
        } else if (!reg.test(value)) {
          callback(new Error('Only numbers,letters and underline are allowed, and letters must be in front'));
        } else {
          callback();
        }
      };
      // 检查email是否为空
      let checkEmail = (rule, value, callback) => {
        if (value === '' || value.trim().length <= 0) {
          return callback(new Error('email is required'));
        } else if (!emailReg.test(value)) {
          return callback(new Error('email format is incorrect'));
        } else {
          callback();
        }
      };
      // 检查手机号是否为空
      let checkPhoneNumber = (rule, value, callback) => {
        if (value === '' || value.trim().length <= 0) {
          return callback(new Error('Phone is required'));
        } else if (!phoneReg.test(value)) {
          return callback(new Error('Phone format is incorrect'));
        } else {
          callback();
        }
      };
      let checkPassword1 = (rule, value, callback) => {
          if (!value) {
            return callback(new Error('Password is required'));
          } else if (this.userData.password === this.userData.userName) {
            return callback(new Error('The password cannot be the same as the user name'));
          } else if(!passwordReg.test(value)) {
            return callback(new Error('The password should be 6-16 non-null characters'));
          }else{
            callback();
          }
      };
      let checkPassword2 = (rule, value, callback) => {
          if (!value) {
            return callback(new Error('ConfirmPassword is required'));
          }else if(!passwordReg.test(value)) {
            return callback(new Error('The password should be 6-16 non-null characters'));
          } else if (value !== this.userData.password) {
            return callback(new Error('The two passwords you entered don\'t match'));
          }else {
            callback();
          }
      };
      return{
        title: {from:{},to:{}},
        isOwner: '',
        isAdd: '',
        isEdit:'',
        isReset: '',
        user: '',
        userData: {
          userId: 0,
          userName: '',
          email: '',
          phone: '',
          role: '',
          password: '',
          confirmPassword: ''
        },
        rules: {
          userName: [{required: true, validator: checkUserName, trigger: 'blur'}],
          email: [{required: true, validator: checkEmail, trigger: 'blur'}],
          phone: [{required: true, validator: checkPhoneNumber, trigger: 'blur'}],
          role: [{required: true, message: 'Please select role', trigger: 'change'}],
          password: [{required: true, validator: checkPassword1, trigger: 'blur'}],
          confirmPassword: [{required: true, validator: checkPassword2, trigger: 'blur'}],
        }
      }
    },
    created(){
      let query = this.$route.query
      if(typeof(query.userName) === "undefined"){
        this.isEdit = false;
        this.isReset = false;
        this.isAdd = true;
        this.title = 'User List -> Add User'
        this.title = {
            from:{name:'User List',url:'/user/UserList'},
            to:{name:'Add User'}
          };
      }else{
        this.title = 'User List -> Edit User'
        this.title = {
            from:{name:'User List',url:'/user/UserList'},
            to:{name:'Edit User'}
          };
        this.isEdit = true;
        this.isAdd = false;
        if(query.operator == 1 || query.role == 0){
          this.isReset = false;
        }
        else {
          this.isReset = true;
        }
        this.userData.userId = query.userId;
        this.userData.userName = query.userName;
        this.userData.email = query.email;
        this.userData.phone = query.phone;
        this.userData.role = query.role;
      }
      this.isOwner= this.userData.userName === localStorage.getItem("userName")
    },
    methods:{
       getmd5(str) {
        return hex_md5(str);
      },

      //保存用户
      saveUser(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            User.save({
              userName: this.userData.userName,
              email: this.userData.email,
              phone: this.userData.phone,
              userRole: this.userData.role,
              createUser: localStorage.getItem("userName"),
              password: this.getmd5(this.userData.password),
            }).then(res => {
              if (res.code === Code.SUCCESS) {
                this.$message({message: Tips.SAVE_SUCCESS, type: 'success'});
                this.$router.push('UserList')
              } else {
                this.$message({message: res.message, type: 'error', center: true});
              }
            }).catch(() => {
            })
        }
        })
      },

      // 修改用户
      editUser(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            User.update({
              userId: this.userData.userId,
              userName: this.userData.userName,
              email: this.userData.email,
              phone: this.userData.phone,
              userRole: this.userData.role,
              createUser: localStorage.getItem("userName"),
            }).then(res => {
              if (res.code === Code.SUCCESS) {
                this.$message({message: Tips.SAVE_SUCCESS, type: 'success'});
                this.$router.push('UserList')
              } else {
                this.$message({message: res.message, type: 'error', center: true});
              }
            }).catch(() => {
            })
        }
        })
      },

      //重置密码
      resetPassword() {
            User.resetPassword({
              userId: this.userData.userId,
              userName: this.userData.userName,
              updateUser:    localStorage.getItem("userName")
            }).then(res => {
              if (res.code === Code.SUCCESS) {
                this.$message({message: Tips.RESET_SUCCESS, type: 'success'});
                this.$router.push('UserList')
              } else {
                this.$message({message: res.message, type: 'error', center: true});
              }
            }).catch(() => {
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
    .button {
      position: absolute;
      right: 20px;
      bottom: 15px;
    }
  }
  .subtitle{background-color: #f7f7f7;height: 35px;line-height: 35px;padding-left: 15px}
</style>
