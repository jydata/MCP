<template>
  <section>

    <!-- header -->
    <div class="header">
      <div class="top clearfix">
        <div class="left">
          <i class="icon-p-service"></i>
          <span>Target Schema</span>
        </div>
        <div class="right">
          <el-button @click="addSchemaFun" :loading="loadingAdd">Add</el-button>
          <el-button @click="createSchemaFun" :loading="loadingCreate">Create Schema</el-button>
        </div>
      </div>
    </div>

    <!-- list -->
    <div class="table-master">
      <el-table ref="multipleTable" class="table-overflow-x-hidden" :data="dataList" border v-autoHeight  @cell-click="handleCellChange" @selection-change="handleSelectionChange">
        <el-table-column type="selection" class="selection" :selectable="checkboxInit" width="40" align="center"></el-table-column>
        <el-table-column prop="routeName" label="RouteName" align="center" width="210" :sortable=true></el-table-column>
        <el-table-column prop="targetName" label="Target" align="center" width="230" :sortable=true></el-table-column>
        <el-table-column prop="schemaName" label="SchemaName" align="center" show-overflow-tooltip :sortable=true>
          <template slot-scope="scope">
            <el-input size="small" v-model="scope.row.schemaName" @blur="handleEdit(scope.$index, scope.row)"
                      v-if="scope.row.isEdit"></el-input>
            <span v-else>{{scope.row.schemaName}}</span>
          </template>
        </el-table-column>
        <el-table-column label="Executed" align="center" width="120">
          <template slot-scope="scope">
            <span>{{scope.row.executeFlag === 0 ? 'No' : 'Yes'}}</span>
          </template>
        </el-table-column>
        <el-table-column width="120" label="Status" align="center">
          <template slot-scope="scope">
            <el-switch
              active-color="#3d82ca"
              inactive-color="#bbb"
              v-model="scope.row.deleteFlag"
              @change="changeSwitchFun(scope.$index,scope.row)"
              active-value="1"
              inactive-value="0">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="Create Time" align="center" width="160"></el-table-column>
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
              <el-button @click="updateDeleteFlagFun('connForm')" :loading="loadingEdit">Yes</el-button>
            </span>
    </el-dialog>

    <!-- add -->
    <el-dialog title="Add Schema information" :close-on-click-modal="false" :visible.sync="addLinkDialog" :show-close="false" width="33%" height="80%">
      <el-form label-width="160px" :model="connForm" :rules="rules" ref="connForm" class="item-error">
        <el-form-item label="Route Name：" prop="routeId">
          <el-select @change="showTargetInfoFun" v-model="connForm.routeId" filterable clearable
                     placeholder="Please select">
            <el-option
              v-for="item in dbRouteList"
              :key="item.routeId"
              :label="item.routeName"
              :value="item.routeId"
            >
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="Target Name：" prop="targetName">
          <el-input v-model="connForm.targetName" style="width: 75%" :disabled="true"></el-input>
        </el-form-item>

        <el-form-item label="Schema Name：" prop="schemaName">
          <el-input v-model="connForm.schemaName" style="width: 75%" clearable></el-input>
        </el-form-item>

        <el-form-item>
          <el-button @click="closeDialog('connForm')">Close</el-button>
          <el-button @click="saveSchemaFun('connForm')" :loading="loadingSave">Confirm</el-button>
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

  var reg = /^(?!_|[0-9])(?!.*?_$)[a-zA-Z0-9_]+$/;

  export default {
    data () {
      // 检查是否选择路由
      var checkRouteName = (rule, value, callback) => {
        if (this.connForm.routeId === '') {
          return callback(new Error('请先选择对应的路由信息!'));
        } else {
          callback();
        }
      };

      // 检查Schema名是否为空
      var checkSchemaName = (rule, value, callback) => {
        if (value === '' || value.trim().length <= 0) {
          return callback(new Error('Schema名不能为空!'));
        } else if (!reg.test(value)) {
          callback(new Error('Schema只包含字母、数字以及_,不以_和数字开头，不以_结束!'));
        } else {
          callback();
        }
      };

      return {
        page: page,
        pageSize: pageSize,
        pageSizes: pageSizes,
        total: 0,
        dataList: [],
        multipleSelection: [],
        dbRouteList: [],
        connForm: {
          schemaId: '',
          routeId: '',
          routeName: '',
          targetId: '',
          targetName: '',
          schemaName: ''
        },
        rules: {
          routeId: [
            {required: true, validator: checkRouteName, trigger: 'blur'}
          ],
          schemaName: [
            {required: true, validator: checkSchemaName, trigger: 'blur'},
            {min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur'}
          ]
        },
        loadingSearch: false,
        loadingAdd: false,
        loadingCreate: false,
        loadingSave: false,
        addLinkDialog: false,
        dialogVisible: false,
        loadingEdit: false,
        showMsg: ''
      }
    },
    created () {
      this.getSchemaListFun();
    },
    methods: {
      checkboxInit(row, index){
        if (row.executeFlag.toString() === "1" || row.deleteFlag === "0") {
          // 不可选
          return false;
        } else {
          return true;
        }
      },
      handleCellChange(row, column, cell, event){
        // 当为Schema列时才可以修改
        if (column.label == "SchemaName") {
          $.each(this.dataList, function (key, item) {
            item.isEdit = false;
          })

          // 只有是未创建且非删除的数据才可以修改
          if (row.executeFlag == 0 && row.deleteFlag == 1) {
            row.isEdit = true;

            this.$nextTick(function () {
              $(event.target).children().find("input").focus();
            })
          }
        }
      },
      handleEdit(index, row) {
        $.each(this.dataList, function (key, item) {
          item.isEdit = false;
        })

        // row.isEdit = false;
        if(!reg.test(row.schemaName)){
          this.getSchemaListFun();
          alert("Schema只包含字母、数字以及_,不以_和数字开头，不以_结束!");
        } else {
          // 编辑Schema信息
          let param = {
            schemaId: row.schemaId,
            routeId: row.routeId,
            routeName: row.routeName,
            schemaName: row.schemaName,
            flag: '2'
          }

          Metadata.saveSchemaInfo(param).then(res => {
            if (res.code === Code.SUCCESS) {
              this.getSchemaListFun();
              this.$message({message: Tips.UPDATE_SUCCESS, type: 'success', center: true});
            } else {
              this.getSchemaListFun();
              this.$message({message: res.message, type: 'error', center: true});
            }
          }).catch(() => {
            this.getSchemaListFun();
            this.$message({message: Tips.UPDATE_ERROR, type: 'error', center: true});
          })
        }
      },
      changeSwitchFun(index, row) {
        this.dialogVisible = true;

        this.connForm.schemaId = row.schemaId;
        this.connForm.routeId = row.routeId;
        this.connForm.schemaName = row.schemaName;
        this.connForm.executeFlag = row.executeFlag;
        this.connForm.deleteFlag = row.deleteFlag;
        if (row.deleteFlag == "0") {
          this.showMsg = '禁用后将无法正常访问，是否确认禁用？';
        } else {
          this.showMsg = '是否确认启用？';
        }
      },
      closeDialogFun(){
        this.dialogVisible = false;

        if (this.connForm.deleteFlag == "0") {
          this.connForm.deleteFlag == "1";
        } else {
          this.connForm.deleteFlag == "0";
        }

        this.getSchemaListFun();
      },
      updateDeleteFlagFun(){
        this.loadingEdit = true;

        let param = {
          schemaId: this.connForm.schemaId,
          routeId: this.connForm.routeId,
          schemaName: this.connForm.schemaName,
          executeFlag: this.connForm.executeFlag,
          deleteFlag: this.connForm.deleteFlag,
          flag: '3'
        }

        Metadata.saveSchemaInfo(param).then(res => {
          if (res.code === Code.SUCCESS) {
            this.loadingEdit = false;
            this.dialogVisible = false;
            this.$message({message: Tips.SAVE_SUCCESS, type: 'success', center: true});
            this.getSchemaListFun();
          } else {
            this.loadingEdit = false;
            this.$message({message: res.message, type: 'error', center: true});
            this.getSchemaListFun();
          }
        }).catch(() => {
          this.loadingEdit = false;
        })
      },
      handleCloseFun(){
        this.dialogVisible = false;
      },
      handleSelectionChange(val) {
        this.multipleSelection = val;
      },
      getSchemaListFun(){
        Metadata.getSchemaList({
          param: {
            pageSize: this.pageSize,
            currentPage: this.page
          }
        }).then(res => {
          if (res.code === Code.SUCCESS) {
            this.dataList = res.items.list;
            this.total = res.items.total;
          } else {
            this.$message({message: res.message, type: 'error', center: true});
          }
        })
      },
      getRouteList(){
        Metadata.getDBRoutes({
          param: {
            pageSize: this.pageSize,
            currentPage: this.page
          },
          info: {flag: '2'}
        }).then(res => {
          if (res.code === Code.SUCCESS) {
            this.dbRouteList = res.items.list;
          } else {
            this.$message({message: res.message, type: 'error', center: true});
          }
        })
      },
      addSchemaFun(){
        this.addLinkDialog = true;

        this.connForm.routeName = '';
        this.connForm.routeId = '';
        this.connForm.targetName = '';
        this.connForm.schemaName = '';

        this.getRouteList();
      },
      showTargetInfoFun(val){
        let obj = {};
        obj = this.dbRouteList.find((item) => {
          return item.routeId === val;
        });

        if (obj != "" && obj != undefined) {
          this.connForm.routeName = obj.routeName;

          Metadata.getDBRouteInfo({
            param: {
              routeId: this.connForm.routeId
            }
          }).then(res => {
            if (res.code === Code.SUCCESS) {
              this.connForm.targetId = res.items.targetId;
              this.connForm.targetName = res.items.targetName;
            } else {
              this.$message({message: res.message, type: 'error', center: true});
            }
          })
        }
      },
      saveSchemaFun(formName){
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.loadingSave = true;

            let param = {
              schemaId: this.connForm.schemaId,
              routeId: this.connForm.routeId,
              routeName: this.connForm.routeName,
              targetId: this.connForm.targetId,
              targetName: this.connForm.targetName,
              schemaName: this.connForm.schemaName,
              flag: '1'
            }

            Metadata.saveSchemaInfo(param).then(res => {
              if (res.code === Code.SUCCESS) {
                this.loadingSave = false;
                this.addLinkDialog = false;
                this.$message({message: Tips.SAVE_SUCCESS, type: 'success', center: true});
                this.getSchemaListFun();
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
      createSchemaFun(){
        if (this.multipleSelection.length < 1) {
          alert("Please select the data you want to execute.");
        } else {
          this.loadingCreate = true;
          let param = this.multipleSelection
          Metadata.createSchemaInfo(param).then(res => {
            if (res.code === Code.SUCCESS) {
              this.loadingCreate = false;
              this.$message({message: Tips.CREATE_SUCCESS, type: 'success', center: true});
              this.getSchemaListFun()
            } else {
              this.loadingCreate = false;
              this.$message({message: Tips.CREATE_FAILED, type: 'error', center: true});
            }
          })
        }
      },
      closeDialog(formName){
        this.dbRouteList = '';
        this.connForm.routeName = '';
        this.connForm.targetName = '';
        this.connForm.schemaName = '';

        this.addLinkDialog = false;
        this.$refs[formName].clearValidate();
      },
      handleSizeChange(pageSize){
        this.pageSize = pageSize;
        this.getSchemaListFun();
      },
      handleCurrentChange(page){
        this.page = page;
        this.getSchemaListFun();
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

