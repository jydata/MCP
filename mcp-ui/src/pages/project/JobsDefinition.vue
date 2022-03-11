<template>
<section>
	<!--头部-->
	<div class="header">
		<div class="title">
			<el-breadcrumb separator-class="el-icon-arrow-right">
				<el-breadcrumb-item :to="{ path: '/project/ProjectList' }">Project List</el-breadcrumb-item>
				<el-breadcrumb-item>Jobs Definition</el-breadcrumb-item>
			</el-breadcrumb>
		</div>
	</div>
	<div class="container pd20" style="min-height: 400px;">
		<div class="func-area flex">
			<selectMulx  :data="projects" v-model="params.projectId" :options="mulxOptions" @back="tapLi"></selectMulx>
			<el-input size="medium" @keyup.enter.native="init" v-model="params.jobName" placeholder="Job Search" style="width: 220px"></el-input>
			<el-button class="ml10" size="small" icon="el-icon-search" @click="init"></el-button>
		</div>
		<div class="mt15">
			<el-table v-loading="loading1" @row-click="queJobsByStatus" :span-method="spanAcs" :row-style="rowStyle" :row-class-name="rowClass" class="shadow margin0" :data="jobsList" empty-text="No data" width="60%">
				<el-table-column label="#" width="50px">
					<template slot-scope="scope">
						<i @click.stop="unwrapRow(scope.row)" :class="{'icon-zhankai':!scope.row.unwrap,'icon-shouqi':scope.row.unwrap}"></i>
					</template>
				</el-table-column>
				<el-table-column label="Job Group" prop="groupName"></el-table-column>
				<el-table-column label="Job Name" prop="jobName"></el-table-column>
				<el-table-column label="Job Type" prop="jobType"></el-table-column>
				<el-table-column label="Route Name" prop="routeName"></el-table-column>
				<el-table-column label="Status" prop="status"></el-table-column>
				<el-table-column label="Actions" class="action-td" width=240>
					<template slot-scope="scope">
						<div v-if="scope.row.jobType=='metadata'||scope.row.jobType=='increment'">
							<el-button class="" size="small" @click.stop="killAllJobById(scope.row)">Configure</el-button>
							<el-button :disabled="setBtnRunType(scope.row)" class="ml10" size="small" @click.stop="setJob1(scope.row)" style="width: 109px;">{{setBtnTxt(scope.row.status)}}</el-button>
						</div>
						<div v-if="scope.row.jobType=='ddl'||scope.row.jobType=='full'">
							<el-button class="" size="small" @click.stop="killAllJobById(scope.row)">Configure</el-button>
							<el-button :disabled="setBtnRunType(scope.row)" class="ml10" size="small" @click.stop="setJob1(scope.row)" style="width: 109px;">{{setBtnTxt(scope.row.status)}}</el-button>
						</div>
					</template>
				</el-table-column>
			</el-table>
		</div>
		<div class="mt15">
			<el-radio-group fill="#2c6daf" @change="tapLi(false,execution)" v-model="execution" class="mb15">
				<el-radio-button v-for="item in jobStatus" :key="item" :label="item"></el-radio-button>
			</el-radio-group>
			<el-table v-loading="loading2" :data="jobsList2" empty-text="No data" width="60%" class="shadow margin0">
				<el-table-column width="100" label="Log Id">
					<template slot-scope="scope">
						<router-link class="deepblue" :to="'/project/JobExecution/'+scope.row.jobId+'/'+scope.row.logId+'/'+scope.row.triggerTime">{{scope.row.logId}}</router-link>
					</template>
				</el-table-column>
				<el-table-column label="Job Id" prop="jobId"></el-table-column>
				<el-table-column label="Job Name" prop="jobName"></el-table-column>
				<el-table-column label="At Time">
					<template slot-scope="scope">
						<p>{{fmttime(scope.row.handleTime,scope.row.handleEndTime)}}</p>
					</template>
				</el-table-column>
				<el-table-column width="100" label="Action" v-if="execution!='Recent'">
					<template slot-scope="scope">
						<a class="deepblue" href="javascript:" @click="setJob2(scope.row)">{{scope.row.handleCode==300?'Kill':'Run Again'}}</a>
					</template>
				</el-table-column>
			</el-table>
		</div>
	</div>
