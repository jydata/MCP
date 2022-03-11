<template>
  <section>

    <!-- header[left] -->
    <el-row :gutter="20">
      <el-col :span="6">

        <div class="left">
          <i class="icon-p-service"></i>
          <span>Tabel Structure</span>
        </div>

        <!-- route info -->
        <div style="margin-top: 5px; margin-right: 3px; margin-bottom: 3px">
          路由：<el-select @change="showTargetInfoFun" @clear="clearTargetInfoFun" v-model="routeId" style="width: 52%;" filterable clearable placeholder="请选择">
            <el-option
              v-for="item in dbRouteList"
              :key="item.routeId"
              :label="item.routeName"
              :value="item.routeId"
            >
            </el-option>
          </el-select>

          <el-button align="right" @click="batchGenerateSQL" :loading="loadingBatchExec">批量转换</el-button>

          <div style="margin-top: 3px">
            源端：<el-input v-model="sourceName" style="width: 33%" :disabled="true"></el-input>
            终端：<el-input v-model="targetName" style="width: 34%" :disabled="true"></el-input>
          </div>

          <div style="margin-top: 3px; margin-bottom: 3px">
            终端Schema：<el-select @change="showTreeInfo" @clear="clearSchemaInfoFun" v-model="schemaId" style="width: 65%;" align="left" filterable clearable placeholder="请选择">
            <el-option
              v-for="item in dbSchemaList"
              :key="item.schemaId"
              :label="item.schemaName"
              :value="item.schemaId"
            >
            </el-option>
          </el-select>
          </div>
        </div>

        <div style="margin-top: 3px">
          <el-input placeholder="输入关键字进行过滤" v-model="filterText"/>
        </div>

        <!-- db-table tree -->
        <div style="margin-top: 10px; overflow: auto; height: 490px;">
          <el-tree
            class="filter-tree"
            :props="defaultProps"
            show-checkbox
            node-key="label"
            :data="listdata"
            :expand-on-click-node="isExpandTree"
            @node-click="handleNodeClickFun"
            :filter-node-method="filterNode"
            ref="meta_tree">
          </el-tree>
        </div>
      </el-col>

      <!-- main -->
      <el-col :span="18" v-show="isShow">
        <div style="height: 30px; margin-top: 20px; font-size: large; background: #66b1ff" align="center">表结构查看</div>

        <!-- schema selection -->
        <div style="margin-top: 5px; margin-bottom: 5px" align="right">
          <!-- operate btn -->
          <el-button @click="generateSQLFun" align="right">转  换</el-button>
          <el-button @click="clearSQLFun" align="right">清  空</el-button>
          <!--<el-button @click="deleteSQLFun" align="right">删  除</el-button>-->
          <el-button @click="saveSQLFun" align="right">保  存</el-button>

        </div>

        <!-- column table -->
        <div style="height: 200px; overflow: auto">
          <el-table :data="tableInfo" border height="190" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="40"></el-table-column>
            <el-table-column prop="tableSchema" fixed label="数据库名称" width="100" align="center"></el-table-column>
            <el-table-column prop="tableName" fixed label="表名称" width="130" align="center"></el-table-column>
            <el-table-column prop="columnName" label="表字段名称" width="130" align="center" :sortable=true></el-table-column>
            <el-table-column prop="columnType" label="表字段类型" width="130" align="center"></el-table-column>
            <el-table-column prop="isNullable" label="是否允许为空" align="center"></el-table-column>
            <el-table-column prop="indexName" label="索引名称" align="center"></el-table-column>
            <el-table-column prop="seqInIndex" label="索引序列" align="center"></el-table-column>
            <el-table-column prop="columnComment" label="备注信息" align="center"></el-table-column>
          </el-table>
        </div>

        <!-- create sql -->
        <div style="margin-top: 5px">
          <span>建表语句：</span>
          <div>
            <el-input style="min-height: 80px!important; margin-top: 4px; width: 100%" :rows="3" type="textarea" v-model="ddlSqlInfo" placeholder="Please input Phoenix SQL for HBase Table."/>
          </div>
        </div>

        <!-- secondary index sql -->
        <div style="margin-top: 5px">
          <span>二级索引表建表语句：</span>
          <div>
            <el-input style="min-height: 80px!important; margin-top: 4px; width: 100%;" :rows="3" type="textarea" v-model="indexSqlInfo" placeholder="Please input Phoenix Index SQL for HBase Table."/>
          </div>
        </div>

        <!-- opertate info -->
        <div style="margin-top: 5px">
          <span>操作记录：</span>
          <div>
            <el-input style="min-height: 80px!important; margin-top: 4px; width: 100%;" :rows="3" type="textarea" v-model="tabOperaInfo" disabled="disabled"/>
          </div>
        </div>
      </el-col>

    </el-row>
  </section>
