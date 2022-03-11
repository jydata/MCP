<template>
  <section>
    <!-- header -->
    <div class="header">
      <div class="top clearfix">
        <div class="left">
          <i class="icon-p-service"></i>
          <span>Data Connections Management</span>
        </div>
        <div class="right" >
          <el-button @click="addDBLinkFun" :loading="loadingAdd">Add</el-button>
        </div>
      </div>
    </div>

    <!-- 源/终端 -->
    <el-tabs type="border-card" v-model="activeTab" @tab-click="getDBLinksFun">

      <!-- 源  端 -->
      <el-tab-pane label="Source" name="source">
        <div align="right" style="margin-bottom: 8px">
          <el-button @click="syncMetadataFun" :loading="loadingLoad">Data Synchronization</el-button>
        </div>
        <!-- conn list -->
        <div class="table-master">
          <el-table class="table-overflow-x-hidden" :data="dataSList" border v-autoHeight @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="40"></el-table-column>

            <!--<el-table-column prop="linkName" label="连接名" align="center" width="90" :sortable=true></el-table-column>-->
            <!--<el-table-column prop="ip" label="服务器IP" align="center" width="120" :sortable=true></el-table-column>-->
            <!--<el-table-column prop="port" label="端口号" align="center" width="80" :sortable=true></el-table-column>-->
            <!--<el-table-column prop="dbName" label="数据库名称" align="center" width="120" :sortable=true></el-table-column>-->
            <!--<el-table-column prop="username" label="用户名" align="center" width="100"></el-table-column>-->
            <!--<el-table-column prop="url" label="服务器地址" align="center" show-overflow-tooltip></el-table-column>-->
            <!--<el-table-column prop="createTime" label="修改时间" align="center" width="160"></el-table-column>-->
            <!--<el-table-column label="是否已执行" align="center" width="100">-->

            <el-table-column prop="linkName" label="LinkName" align="center" width="90" :sortable=true></el-table-column>
            <el-table-column prop="ip" label="Server IP" align="center" :sortable=true></el-table-column>
            <el-table-column prop="port" label="Port" align="center" width="80" :sortable=true></el-table-column>
            <el-table-column prop="dbName" label="DBName" align="center" :sortable=true></el-table-column>
            <el-table-column prop="username" label="UserName" align="center" width="100"></el-table-column>
            <el-table-column prop="url" label="Server Address" align="center" show-overflow-tooltip></el-table-column>
            <el-table-column prop="createTime" label="Create Time" align="center"></el-table-column>
            <el-table-column label="Executed" align="center" width="100">

              <template slot-scope="scope">
                <span>{{scope.row.executeFlag === 0 ? 'No' : 'Yes'}}</span>
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
              :total="totalS">
          </el-pagination>
        </div>
      </el-tab-pane>

      <!-- 终  端 -->
      <el-tab-pane label="Target" name="target">
        <!-- conn list -->
        <div class="table-master">
          <el-table class="table-overflow-x-hidden" :data="dataTList" border v-autoHeight>

            <!--<el-table-column prop="linkName" label="连接名" align="center" width="120" :sortable=true></el-table-column>-->
            <!--<el-table-column prop="ip" label="服务器IP" align="center" width="320" :sortable=true></el-table-column>-->
            <!--<el-table-column prop="port" label="端口号" align="center" width="80" :sortable=true></el-table-column>-->
            <!--<el-table-column prop="dbName" label="数据库名称" align="center" width="120" :sortable=true></el-table-column>-->
            <!--<el-table-column prop="url" label="服务器地址" align="center" show-overflow-tooltip></el-table-column>-->
            <!--<el-table-column prop="createTime" label="修改时间" align="center" width="160"></el-table-column>-->

            <el-table-column prop="linkName" label="LinkName" align="center" width="120" :sortable=true></el-table-column>
            <el-table-column prop="ip" label="Server IP" align="center" width="320" :sortable=true></el-table-column>
            <el-table-column prop="port" label="Port" align="center" width="80" :sortable=true></el-table-column>
            <el-table-column prop="dbName" label="DBName" align="center" width="120" :sortable=true></el-table-column>
            <el-table-column prop="url" label="Server Address" align="center" show-overflow-tooltip></el-table-column>
            <el-table-column prop="createTime" label="Create Time" align="center" width="160"></el-table-column>

          </el-table>
          <el-pagination
              align="center"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              :page-sizes="pageSizes"
              :page-size="pageSize"
              :current-page="page"
              layout="total, sizes, prev, pager, next, jumper"
              :total="totalT">
          </el-pagination>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- add -->
    <el-dialog class="add-dialog" title="Add Database Connection Configuration" :close-on-click-modal="false" :visible.sync="addLinkDialog" :show-close="false" width="33%" height="80%">
      <el-form label-width="160px" :model="connForm" :rules="rules" ref="connForm" class="item-error">
        <el-form-item label="Data Source Type：" prop="dbType">
          <el-select @change="choiceDBFun('connForm')" v-model="connForm.dbType" filterable clearable placeholder="Please select">
            <el-option
              v-for="item in dbTypeList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Data Source：" prop="dbChoice">
          <!--@change="showAddLevelFun"-->
          <el-select @change="flagDBFun('connForm')" v-model="connForm.dbChoice" filterable clearable placeholder="Please select">
            <el-option
              v-for="item in dbChoiceList"
              :key="item.datasourceName"
              :label="item.datasourceName"
              :value="item.datasourceName">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Data Source Flag：" v-show="dsFlag" prop="dbFlag">
          <el-select v-model="connForm.dbFlag" filterable clearable placeholder="Please select">
            <el-option
              v-for="item in dbFlagList"
              :key="item.datasourceId"
              :label="item.datasourceFlag"
              :value="item.datasourceFlag"
            >
            </el-option>
          </el-select>
        </el-form-item>

        <!--(connForm.dbChoice == 'Phoenix' && connForm.dbFlag == 'PRD') || (connForm.dbChoice == 'Phoenix' && connForm.dbFlag == 'DEV') || connForm.dbChoice == 'MySQL'-->
        <el-form-item label="LinkName：" prop="linkName" style="position: relative; padding-top: 0px"
                      v-show="isShowLinkName">
          <el-input v-model="connForm.linkName" style="width: 75%" clearable></el-input>
        </el-form-item>
        <el-form-item label="IP/Host：" prop="ip" v-show="isShowIpInfo">
          <el-input v-model="connForm.ip" style="width: 75%" clearable></el-input>
        </el-form-item>
        <el-form-item label="Port：" prop="port" v-show="isShowPortInfo">
          <el-input v-model="connForm.port" style="width: 75%" clearable></el-input>
        </el-form-item>
        <el-form-item label="DBName：" prop="dbName" v-show="isShowDBName">
          <el-input v-model="connForm.dbName" style="width: 75%" clearable></el-input>
        </el-form-item>

        <!--connForm.dbChoice == 'MySQL'-->
        <el-form-item label="UserName：" prop="username" v-show="isShowUserName">
          <el-input v-model="connForm.username" style="width: 75%" clearable></el-input>
        </el-form-item>
        <el-form-item label="PassWord：" prop="password" v-show="isShowPassword">
          <el-input type="password" v-model="connForm.password" style="width: 75%" clearable></el-input>
        </el-form-item>

        <el-form-item>
          <el-button @click="testInsertDBLinkFun('connForm')" :loading="loadingTest">Connect</el-button>
          <el-button @click="closeDialog('connForm')">Close</el-button>
          <el-button @click="insertDBLinkFun('connForm')" :loading="loadingSave">Confirm</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

    <!-- sync -->
    <el-dialog class="sync-dialog" title="Data structure synchronization information" :close-on-click-modal="false" :visible.sync="addSyncDialog"  :show-close="false" width="33%" height="60%">
      <div class="sync-input">
        <el-input placeholder="Input keywords for filtering"  v-model="filterText"/>
      </div>
      <div class="sync-table">
        <el-tree
          class="filter-tree"
          :props="defaultProps"
          show-checkbox
          lazy
          node-key="label"
          :load="loadNode"
          @check-change="handleCheckChange"
          :filter-node-method="filterNode"
          ref="meta_tree">
        </el-tree>
      </div>
      <div class="sync-footer" align="right">
        <el-button @click="closeSync" >Close</el-button>
        <el-button @click="syncDBFun" :loading="loadingSync">Sync</el-button>
      </div>
    </el-dialog>

  </section>
