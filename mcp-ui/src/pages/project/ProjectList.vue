<template>
    <section>
    	<!--头部-->
    <div class="header">
      <div class="title">
        <el-breadcrumb separator-class="el-icon-arrow-right">
          <el-breadcrumb-item>Project List</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
    </div>

      <!--内容-->
    <div class="container">
    	<div class="search">
    		<div class="left">
    			<el-input size="medium" v-model="projectName" placeholder="Search Project" style="width: 220px" @keyup.enter.native="queryProject" clearable></el-input>
          <el-button class="ml5" size="small" icon="el-icon-search" @click="queryProject"></el-button>
        </div>
    		<div class="right">
          <el-button size="mini" style="height: 36px;" @click="goProject">New Project</el-button>
        </div>
    	</div>
    	<div class="table shadow">
    		<el-table empty-text="No data" :data="ProjectList">
          <el-table-column label="#" type="index"></el-table-column>
          <el-table-column label="Project Name" prop="projectName">
          	<template slot-scope="scope">
              <router-link class="project-a"  :to="'/project/JobsDefinition/'+scope.row.projectId">
                <i class="icon-tasks"></i>
                <span style="display:inline-block;margin-left: 3px;font-size: 24px">{{scope.row.projectName}}</span>
              </router-link>
          	</template>
          </el-table-column>
          <el-table-column label="Executions" >
          	<template slot-scope="scope">
              <router-link class="project-2running" :to="'/project/JobsDefinition/'+scope.row.projectId"><span class="deepblue">{{scope.row.executeNum}}</span><span> Executions In the last day</span></router-link>
          	  <router-link class="project-2fail" :to="'/project/JobsDefinition/'+scope.row.projectId+'/Failed'">
                （<span>{{scope.row.failNum}}</span> Falied）
              </router-link>
          	</template>
          </el-table-column>
          <el-table-column width="140px" label="Actions">
            <template slot-scope="scope">
                <i class="icon-edit mr20" title="Edit Project" @click="goProject(scope.row)"></i>
                <i class="icon-trash mr20" title="Delete Project" @click="deleteProject(scope.row)"></i>
                <i class="icon-plus-circle" title="Create Job" @click="goJob(scope.row)"></i>
            </template>
          </el-table-column>
        </el-table>
    	</div>
    </div>
    <div class="container">
			<el-radio-group fill="#2c6daf" @change="changeRadio" v-model="jobStatusLabel" class="mb15">
				<el-radio-button v-for="item in jobStatusAry" :key="item.key" :label="item.label" :disabled="loading.jobTable"></el-radio-button>
			</el-radio-group>
      <el-table empty-text="No data" :data="jobTableList" v-loading="loading.jobTable" element-loading-text="loading...">
        <el-table-column label="JobID" prop="jobId">
          <template slot-scope="scope">
						<router-link class="deepblue" :to="'/project/JobExecution/'+scope.row.jobId+'/'+scope.row.logId+'/'+scope.row.triggerTime">{{scope.row.jobId}}</router-link>
					</template>
        </el-table-column>
        <el-table-column label="ProjectName" prop="projectName"></el-table-column>
        <el-table-column label="JobName" prop="jobName"></el-table-column>
        <el-table-column label="GroupName" prop="groupName"></el-table-column>
        <el-table-column label="RouteName" prop="routeName"></el-table-column>
        <el-table-column label="StartTime" prop="triggerTime" :formatter="$root.formatDate"></el-table-column>
        <el-table-column label="Duration" prop="duration"></el-table-column>
        </el-table>
    </div>

    </section>
</template>

<script>
  import * as Project from '@api/Project';
  import * as Code from '@config/code';
  import * as Tips from '@config/tips';
    export default {
      name: "ProjectList",
      data(){
         return{
         	projectName: '',
          ProjectList: [], 
          //  radio button
          jobStatus: 'running',
          jobStatusLabel: 'Running Jobs',
          jobStatusAry: [{
            label: 'Running Jobs',
            key: 'running'
          },{
            label: 'Failed Jobs',
            key: 'fail'
          }],
          // job table list
          jobTableList: [],
          // loading
          loading: {
            jobTable: false
          }
         }
      },
      created(){
        this.queryProject();
        /*清除job页面选择的状态*/
        this.$store.state.TEMPJOBTYPE=false;
        this.getJobTable()
      },
      methods:{
        // job radio
        changeRadio (status) {
          this.jobStatusAry.forEach(row => {
            if (row.label === status) this.jobStatus = row.key
          })
          this.getJobTable()
        },
        // job table list
        getJobTable () {
          this.loading.jobTable = true
          Project.jobTableList({status: this.jobStatus}).then(res => {
            this.loading.jobTable = false
            if (res.code === Code.SUCCESS) {
              this.jobTableList = res.items
            } else {
              this.$message.error(res.message);
            }
          }).catch((er) => {
            this.loading.jobTable = false
          })
        },

        //查询project
        queryProject(){
      	  let params = {}
      	  if(this.projectName == ''){
      	    params = {}
          }else{
            params = {projectName: this.projectName}
          }
        	 Project.queryProject(params).then(res => {
              if (res.code === Code.SUCCESS) {
                this.ProjectList = res.items
              } else {
                this.$message({message: res.message, type: 'error'});
              }
            }).catch(() => {
            })
        },

        //跳转 Project
        goProject(row){
          this.$router.push({
            path: 'NewProject',
            query:{
              projectName : row.projectName,
              projectDescription : row.projectDescription,
              projectAgent : row.projectAgent,
              projectId: row.projectId
            }
          });
        },
        //跳转 New Job
        goJob(row){
          this.$router.push({
            path: 'NewJob',
            query:{
              origin: 'project',
              projectName : row.projectName,
              projectAgent: row.projectAgent,
              projectId: row.projectId
            }
          });
        },

        //Delete Project
        deleteProject(item){
          this.$confirm('Are you confirm delete?', 'Message', {
            confirmButtonText: 'Confirm',
            cancelButtonText: 'Cancel',
          }).then(() => {
            Project.delProject(item.projectId).then(res => {
              if (res.code === Code.SUCCESS) {
                this.$message({message: Tips.DELETE_SUCCESS, type: 'success'});
                this.queryProject()
              } else {
                this.$message({message: res.message, type: 'error'});
              }
            })
          }).catch(() => {
            })
        }
      }
    }
</script>

<style lang="less" scoped>
a{cursor: pointer;}
.search{
	display: flex;
	justify-content: space-between;
  .left{}
  .right{}
}
.project-a{
  color: #2c6daf;
  &:hover{
    color:#f00;
    .icon-tasks{
      &:before{
        color: #f00;
      };
    }
  }
  .icon-tasks{
    position: relative;top: 2px;
    &:before{
      color: #2c6daf;
    };
  }
}
.project-2fail,.project-2running{
  color: #666;
}
.project-2running{
  &:hover{
    color: #2c6daf;
  }
}
.project-2fail{
  &:hover{
    color: #f00;
  }
}
.project-2fail{
  span{
    color: #f00;
  }
}
.icon-plus-circle:before {font-size: 25px}

</style>
