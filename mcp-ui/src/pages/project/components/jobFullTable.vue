<!--
-->
<template>
  <el-form-item class="mt20" style="width: 100%">
    <el-button class="mb10" @click="clickAddList">Add list</el-button>
    <el-table empty-text="No data" :data="fullDbTable" max-height="300">
      <el-table-column label="DB" prop="dbValue">
        <template slot-scope="scope">
          <el-select
            filterable
            size="small"
            class="job-table-select"
            v-model="scope.row.dbValue"
            @change="changefullDb($event, scope.row)"
            @visible-change="visibleChangefullDb($event, scope.row.dbValue)"
            placeholder="Please select DB"
          >
            <el-option
              v-for="(item,index) in fullDbList"
              :label="item.label"
              :value="item.id"
              :key="index"
            ></el-option>
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="Table" prop="tableValue">
        <template slot-scope="scope">
          <el-select
            filterable
            size="small"
            class="job-table-select"
            v-model="scope.row.tableValue"
            @focus="getfullTable(scope.row)"
            @change="changefullTable($event, scope.row)"
            @visible-change="visibleChangefullTable($event, scope.row.tableValue)"
            placeholder="Please select Table"
          >
            <el-option
              v-for="(item,index) in scope.row.fullTableList"
              :label="item.label"
              :value="item.label"
              :key="index"
              :disabled="item.isRepeat"
            ></el-option>
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="Column" prop="columnValue" width="630">
        <template slot-scope="scope">
          <el-select
            filterable
            size="small"
            v-model="scope.row.columnValue"
            @focus="getfullColumn(scope.row)"
            @change="changefullColumn($event, scope.row)"
            @visible-change="visibleChangefullColumn($event, scope.row.columnValue)"
            placeholder="Please select Column"
          >
            <el-option
              v-for="(item,index) in scope.row.fullColumnList"
              :label="item.label"
              :value="item.label"
              :key="index"
            ></el-option>
          </el-select>
          <el-date-picker
            size="small"
            v-model="scope.row.syncDuration"
            type="datetimerange"
            unlink-panels
            start-placeholder="Start Date"
            end-placeholder="End Date"
          ></el-date-picker>
        </template>
      </el-table-column>
      <el-table-column label="Action" width="90">
        <template slot-scope="scope">
          <i class="icon-trash" @click="clickDeleteList(scope.row)"></i>
        </template>
      </el-table-column>
    </el-table>
  </el-form-item>
</template>

<script>