</template>
<script>
  import {page, pageSize, pageSizes} from '@config/config';
  import * as Code from '@config/code';
  import * as Tips from '@config/tips';
  import * as Metadata from '@api/Metadata';
  import JsEncrypt from 'jsencrypt'

  export default {
    data () {
      // 检查数据源信息是否为空
      var checkTypeInfo = (rule, value, callback) => {
        if (value === '' || value.trim().length <= 0) {
          return callback(new Error('请选择数据源类型信息!'));
        } else {
          callback();
        }
      };

      // 检查数据源是否为空
      var checkChoiceInfo = (rule, value, callback) => {
        if (value === '' || value.trim().length <= 0) {
          return callback(new Error('请选择数据源信息!'));
        } else {
          callback();
        }
      };

      // 检查数据源标识是否为空
      var checkFlagInfo = (rule, value, callback) => {
        if (value === '' || value.trim().length <= 0) {
          return callback(new Error('请选择数据源标识信息!'));
        } else {
          callback();
        }
      };

      // 检查连接名是否为空
      var checkLinkName = (rule, value, callback) => {
        if (value === '' || value.trim().length <= 0) {
          return callback(new Error('连接名不能为空'));
        } else {
          callback();
        }
      };

      // var reg = /(?=(\b|\D))(((\d{1,2})|(1\d{1,2})|(2[0-4]\d)|(25[0-5]))\.){3}((\d{1,2})|(1\d{1,2})|(2[0-4]\d)|(25[0-5]))(?=(\b|\D))/;
      var reg = /^(?:(?:^|,)(?:[0-9]|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])(?:\.(?:[0-9]|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])){3})+$/;
      // 检查ip是否正确
      var checkIP = (rule, value, callback) => {
        if (value === '' || value.trim().length <= 0) {
          return callback(new Error('IP地址不能为空'));
        } else if (!reg.test(value)) {
          callback(new Error('IP地址输入格式不正确'));
        } else {
          callback();
        }
      };

      // 检查端口号是否位数字
      var checkPort = (rule, value, callback) => {
        if (value === '' || value.trim().length <= 0) {
          return callback(new Error('端口号不能为空'));
        } else if (!Number(value)) {
          callback(new Error('端口号必须为数字'));
        } else {
          callback();
        }
      };

      var checkUserName = (rule, value, callback) => {
        if (!this.dsFlag) {
          if (value === '') {
            callback(new Error('请输入连接用户名!'));
          } else {
            callback();
          }
        } else {
          callback();
        }
      };

      var checkPassword = (rule, value, callback) => {
        if (!this.dsFlag) {
          if (value === '') {
            callback(new Error('请输入连接密码!'));
          } else {
            callback();
          }
        } else {
          callback();
        }
      };
      return {
        page: page,
        pageSize: pageSize,
        pageSizes: pageSizes,
        totalS: 0,
        totalT: 0,
        loadingAdd: false,
        loadingLoad: false,
        loadingTest: false,
        loadingSave: false,
        loadingSync: false,
        addLinkDialog: false,
        addSyncDialog: false,
        linkNameShow: false,
        dsFlag: false,
        isShowLinkName: false,
        isShowIpInfo: false,
        isShowPortInfo: false,
        isShowDBName: false,
        isShowUserName: false,
        isShowPassword: false,
        dataSList: [],
        dataTList: [],
        dbTypeList: [{
          value: '0',
          label: 'Source'
        }, {
          value: '1',
          label: 'Target'
        }],
        dbChoiceList: [],
        dbFlagList: [],
        multipleSelection: [],
        multipleTreeSelection: [],
        connForm: {
          datasourceId: '',
          dbType: '',
          dbChoice: '',
          dbFlag: '',
          linkName: '',
          ip: '',
          port: '',
          dbName: '',
          url: '',
          username: '',
          password: ''
        },
        rules: {
          dbType: [
            {required: true, validator: checkTypeInfo, trigger: 'blur'}
          ],
          dbChoice: [
            {required: true, validator: checkChoiceInfo, trigger: 'blur'}
          ],
          /*dbFlag: [
            {validator: checkFlagInfo, trigger: 'blur'}
          ],*/
          linkName: [
            {required: true, validator: checkLinkName, trigger: 'blur'},
            {min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur'}
          ],
          ip: [
            {required: true, validator: checkIP, trigger: 'blur'}
          ],
          port: [
            {required: true, validator: checkPort, trigger: 'blur'}
          ],
          dbName: [
            {required: true, message: '请输入数据库名称', trigger: 'blur'},
            {min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur'}
          ],
          username: [
            {validator: checkUserName, trigger: 'blur'}
          ],
          password: [
            {validator: checkPassword, trigger: 'blur'}
          ]
        },
        filterText: '',
        defaultProps: {
          label: 'label',
          isLeaf: 'isleaf'
        },
        activeTab: 'source'
      }
    },
    created () {
      this.getDBLinksFun(null, null);
    },
    methods: {
      loadOptionsFun(flag){
        if (flag == '2' && this.connForm.dbType == '1') {
          this.dsFlag = true;

          this.isShowLinkName = true;
          this.isShowIpInfo = true;
          this.isShowPortInfo = true;
          this.isShowDBName = true;
          this.isShowUserName = false;
          this.isShowPassword = false;
        } else if (flag == '2' && this.connForm.dbType == '0') {
          this.isShowLinkName = true;
          this.isShowIpInfo = true;
          this.isShowPortInfo = true;
          this.isShowDBName = true;
          this.isShowUserName = true;
          this.isShowPassword = true;
        }

        Metadata.loadOptions({
          datasourceName: this.connForm.dbChoice,
          datasourceType: this.connForm.dbType,
          flag: flag
        }).then(res => {
          if (res.code === Code.SUCCESS) {
            this.connForm.datasourceId = res.items[0].datasourceId;
            if (flag != '2') {
              this.dbChoiceList = res.items;

              this.isShowLinkName = false;
              this.isShowIpInfo = false;
              this.isShowPortInfo = false;
              this.isShowDBName = false;
              this.isShowUserName = false;
              this.isShowPassword = false;
            } else {
              this.dbFlagList = res.items;
            }
          } else {
            this.$message({message: res.message, type: 'error', center: true});
          }
        })
      },
      handleSelectionChange(val) {
        this.multipleSelection = val;
      },
      handleCheckChange() {
        this.multipleTreeSelection = this.$refs.meta_tree.getCheckedNodes();
      },
      encryptFun(pwd){
        let crypt = new JSEncrypt();
        crypt.setKey(`MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjh/VN6nKKDlk4cb43aUsEpsNRDqgfFmb6Hs1hPhN4yysh3tvpdlKwPRTlSSeqa7Oy0AG6rZnx8bNXSdFQPWImVYwbg/xbYhj2v8JGDuaKBviGxxPAXLHfpR6h6AQT33rAMkkVn9u7jj4/OtNWYWCWa0I4/Tk8sDlvwhtjvmeEzyF14KzjEdBnBKL8SNvLe26dipckasOjQMlt1Eeko7PoMVa07vit0bVqkhheXI5ppGkx2WfnrZqyMukDB515SEmlyQQYqkmXdmSAqPEYot+rANNKaUebr3TR7K5SKidZ27YlCm/gVZnU8amaycDNU0HGvkrFmk75Jh54SYh3kMwqQIDAQAB`)

        return crypt.encrypt(pwd)
      },
      getDBLinksFun(tab, event){
        if (null == tab || tab == '' || (tab != null && tab.name == 'source')) {
          Metadata.getDBLinks({
            param: {
              pageSize: this.pageSize,
              currentPage: this.page,
            },
            info: {dsType: 'source'}
          }).then(res => {
            if (res.code === Code.SUCCESS) {
              this.dataSList = res.items.list;
              this.totalS = res.items.total;
            } else {
              this.$message({message: res.message, type: 'error'});
            }
          })
        } else if (tab != null && tab.name == 'target') {
          Metadata.getDBLinks({
            param: {
              pageSize: this.pageSize,
              currentPage: this.page
            },
            info: {dsType: 'target'}
          }).then(res => {
            if (res.code === Code.SUCCESS) {
              this.dataTList = res.items.list;
              this.totalT = res.items.total;
            } else {
              this.$message({message: res.message, type: 'error'});
            }
          })
        }
      },
      addDBLinkFun(){
        this.addLinkDialog = true;

        this.connForm.dbType = '';
        this.connForm.dbChoice = '';
        this.connForm.dbFlag = '';

        this.dsFlag = false;

        this.isShowLinkName = false;
        this.isShowIpInfo = false;
        this.isShowPortInfo = false;
        this.isShowDBName = false;
        this.isShowUserName = false;
        this.isShowPassword = false;
      },
      choiceDBFun(formName){
        this.$refs[formName].clearValidate();
        this.dsFlag = false;
        this.connForm.dbChoice = '';
        this.connForm.dbFlag = '';
        this.connForm.datasourceId = '';

        if (this.connForm.dbType == '0') {
          // 加载源端下拉列表
          this.loadOptionsFun('0');
        } else {
          // 加载终端下拉列表
          this.loadOptionsFun('1');
        }
      },
      flagDBFun(formName){
        this.$refs[formName].clearValidate();
        this.dsFlag = false;
        this.connForm.dbFlag = '';
        this.connForm.datasourceId = '';
        this.connForm.linkName = '';
        this.connForm.ip = '';
        this.connForm.port = '';
        this.connForm.dbName = '';
        this.connForm.url = '';
        this.connForm.username = '';
        this.connForm.password = '';

        // 加载终端二级下拉列表
        this.loadOptionsFun('2');
      },
      testInsertDBLinkFun(formName){
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.loadingTest = true;
            let encPwd = this.encryptFun(this.connForm.password)

            Metadata.testDBLink({
              datasourceId: this.connForm.datasourceId,
              datasourceType: this.connForm.dbType,
              datasourceChoice: this.connForm.dbChoice,
              linkName: this.connForm.linkName,
              ip: this.connForm.ip,
              port: this.connForm.port,
              dbName: this.connForm.dbName,
              username: this.connForm.username,
              password: encPwd
            }).then(res => {
              if (res.code === Code.SUCCESS) {
                this.loadingTest = false;
                this.$message({message: Tips.TEST_SUCCESS, type: 'success'});
              } else {
                this.$message({message: res.message, type: 'error'});
              }
            }).catch(() => {
              this.loadingTest = false;
            })
          } else {
            console.log('submit failed!!');
            return false;
          }
        });
      },
      insertDBLinkFun(formName){
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.loadingSave = true;
            let encPwd = this.encryptFun(this.connForm.password)

            Metadata.insertDBLink({
              datasourceId: this.connForm.datasourceId,
              datasourceType: this.connForm.dbType,
              datasourceChoice: this.connForm.dbChoice,
              linkName: this.connForm.linkName,
              ip: this.connForm.ip,
              port: this.connForm.port,
              dbName: this.connForm.dbName,
              username: this.connForm.username,
              password: encPwd
            }).then(res => {
              if (res.code === Code.SUCCESS) {
                this.loadingSave = false;
                this.addLinkDialog = false;
                this.$message({message: Tips.INSERT_SUCCESS, type: 'success'});
                this.getDBLinksFun();
              } else {
                this.$message({message: res.message, type: 'error'});
              }
            }).catch(() => {
              this.loadingSave = false;
            })
          } else {
            console.log('submit failed!!');
            return false;
          }
        });
      },
      syncMetadataFun(){
        if (this.multipleSelection.length < 1) {
          alert("Please check the database you want to synchronize.")
        } else if (this.multipleSelection.length < 1) {
          alert("每次只能同步一个库信息.")
        } else {
          this.addSyncDialog = true;
          this.filterText = '';
        }
      },
      syncDBFun(){
        this.loadingSync = true;

        let param = {
          linkList: this.multipleSelection,
          treeList: this.multipleTreeSelection
        }
        Metadata.syncMetadata(param).then(res => {
          if (res.code === Code.SUCCESS) {
            this.$message({message: Tips.SYNC_SUCCESS, type: 'success', center: true});
            this.loadingSync = false;
            this.addSyncDialog = false;
            this.getDBLinksFun()
          } else {
            this.$message({message: Tips.SYNC_FAILED, type: 'error', center: true});
            this.loadingSync = false;
          }
        })
      },
      closeSync(){
        this.$refs.meta_tree.setCheckedKeys([]);
        this.addSyncDialog = false
      },
      closeDialog(formName){
        this.connForm.dbType = '';
        this.connForm.dbChoice = '';
        this.connForm.dbFlag = '';
        this.dsFlag = false;

        this.addLinkDialog = false;

        this.isShowLinkName = false;
        this.isShowIpInfo = false;
        this.isShowPortInfo = false;
        this.isShowDBName = false;
        this.isShowUserName = false;
        this.isShowPassword = false;

        if(this.$refs[formName] !== undefined){
          this.$refs[formName].resetFields();
        }
        // this.$refs[formName].clearValidate();
      },
      handleSizeChange(pageSize){
        this.pageSize = pageSize;
        this.getDBLinksFun();
      },
      handleCurrentChange(page){
        this.page = page;
        this.getDBLinksFun();
      },
      filterNode(value, data) {
        if (!value) return true;
        return data.label.indexOf(value) !== -1;
      },
      // 共三个参数，依次为：传递给 data 属性的数组中该节点所对应的对象、节点对应的Node、节点组件本身
      loadNode (node, resolve) {
        if (node.level === 0) {
          // 加载数据库级别node
          let flag = "0"
          let param = this.multipleSelection

          Metadata.loadTree(flag, param).then(res => {
            const data = res.items;
            return resolve(data);
          })
        }
      },
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

<style lang="less" scoped>
  .el-tabs--border-card {
    .el-tabs__header .el-tabs__item.is-active {color: #7eb4e2}
    .el-tabs__header .el-tabs__item:not(.is-disabled):hover{color: #7eb4e2}
  }

    .sync-dialog{
      .sync-input{margin-left: 20px;width: 385px}
      .sync-table{margin-top: 15px; overflow: auto; height: 350px;}
      .sync-footer{margin-top: 15px}
    }
</style>
