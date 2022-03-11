<template>
  <section>
    <!--头部-->
    <div class="header">
      <div class="title">
        <el-breadcrumb separator-class="el-icon-arrow-right">
          <el-breadcrumb-item to="/meta/DSConnections">DS Connections</el-breadcrumb-item>
          <el-breadcrumb-item>Synchronize Source Metadata</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
    </div>

    <!--内容-->
    <div class="container gray mt0">
      <div class="left shadow" element-loading-spinner="el-icon-loading" v-loading="treeLoading">
        <el-input class="search-input" prefix-icon="el-icon-search" placeholder="Search schema or table" v-model="filterText"></el-input>
        <el-tree
          :style="{maxHeight: treeHeight + 'px'}"
          class="pd10 mcp-rollbar"
          :data="schemaList"
          :props="defaultProps"
          ref="tree"
          show-checkbox
          node-key="id"
          @check="treeCheck"
          v-if="treeIsShow"
          :default-checked-keys="treeDefaultKeys"
          empty-text="No data"
          :filter-node-method="filterNode"
          @node-click="getMetadata">
        </el-tree>
      </div>
      <div class="right">
        <div>
          <ul>
            <li class="shadow" style="width: 32%;height: 110px;">
              <img class="mr30" src="../../assets/img/MySQL.png">
              <span>{{this.$route.query.sourceName}}</span>
              <i :class="hourglassIcon" style="height:10px;line-height: 70px" :title="hourglassTitle"></i>
            </li>
            <li class="shadow dflex" style="width:17%;height: 110px;">
              <span>Schemas</span>
              <span class="number"><i class="el-icon-loading" v-show="countLoading"></i> {{schemas}}</span>
            </li>
            <li class="shadow dflex" style="width: 17%;height: 110px;">
              <span>Tables</span>
              <span class="number"><i class="el-icon-loading" v-show="countLoading"></i> {{tables}}</span>
            </li>
            <li class="shadow dflex" style="width: 17%;height: 110px;">
              <span>Max Table Rows</span>
              <span class="number"><i class="el-icon-loading" v-show="countLoading"></i> {{maxRows}}</span>
            </li>
            <li class="shadow dflex" style="width: 17%;height: 110px;">
              <span>Total Rows</span>
              <span class="number"><i class="el-icon-loading" v-show="countLoading"></i> {{totalRows}}</span>
            </li>
          </ul>
        </div>
        <div class="tabs">
          <div style="display: flex;justify-content: space-between;">
            <ul>
              <li class="shadow"
                  style="width:90px;height: 30px"
                  :style="bgMeta"
                  @click="showMetadata">Metadata</li>
              <li class="shadow"
                  style="width:90px;height: 30px"
                  :style="bgSample"
                  @click="showSample">Sample Data</li>
              <li class="shadow"
                  style="width:90px;height: 30px"
                  :style="bgDDL"
                  @click="showDDL">DDL</li>
            </ul>
            <span>
              <el-button @click="createJob" style="margin-top: 18px">Create Job</el-button>
            </span>
          </div>
          <div class="tabs-content" style="margin-top: -10px">
            <!--Metadata-->
            <div class="meta-content" v-show="metaTable">
              <el-table class="shadow" empty-text="No data" :data="tableList">
                <el-table-column label="Schema" prop="tableSchema" ></el-table-column>
                <el-table-column label="Table Name" prop="tableName"></el-table-column>
                <el-table-column label="Engine" prop="engine"></el-table-column>
                <el-table-column label="Charset" prop="tableType"></el-table-column>
                <el-table-column label="Table Comment" prop="tableComment" ></el-table-column>
                <el-table-column label="Table Rows" prop="tableRows" ></el-table-column>
                <el-table-column label="Status" prop="syncFlag" align="center">
                  <template slot-scope="scope">
                    <div v-if="scope.row.syncFlag == 1">Synchronized</div>
                    <div v-if="scope.row.syncFlag == 0">Unsynchronized</div>
                  </template>
                </el-table-column>
              </el-table>

              <el-table class="shadow" empty-text="No data" :data="columnList">
                <el-table-column label="Column" prop="columnName"></el-table-column>
                <el-table-column label="#" type="index"></el-table-column>
                <el-table-column label="Data Type" prop="columnType"></el-table-column>
                <el-table-column label="Not Null" prop="isNullable" align="center">
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

            <!--Sample-->
            <div class="sample-content" v-show="sampleTable">
              <el-table class="shadow" empty-text="No data" :data="sampleList">
                <el-table-column  v-for="item in this.columnName" :label="item" :key="item" :prop="item"
                                  style="white-space: nowrap" :render-header="labelHead"  show-overflow-tooltip></el-table-column>
              </el-table>
            </div>

            <!--DDL-->
            <div class="ddl-content" v-show="ddlTable">
              <div  v-highlight>
                 <pre>
                   <code v-html="tableDDL">
                   </code>
                 </pre>
              </div>
            </div>

          </div>
        </div>
      </div>
    </div>

    <!--提示框-->
    <el-dialog title="Message" :visible.sync="dialogTip">
      <span v-if="syncAll">Are you confirm synchronize All?</span>
      <span v-else>Are you confirm synchronize?</span>
    </el-dialog>
    <el-dialog title="Message" :visible.sync="dialogNode">
        <span>Please select the source database or table to be synchronized!</span>
    </el-dialog>
  </section>
