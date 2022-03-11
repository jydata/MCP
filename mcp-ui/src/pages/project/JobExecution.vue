<template>
<section>
	<!--头部-->
	<div class="header">
		<div class="title">
			<el-breadcrumb separator-class="el-icon-arrow-right">
				<el-breadcrumb-item to="/project/ProjectList">Project List</el-breadcrumb-item>
				<el-breadcrumb-item>Jobs Execution</el-breadcrumb-item>
			</el-breadcrumb>
		</div>
	</div>

	<div class="container pd20">
		<ul class="dash-area flex">
			<li class="flex1 flex left fxmiddle">
				<ul>
					<li>Job Id:</li>
					<li>Log Id:</li>
					<li>Job Group:</li>
					<li>Job Name:</li>
					<li>Job Type:</li>
					<li>Job Status:</li>
				</ul>
				<div></div>
			</li>
			<li class="flex1 right fxmiddle">
				<div></div>
				<ul>
					<li>{{execu.jobId}}</li>
					<li>{{execu.logId}}</li>
					<li>{{execu.groupName}}</li>
					<li>{{execu.jobName}}</li>
					<li>{{execu.jobType}}</li>
					<li>{{fmtStatus(execu.handleCode)}}</li>
				</ul>
			</li>
		</ul>
		<el-radio-group fill="#2c6daf" @change="exChange" v-model="execution" class="mb15 mt15">
			<el-radio-button label="Summary"></el-radio-button>
			<el-radio-button label="Log Output"></el-radio-button>
		</el-radio-group>
		<ul class="shadow margin0">
			<li v-show="execution=='Summary'">
				<el-table :data="jobsList" empty-text="No data" width="60%">
					<el-table-column label="JobType" prop="scheduleType"></el-table-column>
					<el-table-column label="Start Time" :formatter="fmtHandleTime" prop="handleTime"></el-table-column>
					<el-table-column label="Duration" prop="duration" :formatter="fmtDuration"></el-table-column>
					<el-table-column label="Binlog Name" prop="binlogName"></el-table-column>
					<el-table-column label="Binlog Position" prop="binlogPosition"></el-table-column>
					<el-table-column label="Agent Group" prop="agent"></el-table-column>
				</el-table>
			</li>
			<li v-show="execution=='Log Output'">
				<ul class="log-output">
					<li class="flex" style="height: 15px;"> <p class="left"></p><p class="right flex1"></p></li>
					<li v-for="item in logInfo" class="flex">
						<p class="left">{{item.time}}</p>
						<p class="right flex1" v-html="item.cont"></p>
					</li>
					<li class="flex" style="height: 15px;"> <p class="left"></p><p class="right flex1"></p></li> </ul>
			</li>
		</ul>
	</div>
</section>
</template>

