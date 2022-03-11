<template>
  <section>
    <!-- header -->
    <div class="header">
      <div class="top clearfix">
        <div class="left">
          <i class="icon-p-service"></i>
          <span>Table Generation</span>
        </div>
        <div class="right">
          <el-button @click="getHBaseSQLsFun">Query</el-button>
          <el-button @click="createTabFun" :loading="loadingCreate">Create Table</el-button>
        </div>
      </div>
      <div class="down" >
        <div class="list clearfix">
          <div class="left ">
            <div class="press">
              <span class="key">Target Schema</span>
              <el-input v-model="selectForm.targetDbName" clearable></el-input>
            </div>
            <div class="press">
              <span class="key">Route Name</span>
              <el-input v-model="selectForm.routeName" clearable></el-input>
            </div>
            <div class="press">
              <span class="key" >Source Schema</span>
              <el-input v-model="selectForm.srcDbName" clearable></el-input>
            </div>
            <div class="press">
              <span class="key">Executed</span>
              <el-select v-model="selectForm.executeFlag" placeholder="Please select"  clearable>
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

    <!-- table list -->
    <div class="table-master">
      <el-table :data="dataList" border v-autoHeight  @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="40"></el-table-column>

        <!--<el-table-column prop="routeName" label="路由名称" align="center" width="120" :sortable=true></el-table-column>-->
        <!--<el-table-column prop="srcDbName" label="源库名称" align="center" width="120" :sortable=true></el-table-column>-->
        <!--<el-table-column prop="srcTableName" label="源表名称" align="center" width="160" :sortable=true></el-table-column>-->
        <!--<el-table-column prop="targetDbName" label="终端Schema" align="center" width="130" :sortable=true></el-table-column>-->
        <!--<el-table-column prop="targetTableName" label="终端表名称" align="center" width="200" :sortable=true></el-table-column>-->
        <!--<el-table-column prop="indexTableName" label="索引表名称" align="center" width="200"></el-table-column>-->
        <!--<el-table-column prop="ddlSql" label="建表DDL" align="center" show-overflow-tooltip></el-table-column>-->
        <!--<el-table-column prop="indexSql" label="索引表DDL" align="center"  show-overflow-tooltip></el-table-column>-->
        <!--<el-table-column prop="updateTime" label="修改时间" align="center" width="160"></el-table-column>-->
        <!--<el-table-column label="是否已执行" align="center" width="100">-->

        <el-table-column prop="routeName" label="RouteName" align="center" width="120" :sortable=true></el-table-column>
        <el-table-column prop="srcDbName" label="Source Schema" align="center" width="160" :sortable=true></el-table-column>
        <el-table-column prop="srcTableName" label="Source TableName" align="center"  :sortable=true></el-table-column>
        <el-table-column prop="targetDbName" label="Target Schema" align="center" :sortable=true></el-table-column>
        <el-table-column prop="targetTableName" label="Target TableName" align="center"  :sortable=true></el-table-column>
        <el-table-column prop="indexTableName" label="Index TableName" align="center" ></el-table-column>
        <el-table-column prop="ddlSql" label="DDL SQL" align="center" width="100" show-overflow-tooltip></el-table-column>
        <el-table-column prop="indexSql" label="Index SQL" align="center"  width="100" show-overflow-tooltip></el-table-column>
        <el-table-column prop="updateTime" label="Create Time" align="center"></el-table-column>
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
          :total="total">
      </el-pagination>
    </div>
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
      return {
        userRoleList: [{value: '1', label: 'Yes'}, {value: '0', label: 'No'},],
        page: page,
        pageSize: pageSize,
        pageSizes: pageSizes,
        total: 0,
        dataList: [],
        multipleSelection: [],
        loadingCreate: false,
        selectForm: {
          routeName: '',
          srcDbName: '',
          targetDbName: '',
          executeFlag: '',
        },
      }
    },
    created () {
      this.getHBaseSQLsFun();
    },
    methods: {
      // 多选赋值
      handleSelectionChange(val) {
        this.multipleSelection = val;
      },
      getHBaseSQLsFun(page){
        if(page) this.page=1;
        Metadata.getHBaseSQLInfos({
          param: {
            pageSize: this.pageSize,
            currentPage: page ?  1 : this.page
          },
          info: {
            routeName: this.selectForm.routeName,
            srcDbName: this.selectForm.srcDbName,
            targetDbName: this.selectForm.targetDbName,
            executeFlag: this.selectForm.executeFlag,
          }
        }).then(res => {
          if (res.code === Code.SUCCESS) {
            this.dataList = res.items.list;
            this.total = res.items.total;
          } else {
            this.$message({message: res.message, type: 'error'});
          }
        })
      },
      createTabFun(){
        if(this.multipleSelection.length < 1){
          alert("Please select the data you want to execute.");
        } else {
          this.loadingCreate = true;
          let param = this.multipleSelection
          Metadata.multiExecSQL(param).then(res => {
            this.loadingCreate = false;
            if (res.code === Code.SUCCESS) {
              this.$message({message: Tips.TARGET_GENERATION_SUCCESS, type: 'success'});
              this.getHBaseSQLsFun()
            } else {
              this.$message({message: Tips.TARGET_GENERATION_FAILED, type: 'error'});
            }
          }).catch(() => {
            this.loadingCreate = false;
          })
        }
      },
      handleSizeChange(pageSize){
        this.pageSize = pageSize;
        this.getHBaseSQLsFun();
      },
      handleCurrentChange(page){
        this.page = page;
        this.getHBaseSQLsFun();
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

