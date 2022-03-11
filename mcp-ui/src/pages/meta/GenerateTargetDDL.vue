<template>
  <section>
    <!--头部-->
    <div class="header">
      <div class="title">
        <el-breadcrumb separator-class="el-icon-arrow-right">
          <el-breadcrumb-item>Generate Target DDL</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
    </div>

    <!--内容-->
    <div class="container gray mt0">
      <div class="left flex fxcolumn">
        <div v-if="isTarget">
          <el-cascader :options="targetList" placeholder="Target / Schema" size="medium" @change="changeTarget" ref="cascader"></el-cascader>
        </div>
        <div class="shadow tree flex1">
          <el-input class="search-input" prefix-icon="el-icon-search" placeholder="Search sourceSchema or table" v-model="filterText"></el-input>
          <el-tree
            class="pd10 mcp-rollbar"
            :style="{maxHeight: treeHeight + 'px'}"
            empty-text="No data"
            :data="schemaList"
            :props="defaultProps"
            ref="tree"
            accordion
            :filter-node-method="filterNode"
            @node-click="getMetadata">
          </el-tree>
        </div>
      </div>
      <div class="right flex1">
        <div class="top">
          <el-table class="shadow" :data="routeList" empty-text="No data">
            <el-table-column type="expand">
              <template slot-scope="props">
                <el-table class="no-header no-table" :data="props.row.children" empty-text="No data">
                  <el-table-column width="48px"></el-table-column>
                  <el-table-column prop="routeName" min-width="92px"></el-table-column>
                  <el-table-column prop="sourceName" min-width="93px"></el-table-column>
                  <el-table-column prop="dbName" min-width="178px"></el-table-column>
                  <el-table-column prop="targetName" min-width="93px"></el-table-column>
                  <el-table-column min-width="93px">
                    <template slot-scope="scope">
                      <span>{{schemaName}}</span>
                    </template>
                  </el-table-column>
                  <el-table-column prop="ruleName" min-width="125px"></el-table-column>
                  <el-table-column min-width="93px"></el-table-column>
                </el-table>
              </template>
            </el-table-column>
            <el-table-column min-width="120px" label="Routing Name" prop="routeName"></el-table-column>
            <el-table-column min-width="120px" label="Source Name" prop="sourceName"></el-table-column>
            <el-table-column label="Database" min-width="230px">
              <template slot-scope="scope">
                <el-select  class="db-select"
                            placeholder="Please select"
                            @focus="getDatabase(scope.row)"
                            v-model="scope.row.database"
                            @change="changeValue"
                            filterable multiple collapse-tags>
                  <el-option v-for="item in DBList"
                             :key="item"
                             :label="item"
                             :value="item"></el-option>
                </el-select>
              </template>
            </el-table-column>
            <el-table-column min-width="120px" label="Target Name" prop="targetName"></el-table-column>
            <el-table-column min-width="120px" label="Target Schema">
              <template slot-scope="scope">
                <span>{{schemaName}}</span>
              </template>
            </el-table-column>
            <el-table-column  min-width="160px" label="Rule Name">
              <template slot-scope="scope">
                <el-select placeholder="Please select" @focus="getRules" v-model="scope.row.ruleName" filterable>
                  <el-option v-for="item in ruleList"
                             :key="item.ruleName"
                             :label="item.ruleName"
                             :value="item.ruleName"></el-option>
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="Action" min-width="120px">
              <template slot-scope="scope">
                <el-button size="small" @click="gotoDDL(scope.row)">Create Job</el-button>
              </template>
            </el-table-column>
          </el-table>

        </div>

        <div>
          <div>
            <ul>
              <li class="shadow" style="background-color:#2c6daf;color:#fff;width: 90px">Metadata</li>
            </ul>
          </div>
            <!--Metadata-->
          <div class="meta-content" style="margin-top: -10px">
              <el-table class="shadow" empty-text="No data" :data="metadataList">
                <el-table-column label="Source" prop="sourceName"></el-table-column>
                <el-table-column label="Schema" prop="database"></el-table-column>
                <el-table-column label="Table Name" prop="tableName"></el-table-column>
                <el-table-column label="Engine" prop="engine"></el-table-column>
                <el-table-column label="Charset" prop="charset"></el-table-column>
                <el-table-column label="Table Comment" prop="tableComment" width="150px"></el-table-column>
                <el-table-column label="Table Rows" prop="tableRows"></el-table-column>
                <el-table-column label="Rule Name">
                  <template slot-scope="scope">
                    <el-select @focus="getRules"
                               v-model="ruleData"
                               placeholder="Please select"
                               filterable clearable>
                      <el-option v-for="item in ruleList"
                                 :label="item.ruleName"
                                 :key="item.ruleName"
                                 :value="item.ruleName"></el-option>
                    </el-select>
                  </template>
                </el-table-column>
              </el-table>

              <el-table :data="columnList" class="shadow" empty-text="No data" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55"></el-table-column>
                <el-table-column label="Column" prop="columnName"></el-table-column>
                <el-table-column label="#" type="index"></el-table-column>
                <el-table-column label="Data Type" prop="columnType"></el-table-column>
                <el-table-column label="Not Null"  prop="isNullable" align="center">
                  <template slot-scope="scope">
                    <el-checkbox true-label="NO" false-label="YES" v-model="scope.row.isNullable" disabled="true">
                    </el-checkbox>
                  </template>
                </el-table-column>
                <el-table-column label="Key" prop="columnKey"></el-table-column>
                <el-table-column label="Default" prop="columnDefault"></el-table-column>
                <el-table-column label="Comment" prop="columnComment"></el-table-column>
              </el-table>
            </div>
        </div>

        <div class="down">
          <div class="tabs">
            <div style="display: flex;justify-content: space-between;">
              <ul>
                <li class="shadow"
                    style="width:90px;height: 30px"
                    :style="bgTable"
                    @click="showTableDDL">TableDDL</li>
                <li class="shadow"
                    style="width:90px;height: 30px"
                    :style="bgIndex"
                    @click="showIndexDDL">Index DDL</li>
                <li class="shadow"
                    style="width:90px;height: 30px"
                    :style="bgOperate"
                    @click="showOperateDDL">Operate DDL</li>
              </ul>
              <span style="margin-top: 20px;" v-show="isDDLFlag">
                <i class="icon-cogs mr20" title="Transform DDL" @click="transformSingleDDL"></i>
                <i class="icon-floppy-o mr20" title="Save" v-loading.fullscreen.lock="isLoading" @click="saveSingleDDL"></i>
                <i class="icon-refresh" v-loading.fullscreen.lock="isLoading" title="Synchronize" @click="syncSingleDDL"></i>
              </span>
            </div>
            <div class="tabs-content" style="margin-top: -10px">
              <!--table-->
              <div class="table-content shadow" v-show="tablePanel">
                <div  v-highlight>
                 <pre>
                   <code v-html="tableDDL" contenteditable="true" ref="ddl">
                   </code>
                 </pre>
                </div>
              </div>

              <!--Index-->
              <div class="index-content shadow" v-show="indexPanel" >
                <div  v-highlight>
                 <pre>
                   <code v-html="indexDDL" contenteditable="true" ref="index">
                   </code>
                 </pre>
                </div>
              </div>

              <!--operate-->
              <div :class="['operate-content','shadow',{'center':isCenter}]" v-show="operatePanel">
                <div  v-highlight>
                 <pre>
                   <code v-html="operateDDL">
                   </code>
                 </pre>
                </div>
              </div>
            </div>

          </div>
        </div>
      </div>
    </div>

    <!--提示框-->
    <el-dialog title="Message" :visible.sync="dialogRule">
      <span>Please select rule!</span>
    </el-dialog>
  </section>
