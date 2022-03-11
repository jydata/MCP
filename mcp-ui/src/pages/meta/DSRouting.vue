<template>
<section>
	<!--头部-->
	<div class="header">
		<div class="title">
			<el-breadcrumb separator-class="el-icon-arrow-right">
				<el-breadcrumb-item to="/meta/DSConnections">DS Connections</el-breadcrumb-item>
				<el-breadcrumb-item>DS Routing</el-breadcrumb-item>
			</el-breadcrumb>
		</div>
	</div>
	<div class="flex" style="flex-direction: row-reverse;padding-top: 15px;padding-right: 20px;">
		<el-button @click="addRouting" class="" size="small">New Routing</el-button>
	</div>
	<!--内容-->
	<div class="container">
		<div>
			<ul>
				<li class="main shadow flex group-li" v-for="(item, index) in groupItems">
					<div class="left flex1">
						<div class="routing flex">
							<div @click="clickLine(item.info)" :class="['angle','angle-'+item.info.routeStatus,{'angle-focus':focusLine.routeId==item.info.routeId}]" v-for="item in item.lines" :style="{left:item.x+'px',top:item.y+'px',width:item.z+'px',transform:'rotate('+item.deg+'deg)','transform-origin':'0 50%'}" :key="item.info.routeId">
								<input maxlength="20" :disabled="item.info.routeStatus==0" :style="{width:item.info.routeName.length*14+'px'}" @change="item.info.routeId?updateRouterName(item.info):saveRoute(item.info)" type="text" v-model="item.info.routeName">
								<div @click="updateRouterStatus(item.info)"></div>
							</div>
							<div class="source flex1">
								<div class="circle shadow" :id="'source-'+s.sourceId" v-for="s in item.sources">
									<img src="../../assets/img/MySQL.png" width="80px" >
									<span class="mt5">{{s.sourceName}}</span>
								</div>
							</div>
							<div class="target">
								<div class="circle shadow" :id="'target-'+t.targetId" v-for="t in item.targets" @click="showAddPanel(t, index)">
									<img src="../../assets/img/phoenix.png" width="80px" >
									<span class="mt10">{{t.targetName}}</span>
									<i v-show="item.currtTarget.targetId==t.targetId" class="icon-thumb-tack"></i>
								</div>
							</div>
						</div>
					</div>
					<div class="right flex fxcolumn" v-show="addSchemaPanel">
						<div v-if="item.showSchema" class="btn-set flex">
							<div class="button">
								<i v-loading.fullscreen.lock="fullload" @click="saveSchemalists(2, false,index)" class="icon-refresh"></i>
							</div>
							<div class="button">
								<i v-loading.fullscreen.lock="fullload" @click="saveSchemalists(1, false,index)" class="icon-floppy-o"></i>
							</div>
						</div>
						<div v-if="item.showSchema" class="add-panel">
							<el-table class="shadow" empty-text="No data" :data="item.schemas">
								<el-table-column label="Target Schema" prop="targetSchema">
									<template slot-scope="scope">
										<el-input v-if="(scope.row.executeFlag==0&&scope.row.deleteFlag==1)||scope.row.newline" @keyup.enter.native="saveSchemalists(50, scope.row, index)" maxlength=20 v-model="scope.row.schemaName"></el-input>
										<p v-else style="text-align: left;text-indent: 6px;">{{scope.row.schemaName}}</p>
									</template>
								</el-table-column>
								<el-table-column label="Synchronized" prop="syncFlag">
									<template slot-scope="scope">
										<el-checkbox
										v-if="scope.row.executeFlag==1||scope.row.newline||scope.row.deleteFlag==0"
										:disabled="true"
										active-color="#2c6daf"
										true-label="1"
										false-label="0"
										v-model="scope.row.executeFlag">
									</el-checkbox>
									<el-checkbox
										v-else
										active-color="#2c6daf"
										true-label="1"
										false-label="0"
										v-model="scope.row.checked">
									</el-checkbox>
								</template>
							</el-table-column>
							<el-table-column label="Status" prop="status">
								<template slot-scope="scope">
									<el-switch
										:disabled="scope.row.newline"
										v-model="scope.row.deleteFlag"
										active-color="#2c6daf"
										active-value="1"
										inactive-value="0"
										@change="saveSchemalists(3, scope.row, index)"
										>
									</el-switch>
								</template>
							</el-table-column>
								<el-table-column label="Action">
									<template slot-scope="scope">
										<i v-if="scope.row.newline" @click="addLine(scope.$index, index)" :class="{'icon-plus-circle':item.schemas.length==scope.$index+1,'icon-trash':item.schemas.length!=scope.$index+1}"></i>
										<a class="deepblue" v-if="scope.row.executeFlag==1&&scope.row.deleteFlag==1&&item.hasDDL" @click="tapTo(scope.row, index)">DDL</a>
									</template>
								</el-table-column>
							</el-table>
						</div>
					</div>
				</li>
				<li class="main shadow group-li" style="height: 500px;" v-show="noRouting">
					<div class="no-routing">no data</div>
				</li>
				
			</ul>
		</div>
	</div>
