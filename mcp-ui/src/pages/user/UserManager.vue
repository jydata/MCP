<template>
  <section>
    <!-- header -->
    <div class="header">
      <div class="top clearfix">
        <div class="left">
          <i class="icon-p-service"></i>
          <span>User Manager</span>
        </div>
        <div class="right" v-if="managerList.userRole==='0'">
          <el-button @click="searchTable(1)">Query</el-button>
          <el-button @click="addUserInfoFun" :loading="loadingAdd">Add</el-button>
        </div>
      </div>
      <div class="down" v-if="managerList.userRole==='0'">
        <div class="list clearfix">
          <div class="left ">
            <div class="press">
              <span class="key">UserName</span>
              <el-input v-model="selectForm.username" clearable></el-input>
            </div>
            <div class="press">
              <span class="key" >Mobile Phone</span>
              <el-input v-model="selectForm.phone" clearable></el-input>
            </div>
            <div class="press">
              <span class="key">Status</span>
              <el-select v-model="selectForm.userstatus1" placeholder="Please select" clearable>
                <el-option
                  v-for="item in userStatusList"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </div>
            <div class="press">
              <span class="key">User Role</span>
              <el-select v-model="selectForm.userRole" placeholder="Please select"  clearable>
                <el-option
                  v-for="item in userRoleList"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- list -->
    <div class="table-master">
      <el-table :data="dataUser" border v-autoHeight highlight-current-row>
        <el-table-column type="index" label="ID" align="center" width="60px"></el-table-column>
        <el-table-column prop="userName" label="UserName" align="center"></el-table-column>
        <el-table-column prop="email" label="Email" align="center" show-overflow-tooltip
                         sortable></el-table-column>
        <el-table-column prop="phone" label="Mobile Phone" align="center" show-overflow-tooltip
                         sortable :formatter="setPhone"></el-table-column>
        <el-table-column prop="userRole" label="User Role" align="center"sortable>
          <template slot-scope="scope">
            <span>{{scope.row.userRole === "0" ? 'Administrator' : 'User'}}</span>
          </template>
        </el-table-column>
        <el-table-column prop="userStatus" label="Status" align="center" width="100">
          <template slot-scope="scope">
            <el-switch
              :disabled="managerList.userRole === scope.row.userRole || managerList.userRole === `1`"
              active-color="#3d82ca"
              inactive-color="#bbb"
              v-model="scope.row.userStatus"
              @change="changeSwitchFun(scope.$index,scope.row)"
              active-value="0"
              inactive-value="1">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column prop="createUser" label="Creater" align="center" show-overflow-tooltip></el-table-column>
        <el-table-column prop="updateUser" label="Modifier" align="center"></el-table-column>
        <el-table-column prop="updateTime" label="Modify Time" sortable align="center"></el-table-column>
        <!--list中的操作-->
        <el-table-column label="Handle" align="center">
          <template slot-scope="scope">
            <el-button @click="editUserInfoFun(scope.$index, scope.row)" :loading="loadingEdit" type="text"
                       v-if="scope.row.userStatus==='0'&& (managerList.userRole==='0'&& scope.row.userName === managerList.userName || scope.row.userRole === `1`)||(managerList.userRole === '1'&& scope.row.userName === managerList.userName)">
              Modify
            </el-button>
            <el-button @click="formatPasswordsFun(scope.$index,scope.row)" :loading="loadingReset" type="text"
                       style="height:18px;float: right"
                       v-if="scope.row.userStatus==='0' && managerList.userRole !== '1'">Reset Password</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
          align="center"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :page-sizes="pageSizes"
          :page-size="pageSize"
          :current-page="page"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
      </el-pagination>
    </div>
    <!-- add   (managerList.userRole !== scope.row.userRole || managerList.userName === scope.row.userName)-->
    <el-dialog :title="showTitle" :close-on-click-modal="false" :visible.sync="addUserDialog" :show-close="false"
               width="40%" height="80%"
               :before-close="closeEditDialogFun">
      <el-form label-width="160px" :model="userForm" :rules="rules" ref="userForm" class="item-error">
        <el-form-item label="UserName：" prop="userName"  v-show="isShowUserName">
          <el-input v-model="userForm.userName" style="width: 75%" clearable :disabled="isDisabledUserName"></el-input>
        </el-form-item>
        <el-form-item label="Email：" prop="email" v-show="isShowEmail">
          <el-input v-model="userForm.email" style="width: 75%" clearable></el-input>
        </el-form-item>
        <el-form-item label="Mobile Phone：" prop="phone" v-show="isShowPhone">
          <el-input v-model="userForm.phone" style="width: 75%" clearable></el-input>
        </el-form-item>
        <el-form-item label="User Role：" prop="userRole"  v-show="isShowUserRoles">
          <el-select v-model="userForm.userRole" filterable placeholder="Please select" style="width: 75%">
            <el-option
              v-for="item in userRoleList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Password：" prop="password"  v-show="isShowPassword">
          <el-input type="password" v-model="userForm.password" style="width: 75%" clearable></el-input>
        </el-form-item>
        <el-form-item label="Repeat Password：" prop="repeatPassword" style="position: relative; padding-top: 0"
                      v-show="isShowRepeatPassword">
          <el-input type="password" v-model="userForm.repeatPassword" style="width: 75%" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button @click="insertUserInfoFun('userForm')" v-show="isShowInsertButton"><i class="el-icon-circle-plus"></i> Insert</el-button>
          <el-button @click="resetForm('userForm')" v-show="isShowResetButton"><i class="el-icon-refresh"></i> Reset</el-button>
          <el-button @click="editUserFun('userForm')" v-show="isShowSaveButton"><i class="el-icon-edit-outline"></i> Edit</el-button>
          <el-button @click="closeInsertUserDialogFun('userForm')"><i class="el-icon-circle-close"></i> Close</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
    <!-- reset password -->
    <el-dialog :close-on-click-modal="false" :visible.sync="resetPasswordDialog" width="40%" height="80%" center>
      Do you confirm to reset password?
      <span slot="footer" class="dialog-footer">
        <el-button @click="resetPasswordDialog = false">No</el-button>
        <el-button @click="resetPasswordFun()">Yes</el-button>
      </span>
    </el-dialog>
    <!--状态弹框按钮-->
    <el-dialog :close-on-click-modal="false" title="Tips" :visible.sync="changeUserStatusDialog" width="30%"
               :before-close="closeDialogFun">
      <span v-text="showMsg"></span>
      <span slot="footer" class="dialog-footer">
              <el-button @click="closeDialogFun">No</el-button>
              <el-button @click="updateStatusFun">Yes</el-button>
      </span>
    </el-dialog>
  </section>
