webpackJsonp([7],{588:function(e,t,n){n(763);var o=n(307)(n(676),n(737),"data-v-03c112df",null);o.options.__file="D:\\jiuye-pro\\jydata-git\\mcp-new\\mcp-ui\\src\\pages\\project\\JobsDefinition.vue",o.esModule&&Object.keys(o.esModule).some(function(e){return"default"!==e&&"__esModule"!==e})&&console.error("named exports are not supported in *.vue files."),o.options.functional&&console.error("[vue-loader] JobsDefinition.vue: functional components are not supported with templates, they should use render functions."),e.exports=o.exports},597:function(e,t,n){"use strict";var o=n(310),r=n.n(o),s=n(599),a=n.n(s),i=n(600),u=n.n(i),c=function(){function e(){a()(this,e)}return u()(e,null,[{key:"setPromise",value:function(e,t,n){return new r.a(function(o,r){switch(e.toUpperCase()){case"GET":axios.get(t,{params:n}).then(function(e){e?o(e.data):r()});break;case"POST":case"PUT":axios({method:e,url:t,data:n}).then(function(e){e?o(e.data):r()});break;case"DELETE":axios.delete(t,{data:n}).then(function(e){e?o(e.data):r()})}})}}]),e}();t.a=c},598:function(e,t,n){"use strict";n.d(t,"a",function(){return o}),n.d(t,"b",function(){return r});var o="SUCCESS",r="ERROR"},599:function(e,t,n){"use strict";t.__esModule=!0,t.default=function(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}},600:function(e,t,n){"use strict";t.__esModule=!0;var o=n(309),r=function(e){return e&&e.__esModule?e:{default:e}}(o);t.default=function(){function e(e,t){for(var n=0;n<t.length;n++){var o=t[n];o.enumerable=o.enumerable||!1,o.configurable=!0,"value"in o&&(o.writable=!0),(0,r.default)(e,o.key,o)}}return function(t,n,o){return n&&e(t.prototype,n),o&&e(t,o),t}}()},601:function(e,t,n){"use strict";n.d(t,"e",function(){return o}),n.d(t,"j",function(){return r}),n.d(t,"b",function(){return s}),n.d(t,"f",function(){return a}),n.d(t,"k",function(){return i}),n.d(t,"l",function(){return u}),n.d(t,"c",function(){return c}),n.d(t,"g",function(){return l}),n.d(t,"i",function(){return d}),n.d(t,"h",function(){return f}),n.d(t,"d",function(){return m}),n.d(t,"a",function(){return p});var o="Operation is successful！",r="Add successfully！",s="Updated successfully！",a="Deleted successfully！",i="Connection test successfully!",u="Connection test successfully!",c="Saved successfully!",l="Saved failed!",d="Synchronized successfully!",f="Transform successfully!",m="Reset password successfully！",p="Modify successfully！"},604:function(e,t,n){"use strict";var o=n(597);n.d(t,"b",function(){return r}),n.d(t,"a",function(){return s}),n.d(t,"g",function(){return a}),n.d(t,"e",function(){return i}),n.d(t,"f",function(){return u}),n.d(t,"c",function(){return c}),n.d(t,"d",function(){return l});var r=function(e){return o.a.setPromise("GET","/mcp/project/query",e)},s=function(e){return o.a.setPromise("GET","/mcp/project/query_name",e)},a=function(e){return o.a.setPromise("POST","/mcp/project/edit",e)},i=function(e){return o.a.setPromise("DELETE","/mcp/project/delete?projectId="+e)},u=function(e){return o.a.setPromise("GET","/mcp/job/query_agents",e)},c=function(e){return o.a.setPromise("GET","/mcp/group/query",e)},l=function(e){return o.a.setPromise("GET","/mcp/joblog/queryJobSchedulerLog",e)}},605:function(e,t,n){e.exports={default:n(606),__esModule:!0}},606:function(e,t,n){var o=n(26),r=o.JSON||(o.JSON={stringify:JSON.stringify});e.exports=function(e){return r.stringify.apply(r,arguments)}},611:function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var o=n(597);n.d(t,"getJobs",function(){return r}),n.d(t,"queJobs",function(){return s}),n.d(t,"queryJobNames",function(){return a}),n.d(t,"queryDBTree",function(){return i}),n.d(t,"queryTree",function(){return u}),n.d(t,"insertJob",function(){return c}),n.d(t,"queryAgents",function(){return l}),n.d(t,"checkEnvIncrement",function(){return d}),n.d(t,"checkEnvColumn",function(){return f}),n.d(t,"checkEnvMeta",function(){return m}),n.d(t,"checkEnvDDL",function(){return p}),n.d(t,"updateJobEnable",function(){return b}),n.d(t,"queryBinlog",function(){return g}),n.d(t,"trigger",function(){return h}),n.d(t,"triggerJob",function(){return j}),n.d(t,"triggerJobById",function(){return y}),n.d(t,"pauseJob",function(){return v}),n.d(t,"pauseJobById",function(){return w}),n.d(t,"resumeJob",function(){return T}),n.d(t,"resumeJobById",function(){return P}),n.d(t,"killJobById",function(){return _}),n.d(t,"removeMetaJobById",function(){return I}),n.d(t,"killAllJobById",function(){return J}),n.d(t,"queryLogs",function(){return E}),n.d(t,"queryById",function(){return S}),n.d(t,"queJobLogById",function(){return k}),n.d(t,"queloglistById",function(){return x}),n.d(t,"queryDbTable",function(){return N}),n.d(t,"queryFullDbTable",function(){return C});var r=function(e){return o.a.setPromise("GET","/mcp/job/jobs",e)},s=function(e){return o.a.setPromise("POST","/mcp/job/query_jobs",e)},a=function(e){return o.a.setPromise("GET","/mcp/job/query_job_names",e)},i=function(e){return o.a.setPromise("GET","/mcp/job/query_tree_list",e)},u=function(e){return o.a.setPromise("GET","/mcp/job/query_tree",e)},c=function(e){return o.a.setPromise("POST","/mcp/job/add_job",e)},l=function(e){return o.a.setPromise("GET","/mcp/job/query_agents",e)},d=function(e){return o.a.setPromise("POST","/mcp/job/check_env_increment",e)},f=function(e){return o.a.setPromise("POST","/mcp/job/check_env_column",e)},m=function(e){return o.a.setPromise("POST","/mcp/job/check_env_metadata?sourceId="+e,e)},p=function(e,t){return o.a.setPromise("POST","/mcp/job/check_env_ddl?routeId="+t,e)},b=function(e){return o.a.setPromise("PUT","/mcp/job/update",e)},g=function(e){return o.a.setPromise("GET","/mcp/job/query_binlog",e)},h=function(e){return o.a.setPromise("PUT","/mcp/job/trigger_save",e)},j=function(e,t){return o.a.setPromise("POST","/mcp/job/trigger/"+e,t)},y=function(e,t){return o.a.setPromise("PUT","/mcp/job/trigger?jobId="+t,e)},v=function(e,t){return o.a.setPromise("POST","/mcp/job/pause/"+e,t)},w=function(e,t){return o.a.setPromise("PUT","/mcp/job/pause?jobId="+t,e)},T=function(e,t){return o.a.setPromise("POST","/mcp/job/resume/"+e,t)},P=function(e,t){return o.a.setPromise("PUT","/mcp/job/resume?jobId="+t,e)},_=function(e,t){return o.a.setPromise("PUT","/mcp/job/kill?LogId="+t,e)},I=function(e,t){return o.a.setPromise("PUT","/mcp/job/remove?jobId="+t,e)},J=function(e,t){return o.a.setPromise("PUT","/mcp/job/configure?jobId="+t,e)},E=function(e){return o.a.setPromise("GET","/mcp/joblog/logs",e)},S=function(e,t){return o.a.setPromise("GET","/mcp/joblog/logs/"+e,t)},k=function(e){return o.a.setPromise("POST","/mcp/joblog/loginfo",e)},x=function(e){return o.a.setPromise("POST","/mcp/joblog/query_log",e)},N=function(e){return o.a.setPromise("GET","/mcp/ddl_rule/query_db_table",e)},C=function(e){return o.a.setPromise("GET","/mcp/ddl_rule/query_full_db_table",e)}},676:function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var o=n(605),r=n.n(o),s=n(611),a=n(604),i=n(598),u=n(601),c=function(){return n.e(22).then(n.bind(null,732))};t.default={name:"JobsDefinition",components:{selectMulx:c},data:function(){return{mulxOptions:{title:"Projects",a:{content:"New Project",href:"/project/NewProject"},labelId:"projectId",labelName:"projectName"},projects:[],params:{projectId:Number(this.$route.params.name||""),jobName:""},projectName:"loadding...",jobStatus:["Running","Recent","Failed"],jobsList:[],jobsList2:[],execution:this.$store.state.TEMPJOBTYPE||this.$route.params.type||"Running",jobUnwrapList:[],loading1:!1,loading2:!1}},methods:{setBtnRunType:function(e){var t=!0;switch(e.status){case"ready":t=!0;break;case"running":switch(t="ddl"==e.jobType||"full"==e.jobType,e.jobType){case"ddl":case"full":t=!0;break;case"metadata":t=!e.routeId;break;default:t=!1}break;case"wait":t="ddl"==e.jobType||"full"==e.jobType;break;case"success":case"fail":case"init":t=!1}return t},setBtnTxt:function(e){switch(e){case"running":return"Pause";case"wait":return"Resume";default:return"Run Job Now"}},killAllJobById:function(e){var t=this;this.$confirm("Modifying the information requires closing the task in execution,Confirm to close it?","",{confirmButtonText:"Sure",cancelButtonText:"Cancel"}).then(function(){t.loading1=!0,s.killAllJobById({},e.jobId).then(function(n){n.code===i.a?t.tap2Edit(e,n.items):t.$message({message:n.message,type:"error",center:!0}),t.loading1=!1}).catch(function(){t.loading1=!1})}).catch(function(){t.loading1=!1})},tap2Edit:function(e,t){this.$router.push({path:"/project/NewJob",query:{origin:"job",projectName:this.projectName,projectId:this.params.projectId,srcDb:e.srcDb,srcTable:e.srcTable,cron:e.cron,sourceId:e.sourceId,sourceName:e.sourceName,groupName:e.groupName,groupId:e.groupId,jobId:e.jobId,jobName:e.jobName,jobType:e.jobType,routeName:e.routeName,targetName:e.targetName,schemaName:e.schemaName,routeId:e.routeId,agent:e.agent,targetId:e.targetId,schemaId:e.targetSchemaId,binlogName:e.binlogName,binlogPosition:e.binlogPosition,recentBinlog:e.recentBinlog,busStartTime:e.busStartTime,busEndTime:e.busEndTime,ruleName:e.ruleName,schemaList:r()(t.tree),defaultSelect:t.checkList,dbTableList:r()(t.dbTableList),checkIncreList:r()(t.checkIncreList),checkFullList:r()(t.checkFullList)}})},setJob1:function(e){var t=this,n=e.status,o="triggerJobById",r=e.jobId,a={};"wait"==n&&(o="resumeJobById"),"running"==n&&(o="pauseJobById"),this.loading1=!0,this.$store.state.TDSELECTED=e.jobId,s[o](a,r).then(function(n){if(n.code===i.a){t.execution,e.logId;t.queJobs()}else t.$message({message:n.message,type:"error",center:!0});t.loading1=!1}).catch(function(){t.loading1=!1})},setJob2:function(e){var t=this,n=this.execution,o="killJobById",r=e.logId;"Failed"==n&&(o="triggerJobById",r=e.jobId),this.loading2=!0,s[o]({},r).then(function(e){e.code===i.a?(t.$message({message:u.e,type:"success",center:!0}),t.queJobsByStatus()):t.$message({message:e.message,type:"error",center:!0}),t.loading2=!1}).catch(function(){t.loading2=!1})},unwrapRow:function(e){var t=this.jobUnwrapList.indexOf(e.groupName);t>-1?this.jobUnwrapList.splice(t,1):this.jobUnwrapList.push(e.groupName),e.rowspan>1&&(e.unwrap=!e.unwrap,this.jobsList.forEach(function(t){t.groupName==e.groupName&&(t.first||(t.show=e.unwrap))}))},unwrapAct:function(e,t){},fmtJobs:function(e){var t=this,n=[],o={};e.forEach(function(e){e.show=!1;var n=e.groupName;if(o.hasOwnProperty(n)){o[n][0].rowspan++,o[n][0].unwrap=!1,e.first=e.show=!1;t.jobUnwrapList.indexOf(e.groupName)>-1&&(e.show=!0,o[n][0].unwrap=!0),o[n].push(e)}else e.rowspan=1,e.first=e.unwrap=e.show=!0,o[n]=[e]});for(var r in o)n=n.concat(o[r]);return n},tapLi:function(e,t){var n="/project/JobsDefinition/"+(e.projectId||this.$route.params.name);n+="/"+(t||this.$route.params.type||"Running"),t?(this.$store.state.TEMPJOBTYPE=t,this.queJobsByStatus()):this.$router.replace({path:n})},setCurtProject:function(e){this.projects=e,this.init();for(var t=0;t<e.length;t++)if(e[t].projectId==this.$route.params.name){this.projectName=e[t].projectName;break}},queProjects:function(){var e=this;if(this.$store.JOBPROJECTSEXPIRE==(new Date).getMinutes()&&this.$store.JOBPROJECTS)return void this.setCurtProject(this.$store.JOBPROJECTS);a.a().then(function(t){t.code===i.a?(e.projects=t.items,t.items.length?(e.$store.JOBPROJECTS=t.items,e.$store.JOBPROJECTSEXPIRE=(new Date).getMinutes(),e.setCurtProject(t.items)):e.projectName="No Project"):e.$message({message:t.message,type:"error",center:!0})}).catch(function(){})},queJobs:function(){var e=this;s.queJobs(this.params).then(function(t){t.code===i.a?(e.tempJbos=t.items,e.jobsList=e.fmtJobs(t.items),e.queJobsByStatus()):e.$message({message:t.message,type:"error",center:!0})}).catch(function(){})},queJobsByStatus:function(e){var t=this;this.loading2=!0;var n={},o=this.$store.state.TDSELECTED;e?e.jobId===o?(n.jobList=this.jobsList.map(function(e){return e.jobId}),this.$store.state.TDSELECTED=""):(n.jobList=[e.jobId],this.$store.state.TDSELECTED=e.jobId):n.jobList=o?[o]:this.jobsList.map(function(e){return e.jobId}),n.entity={logId:"",handleCode:this.fmtStatus(this.execution)},this.jobsList=this.fmtJobs(this.tempJbos),s.queloglistById(n).then(function(e){e.code===i.a?t.jobsList2=e.items:t.$message({message:e.message,type:"error",center:!0}),t.loading2=!1}).catch(function(){t.loading2=!1})},rowClass:function(e){var t=e.row,n=(e.rowIndex,this.$store.state.TDSELECTED);if(t.jobId==n)return t.first?t.unwrap&&1==t.rowspan?["td-selected-other"]:["td-selected"]:["td-selected-other"]},rowStyle:function(e){var t=e.row;e.rowIndex;return"display: "+(t.show?"":"none")},spanAcs:function(e){var t=e.row,n=e.columnIndex;if(0===n||1===n){var o=0;return t.first&&(o=t.unwrap?t.rowspan:1),{rowspan:o,colspan:1}}},init:function(){this.$store.state.TDSELECTED=!1,this.queJobs()},cmptime:function(e,t){e=new Date(e),t=new Date(t);var n=Math.round(t.getTime()-e.getTime())/1e3;return Math.floor(n/60)+"m"+Math.round(n%60)+"s"},fmttime:function(e,t){if(!e)return"";var n={},o=new Date(e);n.Y=o.getFullYear().toString().substring(2),n.M=o.getMonth()+1,n.D=o.getDate(),n.H=o.getHours(),n.m=o.getMinutes(),n.type="AM",n.H>11&&(n.type="PM"),n.H>12&&(n.H=n.H-12);var r=n.M+"/"+n.D+"/"+n.Y+" "+n.H+":"+n.m+" "+n.type;return t&&(r+=" "+this.cmptime(e,t)),r},fmtStatus:function(e){switch(e){case"Running":return 300;case"Recent":return 200;default:return 500}}},created:function(){this.queProjects()}}},703:function(e,t,n){t=e.exports=n(306)(),t.push([e.i,"\n.func-area .el-input[data-v-03c112df]{\r\n\twidth: 150px;margin-right: 10px;\n}\n.func-area .el-select[data-v-03c112df]{\r\n\twidth: 100px;margin-right: 10px;\n}\n.func-area .el-button[data-v-03c112df]{\r\n\tbackground: #2c6daf;\n}\n.margin0[data-v-03c112df]{\r\n\tmargin: 0;\n}\r\n",""])},737:function(e,t,n){e.exports={render:function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("section",[n("div",{staticClass:"header"},[n("div",{staticClass:"title"},[n("el-breadcrumb",{attrs:{"separator-class":"el-icon-arrow-right"}},[n("el-breadcrumb-item",{attrs:{to:{path:"/project/ProjectList"}}},[e._v("Project List")]),e._v(" "),n("el-breadcrumb-item",[e._v("Jobs Definition")])],1)],1)]),e._v(" "),n("div",{staticClass:"container pd20",staticStyle:{"min-height":"400px"}},[n("div",{staticClass:"func-area flex"},[n("selectMulx",{attrs:{data:e.projects,options:e.mulxOptions},on:{back:e.tapLi},model:{value:e.params.projectId,callback:function(t){e.$set(e.params,"projectId",t)},expression:"params.projectId"}}),e._v(" "),n("el-input",{staticStyle:{width:"220px"},attrs:{size:"medium",placeholder:"Job Search"},nativeOn:{keyup:function(t){return!t.type.indexOf("key")&&e._k(t.keyCode,"enter",13,t.key,"Enter")?null:e.init.apply(null,arguments)}},model:{value:e.params.jobName,callback:function(t){e.$set(e.params,"jobName",t)},expression:"params.jobName"}}),e._v(" "),n("el-button",{staticClass:"ml10",attrs:{size:"small",icon:"el-icon-search"},on:{click:e.init}})],1),e._v(" "),n("div",{staticClass:"mt15"},[n("el-table",{directives:[{name:"loading",rawName:"v-loading",value:e.loading1,expression:"loading1"}],staticClass:"shadow margin0",attrs:{"span-method":e.spanAcs,"row-style":e.rowStyle,"row-class-name":e.rowClass,data:e.jobsList,"empty-text":"No data",width:"60%"},on:{"row-click":e.queJobsByStatus}},[n("el-table-column",{attrs:{label:"#",width:"50px"},scopedSlots:e._u([{key:"default",fn:function(t){return[n("i",{class:{"icon-zhankai":!t.row.unwrap,"icon-shouqi":t.row.unwrap},on:{click:function(n){return n.stopPropagation(),e.unwrapRow(t.row)}}})]}}])}),e._v(" "),n("el-table-column",{attrs:{label:"Job Group",prop:"groupName"}}),e._v(" "),n("el-table-column",{attrs:{label:"Job Name",prop:"jobName"}}),e._v(" "),n("el-table-column",{attrs:{label:"Job Type",prop:"jobType"}}),e._v(" "),n("el-table-column",{attrs:{label:"Route Name",prop:"routeName"}}),e._v(" "),n("el-table-column",{attrs:{label:"Status",prop:"status"}}),e._v(" "),n("el-table-column",{staticClass:"action-td",attrs:{label:"Actions",width:"240"},scopedSlots:e._u([{key:"default",fn:function(t){return["metadata"==t.row.jobType||"increment"==t.row.jobType?n("div",[n("el-button",{attrs:{size:"small"},on:{click:function(n){return n.stopPropagation(),e.killAllJobById(t.row)}}},[e._v("Configure")]),e._v(" "),n("el-button",{staticClass:"ml10",staticStyle:{width:"109px"},attrs:{disabled:e.setBtnRunType(t.row),size:"small"},on:{click:function(n){return n.stopPropagation(),e.setJob1(t.row)}}},[e._v(e._s(e.setBtnTxt(t.row.status)))])],1):e._e(),e._v(" "),"ddl"==t.row.jobType||"full"==t.row.jobType?n("div",[n("el-button",{attrs:{size:"small"},on:{click:function(n){return n.stopPropagation(),e.killAllJobById(t.row)}}},[e._v("Configure")]),e._v(" "),n("el-button",{staticClass:"ml10",staticStyle:{width:"109px"},attrs:{disabled:e.setBtnRunType(t.row),size:"small"},on:{click:function(n){return n.stopPropagation(),e.setJob1(t.row)}}},[e._v(e._s(e.setBtnTxt(t.row.status)))])],1):e._e()]}}])})],1)],1),e._v(" "),n("div",{staticClass:"mt15"},[n("el-radio-group",{staticClass:"mb15",attrs:{fill:"#2c6daf"},on:{change:function(t){return e.tapLi(!1,e.execution)}},model:{value:e.execution,callback:function(t){e.execution=t},expression:"execution"}},e._l(e.jobStatus,function(e){return n("el-radio-button",{key:e,attrs:{label:e}})}),1),e._v(" "),n("el-table",{directives:[{name:"loading",rawName:"v-loading",value:e.loading2,expression:"loading2"}],staticClass:"shadow margin0",attrs:{data:e.jobsList2,"empty-text":"No data",width:"60%"}},[n("el-table-column",{attrs:{width:"100",label:"Log Id"},scopedSlots:e._u([{key:"default",fn:function(t){return[n("router-link",{staticClass:"deepblue",attrs:{to:"/project/JobExecution/"+t.row.jobId+"/"+t.row.logId+"/"+t.row.triggerTime}},[e._v(e._s(t.row.logId))])]}}])}),e._v(" "),n("el-table-column",{attrs:{label:"Job Id",prop:"jobId"}}),e._v(" "),n("el-table-column",{attrs:{label:"Job Name",prop:"jobName"}}),e._v(" "),n("el-table-column",{attrs:{label:"At Time"},scopedSlots:e._u([{key:"default",fn:function(t){return[n("p",[e._v(e._s(e.fmttime(t.row.handleTime,t.row.handleEndTime)))])]}}])}),e._v(" "),"Recent"!=e.execution?n("el-table-column",{attrs:{width:"100",label:"Action"},scopedSlots:e._u([{key:"default",fn:function(t){return[n("a",{staticClass:"deepblue",attrs:{href:"javascript:"},on:{click:function(n){return e.setJob2(t.row)}}},[e._v(e._s(300==t.row.handleCode?"Kill":"Run Again"))])]}}],null,!1,178564925)}):e._e()],1)],1)])])},staticRenderFns:[]},e.exports.render._withStripped=!0},763:function(e,t,n){var o=n(703);"string"==typeof o&&(o=[[e.i,o,""]]),o.locals&&(e.exports=o.locals);n(308)("5acd3823",o,!1)}});