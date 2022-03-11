<template>
  <section>
    <!--头部-->
    <div class="header">
      <div class="title">
        <el-breadcrumb separator-class="el-icon-arrow-right">
          <el-breadcrumb-item>Home</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
    </div>

    <div class="container">
      <div class="top-wrapper">
        <div class="left relative">
          <div class="canvas-box flex fxcolumn">
            <h2 class="center">Job <i v-show="timeout.jobTitle" class="el-icon-loading"></i></h2>
            <div class="flex1" ref="job"></div>
          </div>
          <div v-show="timeout.job" class="chart-loading middle">
            <div class="middle">
              <p><i class="el-icon-loading"></i></p>
              <p>loading...</p>
            </div>
          </div>
        </div>
        <div class="right relative">
          <div class="canvas-box flex fxcolumn">
            <h2 class="center">Sync data streaming <i v-show="timeout.syncTitle" class="el-icon-loading"></i></h2>
            <div class="flex1" ref="dataStream"></div>
          </div>
          <div v-show="timeout.sync" class="chart-loading middle">
            <div class="middle">
              <p><i class="el-icon-loading"></i></p>
              <p>loading...</p>
            </div>
          </div>
        </div>
      </div>
      <div class="mid-wrapper">
        <div class="left relative">
          <div class="canvas-box flex fxcolumn">
            <h2 class="center">Sync agent data streaming <i v-show="timeout.techTitle" class="el-icon-loading"></i></h2>
            <dash-echarts-line :chartData="syncAgentLine"></dash-echarts-line>
          </div>
          <div v-show="timeout.syncAgentLine" class="chart-loading middle">
            <div class="middle">
              <p><i class="el-icon-loading"></i></p>
              <p>loading...</p>
            </div>
          </div>
        </div>
        <div class="right relative">
          <div class="canvas-box flex fxcolumn">
            <h2 class="center">Sync job data streaming <i v-show="timeout.techTitle" class="el-icon-loading"></i></h2>
            <dash-echarts-line :chartData="syncJobLine"></dash-echarts-line>
          </div>
          <div v-show="timeout.syncJobLine" class="chart-loading middle">
            <div class="middle">
              <p><i class="el-icon-loading"></i></p>
              <p>loading...</p>
            </div>
          </div>
        </div>
      </div>
      <div class="mid-wrapper">
        <div class="left relative">
          <div class="canvas-box flex fxcolumn">
            <h2 class="center">Tech metadata <i v-show="timeout.techTitle" class="el-icon-loading"></i></h2>
            <div class="flex1" ref="techMetadata"></div>
          </div>
          <div v-show="timeout.tech" class="chart-loading middle">
            <div class="middle">
              <p><i class="el-icon-loading"></i></p>
              <p>loading...</p>
            </div>
          </div>
        </div>
        <div ref="bizMetadata" class="right"></div>
      </div>
      <div class="mid-wrapper">
        <div class="left relative">
          <div class="canvas-box flex fxcolumn">
            <h2 class="center">Agent Log Error Monitor <i v-show="timeout.agentTbale" class="el-icon-loading"></i></h2>
            <el-table class="shadow dash-agent-table" empty-text="No data" :data="agentTable" v-loading="timeout.agentTbale" element-loading-text="loading...">
              <el-table-column label="Agent" prop="agentName" ></el-table-column>
              <el-table-column label="T" prop="todayError"></el-table-column>
              <el-table-column label="T-1" prop="lastdayError"></el-table-column>
              <el-table-column label="T-7" prop="lastweekError"></el-table-column>
            </el-table>
          </div>
        </div>
        <div class="right relative">
          <div class="canvas-box flex fxcolumn">
            <h2 class="center">MySQL To Phoenix (Table counts) <i v-show="timeout.mysqlEchartsBar" class="el-icon-loading"></i></h2>
            <dash-echarts-bar :chartData="mysqlChartData"></dash-echarts-bar>
          </div>
          <div v-show="timeout.mysqlEchartsBar" class="chart-loading middle">
            <div class="middle">
              <p><i class="el-icon-loading"></i></p>
              <p>loading...</p>
            </div>
          </div>
        </div>
      </div>
      <div class="mid-wrapper">
        <div class="left relative">
          <div class="canvas-box flex fxcolumn">
            <h2 class="center">Hot Tables(TOP 10) <i v-show="timeout.hotTable" class="el-icon-loading"></i></h2>
            <div class="center btn-box"> 
              <span v-for="item in hotBtnArry" :key="item.key" 
                :style="{cursor: 'pointer', paddingRight: '5px', color: hotBtnIsSelect === item.key ? '#0000FF': '#000000'}" 
                @click="clickHotBtn(item.key)">{{item.label}} <i>|</i></span>
            </div>
            <el-table class="shadow" empty-text="No data" :data="hotTable" v-loading="timeout.hotTable" element-loading-text="loading...">
              <el-table-column label="SourceDB" prop="sourceDb" :show-overflow-tooltip="true"></el-table-column>
              <el-table-column label="SourceTable" prop="sourceTb" :show-overflow-tooltip="true"></el-table-column>
              <el-table-column label="Increse(line) " prop="sourceCount"></el-table-column>
              <el-table-column label="HotRank" type="index" width="80"></el-table-column>
            </el-table>
          </div>
        </div>
        <div class="right relative">
        </div>
      </div>
      <!-- <div class="down-wrapper">
        <div ref="dataCompare" class="wrapper">

        </div>
      </div> -->
    </div>
  </section>