</template>

<script>
  import {page, pageSize, pageSizes} from '@config/config';
  import * as Code from '@config/code';
  import * as Tips from '@config/tips';
  import * as User from '@api/User';
  import JsEncrypt from 'jsencrypt'
  import crypto from 'crypto';

  // 默认配置
  export default {
    name: 'dataUser',
    components: {},
    data() {
      var emailReg = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
      var passwordReg = /^\S{6,16}$/;
      var phoneReg = /^1[35678]\d{9}$/;
      // 检查用户名是否为空

      var checkUserName = (rule, value, callback) => {
        if (value === '' || value.trim().length <= 0) {
          return callback(new Error('用户名不能为空'));
        } else if (value.lastIndexOf(" ") !== -1) {
          return callback(new Error('用户名中不能包含空格'));
        } else {
          callback();
        }
      };
      // 检查email是否为空
      var checkEmail = (rule, value, callback) => {
        if (value === '' || value.trim().length <= 0) {
          return callback(new Error('email不能为空'));
        } else if (!emailReg.test(value)) {
          return callback(new Error('email格式不正确'));
        } else {
          callback();
        }
      };
      // 检查手机号是否为空
      var checkPhoneNumber = (rule, value, callback) => {
          if (value === '' || value.trim().length <= 0) {
            return callback(new Error('手机号不能为空'));
          } else if (!phoneReg.test(value)) {
            return callback(new Error('手机号格式不正确'));
          } else {
          callback();
        }
      };
      var checkPassword1 = (rule, value, callback) => {
        if(this.isShowPassword == true){
          if (!value) {
            return callback(new Error('密码不能为空'));
          } else if (this.userForm.password === this.userForm.userName) {
            return callback(new Error('密码不能和用户名的正序相同'));
          } else if(!passwordReg.test(value)) {
            return callback(new Error('密码应为6到16位非空字符'));
          }else{
            callback();
        }
        }else{
          callback();
        }
      };
      var checkPassword2 = (rule, value, callback) => {
        if(this.isShowRepeatPassword == true){
          if (!value) {
            return callback(new Error('确认密码不能为空'));
          }else if(!passwordReg.test(value)) {
            return callback(new Error('密码应为6到16位非空字符'));
          } else if (value !== this.userForm.password) {
            return callback(new Error('两次输入的密码不一致'));
          }else {
            callback();
          }
        }else{
          callback();
        }
      };
      return {
        userStatusList: [{value: '0', label: 'Enable'}, {value: '1', label: 'Disable'}],
        userRoleList: [{value: '0', label: 'Administrator'}, {value: '1', label: 'User'},],
        page: page,
        pageSize: pageSize,
        pageSizes: pageSizes,
        total: 0,
        loadingAdd: false,
        addUserDialog: false,
        editLinkDialog: false,
        resetPasswordDialog: false,
        changeUserStatusDialog: false,
        isDisabledUserName: false,
        isDisabledEdit: false,
        isDisabledResetPassword: false,
        isShowUserName: false,
        isShowEmail: false,
        isShowPhone: false,
        isShowUserRoles: false,
        isShowPassword: false,
        isShowRepeatPassword: false,
        isShowSaveButton: false,
        isShowResetButton: false,
        isShowInsertButton: false,
        loadingEdit: false,
        loadingReset: false,
        dataUser: [],
        managerList: {},
        showMsg: '',
        showTitle: '',
        userForm: {
          userId: '',
          userName: '',
          email: '',
          phone: '',
          password: '',
          repeatPassword: '',
          userRole: '',
          userStatus: '',
          olderEmail: '',
          olderPhoneNumber: '',
          orderUserRoles: '',
        },
        selectForm: {
          username: '',
          userstatus1: '',
          phone: '',
          userRole: '',
        },
        rules: {
          userName: [{required: true, validator: checkUserName, trigger: 'blur'}],
          email: [{required: true, validator: checkEmail, trigger: 'blur'}],
          phone: [{required: true, validator: checkPhoneNumber, trigger: 'blur'}],
          userRole: [{required: true, message: '请选择用户角色', trigger: 'change'}],
          password: [{required: true, validator: checkPassword1, trigger: 'blur'}],
          repeatPassword: [{required: true, validator: checkPassword2, trigger: 'blur'}],
        }
      }
    },
    created() {
      this.getManagerInfoFun();
    },
    methods: {
      getManagerInfoFun() {
        User.getManagerInfo({
          param: {
            pageSize: "1",
            currentPage: "1"
          },
          info: {
            userName: localStorage.getItem("userName")
          }
        }).then(res => {
          if (res.code === Code.SUCCESS
          ) {
            this.managerList = res.items.list[0];
            this.getManagerPageFun();
          }
          else {
            this.$message({message: res.message, type: 'error', center: true});
          }
        })
      },
      //按条件查询用户信息
      searchTable(page) {
        if(page) this.page=1;
        User.queryList({
          param: {
            pageSize: this.pageSize,
            currentPage: page ?  1 : this.page

          },
          info: {
            userName: this.selectForm.username,
            userStatus: this.selectForm.userstatus1,
            phone: this.selectForm.phone,
            userRole: this.selectForm.userRole,
          }
        }).then(res => {
          if (res.code === Code.SUCCESS
          ) {
            this.dataUser = res.items.list;
            this.total = res.items.total;
          } else {
            this.$message({message: res.message, type: 'error', center: true});
          }
        })
      },
      // 根据登录用户权限来获取列表数据
      getManagerPageFun() {
        User.getManagerList({
          param: {
            pageSize: this.pageSize,
            currentPage: this.page
          },
          info: {
            userName: this.managerList.userName,
            userRole: this.managerList.userRole
          }
        }).then(res => {
          if (res.code === Code.SUCCESS) {
            this.dataUser = res.items.list;
            this.total = res.items.total;
          } else {
            this.$message({message: res.message, type: 'error', center: true});
          }
        })
      },
      //手机号显示中间设置***
      setPhone(row, column) {
        var d = row[column.property];
        if (d === undefined) {
          return "";
        } else {
          d = d.substr(0, 3) + '****' + d.substr(7);
          return d;
        }
      },
      // 新增窗口弹出按钮
      addUserInfoFun(formName) {
        this.showTitle = '新增用户';
        this.addUserDialog = true;
        this.isShowUserName = true;
        this.isDisabledUserName = false;
        this.isShowEmail = true;
        this.isShowPhone = true;
        this.isShowUserRoles = true;
        this.isShowPassword = true;
        this.isShowRepeatPassword = true;
        this.isShowInsertButton = true;
        this.isShowResetButton = true;
        this.isShowSaveButton = false;
        this.resetForm(formName);
      },
      // 新增用户的重置按钮
      resetForm(formName) {
        if(this.$refs[formName] !== undefined){
          this.$refs[formName].resetFields();
        }
        this.resetFormFun();
      },
      resetFormFun() {
        this.userForm.userName = '';
        this.userForm.email = '';
        this.userForm.phone = '';
        this.userForm.userRole = '';
        this.userForm.userStatus = '';
        this.userForm.password = '';
        this.userForm.repeatPassword = '';
      },
      closeEditDialogFun() {
        this.addUserDialog = false;
        this.resetFormFun();
      },
      closeInsertUserDialogFun(formName) {
        this.resetForm(formName);
        this.addUserDialog = false;
        this.isShowUserName = false;
        this.isDisabledUserName = false;
        this.isShowEmail = false;
        this.isShowPhone = false;
        this.isShowUserRoles = false;
        this.isShowPassword = false;
        this.isShowRepeatPassword = false;
        this.isShowInsertButton = false;
        this.isShowInsertButton = false;
        this.isShowSaveButton = false;
        this.showTitle = '';
        this.resetFormFun();
      },
      // 新增用户的保存按钮
      insertUserInfoFun(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            User.save({
              userName: this.userForm.userName,
              email: this.userForm.email,
              phone: this.userForm.phone,
              userRole: this.userForm.userRole,
              createUser: this.managerList.userName,
              password: this.getmd5(this.userForm.password),
            }).then(res => {
              if (res.code === Code.SUCCESS) {
                this.addUserDialog = false;
                this.$message({message: Tips.INSERT_SUCCESS, type: 'success'});
                this.getUserInfoFun();
                this.resetForm(formName);
              } else {
                this.$message({message: res.message, type: 'error', center: true});
              }
              this.getManagerInfoFun();
            })
          } else {
            return false;
          }
        });
      },
      // 状态的取消按钮
      closeDialogFun() {
        this.changeUserStatusDialog = false;
        this.getManagerInfoFun();
      },
      // 修改状态按钮
      changeSwitchFun(index, row) {
        this.userForm = Object.assign({}, row);
        if (this.managerList.userRole === `0`) {
          this.changeUserStatusDialog = true;
          if (row.userStatus === "1") {
            this.showMsg = '禁用后将无法正常访问，是否确认禁用？';
          } else {
            this.showMsg = '是否确认启用？';
          }
        } else {
          this.$message({message: "当前用户权限不支持修改其他用户状态！", type: 'error', center: true});
        }
      },
      // 修改状态 保存按钮
      updateStatusFun() {
        User.updateStatus({
          userId: this.userForm.userId,
          userName: this.userForm.userName,
          userRole: this.userForm.userRole,
          userStatus: this.userForm.userStatus,
          updateUser: this.managerList.userName,
        }).then(res => {
          this.changeUserStatusDialog = false;
          if (res.code === Code.SUCCESS) {
            this.$message({message: Tips.UPDATE_SUCCESS, type: 'success', center: true});
            this.searchTable(page);
          } else {
            this.$message({message: res.message, type: 'error', center: true});
          }
          this.getManagerInfoFun();
        })
      },
      // 修改按钮
      editUserInfoFun(index, row) {
        this.userForm = Object.assign({}, row);
        if (this.managerList.userRole === `0`) {
          if ((this.userForm.userRole === `0` && this.userForm.userName === this.managerList.userName) || this.userForm.userRole === `1`) {
            this.showEditFun();
          } else {
            this.$message({message: "当前用户权限不支持修改其他超级管理员信息！", type: 'error'});
          }
        } else if (this.managerList.userRole === `1` && this.managerList.userName === this.userForm.userName) {
          this.showEditFun();
        } else {
          this.$message({message: "当前用户权限不支持修改他人信息！", type: 'error'});
        }
      },
      showEditFun() {
        this.showTitle = '修改用户';
        this.isShowUserName = true;
        this.isDisabledUserName = true;
        this.isShowEmail = true;
        this.isShowPhone = true;
        this.addUserDialog = true;
        this.isShowSaveButton = true;
        this.isShowPassword = false;
        this.isShowRepeatPassword = false;
        this.isShowInsertButton = false;
        this.isShowResetButton = false;
        this.isShowUserRoles = this.managerList.userRole === "0";
        this.userForm.olderEmail = this.userForm.email;
        this.userForm.olderPhoneNumber = this.userForm.phone;
        this.userForm.orderUserRoles = this.userForm.userRole;
        this.userForm.repeatPassword = this.userForm.password;
      },
      // 修改用户信息 保存按钮
      editUserFun(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            //Email、手机号、状态都相同
            if (this.userForm.olderEmail === this.userForm.email && this.userForm.olderPhoneNumber === this.userForm.phone && this.userForm.orderUserRoles === this.userForm.userRole) {
              this.$message({message: "修改成功", type: 'success'});
              this.addUserDialog = false;
            } else if(this.userForm.olderEmail === this.userForm.email && this.userForm.olderPhoneNumber !== this.userForm.phone ) {
              User.update({
                userId: this.userForm.userId,
                userName: this.userForm.userName,
                phone: this.userForm.phone,
                userRole: this.userForm.userRole,
                updateUser: this.managerList.userName
              }).then(res => {
                if (res.code === Code.SUCCESS) {
                  this.addUserDialog = false;
                  this.$message({message: Tips.UPDATE_SUCCESS, type: 'success'});
                  if (this.managerList.userRole === `0`) {
                    this.searchTable();
                  } else {
                    this.getManagerInfoFun();
                  }
                } else {
                  this.$message({message: res.message, type: 'error', center: true});
                }
              })
            }else if(this.userForm.olderEmail !== this.userForm.email && this.userForm.olderPhoneNumber === this.userForm.phone ) {
              User.update({
                userId: this.userForm.userId,
                userName: this.userForm.userName,
                email: this.userForm.email,
                userRole: this.userForm.userRole,
                updateUser: this.managerList.userName
              }).then(res => {
                if (res.code === Code.SUCCESS) {
                  this.addUserDialog = false;
                  this.$message({message: Tips.UPDATE_SUCCESS, type: 'success'});
                  if (this.managerList.userRole === `0`) {
                    this.searchTable();
                  } else {
                    this.getManagerInfoFun();
                  }
                } else {
                  this.$message({message: res.message, type: 'error', center: true});
                }
              })
            }
            else {
              User.update({
                userId: this.userForm.userId,
                userName: this.userForm.userName,
                email: this.userForm.email,
                phone: this.userForm.phone,
                userRole: this.userForm.userRole,
                updateUser: this.managerList.userName
              }).then(res => {
                if (res.code === Code.SUCCESS) {
                  this.addUserDialog = false;
                  this.$message({message: Tips.UPDATE_SUCCESS, type: 'success'});
                  if (this.managerList.userRole === `0`) {
                    this.searchTable(page);
                  } else {
                    this.getManagerInfoFun();
                  }
                } else {
                  this.$message({message: res.message, type: 'error', center: true});
                }
              })
            }
            //this.resetForm(formName);
          } else {
            return false;
          }
        })
      },
      // 重置密码按钮
      formatPasswordsFun(index, row) {
        this.userForm = Object.assign({}, row);
        if (this.managerList.userRole === `0`) {
            this.resetPasswordDialog = true;
            this.userForm.userId = row.userId;
            this.userForm.userName = row.userName;
        } else if (this.userForm.userName === this.managerList.userName) {
          this.resetPasswordDialog = true;
          this.userForm.userId = row.userId;
          this.userForm.userName = row.userName;
        }
        else {
          this.$message({message: "当前用户权限不支持重置他人密码！", type: 'error', center: true});
        }
      },
      // 重置密码 保存按钮
      resetPasswordFun() {
        User.resetPassword({
          userId: this.userForm.userId,
          userName: this.userForm.userName,
          updateUser: this.managerList.userName,
        }).then(res => {
          this.resetPasswordDialog = false;
          if (res.code === Code.SUCCESS) {
            this.$message({message: "重置密码成功", type: 'success', center: true});
            this.searchTable(page);
          } else {
            this.$message({message: res.message, type: 'error', center: true});
          }
        })
      },
      //抓取返回数据的信息
      getUserInfoFun() {
        User.queryList({
          param: {
            pageSize: this.pageSize,
            currentPage: this.page
          }
        }).then(res => {
          if (res.code === Code.SUCCESS
          ) {
            this.dataUser = res.items.list;
            this.total = res.items.total;
          }
          else {
            this.$message({message: res.message, type: 'error', center: true});
          }
        })
      },
      handleCurrentChange(page) {
        this.page = page;
        this.searchTable();
      },
      handleSizeChange(pageSize) {
        this.pageSize = pageSize;
        this.searchTable();
      },
      getmd5(str){
        var a;
        var md5 = crypto.createHash("md5");
        //update("中文", "utf8")
        md5.update(str);
        var a = md5.digest('hex');
        return a;
      }
    },
    directives: {
      autoHeight: {
        update (el, binding, vnode) {
          const vm = vnode.context, $el = vm.$el, $srcoll = '.el-table__body-wrapper', $modules = '.role-search-area';
          let height = $el.clientHeight - $($modules).height() - 110;
          $(el).find($srcoll).css({maxHeight: height});
        }
      }
    }
  }
</script>