</section>
</template>

<script>
import * as Meta from '@api/Meta';
import * as Code from '@config/code';
import * as Tips from '@config/tips';
export default {
	name: "DSRouting",
	data(){
		return{
			routeList: [],
			textFlag: true,
			addSchemaPanel: false,
			lockIcon: 'icon-lock',
			schemas: [],
			sources:[],
			targets:[],
			angle:{},
			lines:[],
			currtTarget:{},
			fullload:false,
			focusLine:{},
			isLast:false,
			hasDDL:false,
			noRouting:false,
			fmtedItems:[],
			groupItems:[],
		}
	},
	created(){
		this.$store.state.sourceTargetMust=false;
		this.getRoutes();

	},
	methods:{
		addRouting(){
			this.$store.state.sourceTargetMust=true;
			this.$router.push({
				path:"/meta/DSConnections"
			})
		},
		/*新增数据库连接*/
		saveRoute(info){
			let focusLine=this.focusLine;
			if(!info.routeName){
				return this.$message({message: 'Route name field is requried!', type: 'warning', center: true});
			};
			if(!/^[A-Za-z0-9]{1}[A-Za-z0-9_-]{0,19}$/.test(info.routeName)){
				return this.$message({message: "The rule is start with a letter，without special symbols ，character length less than 20 !", type: 'warning'});
			};
			Meta.saveRoute(info).then(res => {
				if (res.code === Code.SUCCESS) {
					this.$message({message: Tips.SAVE_SUCCESS, type: 'success'});
					this.$store.state.dsRoute={sourceId:info.sourceId, targetId:info.targetId};
					this.getRoutes()
				} else {
					this.$message({message: res.message, type: 'error'});
				}
			}).catch((e) => {
				info.routeName=''
			})
		},
		/*改变路由名字*/
		updateRouterName(info){
			let focusLine=this.focusLine;
			if(info.routeName){
				if(!/^[A-Za-z0-9]{1}[A-Za-z0-9_-]{0,19}$/.test(info.routeName)){
					info.routeName=focusLine.routeName;
					return this.$message({message: "The rule is start with a letter，without special symbols ，character length less than 20 !", type: 'warning'});
				};
				this.$confirm(`Are you sure name it “${info.routeName}”?`, '', {
					confirmButtonText: 'Sure',
					cancelButtonText: 'Cancel',
				}).then(() => {
					Meta.updateRouterName(info).then(res => {
						if (res.code === Code.SUCCESS) {
							this.$message({message: Tips.FLAG_SUCCESS, type: 'success'});
						} else {
							info.routeName=focusLine.routeName;
							this.$message({message: res.message, type: 'error', center: true});
						}
					}).catch(() => {
						info.routeName=focusLine.routeName;
						this.$message({message: res.message, type: 'error', center: true});
					})
				}).catch(()=>{
					info.routeName=focusLine.routeName;
				})
				
			}else{
				info.routeName=focusLine.routeName;
				this.$message({message: 'Route name field is requried!', type: 'warning', center: true});
			}
		},
		/*改变路由状态*/
		updateRouterStatus(info){
			if(!info.routeId){return;}
			this.$confirm(`Confirm to ${info.routeStatus==1?'disable':'enabled'} the selected route?`, '', {
				confirmButtonText: 'Sure',
				cancelButtonText: 'Cancel',
			}).then(() => {
				if(info.routeStatus==1){
					info.routeStatus=0
				}else{
					info.routeStatus=1
				}
				Meta.updateRouterStatus(info).then(res => {
					if (res.code === Code.SUCCESS) {
						this.$message({message: Tips.FLAG_SUCCESS, type: 'success'});

					} else {
						this.$message({message: res.message, type: 'error', center: true});
					}
				}).catch(() => {
				})
			}).catch(()=>{

			})

		},
		tapTo(item, index){
			let sources=this.groupItems[index].currtTarget.sources, dataFmt=sources.filter(m=>m.routeStatus==1),
			param=dataFmt.map(m=>m.sourceId).toString();
			this.$router.push({
				path:'GenerateTargetDDL',
				query:{
					routeId:dataFmt.map(m=>m.routeId),
					sourceId:dataFmt.map(m=>m.sourceId),
					schemaId:item.schemaId,
					schemaName:item.schemaName,
					targetId:this.groupItems[index].currtTarget.targetId
				}
			});
		},
		pushOne(index){
			let val=this.groupItems[index].currtTarget
			this.groupItems[index].schemas.push({
				routeId: val.routeId,
				routeName: val.routeName,
				targetId: val.targetId,
				targetName: val.targetName,
				schemaName: '',
				checked:false,
				executeFlag: '0',
				flag: 1,
				newline:true
			})
		},
		/*添加一行*/
		addLine(schemaIndex, index){
			if(this.groupItems[index].schemas.length==schemaIndex+1){
				/*点击的是最后一个*/
				this.pushOne(index)
			}else{
				this.groupItems[index].schemas.splice(schemaIndex,1)
			}
		},
		fmtSchemas(val, index){
			let sm=this.groupItems[index].schemas, tmp=[];
			/*1新增，2编辑*/
			if(val==2){
				/*同步已经勾选的信息*/
				tmp=sm.filter(item=>item.checked)
				if(!tmp.length){
					this.$message({message: 'Please select the data to sync first!', type: 'warning'});
				}
			}else if(val==1){
				/*上传添加新增信息*/
				let sm=this.groupItems[index].schemas;
				tmp=sm.filter(item=>item.newline&&item.schemaName!="")
				if(!tmp.length){
					this.$message({message: 'Schema name field is requried!', type: 'warning'});
				}else{
					for(let i=0;i<tmp.length;i++){
						if(!/^[A-Za-z]{1}\w{0,28}$/.test(tmp[i].schemaName)){
							this.$message({message: "The rule is start with a letter，without special symbols ，character length less than 20 !", type: 'warning'});
							tmp=[];
							break;
						}
					}
				}
			};
			return tmp
		},
		rouParams(row){
			return {
				routeEntityList:this.currtTarget.sources.map(m=>{
					return {
						routeId:m.routeId,routeName:m.routeName
					}
				}),
				schemaEntityList:row
			}
		},
		saveSchemalists(val, row, index){
			let api='createSchemalists', params={};
			if(val==3){
				api='saveSchemalists'
				row.flag=val;
				params=[row];
			}else{
				if(val==50){
					if(row.newline){return};
					if(!/^[A-Za-z]{1}\w{0,28}$/.test(row.schemaName)){
						return this.$message({message: "The rule is start with a letter，without special symbols ，character length less than 20 !", type: 'warning'});
					};
					params=[row];
					api='saveSchemalists'
					/*修改单个schamename*/
				}else{
					/*保存新schame和同步status*/
					params=this.fmtSchemas(val, index);
					if(!params.length){return};
					if(val==1){
						api='saveSchemalists'
					}
				}
				
			}
			// return
			this.fullload=true;
			Meta[api](params).then(res => {
				if (res.code === Code.SUCCESS) {
					this.fullload=false
					this.$message({message: Tips.FLAG_SUCCESS, type: 'success'});
					if(!row){
						this.showAddPanel(this.groupItems[index].currtTarget, index)
					};
				} else {
					this.fullload=false
					if(row){
						row.deleteFlag=row.deleteFlag==1?'0':'1'
						if(val==50){this.showAddPanel(this.groupItems[index].currtTarget, index)}
					};
					this.$message({message: res.message, type: 'error', center: true});
				}
			}).catch(() => {
				if(row){
					row.deleteFlag=row.deleteFlag==1?'0':'1'
					if(val==50){this.showAddPanel(this.groupItems[index].currtTarget, index)}
				};
				this.fullload=false
			})
		},
		/*schema 面板*/
		showAddPanel(val, index){
			this.groupItems[index].currtTarget=val;
			let sources=val.sources, dataFmt=sources.filter(m=>m.routeStatus==1),
			param=dataFmt.map(m=>m.sourceId).toString();
			Meta.existTables({param:param}).then(res => {
				if (res.code === Code.SUCCESS) {
					this.groupItems[index].hasDDL=res.items||false;
					Meta.querySchemalists({targetName:val.targetName}).then(res=>{
						res.items.map(item=>{
							item.executeFlag=item.executeFlag+'';
							item.checked=false;
							item.flag=2;
						})
						this.groupItems[index].schemas=res.items;
						this.pushOne(index)
					}).catch(()=>{

					});

					if(!this.addSchemaPanel){
						setTimeout(()=>{
							this.cmpLine()
						},10)
						this.addSchemaPanel=true
					};
					this.groupItems[index].showSchema=true
					
				}
			}).catch(() => {
			})
			
		},
		/*匹配SourceId 获取route*/
		matchSource(id){
			return this.fmtedItems.filter(item=>item.sourceId==id)
		},
		/*匹配targetId 获取route*/
		matchTarget(id){
			return this.fmtedItems.filter(item=>item.targetId==id)
		},
		groupItem(){
			let Mulx=[], Single=[], singleId={}, mulxId={};
			this.fmtedItems.forEach((item,index)=>{
				let temp=[];
				if(this.matchSource(item.sourceId).length<4&&this.matchTarget(item.targetId).length<2){
					Single.push(item);
				}else{
					Mulx.push(item);
				}
			})
			
			let tempSingle=Single, tempMulx=Mulx;
			tempSingle.forEach((item, index)=>{
				let hasInMulx=tempMulx.filter(n=>n.sourceId==item.sourceId)
				/*console.log('------item-----')
				console.log(JSON.stringify(item))*/
				if(hasInMulx.length){
					Mulx.push(item)
					Single.splice(index,1)
				}
			})

			Single.forEach(item=>{
				if(singleId.hasOwnProperty(item.sourceId)){
					singleId[item.sourceId].push(item)
				}else{
					singleId[item.sourceId]=[item]
				}
			})

			let Data=Mulx.length?[Mulx]:[], TD=[]
			for(let c in singleId){
				Data.push(singleId[c])
			}
			Data.forEach((item, index)=>{
				TD.push(this.fmtItems(item, index))
			})
			this.groupItems=TD;
			setTimeout(()=>{
				this.cmpLine();
			},20)
		},
		/*获取路由信息*/
		getRoutes(){
			Meta.getDSRoutes({}).then(res => {
				if (res.code === Code.SUCCESS) {
					let items=res.items, route=this.$route.query;
					this.routeList = items;
					if(route.sourceId&&route.targetId){
						let norRpeat=true;
						for(let i=0;i<items.length;i++){
							if(items[i].targetId==route.targetId&&items[i].sourceId==route.sourceId){
								norRpeat=false; break;
							}
						};
						if(norRpeat){
							items.unshift({
								targetId:route.targetId,
								targetName:route.targetName,
								sourceId:route.sourceId,
								sourceName:route.sourceName,
								routeName:'',
								routeStatus:1
							});
						}else{
							let state=this.$store.state.dsRoute||{}, rp=this.$route.params||{};
							if(!this.$store.state.dsRoute&&(state.routeId==rp.routeId)){
								this.$store.state.dsRoute=route
								this.$message({message: (route.sourceName+' and '+route.targetName+' routing relationships already exist !'), type: 'warning', center: true});
							};
						}
						
					};
					this.fmtedItems=items;
					/*this.fmtItems(items);*/
					if(!items.length){
						this.noRouting=true
					}else{
						this.groupItem();
						this.$store.state.targets=this.$store.state.sources=null
					}
				} else {
					this.$message({message: res.message, type: 'error', center: true});
				}
			}).catch(() => {
			})
		},
		/*计算源与目标的距离，角度，起始点...*/
		cmpLine(){
			let items=this.groupItems;
			items.forEach((s, index)=>{
				let sources=s.sources, diameter=110, radius=diameter/2, lines=[];
				sources.forEach(item=>{
					let targets=item.targets
					let sourceNode=document.querySelector('#source-'+item.sourceId);
					targets.forEach(m=>{
						let targetNode=document.querySelector('#target-'+m.targetId)
						let sourceNodeTop=sourceNode.offsetTop, sourceNodeLeft=sourceNode.offsetLeft;
						let start=[radius+sourceNodeLeft, radius+sourceNodeTop];
						let targetNodeTop=targetNode.offsetTop, targetNodeLeft=targetNode.offsetLeft;
						let end=[targetNodeLeft+radius, targetNodeTop+radius];
						let width=Math.sqrt(Math.pow(start[0]-end[0],2)+Math.pow(start[1]-end[1],2));
						lines.push(this.getAngle(start, end,m))
					})
				})
				this.groupItems[index].lines=lines;
			})
			setTimeout(()=>{
				if(document.querySelector('.angle-focus input')){
					document.querySelector('.angle-focus input').focus()
				}
			},100)
		},
		getAngle(a, b, info){
			let ax=a[0], ay=a[1], bx=b[0], by=b[1];
			let x=Math.abs(ax-bx),y=Math.abs(ay-by);
			let z=Math.sqrt(x*x+y*y);
			return {x:ax, y:ay, deg:ay<by?(Math.asin(y/z)/Math.PI*180):(0-Math.asin(y/z)/Math.PI*180), z:Math.round(z)-65,info:info, focus:false};
		},
		fmtItems(items,index){
			if(!items){console.error('fmtItems 缺少 items');return ''};
			let [sources,targets]=[[],[]];
			items.forEach(item=>{
				let sourceIndex=-1, targetIndex=-1;
				/*匹配是否存在已有源*/
				for(let i=0;i<sources.length;i++){
					if(sources[i].sourceId==item.sourceId){
						sourceIndex=i;
						break;
					}
				};
				if(sourceIndex>-1){
					/*已经存在*/
					sources[sourceIndex].targets.push(item)
				}else{
					sources.push({
						sourceId:item.sourceId,
						sourceName:item.sourceName,
						targets:[item]
					})
				};
				/*匹配是否存在已有目标*/
				for(let i=0;i<targets.length;i++){
					if(targets[i].targetId==item.targetId){
						targetIndex=i;
						break;
					}
				};
				if(targetIndex<0){
					targets.push({
						targetName:item.targetName,
						targetId: item.targetId,
						sources:[item]
					})
				}else{
					/*已经存在*/
					targets[targetIndex].sources.push(item)
				}
			})
			return {
				sources,targets,
				lines:[],
				showSchema:false,
				currtTarget:{},
				schemas:[],
				hasDDL:false
			}
		},
		/*保存路由*/
		saveRouteName(list){
			Meta.saveRouteName({
				routeName: list.routeName,
				routeId: list.routeId
			}).then(res => {
				if (res.code === Code.SUCCESS) {
					this.$message({message: Tips.INSERT_SUCCESS, type: 'success'});
					this.getRoutes()
				} else {
					this.$message({message: res.message, type: 'error', center: true});
				}
			}).catch(() => {
			})
		},
		clickLine(info){
			this.focusLine=JSON.parse(JSON.stringify(info))
		}
	}
}
</script>

