<!--
  chartData： 数组类型，整个数据部分
  chartData例：[{
      "finenessRate": "all",
      "normalCount": 856,
      "warnCount": 0
  }]
  text：大标题 ，字符串，非必填；
  subtext：副标题，字符串，非必填；
  colorPool：颜色池，数组，非必填（有默认值）；
  xAxisData例：['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
  const colorPool = ['#5182E4', '#9BCC66', '#3FB27F', '#F7CA4A', '#F88D48', '#F06451', '#CD62D5', '#8A55D4', '#5156B8', '#51B4F1', '#69D4DB', '#D32C6B']
  const colorPool = ['#c23531','#2f4554', '#61a0a8', '#d48265', '#91c7ae','#749f83',  '#ca8622', '#bda29a','#6e7074', '#546570', '#c4ccd3']
-->
<template>
  <div :id="elId"
       :style="contentStyle"></div>
</template>

<script>
const colorPool = ['#c23531', '#2f4554', '#61a0a8', '#d48265', '#91c7ae', '#749f83', '#ca8622', '#bda29a', '#6e7074', '#546570', '#c4ccd3']
import uuidv1 from 'uuid/v1'

export default {
  name: 'dash-echarts-bar',
  props: {
    colorPool: {
      type: Array,
      default: () => {
        return ['#A61F24', '#243542']
      }
    },
    width: {
      type: String,
      default: "100%"
    },
    height: {
      type: String,
      default: "100%"
    },
    chartData: {
      type: Array,
      default: []
    },
    query: {
      type: Object,
      default: () => {
        return {
        }
      }
    }
  },
  watch: {
    'query.showType': function (newVal, oldVal) {
      this.initOption()
    },
    chartData: {
      handler: function (newVal, oldVal) {
        this.initOption()
      },
      deep: true
    }
  },
  data() {
    return {
      elId: uuidv1(), // 获取随机id
      // div styles
      contentStyle: {
        width: "",
        height: ""
      },
    }
  },
  created() {
    this.initStyle()
  },
  mounted() {
    this.initOption()
  },
  methods: {
    // 初始化 样式
    initStyle() {
      const { width, height } = this;
      this.contentStyle = Object.assign({}, this.contentStyle, {
        width,
        height
      });
    },
    // 初始化 数据
    initOption() {
      let seriesData1 = []
      let seriesData2 = []
      let xAxisData = []
      this.chartData.forEach((item, index) => {
        xAxisData.push(item.finenessRate)
        seriesData1.push(item.normalCount)
        seriesData2.push(item.warnCount)
      })
 
      const option = {
        color: this.colorPool || colorPool,
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
          left: 'center',
          bottom: 10,
          data: ['Normal', 'Critical']
        },
        grid: {
          top: 20,
          left: 20,
          right: 20,
          containLabel: true
        },
        calculable: true,
        xAxis: {
          type: 'category',
          data: xAxisData || []
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          name: 'Normal',
          type: 'bar',
          data: seriesData1 || [],
          label: {
            normal: {
              show: true,
              position: 'insideBottom'
            }
          },
        },{
          name: 'Critical',
          type: 'bar',
          data: seriesData2 || [],
          label: {
            normal: {
              color: '#666',
              show: true,
              position: 'insideBottom'
            }
          },
        }],
      }
      let chartObj = echarts.init(document.getElementById(this.elId))
      chartObj.setOption(option)
    },
  }
}
</script>
<style scoped>
.jy-container {
  width: 100%;
  min-height: 440px;
}
</style>