</template>
<script>
  import * as Code from '@config/code';
  import * as Dashboard from '@api/Dashboard';
  import * as dashEchartsBar from './components/dashEchartsBar.vue'
  import * as dashEchartsLine from './components/dashEchartsLine.vue'
  export default {
    name: 'dashboard',
    components: {
      dashEchartsBar, dashEchartsLine
    },
    data () {
      return {
        jobList: [],
        techList: [],
        syncList: [],
        // Agent
        agentTable: [],
        // mysql
        mysqlChartData: [],
        // hot
        hotTable: [],
        hotBtnIsSelect: 'day',
        hotBtnArry: [{
          label: 'day',
          key: 'day'
        },{
          label: 'week',
          key: 'week'
        },{
          label: 'half a month',
          key: 'week2'
        },{
          label: 'month',
          key: 'month'
        },{
          label: 'all',
          key: 'all'
        }],
        // sync agent line
        syncAgentLine: [],
        // sync job line
        syncJobLine: [],

        timeout:{
          sync:true,
          syncTitle:false,
          job:true,
          jobTitle:false,
          tech:true,
          techTitle:false,
          agentTbale: false,
          mysqlEchartsBar: false,
          hotTable: false,
          syncAgentLine: false,
          syncJobLine: false
        },
        streamOption: {
          backgroundColor: '#fafbfc',
          title: {
            text: 'Sync data streaming',
            left: 'center',
            top: 20,
            textStyle: {
              color: '#aaa'
            }
          },
          tooltip: {
            trigger: 'axis'
          },
          legend: {
            data:['Routing1','Routing2'],
            bottom: '3%',
            left: '3%',
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '13%',
            containLabel: true
          },
          toolbox: {
            feature: {
              saveAsImage: {}
            }
          },
          xAxis: {
            type: 'category',
            boundaryGap: false,
            data: ['11/12','11/17','11/22','11/27','12/3','12/8','12/13'],
            axisLine: {
              lineStyle: {
                color: '#888'
              }
            }
          },
          yAxis: {
            type: 'value',
            axisLine: {
              lineStyle: {
                color: '#888'
              }
            }
          },
          series: [
            {
              name:'Routing1',
              type:'line',
              data:[120, 132, 101, 134, 90, 230, 210]
            },
            {
              name:'Routing2',
              type:'line',
              data:[220, 182, 191, 234, 290, 330, 310]
            }
          ]
        },
        bizOption: {
          backgroundColor: '#fafbfc',
          title: {
            text: 'Biz metadata',
            left: 'center',
            top: 20,
            textStyle: {
              color: '#aaa'
            }
          },
          color: ['#a83720','#209843','#60973a'],
          tooltip : {
            trigger: 'axis',
            axisPointer : {
              type : 'shadow'
            }
          },
          legend: {
            data:['total tables','sync ddl tables','sync data tables'],
            bottom: '3%'
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '13%',
            containLabel: true
          },
          xAxis : [
            {
              type : 'category',
              data : ['source tables','target tables'],
              axisLine: {
                lineStyle: {
                  color: '#888'
                }
              }
            }
          ],
          yAxis : [
            {
              type : 'value',
              min: 0,
              max: 1000,
              axisLine: {
                lineStyle: {
                  color: '#888'
                }
              }
            }
          ],
          series : [
            {
              name:'total tables',
              type:'bar',
              data:[320, 132],
              barWidth: '15%'
            },
            {
              name:'sync ddl tables',
              type:'bar',
              data:[120, 932],
              barWidth: '15%'
            },
            {
              name:'sync data tables',
              type:'bar',
              data:[220, 182],
              barWidth: '15%'
            }
          ]
        },
        compareOption: {
          backgroundColor: '#fafbfc',
          color: ['#209843','#a83720'],
          title: {
            text: 'Sync data records compared',
            left: 'center',
            top: 20,
            textStyle: {
              color: '#aaa'
            }
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'cross'
            }
          },
          grid: {
            bottom: '15%'
          },
          toolbox: {
            feature: {
              saveAsImage: {show: true}
            }
          },
          legend: {
            data:['source records','target records'],
            bottom: '3%'
          },
          xAxis: [
            {
              type: 'category',
              axisTick: {
                alignWithLabel: true
              },
              axisLine: {
                lineStyle: {
                  color: '#888'
                }
              },
              data: ['2018-12-04','2018-12-05','2018-12-06','2018-12-07','2018-12-08','2018-12-09','2018-12-10','2018-12-11','2018-12-12','2018-12-13','2018-12-14']
            }
          ],
          yAxis: [
            {
              type: 'value',
              min: 0,
              max: 1000,
              position: 'left',
              axisLine: {
                lineStyle: {
                  color: '#888'
                }
              },
              axisLabel: {
                formatter: '{value}'
              }
            }
          ],
          series: [
            {
              name:'source records',
              type:'bar',
              data:[210, 490, 720, 232, 256, 767, 130, 162, 326, 200, 64]
            },
            {
              name:'target records',
              type:'line',
              data:[200, 212, 313, 451, 603, 102, 203, 934, 230, 165, 120]
            }
          ]
        }
    }
    },
    watch: {},
    created () {
      this.timerA()
      this.timerB()
      this.timerC()
      this.getMysql()
      this.getHot()
      this.getSyncAgent()
      this.getSyncJob()
    },
    methods: {

      timerA(){
        if(this.$route.name!='dashboard'){return;}
        this.getSync();
        setTimeout(()=>{
          this.timerA()
        },1000*60*2)
      },
      timerB(){
        if(this.$route.name!='dashboard'){return;}
        this.getJob();
        this.getTech();
        setTimeout(()=>{
          this.timerB()
        },1000*60*2)
      },
      timerC(){
        if(this.$route.name!='dashboard'){return;}
        // this.getAgent();
        setTimeout(()=>{
          this.timerC()
        },1000*60*60)
      },
      // hot
      getHot() {
        this.timeout.hotTable = true
        Dashboard.hotTbale({fineness: this.hotBtnIsSelect}).then(res => {
          this.timeout.hotTable = false
          if (res.code === Code.SUCCESS) {
            this.hotTable = res.items
          } else {
            this.$message.error(res.message)
          }
        }).catch((er) => {
          this.timeout.hotTable = false
        })
      },
      clickHotBtn(status) {
        this.hotBtnIsSelect = status
        this.getHot()
      },
      // mysql
      getMysql() {
        this.timeout.mysqlEchartsBar = true
        Dashboard.mysqlEchartsBar({}).then(res => {
          this.timeout.mysqlEchartsBar = false
          if (res.code === Code.SUCCESS) {
            this.mysqlChartData = res.items
          } else {
            this.$message.error(res.message)
          }
        }).catch((er) => {
          this.timeout.mysqlEchartsBar = false
        })
      },
      // getAgent
      getAgent() {
        this.timeout.agentTbale = true
        Dashboard.agentTable({}).then(res => {
          this.timeout.agentTbale = false
          if (res.code === Code.SUCCESS) {
            this.agentTable = res.items
          } else {
            this.$message.error(res.message)
          }
        }).catch((er) => {
          this.timeout.agentTbale = false
        })
      },

      getJob() {
        this.timeout.jobTitle=true
        Dashboard.jobCount({}).then(res => {
          if (res.code === Code.SUCCESS) {
            this.jobList = res.items
            // let list = this.jobList.sort(function (a, b) { return a.value - b.value; })
            let list = this.jobList
            let series = []
            let xAxis = []
            list.forEach( row=> {
              xAxis.push(row.name)
              series.push(row.value)
            })
            echarts.init(this.$refs.job).setOption({
              backgroundColor: '#fafbfc',
              tooltip : {
                trigger: 'axis',
                axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                    type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                }
              },
              xAxis: [{
                type: 'category',
                data: xAxis,
                axisTick: {
                    alignWithLabel: true
                }
              }],
              yAxis: [{
                type: 'value',
                minInterval: 1, //设置成1保证坐标轴分割刻度显示成整数。
              }],
              series: [{
                name:'Job',
                type:'bar',
                data: series,
                label: {
                  normal: {
                      show: true,
                      position: 'insideBottom'
                  }
                },
                itemStyle: {
                  normal: {
                    color: function(params) {
                        // build a color map as your need.
                        var colorList = ['#91c7ae','#bda29a','#ca8622','#46ad6a','#c23531']
                        return colorList[params.dataIndex]
                    },
                  },
                  emphasis: {
                    shadowBlur: 200,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                  }
                },
                animationType: 'scale',
                animationEasing: 'elasticOut',
                animationDelay: function (idx) {
                  return Math.random() * 200;
                }
              }]
            });
          } else {
            this.$message({message:  res.message, type: 'error'});
          }
          this.timeout.job=this.timeout.jobTitle=false
        }).catch(() => {
          this.timeout.job=this.timeout.jobTitle=false
        })
      },

      getTech(){
        this.timeout.techTitle=true
        Dashboard.techCount({}).then(res => {
          if (res.code === Code.SUCCESS) {
            let techList=[]
           for(let list of res.items){
             techList.push(list.value)
           }
            echarts.init(this.$refs.techMetadata).setOption({
              backgroundColor: '#fafbfc',
              color: ['#61a0a8','#d48265'],//'#61a0a8'   '#d48265' '#91c7ae','#bda29a'  '#c4ccd3'
              title: {
                /*text: 'Tech metadata',
                left: 'center',
                top: 20,
                textStyle: {
                  color: '#aaa'
                }*/
              },
              tooltip : {
                trigger: 'axis',
                axisPointer : {
                  type : 'shadow'
                }
              },
              xAxis: {
                type: 'category',
                data: ['Sources', 'Targets', 'Routes', 'Agents'],
                axisTick: {
                  alignWithLabel: true
                },
                axisLine: {
                  lineStyle: {
                    color: '#888'
                  }
                }
              },
              yAxis: {
                type: 'value',
                minInterval: 1,
                boundaryGap: [0,0.1],
                axisLine: {
                  lineStyle: {
                    color: '#888'
                  }
                }
              },
              series: [{
                data: techList,
                type: 'bar',
                barWidth: '40%',
              }]
            })
          } else {
            this.$message({message:  res.message, type: 'error'});
          }
          this.timeout.tech=this.timeout.techTitle=false
        }).catch(() => {
          this.timeout.tech=this.timeout.techTitle=false
        })
      },
      fmtLine(items, listKey, nameKey){
        let routes={}, times=Array.from(new Set(items.map(item=>item.time)));
        let T={
          series:{},
          xAxis:times,
          legends:[]
        }
        /*生成并查找所有route name，对每个route补充0*/
        items.forEach(item=>{
          let data=item[listKey];
          data.forEach(m=>{
            let routeskey = m[nameKey]
            if(!routes.hasOwnProperty(routeskey)){
              let temp={};
              times.forEach(t=>{
                temp[t]=0;
              })
              routes[routeskey]=temp;
              T.legends.push(routeskey + '');
            }
          })
        });
        /*循环对存在的时间点填充正式数据，也可以在这里填充0，但是考虑到时间排序就放到上面了*/
        items.forEach(item=>{
          let data=item[listKey], time=item.time;
          data.forEach(m=>{
            let routeskey = m[nameKey]
            routes[routeskey][time]=m.recordSum
          })
        });
        T.series=routes;
        return T;
      },
      getSync(){
        this.timeout.syncTitle=true
        Dashboard.syncCount({}).then(res => {
          if (res.code === Code.SUCCESS) {
            let data = res.items||[];
            let FTD=this.fmtLine(data, 'routeList', 'routeName'), series=[];
            /*生成series*/
            for(let i in FTD.series){
              let sum=FTD.series[i], si=[]
              for(var s in sum){
                si.push(sum[s])
              }
              series.push({
                  name:i,
                  type:'line',
                  data:si,
                  smooth:true,
                })
            }
            echarts.init(this.$refs.dataStream).clear();
            echarts.init(this.$refs.dataStream).setOption({
              backgroundColor: '#fafbfc',
              title: {
                /*text: 'Sync data streaming',
                left: 'center',
                top: 20,
                textStyle: {
                  color: '#aaa'
                }*/
              },
              tooltip: {
                trigger: 'axis'
              },
              legend: {
                data: FTD.legends,
                top: '10%',
                left: '10%',
              },
              grid: {
                left: '10%',
                right: '4%',
                bottom: '13%',
                top: '20%',
                containLabel: true
              },
              toolbox: {
                feature: {
                  /*saveAsImage: {}*/
                }
              },
              xAxis: {
                type: 'category',
                boundaryGap: false,
                data: FTD.xAxis,
                axisLine: {
                  lineStyle: {
                    color: '#888'
                  }
                },
                axisLabel: {
                  rotate: 40,
                  fontSize:12,
                }
              },
              yAxis: {
                type: 'value',
                minInterval: 1,
                boundaryGap: [0,0.1],
                axisLine: {
                  lineStyle: {
                    color: '#888'
                  }
                }
              },
              dataZoom: [
                {
                  show: true,
                  start: 97,
                  end: 100,
                  bottom : 18
                },
                {
                  type: 'inside',
                  start: 97,
                  end: 100
                },
              ],
              series: series
            });
          } else {
            this.$message({message:  res.message, type: 'error'});
          }
          this.timeout.sync=this.timeout.syncTitle=false
        }).catch(() => {
          this.timeout.sync=this.timeout.syncTitle=false
        })
      },
      getSyncAgent() {
       this.timeout.syncAgentLine = true
        Dashboard.syncAgentLine({}).then(res => {
          this.timeout.syncAgentLine = false
          if (res.code === Code.SUCCESS) {
            this.syncAgentLine = this.fmtLine(res.items, 'agentList', 'agentName')
          } else {
            this.$message.error(res.message)
          }
        }).catch((er) => {
          this.timeout.syncAgentLine = false
        })
      },
      getSyncJob() {
       this.timeout.syncJobLine = true
        Dashboard.syncJobLine({}).then(res => {
          this.timeout.syncJobLine = false
          if (res.code === Code.SUCCESS) {
            this.syncJobLine = this.fmtLine(res.items, 'jobList', 'jobName')
          } else {
            this.$message.error(res.message)
          }
        }).catch((er) => {
          this.timeout.syncJobLine = false
        })
      },

      // setEchart () {
      //   echarts.init(this.$refs.dataStream).setOption(this.streamOption);
      //   echarts.init(this.$refs.bizMetadata).setOption(this.bizOption);
      //   echarts.init(this.$refs.dataCompare).setOption(this.compareOption);
      // },
    }
  }