</template>
<script>
  import {page, pageSize, pageSizes} from '@config/config';
  import * as Code from '@config/code';
  import * as Tips from '@config/tips';
  import * as Metadata from '@api/Metadata';

  export default {
    // 监控input框内容变化
    watch: {
      filterText(val) {
        this.$refs.meta_tree.filter(val);
      }
    },
    data() {
      return {
        page: page,
        pageSize: pageSize,
        pageSizes: pageSizes,
        total: 0,
        filterText: '',
        id:'',
        schemaId: '',
        routeId: '',
        routeName: '',
        sourceId: '',
        sourceName: '',
        targetId: '',
        targetName: '',
        srcDbName: '',
        srcTableName: '',
        targetDbName: '',
        targetTableName: '',
        indexTableName: '',
        isShow: false,
        isExpandTree: false,
        loadingBatchExec: false,
        listdata: [],
        tableInfo: [],
        multipleSelection: [],
        dbRouteList: [],
        dbSchemaList: [],
        ddlSqlInfo: '',
        indexSqlInfo: '',
        tabOperaInfo: '',
        defaultProps: {
          label: 'label',
          children: 'children',
          isLeaf: 'leaf'
        }
      };
    },
    created () {
      this.showRouteListFun();
    },
    methods: {
      // 多选赋值- col table
      handleSelectionChange(val) {
        this.multipleSelection = val;
      },
      showRouteListFun(){
        Metadata.getRouteList({
          info: {}
        }).then(res => {
          if (res.code === Code.SUCCESS) {
            this.dbRouteList = res.items.list;
          } else {
            this.$message({message: res.message, type: 'error', center: true});
          }
        })
      },
      loadInitTree(val){
        // 加载数据库级别node
        let flag = "1"
        let param = [{
          linkId: val
        }];

        Metadata.loadTree(flag, param).then(res => {
          this.listdata = res.items;
        })
      },
      showSchemaList(val){
        // 处理右侧逻辑-加载终端Schema下拉列表数据
        Metadata.getSchemaList({
          param: {
            pageSize: 50,
            currentPage: 1
          },
          info: {
            routeId: val,
            executeFlag: 1
          }
        }).then(res => {
          if (res.code === Code.SUCCESS) {
            this.dbSchemaList = res.items.list;
          } else {
            this.$message({message: res.message, type: 'error', center: true});
          }
        })
      },
      showTargetInfoFun(val){
        let obj = {};
        obj = this.dbRouteList.find((item) => {
          return item.routeId === val;
        });

        if (obj != "" && obj != undefined) {
          Metadata.getDBRouteInfo({
            param: {
              routeId: obj.routeId
            }
          }).then(res => {
            if (res.code === Code.SUCCESS) {
              this.routeName = res.items.routeName;
              this.sourceId = res.items.sourceId;
              this.sourceName = res.items.sourceName;
              this.targetId = res.items.targetId;
              this.targetName = res.items.targetName;

              this.loadInitTree(res.items.sourceId)
              this.showSchemaList(obj.routeId)
            } else {
              this.$message({message: res.message, type: 'error', center: true});
            }
          })
        }
      },
      clearTargetInfoFun(){
        this.sourceName = '';
        this.targetName = '';

        this.isShow = false;
        this.schemaId = '';
        this.listdata = [];
      },
      clearSchemaInfoFun(){
        this.schemaId = '';

        this.isShow = false;
        this.listdata = [];
      },
      filterNode(value, data) {
        if (!value) return true;
        return data.label.indexOf(value) !== -1;
      },
      handleNodeClickFun(data){
        if (data.isleaf) {
          this.srcDbName = data.id
          // 处理右侧逻辑-加载表中列table
          Metadata.tableStructureInfo({
            param: data.id
          }).then(res => {
            this.isShow = true
            this.tableInfo = res.items
            this.multipleSelection = []
            this.ddlSqlInfo = ''
            this.indexSqlInfo = ''

            this.showDDLInfo()
          })

          // 处理右侧逻辑-加载该表对应的操作记录信息，用于展示
        }
      },
      showTreeInfo(){
        if (this.schemaId != 0) {
          this.loadInitTree(this.sourceId)
        }
      },
      showDDLInfo(){
        if(this.routeId != 0 && this.schemaId != 0) {
          let src_db_name = this.srcDbName.split("#")[0];
          let src_table_name = this.srcDbName.split("#")[1];
          // 处理右侧逻辑-加载已经生成的对应DDL语句信息
          Metadata.getDDLInfo({
            routeId: this.routeId,
            schemaId: this.schemaId,
            srcDbName: src_db_name,
            srcTableName: src_table_name
          }).then(res => {
            if (res.code === Code.SUCCESS) {
              this.id = res.items.id
              this.ddlSqlInfo = res.items.ddlSql

              if (null != res.items.indexSql) {
                this.indexSqlInfo = res.items.indexSql
              }
            } else {
              this.$message({message: res.message, type: 'error', center: true});
            }
          })
        }
      },
      batchGenerateSQL(){
        let param = this.$refs.meta_tree.getCheckedNodes()
        if(null == param || this.sourceId == 0 || param.length == 0){
          alert("请根据路由信息选择要进行批量转换的表.")
        } else if(null == this.schemaId || this.schemaId == 0){
          alert("请先选择对应的终端Schema信息.");
        } else {
          Metadata.batchGenerateSQL(this.routeId, this.schemaId, param).then(res => {
            if (res.code === Code.SUCCESS) {
              this.$message({message: Tips.TRANSFORM_SUCCESS, type: 'success'});
            } else {
              this.$message({message: res.message, type: 'error', center: true});
            }
          })
        }
      },
      // 转换生成SQL语句
      generateSQLFun(){
        if (null == this.schemaId || this.schemaId == 0) {
          alert("请先选择对应的终端Schema信息.");
        } else {
          this.ddlSqlInfo = ''
          this.indexSqlInfo = ''

          let keyFlag = false;
          this.tableInfo.forEach(item => {
            if(item.indexName != null && item.indexName.indexOf("PRIMARY") > -1){
               keyFlag = true;
            }
          });

          if(!keyFlag){
              alert("该表没有主键，无法进行相应的终端表生成.");
          } else {
            let flag = "1"
            if (this.multipleSelection.length < 1) {
              flag = "0"
              this.multipleSelection.push(this.tableInfo[0])
            }

            let param = this.multipleSelection

            Metadata.generateSQL(flag, this.routeId, this.schemaId, param).then(res => {
              this.ddlSqlInfo = res.items.ddlSql
              this.srcDbName = res.items.srcDbName
              this.srcTableName = res.items.srcTableName
              this.targetDbName = res.items.targetDbName
              this.targetTableName = res.items.targetTableName
              this.indexTableName = res.items.indexTableName

              if (null != res.items.indexSql) {
                this.indexSqlInfo = res.items.indexSql
              }
            })
          }
        }
      },
      // 清空转换生成的SQL语句
      clearSQLFun(){
        this.ddlSqlInfo = ''
        this.indexSqlInfo = ''
      },
      // 保存SQL语句入库
      saveSQLFun(){
        if (this.schemaId != 0) {
          Metadata.saveSQL({
            id: this.id,
            routeId: this.routeId,
            routeName: this.routeName,
            schemaId: this.schemaId,
            srcDbName: this.srcDbName,
            srcTableName: this.srcTableName,
            targetDbName: this.targetDbName,
            targetTableName: this.targetTableName,
            indexTableName: this.indexTableName,
            ddlSql: this.ddlSqlInfo,
            indexSql: this.indexSqlInfo
          }).then(res => {
            if (res.code === Code.SUCCESS) {
              this.ddlSqlInfo = res.items.ddlSql
              this.indexSqlInfo = res.items.indexSql
              this.$message({message: Tips.SAVE_SUCCESS, type: 'success'});
            } else {
              this.$message({message: res.message, type: 'error'});
            }
          })
        }
      },
      // 每页条数：pageSize 改变时会触发
      handleSizeChange(size) {
        this.pagesize = size;
      },
      // 当前页码：currentPage 改变时会触发
      handleCurrentChange(currentPage) {
        this.currentPage = currentPage;
      },
    }
  }
</script>
<style>
</style>
