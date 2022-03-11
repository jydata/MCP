<template>
  <section>
    <!--头部-->
    <div class="header">
      <div class="title">
        <el-breadcrumb separator-class="el-icon-arrow-right">
          <el-breadcrumb-item>DS Rules</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
    </div>

    <!--内容-->
    <div class="container pd20">
      <div class="table-1 shadow">
        <el-form label-width="180px" :rules="rule" ref="ruleForm" :model="ruleData" label-position="top">
            <el-form-item label="Rule Name" prop="ruleName" >
              <el-input v-model="ruleData.ruleName" maxlength=20 clearable></el-input>
            </el-form-item>
            <el-form-item label="Rule Type" prop="ruleType">
              <el-select filterable v-model="ruleData.ruleType" placeholder="Please select" @change="changeValue(ruleData.ruleType)">
                <el-option v-for="list in ruleTypeList" :label="list.dictName" :value="list.dictName" :key="list.dictName">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="Table Name Prefix" prop="rulePrefix" v-if="isShowTable">
              <el-input v-model="ruleData.rulePrefix" maxlength=20 clearable></el-input>
            </el-form-item>
            <el-form-item label="Table Name Suffix" prop="rulePrefix" v-if="isShowSuffix">
              <el-input v-model="ruleData.ruleSuffix" maxlength=20 clearable></el-input>
            </el-form-item>
            <el-form-item label="Add Column Name" prop="ruleColumn" v-if="isShowColumn">
              <span slot="label">Add Column Name <span class="fcred">( SQL keyword not allowed )</span></span>
              <el-input placeholder="Use the table database name as the value of the new column and as the union primary key" v-model="ruleData.ruleColumn" maxlength=20 clearable></el-input>
            </el-form-item>
            <el-form-item label="temp" v-if="isShowColumn">
              <span slot="label">Column Value offset <span class="deepblue">( start index: <span class='fcred fsize16'>{{valueSlide[0]}}</span>, end index: <span class='fcred fsize16'>{{valueSlide[1]}}</span> )</span></span>
              <div class="flex fxmiddle slider-rule">
                <!-- <el-switch
                v-model="ruleData.dbFlag"
                active-color="#2c6daf"
                active-value="1"
                inactive-value="0"
                @change=""
                >
                </el-switch> -->
                <span class="mr15">0</span>
                <el-slider
                  class="flex1 mr15"
                  v-model="valueSlide"
                  show-tooltip="true"
                  range
                  :show-tooltip="ruleData.dbFlag==1"
                  :format-tooltip="formatTooltip"
                  :disabled="ruleData.dbFlag=='0'"
                  :max="100">
                </el-slider>
                <span>100</span>
              </div>
            </el-form-item>
          </el-form>
        <div class="button">
          <el-button @click="saveRulesFun('ruleForm')">Save</el-button>
        </div>
      </div>

      <div class="table-2 mt15">
        <el-table class="shadow" :data="ruleList" empty-text="No data" width="60%">
          <el-table-column label="#" type="index" width="60px"></el-table-column>
          <el-table-column label="Rule Name" prop="ruleName" width="200px"></el-table-column>
          <el-table-column label="Rule Type" prop="ruleType" width="300px"></el-table-column>
          <el-table-column label="Comment" prop="comment"></el-table-column>
          <el-table-column label="Details" width="120px">
            <template slot-scope="scope">
              <el-popover
                placement="left"
                width="auto"
                trigger="hover"
                open-delay="0">
                <ul class="detail">
                  <li v-show="scope.row.ruleType == 'Default' || scope.row.ruleType === 'Restore'">
                    <div class="detail-title">Rule Name：</div>
                    <div class="detail-ct deepblue">{{scope.row.ruleName}}</div>
                  </li>
                  <li v-show="scope.row.rulePrefix">
                    <div class="detail-title">Table Name Prefix：</div>
                    <div class="detail-ct deepblue">{{scope.row.rulePrefix}}</div>
                  </li>
                  <li v-show="scope.row.ruleColumn">
                    <div class="detail-title">Rule Column：</div>
                    <div class="detail-ct deepblue">{{scope.row.ruleColumn}}</div>
                  </li>
                  <li v-show="scope.row.dbFlag==1 && scope.row.ruleType !== 'Restore'">
                    <div class="detail-title">beginIndex：</div>
                    <div class="detail-ct deepblue">{{scope.row.beginIndex}}</div>
                  </li>
                  <li v-show="scope.row.dbFlag==1 && scope.row.ruleType !== 'Restore'">
                    <div class="detail-title">endIndex：</div>
                    <div class="detail-ct deepblue">{{scope.row.endIndex}}</div>
                  </li>
                  <li v-show="scope.row.ruleType === 'Restore'">
                    <div class="detail-title">Rule Suffix：</div>
                    <div class="detail-ct deepblue">{{scope.row.ruleSuffix}}</div>
                  </li>
                </ul>
                <i class="detail-icon icon-file-text-o" slot="reference"></i>
              </el-popover>
            </template>
          </el-table-column>
          <el-table-column label="Action" width="70px">
            <template slot-scope="scope">
              <i class="icon-trash" @click="delRulesFun(scope.row)"></i>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </section>
</template>