</template>

<script>
  import * as Meta from '@api/Meta';
  import * as Code from '@config/code';
  import * as Tips from '@config/tips';
  import { Message } from 'element-ui';

  export default {
    name: "GenerateTargetDDL",
    data() {
      return {
        // tree height
        treeHeight: document.body.clientHeight - 275,

        title: '',
        multipleSelection: [],
        filterText: '',
        ruleData: '',
        nodeSourceId: '',
        nodeRouteId: '',
        sourceSchema: '',
        tableName: '',

        // tabs区域
        tableDDL: '',
        indexDDL: '',
        operateDDL: '',

        tablePanel: true,
        indexPanel: false,
        operatePanel: false,

        bgTable: 'background-color:#2c6daf;color:#fff',
        bgIndex: '',
        bgOperate: '',

        targetList: [],
        schemaList: [],
        routeList: [],
        ruleList: [],
        DBList: [],
        metadataList: [],
        columnList: [],
        DDLList: [],
        defaultProps: {
          children: 'children',
          label: 'label'
        },

        dialogRule: false,
        transformId: '',
        saveId: '',
        selectTarget: '',
        isDDLFlag: false,
        selected: '',
        isCenter: false,

        isTarget: '',
        isLoading: false,

        routeId: '',
        sourceId: '',
        schemaId: '',
        schemaName: '',
        targetId: '',
      };
    },

    watch: {
      filterText(val) {
        this.$refs.tree.filter(val);
      },

    },

    created() {
      if (typeof(this.$route.query.schemaId) === "undefined") {
        this.title = 'Generate Target DDL';
        this.isTarget = true;
        this.queryTarget();
      } else {
        this.title = 'DS Routing -> Generate Target DDL';
        this.isTarget = false;
        this.sourceId = this.$route.query.sourceId;
        this.schemaId = this.$route.query.schemaId;
        this.schemaName = this.$route.query.schemaName;
        this.targetId = this.$route.query.targetId;
        this.getSchema();
        this.getRoutes();
      }
    },
    methods: {
      handleSelectionChange(val) {
        this.multipleSelection = val;
      },
      changeValue(arr){
        let len = arr.filter(item => item === 'All').length
        if(arr[0] === 'All'){
          arr.splice(1,arr.length - 1)
        }else if(arr[0] !== 'All' && len >0) {
          let index = arr.indexOf('All')
          arr.splice(index, 1)
        }
      },

      //过滤关键字
      filterNode(value, data) {
        if (!value) return true;
        return data.label.indexOf(value) !== -1;
      },

      //tabs显示对应模块
      showTableDDL() {
        this.tablePanel = true;
        this.indexPanel = false;
        this.operatePanel = false;
        this.bgTable = 'background-color:#2c6daf;color:#fff';
        this.bgIndex = '';
        this.bgOperate = '';
      },
      showIndexDDL() {
        this.tablePanel = false;
        this.indexPanel = true;
        this.operatePanel = false;
        this.bgTable = '';
        this.bgIndex = 'background-color:#2c6daf;color:#fff';
        this.bgOperate = '';
      },
      showOperateDDL() {
        this.tablePanel = false;
        this.indexPanel = false;
        this.operatePanel = true;
        this.bgTable = '';
        this.bgIndex = '';
        this.bgOperate = 'background-color:#2c6daf;color:#fff';
      },

      changeTarget(item) {
        this.routeList=[]
        this.targetId = item[0];
        this.schemaId = item[1];
        this.schemaName = this.$refs.cascader.currentLabels[1];
        this.getSchema();
        this.getRoutes()
      },

      //获取终端信息
      queryTarget() {
        Meta.queryTarget({}).then(res => {
          if (res.code === Code.SUCCESS) {
            for (let list of res.items) {
              let children = [];
              for (let sub of list.entities) {
                let child = {
                  label: sub.schemaName,
                  value: sub.schemaId,
                  children: null
                }
                children.push(child)
              }
              let arr = {
                value: list.entities[0].targetId,
                label: list.targetName,
                children: children
              }
              this.targetList.push(arr)
            }
          } else {
            this.$message({message: res.message, type: 'error'});
          }
        }).catch(() => {
        })
      },
      //显示Schema树
      getSchema() {
        Meta.getSchema({targetId: this.targetId}).then(res => {
          if (res.code === Code.SUCCESS) {
            this.schemaList = res.items;
          } else {
            this.$message({message: res.message, type: 'error'});
          }
        }).catch(() => {
        })
      },

      getRoutes() {
        Meta.getRoutes({
          targetId: this.targetId,
          targetSchemaId: this.schemaId
        }).then(res => {
          if (res.code === Code.SUCCESS) {
            this.routeList = res.items;
          } else {
            this.$message({message: res.message, type: 'error'});
          }
        }).catch(() => {
        })
      },

      //获取DB信息
      getDatabase(item) {
        Meta.getDatabase({sourceId: item.sourceId}).then(res => {
          if (res.code === Code.SUCCESS) {
            this.DBList = res.items;

          } else {
            this.$message({message: res.message, type: 'error'});
          }
        }).catch(() => {
        })
      },
      //获取rule信息
      getRules() {
        Meta.getRules({}).then(res => {
          if (res.code === Code.SUCCESS) {
            this.ruleList = res.items;
          } else {
            this.$message({message: res.message, type: 'error'});
          }
        }).catch(() => {
        })
      },
      //获取Metadata数据
      getMetadata(data, node) {
        this.isDDLFlag = false;
        this.metadataList = [];
        this.columnList = [];
        this.tableDDL = '';
        this.indexDDL = '';
        this.operateDDL = '';
        if (data.isleaf) {
          this.isDDLFlag = true;
          this.ruleData = '';
          let arr = (node.parent.parent.data.id).split("#");
          this.nodeSourceId = arr[0];
          this.nodeRouteId = arr[1];
          this.sourceSchema = node.parent.data.label;
          this.tableName = node.label;
          Meta.getMetadata({
            sourceId: this.nodeSourceId,
            schema: this.sourceSchema,
            table: this.tableName
          }).then(res => {
            if (res.code === Code.SUCCESS) {
              this.metadataList = res.items;
            } else {
              this.$message({message: res.message, type: 'error'});
            }
          }).catch(() => {
          })
          let param = [this.sourceSchema, this.tableName, this.nodeSourceId]
          Meta.getColumns({
            param: param.join("#")
          }).then(res => {
            if (res.code === Code.SUCCESS) {
              this.columnList = res.items;
            } else {
              this.$message({message: res.message, type: 'error'});
            }
          }).catch(() => {
          })
          Meta.getDDL({
            routeId: this.nodeRouteId,
            schemaId: this.schemaId,
            srcDbName: this.sourceSchema,
            srcTableName: this.tableName
          }).then(res => {
            if (res.code === Code.SUCCESS) {
              this.DDLList = res.items;
              this.tableDDL = this.DDLList.ddlSql;
              this.indexDDL = this.DDLList.indexSql;
              this.transformId = this.DDLList.id
            } else {
              this.$message({message: res.message, type: 'error'});
            }
          }).catch(() => {
          })
          Meta.getBinlog(this.nodeRouteId, this.sourceSchema, this.tableName).then(res => {
            if (res.code === Code.SUCCESS) {
              this.operateDDL = res.items;
              if(res.items == null){
                this.operateDDL = 'No Data'
                this.isCenter = true
              }else{
                this.isCenter = false
              }
            } else {
              this.$message({message: res.message, type: 'error'});
            }
          }).catch(() => {
          })
        }
      },

      //跳转Batch DDL
      gotoDDL(val) {
        let r=this.ruleList.filter(item=> item.ruleName == val.ruleName);
        let ruleId = r.length?r[0].ruleId:''
        if (typeof(val.ruleName) == "undefined" || val.ruleName == '') {
          this.dialogRule = true;
        } else {
          this.$router.push({
            path: '/project/NewJob',
            query: {
              origin: 'DDL',
              routeId: val.routeId,
              routeName: val.routeName,
              sourceId: val.sourceId,
              sourceName: val.sourceName,
              schemaId: this.schemaId,
              targetId: this.targetId,
              targetName: val.targetName,
              targetSchema: this.schemaName,
              ruleId: ruleId,
              ruleName: val.ruleName,
              database: val.database
            }
          })
        }
      },

      transformSingleDDL() {
        if (this.transformId === '') {
          this.transformId = 0;
        }
        if (this.ruleData === '') {
          this.dialogRule = true
        } else if (this.columnList.filter(item => item.columnKey == "PRI").length == 0) {
          this.$message.error("This table must have a primary key!")
        } else {
          let params = [];
          if (this.multipleSelection == '') {
            params = (this.columnList.filter(item => item.columnKey == "PRI"));
          } else {
            params = this.multipleSelection
          }
          Meta.transformSingleDDL(params, 1, this.nodeRouteId, this.schemaId, this.transformId, this.ruleData, this.sourceSchema).then(res => {
            if (res.code === Code.SUCCESS) {
              this.DDLList = res.items;
              this.tableDDL = res.items.ddlSql;
              this.indexDDL = res.items.indexSql;
              this.$message({message: Tips.TRANSFORM_SUCCESS, type: 'success'});
            } else {
              this.$message({message: res.message, type: 'error'});
            }
          }).catch(() => {
          })
        }
      },

      syncSingleDDL() {
        let arr = this.$refs.ddl.innerText;
        let indexArr = this.$refs.index.innerText;
        if (this.ruleData === '') {
          this.dialogRule = true
        } else if (arr.indexOf("CREATE TABLE") == -1) {
          this.$message.error("DDL cannot be empty!")
        } else {
          let params = [];
          this.isLoading = true;
          params.push(this.DDLList)
          Meta.syncSingleDDL(params, this.nodeSourceId, this.ruleData, this.sourceSchema, this.nodeRouteId, this.schemaId).then(res => {
            if (res.code === Code.SUCCESS) {
              this.$message({message: Tips.SYNC_SUCCESS, type: 'success'});
            } else {
              this.$message({message: res.message, type: 'error'});
            }
            this.isLoading = false;
          }).catch(() => {
            this.isLoading = false;
          })
        }
      },

      //单表保存DDL
      saveSingleDDL() {
        let arr = this.$refs.ddl.innerText;
        let indexArr = this.$refs.index.innerText;
        if (arr.indexOf("CREATE TABLE") != -1 || indexArr.indexOf("CREATE INDEX") != -1) {
          this.isLoading = true;
          this.DDLList.ddlSql = this.$refs.ddl.innerText;
          this.DDLList.indexSql = this.$refs.index.innerText;
          let params = this.DDLList;
          Meta.saveDDL(params, this.sourceSchema, this.nodeRouteId).then(res => {
            if (res.code === Code.SUCCESS) {
              this.DDLList = res.items;
              this.$message({message: Tips.SAVE_SUCCESS, type: 'success'});
              setTimeout(() => {
                this.isLoading = false
              }, 1000)
            } else {
              this.isLoading = false
              this.$message({message: res.message, type: 'error'});
            }
          }).catch(() => {
            this.isLoading = false
          })
        } else {
          this.$message.error("DDL cannot be empty!")
        }
      }
    },
  }
