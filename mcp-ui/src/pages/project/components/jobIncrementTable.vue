<!--
-->
<template>
  <el-form-item class="mt20" style="width: 65%">
    <el-button class="mb10" @click="clickAddList">Add list</el-button>
    <el-table empty-text="No data" :data="incrementDbTable" max-height="300">
      <el-table-column label="DB" prop="dbValue">
        <template slot-scope="scope">
          <el-select
            filterable
            size="small"
            class="job-table-select"
            v-model="scope.row.dbValue"
            @change="changeIncrementDb($event, scope.row)"
            @visible-change="visibleChangeIncrementDb($event, scope.row.dbValue)"
            placeholder="Please select DB"
          >
            <el-option
              v-for="(item,index) in incrementDbList"
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
            multiple
            collapse-tags
            size="small"
            class="job-table-select"
            v-model="scope.row.tableValue"
            :disabled="scope.row.dbValue === 'All'"
            :placeholder="scope.row.dbValue === 'All' ? 'Have all' : 'Please select table'"
            @focus="getIncrementTable(scope.row)"
            @change="changeIncrementTable"
            @visible-change="visibleChangeIncrementTable($event, scope.row.tableValue, scope.row.dbValue)"
          >
            <el-option
              v-for="(item,index) in scope.row.incrementTableList"
              :label="item.label"
              :value="item.id"
              :key="index"
            ></el-option>
          </el-select>
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
  name: 'job-increment-table',
  props: {
    incrementData: {
      type: Array,
      default: () => {
        return {}
      }
    },
    incrementDbTable: {
      type: Array,
      default: () => {
        return []
      }
    },
    incrementDbList: {
      type: Array,
      default: () => {
        return []
      }
    },
    incrementDbListCopy: {
      type: Array,
      default: () => {
        return []
      }
    },
    dbOld: {
      type: String,
      default: ''
    }
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
    // increment table list add list
    clickAddList() {
      if (this.incrementData.route.length <= 0) return this.$message.warning('Please choose routing')
      if (this.incrementDbTable && this.incrementDbTable.length === 1 && this.incrementDbTable[0].dbValue === 'All') return this.$message.warning('Have choose all the db')
      let incrementTable = {
        id: this.incrementDbTable.length <= 0 ? 0 : this.incrementDbTable[0].id + 1,
        dbValue: '',
        tableValue: [],
        incrementTableList: []
      }
      this.incrementDbTable.unshift(incrementTable)
      console.log('lg-------id---', this.incrementDbTable)
    },
    // increment table list -> db select visible change 
    // 赋值db select 上一次value
    visibleChangeIncrementDb(event, val) {
      if (event) {
        this.dbOld = val
      }
    },
    // increment table list -> table select visible change
    visibleChangeIncrementTable(event, table, db) {

    },
    handleChangeIncrementDb1(val) {
      let row = this.incrementDbTable.find(function (row) { return row.dbValue === val })
      this.incrementDbTable = []
      this.incrementDbTable.push(row)
      this.incrementDbList = [...this.incrementDbListCopy.slice(1)]
      this.incrementDbList.sort(function (a, b) { return (a.id).localeCompare(b.id) })
    },
    // increment table list -> db select change 
    changeIncrementDb(val, rows) {
      if (val === 'All') {
        if (this.incrementDbTable.length <= 1) return this.handleChangeIncrementDb1(val)
        this.$confirm('You selected all the db, a move that will delete selected other db, whether or not to continue?', 'message', {
          confirmButtonText: 'confirm',
          cancelButtonText: 'cancel',
          type: 'warning'
        }).then(() => {
          // 删除其他table list row
          this.handleChangeIncrementDb1(val)
          return
        }).catch(() => {
          val = this.dbOld
          rows.dbValue = this.dbOld
        })
      } else {
        // 清除table
        if (this.dbOld !== val) {
          rows.tableValue = []
        }
        let index = -1
        this.incrementDbList.forEach((row, i) => {
          if (row.label === val) index = i
        })
        if (index !== -1) {
          this.incrementDbList.splice(index, 1)
          if (this.dbOld !== '') {
            this.incrementDbListCopy.forEach(items => {
              if (items.label === this.dbOld) {
                this.incrementDbList.push(items)
                this.incrementDbList.sort(function (a, b) { return (a.id).localeCompare(b.id) })
              }
            })
          }
        }
      }
    },
    // increment table list -> table select change 
    changeIncrementTable(arr) {
      let obj = arr[arr.length - 1]
      if (!obj || arr.length === 1) return
      if (obj === 'All') {
        // 删除其他
        arr.splice(0, arr.length - 1)
      } else {
        // 删除全部
        let j = arr.indexOf('All')
        if (j !== undefined && j !== -1) {
          arr.splice(j, 1)
        }
      }
    },
    // increment table list -> table focus
    getIncrementTable(row) {
      let item = this.incrementDbListCopy.find(function (item) { return item.id === row.dbValue })
      // 赋值table select
      let list = item.children || []

      this.incrementDbTable.forEach((items) => {
        if (items.id === row.id) {
          items.incrementTableList = list
        }
      })
    },
    // increment table list -> delete
    clickDeleteList(row) {
      for (const index in this.incrementDbTable) {
        if (row.id === this.incrementDbTable[index].id) {
          this.incrementDbTable.splice(index, 1)
          break
        }
      }
      if (row.dbValue && row.dbValue !== '') {
        this.incrementDbListCopy.forEach((items) => {
          if (items.label === row.dbValue) {
            this.incrementDbList.push(items)
            this.incrementDbList.sort(function (a, b) { return (a.id).localeCompare(b.id) })
          }
        })
      }
    },
    // 处理数据 incrementDbTable
    // 全选：srcDb = null;    srcTable = null;
    // 其他：srcDb：db1,db2;   srcTable：db1,t1,t2|db2,t3,t4
    handleIncrementDbTable(params) {
      let srcDb = ''
      let srcTable = ''
      this.$emit('emitJobIncrementTabel', this.incrementDbTable, this.incrementDbList, this.incrementDbListCopy, this.dbOld)

      for (let key in this.incrementDbTable) {
        let row = this.incrementDbTable[key]
        if (row.dbValue !== '') {
          // db all
          if (row.dbValue === 'All') {
            params.srcDb = null
            params.srcTable = null
            return params
          } else {// db no ALL
            srcDb = srcDb !== '' ? (srcDb + ',' + row.dbValue) : row.dbValue
            srcTable = srcTable !== '' ? (srcTable + '|' + row.dbValue) : row.dbValue

            // 已选择db 未选择 table
            if (row.tableValue.length <= 0) {
              this.$message.error('Select the db, which have not been selection table')
              return false
            }
            // table all
            if (row.tableValue[0] === 'All') {
              row.incrementTableList.forEach((item) => {
                if (item.label !== 'All') srcTable = srcTable + ',' + item.id
              })
            } else {// table no all
              srcTable = srcTable + ',' + row.tableValue.join(',')
            }
          }
        }
      }
      params.srcDb = srcDb
      params.srcTable = srcTable
      if (params.srcDb === '') {
        this.$message.error('Select at least one db')
        return false
      }
      return params
    },
  }
}
</script>
<style scoped lang="less">
.job-table-select {
  width: 80%;
}
</style>
