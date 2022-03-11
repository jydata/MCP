<template>
  <section>
    <div class="header">
      <div class="top clearfix">
        <div class="left">
          <i class="icon-p-service"></i>
          <span>Scheduler Logs</span>
        </div>
        <div class="right">
          <el-button :disabled="loading"  @click="searchFun">Query</el-button>
        </div>
      </div>
      <div class="down">
        <div class="list clearfix">
          <div class="press">
            <span class="key">JobName</span>
            <!--<el-input v-model="jobName" style="width: 65%"></el-input>-->
              <el-select @change="changeJobNameFun" v-model="jobId" filterable clearable placeholder="Please select">
                <el-option
                  v-for="item in jobNameList"
                  :key="item.jobId"
                  :label="item.jobName"
                  :value="item.jobId">
                </el-option>
              </el-select>
          </div>
          <div class="press">
            <span class="key">Trigger Code</span>
            <!--<el-input v-model="jobName" style="width: 65%"></el-input>-->
              <el-select @change="changeTriggerCodeFun" v-model="triggerCode" filterable clearable placeholder="Please select">
                <el-option
                  v-for="item in triggerCodeList"
                  :key="item.code"
                  :label="item.msg"
                  :value="item.code">
                </el-option>
              </el-select>
          </div>
        </div>
      </div>
    </div>
    <!--主列表显示-->
    <div class="table-master">
      <el-table :data="dataList" border v-autoHeight highlight-current-row>
        <el-table-column type="index" label="ID" align="center" width="60px" :sortable=true></el-table-column>
        <el-table-column prop="jobName" label="JobName" align="center" show-overflow-tooltip :sortable=true></el-table-column>
        <el-table-column prop="triggerTime" label="Trigger Time" align="center" :formatter="formatTriggerTime" width="160px"></el-table-column>
        <el-table-column prop="triggerCode" label="Trigger Code" align="center">
          <template slot-scope="scope">
            <span>{{scope.row.triggerCode === 100 ? 'Init' : scope.row.triggerCode === 200 ? 'Success' : 'Fail'}}</span>
          </template>
        </el-table-column>
        <el-table-column prop="triggerMsg" label="Trigger Message" show-overflow-tooltip align="center"></el-table-column>

        <el-table-column prop="handleTime" label="Handle Time" :formatter="formatHandleTime" width="160px" align="center"></el-table-column>
        <el-table-column prop="handleCode" label="Handle Code" align="center">
          <template slot-scope="scope">
            <span>{{scope.row.handleCode === 100 ? 'Init' : scope.row.handleCode === 200 ? 'Success' : 'Fail'}}</span>
          </template>
        </el-table-column>
        <el-table-column prop="handleMsg" label="Handle Message" show-overflow-tooltip align="center"></el-table-column>
        <el-table-column label="Handles" align="center">
          <template slot-scope="scope">
            <el-button type="text" @click="handleLog(scope.row)">Log Detail</el-button>
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

    <!-- log dialog -->
    <el-dialog
      :close-on-click-modal="false"
      title="Log Details"
      :visible.sync="enableLogDialog"
      :before-close="handleCloseFun"
      @close="closeLogDialogFun">
      <el-scrollbar style="height: 100%">
        <span v-html="showLogMsg"></span>
      </el-scrollbar>
      <span slot="footer" class="dialog-footer">
        <el-button @click="closeLogDialogFun">Close</el-button>
      </span>
    </el-dialog>

  </section>
</template>

<script>
  import {page, pageSize, pageSizes} from '@config/config';
  import * as Code from '@config/code';
  import * as Tips from '@config/tips';
  import * as Job from '@api/Job';
  import * as Metadata from '@api/Metadata';
  import JsEncrypt from 'jsencrypt'

  export default {
    name: 'dataList',
    data () {
      return {
        page: page,
        pageSize: pageSize,
        pageSizes: pageSizes,
        total: 0,
        dataList: [],
        jobNameList:[],
        triggerCodeList:[
          {
           code:100,
           msg:'Init'
          },
          {
            code:200,
            msg:'Success'
          },
          {
            code:500,
            msg:'Fail'
          }
        ],
        jobId: '',
        jobName: '',
        triggerCode: '',
        showLogMsg: '',
        loading: false,
        enableLogDialog: false,
        rules: {
        },
      }
    },
    created () {
      this.loadJobNames();
      this.jobId = this.$store.state.jobId ? this.$store.state.jobId : '';
      this.searchFun(1);
    },
    methods: {
      // 搜索
      searchFun(page){
        if(page) this.page=1;
        Job.queryLogs({
          pageSize: this.pageSize,
          currentPage: page ?  1 : this.page,
          jobId: this.jobId,
          triggerCode: this.triggerCode
        }).then(res => {
          if (res.code === Code.SUCCESS) {
            this.dataList = res.items.list;
            this.total = res.items.total;
          } else {
            this.$message({message: res.message, type: 'error'});
          }
        })
      },

      // 加载作业名称列表
      loadJobNames(){
        Job.queryJobNames().then(res => {
          if (res.code === Code.SUCCESS) {
            this.jobNameList = res.items;
          } else {
            this.$message({message: res.message, type: 'error'});
          }
        })
      },

      changeJobNameFun(val){
        this.jobNameList.find((item) => {
          if (item.jobId === val){
            this.jobId = item.jobId;
            this.jobName = item.jobName;
          }
        });
      },
      changeTriggerCodeFun(val){
        this.triggerCodeList.find((item) => {
          if (item.code === val){
            this.triggerCode = item.code;
          }
        });
      },

      // 日志信息
      handleLog(row) {
        this.enableLogDialog = true;
        // 读取日志
        Job.queJobLogById({
          logId: row.logId,
          triggerTime: row.triggerTime,
          fromLineNum: 1000
        }).then(res => {
          if (res.code === Code.SUCCESS) {
            this.showLogMsg = res.items.logInfo;
          } else {
            this.$message({message: res.message, type: 'error'});
          }
        })
      },

      handleCloseFun(){
        this.enableLogDialog = false;
      },
      closeLogDialogFun(){
        this.enableLogDialog = false;
      },

      handleSizeChange(pageSize){
        this.pageSize = pageSize;
        this.searchFun();
      },
      handleCurrentChange(page){
        this.page = page;
        this.searchFun();
      },
      formatTime(date, fmt){
        var o = {
          "M+": date.getMonth() + 1, //月份
          "d+": date.getDate(), //日
          "h+": date.getHours(), //小时
          "m+": date.getMinutes(), //分
          "s+": date.getSeconds(), //秒
          "q+": Math.floor((date.getMonth() + 3) / 3), //季度
          "S": date.getMilliseconds() //毫秒
        };
        if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
          if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
      },
      formatTriggerTime(item){
        let str = '';
        if (item && item.triggerTime){
            str = this.formatTime(new Date(item.triggerTime), "yyyy-MM-dd hh:mm:ss");
        }
        return str;
      },
      formatHandleTime(item){
        let str = '';
        if (item && item.handleTime){
          str = this.formatTime(new Date(item.handleTime), "yyyy-MM-dd hh:mm:ss");
        }
        return str;
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

<style lang="less">
  .el-aside {
    background-color: #D3DCE6;
    color: #333;
  }
  .el-main {
    background-color: #E9EEF3;
    color: #333;
  }

</style>
