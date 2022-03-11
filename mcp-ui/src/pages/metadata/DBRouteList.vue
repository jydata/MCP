<template>
  <section>
    <!-- header -->
    <div class="header">
      <div class="top clearfix">
        <div class="left">
          <i class="icon-p-service"></i>
          <span>Data Routing Management</span>
        </div>
        <div class="right">
          <el-button @click="popDBRouteFun" :loading="loadingAdd">Add</el-button>
        </div>
      </div>
    </div>

    <!-- route list -->
    <div class="table-master">
      <el-table :data="dataList" border v-autoHeight>
        <el-table-column type="index" label="ID" align="center" width="60" :sortable=true></el-table-column>

        <!--<el-table-column prop="routeName" label="路由名称" align="center" :sortable=true></el-table-column>-->
        <!--<el-table-column prop="sourceName" label="源端" align="center" :sortable=true></el-table-column>-->
        <!--<el-table-column prop="targetName" label="目标端" align="center" :sortable=true></el-table-column>-->
        <!--<el-table-column prop="updateTime" label="修改时间" align="center" :sortable=true></el-table-column>-->
        <!--<el-table-column width="150" label="状态" align="center">-->

        <el-table-column prop="routeName" label="RouteName" align="center" :sortable=true></el-table-column>
        <el-table-column prop="sourceName" label="Source" align="center" :sortable=true></el-table-column>
        <el-table-column prop="targetName" label="Target" align="center" :sortable=true></el-table-column>
        <el-table-column prop="updateTime" label="Create Time" align="center" :sortable=true></el-table-column>
        <el-table-column width="150" label="Status" align="center">

          <template slot-scope="scope">
            <el-switch
              active-color="#3d82ca"
              inactive-color="#bbb"
              v-model="scope.row.routeStatus"
              @change="changeSwitchFun(scope.$index,scope.row)"
              active-value="1"
              inactive-value="0">
            </el-switch>
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

    <!-- change status dialog -->
    <el-dialog
      :close-on-click-modal="false"
      title="Tips"
      :visible.sync="dialogVisible"
      :show-close="false"
      width="30%"
      :before-close="handleCloseFun">
      <span v-text="showMsg"></span>
      <span slot="footer" class="dialog-footer">
              <el-button @click="closeDialogFun">No</el-button>
              <el-button @click="updateStatusFun">Yes</el-button>
            </span>
    </el-dialog>

    <!-- add route dialog -->
    <el-dialog class="add-dialog" title="Add Route information" :close-on-click-modal="false" :visible.sync="routeDialog" :show-close="false" width="30%" height="60%">
      <el-form label-width="160px" :model="connForm" :rules="rules" ref="connForm" class="item-error">
        <el-form-item label="Route Name：" prop="routeName">
          <el-input v-model="connForm.routeName" style="width: 90%" clearable></el-input>
        </el-form-item>
        <el-form-item label="Source Name：" prop="sourceName">
          <el-select @change="showSrcIPFun" v-model="connForm.sourceName" filterable clearable placeholder="Please select">
            <el-option
              v-for="item in sources"
              :key="item.linkName"
              :label="item.linkName"
              :value="item.linkName">
            </el-option>
          </el-select>
        </el-form-item>

        <!--<el-input v-model="connForm.sourceId" type="hidden"></el-input>-->
        <el-form-item label="Source IP：">
          <el-input v-model="connForm.sourceIp" style="width: 90%" disabled=""></el-input>
        </el-form-item>

        <el-form-item label="Target Name：" prop="targetName">
          <el-select @change="showTargetIPFun" v-model="connForm.targetName" filterable clearable placeholder="Please select">
            <el-option
              v-for="item in targets"
              :key="item.linkName"
              :label="item.linkName"
              :value="item.linkName">
            </el-option>
          </el-select>
        </el-form-item>

        <!--<el-input v-model="connForm.targetId" type="hidden"></el-input>-->
        <el-form-item label="Target Ip：">
          <el-input v-model="connForm.targetIp" style="width: 90%" disabled=""></el-input>
        </el-form-item>

        <el-form-item>
          <el-button @click="closeAddFun('connForm')">Close</el-button>
          <el-button @click="saveDBRouteFun('connForm')" :loading="loadingSave">Confirm</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

  </section>
