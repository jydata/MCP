webpackJsonp([16],{586:function(e,t,a){a(780);var n=a(307)(a(674),a(754),"data-v-7412953c",null);n.options.__file="D:\\jiuye-pro\\jydata-git\\mcp-new\\mcp-ui\\src\\pages\\meta\\GenerateTargetDDL.vue",n.esModule&&Object.keys(n.esModule).some(function(e){return"default"!==e&&"__esModule"!==e})&&console.error("named exports are not supported in *.vue files."),n.options.functional&&console.error("[vue-loader] GenerateTargetDDL.vue: functional components are not supported with templates, they should use render functions."),e.exports=n.exports},597:function(e,t,a){"use strict";var n=a(310),s=a.n(n),r=a(599),i=a.n(r),o=a(600),l=a.n(o),c=function(){function e(){i()(this,e)}return l()(e,null,[{key:"setPromise",value:function(e,t,a){return new s.a(function(n,s){switch(e.toUpperCase()){case"GET":axios.get(t,{params:a}).then(function(e){e?n(e.data):s()});break;case"POST":case"PUT":axios({method:e,url:t,data:a}).then(function(e){e?n(e.data):s()});break;case"DELETE":axios.delete(t,{data:a}).then(function(e){e?n(e.data):s()})}})}}]),e}();t.a=c},598:function(e,t,a){"use strict";a.d(t,"a",function(){return n}),a.d(t,"b",function(){return s});var n="SUCCESS",s="ERROR"},599:function(e,t,a){"use strict";t.__esModule=!0,t.default=function(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}},600:function(e,t,a){"use strict";t.__esModule=!0;var n=a(309),s=function(e){return e&&e.__esModule?e:{default:e}}(n);t.default=function(){function e(e,t){for(var a=0;a<t.length;a++){var n=t[a];n.enumerable=n.enumerable||!1,n.configurable=!0,"value"in n&&(n.writable=!0),(0,s.default)(e,n.key,n)}}return function(t,a,n){return a&&e(t.prototype,a),n&&e(t,n),t}}()},601:function(e,t,a){"use strict";a.d(t,"e",function(){return n}),a.d(t,"j",function(){return s}),a.d(t,"b",function(){return r}),a.d(t,"f",function(){return i}),a.d(t,"k",function(){return o}),a.d(t,"l",function(){return l}),a.d(t,"c",function(){return c}),a.d(t,"g",function(){return u}),a.d(t,"i",function(){return d}),a.d(t,"h",function(){return m}),a.d(t,"d",function(){return f}),a.d(t,"a",function(){return h});var n="Operation is successful！",s="Add successfully！",r="Updated successfully！",i="Deleted successfully！",o="Connection test successfully!",l="Connection test successfully!",c="Saved successfully!",u="Saved failed!",d="Synchronized successfully!",m="Transform successfully!",f="Reset password successfully！",h="Modify successfully！"},603:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var n=a(597);a.d(t,"getDSLink",function(){return s}),a.d(t,"getLoads",function(){return r}),a.d(t,"saveDSLink",function(){return i}),a.d(t,"testDSLink",function(){return o}),a.d(t,"getDBTree",function(){return l}),a.d(t,"getCount",function(){return c}),a.d(t,"getMetaTable",function(){return u}),a.d(t,"getMetaColumn",function(){return d}),a.d(t,"getTableDDL",function(){return m}),a.d(t,"getSample",function(){return f}),a.d(t,"syncRatio",function(){return h}),a.d(t,"batchSyncdata",function(){return p}),a.d(t,"getDSRoutes",function(){return g}),a.d(t,"saveRouteName",function(){return b}),a.d(t,"querySchemalists",function(){return v}),a.d(t,"saveSchemalists",function(){return D}),a.d(t,"createSchemalists",function(){return y}),a.d(t,"updateRouterStatus",function(){return x}),a.d(t,"updateRouterName",function(){return _}),a.d(t,"saveRoute",function(){return L}),a.d(t,"existTables",function(){return S}),a.d(t,"queryRoute",function(){return T}),a.d(t,"getRules",function(){return P}),a.d(t,"getRuleType",function(){return w}),a.d(t,"saveRules",function(){return N}),a.d(t,"delRules",function(){return I}),a.d(t,"queryTarget",function(){return C}),a.d(t,"getSchema",function(){return k}),a.d(t,"getRule",function(){return R}),a.d(t,"getDatabase",function(){return O}),a.d(t,"getRoutes",function(){return q}),a.d(t,"getMetadata",function(){return $}),a.d(t,"getColumns",function(){return E}),a.d(t,"getDDL",function(){return G}),a.d(t,"getBinlog",function(){return M}),a.d(t,"transformDDL",function(){return j}),a.d(t,"transformSingleDDL",function(){return A}),a.d(t,"syncDDL",function(){return B}),a.d(t,"syncSingleDDL",function(){return H}),a.d(t,"saveDDL",function(){return F});var s=function(e){return n.a.setPromise("GET","/mcp/conn/query",e)},r=function(e){return n.a.setPromise("POST","/mcp/conn/load_options",e)},i=function(e){return n.a.setPromise("POST","/mcp/conn/save",e)},o=function(e){return n.a.setPromise("POST","/mcp/conn/test",e)},l=function(e){return n.a.setPromise("POST","/mcp/sync/query_dstree",e)},c=function(e){return n.a.setPromise("POST","/mcp/sync/calc_db_info",e)},u=function(e,t){return n.a.setPromise("POST","/mcp/sync/query_table?dataList="+t,e)},d=function(e,t){return n.a.setPromise("POST","/mcp/sync/query_column?dataList="+t,e)},m=function(e,t){return n.a.setPromise("POST","/mcp/sync/load_ddl?dataList="+t,e)},f=function(e,t){return n.a.setPromise("POST","/mcp/sync/query_example?dataList="+t,e)},h=function(e,t){return n.a.setPromise("POST","/mcp/sync/sync_ratio?sourceId="+t,e)},p=function(e){return n.a.setPromise("POST","/mcp/sync/batch_sync",e)},g=function(e){return n.a.setPromise("GET","/mcp/route/query",e)},b=function(e){return n.a.setPromise("POST","/mcp/route/update_route_name",e)},v=function(e){return n.a.setPromise("POST","/mcp/route/query_schema",e)},D=function(e){return n.a.setPromise("POST","/mcp/route/save_schema",e)},y=function(e){return n.a.setPromise("POST","/mcp/route/create_schema",e)},x=function(e){return n.a.setPromise("POST","/mcp/route/update_status",e)},_=function(e){return n.a.setPromise("POST","/mcp/route/update_name",e)},L=function(e){return n.a.setPromise("POST","/mcp/route/save",e)},S=function(e){return n.a.setPromise("GET","/mcp/route/exist_table",e)},T=function(e){return n.a.setPromise("GET","/mcp/route/job_routes",e)},P=function(e){return n.a.setPromise("GET","/mcp/rule/query_list",e)},w=function(e){return n.a.setPromise("GET","/mcp/rule/query_type",e)},N=function(e){return n.a.setPromise("POST","/mcp/rule/save",e)},I=function(e){return n.a.setPromise("POST","/mcp/rule/delete",e)},C=function(e){return n.a.setPromise("GET","/mcp/ddl/query_target_info",e)},k=function(e){return n.a.setPromise("GET","/mcp/ddl/query_db_trees",e)},R=function(e){return n.a.setPromise("GET","/mcp/ddl/query_rules",e)},O=function(e){return n.a.setPromise("GET","/mcp/ddl/query_source_db",e)},q=function(e){return n.a.setPromise("GET","/mcp/ddl/query_routes",e)},$=function(e){return n.a.setPromise("GET","/mcp/ddl/query_table_meta",e)},E=function(e){return n.a.setPromise("GET","/mcp/ddl/query_columns",e)},G=function(e){return n.a.setPromise("POST","/mcp/ddl/query_ddl_info",e)},M=function(e,t,a){return n.a.setPromise("GET","/mcp/ddl/query_binlogddl_info/"+e+"/"+t+"/"+a)},j=function(e,t,a,s){return n.a.setPromise("POST","/mcp/ddl/batch_generate_sql/"+t+"/"+a+"/"+s,e)},A=function(e,t,a,s,r,i,o){return n.a.setPromise("POST","/mcp/ddl/generate_sql/"+t+"/"+a+"/"+s+"/"+r+"/"+i+"/"+o,e)},B=function(e,t){return n.a.setPromise("POST","/mcp/ddl/multi_exec_sql/"+e+"/"+t)},H=function(e,t,a,s,r,i){return n.a.setPromise("POST","/mcp/ddl/exec_sql/"+t+"/"+a+"/"+s+"/"+r+"/"+i,e)},F=function(e,t,a){return n.a.setPromise("POST","/mcp/ddl/save_sql/"+t+"/"+a,e)}},674:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var n=a(121),s=a.n(n),r=a(603),i=a(598),o=a(601),l=a(789);a.n(l);t.default={name:"GenerateTargetDDL",data:function(){return{treeHeight:document.body.clientHeight-275,title:"",multipleSelection:[],filterText:"",ruleData:"",nodeSourceId:"",nodeRouteId:"",sourceSchema:"",tableName:"",tableDDL:"",indexDDL:"",operateDDL:"",tablePanel:!0,indexPanel:!1,operatePanel:!1,bgTable:"background-color:#2c6daf;color:#fff",bgIndex:"",bgOperate:"",targetList:[],schemaList:[],routeList:[],ruleList:[],DBList:[],metadataList:[],columnList:[],DDLList:[],defaultProps:{children:"children",label:"label"},dialogRule:!1,transformId:"",saveId:"",selectTarget:"",isDDLFlag:!1,selected:"",isCenter:!1,isTarget:"",isLoading:!1,routeId:"",sourceId:"",schemaId:"",schemaName:"",targetId:""}},watch:{filterText:function(e){this.$refs.tree.filter(e)}},created:function(){void 0===this.$route.query.schemaId?(this.title="Generate Target DDL",this.isTarget=!0,this.queryTarget()):(this.title="DS Routing -> Generate Target DDL",this.isTarget=!1,this.sourceId=this.$route.query.sourceId,this.schemaId=this.$route.query.schemaId,this.schemaName=this.$route.query.schemaName,this.targetId=this.$route.query.targetId,this.getSchema(),this.getRoutes())},methods:{handleSelectionChange:function(e){this.multipleSelection=e},changeValue:function(e){var t=e.filter(function(e){return"All"===e}).length;if("All"===e[0])e.splice(1,e.length-1);else if("All"!==e[0]&&t>0){var a=e.indexOf("All");e.splice(a,1)}},filterNode:function(e,t){return!e||-1!==t.label.indexOf(e)},showTableDDL:function(){this.tablePanel=!0,this.indexPanel=!1,this.operatePanel=!1,this.bgTable="background-color:#2c6daf;color:#fff",this.bgIndex="",this.bgOperate=""},showIndexDDL:function(){this.tablePanel=!1,this.indexPanel=!0,this.operatePanel=!1,this.bgTable="",this.bgIndex="background-color:#2c6daf;color:#fff",this.bgOperate=""},showOperateDDL:function(){this.tablePanel=!1,this.indexPanel=!1,this.operatePanel=!0,this.bgTable="",this.bgIndex="",this.bgOperate="background-color:#2c6daf;color:#fff"},changeTarget:function(e){this.routeList=[],this.targetId=e[0],this.schemaId=e[1],this.schemaName=this.$refs.cascader.currentLabels[1],this.getSchema(),this.getRoutes()},queryTarget:function(){var e=this;r.queryTarget({}).then(function(t){if(t.code===i.a){var a=!0,n=!1,r=void 0;try{for(var o,l=s()(t.items);!(a=(o=l.next()).done);a=!0){var c=o.value,u=[],d=!0,m=!1,f=void 0;try{for(var h,p=s()(c.entities);!(d=(h=p.next()).done);d=!0){var g=h.value,b={label:g.schemaName,value:g.schemaId,children:null};u.push(b)}}catch(e){m=!0,f=e}finally{try{!d&&p.return&&p.return()}finally{if(m)throw f}}var v={value:c.entities[0].targetId,label:c.targetName,children:u};e.targetList.push(v)}}catch(e){n=!0,r=e}finally{try{!a&&l.return&&l.return()}finally{if(n)throw r}}}else e.$message({message:t.message,type:"error"})}).catch(function(){})},getSchema:function(){var e=this;r.getSchema({targetId:this.targetId}).then(function(t){t.code===i.a?e.schemaList=t.items:e.$message({message:t.message,type:"error"})}).catch(function(){})},getRoutes:function(){var e=this;r.getRoutes({targetId:this.targetId,targetSchemaId:this.schemaId}).then(function(t){t.code===i.a?e.routeList=t.items:e.$message({message:t.message,type:"error"})}).catch(function(){})},getDatabase:function(e){var t=this;r.getDatabase({sourceId:e.sourceId}).then(function(e){e.code===i.a?t.DBList=e.items:t.$message({message:e.message,type:"error"})}).catch(function(){})},getRules:function(){var e=this;r.getRules({}).then(function(t){t.code===i.a?e.ruleList=t.items:e.$message({message:t.message,type:"error"})}).catch(function(){})},getMetadata:function(e,t){var a=this;if(this.isDDLFlag=!1,this.metadataList=[],this.columnList=[],this.tableDDL="",this.indexDDL="",this.operateDDL="",e.isleaf){this.isDDLFlag=!0,this.ruleData="";var n=t.parent.parent.data.id.split("#");this.nodeSourceId=n[0],this.nodeRouteId=n[1],this.sourceSchema=t.parent.data.label,this.tableName=t.label,r.getMetadata({sourceId:this.nodeSourceId,schema:this.sourceSchema,table:this.tableName}).then(function(e){e.code===i.a?a.metadataList=e.items:a.$message({message:e.message,type:"error"})}).catch(function(){});var s=[this.sourceSchema,this.tableName,this.nodeSourceId];r.getColumns({param:s.join("#")}).then(function(e){e.code===i.a?a.columnList=e.items:a.$message({message:e.message,type:"error"})}).catch(function(){}),r.getDDL({routeId:this.nodeRouteId,schemaId:this.schemaId,srcDbName:this.sourceSchema,srcTableName:this.tableName}).then(function(e){e.code===i.a?(a.DDLList=e.items,a.tableDDL=a.DDLList.ddlSql,a.indexDDL=a.DDLList.indexSql,a.transformId=a.DDLList.id):a.$message({message:e.message,type:"error"})}).catch(function(){}),r.getBinlog(this.nodeRouteId,this.sourceSchema,this.tableName).then(function(e){e.code===i.a?(a.operateDDL=e.items,null==e.items?(a.operateDDL="No Data",a.isCenter=!0):a.isCenter=!1):a.$message({message:e.message,type:"error"})}).catch(function(){})}},gotoDDL:function(e){var t=this.ruleList.filter(function(t){return t.ruleName==e.ruleName}),a=t.length?t[0].ruleId:"";void 0===e.ruleName||""==e.ruleName?this.dialogRule=!0:this.$router.push({path:"/project/NewJob",query:{origin:"DDL",routeId:e.routeId,routeName:e.routeName,sourceId:e.sourceId,sourceName:e.sourceName,schemaId:this.schemaId,targetId:this.targetId,targetName:e.targetName,targetSchema:this.schemaName,ruleId:a,ruleName:e.ruleName,database:e.database}})},transformSingleDDL:function(){var e=this;if(""===this.transformId&&(this.transformId=0),""===this.ruleData)this.dialogRule=!0;else if(0==this.columnList.filter(function(e){return"PRI"==e.columnKey}).length)this.$message.error("This table must have a primary key!");else{var t=[];t=""==this.multipleSelection?this.columnList.filter(function(e){return"PRI"==e.columnKey}):this.multipleSelection,r.transformSingleDDL(t,1,this.nodeRouteId,this.schemaId,this.transformId,this.ruleData,this.sourceSchema).then(function(t){t.code===i.a?(e.DDLList=t.items,e.tableDDL=t.items.ddlSql,e.indexDDL=t.items.indexSql,e.$message({message:o.h,type:"success"})):e.$message({message:t.message,type:"error"})}).catch(function(){})}},syncSingleDDL:function(){var e=this,t=this.$refs.ddl.innerText;this.$refs.index.innerText;if(""===this.ruleData)this.dialogRule=!0;else if(-1==t.indexOf("CREATE TABLE"))this.$message.error("DDL cannot be empty!");else{var a=[];this.isLoading=!0,a.push(this.DDLList),r.syncSingleDDL(a,this.nodeSourceId,this.ruleData,this.sourceSchema,this.nodeRouteId,this.schemaId).then(function(t){t.code===i.a?e.$message({message:o.i,type:"success"}):e.$message({message:t.message,type:"error"}),e.isLoading=!1}).catch(function(){e.isLoading=!1})}},saveSingleDDL:function(){var e=this,t=this.$refs.ddl.innerText,a=this.$refs.index.innerText;if(-1!=t.indexOf("CREATE TABLE")||-1!=a.indexOf("CREATE INDEX")){this.isLoading=!0,this.DDLList.ddlSql=this.$refs.ddl.innerText,this.DDLList.indexSql=this.$refs.index.innerText;var n=this.DDLList;r.saveDDL(n,this.sourceSchema,this.nodeRouteId).then(function(t){t.code===i.a?(e.DDLList=t.items,e.$message({message:o.c,type:"success"}),setTimeout(function(){e.isLoading=!1},1e3)):(e.isLoading=!1,e.$message({message:t.message,type:"error"}))}).catch(function(){e.isLoading=!1})}else this.$message.error("DDL cannot be empty!")}}}},720:function(e,t,a){t=e.exports=a(306)(),t.push([e.i,"\n.container[data-v-7412953c] {\n  height: auto;\n  display: -ms-flexbox;\n  display: flex;\n}\n.container .left[data-v-7412953c] {\n  width: 20%;\n  height: auto;\n}\n.container .left .el-cascader[data-v-7412953c] {\n  margin-top: 15px;\n  width: 100%;\n}\n.container .left .tree .el-tree[data-v-7412953c] {\n  overflow-y: auto;\n  max-height: 560px;\n}\n.container .right[data-v-7412953c] {\n  margin-left: 15px;\n  width: 70%;\n  height: 100%;\n}\n.container .right ul[data-v-7412953c] {\n  display: -ms-flexbox;\n  display: flex;\n}\n.container .right ul li[data-v-7412953c] {\n  height: 30px;\n  display: -ms-flexbox;\n  display: flex;\n  -ms-flex-pack: distribute;\n      justify-content: space-around;\n  -ms-flex-align: center;\n      align-items: center;\n  padding: 6px;\n}\n.container .right .down > .tabs ul > li[data-v-7412953c] {\n  cursor: pointer;\n}\n.container .right .down > .tabs ul > li[data-v-7412953c]:hover {\n  color: #2c6daf;\n}\n.cascader-header[data-v-7412953c] {\n  color: #aaa;\n  background-color: #f7f7f7;\n  width: 100%;\n  height: 40px;\n  line-height: 40px;\n}\n.card-box[data-v-7412953c] {\n  width: 100%;\n  cursor: pointer;\n  height: 40px;\n  line-height: 40px;\n}\n.card-title[data-v-7412953c] {\n  display: -ms-flexbox;\n  display: flex;\n  -ms-flex-align: center;\n      align-items: center;\n  -ms-flex-pack: justify;\n      justify-content: space-between;\n}\n.card[data-v-7412953c] {\n  margin-top: -40px;\n  margin-left: 20%;\n  z-index: 100;\n  width: 15%;\n  display: inline;\n  position: absolute;\n  text-align: center;\n  background-color: #fff;\n  box-shadow: #ddd 0 0 10px;\n}\n.card li[data-v-7412953c]:hover {\n  background-color: #f5f7fa;\n}\n.bgColor[data-v-7412953c] {\n  background-color: #f5f7fa;\n}\n",""])},754:function(e,t,a){e.exports={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("section",[a("div",{staticClass:"header"},[a("div",{staticClass:"title"},[a("el-breadcrumb",{attrs:{"separator-class":"el-icon-arrow-right"}},[a("el-breadcrumb-item",[e._v("Generate Target DDL")])],1)],1)]),e._v(" "),a("div",{staticClass:"container gray mt0"},[a("div",{staticClass:"left flex fxcolumn"},[e.isTarget?a("div",[a("el-cascader",{ref:"cascader",attrs:{options:e.targetList,placeholder:"Target / Schema",size:"medium"},on:{change:e.changeTarget}})],1):e._e(),e._v(" "),a("div",{staticClass:"shadow tree flex1"},[a("el-input",{staticClass:"search-input",attrs:{"prefix-icon":"el-icon-search",placeholder:"Search sourceSchema or table"},model:{value:e.filterText,callback:function(t){e.filterText=t},expression:"filterText"}}),e._v(" "),a("el-tree",{ref:"tree",staticClass:"pd10 mcp-rollbar",style:{maxHeight:e.treeHeight+"px"},attrs:{"empty-text":"No data",data:e.schemaList,props:e.defaultProps,accordion:"","filter-node-method":e.filterNode},on:{"node-click":e.getMetadata}})],1)]),e._v(" "),a("div",{staticClass:"right flex1"},[a("div",{staticClass:"top"},[a("el-table",{staticClass:"shadow",attrs:{data:e.routeList,"empty-text":"No data"}},[a("el-table-column",{attrs:{type:"expand"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("el-table",{staticClass:"no-header no-table",attrs:{data:t.row.children,"empty-text":"No data"}},[a("el-table-column",{attrs:{width:"48px"}}),e._v(" "),a("el-table-column",{attrs:{prop:"routeName","min-width":"92px"}}),e._v(" "),a("el-table-column",{attrs:{prop:"sourceName","min-width":"93px"}}),e._v(" "),a("el-table-column",{attrs:{prop:"dbName","min-width":"178px"}}),e._v(" "),a("el-table-column",{attrs:{prop:"targetName","min-width":"93px"}}),e._v(" "),a("el-table-column",{attrs:{"min-width":"93px"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("span",[e._v(e._s(e.schemaName))])]}}],null,!0)}),e._v(" "),a("el-table-column",{attrs:{prop:"ruleName","min-width":"125px"}}),e._v(" "),a("el-table-column",{attrs:{"min-width":"93px"}})],1)]}}])}),e._v(" "),a("el-table-column",{attrs:{"min-width":"120px",label:"Routing Name",prop:"routeName"}}),e._v(" "),a("el-table-column",{attrs:{"min-width":"120px",label:"Source Name",prop:"sourceName"}}),e._v(" "),a("el-table-column",{attrs:{label:"Database","min-width":"230px"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("el-select",{staticClass:"db-select",attrs:{placeholder:"Please select",filterable:"",multiple:"","collapse-tags":""},on:{focus:function(a){return e.getDatabase(t.row)},change:e.changeValue},model:{value:t.row.database,callback:function(a){e.$set(t.row,"database",a)},expression:"scope.row.database"}},e._l(e.DBList,function(e){return a("el-option",{key:e,attrs:{label:e,value:e}})}),1)]}}])}),e._v(" "),a("el-table-column",{attrs:{"min-width":"120px",label:"Target Name",prop:"targetName"}}),e._v(" "),a("el-table-column",{attrs:{"min-width":"120px",label:"Target Schema"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("span",[e._v(e._s(e.schemaName))])]}}])}),e._v(" "),a("el-table-column",{attrs:{"min-width":"160px",label:"Rule Name"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("el-select",{attrs:{placeholder:"Please select",filterable:""},on:{focus:e.getRules},model:{value:t.row.ruleName,callback:function(a){e.$set(t.row,"ruleName",a)},expression:"scope.row.ruleName"}},e._l(e.ruleList,function(e){return a("el-option",{key:e.ruleName,attrs:{label:e.ruleName,value:e.ruleName}})}),1)]}}])}),e._v(" "),a("el-table-column",{attrs:{label:"Action","min-width":"120px"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("el-button",{attrs:{size:"small"},on:{click:function(a){return e.gotoDDL(t.row)}}},[e._v("Create Job")])]}}])})],1)],1),e._v(" "),a("div",[e._m(0),e._v(" "),a("div",{staticClass:"meta-content",staticStyle:{"margin-top":"-10px"}},[a("el-table",{staticClass:"shadow",attrs:{"empty-text":"No data",data:e.metadataList}},[a("el-table-column",{attrs:{label:"Source",prop:"sourceName"}}),e._v(" "),a("el-table-column",{attrs:{label:"Schema",prop:"database"}}),e._v(" "),a("el-table-column",{attrs:{label:"Table Name",prop:"tableName"}}),e._v(" "),a("el-table-column",{attrs:{label:"Engine",prop:"engine"}}),e._v(" "),a("el-table-column",{attrs:{label:"Charset",prop:"charset"}}),e._v(" "),a("el-table-column",{attrs:{label:"Table Comment",prop:"tableComment",width:"150px"}}),e._v(" "),a("el-table-column",{attrs:{label:"Table Rows",prop:"tableRows"}}),e._v(" "),a("el-table-column",{attrs:{label:"Rule Name"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("el-select",{attrs:{placeholder:"Please select",filterable:"",clearable:""},on:{focus:e.getRules},model:{value:e.ruleData,callback:function(t){e.ruleData=t},expression:"ruleData"}},e._l(e.ruleList,function(e){return a("el-option",{key:e.ruleName,attrs:{label:e.ruleName,value:e.ruleName}})}),1)]}}])})],1),e._v(" "),a("el-table",{staticClass:"shadow",attrs:{data:e.columnList,"empty-text":"No data"},on:{"selection-change":e.handleSelectionChange}},[a("el-table-column",{attrs:{type:"selection",width:"55"}}),e._v(" "),a("el-table-column",{attrs:{label:"Column",prop:"columnName"}}),e._v(" "),a("el-table-column",{attrs:{label:"#",type:"index"}}),e._v(" "),a("el-table-column",{attrs:{label:"Data Type",prop:"columnType"}}),e._v(" "),a("el-table-column",{attrs:{label:"Not Null",prop:"isNullable",align:"center"},scopedSlots:e._u([{key:"default",fn:function(t){return[a("el-checkbox",{attrs:{"true-label":"NO","false-label":"YES",disabled:"true"},model:{value:t.row.isNullable,callback:function(a){e.$set(t.row,"isNullable",a)},expression:"scope.row.isNullable"}})]}}])}),e._v(" "),a("el-table-column",{attrs:{label:"Key",prop:"columnKey"}}),e._v(" "),a("el-table-column",{attrs:{label:"Default",prop:"columnDefault"}}),e._v(" "),a("el-table-column",{attrs:{label:"Comment",prop:"columnComment"}})],1)],1)]),e._v(" "),a("div",{staticClass:"down"},[a("div",{staticClass:"tabs"},[a("div",{staticStyle:{display:"flex","justify-content":"space-between"}},[a("ul",[a("li",{staticClass:"shadow",staticStyle:{width:"90px",height:"30px"},style:e.bgTable,on:{click:e.showTableDDL}},[e._v("TableDDL")]),e._v(" "),a("li",{staticClass:"shadow",staticStyle:{width:"90px",height:"30px"},style:e.bgIndex,on:{click:e.showIndexDDL}},[e._v("Index DDL")]),e._v(" "),a("li",{staticClass:"shadow",staticStyle:{width:"90px",height:"30px"},style:e.bgOperate,on:{click:e.showOperateDDL}},[e._v("Operate DDL")])]),e._v(" "),a("span",{directives:[{name:"show",rawName:"v-show",value:e.isDDLFlag,expression:"isDDLFlag"}],staticStyle:{"margin-top":"20px"}},[a("i",{staticClass:"icon-cogs mr20",attrs:{title:"Transform DDL"},on:{click:e.transformSingleDDL}}),e._v(" "),a("i",{directives:[{name:"loading",rawName:"v-loading.fullscreen.lock",value:e.isLoading,expression:"isLoading",modifiers:{fullscreen:!0,lock:!0}}],staticClass:"icon-floppy-o mr20",attrs:{title:"Save"},on:{click:e.saveSingleDDL}}),e._v(" "),a("i",{directives:[{name:"loading",rawName:"v-loading.fullscreen.lock",value:e.isLoading,expression:"isLoading",modifiers:{fullscreen:!0,lock:!0}}],staticClass:"icon-refresh",attrs:{title:"Synchronize"},on:{click:e.syncSingleDDL}})])]),e._v(" "),a("div",{staticClass:"tabs-content",staticStyle:{"margin-top":"-10px"}},[a("div",{directives:[{name:"show",rawName:"v-show",value:e.tablePanel,expression:"tablePanel"}],staticClass:"table-content shadow"},[a("div",{directives:[{name:"highlight",rawName:"v-highlight"}]},[a("pre",[e._v("                 "),a("code",{ref:"ddl",attrs:{contenteditable:"true"},domProps:{innerHTML:e._s(e.tableDDL)}},[e._v("\n                 ")]),e._v("\n               ")])])]),e._v(" "),a("div",{directives:[{name:"show",rawName:"v-show",value:e.indexPanel,expression:"indexPanel"}],staticClass:"index-content shadow"},[a("div",{directives:[{name:"highlight",rawName:"v-highlight"}]},[a("pre",[e._v("                 "),a("code",{ref:"index",attrs:{contenteditable:"true"},domProps:{innerHTML:e._s(e.indexDDL)}},[e._v("\n                 ")]),e._v("\n               ")])])]),e._v(" "),a("div",{directives:[{name:"show",rawName:"v-show",value:e.operatePanel,expression:"operatePanel"}],class:["operate-content","shadow",{center:e.isCenter}]},[a("div",{directives:[{name:"highlight",rawName:"v-highlight"}]},[a("pre",[e._v("                 "),a("code",{domProps:{innerHTML:e._s(e.operateDDL)}},[e._v("\n                 ")]),e._v("\n               ")])])])])])])])]),e._v(" "),a("el-dialog",{attrs:{title:"Message",visible:e.dialogRule},on:{"update:visible":function(t){e.dialogRule=t}}},[a("span",[e._v("Please select rule!")])])],1)},staticRenderFns:[function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",[a("ul",[a("li",{staticClass:"shadow",staticStyle:{"background-color":"#2c6daf",color:"#fff",width:"90px"}},[e._v("Metadata")])])])}]},e.exports.render._withStripped=!0},780:function(e,t,a){var n=a(720);"string"==typeof n&&(n=[[e.i,n,""]]),n.locals&&(e.exports=n.locals);a(308)("6e6767ce",n,!1)}});