<script>
  import * as Meta from '@api/Meta';
  import * as Code from '@config/code';
  import * as Tips from '@config/tips';
  export default {
      name: "DSRules",
      data(){
        let reg = /^[a-zA-Z]+[\w]*$/;
        let checkTable = (rule, value, callback) => {
          if (value.trim().length <= 0) {
              if(value === '' ){
                return callback();
              }else{
                return callback(new Error('Spaces are not allowed'));
              }
            }
           else if (!reg.test(value)) {
            callback(new Error('Only numbers,letters and underline are allowed, and letters must be in front'));
          } else {
            callback();
          }
        };
        let checkColumn = (rule, value, callback) => {
          if (value === '' || value.trim().length <= 0) {
            return callback(new Error('Column is required'));
          } else if (!reg.test(value)) {
            callback(new Error('Only numbers,letters and underline are allowed, and letters must be in front'));
          } else {
            callback();
          }
        };
        return{
          valueSlide:[0,100],
          isShowTable: false,
          isShowColumn: false,
          isShowSuffix: false,
          isDetail: true,

          ruleTypeList:[],

          ruleData: {
            ruleName: '',
            ruleType: '',
            rulePrefix: '',
            ruleColumn: '',
            ruleSuffix: '',
            dbFlag:1
          },
          ruleList: [],
          rule:{
            ruleName: [
              {required: true, message: 'Rule Name is required', trigger: 'blur'},
            ],
            ruleType: [
              {required: true, message: 'Rule Type is required', trigger: 'blur'}
            ],
            rulePrefix: [
              {required: false, validator: checkTable, trigger: 'blur'},
            ],
            ruleColumn: [
              {required: true, validator: checkColumn, trigger: 'blur'},
            ]
          }
        }
      },
      created(){
        this.getRuleType()
        this.getRulesFun()
      },
      methods:{
        leave(item){
          item.focus = false;
        },
        changeValue(item){
          switch (item) {
            case 'Default':
              this.isShowColumn = false;
              this.isShowTable = false;
              this.isShowSuffix = false
              this.ruleData.rulePrefix = '';
              this.ruleData.ruleColumn = ''
              break;
            case 'Merge table':
              this.isShowTable = true;
              this.isShowColumn = false;
              this.isShowSuffix = false
              this.ruleData.ruleColumn = ''
              break;
            case 'Merge table and add column':
              this.isShowSuffix = false
              this.isShowColumn = true;
              this.isShowTable = true;
              break;
            case 'Restore':
              this.isShowSuffix = true
              this.isShowColumn = false
              this.isShowTable = false
              break
            default: 
              break;
          }
        },
        //获取规则
        getRulesFun(){
          Meta.getRules({}).then(res => {
            if (res.code === Code.SUCCESS) {
              this.ruleList = res.items;
              this.ruleList.forEach(val=>{this.$set(val,'focus',false)})
            } else {
              this.$message({message: res.message, type: 'error'});
            }
          })
        },

        //获取规则类型
        getRuleType(){
          Meta.getRuleType({}).then(res => {
            if (res.code === Code.SUCCESS) {
              this.ruleTypeList = res.items;
            } else {
              this.$message({message: res.message, type: 'error'});
            }
          })
        },

        //保存规则
        saveRulesFun(formName){
          this.$refs[formName].validate((valid) => {
            if (valid) {
              let slide=this.valueSlide;
              let p={
                ruleName: this.ruleData.ruleName,
                ruleType: this.ruleData.ruleType,
                rulePrefix: this.ruleData.rulePrefix,
                ruleColumn: this.ruleData.ruleColumn,
                ruleSuffix: this.ruleData.ruleSuffix,
                /*dbFlag:this.ruleData.ruleType!='Merge table and add column'?'0':this.ruleData.dbFlag,*/
                dbFlag:1,
                beginIndex:slide[0],
                endIndex:slide[1]
              };
              if(p.dbFlag==0){
                p.beginIndex=p.endIndex=''
              };
              if(slide[0]==slide[1]){
                return this.$message({message:'Start index is the same as end index',type:'warning'})
              }
              Meta.saveRules(p).then(res => {
                if (res.code === Code.SUCCESS) {
                  this.$message({message: Tips.SAVE_SUCCESS, type: 'success'});
                  this.getRulesFun()
                  this.ruleData={
                    ruleName:'',
                    ruleType:p.ruleType,
                    rulePrefix:'',
                    ruleColumn:'',
                    dbFlag:'1',
                    beginIndex:slide[0],
                    endIndex:slide[1]
                  }
                } else {
                  this.$message({message: res.message, type: 'error'});
                }
              }).catch(() => {
              })
            }
            })
        },

        //删除规则
        delRulesFun(item){
          this.$confirm('Are you sure delete this rule？', 'Message', {
            confirmButtonText: 'Confirm',
            cancelButtonText: 'Cancel',
          }).then(() => {
            let params = item;
            Meta.delRules(params).then(res => {
              if (res.code === Code.SUCCESS) {
                this.$message({message: Tips.DELETE_SUCCESS, type: 'success'});
                this.getRulesFun()
              } else {
                this.$message({message: res.message, type: 'error'});
              }
            })
          }).catch(() => {
            })
        },
        formatTooltip(val){
          return 'Table of DBName: '+val
        }
      }
  }
</script>

<style lang="less" scoped>
  .table-1{margin: 0;display: flex;justify-content: space-between;
    .el-form{width: 60%;display: flex;flex-direction: column;flex-wrap: wrap;padding: 20px;}
    .button{position: relative;width: 30%;
      .el-button{position:absolute;bottom: 30px;right: 30px}}
  }
  .el-select{width: 100%}

  .detail-title{
    color: #888;
    flex: 1;max-width: 136px;min-width: 136px;
    text-align: right;
  }
  .detail-ct{
    flex: 1;
    text-align: left;display: block;
  }
  .detail{
    white-space: nowrap;
    li{
      display: flex;
    }
  }
  .slider-rule{
    span{
      padding: 0 5px;font-size: 17px;
    }
  }
</style>