</template>
<script>
  import {page, pageSize, pageSizes} from '@config/config';
  import * as Code from '@config/code';
  import * as Tips from '@config/tips';
  import * as Metadata from '@api/Metadata';

  export default {
    data () {
      var checkRouteName = (rule, value, callback) => {
        if (value === '') {
          callback(new Error('请填写路由名称信息!'));
        } else {
          callback();
        }
      };

      var checkSourceName = (rule, value, callback) => {
        if (value === '') {
          callback(new Error('请勾选源端信息!'));
        } else {
          callback();
        }
      };

      var checkTargetName = (rule, value, callback) => {
        if (value === '') {
          callback(new Error('请勾选终端信息!'));
        } else {
          callback();
        }
      };

      return {
        page: page,
        pageSize: pageSize,
        pageSizes: pageSizes,
        total: 0,
        loadingAdd: false,
        loadingSave: false,
        routeDialog: false,
        dataList: [],
        sources: [],
        targets: [],
        connForm: {
          routeId: '',
          sourceId: '',
          sourceIp: '',
          targetId: '',
          targetIp: '',
          routeName: '',
          sourceName: '',
          targetName: '',
          routeStatus: ''
        },
        rules: {
          routeName: [
            {required: true, validator: checkRouteName, trigger: 'blur'}
          ],
          sourceName: [
            {required: true, validator: checkSourceName, trigger: 'blur'}
          ],
          targetName: [
            {required: true, validator: checkTargetName, trigger: 'blur'}
          ]
        },
        dialogVisible: false,
        showMsg: '',
      }
    },
    created () {
      this.getDBRoutesFun();
    },
    methods: {
      loadOptionsFun(flag){
        let param = {
          flag: flag
        }

        Metadata.loadRouteOptions(param).then(res => {
          this.loadingAdd = false;
          if (res.code === Code.SUCCESS) {
            if (flag == '0') {
              this.sources = res.items;
            } else {
              this.targets = res.items;
            }
          } else {
            this.$message({message: res.message, type: 'error', center: true});
          }
        })
      },
      showSrcIPFun(){
        for (let item of this.sources) {
          if (this.connForm.sourceName == item.linkName) {
            this.connForm.sourceId = item.linkId;
            this.connForm.sourceIp = item.ip;
          }
        }
      },
      showTargetIPFun(){
        for (let item of this.targets) {
          if (this.connForm.targetName == item.linkName) {
            this.connForm.targetId = item.linkId;
            this.connForm.targetIp = item.ip;
          }
        }
      },
      getDBRoutesFun(){
        Metadata.getDBRoutes({
          param: {
            pageSize: this.pageSize,
            currentPage: this.page
          },
          info: {flag: '1'}
        }).then(res => {
          if (res.code === Code.SUCCESS) {
            this.dataList = res.items.list;
            this.total = res.items.total;
          } else {
            this.$message({message: res.message, type: 'error', center: true});
          }
        })
      },
      popDBRouteFun(){
        this.loadingAdd = true;
        this.routeDialog = true;

        this.connForm.routeName = 'MCP-1';
        this.connForm.sourceName = '';
        this.connForm.targetName = '';

        this.connForm.sourceIp = '';
        this.connForm.targetIp = '';

        // 加载源端下拉列表
        this.loadOptionsFun('0');
        // 加载终端下拉列表
        this.loadOptionsFun('1');
      },
      handleCloseFun(){
        this.dialogVisible = false;
      },
      changeSwitchFun(index, row) {
        this.dialogVisible = true;

        this.connForm.routeId = row.routeId;
        this.connForm.routeName = row.routeName;
        this.connForm.routeStatus = row.routeStatus;
        if (row.routeStatus == "0") {
          this.showMsg = '禁用后将无法正常访问，是否确认禁用？';
        } else {
          this.showMsg = '是否确认启用？';
        }
      },
      updateStatusFun(){
        Metadata.updateRouteStatus({
          routeId: this.connForm.routeId,
          routeName: this.connForm.routeName,
          routeStatus: this.connForm.routeStatus
        }).then(res => {
          this.dialogVisible = false;
          if (res.code === Code.SUCCESS) {
            this.$message({message: Tips.UPDATE_SUCCESS, type: 'success', center: true});
            this.getDBRoutesFun();
          } else {
            this.$message({message: res.message, type: 'error', center: true});
            this.getDBRoutesFun();
          }
        })
      },
      closeAddFun(formName){
        this.routeDialog = false;

        this.$refs[formName].clearValidate();
      },
      closeDialogFun(){
        this.dialogVisible = false;
        if (this.connForm.routeStatus == "0") {
          this.connForm.routeStatus == "1";
        } else {
          this.connForm.routeStatus == "0";
        }
        this.getDBRoutesFun();
      },
      saveDBRouteFun(formName){
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.loadingSave = true;

            let param = {
              routeName: this.connForm.routeName,
              sourceId: this.connForm.sourceId,
              sourceName: this.connForm.sourceName,
              targetId: this.connForm.targetId,
              targetName: this.connForm.targetName
            }

            Metadata.saveDBRoute(param).then(res => {
              this.loadingSave = false;
              this.routeDialog = false;
              if (res.code === Code.SUCCESS) {
                this.$message({message: Tips.INSERT_SUCCESS, type: 'success', center: true});
                this.getDBRoutesFun();
              } else {
                this.$message({message: res.message, type: 'error', center: true});
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
      handleSizeChange(pageSize){
        this.pageSize = pageSize;
        this.getDBLinksFun();
      },
      handleCurrentChange(page){
        this.page = page;
        this.getDBLinksFun();
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

