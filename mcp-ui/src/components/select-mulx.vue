<template>
<div class="job-own-select">
	<div class="flex fxmiddle left">
		<i class="icon-xm-mange"></i>
		<span class="wrapit">{{selected.name}}</span>
	</div>
	<div class="right">
		<i class="icon-small-arrow"></i>
	</div>
	<ul class="abs-ul">
		<div class="abs-li-set">
			<p class="fc999">{{options.title}}</p>
			<p class="mt5">
				<router-link :to="options.a.href" >{{options.a.content}} <i class="el-icon-plus"></i>
				</router-link>
			</p>
		</div>
		<li @click="back(item)" :class="{deepblue:item[options.labelId]==value}" class="li-active" v-for="item in data">
			<i class="icon-xm-list"></i>{{item[options.labelName]}}
		</li>
	</ul>
</div>
</template>

<script>
export default {
	name: 'selectMulx',
	props:['options', 'data', 'value', ],
	data() {

		return {
			selected:{
				name:'loading...',
				id:''
			}
		}
	},
	mounted() {
		this.fmtData()
	},
	methods: {
		fmtData(){
			let items=this.data, options=this.options;
			if(!items.length){return};
			for(let i=0;i<items.length;i++){
				if(items[i][options.labelId]==this.value){
					this.selected.name=items[i][options.labelName];
					break;
				}
			}
		},
		back(val){
			this.value=val[this.options.labelId];
			this.fmtData()
			this.$emit('back',val)
		}
	},
	watch:{
		data(){
			this.fmtData()
		}
	}
}
</script>

<style>

/* 自定义下拉选择 */
.job-own-select{
	padding: 0 10px;position: relative;margin-right: 10px;display: inline-flex;
}
.job-own-select:hover{
	/*box-shadow: 0 0 5px rgba(0,0,0,0.1)*/
}
.job-own-select:hover .abs-ul{
	display: block;
}
.abs-ul{
	 display: none; 
	position: absolute;top: 46px;left: 0px; background: #fff;z-index: 1000;width: 140px;
	border-radius: 4px;box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
	max-height: 370px;overflow-y: auto;line-height: 1.4;
}
.abs-ul::-webkit-scrollbar{
	width:4px;height:6px;border-radius:6px;background-color:#fff;
}
.abs-ul::-webkit-scrollbar-thumb{
	border-radius:6px;background-color:#ccc;
}
.abs-ul::-webkit-scrollbar-thumb:hover{
	background-color:#2c6daf;
}
.job-own-select:before{
	content: '';width: 100%;height: 20px;display: inline-block;position: absolute;bottom: -15px;left: 0;
}
.abs-ul li{
	padding: 6px 12px;cursor: pointer;line-height: 1.4;
}
.abs-ul li .icon-xm-list:before{
	font-size: 14px;margin-right: 6px;
}
.abs-li-set{
	border-bottom: 1px solid #ddd;padding: 10px 12px;
}
.li-active:hover{
	background: #eee;
}
.abs-li-set p{
	color: #999;font-size: 13px;
}
.abs-li-set a{
	color: rgb(44, 109, 175);
}
.job-own-select .left span{
	min-width: 70px;max-width: 100px;padding: 0 6px;
}
.job-own-select .right{
	padding: 0 8px 0 1px;position: relative;line-height: 1.4;
}
.job-own-select .right i{
	display: inline-block;
	position: relative;top: 3px;
}
.job-own-select:hover .right i{
	transform: rotate(-180deg);top: 22px;
}
.job-own-select .right .icon-small-arrow{
	height: 12px;
}
</style>
