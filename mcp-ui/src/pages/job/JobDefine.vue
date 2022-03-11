<template>
  <section>
    <div class="header">
      <div class="top clearfix">
        <div class="left">
          <i class="icon-p-service"></i>
          <span>Job Define Management</span>
        </div>
        <div class="right">
          <el-button :disabled="loading"  @click="searchFun(1)">Query</el-button>
          <el-button  @click="addNeWJobFun">Add</el-button>
        </div>
      </div>
      <div class="down">
        <div class="list clearfix">
          <div class="press">
            <span class="key">Job Name</span>
            <el-input v-model="jobName" clearable></el-input>
          </div>
          <div class="press">
            <span class="key">Route Name</span>
            <el-input v-model="routeName"  clearable></el-input>
          </div>
          <!--<div class="press">
            <el-button :disabled="loading" class="el-button-search" @click="delBatchFun">
              <i class="icon-tab-close"></i> 批量删除
            </el-button>
          </div>-->
        </div>
      </div>
    </div>

    <!--主列表显示-->
    <div class="table-master">
      <el-table style="width: 100%;" :data="dataList" border  v-autoHeight highlight-current-row>
        <el-table-column type="index" label="ID" align="center" width="60" :sortable=true fixed></el-table-column>
        <el-table-column prop="jobName" label="JobName" align="center" :sortable=true fixed width="120"></el-table-column>
        <el-table-column prop="routeName" label="RouteName" align="center" :sortable=true width="120"></el-table-column>
        <el-table-column prop="agent" label="Agent Active" align="center" :sortable=true width="140"></el-table-column>
        <el-table-column prop="cron" label="Cron" align="center" :sortable=true width="120"></el-table-column>
        <el-table-column prop="jobType" label="Job Type" align="center" width="140">
          <template slot-scope="scope">
            <span>{{scope.row.jobType === 'increment' ? 'Increment' : scope.row.jobType === 'full' ? 'Full' : 'ColumnName'}}</span>
          </template>
        </el-table-column>
        <el-table-column prop="busStartTime" label="Bus Start Time" :formatter="formatBusStartTime" align="center" width="160"></el-table-column>
        <el-table-column prop="busEndTime" label="Bus End Time" :formatter="formatBusEndTime" align="center" width="160"></el-table-column>
        <el-table-column prop="recentBinlog" label="Recent Binlog" align="center" width="120">
          <template slot-scope="scope">
            <span>{{scope.row.recentBinlog === '1' ? 'Yes' : 'No'}}</span>
          </template>
        </el-table-column>
        <el-table-column prop="recentBinlogName" label="Recent BinlogName" align="center" width="160"></el-table-column>
        <el-table-column prop="recentBinlogPosition" label="Recent Binlog Position" align="center" width="180"></el-table-column>
        <el-table-column prop="enable" label="Enable" align="center" width="80">
          <template slot-scope="scope">
            <el-switch
            active-color="#3d82ca"
            inactive-color="#bbb"
            v-model="scope.row.enable"
            @change="changeEnableSwitchFun(scope.$index, scope.row)"
            :active-value="1"
            :inactive-value="0">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="Status" align="center" width="80">
          <template slot-scope="scope">
            <span>{{scope.row.status === 'init' ? 'Init' : scope.row.status === 'wait' ? 'Wait' : scope.row.status === 'running' ? 'Running' : scope.row.status === 'fail' ? 'Fail' : scope.row.status === 'success' ? 'Success' : ''}}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="Create Time" :formatter="formatTimeFun" align="center" width="160"></el-table-column>
        <el-table-column prop="binlogName" label="BinlogName" align="center" width="160"></el-table-column>
        <el-table-column prop="binlogPosition" label="BinlogPosition" align="center" width="120"></el-table-column>
        <el-table-column label="Handle" align="center" width="190" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" v-if="scope.row.enable === 1 && scope.row.jobType !== 'increment'" @click="handleTrigger(scope.row)">Execute</el-button>
            <el-button type="text" v-if="scope.row.status === 'running'" @click="handlePause(scope.row)">Pause</el-button>
            <el-button type="text" v-if="scope.row.status === 'wait'" @click="handleResume(scope.row)">Recover</el-button>
            <el-button type="text" style="float: right;height: 18px;" @click="handleLog(scope.row)">Log</el-button>
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

    <!-- add job dialog -->
    <el-dialog class="add-dialog" title="Add Job" :visible.sync="addJobDialog"  :close-on-click-modal="false">
      <el-tabs type="card" style="height: 100%;">
        <!--增量同步-->
        <el-tab-pane label="Increment">
          <label style="font-weight:bold">IncrementData Sync</label>
          <el-container style="height: auto">
            <el-aside width="100%">
              <el-form label-width="110px" :model="addFormIncrement" :rules="rules" ref="addFormIncrement" class="item-error">
                <el-row>
                  <el-col :span="11">
                    <el-form-item label="Job Name" prop="jobNameIncrement">
                      <el-input v-model="addFormIncrement.jobNameIncrement" size="mini" style="width: 180px" clearable></el-input>
                    </el-form-item>
                    <el-form-item label="Route ID" prop="routeIdIncrement">
                      <el-select @change="changeRouteIncrementFun" v-model="addFormIncrement.routeIdIncrement" style="width: 180px" size="mini" ilterable clearable placeholder="Please select">
                          <el-option
                            v-for="item in routeList"
                            :key="item.routeId"
                            :label="item.routeName"
                            :value="item.routeId">
                          </el-option>
                      </el-select>
                    </el-form-item>
                    <div style="margin-left: 110px">
                      <el-checkbox v-model="addFormIncrement.isRecent" @change="handleCheckBinlogChange">Recent Binlog</el-checkbox>
                    </div>
                    <el-form-item label="BinlogName" prop="binlogName">
                      <el-select v-model="addFormIncrement.binlogName" style="width: 180px" size="mini" class="binlogDisabled" :disabled="isBinlogDisabled" filterable clearable placeholder="Please select">
                        <el-option v-for="item in binlogList" :key="item.log_name" :label="item.log_name" :value="item.log_name"></el-option>
                      </el-select>
                    </el-form-item>
                    <el-form-item label="BinlogPosition" prop="binlogPosition">
                      <el-input v-model="addFormIncrement.binlogPosition" type="number" style="width: 80%" size="mini" class="binlogDisabled" :disabled="isBinlogDisabled"></el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="11">
                    <el-form-item label="Agent" prop="agentIncrement">
                      <div style="overflow: auto; height: 180px;width: 190px">
                        <el-tree
                          :props="defaultAgentProps"
                          show-checkbox
                          node-key="agentId"
                          :data="agentList"
                          @check-change="handleCheckAgentChange"
                          :expand-on-click-node="true"
                          ref="agent_tree">
                        </el-tree>
                      </div>
                    </el-form-item>
                    <!--<el-form-item label="Cron" prop="cronIncrement">
                      <el-input v-model="addFormIncrement.cronIncrement"  style="width: 180px" size="mini" clearable></el-input>
                    </el-form-item>
                    <el-form-item>
                      <el-tooltip placement="left-start">
                        <div slot="content">
                          Cron表达式提示：<br/>
                          秒（0–59）<br/>
                          分钟（0–59）<br/>
                          小时（0–23）<br/>
                          月份中的日期（1–31）<br/>
                          月份（1–12或JAN–DEC）<br/>
                          星期中的日期（1–7或SUN–SAT）<br/>
                          年份（1970–2099）<br/>
                          <br/>
                          特殊字符：<br/>
                          * ("所有值") - 用来选择一个字段中的所有值. <br/><br/>
                          ? ("没有具体的值") - "?"就是来跳过一个的作用。<br/><br/>
                            - - 用于指定范围。<br/><br/>
                          , - 用于指定额外的值。<br/><br/>
                          / - 用来指定增量。 <br/><br/>
                          L ("last") - 2个字段中可以使用它，并且意义不同。<br/>
                          例如，在“月”字段中使用它，表示，这个月的最后一天。<br/>
                          如果在“day-of-week”字段中使用它，表示"7"或者"SAT"，也就是周六。<br/><br/>
                          W ("weekday") - 用于指定工作日（周一至周五）最近的某一天。<br/>
                          # - 用于指定月份的第几天。<br/>
                        </div>
                        <el-button  icon="el-icon-warning" type="primary" size="mini" circle></el-button>
                      </el-tooltip>
                      <el-tooltip placement="right-start">
                        <div slot="content">
                          每隔10秒执行一次：*/10 * * * * ?<br/>
                          每隔1分钟执行一次：0 */1 * * * ?<br/>
                          每天22点执行一次：0 0 22 * * ?<br/>
                          每天凌晨3点执行一次：0 0 3 * * ?<br/>
                          每月1号凌晨3点执行一次：0 0 1 3 * ?<br/>
                          每月最后一天22点执行一次：0 0 22 L * ?<br/>
                          每周星期天凌晨3点实行一次：0 0 3 ? * L<br/>
                          在20分、30分、40分执行一次：0 20,30,40 * * * ?<br/>
                          每天0点、10点、16点、22点都执行一次：0 0 0,10,16,22 * * ?
                        </div>
                        <el-button  icon="el-icon-question" type="primary" size="mini" circle></el-button>
                      </el-tooltip>
                    </el-form-item>-->
                  </el-col>
                </el-row>
                <el-form-item>
                  <el-button @click="checkEnvFun('addFormIncrement', 'increment')">Check Env</el-button>
                  <el-button @click="insertJobFun('addFormIncrement', 'increment')">Insert</el-button>
                  <el-button @click="addJobDialog=!addJobDialog">Close</el-button>
                </el-form-item>
              </el-form>
            </el-aside>
            <el-main><span v-html="addFormIncrement.showIncrementEnvMsg"></span></el-main>
          </el-container>
        </el-tab-pane>

        <!--全量同步-->
        <el-tab-pane label="Full">
          <label style="font-weight:bold">FullData Sync</label>
          <el-container>
            <el-aside width="100%">
              <el-form label-width="100px" :model="addFormFull" :rules="rules" ref="addFormFull" class="item-error">
                <el-row>
                  <div style="margin-left: 392px">
                    <el-checkbox v-model="addFormFull.busTime" align="left"  @change="handleCheckBusTimeChange">Bus Time</el-checkbox>
                  </div>
                  <el-col :span="11">
                    <el-form-item label="Job Name" prop="jobNameFull">
                      <el-input v-model="addFormFull.jobNameFull"  size="mini" style="width: 190px" clearable></el-input>
                    </el-form-item>
                    <el-form-item label="Route Id" prop="routeIdFull">
                      <el-select @change="changeRouteFullFun" v-model="addFormFull.routeIdFull" size="mini" style="width: 190px" filterable clearable placeholder="Please select">
                        <el-option
                          v-for="item in routeList"
                          :key="item.routeId"
                          :label="item.routeName"
                          :value="item.routeId">
                        </el-option>
                      </el-select>
                    </el-form-item>
                    <el-form-item label="Sync Table">
                      <div style="overflow: auto; height: 205px;width: 190px">
                        <el-tree
                          :props="defaultProps"
                          show-checkbox
                          node-key="label"
                          :data="tableList"
                          :expand-on-click-node="true"
                          ref="meta_tree">
                        </el-tree>
                      </div>
                    </el-form-item>
                  </el-col>
                  <el-col :span="11">
                    <el-form-item label="Start Time">
                      <el-date-picker
                        v-model="addFormFull.busStartTime"
                        type="datetime"
                        placeholder="Please select"
                        :picker-options="pickerOptions"
                        :disabled="isBusTimeDisabled"
                        size="mini"
                        style="width: 190px"
                        class="timeDisabled">
                      </el-date-picker>
                    </el-form-item>
                    <el-form-item label="End Time">
                      <el-date-picker
                        v-model="addFormFull.busEndTime"
                        type="datetime"
                        placeholder="Please select"
                        :picker-options="pickerOptions"
                        default-time="23:59:59"
                        :disabled="isBusTimeDisabled"
                        size="mini"
                        style="width: 190px"
                        class="timeDisabled">
                      </el-date-picker>
                    </el-form-item>

                    <el-form-item label="Cron:" prop="cronFull">
                      <el-input v-model="addFormFull.cronFull" size="mini" style="width: 190px" clearable></el-input>
                    </el-form-item>
                    <el-form-item>
                      <el-tooltip placement="left-start">
                        <div slot="content">
                          Cron表达式提示：<br/>
                          秒（0–59）<br/>
                          分钟（0–59）<br/>
                          小时（0–23）<br/>
                          月份中的日期（1–31）<br/>
                          月份（1–12或JAN–DEC）<br/>
                          星期中的日期（1–7或SUN–SAT）<br/>
                          年份（1970–2099）<br/>
                          <br/>
                          特殊字符：<br/>
                          * ("所有值") - 用来选择一个字段中的所有值. <br/><br/>
                          ? ("没有具体的值") - "?"就是来跳过一个的作用。<br/><br/>
                          - - 用于指定范围。<br/><br/>
                          , - 用于指定额外的值。<br/><br/>
                          / - 用来指定增量。 <br/><br/>
                          L ("last") - 2个字段中可以使用它，并且意义不同。<br/>
                          例如，在“月”字段中使用它，表示，这个月的最后一天。<br/>
                          如果在“day-of-week”字段中使用它，表示"7"或者"SAT"，也就是周六。<br/><br/>
                          W ("weekday") - 用于指定工作日（周一至周五）最近的某一天。<br/>
                          # - 用于指定月份的第几天。<br/>
                        </div>
                        <el-button type="primary" icon="el-icon-warning" size="mini" circle></el-button>
                      </el-tooltip>
                      <el-tooltip placement="right-start">
                        <div slot="content">
                          每隔10秒执行一次：*/10 * * * * ?<br/>
                          每隔1分钟执行一次：0 */1 * * * ?<br/>
                          每天22点执行一次：0 0 22 * * ?<br/>
                          每天凌晨3点执行一次：0 0 3 * * ?<br/>
                          每月1号凌晨3点执行一次：0 0 1 3 * ?<br/>
                          每月最后一天22点执行一次：0 0 22 L * ?<br/>
                          每周星期天凌晨3点实行一次：0 0 3 ? * L<br/>
                          在20分、30分、40分执行一次：0 20,30,40 * * * ?<br/>
                          每天0点、10点、16点、22点都执行一次：0 0 0,10,16,22 * * ?
                        </div>
                        <el-button type="primary" icon="el-icon-question" size="mini" circle></el-button>
                      </el-tooltip>
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-form-item>
                  <el-button @click="checkEnvFun('addFormFull', 'full')">Check Env</el-button>
                  <el-button @click="insertJobFun('addFormFull', 'full')">Insert</el-button>
                  <el-button @click="addJobDialog=!addJobDialog">Close</el-button>
                </el-form-item>
              </el-form>
            </el-aside>

            <el-main><span v-html="addFormFull.showFullEnvMsg"></span></el-main>
          </el-container>
        </el-tab-pane>

        <!--列名同步-->
        <el-tab-pane label="Column">
          <label style="font-weight:bold">Column Information Sync</label>
          <el-container>
            <el-aside width="100%">
              <el-form label-width="100px" :model="addFormColumn" :rules="rules" ref="addFormColumn" class="item-error">
                <el-form-item label="Job Name" prop="jobNameColumn">
                  <el-input v-model="addFormColumn.jobNameColumn"size="mini" style="width: 190px" clearable></el-input>
                </el-form-item>
                <el-form-item label="Route ID" prop="routeIdColumn">
                  <el-select @change="changeRouteColumnFun"  size="mini" style="width: 190px" v-model="addFormColumn.routeIdColumn"filterable clearable placeholder="Please select">
                    <el-option
                      v-for="item in routeList"
                      :key="item.routeId"
                      :label="item.routeName"
                      :value="item.routeId">
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="Cron" prop="cronColumn">
                  <el-input v-model="addFormColumn.cronColumn" size="mini" style="width: 190px" clearable></el-input>
                </el-form-item>
                <el-form-item>
                  <el-tooltip placement="left-start">
                    <div slot="content">
                      Cron表达式提示：<br/>
                      秒（0–59）<br/>
                      分钟（0–59）<br/>
                      小时（0–23）<br/>
                      月份中的日期（1–31）<br/>
                      月份（1–12或JAN–DEC）<br/>
                      星期中的日期（1–7或SUN–SAT）<br/>
                      年份（1970–2099）<br/>
                      <br/>
                      特殊字符：<br/>
                      * ("所有值") - 用来选择一个字段中的所有值. <br/><br/>
                      ? ("没有具体的值") - "?"就是来跳过一个的作用。<br/><br/>
                      - - 用于指定范围。<br/><br/>
                      , - 用于指定额外的值。<br/><br/>
                      / - 用来指定增量。 <br/><br/>
                      L ("last") - 2个字段中可以使用它，并且意义不同。<br/>
                      例如，在“月”字段中使用它，表示，这个月的最后一天。<br/>
                      如果在“day-of-week”字段中使用它，表示"7"或者"SAT"，也就是周六。<br/><br/>
                      W ("weekday") - 用于指定工作日（周一至周五）最近的某一天。<br/>
                      # - 用于指定月份的第几天。<br/>
                    </div>
                    <el-button type="primary" icon="el-icon-warning" size="mini" circle></el-button>
                  </el-tooltip>
                  <el-tooltip placement="right-start">
                    <div slot="content">
                      每隔10秒执行一次：*/10 * * * * ?<br/>
                      每隔1分钟执行一次：0 */1 * * * ?<br/>
                      每天22点执行一次：0 0 22 * * ?<br/>
                      每天凌晨3点执行一次：0 0 3 * * ?<br/>
                      每月1号凌晨3点执行一次：0 0 1 3 * ?<br/>
                      每月最后一天22点执行一次：0 0 22 L * ?<br/>
                      每周星期天凌晨3点实行一次：0 0 3 ? * L<br/>
                      在20分、30分、40分执行一次：0 20,30,40 * * * ?<br/>
                      每天0点、10点、16点、22点都执行一次：0 0 0,10,16,22 * * ?
                    </div>
                    <el-button type="primary" icon="el-icon-question" size="mini" circle></el-button>
                  </el-tooltip>
                </el-form-item>
                <el-form-item>
                  <el-button @click="checkEnvFun('addFormColumn', 'column')">Check Env</el-button>
                  <el-button @click="insertJobFun('addFormColumn', 'column')">Insert</el-button>
                  <el-button @click="addJobDialog=!addJobDialog">Close</el-button>
                </el-form-item>
              </el-form>
            </el-aside>
            <el-main><span v-html="addFormColumn.showColumnEnvMsg"></span></el-main>
          </el-container>
        </el-tab-pane>

      </el-tabs>
    </el-dialog>

    <!-- change enable dialog -->
    <el-dialog
      :close-on-click-modal="false"
      title="Tips"
      :visible.sync="enableJobDialog"
      width="30%"
      :before-close="handleCloseFun"
      @close="closeDialogFun">
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
  import * as Job from '@api/Job';
  import * as Metadata from '@api/Metadata';
  import JsEncrypt from 'jsencrypt'

  export default {
    name: 'dataList',
    data () {
      // 检查作业名称是否为空
      var checkJobName = (rule, value, callback) => {
        if (value === '' || value.trim().length <= 0) {
          return callback(new Error('作业名称不能为空'));
        } else {
          callback();
        }
      };
      // 检查路由是否为空
      var checkRouteId = (rule, value, callback) => {
        if (value === '') {
          return callback(new Error('请选择路由'));
        } else {
          callback();
        }
      };
      // 检查Cron是否为空
      var checkCron = (rule, value, callback) => {
        if (value === '' || value.trim().length <= 0) {
          return callback(new Error('Cron不能为空'));
        } else {
          callback();
        }
      };
      // 检查Agent是否为空
      var checkAgent = (rule, value, callback) => {
        if (this.addFormIncrement.multiAgent === '' || this.addFormIncrement.multiAgent.trim().length <= 0) {
          return callback(new Error('请选择Agent'));
        } else {
          callback();
        }
      };
      // 检查binlogName是否为空
      var checkBinlogName = (rule, value, callback) => {
        if (value === '' || value.trim().length <= 0) {
          return callback(new Error('Binlog名称不能为空'));
        } else {
          callback();
        }
      };
      // 检查binlogPosition是否为空
      var checkBinlogPosition = (rule, value, callback) => {
        if (value === '') {
          return callback(new Error('Binlog位置不能为空'));
        } else {
          callback();
        }
      };

      return {
        page: page,
        pageSize: pageSize,
        pageSizes: pageSizes,
        total: 0,
        jobId: '',
        jobName: '',
        jobType: '',
        agent: '',
        cron: '',
        routeId: '',
        routeName: '',
        sourceId: '',
        sourceName: '',
        targetId: '',
        targetName: '',
        enable: 0,
        dataList: [],
        routeList: [],
        tableList: [],
        agentList: [],
        binlogList: [],
        multiSelection: [],
        isExpandTableTree: true,
        loading: false,
        addJobDialog: false,
        enableJobDialog: false,
        showMsg: '',
        isBinlogDisabled:true,
        isBusTimeDisabled: false,
        isShowJobNameColumn: false,

        addFormIncrement: {
          multiAgent: '',
          jobNameIncrement: '',
          routeIdIncrement: '',
          routeNameIncrement: '',
          cronIncrement: '',
          isRecent: true,
          recentBinlog: '1',
          binlogName: '',
          binlogPosition: '',
          showIncrementEnvMsg: ''
        },
        addFormFull: {
          jobNameFull: '',
          routeIdFull: '',
          routeNameFull: '',
          cronFull: '',
          busTime:true,
          busStartTime: '',
          busEndTime: '',
          showFullEnvMsg: ''
        },
        addFormColumn: {
          jobNameColumn: '',
          routeIdColumn: '',
          routeNameColumn: '',
          cronColumn: '',
          showColumnEnvMsg: ''
        },

        defaultProps: {
          label: 'label',
          children: 'children',
          isLeaf: 'leaf'
        },
        defaultAgentProps: {
          label: 'agentId'
        },

        // 日期控件
        pickerOptions: {
          shortcuts: [{
            text: '今天',
            onClick(picker) {
              picker.$emit('pick', new Date());
            }
          }, {
            text: '昨天',
            onClick(picker) {
              const date = new Date();
              date.setTime(date.getTime() - 3600 * 1000 * 24);
              picker.$emit('pick', date);
            }
          }, {
            text: '一周前',
            onClick(picker) {
              const date = new Date();
              date.setTime(date.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit('pick', date);
            }
          }]
        },

        rules: {
          jobNameIncrement: [
            {required: true, validator: checkJobName, trigger: 'blur'},
            {min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur'}
          ],
          jobNameFull: [
            {required: true, validator: checkJobName, trigger: 'blur'},
            {min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur'}
          ],
          jobNameColumn: [
            {required: true, validator: checkJobName, trigger: 'blur'},
            {min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur'}
          ],

          routeIdIncrement: [
            {required: true, validator: checkRouteId},
          ],
          routeIdFull: [
            {required: true, validator: checkRouteId},
          ],
          routeIdColumn: [
            {required: true, validator: checkRouteId},
          ],

          cronIncrement: [
            {required: true, validator: checkCron}
          ],
          cronFull: [
            {required: true, validator: checkCron}
          ],
          cronColumn: [
            {required: true, validator: checkCron}
          ],
          agentIncrement: [
            {required: true, validator: checkAgent}
          ],
          binlogName: [
            {required: true, validator: checkBinlogName}
          ],
          binlogPosition: [
            {required: true, validator: checkBinlogPosition}
          ]
        },
      }
    },
    created () {
      this.searchFun();
    },
    methods: {
      // 搜索
      searchFun(page){
        if(page) this.page=1;
        this.dataList = [];
        this.total = 0;
        Job.getJobs({
          currentPage: page ?  1 : this.page,
          pageSize: this.pageSize,
          jobName: this.jobName,
          routeName: this.routeName
        }).then(res => {
          if (res.code === Code.SUCCESS) {
            this.dataList = res.items.list;
            this.total = res.items.total;
          } else {
            this.$message({message: res.message, type: 'error'});
          }
        })
      },

      // 新增弹出框
      addNeWJobFun(){
        this.addJobDialog = true;

        this.agentList = [];
        this.binlogList = [];

        this.addFormIncrement.multiAgent = '';
        this.addFormIncrement.jobNameIncrement = '';
        this.addFormIncrement.routeIdIncrement = '';
        this.addFormIncrement.routeNameIncrement = '';
        this.addFormIncrement.binlogName = '';
        this.addFormIncrement.binlogPosition = 1;
        this.addFormIncrement.cronIncrement = '';
        this.addFormIncrement.showIncrementEnvMsg = '';

        this.addFormFull.cronFull = '';
        this.addFormFull.routeIdFull = '';
        this.addFormFull.jobNameFull = '';
        this.addFormFull.busStartTime = '';
        this.addFormFull.busEndTime = '';
        this.addFormFull.showFullEnvMsg = '';

        this.addFormColumn.routeIdColumn = '';
        this.addFormColumn.jobNameColumn = '';
        this.addFormColumn.cronColumn = '';
        this.addFormColumn.showColumnEnvMsg = '';

        this.isBinlogDisabled = true;
        this.isBusTimeDisabled = false;
        $(".timeDisabled").attr("disabled", this.isBusTimeDisabled);
        $(".timeDisabled").attr("disabled", this.isBinlogDisabled);

        // 加载数据
        this.loadAgent();
        this.loadBinlog();
        this.loadRouteListFun();
      },

      // 加载Agent
      loadAgent () {
        Job.queryAgents().then(res => {
          this.agentList = res.items;
        })
      },
      // 加载binlog
      loadBinlog () {
        let recent = true;
        Job.queryBinlog({
        }).then(res => {
          this.binlogList = res.items;
          // 默认加载最新日志
          let binglog = this.binlogList[0];
          this.addFormIncrement.binlogName = binglog.log_name;
          this.addFormIncrement.binlogPosition = binglog.file_size;
        })
      },
      // 加载路由
      loadRouteListFun(){
        Metadata.getRouteList({
          info: {}
        }).then(res => {
          if (res.code === Code.SUCCESS) {
            this.routeList = res.items.list;
          } else {
            this.$message({message: res.message, type: 'error', center: true});
          }
        })
      },
      // 加载同步表
      loadSourceTree(val){
        let flag = "1"
        let param = [{
          linkId: val
        }];

        Metadata.loadTree(flag, param).then(res => {
          this.tableList = res.items;
        })
      },

      // 路由选择函数
      changeRouteIncrementFun(val){
        this.routeList.find((item) => {
          if (item.routeId === val){
            this.addFormIncrement.routeIdIncrement = item.routeId;
            this.addFormIncrement.routeNameIncrement = item.routeName;
          }
        });
      },
      changeRouteFullFun(val){
        let obj = {};
        obj = this.routeList.find((item) => {
          return item.routeId === val;
        });

        if (obj != "" && obj != undefined) {
          Metadata.getDBRouteInfo({
            param: {
              routeId: obj.routeId
            }
          }).then(res => {
            if (res.code === Code.SUCCESS) {
              this.addFormFull.routeIdFull = res.items.routeId;
              this.addFormFull.routeNameFull = res.items.routeName;
              this.sourceId = res.items.sourceId;
              this.loadSourceTree(res.items.sourceId)
            } else {
              this.$message({message: res.message, type: 'error', center: true});
            }
          })
        }
      },
      changeRouteColumnFun(val){
        this.routeList.find((item) => {
            if (item.routeId === val){
                this.addFormColumn.routeIdColumn = item.routeId;
                this.addFormColumn.routeNameColumn = item.routeName;
            }
        });
      },

      // Binlog
      handleCheckBinlogChange(){
        this.isBinlogDisabled = !this.isBinlogDisabled;
        if (this.isBinlogDisabled){
          // 最新
          this.addFormIncrement.recentBinlog = '1';
          var binglog = this.binlogList[0];
          this.addFormIncrement.binlogName = binglog.log_name;
          this.addFormIncrement.binlogPosition = binglog.file_size;
          $(".binlogDisabled").attr("disabled", this.isBinlogDisabled);
        }else {
          // 自选
          this.addFormIncrement.recentBinlog = '0';
          this.addFormIncrement.binlogPosition = 1;
          $(".binlogDisabled").removeAttr("style");
          $(".binlogDisabled").removeAttr("disabled");
        }
      },

      // Agent
      handleCheckAgentChange() {
        var agents = this.$refs.agent_tree.getCheckedNodes();
        // 先清空，再赋值
        this.addFormIncrement.multiAgent = '';

        var length = agents.length;
        var j = length - 1;
        for (var i = 0; i < length; i++){
            var agent = agents[i];
            if (i == j){
              this.addFormIncrement.multiAgent += agent.agentId;
            }else {
              this.addFormIncrement.multiAgent += agent.agentId;
              this.addFormIncrement.multiAgent += ",";
            }
        }
      },

      // 环境检查
      checkEnvFun(formName, jobType){
          console.log(jobType);
        this.$refs[formName].validate((valid) => {
          if (valid){
            if (jobType == 'increment') {
              Job.checkEnvIncrement({
                routeId: this.addFormIncrement.routeIdIncrement,
                agent: this.addFormIncrement.multiAgent
              }).then(res => {
                if (res.code === Code.SUCCESS) {
                  this.addFormIncrement.showIncrementEnvMsg = res.items.message;
                } else {
                  this.$message({message: res.message, type: 'error', center: true});
                }
              })
            } else if (jobType == 'full') {
              alert("全量同步环境检查");
            } else if (jobType == 'column') {
              Job.checkEnvColumn({
                routeId: this.addFormColumn.routeIdColumn
              }).then(res => {
                if (res.code === Code.SUCCESS) {
                    console.log(res.items.message);
                  this.addFormColumn.showColumnEnvMsg = res.items.message;
                } else {
                  this.$message({message: res.message, type: 'error', center: true});
                }
              })
            }
          }else {
              return false;
          }
        });
      },

      // 添加
      insertJobFun(formName, jobType){
        this.addJobDialog = true;

        let param = {}
        if (jobType == 'increment') {
          param = {
            jobType: jobType,
            jobName: this.addFormIncrement.jobNameIncrement,
            routeId: this.addFormIncrement.routeIdIncrement,
            routeName: this.addFormIncrement.routeNameIncrement,
            agent: this.addFormIncrement.multiAgent,
            binlogName: this.addFormIncrement.binlogName,
            recentBinlog: this.addFormIncrement.recentBinlog,
            binlogPosition: this.addFormIncrement.binlogPosition,
            cron: this.addFormIncrement.cronIncrement
          }
        } else if (jobType == 'full') {
          param = {
            jobType: jobType,
            jobName: this.addFormFull.jobNameFull,
            routeId: this.addFormFull.routeIdFull,
            routeName: this.addFormFull.routeNameFull,
            cron: this.addFormFull.cronFull
          }
        } else if (jobType == 'column') {
          param = {
            jobType: jobType,
            jobName: this.addFormColumn.jobNameColumn,
            routeId: this.addFormColumn.routeIdColumn,
            routeName: this.addFormColumn.routeNameColumn,
            cron: this.addFormColumn.cronColumn
          }
        }

        this.$refs[formName].validate((valid) => {
          if (valid) {
            Job.insertJob(param).then(res => {
              if (res.code === Code.SUCCESS) {
                this.$message({message: Tips.INSERT_SUCCESS, type: 'success'});
                this.addJobDialog = false;
                this.searchFun();
              } else {
                this.$message({message: res.message, type: 'error'});
                return false;
              }
            }).catch(() => {
              this.$message({message: res.message, type: 'error'});
              this.addJobDialog = false;
            })
          } else {
            return false;
          }
        });
      },

      // 状态开关按钮
      handleCloseFun(){
        this.enableJobDialog = false;
      },
      changeEnableSwitchFun(index, row) {
        // 校验作业状态，运行中需要先暂停
        if (row.status == 'running') {
          this.$message({message: '请先暂停作业，再操作', type: 'error', center: true});
          this.searchFun();
        }else{
          // 提示
          this.enableJobDialog = true;
          this.jobId = row.jobId;
          this.enable = row.enable;
          if (row.enable == 0) {
            this.showMsg = '禁用后将无法正常调度，是否确认禁用？';
          } else {
            this.showMsg = '是否确认启用？';
          }
        }
      },
      closeDialogFun(){
        this.enableJobDialog = false;
        if (this.enable == 0) {
          this.enable == 1;
        } else {
          this.enable == 0;
        }
        this.searchFun();
      },
      // 更新状态
      updateStatusFun(){
        Job.updateJobEnable({
          jobId: this.jobId,
          enable: this.enable
        }).then(res => {
          this.enableJobDialog = false;
          if (res.code === Code.SUCCESS) {
            this.$message({message: Tips.UPDATE_SUCCESS, type: 'success', center: true});
            this.searchFun();
          } else {
            this.$message({message: res.message, type: 'error', center: true});
          }
        }).catch((e) => {
          this.$message({message: '更新异常', type: 'error', center: true});
          this.closeDialogFun();
        })
      },

      // 作业执行
      handleTrigger(row) {
        if (row.enable == 0) {
          this.$message({message: Tips.NOT_ALLOW_OPERATION, type: 'error', center: true});
        } else {
          Job.triggerJob(row.jobId, {}).then(res => {
            if (res.code === Code.SUCCESS) {
              this.$message({message: Tips.EXECUTE_SUCCESS, type: 'success', center: true});
              this.searchFun();
            } else {
              this.$message({message: res.message, type: 'error', center: true});
            }
          })
        }
      },
      // 暂停作业
      handlePause(row) {
        if (row.enable == 0) {
          this.$message({message: Tips.NOT_ALLOW_OPERATION, type: 'error', center: true});
        } else {
          Job.pauseJob(row.jobId, {}).then(res => {
            if (res.code === Code.SUCCESS) {
              this.$message({message: Tips.PAUSE_SUCCESS, type: 'success', center: true});
              this.searchFun();
            } else {
              this.$message({message: res.message, type: 'error', center: true});
            }
          })
        }
      },
      // 恢复作业
      handleResume(row) {
        if (row.enable == 0) {
          this.$message({message: Tips.NOT_ALLOW_OPERATION, type: 'error', center: true});
        } else {
          Job.resumeJob(row.jobId, {}).then(res => {
            if (res.code === Code.SUCCESS) {
              this.$message({message: Tips.RESUME_SUCCESS, type: 'success', center: true});
              this.searchFun();
            } else {
              this.$message({message: res.message, type: 'error', center: true});
            }
          })
        }
      },
      // 作业日志信息
      handleLog(row) {
        // 跳转到日志页面查看
        this.$router.push({path: '/job/schedulerLogs'});
        this.$store.state.jobId = row.jobId;
      },

      // 业务时间
      handleCheckBusTimeChange(){
        this.isBusTimeDisabled = !this.isBusTimeDisabled;
        if (this.isBusTimeDisabled){
          this.busStartTime = '';
          this.busEndTime = '';
          $(".timeDisabled").attr("disabled", this.isBusTimeDisabled);
        }else {
          $(".timeDisabled").removeAttr("disabled");
        }
      },
      handleSizeChange(pageSize){
        this.pageSize = pageSize;
        this.searchFun(0);
      },
      handleCurrentChange(page){
        this.page = page;
        this.searchFun(0);
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
      formatTimeFun(item){
        let str = '';
        if (item && item.createTime){
            str = this.formatTime(new Date(item.createTime), "yyyy-MM-dd hh:mm:ss");
        }
        return str;
      },
      formatBusStartTime(item){
        let str = '';
        if (item && item.busStartTime){
            str = this.formatTime(new Date(item.busStartTime), "yyyy-MM-dd hh:mm:ss");
        }
        return str;
      },
      formatBusEndTime(item){
        let str = '';
        if (item && item.busEndTime){
            str = this.formatTime(new Date(item.busEndTime), "yyyy-MM-dd hh:mm:ss");
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

<style lang="less" scoped>
  .el-aside {
    background-color: #fff;
    color: #333;
  }
  .el-main {
    background-color: #E9EEF3;
    color: #333;
  }
</style>
