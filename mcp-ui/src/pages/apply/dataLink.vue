<template>
  <section>
    <div class="header">
      <div class="top clearfix">
        <div class="left">
          <i class="icon-p-service"></i>
          <span>数据连接</span>
        </div>
        <div class="right">
          <el-button @click="addLinkDialog = !addLinkDialog" :disabled="loading">新增</el-button>
        </div>
      </div>
      <div class="down">
        <div class="list clearfix">
          <div class="press">
            <span class="key">服务器IP</span>
            <el-input style="width: 150px;"></el-input>
          </div>
          <div class="press">
            <el-button :disabled="loading" class="el-button-search">
              <i class="icon-search"></i>查询
            </el-button>
          </div>
        </div>
      </div>
    </div>
    <!--列表显示-->
    <div class="table-master">
      <el-table
        class="table-overflow-x-hidden"
        :data="dataLink"
        border
        v-autoHeight>
        <el-table-column type="index" label="序号" width="50"></el-table-column>
        <el-table-column prop="linkName" label="连接名" width="100"></el-table-column>
        <el-table-column prop="url" label="服务器地址" show-overflow-tooltip></el-table-column>
        <el-table-column prop="ip" label="服务器IP" width="120" show-overflow-tooltip></el-table-column>
        <el-table-column prop="port" label="端口号" width="80"></el-table-column>
        <el-table-column prop="dbName" label="数据库名称" width="100"></el-table-column>
        <el-table-column prop="driver" label="数据库驱动" width="200"></el-table-column>
        <el-table-column prop="linkParam" label="连接参数" width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="userName" label="用户名" width="120"></el-table-column>
      </el-table>
      <el-pagination
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :page-sizes="pageSizes"
        :page-size="pageSize"
        :current-page="page"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </div>
    <!--新增弹框-->
    <el-dialog title="新增数据库连接配置" :visible.sync="addLinkDialog" width="30%">
      <el-form label-width="100px" :model="addDataLink">
        <el-form-item label="连接名">
          <el-input v-model="addDataLink.linkName"></el-input>
        </el-form-item>
        <el-form-item label="数据库驱动">
          <el-input v-model="addDataLink.driver"></el-input>
        </el-form-item>
        <el-form-item label="url">
          <el-input v-model="addDataLink.url"></el-input>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="addDataLink.userName"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="addDataLink.password"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button @click="addLinkDialog=!addLinkDialog">取消</el-button>
          <el-button @click="testInsertLinkFun">测试连接</el-button>
          <el-button @click="insertLinkFun">确定</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </section>
</template>
<script>
  import {page, pageSize, pageSizes} from '@config/config';
  import * as Code from '@config/code';
  import * as Tips from '@config/tips';
  import * as Apply from '@api/Apply';
  export default {
    name: 'dataLink',
    components: {},
    data () {
      return {
        page: page,
        pageSize: pageSize,
        pageSizes: pageSizes,
        total: 0,
        loading: false,
        addLinkDialog: false,
        dataLink: [],
        addDataLink: {
          linkName: '',
          driver: '',
          url: '',
          userName: '',
          password: ''
        }
      }
    },
    created () {
      this.getDataLink();
    },
    methods: {
      getDataLink(){
        this.loading = true;
        Apply.queryDataLink({
          param: {
            pageSize: this.pageSize,
            currentPage: this.page
          }
        }).then(res => {
          this.loading = false;
          if (res.code * 1 === Code.SUCCESS) {
            this.dataLink = res.data.list;
            this.total = res.data.total;
          } else {
            this.$message({message: res.message, type: 'error'});
          }
        }).catch(() => {
          this.loading = false;
        })
      },
      testInsertLinkFun(){
        this.loading = true;
        Apply.testDataLink({
          dbLink: this.addDataLink
        }).then(res => {
          this.loading = false;
          if (res.code * 1 === Code.SUCCESS && res.data.flag) {
            this.addLinkDialog = false;
            this.$message({message: Tips.UPDATE_SUCCESS, type: 'success'});
          } else {
            this.$message({message: res.data.message, type: 'error'});
          }
        }).catch(() => {
          this.loading = false;
        })
      },
      insertLinkFun(){
        this.loading = true;
        Apply.insertDataLink({
          dbLink: this.addDataLink
        }).then(res => {
          this.loading = false;
          if (res.code * 1 === Code.SUCCESS && res.data.flag) {
            this.addLinkDialog = false;
            this.$message({message: Tips.UPDATE_SUCCESS, type: 'success'});
          } else {
            this.$message({message: res.data.message, type: 'error'});
          }
        }).catch(() => {
          this.loading = false;
        })
      },
      handleSizeChange(pageSize){
        this.pageSize = pageSize;
        this.getDataLink();
      },
      handleCurrentChange(page){
        this.page = page;
        this.getDataLink();
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

<style lang="less">

</style>
