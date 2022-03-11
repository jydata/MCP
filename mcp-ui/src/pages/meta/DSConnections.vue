<template>
  <section>
    <!--头部-->
    <div class="header">
      <div class="title">
        <el-breadcrumb separator-class="el-icon-arrow-right">
          <el-breadcrumb-item>DS Connections</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
    </div>

    <!--内容-->
    <div class="container gray mt0">

      <div class="source mb50">
        <div>
          <ul class="main-top">
            <li class="shadow tab-title" style="background-color:#2c6daf;color:#fff;">Sources</li>
          </ul>
        </div>

        <div class="main border">
          <div class="table-list">
            <div class="shadow add-area mr10" style="width: 190px;height: 100px;cursor: pointer" @click="showSourcePanel">
                  <i class="icon-plus-circle mr20"></i>
                  <span class="f16">Add Source</span>
            </div>
            <ul v-show="loading.source">
              <li class="shadow mr10" style="width: 190px;height: 100px;">
                  <i class="el-icon-loading fsize18"></i>
                  <p class="ml5">loading...</p>
              </li>
            </ul>
            <ul  v-for="(dataS,index) in dataSList" v-show="index < showSNum" :key="index">
              <li class="shadow mr10" style="width: 190px;height: 100px;" @click="showSData(dataS)">
                  <i :class="{'icon-thumb-tack':dataS.focus}"></i>
                  <img class="mr20 ml10" src="../../assets/img/MySQL.png">
                  <span class="mr20 dee" style="word-break: break-word;font-weight: bold">{{dataS.linkName}}</span>
                  <i :class="['icon-eye','fr','icon',{'icon-eye-green':dataS.executeFlag==1}]"  style="height: 80px;"></i>
              </li>
            </ul>
          </div>
          <div class="arrow center mt10" v-show="isSArrow">
            <i :class="showSIcon" @click="showSInfo()"></i>
          </div>
          <div class="add-panel shadow pd20" v-show="addSourcePanel">
            <el-form label-width="130px" :rules="rules" :model="sourceData" ref="sourceData">
              <el-form-item label="Source Type：" prop="sourceType">
                <el-select filterable placeholder="Please select"
                           v-model="sourceData.sourceType"
                           @focus="getSLoad"
                           @change="changeSourceValue"
                           size="mini"
                           :disabled="isSloading">
                  <el-option v-for="item in loadSList" :label="item.dictName" :value="item.dictId" :key="item.dictId"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="IP/Host：" prop="ip">
                <el-input v-model="sourceData.ip" clearable :disabled="isSloading"></el-input>
              </el-form-item>
              <el-form-item label="Database：" prop="database">
                <el-input v-model="sourceData.database" maxlength=20 clearable :disabled="isSloading"></el-input>
              </el-form-item>
              <el-form-item class="button">
                  <i class="icon-stethoscope mr20" title="Test Connection" v-show="isIconSloading" @click="testSourceLink('sourceData')"></i>
                  <i class="icon-floppy-o mr20" title="Save" v-show="isIconSloading" @click="saveSourceFun('sourceData')"></i>
                  <i class="icon-refresh" title="Go to DS Synchronize Source Metadata" v-show="isIconloading" @click="goDataSync"></i>
              </el-form-item>
              <el-form-item label="Source Name：" prop="sourceName">
                <el-input v-model="sourceData.sourceName" maxlength=20 clearable :disabled="isSloading"></el-input>
              </el-form-item>
              <el-form-item label="Port：" prop="port">
                <el-input v-model="sourceData.port" clearable :disabled="isSloading"></el-input>
              </el-form-item>
              <el-form-item label="User Name：" prop="username">
                <el-input v-model="sourceData.username" maxlength=20 class="yellow" clearable :disabled="isSloading"></el-input>
              </el-form-item>
              <el-form-item class="yellow" label="Password：" prop="password">
                <el-input v-model="sourceData.password" type="password" clearable :disabled="isSloading"></el-input>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </div>

      <div class="target">
        <div class="main-top ">
              <li class="shadow tab-title" style="background-color:#2c6daf;color:#fff;">Targets</li>
              <i class="icon-link" title="Go to DS Routing"  @click="goDSRouting"></i>
        </div>
        <div class="main border">
          <div class="table-list">
            <div class="shadow add-area mr10" style="width: 190px;height: 100px;cursor: pointer" @click="showTargetPanel()">
              <i class="icon-plus-circle mr20"></i>
              <span class="f16">Add Target</span>
            </div>
            <ul v-show="loading.target">
              <li class="shadow mr10" style="width: 190px;height: 100px;">
                  <i class="el-icon-loading fsize18"></i>
                  <p class="ml5">loading...</p>
              </li>
            </ul>
            <ul v-for="(dataT,index) in dataTList" v-show="index < showTNum" :key="index">
              <li class="shadow mr10" style="width: 190px;height: 100px;" @click="showTData(dataT)">
                <i :class="{'icon-thumb-tack':dataT.focus}"></i>
                <img class="mr20 ml10" src="../../assets/img/phoenix.png">
                <span class="mr20" style="word-break: break-word;font-weight: bold">{{dataT.linkName}}</span>
              </li>

            </ul>
          </div>
          <div class="arrow center mt10" v-show="isTArrow">
            <i :class="showTIcon" @click="showTInfo()"></i>
          </div>
          <div class="add-panel shadow pd20" v-show="addTargetPanel">
            <el-form label-width="130px" :rules="rules" :model="targetData" ref="targetData">
              <el-form-item label="Target Type：" prop="targetType">
                <el-select v-model="targetData.targetType"
                           @focus="getTLoad"
                           placeholder="Please select"
                           :disabled="isTloading"
                           filterable>
                  <el-option v-for="item in loadTList"
                             :label="item.dictName"
                             :key="item.dictName"
                             :value="item.dictName">
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="Target Name：" prop="targetName">
                <el-input v-model="targetData.targetName" maxlength=20 clearable :disabled="isTloading"></el-input>
              </el-form-item>
              <el-form-item label="Database：" prop="database">
                <el-input v-model="targetData.database" maxlength=20 clearable :disabled="isTloading"></el-input>
              </el-form-item>
              <el-form-item class="button">
                <i class="icon-stethoscope mr20" v-show="isIconTloading" title="Test Connection" @click="testTargetLink('targetData')"></i>
                <i class="icon-floppy-o" title="Save" v-show="isIconTloading" @click="saveTargetFun('targetData')"></i>
              </el-form-item>
              <el-form-item label="ENV：" prop="env">
                <el-select v-model="targetData.env"
                           placeholder="Please select"
                           @focus="getEnvLoad"
                           @change="changeEnvValue"
                           :disabled="isTloading"
                           filterable>
                  <el-option v-for="item in envList" :label="item.databaseFlag" :value="item.dictId" :key="item.dictId"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="IP/Host：" prop="ip">
                <el-input  v-model="targetData.ip" clearable :disabled="isTloading"></el-input>
              </el-form-item>
              <el-form-item label="Port：" prop="port">
                <el-input v-model="targetData.port" clearable :disabled="isTloading"></el-input>
              </el-form-item>
              <!--<el-form-item class="yellow" label="User Name：" prop="username">-->
                <!--<el-input v-model="targetData.username" clearable :disabled="true"></el-input>-->
              <!--</el-form-item>-->
              <!--<el-form-item class="yellow" label="Password：" prop="password">-->
                <!--<el-input v-model="targetData.password" type="password" clearable :disabled="true"></el-input>-->
              <!--</el-form-item>-->
            </el-form>
          </div>
        </div>
      </div>
    </div>

    <!--提示框-->
    <el-dialog title="Message" :visible.sync="dialogTip">
      <span>Please select the Source and Target！</span>
    </el-dialog>

  </section>