</script>

<style lang="less" scoped>
  .container{height: auto;display: flex;
  .left{
    width: 20%;
    height: auto;
    .el-cascader{
      margin-top: 15px;
      width: 100%;
    }
    .tree{

      .el-tree{
        overflow-y: auto; max-height: 560px;
      }
    }
  }
  .right{
    margin-left: 15px;
    width: 70%;
    height: 100%;
    ul{display: flex;
      li{height: 30px;display: flex;justify-content: space-around;align-items: center;padding: 6px;} }
    .down>.tabs{ul>li{cursor: pointer;&:hover{color:#2c6daf}}}
  }
  }

  .cascader-header{
    color: #aaa;
    background-color: #f7f7f7;
    width: 100%;
    height: 40px;
    line-height: 40px;
  }
  .card-box {
    width: 100%;
    cursor: pointer;
    height: 40px;
    line-height: 40px;
  }

    .card-title{
      display: flex;
      align-items: center;
      justify-content: space-between;
    }

    .card{
      margin-top: -40px;
      margin-left: 20%;
      z-index: 100;
      width: 15%;
      display: inline;
      position: absolute;
      text-align: center;
      background-color: #fff;
      box-shadow: #ddd 0 0 10px;
      li{
        &:hover{
          background-color: #f5f7fa;
        }
      }
    }

  .bgColor{background-color: #f5f7fa}

</style>
