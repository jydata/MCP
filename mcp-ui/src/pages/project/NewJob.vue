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
    <div class="container">
      <div class="tabs"
           style="margin-left: 14px">
        <div class="dcenter">
          <span class="shadow mt0 center"
                style="width:90px;height: 40px;line-height: 40px"
                v-show="incrementFlag"
                :style="bgIncrement"
                @click="showIncrement">Increment</span>
          <span class="shadow mt0 center"
                style="width:90px;height: 40px;line-height: 40px"
                v-show="fullFlag"
                :style="bgFull"
                @click="showFull">Full</span>
          <span class="shadow mt0 center"
                style="width:90px;height: 40px;line-height: 40px"
                v-show="metaFlag"
                :style="bgMetadata"
                @click="showMetadata">Metadata</span>
          <span class="shadow mt0 center"
                style="width:90px;height: 40px;line-height: 40px"
                v-show="ddlFlag"
                :style="bgDDL"
                @click="showDDL">Batch DDL</span>
        </div>
      </div>
      <div class="tabs-content"
           style="margin-top: -10px">
        <!--Increment-->
        <div class="increment"
             v-show="incrementTable">
          <el-form label-position="top"
                   class="shadow pd20"
                   style="padding-bottom: 20px"
                   :rules="rules"
                   ref="incrementData"
                   :model="incrementData">
            <el-form-item label="Job Name"
                          prop="jobName">
              <el-input maxlength=20
                        v-model="incrementData.jobName"
                        clearable></el-input>
            </el-form-item>
            <el-form-item label="Project">
              <el-select placeholder="Please select"
                         @focus="queryProject"
                         @change="changeIncrePro"
                         v-model="incrementData.projectName">
                <el-option v-for="item in projectList"
                           :label="item.projectName"
                           :value="item"
                           :key="item.projectId"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="Group">
              <el-input maxlength=20
                        v-model="incrementData.groupName"
                        style="width: 54%;"
                        clearable>
              </el-input>
              <el-dropdown trigger="click"
                           @command="changeIncreGroup">
                <span class="el-dropdown-link ml20">Choose Group<i class="el-icon-arrow-down el-icon--right"></i></span>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item v-for="item in groupList"
                                    :key="item.groupId"
                                    :command="item">{{item.groupName}}</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </el-form-item>
            <div class="border"></div>
            <el-form-item label="Routing">
              <el-cascader placeholder="Please select"
                           :options="routeList"
                           :props="routeProps"
                           style="width: 65%;"
                           v-model="incrementData.route"
                           @change="changeIncrement">
              </el-cascader>
            </el-form-item>
            <div class="border"
                 v-if="isBinlog"></div>
            <el-form-item label="Binlog File Name"
                          v-if="isBinlog">
              <el-select placeholder="Please select"
                         v-model="incrementData.binlogName"
                         @focus="queryBinlog"
                         @change="changeBinlog"
                         :disabled="isDisabled">
                <el-option v-for="item in binlogList"
                           :label="item.log_name"
                           :value="item.file_size"
                           :key="item.log_name">
                </el-option>
              </el-select>
              <el-checkbox style="margin-left: 60px"
                           label="Recent Binlog File"
                           v-model="isChecked"
                           @change="changeRencent"></el-checkbox>
            </el-form-item>
            <el-form-item label="Binlog Position"
                          prop="startPosition"
                          v-if="isBinlog">
              <el-input v-model="incrementData.startPosition"
                        style="width: 32%"
                        :disabled="isDisabled"
                        clearable></el-input>
              <span class="textgray"> - </span>
              <el-input style="width: 32%"
                        v-model="incrementData.binlogPosition"
                        :disabled="true"
                        clearable></el-input>
            </el-form-item>
            <job-increment-table 
              ref="ref-increment-job-table"
              :incrementData="incrementData"
              :incrementDbTable="incrementDbTable"
              :incrementDbList="incrementDbList"
              :incrementDbListCopy="incrementDbListCopy"
              :dbOld="incrementDbOld"
              @emitJobIncrementTabel="emitJobIncrementTabel"
              ></job-increment-table >
            <el-form-item class="button">
              <el-button @click="checkIncreEnv('incrementData')">Check Env</el-button>
              <el-button @click="runJob('incrementData')"
                         :disabled="increDisabled">Run Job Now</el-button>
              <el-button @click="saveJob('incrementData')"
                         :disabled="increDisabled">Save</el-button>
            </el-form-item>
          </el-form>
        </div>
        <!--Full-->
        <div class="full"
             v-show="bgFull">
          <!-- full tree 暂时隐藏 -->
          <div class="left shadow" v-if="false">
            <el-input class="search-input"
                      prefix-icon="el-icon-search"
                      placeholder="Search schema or table"
                      v-model="filterText"></el-input>
            <el-tree empty-text="Please select route to load the source information"
                     class="pd10 mcp-rollbar"
                     :style="{maxHeight: treeHeight + 'px'}"
                     :data="fullList"
                     :props="defaultProps"
                     ref="tree"
                     :filter-node-method="filterNode"
                     @node-click="getFullNode">
            </el-tree>
          </div>
          <div class="right shadow pd20" style="width: 100%">
            <el-form label-position="top"
                     style="padding-bottom: 20px"
                     :rules="rules"
                     ref="fullData"
                     :model="fullData">
              <el-form-item label="Job Name"
                            prop="jobName">
                <el-input maxlength=20
                          v-model="fullData.jobName"
                          clearable></el-input>
              </el-form-item>
              <el-form-item label="Project">
                <el-select placeholder="Please select"
                           @focus="queryProject"
                           @change="changeFullPro"
                           v-model="fullData.projectName">
                  <el-option v-for="item in projectList"
                             :label="item.projectName"
                             :value="item"
                             :key="item.projectId"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="Group">
                <el-input maxlength=20
                          v-model="fullData.groupName"
                          style="width: 51%;"
                          clearable>
                </el-input>
                <el-dropdown trigger="click"
                             @command="changeFullGroup">
                  <span class="el-dropdown-link ml20">Choose Group<i class="el-icon-arrow-down el-icon--right"></i></span>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item v-for="item in groupList"
                                      :key="item.groupId"
                                      :command="item">{{item.groupName}}</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </el-form-item>
              <div class="border"></div>
              <el-form-item label="Routing">
                <el-cascader placeholder="Please select"
                             :options="routeList"
                             :props="routeProps"
                             style="width: 65%;"
                             v-model="fullData.route"
                             @change="changeFull">
                </el-cascader>
              </el-form-item>
              <div class="border"></div>
              <!-- full tree 暂时去掉 -->
              <el-form-item v-if="false" label="Synchronize Duration">
                <el-date-picker size="small"
                                v-model="fullData.syncDuration"
                                type="daterange"
                                unlink-panels
                                start-placeholder="Start Date"
                                end-placeholder="End Date">
                </el-date-picker>
              </el-form-item>
              <job-full-table 
                ref="ref-full-job-table"
                :fullData="fullData"
                :fullDbTable="fullDbTable"
                :fullDbList="fullDbList"
                :fullDbOld="fullDbOld"
                :fullTableOld="fullTableOld"
                @emitJobFullTabel="emitJobFullTabel"
                ></job-full-table>
              <el-form-item class="button">
                <el-button @click="checkFullEnv('fullData')">Check Env</el-button>
                <el-button @click="runJob('fullData')"
                           :disabled="fullDisabled">Run Job Now</el-button>
                <el-button @click="saveJob('fullData')"
                           :disabled="fullDisabled">Save</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
        <!--Metadata-->
        <div class="metadata"
             v-show="metadataTable">
          <div class="left shadow">
            <el-input class="search-input"
                      prefix-icon="el-icon-search"
                      placeholder="Search schema or table"
                      v-model="filterText1"></el-input>
            <el-tree empty-text="Please select route to load the source information"
                     class="pd10 mcp-rollbar"
                     :style="{maxHeight: treeHeight + 'px'}"
                     :data="metadataList"
                     :props="defaultProps"
                     ref="tree1"
                     show-checkbox
                     node-key="id"
                     @check="treeMetadataCheck"
                     :default-checked-keys="defaultSelect"
                     :default-expanded-keys="checkedId"
                     :filter-node-method="filterNode1"
                     @node-click="getMetaNode">
            </el-tree>
          </div>
          <div class="right shadow pd20">
            <el-form label-position="top"
                     style="padding-bottom: 20px"
                     :rules="rules"
                     ref="metaData"
                     :model="metaData">
              <el-form-item label="Job Name"
                            prop="jobName">
                <el-input maxlength=20
                          v-model="metaData.jobName"
                          clearable></el-input>
              </el-form-item>
              <el-form-item label="Project">
                <el-select placeholder="Please select"
                           @focus="queryProject"
                           @change="changeMetaPro"
                           v-model="metaData.projectName">
                  <el-option v-for="item in projectList"
                             :label="item.projectName"
                             :value="item"
                             :key="item.projectId"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="Group">
                <el-input maxlength=20
                          v-model="metaData.groupName"
                          style="width: 51%;"
                          clearable>
                </el-input>
                <el-dropdown trigger="click"
                             @command="changeMetaGroup">
                  <span class="el-dropdown-link ml20">Choose Group<i class="el-icon-arrow-down el-icon--right"></i></span>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item v-for="item in groupList"
                                      :key="item.groupId"
                                      :command="item">{{item.groupName}}</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </el-form-item>
              <div class="border"></div>
              <el-form-item label="Routing"
                            v-show="isMetadata">
                <el-cascader placeholder="Please select"
                             :options="routeList"
                             :props="routeProps"
                             style="width: 65%;"
                             v-model="metaData.route"
                             @change="changeMeta">
                </el-cascader>
              </el-form-item>
              <div class="border"></div>
              <el-form-item label="Cron"
                            prop="cron"
                            v-if="isMetadata">
                <el-input v-model="metaData.cron"
                          clearable></el-input>
                <el-tooltip placement="left-start"
                            class="ml20">
                  <div slot="content">
                    Cron表达式提示：<br />
                    秒（0–59）<br />
                    分钟（0–59）<br />
                    小时（0–23）<br />
                    月份中的日期（1–31）<br />
                    月份（1–12或JAN–DEC）<br />
                    星期中的日期（1–7或SUN–SAT）<br />
                    年份（1970–2099）<br />
                    <br />
                    特殊字符：<br />
                    * ("所有值") - 用来选择一个字段中的所有值. <br /><br />
                    ? ("没有具体的值") - "?"就是来跳过一个的作用。<br /><br />
                    - - 用于指定范围。<br /><br />
                    , - 用于指定额外的值。<br /><br />
                    / - 用来指定增量。 <br /><br />
                    L ("last") - 2个字段中可以使用它，并且意义不同。<br />
                    例如，在“月”字段中使用它，表示，这个月的最后一天。<br />
                    如果在“day-of-week”字段中使用它，表示"7"或者"SAT"，也就是周六。<br /><br />
                    W ("weekday") - 用于指定工作日（周一至周五）最近的某一天。<br />
                    # - 用于指定月份的第几天。<br />
                  </div>
                  <el-button icon="el-icon-warning"
                             type="primary"
                             size="mini"
                             circle></el-button>
                </el-tooltip>
                <el-tooltip placement="right-start">
                  <div slot="content">
                    每隔10秒执行一次：*/10 * * * * ?<br />
                    每隔1分钟执行一次：0 */1 * * * ?<br />
                    每天22点执行一次：0 0 22 * * ?<br />
                    每天凌晨3点执行一次：0 0 3 * * ?<br />
                    每月1号凌晨3点执行一次：0 0 3 1 * ?<br />
                    每月最后一天22点执行一次：0 0 22 L * ?<br />
                    每周星期天凌晨3点实行一次：0 0 3 ? * L<br />
                    在20分、30分、40分执行一次：0 20,30,40 * * * ?<br />
                    每天0点、10点、16点、22点都执行一次：0 0 0,10,16,22 * * ?
                  </div>
                  <el-button icon="el-icon-question"
                             type="primary"
                             size="mini"
                             circle></el-button>
                </el-tooltip>
              </el-form-item>
              <el-form-item class="button">
                <el-button @click="checkMetaEnv('metaData')">Check Env</el-button>
                <el-button @click="runJob('metaData')"
                           :disabled="metaDisabled">Run Job Now</el-button>
                <el-button @click="saveJob('metaData')"
                           :disabled="metaDisabled">Save</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
        <!--DDL-->
        <div class="ddl"
             v-show="ddlTable">
          <el-form label-position="top"
                   class="shadow pd20"
                   style="padding-bottom: 20px"
                   :rules="rules"
                   ref="ddlData"
                   :model="ddlData">
            <el-form-item label="Job Name"
                          prop="jobName">
              <el-input maxlength=20
                        v-model="ddlData.jobName"
                        clearable></el-input>
            </el-form-item>
            <el-form-item label="Project">
              <el-select placeholder="Please select"
                         @focus="queryProject"
                         @change="changeDDLPro"
                         v-model="ddlData.projectName">
                <el-option v-for="item in projectList"
                           :label="item.projectName"
                           :value="item"
                           :key="item.projectId"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="Group">
              <el-input v-model="ddlData.groupName"
                        style="width: 51%;"
                        clearable>
              </el-input>
              <el-dropdown trigger="click"
                           @command="changeDDLGroup">
                <span class="el-dropdown-link ml20">Choose Group<i class="el-icon-arrow-down el-icon--right"></i></span>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item v-for="item in groupList"
                                    :key="item.groupId"
                                    :command="item">{{item.groupName}}</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </el-form-item>
            <div class="border"></div>
            <el-form-item class="gray">
              <ul class="form">
                <li>
                  <span class="label">Routing Name： </span>
                  <span class="deepblue"
                        v-text="ddlData.routeName"></span>
                </li>
                <li>
                  <span class="label">Source Name： </span>
                  <span class="deepblue"
                        v-text="ddlData.sourceName"></span>
                </li>
                <li>
                  <span class="label">Database： </span>
                  <template slot-scope>
                    <el-select placeholder="Please select"
                               class="deepblue"
                               v-model="ddlData.database"
                               @focus="getDatabase"
                               @change="changeValue"
                               filterable
                               multiple
                               collapse-tags>
                      <el-option v-for="item in DBList"
                                 :key="item"
                                 :label="item"
                                 :value="item"></el-option>
                    </el-select>
                  </template>
                </li>
                <li>
                  <span class="label">Target Name： </span>
                  <span class="deepblue"
                        v-text="ddlData.targetName"></span>
                </li>
                <li>
                  <span class="label">Target Schema： </span>
                  <span class="deepblue"
                        v-text="ddlData.schemaName"></span>
                </li>
                <li>
                  <span class="label">Rule Name： </span>
                  <template slot-scope>
                    <el-select placeholder="Please select"
                               class="deepblue"
                               v-model="ddlData.ruleName"
                               @focus="getRules"
                               @change="changeRules"
                               filterable>
                      <el-option v-for="item in ruleList"
                                 :key="item.ruleId"
                                 :label="item.ruleName"
                                 :value="item"></el-option>
                    </el-select>
                  </template>
                </li>
              </ul>
            </el-form-item>
            <el-form-item class="button">
              <el-button :loading="load4ddl.pass"
                         @click="checkEnvDDL('ddlData')">Check Env</el-button>
              <el-button @click="runJob('ddlData')"
                         :loading="load4ddl.run"
                         :disabled="ddlDisabled">Run Job Now</el-button>
              <el-button @click="saveJob('ddlData')"
                         :loading="load4ddl.save"
                         :disabled="ddlDisabled">Save</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import * as Project from '@api/Project';