</template>

<script>
  import * as Meta from '@api/Meta';
  import * as Code from '@config/code';
  import * as Tips from '@config/tips';
  import JsEncrypt from 'jsencrypt'

  export default {
      name: "DSConnections",
      data(){
        let regName = /^[a-zA-Z]+[\w]*$/;
        let checkLinkName = (rule, value, callback) => {
          if (value === '' || value.trim().length <= 0) {
            return callback(new Error('Source Name is required'));
          } else if (!regName.test(value)) {
            callback(new Error('Only numbers,letters and underline are allowed, and letters must be in front'));
          } else {
            callback();
          }
        };

        // 检查ip是否正确
        let reg = /^(?:(?:^|,)(?:[0-9]|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])(?:\.(?:[0-9]|[1-9]\d|1\d{2}|2[0-4]\d|25[0-5])){3})+$/;
        let checkIP = (rule, value, callback) => {
          if (value === '' || value.trim().length <= 0) {
            return callback(new Error('IP is required'));
          } else if (!reg.test(value)) {
            callback(new Error('IP format is incorrect'));
          } else {
            callback();
          }
        };

        // 检查端口号是否位数字
        let checkPort = (rule, value, callback) => {
          if (value === '' || value.trim().length <= 0) {
            return callback(new Error('Port is required'));
          } else if (!Number(value)) {
            return callback(new Error('Port must be number'));
          } else {
            callback();
          }
        };

        let checkUserName = (rule, value, callback) => {
          if (!this.dsFlag) {
            if (value === '') {
              callback(new Error('UserName is required'));
            } else {
              callback();
            }
          } else {
            callback();
          }
        };

        let checkPassword = (rule, value, callback) => {
          if (!this.dsFlag) {
            if (value === '') {
              callback(new Error('Password is required'));
            } else {
              callback();
            }
          } else {
            callback();
          }
        };
        return{
          showSNum: Math.floor((document.documentElement.clientWidth - 217) / 217),
          showTNum: Math.floor((document.documentElement.clientWidth - 217) / 217),
          showSIcon: 'icon-angle-double-down',
          showTIcon: 'icon-angle-double-down',
          isSArrow: '',
          isTArrow: '',
          eyes: 'icon-eye',
          sourceSelected: false,
          targetSelected: false,
          dialogTip: false,
          dataSList: [],
          dataTList: [],
          loadSList: [],
          loadTList: [],
          envList: [],
          dictList: [],
          sourceData:{
            sourceType: '',
            typeName:'',
            ip: '',
            database: '',
            sourceName: '',
            port: '',
            username: '',
            password: '',
          },
          targetData:{
            targetType: '',
            databaseFlag: '',
            ip: '',
            database: '',
            env: '',
            targetName: '',
            port: '',
            username: '',
            password: '',
          },

          addTargetPanel: false,
          addSourcePanel: false,
          isSloading: '',
          isTloading: '',
          isIconSloading: '',
          isIconTloading: '',
          isIconloading: '',
          sourceId: '',
          sourceName: '',
          targetId: '',
          targetName: '',
          loading:{
            source:true,
            target:true
          },

          rules:{
            sourceType: [
              {required: true, message: 'Source Type is required', trigger: 'blur'}
            ],
            targetType: [
              {required: true, message: 'Target Type is required', trigger: 'blur'}
            ],
            env: [
              {required: true, message: 'Env is required', trigger: 'blur'}
            ],
            sourceName: [
              {required: true, validator: checkLinkName, trigger: 'blur'},
            ],
            targetName: [
              {required: true, validator: checkLinkName, trigger: 'blur'},
            ],
            database: [
              {required: true,  message: 'Database is required', trigger: 'blur'},
            ],
            ip: [
              {required: true, validator: checkIP, trigger: 'blur'}
            ],
            port: [
              {required: true, validator: checkPort, trigger: 'blur'}
            ],
            username: [
              {required: true, validator: checkUserName, trigger: 'blur'},
            ],
            password: [
              {required: true, validator: checkPassword, trigger: 'blur'}
            ]
          }


        }
      },
      created () {
        this.getDBLink();
        this.getDict();
      },
      methods:{
        //清除验证信息
        clearSourceVail(){
          this.$refs['sourceData'].clearValidate();
        },
        clearTargetVail(){
          this.$refs['targetData'].clearValidate();
        },
        //获取数据
        getDBLink(){
          Meta.getDSLink({dataSourceType:0}).then(res => {
            if (res.code === Code.SUCCESS) {
              this.dataSList = res.items;
              if(this.dataSList.length <= this.showNum){
                this.isSArrow = false;
              }else{
                this.isSArrow = true;
              }
            } else {
              this.$message({message: res.message, type: 'error'});
            }
            this.loading.source=false;
          }).catch(err=>{
            this.loading.source=false;
          })
          Meta.getDSLink({dataSourceType:1}).then(res => {
            if (res.code === Code.SUCCESS) {
              this.dataTList = res.items;
              if(this.dataTList.length <= this.showNum){
                this.isTArrow = false;
              }else{
                this.isTArrow = true;
              }
            } else {
              this.$message({message: res.message, type: 'error'});
            }
            this.loading.target=false;
          }).catch(err=>{
            this.loading.target=false;
          })
        },
        //获取下拉加载项
        getSLoad(){
          Meta.getLoads({
            databaseType: 0,
            flag : '0'
          }).then(res => {
            if (res.code === Code.SUCCESS) {
              this.loadSList = res.items;
            } else {
              this.$message({message: res.message, type: 'error'});
            }
          }).catch(() => {
          })
        },
        getTLoad(){
          this.targetData.env = '';
          Meta.getLoads({
            databaseType: 1,
            flag : '1'
          }).then(res => {
            if (res.code === Code.SUCCESS) {
              this.loadTList = res.items;
            } else {
              this.$message({message: res.message, type: 'error'});
            }
          }).catch(() => {
          })
        },
        getEnvLoad(){
          Meta.getLoads({
            dictName: this.targetData.targetType,
            databaseType: 1,
            flag : '2'
          }).then(res => {
            if (res.code === Code.SUCCESS) {
              this.envList = res.items;
            } else {
              this.$message({message: res.message, type: 'error'});
            }
          }).catch(() => {
          })
        },
        //获取字典表
        getDict(){
          Meta.getLoads({
            flag : '3'
          }).then(res => {
            if (res.code === Code.SUCCESS) {
              this.dictList = res.items;
            } else {
              this.$message({message: res.message, type: 'error'});
            }
          }).catch(() => {
          })
        },

        //新增保存
        saveSourceFun(formName){
          this.$refs[formName].validate((valid) => {
            if (valid) {
              let encPwd = this.encryptFun(this.sourceData.password)

              Meta.saveDSLink({
                datasourceType: 0,
                datasourceId: this.sourceData.sourceType,
                datasourceChoice: this.sourceData.typeName,
                linkName: this.sourceData.sourceName,
                ip: this.sourceData.ip,
                port: this.sourceData.port,
                dbName: this.sourceData.database,
                username: this.sourceData.username,
                password: encPwd
              }).then(res => {
                if (res.code === Code.SUCCESS) {
                  this.$message({message: Tips.SAVE_SUCCESS, type: 'success'});
                  this.getDBLink();
                  this.addSourcePanel = false;
                } else {
                  this.$message({message: res.message, type: 'error'});
                }
              }).catch(() => {
              })
            }
          });
        },
        saveTargetFun(formName){
          this.$refs[formName].validate((valid) => {
            if (valid) {
              // let encPwd = this.encryptFun(this.targetData.password)

              Meta.saveDSLink({
                datasourceType: 1,
                datasourceId: this.targetData.env,
                databaseFlag: this.targetData.databaseFlag,
                datasourceChoice: this.targetData.targetType,
                linkName: this.targetData.targetName,
                ip: this.targetData.ip,
                port: this.targetData.port,
                dbName: this.targetData.database,
                username: '',
                password: ''
              }).then(res => {
                if (res.code === Code.SUCCESS) {
                  this.$message({message: Tips.INSERT_SUCCESS, type: 'success'});
                  this.getDBLink();
                  this.addTargetPanel = false;
                } else {
                  this.$message({message: res.message, type: 'error'});
                }
              }).catch(() => {
              })
            } else {
              console.log('submit failed!');
              return false;
            }
          });
        },

        //测试连接
        testSourceLink(formName){
          this.$refs[formName].validate((valid) => {
              if (valid) {
                let encPwd = this.encryptFun(this.sourceData.password)

                Meta.testDSLink({
                  datasourceType: 0,
                  datasourceId: this.sourceData.sourceType,
                  datasourceChoice: this.sourceData.typeName,
                  linkName: this.sourceData.sourceName,
                  ip: this.sourceData.ip,
                  port: this.sourceData.port,
                  dbName: this.sourceData.database,
                  username: this.sourceData.username,
                  password: encPwd
                }).then(res => {
                  if (res.code === Code.SUCCESS) {
                    this.$message({message: Tips.CONNECTION_TEST_SUCCESS, type: 'success'});
                  } else {
                    this.$message({message: Tips.CONNECTION_TEST_FAILED, type: 'error'});
                  }
                }).catch(() => {
                })
              }
          });
        },
        testTargetLink(formName){
          this.$refs[formName].validate((valid) => {
            if (valid) {
              // let encPwd = this.encryptFun(this.targetData.password)

              Meta.testDSLink({
                datasourceType: 1,
                datasourceId: this.targetData.env,
                databaseFlag: this.targetData.databaseFlag,
                datasourceChoice: this.targetData.targetType,
                linkName: this.targetData.targetName,
                ip: this.targetData.ip,
                port: this.targetData.port,
                dbName: this.targetData.database,
                username: '',
                password: ''
              }).then(res => {
                if (res.code === Code.SUCCESS) {
                  this.$message({message: Tips.CONNECTION_TEST_SUCCESS, type: 'success'});
                } else {
                  this.$message({message: Tips.CONNECTION_TEST_FAILED, type: 'error'});
                }
              }).catch(() => {
              })
            }
          });
        },

        // Add面板
        showSourcePanel(){
          this.dataSList.forEach(val=>{this.$set(val,'focus',false)});
          this.sourceSelected = false;
          if(this.addSourcePanel){
            this.addSourcePanel = false;
          }else{
            this.addSourcePanel = true;
          }
          this.isIconloading = false;
          this.isIconSloading = true;
          this.isSloading = false;
          this.sourceData.sourceType = '';
          this.sourceData.ip = '';
          this.sourceData.database = '';
          this.sourceData.sourceName = '';
          this.sourceData.port = '';
          this.sourceData.username = '';
          this.sourceData.password = '';
          this.clearSourceVail();
        },
        showTargetPanel(){
          this.dataTList.forEach(val=>{this.$set(val,'focus',false)});
          this.targetSelected = false;
          if(this.addTargetPanel){
            this.addTargetPanel = false;
          }else{
            this.addTargetPanel = true;
          }
          this.isIconTloading = true;
          this.isTloading = false;
          this.targetData.targetType = '';
          this.targetData.ip = '';
          this.targetData.database = '';
          this.targetData.targetName = '';
          this.targetData.port = '';
          this.targetData.username = '';
          this.targetData.password = '';
          this.targetData.env = '';
          this.clearSourceVail();
        },

        //显示更多详细信息
        showSInfo(){
          if(this.showSIcon === 'icon-angle-double-down'){
            this.showSNum = this.dataSList.length;
            this.showSIcon = 'icon-angle-double-up'
          }
          else{
            this.showSNum = this.showNum;
            this.showSIcon = 'icon-angle-double-down'
          }
        },
        showTInfo(){
          if(this.showTIcon === 'icon-angle-double-down'){
            this.showTNum = this.dataSList.length;
            this.showTIcon = 'icon-angle-double-up'
          }
          else{
            this.showTNum = this.showNum;
            this.showTIcon = 'icon-angle-double-down'
          }
        },

        //显示数据明细
        showSData(item){
          this.lightEye(item)
          this.isSloading = true;
          this.isIconloading = true;
          this.isIconSloading = false;
          for(let list of this.dataSList){
            if(item.linkName === list.linkName){
              for(let dict of this.dictList){
                if(item.datasourceId === dict.dictId){
                  this.sourceData.sourceType = dict.dictName
                }
              }
              this.sourceData.ip = list.ip;
              this.sourceData.database = list.dbName;
              this.sourceData.sourceName = list.linkName;
              this.sourceData.port = list.port;
              this.sourceData.username = list.username;
              this.sourceData.password = list.password;
            }
          }
          this.clearSourceVail();
        },
        showTData(item){
          this.lightEye(item)
          this.isTloading = true;
          this.isIconTloading = false;
          for(let list of this.dataTList){
            if(item.linkName === list.linkName){
              for(let dict of this.dictList){
                if(item.datasourceId === dict.dictId){
                  this.targetData.targetType = dict.dictName;
                  this.targetData.env = dict.databaseFlag
                }
              }
              this.targetData.ip = list.ip;
              this.targetData.database = list.dbName;
              this.targetData.targetName = list.linkName;
              this.targetData.port = list.port;
              this.targetData.username = list.username;
              this.targetData.password = list.password;  
            }
          }
          this.clearTargetVail();
        },
        lightEye(item){
          if(item.datasourceType == '0'){
            if(item.focus){
              item.focus = false;
              this.addSourcePanel = false;
              this.sourceSelected = false;
            }else{
              this.sourceId = item.linkId;
              this.sourceName = item.linkName;
              this.addSourcePanel = true;
              this.sourceSelected = true;
              this.dataSList.forEach(val=>{this.$set(val,'focus',false)
              })
              this.$set(item,'focus',true)

            }
          }else if(item.datasourceType == '1'){
            if(item.focus){
              item.focus = false;
              this.addTargetPanel = false;
              this.targetSelected = false;
            }else{
              this.targetId = item.linkId;
              this.targetName = item.linkName;
              this.addTargetPanel = true;
              this.targetSelected = true;
              this.dataTList.forEach(val=>{this.$set(val,'focus',false)
              })
              this.$set(item,'focus',true)
            }
          }else{
              item.focus = false
          }

        },

        //同时获取下拉框的label和value的值
        changeSourceValue(dictId){
          let source = this.loadSList.find((item) => {
            return item.dictId === dictId;
          });
          this.sourceData.sourceType = source.dictId;
          this.sourceData.typeName = source.dictName;
        },
        changeEnvValue(dictId){
          let env = this.envList.find((item) => {
            return item.dictId === dictId;
          });
          this.targetData.databaseFlag = env.databaseFlag;
          this.targetData.env = env.dictId;
        },

        //跳转页面
        goDSRouting(){
          if(this.$store.state.sourceTargetMust){
            if(this.sourceSelected && this.targetSelected){
              this.$router.push({
                path:'DSRouting',
                query:{
                  sourceId:this.sourceId,
                  sourceName:this.sourceName,
                  targetId:this.targetId,
                  targetName:this.targetName,
                }
              })
              this.$store.state.dsRoute=false;
            } else {
              this.dialogTip = true;
            }
          }else{
            if(this.sourceSelected && this.targetSelected){
              this.$router.push({
                path:'DSRouting',
                query:{
                  sourceId:this.sourceId,
                  sourceName:this.sourceName,
                  targetId:this.targetId,
                  targetName:this.targetName,
                }
              })
              this.$store.state.dsRoute=false;
            } else {
              this.$router.push('DSRouting')
            }
          }

        },
        goDataSync(){
          this.$router.push({
            path: 'DSMetadataSync',
            query:{
              sourceId: this.sourceId,
              sourceName: this.sourceName,
              datasourceType: 0,
              datasourceChoice: this.sourceData.sourceType,
              datasourceId: this.sourceData.sourceType = 'MySQL' ? 2 : '',
              ip: this.sourceData.ip,
              port: this.sourceData.port,
              dbName: this.sourceData.database,
              username: this.sourceData.username,
              password: this.sourceData.password,
              linkId: this.sourceId
            }
          });
        },


        //加密
        encryptFun(pwd){
          let crypt = new JSEncrypt();
          crypt.setKey(`MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjh/VN6nKKDlk4cb43aUsEpsNRDqgfFmb6Hs1hPhN4yysh3tvpdlKwPRTlSSeqa7Oy0AG6rZnx8bNXSdFQPWImVYwbg/xbYhj2v8JGDuaKBviGxxPAXLHfpR6h6AQT33rAMkkVn9u7jj4/OtNWYWCWa0I4/Tk8sDlvwhtjvmeEzyF14KzjEdBnBKL8SNvLe26dipckasOjQMlt1Eeko7PoMVa07vit0bVqkhheXI5ppGkx2WfnrZqyMukDB515SEmlyQQYqkmXdmSAqPEYot+rANNKaUebr3TR7K5SKidZ27YlCm/gVZnU8amaycDNU0HGvkrFmk75Jh54SYh3kMwqQIDAQAB`)

          return crypt.encrypt(pwd)
        },
      },
      computed:{
        showNum(){
          return Math.floor((document.documentElement.clientWidth - 217) / 217)
        }
      }
    }
</script>

<style lang="less" scoped>
  .container{margin-top: 10px;
    .source,.target{position: relative;
      .main{height: auto;
        .table-list{display: flex;flex-wrap:wrap;justify-content: flex-start;margin-top: 10px; }
        .add-area, li{
          margin-left: 15px;
          display: flex;
          align-items: center;
          justify-content: center;
          position: relative;
          .icon{ position: absolute;top: 10px;right: 10px}
          .icon-thumb-tack{ position: absolute;top: -10px;left: 10px }
        }
        li{cursor: pointer}
      }
        .add-panel {
          margin: 20px;
          .el-form{display: flex;flex-wrap: wrap;
          }
        .el-form-item{width: 25%}
          .button{display: flex;flex-direction: row-reverse;}
        } }
    .target{position: absolute;position:relative;margin-top: 20px;}
  }

  .tab-title{width:100px;height: 30px;display: flex;justify-content: space-around;align-items: center;padding: 6px;}

  .main-top{
    position: relative;top: 2px;
    .icon-link{
      position: absolute;
      right: 20px;
      top:10px
    }
  }
</style>