export default {
  name: 'job-full-table',
  props: {
    fullData: {
      type: Array,
      default: () => {
        return []
      }
    },
    fullDbTable: {
      type: Array,
      default: () => {
        return []
      }
    },
    fullDbList: {
      type: Array,
      default: () => {
        return []
      }
    },
    dbOld: {
      type: String,
      default: ''
    },
    tableOld: {
      type: String,
      default: ''
    }
  },
  watch: {

  },
  data() {
    return {

    }
  },
  created() {
  },
  mounted() {
  },
  methods: {
    // full table list add list
    clickAddList() {
      if (this.fullData.route.length <= 0) return this.$message.warning('Please choose routing')
      let fullTable = {
        id: this.fullDbTable.length <= 0 ? 0 : this.fullDbTable[0].id + 1,
        dbValue: '', // id 
        tableValue: '', // label
        columnValue: '', // label
        syncDuration: this.fullDbTable.length > 0 ? this.fullDbTable[0].syncDuration : '',
        fullTableList: [],
        fullColumnList: [],
      }
      this.fullDbTable.unshift(fullTable)
      console.log('lg-------id---', this.fullDbTable)
    },
    // full table list -> db select visible change 
    visibleChangefullDb(event, val) {
      if (event) {
        this.dbOld = val
      }
    },
    // full table list -> table select visible change
    visibleChangefullTable(event, val) {
      if (event) {
        this.tableOld = val // val 是label
      }
    },
    // full table list -> column select visible change
    visibleChangefullColumn(event, val) {

    },

    // full table list -> db select change 
    changefullDb(val, rows) {
      // 清除table column
      if (this.dbOld !== val) {
        rows.tableValue = ''
        rows.columnValue = ''
        // table赋值
        let list = []
        this.fullDbList.forEach((item, i) => {
          if (item.id === val) {
            // 赋值table select
            list = item.children ? [...item.children] : []
          }
        })
        rows.fullTableList = list
      }
    },
    // full table list -> table select change 
    changefullTable(val, rows) {
      // val 是label
      // 清除column
      if (this.dbOld !== val) {
        rows.columnValue = ''
      }
      // // 判断当前val是否已经选择
      // this.fullDbTable.forEach((items) => {
      //   // 已选，重复db+table
      //   if (items.id !== rows.id && rows.dbValue + rows.tableValue === items.dbValue + items.tableValue) {
      //     this.$message.warning(val + ' table in the ' + rows.dbValue + ' has been selected')
      //     val = this.tableOld
      //     rows.tableValue = this.tableOld
      //   }
      // })
    },
    // full table list -> column select change 
    changefullColumn(val, rows) {

    },

    // full table list -> table focus
    getfullTable(row) {
      let list = []
      this.fullDbList.forEach((item, i) => {
        if (item.id === row.dbValue) {
          // 赋值table select
          list = item.children ? [...item.children] : []
        }
      })

      // 剔除已选
      for (let i = list.length - 1; i >= 0; i--) {
        let flag = false
        this.fullDbTable.forEach((items) => {
          // tableValue 是label
          // lg-添加校验：排除当前row, 校验相同dbvalue
          if (row.id !== items.id && row.dbValue === items.dbValue && items.tableValue === list[i].label) flag = true
        })
        if (flag) list.splice(i, 1)
      }

      this.fullDbTable.forEach((items) => {
        if (items.id === row.id) {
          items.fullTableList = list
        }
      })
    },
    // full table list -> column focus
    getfullColumn(row) {
      let list = []
      for (let keyi in this.fullDbList) {
        let obj1 = this.fullDbList[keyi]
        if (obj1.id === row.dbValue) {
          for (let keyj in obj1.children) {
            let obj2 = obj1.children[keyj]
            // tableValue 是label
            if (obj2.label === row.tableValue) {
              // 赋值table select
              list = obj2.children ? [...obj2.children] : []
              break
            }
          }
        }
      }
      this.fullDbTable.forEach((items) => {
        if (items.id === row.id) {
          items.fullColumnList = list
        }
      })
    },

    // full table list -> delete
    clickDeleteList(row) {
      for (const index in this.fullDbTable) {
        if (row.id === this.fullDbTable[index].id) this.fullDbTable.splice(index, 1)
      }
    },
    // 处理数据 fullDbTable
    // 全选：srcDb = null;    srcTable = null;
    // 其他：srcDb：db1,db2;   srcTable：db1,t1,t2|db2,t3,t4
    handleFullDbTable() {
      let srcDb = ''
      let srcTable = ''
      this.$emit('emitJobFullTabel', this.fullDbTable, this.fullDbList, this.dbOld, this.tableOld)
      let syncList = []
      if (this.fullDbTable && this.fullDbTable.length <= 0) {
        this.$message.error('Select at least one db')
        return false
      }
      for (let key in this.fullDbTable) {
        let items = this.fullDbTable[key]
        // 判断db 选中时table是否选中
        if (items.dbValue !== '' && items.tableValue === '' && items.fullTableList.length > 0) {
          this.$message.error('Select the db, which have not been selection table')
          return false
        }
        if (items.dbValue !== '' && items.tableValue !== '') {
          // 获取table id
          let tableRow = items.fullTableList.find(function (table) { return table.label === items.tableValue })
          let tableLabel = tableRow.id.split("#")[0] || ''
          // 时间转换
          let beginTime = ''
          let endTime = ''
          if (items.columnValue && items.syncDuration && items.syncDuration[0] &&  items.syncDuration[0] != 'Invalid date') {
            beginTime = this.$moment(items.syncDuration[0]).format('YYYY-MM-DD HH:mm:ss')
            endTime = this.$moment(items.syncDuration[1]).format('YYYY-MM-DD HH:mm:ss')
          }else if (items.columnValue !== ''){
            this.$message.error('Select the Column, which have not been selection time')
            return false
          }
          // save run params
          let sync = {
            ddlRuleId: tableLabel,
            dbName: items.dbValue,
            tableName: items.tableValue,
            columnName: items.columnValue,
            beginTime: beginTime,
            endTime: endTime,
          }
          syncList.push(sync)
        }
      }
      return syncList
    },
  }
}
</script>
<style scoped lang="less">
.job-table-select {
  width: 80%;
}
</style>