</template>

<script>
  import * as Meta from '@api/Meta';
  import * as Code from '@config/code';
  import * as Tips from '@config/tips';
  export default {
    name: "DSMetadataSync",
    data(){
          return{
            // tree height
            treeHeight: document.body.clientHeight - 225,
            // tree
            treeDefaultKeys: [], // 默认选中项
            treeIsShow: false,  // tree 是否展示
            treeSelectKeys: [], // 默认选中数组

            countLoading:false,
            bgMeta : 'background-color:#2c6daf;color:#fff',
            bgSample: '',
            bgDDL: '',
            metaTable: true,
            sampleTable: false,
            ddlTable: false,

            schemaList:[],
            defaultProps: {
              children: 'children',
              label: 'label',
            },
            columnName:[],
            countList: {},
            tableList: [],
            columnList: [],
            sampleList:[],
            tableDDL: '',
            syncStatus: '',

            filterText: '',
            dialogTip: false,
            dialogNode: false,
            syncAll: '',
            hourglassIcon: '',
            hourglassTitle:'',

            schemas: '',
            tables: '',
            maxRows: '',
            totalRows: '',
            checked: []
          };
      },
    watch: {
      filterText(val) {
        this.$refs.tree.filter(val);
      }
    },
    created(){
      this.getTree();
      this.getCount();
      this.changeStatus();
    },
    methods: {
      labelHead(h,{column,index}){
        let l = column.label.length
        let f = 16
        column.minWidth = f*l+20
        return h('div',{class:'table-head',style:{width:'100%'}},[column.label])
      },
      //过滤关键字
      filterNode(value, data) {
        if (!value) return true;
        return data.label.indexOf(value) !== -1;
      },

      //动态沙漏
      changeStatus(){
        Meta.syncRatio({},this.$route.query.sourceId).then(res => {
          if (res.code === Code.SUCCESS) {
            this.syncStatus = res.items;
            if(this.syncStatus === 1){
              this.hourglassIcon = 'icon-hourglass-2';
              this.hourglassTitle = 'partial Synchronized'
            }else if(this.syncStatus === 2){
              this.hourglassIcon = 'icon-hourglass-3';
              this.hourglassTitle = 'Synchronized'
            }else{
              this.hourglassIcon = 'icon-hourglass-1';
              this.hourglassTitle = 'Unsynchronized'
            }
          } else {
            this.$message({message: res.message, type: 'error'});
          }
        }).catch(() => {
        })
      },

      // 获取tree data 的label
      handleTreeSelectData(list) {
        list.forEach((items) => {  
          const pushTreeSelectKeys = (row, rows) => {
            row.isSelect = true
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
      //查询树
      getTree(){
        this.treeLoading=true;
        let query = this.$route.query
        Meta.getDBTree({
          datasourceType: query.datasourceType,
          datasourceId: query.datasourceId,
          datasourceChoice:query.datasourceChoice,
          linkName: query.sourceName,
          ip: query.ip,
          port: query.port,
          dbName: query.dbName,
          username: query.username,
          password: query.password,
          linkId: query.linkId
        }).then(res => {
          if (res.code === Code.SUCCESS) {
            this.schemaList.push(res.items)
            this.treeDefaultKeys = []
            this.treeDefaultKeys.push(res.items.id || '')
            this.treeIsShow = true
            // 默认选中数组
            this.treeSelectKeys = this.schemaList
            this.handleTreeSelectData(this.treeSelectKeys)
          } else {
            this.$message({message: res.message, type: 'error'});
          }
          this.treeLoading=false;
        }).catch(() => {
          this.treeLoading=false;
          location.href = '#/console/dashboard'
        })
      },
      getCount(){
        let query = this.$route.query
        let params = {
          datasourceType: query.datasourceType,
          datasourceId: query.datasourceId,
          datasourceChoice: query.datasourceChoice,
          linkName: query.sourceName,
          ip: query.ip,
          port: query.port,
          dbName: query.dbName,
          username: query.username,
          password: query.password
        };
        this.countLoading=true;
        Meta.getCount(params).then(res => {
          if (res.code === Code.SUCCESS) {
            let countList = res.items;
            this.schemas = this.formatCount(countList.schemaCount)
            this.tables = this.formatCount(countList.tableCount)
            this.maxRows = this.formatCount(countList.maxRows)
            this.totalRows = this.formatCount(countList.totalRows)
          } else {
            this.$message({message: res.message, type: 'error'});
          }
          this.countLoading=false;
        }).catch(() => {
          this.countLoading=false;
        })
      },
      //三位逗号分隔
      formatCount(num){
        if(num.length > 3){
          let a = num.length%3;
          if(a == 0){
            num=num.match(/\d{3}/g).join(",");
          }else{
            num=num.slice(0,a)+','+num.slice(a,num.length).match(/\d{3}/g).join(",")
          }
        }
        return num
      },
      treeCheck(data, node) {
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
        console.log('lg----1--', this.treeSelectKeys)
      },

      // tree checkbox点击时第一级
      handleTreeCheck1 () {
        this.treeSelectKeys.forEach((items1) => {
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
        this.treeSelectKeys.forEach((items1) => {
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

        this.treeSelectKeys.forEach((items1) => {
          items1.children.forEach((items2, i2) => { 
            // 查找当前第二级faId
            if (items2.id === data.faId){
              items2.children.forEach((items3, i3) => {
                // 查找当前第三级label
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

      getMetadata(data,node){
        let query = this.$route.query
        if(node.level == 3){
          this.checked.push(node.parent.data.id)
        }else{
          this.checked.push(node.data.id)
        }
        if(data.isleaf){
          let params = {
            datasourceType: query.datasourceType,
            datasourceId: query.datasourceId,
            datasourceChoice: query.datasourceChoice,
            linkId: query.sourceId,
            linkName: query.sourceName,
            ip: query.ip,
            port: query.port,
            dbName: query.dbName,
            username: query.username,
            password: query.password,
          };
          let dbName = node.parent.label;
          let tableName = data.label;
          let dataList = [dbName,tableName]
          Meta.getMetaTable(params,dataList.toString()).then(res => {
            if (res.code === Code.SUCCESS) {
              this.tableList = res.items;
            } else {
              this.$message({message: res.message, type: 'error'});
            }
          }).catch(() => {
          })
          Meta.getMetaColumn(params,dataList.toString()).then(res => {
            if (res.code === Code.SUCCESS) {
              this.columnList = res.items;
              this.columnName = [];
              for(let list of res.items){
                this.columnName.push(list.columnName)
              }
            } else {
              this.$message({message: res.message, type: 'error'});
            }
          }).catch(() => {
          })
          Meta.getTableDDL(params,dataList.toString()).then(res => {
            if (res.code === Code.SUCCESS) {
              this.tableDDL = res.items;
            } else {
              this.$message({message: res.message, type: 'error'});
            }
          }).catch(() => {
          })
          Meta.getSample(params,dataList.toString()).then(res => {
            if (res.code === Code.SUCCESS) {
              this.sampleList = res.items;
            } else {
              this.$message({message: res.message, type: 'error'});
            }
          }).catch(() => {
          })
        }
      },

      //创建Job
      createJob(){
        // 判读是否有选中tree
        let keys = this.$refs.tree.getCheckedKeys() || []

        if(keys.length <= 0){
          this.dialogNode = true;
        }else{
          let checkedId = Array.from(new Set(this.checked))
          this.$router.push({
            path: '/project/NewJob',
            query:{
              origin : 'metadata',
              schemaList: JSON.stringify(this.schemaList),
              checkedId: checkedId,
              defaultSelect: keys
            }
          });
        }
      },

      //tabs显示对应模块
      showMetadata(){
          this.metaTable = true;
          this.sampleTable = false;
          this.ddlTable = false;
          this.bgMeta = 'background-color:#2c6daf;color:#fff';
          this.bgSample = '';
          this.bgDDL = '';
        },
      showSample(){
          this.sampleTable = true;
          this.metaTable = false;
          this.ddlTable = false;
          this.bgSample = 'background-color:#2c6daf;color:#fff';
          this.bgMeta = '';
          this.bgDDL = '';
        },
      showDDL(){
          this.ddlTable = true;
          this.metaTable = false;
          this.sampleTable = false;
          this.bgDDL = 'background-color:#2c6daf;color:#fff';
          this.bgMeta = '';
          this.bgSample = '';
        },
    }
  }
</script>

<style lang="less" scoped>
  .container{height: auto;display: flex;
    .left{
      width: 20%;
      .el-tree{
        max-height: 560px;overflow-y: auto;
      }
    }

    .right{
      margin-left: 20px;
      width: 70%;
      
      ul{display: flex;
      li{display: flex;justify-content: space-around;align-items: center;padding: 6px;}

    }
      .tabs{ul>li{cursor: pointer;&:hover{color:#2c6daf}}}
    }
  }

  .number{color: #5f8fc1;font-size: 18px;}
  .el-table{margin-left: 0}
  .dflex{display: flex;flex-direction: column;line-height: 50px}
</style>