<style lang="less" scoped>
.container{
	height: auto;
	.group-li{
		margin-top: 0;margin-bottom: 15px;
	}
	.main{height: 100%;display: flex;position: relative;
		.left{width: 50%;padding: 0 30px; max-width: 800px; margin: 10px auto;margin-bottom: 20px;
			.routing{
				position: relative;
				min-height: 200px;display: flex;
				.source{
					min-width: 400px;
					display: flex;flex-direction: column;justify-content: space-around;
				}
				.target{
					display: flex;flex-direction: column;justify-content: space-around;
					.icon-thumb-tack{
						position: absolute;top: -3px;left: 20px;
					}
				}
				.angle{
					position: absolute;
					height: 10px;z-index: 0;
					text-align: center;
					border-radius: 3px;
					&:hover{
						z-index: 10;background: #dcf0ff;
					}
					div{
						height: 100%;position: absolute;width: 100%;top: 0;cursor: pointer;
						&:after{
							content: '';display: block;position: absolute;top: 4px;left: 0;right: 0;z-index: 1;
						}
						&:before{
							content: '';display: block;position: absolute;
							width: 0;height: 0;border-width: 4px;border-color: transparent;border-style: solid;border-left-color:#aaa;border-left-width: 9px;
							right: -8px;top: 0px;
							z-index: 2;
						}
					}
					&:hover div,&:hover input{
						border-color: #189cfb;color: #189cfb;
					}
					input{
						position: relative;height: 14px;margin: 0 auto;display: inline-block;background: #fdfdfd;top: -6px;z-index: 3;padding: 3px 5px;font-size: 13px;text-align: center;min-width: 46px;border: 1px solid #fdfdfd;border-radius: 12px;line-height:14px;
						outline: none;
					}
				}
				.angle-focus{
					z-index: 10;background: #dcf0ff;color: #189cfb;
					div:after{
						border-color: #189cfb!important;
					}
					div:before{
						border-left-color:#189cfb!important;
					}
					input{
						border-color: #189cfb!important;
					}
				}
				.angle-0 div:after{
					border: 1px dashed #aaa;
				}
				.angle-1 div:after{
					border-bottom: 1px solid #aaa;
				}
			}
		}
		.right{width: 46%;display: flex;padding-right: 2%;
			.add-panel{
				padding-bottom: 20px;
				.shadow{
					margin: 0;
				}
				i:before{
					font-size: 30px;
				}
			}
		}
		.btn-set{
			flex-direction: row-reverse;
			padding-top: 5px;
			.button{
				width: 30px;height: 30px;margin: 12px 10px;
				i:before{
					font-size: 30px;
				}
			}
		}
	}
}

.target>.circle,a{cursor: pointer}

.circle{
	width: 110px;height: 110px;border-radius: 110px;
	display: flex;flex-direction: column;justify-content: center;align-items: center;
	position: relative;z-index: 100;position: relative;
}
.no-routing{
	width: 100%;position: absolute;text-align: center;color: #999;height: 500px;line-height: 500px;
}
</style>