import * as Meta from '@api/Meta';
import * as Job from '@api/Job';
import * as Code from '@config/code';
import * as Tips from '@config/tips';
/*const selectMulx=()=>import('components/select-mulx')*/
import jobIncrementTable from './components/jobIncrementTable'
import jobFullTable from './components/jobFullTable'

export default {
  name: "NewJob",
  components: {/*selectMulx*/ jobIncrementTable, jobFullTable },
  data () {
    let reg = /^[a-zA-Z]+[\w]*$/;
    let checkName = (rule, value, callback) => {
      if (value === '' || value.trim().length <= 0) {
        return callback(new Error('Job Name is required'));
      } else if (!reg.test(value)) {
        callback(new Error('Only numbers,letters and underline are allowed, and letters must be in front'));
      } else {
        callback();
      }
    };
    let checkGroup = (rule, value, callback) => {
      if (value === '' || value.trim().length <= 0) {
        return callback(new Error('Job Name is required'));
      } else if (!reg.test(value)) {
        callback(new Error('Only numbers,letters and underline are allowed, and letters must be in front'));
      } else {
        callback();
      }
    };
    let checkPosition = (rule, value, callback) => {
      // isChecked = true 时不校验
      if (this.isChecked) return callback();
      if (value >= this.incrementData.binlogPosition) {
        return callback(new Error('The value should be less than  ' + this.incrementData.binlogPosition));
      } else if (!Number(value)) {
        return callback(new Error('The value must be number'));
      } else {
        callback();
      }
    };
    let cronReg = /^((?:[1-5]?\d(\,(?:[1-5]?\d))*|(\*|[0-9])\/(?:[1-5]?\d)|(?:[1-5]?\d)\-(?:[1-5]?\d)|\*)\s){2}((2[0-3]|[0-1]?\d)(\,(2[0-3]|[0-1]?\d))*|(\*|[0-9])\/(2[0-3]|[0-1]?\d)|(2[0-3]|[0-1]?\d)\-(2[0-3]|[0-1]?\d)|\*)\s((3[0-1]|[1-2]\d|[0-9])(\,(3[0-1]|[0-2]?\d))*|(\*|[0-9])\/(3[0-1]|[1-2]\d|[0-9])|(3[0-1]|[1-2]\d|[0-9])\-(3[0-1]|[1-2]\d|[0-9])|[LW]|(3[0-1]|[1-2]\d|[0-9])[W]|\*|\?)\s(([0-9]|1[0-2])(\,([0-9]|1[0-2]))*|(\*|[0-9])\/([0-9]|1[0-2])|([0-9]|1[0-2])\-([0-9]|1[0-2])|\*)\s([1-7](\,[1-7])*|(\*|[1-7])\/[1-7]|[1-7]\-[1-7]|\*|([L]|[1-7][L])|[1-7]\#[1-5]|\?)(\s([1-2][0-9]{3}(\,[1-2][0-9]{3})|[1-2][0-9]{3}\-[1-2][0-9]{3}|\*))?$/;
    let checkCron = (rule, value, callback) => {
      if (value === '' || value.trim().length <= 0) {
        return callback(new Error('Cron is required'));
      } else if (!cronReg.test(value)) {
        callback(new Error('Cron format is incorrect'));
      } else {
        callback();
      }
    };
    return {
      // tree height
      treeHeight: document.body.clientHeight - 275,
      // full table list
      fullDbTable: [],
      fullDbList: [],
      fullDbOld: '',
      fullTableOld: '',
      
      // increment table list
      incrementDbTable: [],
      incrementDbList: [],
      incrementDbListCopy: [],
      incrementDbOld: '',

      mulxOptions: {
        title: 'Groups',
        a: {
          content: 'New Group',
          href: '/project/NewProject'
        },
        labelId: 'groupId',
        labelName: 'groupName',
      },
      title: { from: { name: '', url: '' }, to: { name: '', ur: '' } },
      bgIncrement: 'background-color:#2c6daf;color:#fff',
      bgFull: '',
      bgMetadata: '',
      bgDDL: '',
      incrementTable: true,
      fullTable: '',
      metadataTable: '',
      ddlTable: '',
      incrementFlag: true,
      fullFlag: true,
      metaFlag: true,
      ddlFlag: true,
      filterText: '',
      filterText1: '',
      checkedId: '',
      defaultSelect: [], // metaData tree default select checkbox

      increDisabled: true,
      fullDisabled: true,
      metaDisabled: true,
      ddlDisabled: true,
      isDisabled: '',
      isBinlog: false,
      isDisplay: '',
      isChecked: true,
      isMetadata: true,
      isCascader: false,

      increGroup: '',
      fullGroup: '',
      metaGroup: '',
      ddlGroup: '',

      incrementData: {
        jobId: null,
        jobName: '',
        groupId: '',
        groupName: '',
        projectName: '',
        projectId: '',
        route: [],
        binlogName: '',
        binlogPosition: '',
        startPosition: 1,
        agent: '',
      },
      fullData: {
        jobId: null,
        jobName: '',
        groupId: '',
        groupName: '',
        projectId: '',
        projectName: '',
        route: [],
        syncDuration: '',
        agent: '',
        srcDb: null,
        srcTable: null,
        schemaId: '',
      },
      metaData: {
        jobId: null,
        jobName: '',
        groupId: '',
        groupName: '',
        projectName: '',
        route: [],
        cron: '',
        sourceId: '',
        sourceName: '',
        projectId: '',
        agent: '',
        srcDb: null,
        srcTable: null,
      },
      ddlData: {
        jobId: null,
        jobName: '',
        groupId: '',
        groupName: '',
        projectId: '',
        projectName: '',
        database: '',
        rule: '',
        routeName: '',
        sourceId: '',
        sourceName: '',
        targetId: '',
        targetName: '',
        schemaId: '',
        schemaName: '',
        ruleName: '',
        agent: '',
      },

      defaultProps: {
        children: 'children',
        value: 'id',
        label: 'label',
      },
      routeProps: {
        children: 'children',
        value: 'id',
        label: 'label',
      },
      routeList: [],
      binlogList: [],
      groupList: [],
      projectList: [],
      fullList: [],
      metadataList: [],
      schemaList: [],

      rules: {
        jobName: [
          { required: false, validator: checkName, trigger: 'blur' },
        ],
        startPosition: [
          { required: false, validator: checkPosition, trigger: 'blur' },
        ],
        group: [
          { required: false, validator: checkGroup, trigger: 'blur' },
        ],
        cron: [
          { required: false, validator: checkCron, trigger: 'blur' }
        ]
      },
      DBList: [],
      ruleList: [],
      load4ddl: {
        pass: false,
        run: false,
        save: false
      }
    }
  },
  created () {
    let query = this.$route.query;
    if (query.schemaList) query.schemaList = JSON.parse(query.schemaList)
    if (query.origin === 'project') {
      this.ddlFlag = false
      this.incrementTable = true;
      this.fullTable = false;
      this.metadataTable = false;
      this.ddlTable = false;
      this.title = {
        from: { name: 'Project List', url: '/project/ProjectList' },
        to: { name: 'New Job' }
      };
      this.incrementData.projectName = query.projectName;
      this.fullData.projectName = query.projectName;
      this.metaData.projectName = query.projectName;
      let a = query.projectId;
      this.incrementData.projectId = query.projectId;
      this.fullData.projectId = query.projectId;
      this.metaData.projectId = query.projectId;
      this.queryGroup(a);
      this.incrementData.agent = query.projectAgent;
      this.fullData.agent = query.projectAgent;
      this.metaData.agent = query.projectAgent;
      this.queryRoute()
    } else if (query.origin === 'job') {
      this.title = {
        from: { name: 'Jobs Definition', url: '/project/ProjectList' },
        to: { name: 'Edit Job' }
      };
      if (query.jobType === 'increment') {
        this.showIncrement();
        this.incrementData = {
          jobId: query.jobId,
          jobName: query.jobName,
          projectId: query.projectId,
          projectName: query.projectName,
          groupId: query.groupId,
          groupName: query.groupName,
          agent: query.agent,
          routeId: query.routeId,
          route: [query.routeId, query.schemaId],
          binlogName: query.binlogName,
          startPosition: query.binlogPosition,
          schemaId: query.schemaId
        };
        this.isBinlog = true;
        this.queryBinlog();
        this.binlogList = [];
        query.recentBinlog == '1' ? this.isChecked = true : this.isChecked = false;
        query.recentBinlog == '1' ? this.isDisabled = true : this.isDisabled = false;
        // this.incrementData.binlogName = query.binlogName;
        // this.incrementData.startPosition = query.binlogPosition
        // table list db
        // this.incrementDbList = this.deepClone(JSON.parse(query.dbTableList))
        this.incrementDbListCopy = this.deepClone(JSON.parse(query.dbTableList))
        let checkAry = JSON.parse(query.checkIncreList)
        let checkCount = Object.keys(checkAry) ? Object.keys(checkAry).length - 1 : -1
        let keyAry = []
        for (let key in checkAry) {
          // edit job 时赋值select incrementTableList
          let child = []
          for (let i in this.incrementDbListCopy) {
            let db = this.incrementDbListCopy[i]
            if (db.id === key) child = db.children
          }
          // db key select
          keyAry.push(key)

          let incrementTable = {
            id: checkCount--,
            dbValue: key,
            tableValue: checkAry[key],
            incrementTableList: child
          }
          this.incrementDbTable.push(incrementTable)
        }
        // edit job 时赋值db incrementDbList 即非选中的数组
        this.incrementDbListCopy.forEach(row => {
          if (keyAry.indexOf(row.id) === -1) this.incrementDbList.push(row)
        })

      }
      if (query.jobType === 'full') {
        this.showFull();
        this.fullData = {
          jobId: query.jobId,
          jobName: query.jobName,
          projectId: query.projectId,
          projectName: query.projectName,
          groupId: query.groupId,
          groupName: query.groupName,
          routeId: query.routeId,
          schemaId: query.schemaId,
          agent: query.agent,
          syncDuration: [query.busStartTime, query.busEndTime],
          route: [query.routeId, query.schemaId]
        };
        this.fullDbList = this.deepClone(JSON.parse(query.dbTableList))
        this.fullDbTable = []
        let checkCount = JSON.parse(query.checkFullList) ? JSON.parse(query.checkFullList).length - 1 : -1
        JSON.parse(query.checkFullList).forEach((items, i) => {
          let dbRow = this.fullDbList.find(function (db) { return db.label === items.dbName })
          let tableRow = (dbRow.children).find(function (table) { return table.label === items.tableName })
          let fullTable = {
            pid: items.id,
            id: checkCount--,
            dbValue: items.dbName, // id 
            tableValue: items.tableName, // label
            columnValue: items.columnName, // label
            syncDuration: [items.beginTime, items.endTime],
            fullTableList: dbRow.children || [],
            fullColumnList: tableRow.children || [],
          }
          this.fullDbTable.push(fullTable)
        })
        // this.queryFullTree()
      }
      if (query.jobType === 'metadata') {
        this.showMetadata();
        this.metaData = {
          jobId: query.jobId,
          jobName: query.jobName,
          projectName: query.projectName,
          projectId: query.projectId,
          groupId: query.groupId,
          groupName: query.groupName,
          cron: query.cron,
          routeId: query.routeId,
          route: [query.routeId, query.schemaId]
        };
        this.isMetadata = !(query.routeId == 0);
        this.metaData.sourceId = query.sourceId;
        this.metaData.sourceName = query.sourceName;
        // metadata tree data
        this.metadataList.push(query.schemaList)
        // metadata tree 默认选中
        this.defaultSelect = query.defaultSelect
        // metadata tree 默认展开
        if (this.defaultSelect && this.defaultSelect.length > 0) {
          this.checkedId = []
          this.checkedId.push(this.defaultSelect[0])
        }
      }
      if (query.jobType === 'ddl') {
        this.showDDL();
        this.ddlData = {
          jobId: query.jobId,
          jobName: query.jobName,
          projectId: query.projectId,
          projectName: query.projectName,
          groupId: query.groupId,
          groupName: query.groupName,
          agent: query.agent,
          targetId: query.targetId,
          schemaId: query.schemaId,
          sourceId: query.sourceId,
          sourceName: query.sourceName,
          targetName: query.targetName,
          routeId: query.routeId,
          routeName: query.routeName,
          schemaName: query.schemaName,
          ruleId: query.ruleId,
          ruleName: query.ruleName,
          database: query.srcDb.split('#'),
          route: [query.routeId, query.schemaId]
        };
      }
      let a = query.projectId;
      this.queryGroup(a);
      this.queryRoute()
    } else if (query.origin === 'metadata') {
      this.title = {
        from: { name: 'DS Synchronize Source Metadata', url: '/meta/DSConnections' },
        to: { name: 'New Job' }
      };
      this.incrementTable = false;
      this.fullTable = false;
      this.ddlTable = false;
      this.metadataTable = true;
      this.incrementFlag = false;
      this.fullFlag = false;
      this.metaFlag = true;
      this.ddlFlag = false;
      this.isMetadata = query.jobType == 'metadata' ? true : false;
      this.bgMetadata = 'background-color:#2c6daf;color:#fff';
      // metadata tree data
      this.metadataList = query.schemaList;
      // metadata tree 默认展开
      this.checkedId = query.checkedId;
      // metadata tree 默认选中
      this.defaultSelect = query.defaultSelect
      this.metaData.sourceId = this.metadataList[0].id
      this.metaData.sourceName = this.metadataList[0].label
      // metadata tree 检查isSelect
      this.handleTreeMetadataDbTable(query.schemaList)
    } else {
      this.title = {
        from: { name: 'Generate Target DDL', url: '/meta/GenerateTargetDDL' },
        to: { name: 'New Job' }
      };
      this.incrementTable = false;
      this.fullTable = false;
      this.metadataTable = false;
      this.ddlTable = true;
      this.incrementFlag = false;
      this.fullFlag = false;
      this.metaFlag = false;
      this.ddlFlag = true;
      this.bgDDL = 'background-color:#2c6daf;color:#fff';
      this.ddlData = {
        jobId: null,
        jobName: '',
        routeId: query.routeId,
        routeName: query.routeName,
        sourceId: query.sourceId,
        sourceName: query.sourceName,
        targetId: query.targetId,
        targetName: query.targetName,
        schemaId: query.schemaId,
        schemaName: query.targetSchema,
        database: query.database || '',
        ruleId: query.ruleId,
        ruleName: query.ruleName || '',
        groupName: '',
        projectName: ''
      };
    };
  },
  watch: {
    filterText (val) {
      this.$refs.tree.filter(val);
    },
    filterText1 (val) {
      this.$refs.tree1.filter(val);
    },
    'incrementData.route': function (newVal, oldVal) {
      // 路由数组变化 情况table
      if (newVal && oldVal.length > 0 && oldVal && oldVal.length > 0 && (newVal[0] !== oldVal[0] || newVal[1] !== oldVal[1])) {
        if (this.incrementFlag) this.incrementDbTable = []
        if (this.fullFlag) {}
      }
    },
    'fullData.route': function (newVal, oldVal) {
      // 路由数组变化 情况table
      if (newVal && oldVal.length > 0 && oldVal && oldVal.length > 0 && (newVal[0] !== oldVal[0] || newVal[1] !== oldVal[1])) {
        if (this.fullFlag) this.fullDbTable = []
      }
    },
  },

  mounted () {
    let query = this.$route.query;
    if (query.origin === 'job' && query.jobType === 'metadata') {
      this.handleTreeSelectData(this.metadataList, this.defaultSelect)
    }
  },

  methods: {
    emitJobIncrementTabel (list1, list2, list3, val1) {
      this.incrementDbTable = list1
      this.incrementDbList = list2
      this.incrementDbListCopy = list3
      this.incrementDbOld = val1
    },
    emitJobFullTabel (list1, list2, val1, val2) {
      this.fullDbTable = list1
      this.fullDbList = list2
      this.fullDbOld = val1
      this.fullTableOld = val2
    },
    // metadata tree 检查isSelect 即赋值srcDb: this.metaData.srcDb, srcTable: this.metaData.srcTable
    // 全选：srcDb = null;    srcTable = null;
    // 其他：srcDb：db1,db2;   srcTable：db1,t1,t2|db2,t3,t4
    handleTreeMetadataDbTable (list) {
      let srcDb = null
      let srcTable = null
      list.forEach((items1) => {
        // 非全选
        if (!items1.isSelect) {
          // 初始化为空串 用于下面拼接字符串
          srcDb = ''
          srcTable = ''
          items1.children.forEach((items2) => {
            // 第二级isSelect选中
            if (items2.isSelect) {
              srcDb = srcDb !== '' ? (srcDb + ',' + items2.label) : items2.label
              srcTable = srcTable !== '' ? (srcTable + '|' + items2.label) : items2.label
              // 已拼接srcDb 然后拼接srcTable
              items2.children.forEach((items3) => {
                if (items3.isSelect) srcTable = srcTable + ',' + items3.label
              })
            } else {
              // 第二级isSelect未选中 循环查看第三级isSelect是否有选中
              let flagIsSelect = false
              for (let i = 0; i < items2.children.length; i++) {
                const items3 = items2.children[i];
                // 第三级isSelect有选中 拼接srcDb
                if (items3.isSelect) {
                  srcDb = srcDb !== '' ? (srcDb + ',' + items2.label) : items2.label
                  srcTable = srcTable !== '' ? (srcTable + '|' + items2.label) : items2.label
                  flagIsSelect = true
                  break
                }
              }
              // 第三级isSelect有选中 已拼接srcDb 然后拼接srcTable
              if (flagIsSelect) {
                items2.children.forEach((items3) => {
                  if (items3.isSelect)  srcTable = srcTable + ',' + items3.label
                })
              }
            }
          }) 
        }
      })
      this.metaData.srcDb = srcDb;
      this.metaData.srcTable = srcTable;
    },

    //过滤关键字
    filterNode (value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
    },
    filterNode1 (value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
    },

    changeValue (arr) {
      let len = arr.filter(item => item === 'All').length
      if (arr[0] === 'All') {
        arr.splice(1, arr.length - 1)
      } else if (arr[0] !== 'All' && len > 0) {
        let index = arr.indexOf('All')
        arr.splice(index, 1)
      }
    },

    //获取rule信息
    getRules () {
      Meta.getRules({}).then(res => {
        if (res.code === Code.SUCCESS) {
          this.ruleList = res.items;
        } else {
          this.$message({ message: res.message, type: 'error' });
        }
      }).catch(() => {
      })
    },
    //获取DB信息
    getDatabase () {
      this.isDisabled = true;
      Meta.getDatabase({ sourceId: this.ddlData.sourceId }).then(res => {
        if (res.code === Code.SUCCESS) {
          this.DBList = res.items;
        } else {
          this.$message({ message: res.message, type: 'error' });
        }
      }).catch(() => {
      })
    },

    //Routing
    queryRoute () {
      this.isCascader = true;
      Meta.queryRoute().then(res => {
        if (res.code === Code.SUCCESS) {
          this.routeList = res.items;
        } else {
          this.$message({ message: res.message, type: 'error' });
        }
      }).catch(() => {
      })
    },
    //改变路由值时的方法
    changeIncrement (item) {
      this.increDisabled = true;
      this.incrementData.routeId = item[0];
      this.incrementData.schemaId = item[1];
      this.queryBinlog();
      this.queryIncrementDbTable()
      this.binlogList = [];
      this.isBinlog = true;
    },
    changeFull (item) {
      this.fullDisabled = true
      this.fullData.routeId = item[0];
      this.fullData.schemaId = item[1];
      // this.queryFullTree();
      this.queryFullDbTable()
      this.fullList = [];
    },
    changeMeta (item) {
      this.metaDisabled = true;
      this.metaData.routeId = item[0];
      this.metaData.schemaId = item[1]
      this.queryMetaTree();
      this.metadataList = [];
    },
    // 获取db list  select data
    queryIncrementDbTable () {
      let params = {
        routeId: this.incrementData.routeId,
        targetSchemaId: this.incrementData.schemaId
      }
      Job.queryDbTable(params).then(res => {
        if (res.code === Code.SUCCESS) {
          this.incrementDbList = this.deepClone(res.items)
          this.incrementDbListCopy = this.deepClone(res.items)
        }
      })
    },
    // 获取db list full select data
    queryFullDbTable () {
      let params = {
        routeId: this.fullData.routeId,
        targetSchemaId: this.fullData.schemaId
      }
      Job.queryFullDbTable(params).then(res => {
        if (res.code === Code.SUCCESS) {
          this.fullDbList = this.deepClone(res.items)
        }
      })
    },
    // 深拷贝地址
    deepClone (obj) {
      let objStr = JSON.stringify(obj)
      let objClone = JSON.parse(objStr)
      return objClone
    },

    //Project
    queryProject () {
      this.incrementData.groupName = '';
      this.fullData.groupName = '';
      this.metaData.groupName = '';
      this.ddlData.groupName = '';
      Project.queryProject({}).then(res => {
        if (res.code === Code.SUCCESS) {
          this.projectList = res.items;
        } else {
          this.$message({ message: res.message, type: 'error' });
        }
      }).catch(() => {
      })
    },
    changeIncrePro (item) {
      this.increDisabled = true;
      this.incrementData.projectName = item.projectName;
      this.incrementData.projectId = item.projectId;
      this.incrementData.agent = item.projectAgent;
      this.queryGroup(this.incrementData.projectId);
      this.groupList = [];
    },
    changeFullPro (item) {
      this.fullDisabled = true
      this.fullData.projectName = item.projectName;
      this.fullData.projectId = item.projectId;
      this.fullData.agent = item.projectAgent;
      this.queryGroup(this.fullData.projectId);
      this.groupList = [];
    },
    changeMetaPro (item) {
      this.metaDisabled = true;
      this.metaData.projectName = item.projectName;
      this.metaData.projectId = item.projectId;
      this.metaData.agent = item.projectAgent;
      this.queryGroup(this.metaData.projectId);
      this.groupList = [];
    },
    changeDDLPro (item) {
      this.ddlDisabled = true;
      this.ddlData.projectName = item.projectName;
      this.ddlData.projectId = item.projectId;
      this.ddlData.agent = item.projectAgent;
      this.queryGroup(this.ddlData.projectId);
      this.groupList = [];
    },

    //Group
    queryGroup (project) {
      this.increDisabled = true;
      this.fullDisabled = true;
      this.metaDisabled = true;
      this.ddlDisabled = true;
      Project.queryGroup({
        projectId: project
      }).then(res => {
        if (res.code === Code.SUCCESS) {
          this.groupList = res.items;
        } else {
          this.$message({ message: res.message, type: 'error' });
        }
      }).catch(() => {
      })
    },
    changeIncreGroup (item) {
      this.increGroup = item;
      this.incrementData.groupName = item.groupName;
    },
    changeFullGroup (item) {
      this.fullGroup = item;
      this.fullData.groupName = item.groupName;
    },
    changeMetaGroup (item) {
      this.metaGroup = item;
      this.metaData.groupName = item.groupName;
    },
    changeDDLGroup (item) {
      this.ddlGroup = item;
      this.ddlData.groupName = item.groupName;
    },

    //binlog
    queryBinlog () {
      Job.queryBinlog({
        routeId: this.incrementData.routeId
      }).then(res => {
        if (res.code === Code.SUCCESS) {
          this.binlogList = res.items;
          this.incrementData.binlogName = this.binlogList[0].log_name;
          this.incrementData.binlogPosition = this.binlogList[0].file_size;
        } else {
          this.$message({ message: res.message, type: 'error' });
        }
      }).catch(() => {
      })
    },
    changeBinlog (item) {
      this.increDisabled = true;
      this.incrementData.binlogPosition = item;
    },
    changeRencent (val) {
      this.increDisabled = true;
      this.isChecked = val;
      if (val) {
        this.isDisabled = true;
        this.incrementData.binlogName = this.binlogList[0].log_name;
        this.incrementData.binlogPosition = this.binlogList[0].file_size;
      } else {
        this.isDisabled = false;
      }
    },

    //加载树
    queryFullTree () {
      let params = {
        routeId: this.fullData.routeId
      }
      Job.queryTree(params).then(res => {
        if (res.code === Code.SUCCESS) {
          this.fullList.push(res.items)
          if (this.fullList[0].children == null) {
            this.fullList = []
          }
        } else {
          this.$message({ message: res.message, type: 'error' });
        }
      }).catch(() => {
      })
    },
    queryMetaTree () {
      this.metadataList = [];
      let params = {
        routeId: this.metaData.routeId
      }
      Job.queryDBTree(params).then(res => {
        if (res.code === Code.SUCCESS) {
          this.metadataList.push(res.items);
          if (this.metadataList.length <= 0) return
          this.metaData.sourceId = res.items.id;
          this.metaData.sourceName = res.items.label
          this.defaultSelect = []
          this.defaultSelect.push(res.items.id || '')
          this.$nextTick(() => {
             this.$refs.tree1.setCheckedKeys(this.defaultSelect);
          })
          this.handleTreeSelectData(this.metadataList)
        } else {
          this.$message({ message: res.message, type: 'error' });
        }
      }).catch(() => {
      })
    },
    // tree metadata check事件
    treeMetadataCheck (data, node) {
      // 第一级 只有一枝
      if (data.level === 1) {
        this.handleTreeCheck1()
      }
      // 第二级 有父一枝 有子枝
      if (data.level === 2) {
        this.handleTreeCheck2(data)
      }
      // 第三极 有父二枝 无子枝
      if (data.level === 3) {
        this.handleTreeCheck3(data)
      }
    },

    // tree checkbox点击时第一级
    handleTreeCheck1 () {
      this.metadataList.forEach((items1) => {
        let select = !items1.isSelect
        items1.isSelect = select
        // 第二级 所有枝isSelect同步
        items1.children.forEach((items2) => {
          items2.isSelect = select
          // 第三级 所有枝isSelect同步
          items2.children.forEach((items3) => {
            items3.isSelect = select
            })
        })
      })
    },

    // tree checkbox点击时第二级
    handleTreeCheck2 (data) {
      let countSel = 0 
      this.metadataList.forEach((items1) => {
        items1.children.forEach((items2, i2) => {
          // 查找当前第二级id
          if (items2.id === data.id){
            let select = !items2.isSelect
            items2.isSelect = select
            // 第三级 所有枝isSelect同步
            items2.children.forEach((items3) => {
              items3.isSelect = select
            })
          }
          // 检查第二级是否全选，修改父一枝的isSelect
          countSel = this.handleTreeCheckFaSelect(items2, items1,countSel, i2)
        })
      })
    },

    // tree checkbox点击时第二级
    handleTreeCheck3 (data) {
      let countSel2 = 0 
      let countSel3 = 0 

      this.metadataList.forEach((items1) => {
        items1.children.forEach((items2, i2) => { 
          // 查找当前第二级faId
          if (items2.id === data.faId){
            items2.children.forEach((items3, i3) => {
              // 查找当前第三级id
              if (items3.id === data.id){
                items3.isSelect = !items3.isSelect
              }
              countSel3 = this.handleTreeCheckFaSelect (items3, items2, countSel3, i3)
            })
          }
          countSel2 = this.handleTreeCheckFaSelect (items2, items1, countSel2, i2)
        })
      })
    },

    // tree检查父级是否全选，修改父枝的isSelect
    handleTreeCheckFaSelect (itemsCur, itemsFa, countSel, i){
      if (!itemsCur.isSelect) itemsFa.isSelect = false 
      else countSel++
      // 第二级循环到最后一个
      if (itemsFa.children && i === itemsFa.children.length -1 && countSel === itemsFa.children.length) {
        itemsFa.isSelect = true
      }
      return countSel
    },

    // 获取tree data 的label
    handleTreeSelectData(list, selectlist) {
      list.forEach((items) => {  
        const pushTreeSelectKeys = (row, rows) => {
          if (!selectlist) row.isSelect = true 
          else {
            row.isSelect = selectlist.includes(row.id)
          }
          row.level = (rows.level || 0) + 1
          row.faId = rows.id || rows.label
        }
        this.recursiveFn(items, pushTreeSelectKeys, list)
      });
    },
    // 递归函数
    recursiveFn(curItem, cb, faItem) {
      cb(curItem, faItem);
      const children = curItem.children;
      if (children && children.length > 0) {
        children.forEach(item => {
          this.recursiveFn(item, cb, curItem);
        });
      }
    },

    //点击树节点
    getMetaNode (data, node) {
      if (node.level === 3) {
        this.metaData.srcDb = node.parent.data.label;
        this.metaData.srcTable = node.data.label
      } else {
        this.metaData.srcDb = null;
        this.metaData.srcTable = null;
      }
    },
    getFullNode (data, node) {
      if (node.level === 3) {
        this.fullData.srcDb = node.parent.data.label;
        this.fullData.srcTable = node.data.label
      } else {
        this.fullData.srcDb = null;
        this.fullData.srcTable = null;
      }
    },

    //检查环境前参数赋值
    getIncreParams () {
      let recent = '', position = ''
      if (this.groupList.filter(item => item.groupName == this.incrementData.groupName).length == 0) {
        this.incrementData.groupId = 0
      } else if (this.groupList.filter(item => item.groupName == this.incrementData.groupName).length == 1) {
        let a = this.groupList.filter(item => item.groupName == this.incrementData.groupName);
        this.incrementData.groupId = a[0].groupId
      } else {
        this.incrementData.groupId = this.increGroup.groupId
      }
      if (this.isChecked) {
        recent = '1';
        position = this.incrementData.binlogPosition
      } else {
        recent = '0';
        position = this.incrementData.startPosition
      }
      return {
        jobType: "increment",
        jobId: this.incrementData.jobId,
        jobName: this.incrementData.jobName,
        agent: this.incrementData.agent,
        binlogName: this.incrementData.binlogName,
        binlogPosition: position,
        recentBinlog: recent,
        routeId: this.incrementData.routeId,
        targetSchemaId: this.incrementData.schemaId,
        groupId: this.incrementData.groupId,
        groupName: this.incrementData.groupName,
        projectId: this.incrementData.projectId,
        projectName: this.incrementData.projectName,
      }
    },
    getFullParams () {
      // let a = this.fullData.syncDuration[0], b = this.fullData.syncDuration[1];
      // let startTime = new Date(b).getTime() / 1000, endTime = new Date(a).getTime() / 1000;
      if (this.groupList.filter(item => item.groupName == this.fullData.groupName).length == 0) {
        this.fullData.groupId = 0
      } else if (this.groupList.filter(item => item.groupName == this.fullData.groupName).length == 1) {
        let a = this.groupList.filter(item => item.groupName == this.fullData.groupName);
        this.fullData.groupId = a[0].groupId
      } else {
        this.fullData.groupId = this.fullGroup.groupId
      }
      return {
        jobType: "full",
        jobId: this.fullData.jobId,
        jobName: this.fullData.jobName,
        agent: this.fullData.agent,
        groupId: this.fullData.groupId,
        groupName: this.fullData.groupName,
        projectId: this.fullData.projectId,
        projectName: this.fullData.projectName,
        routeId: this.fullData.routeId,
        targetSchemaId: this.fullData.schemaId,
        // sourceId: this.fullList[0].id,
        // sourceName: this.fullList[0].label,
        srcDb: this.fullData.srcDb,
        srcTable: this.fullData.srcTable,
        // busEndTime: startTime,
        // busStartTime: endTime,
      }
    },
    getMetaParams () {
      if (this.groupList.filter(item => item.groupName == this.metaData.groupName).length == 0) {
        this.metaData.groupId = 0
      } else if (this.groupList.filter(item => item.groupName == this.metaData.groupName).length == 1) {
        let a = this.groupList.filter(item => item.groupName == this.metaData.groupName);
        this.metaData.groupId = a[0].groupId
      } else {
        this.metaData.groupId = this.metaGroup.groupId
      }
      let params = {}
      // metadata tree 检查isSelect
      this.handleTreeMetadataDbTable(this.metadataList)
      if (this.$route.query.origin === 'metadata' || this.$route.query.routeId == 0) {
        params = {
          jobId: this.metaData.jobId,
          jobName: this.metaData.jobName,
          routeId: 0,
          jobType: "metadata",
          enable: 1,
          projectId: this.metaData.projectId,
          groupId: this.metaData.groupId,
          groupName: this.metaData.groupName,
          cron: this.metaData.cron,
          sourceId: this.metaData.sourceId,
          sourceName: this.metaData.sourceName,
          srcDb: this.metaData.srcDb,
          srcTable: this.metaData.srcTable
        }
      } else {
        params = {
          jobId: this.metaData.jobId,
          jobName: this.metaData.jobName,
          jobType: "metadata",
          enable: 1,
          projectId: this.metaData.projectId,
          groupId: this.metaData.groupId,
          groupName: this.metaData.groupName,
          cron: this.metaData.cron,
          sourceId: this.metadataList[0].id,
          sourceName: this.metadataList[0].label,
          routeId: this.metaData.routeId,
          targetSchemaId: this.metaData.schemaId,
          srcDb: this.metaData.srcDb,
          srcTable: this.metaData.srcTable
        }
      }
      return params
    },

    changeRules (item) {
      this.ddlData.ruleId = item.ruleId;
      this.ddlData.ruleName = item.ruleName;
    },
    getDDLParams () {
      if (this.groupList.filter(item => item.groupName == this.ddlData.groupName).length == 0) {
        this.ddlData.groupId = 0
      } else if (this.groupList.filter(item => item.groupName == this.ddlData.groupName).length == 1) {
        let a = this.groupList.filter(item => item.groupName == this.ddlData.groupName);
        this.ddlData.groupId = a[0].groupId
      } else {
        this.ddlData.groupId = this.ddlGroup.groupId
      }
      return {
        jobType: "ddl",
        jobId: this.ddlData.jobId,
        jobName: this.ddlData.jobName,
        agent: this.ddlData.agent,
        groupId: this.ddlData.groupId,
        groupName: this.ddlData.groupName,
        projectId: this.ddlData.projectId,
        routeId: this.ddlData.routeId,
        ruleId: this.ddlData.ruleId,
        sourceId: this.ddlData.sourceId,
        sourceName: this.ddlData.sourceName,
        srcDb: this.ddlData.database.join('#'),
        targetId: this.ddlData.targetId,
        targetSchemaId: this.ddlData.schemaId,
      }
    },

    //checkEnv
    checkIncreEnv (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.incrementData.projectName.length === 0) {
            this.$message({ message: 'Please select Project', type: 'error' });
          } else if (this.incrementData.groupName.length === 0 || this.incrementData.groupName.indexOf(' ') !== -1) {
            this.$message({ message: 'Please select Group', type: 'error' });
          } else if (this.incrementData.route.length === 0) {
            this.$message({ message: 'Please select Route', type: 'error' });
          } else {
            let params = this.getIncreParams()
            if (this.$refs['ref-increment-job-table'].handleIncrementDbTable({}) === false) return
            Job.checkEnvIncrement(params).then(res => {
              if (res.code === Code.SUCCESS) {
                if (res.items.flag) {
                  this.$message({ dangerouslyUseHTMLString: true, message: res.items.message, type: 'success' });
                  this.increDisabled = false
                } else {
                  this.$message({ dangerouslyUseHTMLString: true, message: res.items.message, type: 'warning' });
                  this.increDisabled = true
                }
              } else {
                this.$message({ message: res.message, type: 'error' });
                this.increDisabled = true
              }
            }).catch(() => {
              this.increDisabled = true
            })
          }
        }
      })
    },
    checkFullEnv (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.fullData.projectName.length === 0) {
            this.$message({ message: 'Please select Project', type: 'error' });
          } else if (this.fullData.groupName.length === 0 || this.fullData.groupName.indexOf(' ') !== -1) {
            this.$message({ message: 'Please select Group', type: 'error' });
          } else if (this.fullData.route.length === 0) {
            this.$message({ message: 'Please select Route', type: 'error' });
          } 
          // else if (this.fullData.syncDuration.length === 0) {
          //   this.$message({ message: 'Please select Synchronize Duration', type: 'error' });
          // } 
          else {
            let params = this.getFullParams()
            if (this.$refs['ref-full-job-table'].handleFullDbTable() === false) return
            Job.checkEnvIncrement(params).then(res => {
              if (res.code === Code.SUCCESS) {
                if (res.items.flag) {
                  this.$message({ dangerouslyUseHTMLString: true, message: res.items.message, type: 'success' });
                  this.fullDisabled = false
                } else {
                  this.$message({ dangerouslyUseHTMLString: true, message: res.items.message, type: 'warning' });
                  this.fullDisabled = true
                }
              } else {
                this.$message({ message: res.message, type: 'error' });
                this.fullDisabled = true
              }
            }).catch(() => {
              this.fullDisabled = true
            })
          }
        }
      })
    },
    checkMetaEnv (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          let query = this.$route.query
          if (this.metaData.projectName.length === 0) {
            this.$message({ message: 'Please select Project', type: 'error' });
          } else if (this.metaData.groupName.length === 0 || this.metaData.groupName.indexOf(' ') !== -1) {
            this.$message({ message: 'Please select Group', type: 'error' });
          } else {
            if (query.origin === 'project' || (query.origin === 'job' && query.routeId === 0)) {
              if (this.metaData.route.length === 0) {
                this.$message({ message: 'Please select Route', type: 'error' });
              } else {
                this.requsetCheckEnvMeta()
              }
            } else {
              this.requsetCheckEnvMeta()
            }
          }
        }
      })
    },
    checkEnvDDL (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.load4ddl.pass = true;
          if (this.ddlData.projectName.length === 0) {
            this.$message({ message: 'Please select Project', type: 'error' });
            this.load4ddl.pass = false;
          } else if (this.ddlData.groupName.length === 0 || this.ddlData.groupName.indexOf(' ') !== -1) {
            this.$message({ message: 'Please select Group', type: 'error' });
            this.load4ddl.pass = false;
          } else if (this.ddlData.database.length === 0) {
            this.$message({ message: 'Please select database', type: 'error' });
            this.load4ddl.pass = false;
          } else if (this.ddlData.ruleName.length === 0) {
            this.$message({ message: 'Please select rule name', type: 'error' });
            this.load4ddl.pass = false;
          } else {
            Job.checkEnvDDL({}, this.ddlData.routeId).then(res => {
              if (res.code === Code.SUCCESS) {
                if (res.items.flag) {
                  this.ddlDisabled = false
                  this.$message({ dangerouslyUseHTMLString: true, message: res.items.message, type: 'success' });
                } else {
                  this.$message({ dangerouslyUseHTMLString: true, message: res.items.message, type: 'error' });
                  this.ddlDisabled = true
                }
              } else {
                this.$message({ message: res.message, type: 'error' });
              }
              this.load4ddl.pass = false;
            }).catch(() => {
              this.load4ddl.pass = false;
            })
          }
        }
      })
    },

    // check env of meta
    requsetCheckEnvMeta () {
      Job.checkEnvMeta(this.metaData.sourceId).then(res => {
        if (res.code === Code.SUCCESS) {
          if (res.items.flag) {
            this.$message({ dangerouslyUseHTMLString: true, message: res.items.message, type: 'success' });
            this.metaDisabled = false
          } else {
            this.$message({ dangerouslyUseHTMLString: true, message: res.items.message, type: 'warning' });
            this.metaDisabled = true
          }
        } else {
          this.$message({ message: res.message, type: 'error' });
          this.metaDisabled = true
        }
      }).catch(() => {
        this.metaDisabled = true
      })
    },

    //runJob
    runJob (formName) {
      let params = {};
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (formName == 'metaData') {
            if (this.metaData.groupName.length === 0 || this.metaData.groupName.indexOf(' ') !== -1) {
              this.$message({ message: 'Please select Group', type: 'error' });
            } else {
              params = this.getMetaParams()
              Job.trigger(params).then(res => {
                if (res.code === Code.SUCCESS) {
                  this.$message({ message: 'run job successfully', type: 'success' });
                  this.$router.push('JobsDefinition/' + params.projectId)
                } else {
                  this.$message({ message: res.message, type: 'error' });
                }
                this.load4ddl.run = false;
              }).catch(() => {
                this.load4ddl.run = false;
              })
            }
          } else {
            if (formName == 'incrementData') {
              params = this.getIncreParams()
              params = this.$refs['ref-increment-job-table'].handleIncrementDbTable(params)
              if (params === false) return
            } else if (formName == 'fullData') {
              params = this.getFullParams()
              let syncList = this.$refs['ref-full-job-table'].handleFullDbTable()
              if (syncList === false) return
              params.syncList = syncList
            } else if (formName == 'ddlData') {
              params = this.getDDLParams()
              this.load4ddl.save = true
            }
            Job.trigger(params).then(res => {
              if (res.code === Code.SUCCESS) {
                this.$message({ message: 'run job successfully', type: 'success' });
                this.$router.push('JobsDefinition/' + params.projectId)
              } else {
                this.$message({ message: res.message, type: 'error' });
              }
              this.load4ddl.run = false;
            }).catch(() => {
              this.load4ddl.run = false;
            })
          }

        }
      })
    },

    //save
    saveJob (formName) {
      let params = {};
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (formName == 'metaData') {
            if (this.metaData.groupName.length === 0 || this.metaData.groupName.indexOf(' ') !== -1) {
              this.$message({ message: 'Please select Group', type: 'error' });
            } else {
              params = this.getMetaParams()
              Job.insertJob(params).then(res => {
                if (res.code === Code.SUCCESS) {
                  this.$message({ message: Tips.SAVE_SUCCESS, type: 'success' });
                  this.$router.push('JobsDefinition/' + params.projectId)
                } else {
                  this.$message({ message: res.message, type: 'error' });
                }
                this.load4ddl.save = false
              }).catch(() => {
                this.load4ddl.save = false
              })
            }
          } else {
            if (formName == 'incrementData') {
              params = this.getIncreParams()
              params = this.$refs['ref-increment-job-table'].handleIncrementDbTable(params)
              if (params === false) return
            } else if (formName == 'fullData') {
              params = this.getFullParams()
              let syncList = this.$refs['ref-full-job-table'].handleFullDbTable()
              if (syncList === false) return
              params.syncList = syncList
            } else if (formName == 'ddlData') {
              params = this.getDDLParams()
              this.load4ddl.save = true
            }
            Job.insertJob(params).then(res => {
              if (res.code === Code.SUCCESS) {
                this.$message({ message: Tips.SAVE_SUCCESS, type: 'success' });
                this.$router.push('JobsDefinition/' + params.projectId)
              } else {
                this.$message({ message: res.message, type: 'error' });
              }
              this.load4ddl.save = false
            }).catch(() => {
              this.load4ddl.save = false
            })
          }

        }
      })
    },

    //显示tabs区域
    showIncrement () {
      this.incrementTable = true;
      this.fullTable = false;
      this.metadataTable = false;
      this.ddlTable = false;
      this.bgIncrement = 'background-color:#2c6daf;color:#fff';
      this.bgFull = '';
      this.bgMetadata = '';
      this.bgDDL = '';
    },
    showFull () {
      this.incrementTable = false;
      this.fullTable = true;
      this.metadataTable = false;
      this.ddlTable = false;
      this.bgIncrement = '';
      this.bgFull = 'background-color:#2c6daf;color:#fff';
      this.bgMetadata = '';
      this.bgDDL = '';
    },
    showMetadata () {
      this.incrementTable = false;
      this.fullTable = false;
      this.metadataTable = true;
      this.ddlTable = false;
      this.bgIncrement = '';
      this.bgFull = '';
      this.bgMetadata = 'background-color:#2c6daf;color:#fff';
      this.bgDDL = '';
    },
    showDDL () {
      this.incrementTable = false;
      this.fullTable = false;
      this.metadataTable = false;
      this.ddlTable = true;
      this.bgIncrement = '';
      this.bgFull = '';
      this.bgMetadata = '';
      this.bgDDL = 'background-color:#2c6daf;color:#fff';

    },

  }
}
</script>