</script>

<style lang="less" scoped>
.chart-loading{
  position: absolute;top: 0;left: 0;
  color: #666;
  background: #fafbfc;
  .el-icon-loading{font-size: 24px;margin-bottom: 5px;}
  width: 100%;height: 100%;display: flex;
}
  .container{
    height: 100%;
    .canvas-box{
      height: 100%;
      h2{
        padding-top: 20px;color: #666;font-size: 18px;
      }
      .btn-box{
       padding: 0 10px;
       text-align: left
      }
    }
  }

  .top-wrapper{
    display: flex;
    height: 500px;
    .left{
      background: #fafbfc;
      margin: 0 15px 15px 20px;
      width: 30%;
    }
    .right{
      background: #fafbfc;
      margin: 0 20px 15px 0px;
      width: 70%;
    }
  }
  .mid-wrapper{
    display: flex;
    height: 500px;
    .left{
      background: #fafbfc;
      margin: 0 15px 15px 20px;
      width: 50%;
    }
    .right{
      background: #fafbfc;
      margin: 0 15px 15px 0px;
      width: 50%;
    }
  }
  .down-wrapper{
    /*margin: 0 20px 15px 20px;*/
    .wrapper{
      margin: 0 20px 15px 20px;
      width: 97%;
      height: 500px;
    }
  }
</style>
<style lang="less">
  .dash-agent-table .el-loading-mask{
    z-index: 988;
  }
</style>