</section>
</template>

<script>
import * as Job from '@api/Job';
import * as Project from '@api/Project';
import * as Code from '@config/code';
import * as Tips from '@config/tips';
const selectMulx=()=>import('components/select-mulx')

export default {
	name: "JobsDefinition",
	components:{selectMulx},
	data(){
		return {
			mulxOptions:{
				title:'Projects',
				a:{
					content:'New Project',
					href:'/project/NewProject'
				},
				labelId:'projectId',
				labelName:'projectName',
			},
			projects:[],
			params:{
				projectId:Number(this.$route.params.name||''),
				jobName:''
			},
			projectName:'loadding...',
			jobStatus:['Running','Recent','Failed'],
			jobsList:[],
			jobsList2:[],
			execution:this.$store.state.TEMPJOBTYPE||this.$route.params.type||'Running',
			jobUnwrapList:[],
			loading1:false,
			loading2:false,
		}
	},
	methods:{
		setBtnRunType(row){
			let disabled=true
			switch (row.status) {
				case 'ready':
					disabled=true
					break;
				case 'running':
					if(row.jobType=='ddl'||row.jobType=='full'){
						disabled=true
					}else{
						disabled=false
					}
					switch (row.jobType) {
						case 'ddl':
						case 'full':
							disabled=true
							break;
						case 'metadata':
							disabled=row.routeId?false:true;
							break;
						default:
							disabled=false
							break;
					}
					break;
				case 'wait':
					if(row.jobType=='ddl'||row.jobType=='full'){
						disabled=true
					}else{
						disabled=false
					}
					break;
				case 'success':
				case 'fail':
				case 'init':
					disabled=false
					break;
			}
			return disabled;
		},
		setBtnTxt(val){
			switch (val) {
				case 'running':
					return 'Pause';
					break;
				case 'wait':
					return 'Resume';
					break;
				default:
					return 'Run Job Now';
					break;
			}
		},
		killAllJobById(row){
			// if(row.status=='running'||row.status=='ready'){
				this.$confirm(`Modifying the information requires closing the task in execution,Confirm to close it?`, '', {
					confirmButtonText: 'Sure',
					cancelButtonText: 'Cancel',
				}).then(() => {
					this.loading1=true;
					Job.killAllJobById({},row.jobId).then(res => {
						if (res.code === Code.SUCCESS) {
							this.tap2Edit(row, res.items)
						} else {
							this.$message({message: res.message, type: 'error', center: true});
						}
						this.loading1=false;
					}).catch(() => {
						this.loading1=false;

					})
				}).catch(()=>{
					this.loading1=false;
				})
		},
		tap2Edit(row, res){
			this.$router.push({
				path: '/project/NewJob',
				query:{
					origin:'job',
					projectName:this.projectName,
					projectId:this.params.projectId,
					srcDb:row.srcDb,
					srcTable:row.srcTable,
					cron:row.cron,
					sourceId:row.sourceId,
					sourceName:row.sourceName,
					groupName:row.groupName,
					groupId:row.groupId,
					jobId:row.jobId,
					jobName:row.jobName,
					jobType:row.jobType,
					routeName:row.routeName,
					targetName:row.targetName,
					schemaName:row.schemaName,
					routeId:row.routeId,
					agent:row.agent,
					targetId:row.targetId,
					schemaId:row.targetSchemaId,
          binlogName:row.binlogName,
          binlogPosition:row.binlogPosition,
          recentBinlog:row.recentBinlog,
          busStartTime:row.busStartTime,
          busEndTime:row.busEndTime,
					ruleName:row.ruleName,
					schemaList: JSON.stringify(res.tree), // edit job tree data
					defaultSelect: res.checkList, // edit job tree select data
					dbTableList: JSON.stringify(res.dbTableList), // increment db table
					checkIncreList: JSON.stringify(res.checkIncreList), // increment db select table
					checkFullList: JSON.stringify(res.checkFullList) // full db table
				}
			})
		},
		setJob1(row){
			/*metadata&&routeId!=0=>add_job*/
			let status=row.status, api='triggerJobById', id=row.jobId, p={};
			if(status=='wait'){
				api='resumeJobById';
			};
			if(status=='running'){
				api='pauseJobById';
			};
			this.loading1=true;
			this.$store.state.TDSELECTED=row.jobId;
			Job[api](p, id).then(res => {
				if (res.code === Code.SUCCESS) {
					let e=this.execution, api='killJobById', id=row.logId;
					this.queJobs()
				} else {
					this.$message({message: res.message, type: 'error', center: true});
				}
				this.loading1=false;
			}).catch(() => {
				this.loading1=false;

			})
		},
		setJob2(row){
			let e=this.execution, api='killJobById', id=row.logId;
			if(e=='Failed'){
				api='triggerJobById';
				id=row.jobId
			};
			this.loading2=true;
			Job[api]({}, id).then(res => {
				if (res.code === Code.SUCCESS) {
					this.$message({message: Tips.FLAG_SUCCESS, type: 'success', center: true});
					this.queJobsByStatus()
				} else {
					this.$message({message: res.message, type: 'error', center: true});
				}
				this.loading2=false;
			}).catch(() => {
				this.loading2=false;

			})
		},
		unwrapRow(row){
			let unwrapIndex=this.jobUnwrapList.indexOf(row.groupName)
			if(unwrapIndex>-1){
				this.jobUnwrapList.splice(unwrapIndex,1)
			}else{
				this.jobUnwrapList.push(row.groupName)
			};
			if(row.rowspan>1){
				row.unwrap=!row.unwrap
				this.jobsList.forEach(item=>{
					if(item.groupName==row.groupName){
						if(!item.first){
							item.show=row.unwrap
						}
					}
				})
			}
		},
		unwrapAct(name, type){

		},
		fmtJobs(items){
			let group=[], gp={};
			items.forEach(item=>{
				item.show=false;
				let destin=item.groupName
				if(gp.hasOwnProperty(destin)){
					gp[destin][0].rowspan++;
					gp[destin][0].unwrap=false;
					item.first=item.show=false;
					let unwraped=this.jobUnwrapList.indexOf(item.groupName)>-1;
					if(unwraped){
						item.show=true;
						gp[destin][0].unwrap=true;
					}
					gp[destin].push(item)
				}else{
					item.rowspan=1
					item.first=item.unwrap=item.show=true;
					gp[destin]=[item]
				};
			})
			for(let i in gp){
				/*for(let m=0;m<gp[i].length;m++){
					let unwraped=this.jobUnwrapList.indexOf(gp[i].groupName)>-1
					if(unwraped){
						gp[i].show=true
						if(gp[i].first){
							item.rowspan=1
						}
					}
				}*/
				group=group.concat(gp[i])

			};
			return group;
		},
		tapLi(item, type){
			let url='/project/JobsDefinition/'+(item.projectId||this.$route.params.name)
			url+='/'+(type||this.$route.params.type||'Running');
			if(type){
				this.$store.state.TEMPJOBTYPE=type;
				this.queJobsByStatus()
			}else{
				this.$router.replace({path:url})
			}

		},
		setCurtProject(items){
			this.projects=items
			this.init();
			for(let i=0;i<items.length;i++){
				if(items[i].projectId==this.$route.params.name){
					this.projectName=items[i].projectName;
					break;
				}
			}
		},
		queProjects(){
			/*一分钟之内不获取最新的*/
			if(this.$store.JOBPROJECTSEXPIRE==new Date().getMinutes()&&this.$store.JOBPROJECTS){
				this.setCurtProject(this.$store.JOBPROJECTS);return;
			}
			Project.queryName().then(res => {
				if (res.code === Code.SUCCESS) {
					this.projects=res.items

					if(res.items.length){
						this.$store.JOBPROJECTS=res.items;
						this.$store.JOBPROJECTSEXPIRE=new Date().getMinutes();
						this.setCurtProject(res.items)
					}else{
						this.projectName='No Project'
					}
				} else {
					this.$message({message: res.message, type: 'error', center: true});
				}
			}).catch(() => {

			})
		},
		queJobs(){
			Job.queJobs(this.params).then(res => {
				if (res.code === Code.SUCCESS) {
					/*this.fmtJobs(data)*/
					this.tempJbos=res.items
					this.jobsList=this.fmtJobs(res.items);
					this.queJobsByStatus();
				} else {
					this.$message({message: res.message, type: 'error', center: true});
				}
			}).catch(() => {
			})
		},
		queJobsByStatus(row){
			this.loading2=true;
			let p={}, id=this.$store.state.TDSELECTED;
			if(row){
				if(row.jobId===id){
					p.jobList=this.jobsList.map(item=>item.jobId);
					this.$store.state.TDSELECTED=''
				}else{
					p.jobList=[row.jobId]
					this.$store.state.TDSELECTED=row.jobId;
				}
			}else{
				if(id){
					p.jobList=[id]
				}else{
					p.jobList=this.jobsList.map(item=>item.jobId);
				}

			}
			p.entity={
				logId:'',
				handleCode:this.fmtStatus(this.execution)
			}
			/*p.projectId=this.params.projectId;
			p.jobId=this.jobId||'';
			p.triggerCode=this.fmtStatus(this.execution);*/

			this.jobsList=this.fmtJobs(this.tempJbos)
			Job.queloglistById(p).then(res => {
				if (res.code === Code.SUCCESS) {
					this.jobsList2=res.items
				} else {
					this.$message({message: res.message, type: 'error', center: true});
				}
				this.loading2=false;
			}).catch(() => {
				this.loading2=false;
			})
		},
		rowClass({row,rowIndex}){
			let selectId=this.$store.state.TDSELECTED;
			if(row.jobId==selectId){
				if(row.first){
					if(row.unwrap){
						if(row.rowspan==1){
							return ["td-selected-other"]
						}else{
							return ["td-selected"]
						}
					}else{
						return ["td-selected"]
					}
				}else{
					return ["td-selected-other"]
				}

			};
		},
		rowStyle({row,rowIndex}){
			return "display: "+(row.show?'':'none')
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
				}
				return{
					rowspan:rowspan,
					colspan:1
				};
			}
		},
		init(){
			this.$store.state.TDSELECTED=false;
			this.queJobs();
		},
		cmptime(start, end){
			start=new Date(start), end=new Date(end);
			let x=Math.round(end.getTime()-start.getTime())/1000;
			let M=Math.floor(x/60);
			let S=Math.round(x%60)
			return M+'m'+S+'s'
		},
		fmttime(start, end){
			if(!start){
				return '';
			}
			let mat={}, date=new Date(start);
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
			let tm=mat.M+"/"+mat.D+"/"+mat.Y+" "+mat.H+":"+mat.m+" "+mat.type;
			if(end){
				tm+=" "+this.cmptime(start, end)
			}
			return tm
		},
		fmtStatus(val){
			switch (val) {
				case 'Running':
					return 300;
					break;
				case 'Recent':
					return 200;
					break;
				default:
					return 500;
					break;
			}
		}
	},
	created(){
		this.queProjects();
	}
}
</script>

<style scoped>
.func-area .el-input{
	width: 150px;margin-right: 10px;
}
.func-area .el-select{
	width: 100px;margin-right: 10px;
}
.func-area .el-button{
	background: #2c6daf;
}
.margin0{
	margin: 0;
}
</style>