<style lang="less" scoped>
.container {
  height: auto;
  .el-tree {
    max-height: 560px;
    overflow-y: auto;
  }
}

.tabs {
  span {
    cursor: pointer;
    &:hover {
      color: #2c6daf;
    }
  }
}

.tabs-content {
  .increment,
  .ddl {
    width: 100%;
    display: flex;
    .el-form {
      margin-left: 15px;
      width: 100%;
    }
    .button {
      margin-top: 15px;
      float: right;
    }
    .el-table,
    .el-input,
    .el-select {
      width: 65%;
    }
  }
  .full,
  .metadata {
    width: 100%;
    display: flex;
    .left {
      width: 20%;
      margin-left: 15px;
    }
    .right {
      margin-left: 15px;
      width: 80%;
      height: 100%;
      .button {
        margin-top: 15px;
        float: right;
      }
      .el-table,
      .el-input,
      .el-select,
      .el-date-editor--daterange.el-input__inner {
        width: 65%;
      }
    }
  }
}

.el-dropdown-link {
  cursor: pointer;
}
ul {
  display: flex;
  flex-direction: column;
  width: 140px;
}
.el-dropdown-menu__item:focus,
.el-dropdown-menu__item:not(.is-disabled):hover {
  color: #586069;
  font-weight: 600;
  background-color: #f5f7fa;
}
.el-button.is-disabled {
  background-color: #2c6daf;
}

.form {
  width: 54%;
  margin-left: 5%;
  .label {
    text-align: right;
    display: inline-block;
    width: 120px;
  }
  .deepblue {
    margin-left: 50px;
    display: inline-block;
  }
}
</style>
