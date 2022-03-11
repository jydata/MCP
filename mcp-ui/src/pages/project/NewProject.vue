<template>
    <section>
      <!--头部-->
      <div class="header">
        <div class="title">
          <el-breadcrumb separator-class="el-icon-arrow-right">
            <el-breadcrumb-item :to="title.from.url">{{title.from.name}}</el-breadcrumb-item>
            <el-breadcrumb-item>{{title.to.name}}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </div>

      <!--内容-->
      <div class="container gray">
        <div class="table shadow mt0">
          <div class="subtitle">Project Detail</div>
          <el-form label-position="top" :rules="rules" :model="projectData" ref="projectForm">
            <el-form-item label="Project Name" prop="projectName">
              <el-input  maxlength=20 v-model="projectData.projectName" clearable></el-input>
            </el-form-item>
            <el-form-item label="Description">
              <el-input  maxlength=20 v-model="projectData.description" clearable></el-input>
            </el-form-item>
          <div class="subtitle" style="margin: 0 -20px">Agent Group</div>
          <el-form-item style="padding:50px">
            <el-checkbox-group v-model="projectData.agent" @change="handleCheckedChange" min="1">
              <el-checkbox  v-for="item in agentList" :label="item.agentId" :key="item.agentId">{{item.agentId}}</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          </el-form>
          <el-button @click="saveProject('projectForm')">Save</el-button>
        </div>
      </div>
    </section>
</template>

<script>
  import * as Project from '@api/Project';
  import * as Code from '@config/code';
  import * as Tips from '@config/tips';
    export default {
        name: "NewProject",
      data(){
        let reg = /^[a-zA-Z]+[\w]*$/;
        let checkName = (rule, value, callback) => {
          if (value === '' || value.trim().length <= 0) {
            return callback(new Error('Project Name is required'));
          } else if (!reg.test(value)) {
            callback(new Error('Only numbers,letters and underline are allowed, and letters must be in front'));
          } else {
            callback();
          }
        };
          return{
            title:{from:{},to:{}},
            agentList: [],
            projectId: '',
            agentId: '',
            projectAgent: '',
            projectData:{
              projectName: '',
              description: '',
              agent: []
            },
            rules:{
              projectName: [
                {required: true, validator: checkName, trigger: 'blur'},
              ],
            }
          }
      },
      created(){
          if(typeof(this.$route.query.projectId) === 'undefined') {
            this.title = {
              from:{url:'/project/ProjectList',name:'Project List'},
              to:{name:'New Project'}
            };
            this.projectId = 0;
          }else {
            this.title = {
              from:{url:'/project/ProjectList',name:'Project List'},
              to:{name:'Edit Project'}
            };
            this.projectData.projectName = this.$route.query.projectName;
            this.projectData.description = this.$route.query.projectDescription;
            this.projectId = this.$route.query.projectId;
            this.projectAgent= this.$route.query.projectAgent;
            this.agentId = this.projectAgent.split(",");
          }
        this.queryAgent();
      },
      methods:{
        //queryAgent
        queryAgent(){
           Project.queryAgent({}).then(res => {
              if (res.code === Code.SUCCESS) {
                this.agentList = res.items;
                this.agentList.forEach(item => item.agentId = item.agentId+'');
                for(let list of this.agentList){
                  let a = this.agentId.filter(item => item == list.agentId);
                  if(a.length){
                    this.projectData.agent.push(a[0]);
                  }
                }
                this.projectAgent = this.projectData.agent.join();
              } else {
                this.$message({message:  res.message, type: 'error'});
              }
            }).catch(() => {
            })
          },

          //保存Project
        saveProject(formName){
          this.$refs[formName].validate((valid) => {
              if (valid) {
                if(this.projectAgent == ''){
                  this.$message({message: 'Please select Agent', type: 'error'})
                }else{
                  let params = {
                    projectName: this.projectData.projectName,
                    projectDescription: this.projectData.description,
                    projectAgent: this.projectAgent,
                    projectId: this.projectId,
                  }
                  Project.eidtProject(params).then(res => {
                    if (res.code === Code.SUCCESS) {
                      this.$router.push('ProjectList');
                      this.$message({message: Tips.SAVE_SUCCESS, type: 'success'});
                    } else {
                      this.$message({message: Tips.SAVE_FAILED, type: 'error'});
                    }
                  }).catch(() => {
                  })
                }
              }
          })
        },

        handleCheckedChange(){
          this.projectAgent = this.projectData.agent.join();
        }
      }
    }
</script>

<style lang="less" scoped>
  .el-form{padding: 20px;
    .el-input{width: 50%}
  }
  .table {
    position: relative;
    .el-button {
      position: absolute;
      right: 20px;
      bottom: 15px;
    }
  }
  .subtitle{background-color: #f7f7f7;height: 35px;line-height: 35px;padding-left: 15px}
</style>