<script>
import * as Job from '@api/Job';
import * as Code from '@config/code';
import * as Tips from '@config/tips';
export default {
	name: "JobsDefinition",
	data(){
		return {
			execu:{
			},
			projects:['BDP', 'IDSS', 'MCP'],
			params:{
				jobList:[this.$route.params.id],
				entity:{
					logId: this.$route.params.logId,
					triggerTime: this.$route.params.time,
					triggerCode:300
				}
			},
			jobsList:[],
			execution:'Summary',
			logInfo:[]
		}
	},
	methods:{
		fmtHandleTime(row){
			let mat={}, date=new Date(row.handleTime);
			mat.Y=date.getFullYear().toString().substring(2);
			mat.M=date.getMonth()+1;
			mat.D=date.getDate();

			mat.H=date.getHours();
			mat.m=date.getMinutes();
			mat.type='AM';
			if(mat.H>11){
				mat.type='PM';
			}
			if(mat.H>12){
				mat.H=mat.H-12;
			}
			return mat.M+"/"+mat.D+"/"+mat.Y+" "+mat.H+":"+mat.m+" "+mat.type+" ";
		},
		fmtDuration(row){
			if(row.duration==null){
				return ''
			}else{
				return row.duration<1?'<1s':row.duration+'s'
			}

		},
		queJobById(){
			let p={};
			p.jobList=[this.$route.params.id];
			p.entity={
				logId:this.$route.params.logId,
			}
			Job.queloglistById(p).then(res => {
				if (res.code === Code.SUCCESS) {
					this.execu=res.items[0]
					this.jobsList=res.items
				} else {
					this.$message({message: res.message, type: 'error', center: true});
				}
			}).catch(() => {
			})
		},
		fmtlog(items){
			let li=[]
			if(items){
				let lit=items.split('\n'), length=lit.length;
				lit.forEach(item=>{
					if(item){
						let obj={}, line=item.split(' [');
						if(length==1&&!line[1]){
							obj.time='';
							obj.cont=line[0];
						}else{
							obj.time=line[0]||'';
							obj.cont=line[1]||'';
						}

						li.push(obj)
					}
				})
			}
			this.logInfo=li

		},
    queJobLogById(){
			Job.queJobLogById(this.params).then(res => {
				if (res.code === Code.SUCCESS) {
					this.fmtlog(res.items.logInfo)
				} else {
					this.getDBRoutes()
					this.$message({message: res.message, type: 'error', center: true});
				}
			}).catch(() => {
				this.getDBRoutes()
			})
		},
		exChange(val){
			if(val=='Log Output'){
				this.queJobLogById()
			}
		},
		rowClass({row,rowIndex}){
			return row.show?'row-show':'row-hide'
		},
		spanAcs({row, columnIndex}){
			if(columnIndex===0||columnIndex===1){
				let rowspan=0;
				if(row.first){
					if(row.unwrap){
						rowspan=row.rowspan
					}else{
						rowspan=1
					}
				};
				return{
					rowspan:rowspan,
					colspan:1
				};
			}
		},
		init(){
			this.queJobById();
		},
		fmtStatus(val){
			switch (val) {
				case 300:
					return 'Running';
					break;
				case 200:
					return 'Success';
					break;
				case 100:
					return 'Init';
					break;
				case 500:
					return 'Failed';
					break;
			}
		}
	},
	created(){
		this.init();
	},

}
</script>

<style scoped>
.dash-area{
	height: 240px;
}
.dash-area>li{
	width: 50%;
}
.dash-area .left{
	background: #efefef;justify-content: flex-end;text-align: right;
}
.dash-area li>div{width: 20px;height: 100%;}
.dash-area ul li{
	padding: 6px 0;height: 19px;
}
.dash-area .right{
	background: #fff;color: #2c6daf;
}
.log-output{

}
.log-output li p{
	padding: 3px 10px 3px;
}
.log-output .left{
	width: 180px;background: #efefef;text-align: right;min-width: 180px;
}
/*表格*/
.action-td i{
	margin: 0 5px;
}
.margin0{
	margin: 0;
}
/* 通用flex */
.center{
	display: inline-flex;justify-content: center;align-items: center;
}
.fxcenter{
	display: inline-flex;display: -webkit-inline-flex;
	-webkit-justify-content: center;
}
.fxmiddle{
	display: -webkit-inline-flex;
	-webkit-align-items: center;
}
.center{
	display: -webkit-inline-flex;
	-webkit-justify-content: center;
	-webkit-align-items: center;
}
.middle{
	display: -webkit-inline-flex;
	-webkit-flex-direction: column;
	-webkit-justify-content: center;
	-webkit-align-items: center;
}
.fxnowrap{
	flex-wrap: nowrap;
}
.fxwrap{
	flex-wrap: wrap;
}
.fxcolumn{
	flex-direction: column;
}
.flex1{
	flex-grow: 1;
}
.flex{
	display: flex;
}
.shrink0{
	flex-shrink: 0;
}
.fsize18{
	font-size: 18px;
}
</style>
