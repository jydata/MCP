webpackJsonp([0],{579:function(t,e,n){n(772),n(771);var a=n(307)(n(665),n(745),"data-v-3dadd264",null);a.options.__file="D:\\jiuye-pro\\jydata-git\\mcp-new\\mcp-ui\\src\\pages\\Dashboard.vue",a.esModule&&Object.keys(a.esModule).some(function(t){return"default"!==t&&"__esModule"!==t})&&console.error("named exports are not supported in *.vue files."),a.options.functional&&console.error("[vue-loader] Dashboard.vue: functional components are not supported with templates, they should use render functions."),t.exports=a.exports},597:function(t,e,n){"use strict";var a=n(310),i=n.n(a),o=n(599),r=n.n(o),s=n(600),c=n.n(s),l=function(){function t(){r()(this,t)}return c()(t,null,[{key:"setPromise",value:function(t,e,n){return new i.a(function(a,i){switch(t.toUpperCase()){case"GET":axios.get(e,{params:n}).then(function(t){t?a(t.data):i()});break;case"POST":case"PUT":axios({method:t,url:e,data:n}).then(function(t){t?a(t.data):i()});break;case"DELETE":axios.delete(e,{data:n}).then(function(t){t?a(t.data):i()})}})}}]),t}();e.a=l},598:function(t,e,n){"use strict";n.d(e,"a",function(){return a}),n.d(e,"b",function(){return i});var a="SUCCESS",i="ERROR"},599:function(t,e,n){"use strict";e.__esModule=!0,e.default=function(t,e){if(!(t instanceof e))throw new TypeError("Cannot call a class as a function")}},600:function(t,e,n){"use strict";e.__esModule=!0;var a=n(309),i=function(t){return t&&t.__esModule?t:{default:t}}(a);e.default=function(){function t(t,e){for(var n=0;n<e.length;n++){var a=e[n];a.enumerable=a.enumerable||!1,a.configurable=!0,"value"in a&&(a.writable=!0),(0,i.default)(t,a.key,a)}}return function(e,n,a){return n&&t(e.prototype,n),a&&t(e,a),e}}()},607:function(t,e,n){var a=n(124)("meta"),i=n(57),o=n(64),r=n(56).f,s=0,c=Object.isExtensible||function(){return!0},l=!n(120)(function(){return c(Object.preventExtensions({}))}),u=function(t){r(t,a,{value:{i:"O"+ ++s,w:{}}})},d=function(t,e){if(!i(t))return"symbol"==typeof t?t:("string"==typeof t?"S":"P")+t;if(!o(t,a)){if(!c(t))return"F";if(!e)return"E";u(t)}return t[a].i},f=function(t,e){if(!o(t,a)){if(!c(t))return!0;if(!e)return!1;u(t)}return t[a].w},h=function(t){return l&&p.NEED&&c(t)&&!o(t,a)&&u(t),t},p=t.exports={KEY:a,NEED:!1,fastKey:d,getWeak:f,onFreeze:h}},608:function(t,e){e.f={}.propertyIsEnumerable},610:function(t,e){e.f=Object.getOwnPropertySymbols},612:function(t,e,n){t.exports={default:n(613),__esModule:!0}},613:function(t,e,n){n(81),n(617),t.exports=n(26).Array.from},614:function(t,e,n){"use strict";var a=n(56),i=n(122);t.exports=function(t,e,n){e in t?a.f(t,e,i(0,n)):t[e]=n}},615:function(t,e,n){var a=n(65);t.exports=Array.isArray||function(t){return"Array"==a(t)}},616:function(t,e,n){var a=n(57);t.exports=function(t,e){if(!a(t)||t._t!==e)throw TypeError("Incompatible receiver, "+e+" required!");return t}},617:function(t,e,n){"use strict";var a=n(63),i=n(55),o=n(311),r=n(319),s=n(318),c=n(123),l=n(614),u=n(125);i(i.S+i.F*!n(320)(function(t){Array.from(t)}),"Array",{from:function(t){var e,n,i,d,f=o(t),h="function"==typeof this?this:Array,p=arguments.length,m=p>1?arguments[1]:void 0,v=void 0!==m,y=0,g=u(f);if(v&&(m=a(m,p>2?arguments[2]:void 0,2)),void 0==g||h==Array&&s(g))for(e=c(f.length),n=new h(e);e>y;y++)l(n,y,v?m(f[y],y):f[y]);else for(d=g.call(f),n=new h;!(i=d.next()).done;y++)l(n,y,v?r(d,m,[i.value,y],!0):i.value);return n.length=y,n}})},621:function(t,e,n){t.exports={default:n(624),__esModule:!0}},623:function(t,e,n){t.exports={default:n(625),__esModule:!0}},624:function(t,e,n){n(638),t.exports=n(26).Object.assign},625:function(t,e,n){n(322),n(81),n(84),n(639),n(642),n(641),n(640),t.exports=n(26).Set},626:function(t,e,n){var a=n(312);t.exports=function(t,e){var n=[];return a(t,!1,n.push,n,e),n}},627:function(t,e,n){var a=n(63),i=n(316),o=n(311),r=n(123),s=n(629);t.exports=function(t,e){var n=1==t,c=2==t,l=3==t,u=4==t,d=6==t,f=5==t||d,h=e||s;return function(e,s,p){for(var m,v,y=o(e),g=i(y),b=a(s,p,3),x=r(g.length),_=0,w=n?h(e,x):c?h(e,0):void 0;x>_;_++)if((f||_ in g)&&(m=g[_],v=b(m,_,y),t))if(n)w[_]=v;else if(v)switch(t){case 3:return!0;case 5:return m;case 6:return _;case 2:w.push(m)}else if(u)return!1;return d?-1:l||u?u:w}}},628:function(t,e,n){var a=n(57),i=n(615),o=n(16)("species");t.exports=function(t){var e;return i(t)&&(e=t.constructor,"function"!=typeof e||e!==Array&&!i(e.prototype)||(e=void 0),a(e)&&null===(e=e[o])&&(e=void 0)),void 0===e?Array:e}},629:function(t,e,n){var a=n(628);t.exports=function(t,e){return new(a(t))(e)}},630:function(t,e,n){"use strict";var a=n(56).f,i=n(321),o=n(317),r=n(63),s=n(315),c=n(312),l=n(126),u=n(323),d=n(324),f=n(48),h=n(607).fastKey,p=n(616),m=f?"_s":"size",v=function(t,e){var n,a=h(e);if("F"!==a)return t._i[a];for(n=t._f;n;n=n.n)if(n.k==e)return n};t.exports={getConstructor:function(t,e,n,l){var u=t(function(t,a){s(t,u,e,"_i"),t._t=e,t._i=i(null),t._f=void 0,t._l=void 0,t[m]=0,void 0!=a&&c(a,n,t[l],t)});return o(u.prototype,{clear:function(){for(var t=p(this,e),n=t._i,a=t._f;a;a=a.n)a.r=!0,a.p&&(a.p=a.p.n=void 0),delete n[a.i];t._f=t._l=void 0,t[m]=0},delete:function(t){var n=p(this,e),a=v(n,t);if(a){var i=a.n,o=a.p;delete n._i[a.i],a.r=!0,o&&(o.n=i),i&&(i.p=o),n._f==a&&(n._f=i),n._l==a&&(n._l=o),n[m]--}return!!a},forEach:function(t){p(this,e);for(var n,a=r(t,arguments.length>1?arguments[1]:void 0,3);n=n?n.n:this._f;)for(a(n.v,n.k,this);n&&n.r;)n=n.p},has:function(t){return!!v(p(this,e),t)}}),f&&a(u.prototype,"size",{get:function(){return p(this,e)[m]}}),u},def:function(t,e,n){var a,i,o=v(t,e);return o?o.v=n:(t._l=o={i:i=h(e,!0),k:e,v:n,p:a=t._l,n:void 0,r:!1},t._f||(t._f=o),a&&(a.n=o),t[m]++,"F"!==i&&(t._i[i]=o)),t},getEntry:v,setStrong:function(t,e,n){l(t,e,function(t,n){this._t=p(t,e),this._k=n,this._l=void 0},function(){for(var t=this,e=t._k,n=t._l;n&&n.r;)n=n.p;return t._t&&(t._l=n=n?n.n:t._t._f)?"keys"==e?u(0,n.k):"values"==e?u(0,n.v):u(0,[n.k,n.v]):(t._t=void 0,u(1))},n?"entries":"values",!n,!0),d(e)}}},631:function(t,e,n){var a=n(85),i=n(626);t.exports=function(t){return function(){if(a(this)!=t)throw TypeError(t+"#toJSON isn't generic");return i(this)}}},632:function(t,e,n){"use strict";var a=n(15),i=n(55),o=n(607),r=n(120),s=n(49),c=n(317),l=n(312),u=n(315),d=n(57),f=n(82),h=n(56).f,p=n(627)(0),m=n(48);t.exports=function(t,e,n,v,y,g){var b=a[t],x=b,_=y?"set":"add",w=x&&x.prototype,C={};return m&&"function"==typeof x&&(g||w.forEach&&!r(function(){(new x).entries().next()}))?(x=e(function(e,n){u(e,x,t,"_c"),e._c=new b,void 0!=n&&l(n,y,e[_],e)}),p("add,clear,delete,forEach,get,has,set,keys,values,entries,toJSON".split(","),function(t){var e="add"==t||"set"==t;t in w&&(!g||"clear"!=t)&&s(x.prototype,t,function(n,a){if(u(this,x,t),!e&&g&&!d(n))return"get"==t&&void 0;var i=this._c[t](0===n?0:n,a);return e?this:i})}),g||h(x.prototype,"size",{get:function(){return this._c.size}})):(x=v.getConstructor(e,t,y,_),c(x.prototype,n),o.NEED=!0),f(x,t),C[t]=x,i(i.G+i.W+i.F,C),g||v.setStrong(x,t,y),x}},633:function(t,e,n){"use strict";var a=n(48),i=n(313),o=n(610),r=n(608),s=n(311),c=n(316),l=Object.assign;t.exports=!l||n(120)(function(){var t={},e={},n=Symbol(),a="abcdefghijklmnopqrst";return t[n]=7,a.split("").forEach(function(t){e[t]=t}),7!=l({},t)[n]||Object.keys(l({},e)).join("")!=a})?function(t,e){for(var n=s(t),l=arguments.length,u=1,d=o.f,f=r.f;l>u;)for(var h,p=c(arguments[u++]),m=d?i(p).concat(d(p)):i(p),v=m.length,y=0;v>y;)h=m[y++],a&&!f.call(p,h)||(n[h]=p[h]);return n}:l},634:function(t,e,n){"use strict";var a=n(55),i=n(66),o=n(63),r=n(312);t.exports=function(t){a(a.S,t,{from:function(t){var e,n,a,s,c=arguments[1];return i(this),e=void 0!==c,e&&i(c),void 0==t?new this:(n=[],e?(a=0,s=o(c,arguments[2],2),r(t,!1,function(t){n.push(s(t,a++))})):r(t,!1,n.push,n),new this(n))}})}},635:function(t,e,n){"use strict";var a=n(55);t.exports=function(t){a(a.S,t,{of:function(){for(var t=arguments.length,e=new Array(t);t--;)e[t]=arguments[t];return new this(e)}})}},638:function(t,e,n){var a=n(55);a(a.S+a.F,"Object",{assign:n(633)})},639:function(t,e,n){"use strict";var a=n(630),i=n(616);t.exports=n(632)("Set",function(t){return function(){return t(this,arguments.length>0?arguments[0]:void 0)}},{add:function(t){return a.def(i(this,"Set"),t=0===t?0:t,t)}},a)},640:function(t,e,n){n(634)("Set")},641:function(t,e,n){n(635)("Set")},642:function(t,e,n){var a=n(55);a(a.P+a.R,"Set",{toJSON:n(631)("Set")})},660:function(t,e,n){function a(t,e,n){var a=e&&n||0,u=e||[];t=t||{};var d=t.node||i,f=void 0!==t.clockseq?t.clockseq:o;if(null==d||null==f){var h=r();null==d&&(d=i=[1|h[0],h[1],h[2],h[3],h[4],h[5]]),null==f&&(f=o=16383&(h[6]<<8|h[7]))}var p=void 0!==t.msecs?t.msecs:(new Date).getTime(),m=void 0!==t.nsecs?t.nsecs:l+1,v=p-c+(m-l)/1e4;if(v<0&&void 0===t.clockseq&&(f=f+1&16383),(v<0||p>c)&&void 0===t.nsecs&&(m=0),m>=1e4)throw new Error("uuid.v1(): Can't create more than 10M uuids/sec");c=p,l=m,o=f,p+=122192928e5;var y=(1e4*(268435455&p)+m)%4294967296;u[a++]=y>>>24&255,u[a++]=y>>>16&255,u[a++]=y>>>8&255,u[a++]=255&y;var g=p/4294967296*1e4&268435455;u[a++]=g>>>8&255,u[a++]=255&g,u[a++]=g>>>24&15|16,u[a++]=g>>>16&255,u[a++]=f>>>8|128,u[a++]=255&f;for(var b=0;b<6;++b)u[a+b]=d[b];return e||s(u)}var i,o,r=n(730),s=n(729),c=0,l=0;t.exports=a},665:function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var a=n(623),i=n.n(a),o=n(612),r=n.n(o),s=n(121),c=n.n(s),l=n(598),u=n(687),d=n(733),f=(n.n(d),n(734));n.n(f);e.default={name:"dashboard",components:{dashEchartsBar:d,dashEchartsLine:f},data:function(){return{jobList:[],techList:[],syncList:[],agentTable:[],mysqlChartData:[],hotTable:[],hotBtnIsSelect:"day",hotBtnArry:[{label:"day",key:"day"},{label:"week",key:"week"},{label:"half a month",key:"week2"},{label:"month",key:"month"},{label:"all",key:"all"}],syncAgentLine:[],syncJobLine:[],timeout:{sync:!0,syncTitle:!1,job:!0,jobTitle:!1,tech:!0,techTitle:!1,agentTbale:!1,mysqlEchartsBar:!1,hotTable:!1,syncAgentLine:!1,syncJobLine:!1},streamOption:{backgroundColor:"#fafbfc",title:{text:"Sync data streaming",left:"center",top:20,textStyle:{color:"#aaa"}},tooltip:{trigger:"axis"},legend:{data:["Routing1","Routing2"],bottom:"3%",left:"3%"},grid:{left:"3%",right:"4%",bottom:"13%",containLabel:!0},toolbox:{feature:{saveAsImage:{}}},xAxis:{type:"category",boundaryGap:!1,data:["11/12","11/17","11/22","11/27","12/3","12/8","12/13"],axisLine:{lineStyle:{color:"#888"}}},yAxis:{type:"value",axisLine:{lineStyle:{color:"#888"}}},series:[{name:"Routing1",type:"line",data:[120,132,101,134,90,230,210]},{name:"Routing2",type:"line",data:[220,182,191,234,290,330,310]}]},bizOption:{backgroundColor:"#fafbfc",title:{text:"Biz metadata",left:"center",top:20,textStyle:{color:"#aaa"}},color:["#a83720","#209843","#60973a"],tooltip:{trigger:"axis",axisPointer:{type:"shadow"}},legend:{data:["total tables","sync ddl tables","sync data tables"],bottom:"3%"},grid:{left:"3%",right:"4%",bottom:"13%",containLabel:!0},xAxis:[{type:"category",data:["source tables","target tables"],axisLine:{lineStyle:{color:"#888"}}}],yAxis:[{type:"value",min:0,max:1e3,axisLine:{lineStyle:{color:"#888"}}}],series:[{name:"total tables",type:"bar",data:[320,132],barWidth:"15%"},{name:"sync ddl tables",type:"bar",data:[120,932],barWidth:"15%"},{name:"sync data tables",type:"bar",data:[220,182],barWidth:"15%"}]},compareOption:{backgroundColor:"#fafbfc",color:["#209843","#a83720"],title:{text:"Sync data records compared",left:"center",top:20,textStyle:{color:"#aaa"}},tooltip:{trigger:"axis",axisPointer:{type:"cross"}},grid:{bottom:"15%"},toolbox:{feature:{saveAsImage:{show:!0}}},legend:{data:["source records","target records"],bottom:"3%"},xAxis:[{type:"category",axisTick:{alignWithLabel:!0},axisLine:{lineStyle:{color:"#888"}},data:["2018-12-04","2018-12-05","2018-12-06","2018-12-07","2018-12-08","2018-12-09","2018-12-10","2018-12-11","2018-12-12","2018-12-13","2018-12-14"]}],yAxis:[{type:"value",min:0,max:1e3,position:"left",axisLine:{lineStyle:{color:"#888"}},axisLabel:{formatter:"{value}"}}],series:[{name:"source records",type:"bar",data:[210,490,720,232,256,767,130,162,326,200,64]},{name:"target records",type:"line",data:[200,212,313,451,603,102,203,934,230,165,120]}]}}},watch:{},created:function(){this.timerA(),this.timerB(),this.timerC(),this.getMysql(),this.getHot(),this.getSyncAgent(),this.getSyncJob()},methods:{timerA:function(){var t=this;"dashboard"==this.$route.name&&(this.getSync(),setTimeout(function(){t.timerA()},12e4))},timerB:function(){var t=this;"dashboard"==this.$route.name&&(this.getJob(),this.getTech(),setTimeout(function(){t.timerB()},12e4))},timerC:function(){var t=this;"dashboard"==this.$route.name&&setTimeout(function(){t.timerC()},36e5)},getHot:function(){var t=this;this.timeout.hotTable=!0,u.a({fineness:this.hotBtnIsSelect}).then(function(e){t.timeout.hotTable=!1,e.code===l.a?t.hotTable=e.items:t.$message.error(e.message)}).catch(function(e){t.timeout.hotTable=!1})},clickHotBtn:function(t){this.hotBtnIsSelect=t,this.getHot()},getMysql:function(){var t=this;this.timeout.mysqlEchartsBar=!0,u.b({}).then(function(e){t.timeout.mysqlEchartsBar=!1,e.code===l.a?t.mysqlChartData=e.items:t.$message.error(e.message)}).catch(function(e){t.timeout.mysqlEchartsBar=!1})},getAgent:function(){var t=this;this.timeout.agentTbale=!0,u.c({}).then(function(e){t.timeout.agentTbale=!1,e.code===l.a?t.agentTable=e.items:t.$message.error(e.message)}).catch(function(e){t.timeout.agentTbale=!1})},getJob:function(){var t=this;this.timeout.jobTitle=!0,u.d({}).then(function(e){if(e.code===l.a){t.jobList=e.items;var n=t.jobList,a=[],i=[];n.forEach(function(t){i.push(t.name),a.push(t.value)}),echarts.init(t.$refs.job).setOption({backgroundColor:"#fafbfc",tooltip:{trigger:"axis",axisPointer:{type:"shadow"}},xAxis:[{type:"category",data:i,axisTick:{alignWithLabel:!0}}],yAxis:[{type:"value",minInterval:1}],series:[{name:"Job",type:"bar",data:a,label:{normal:{show:!0,position:"insideBottom"}},itemStyle:{normal:{color:function(t){return["#91c7ae","#bda29a","#ca8622","#46ad6a","#c23531"][t.dataIndex]}},emphasis:{shadowBlur:200,shadowOffsetX:0,shadowColor:"rgba(0, 0, 0, 0.5)"}},animationType:"scale",animationEasing:"elasticOut",animationDelay:function(t){return 200*Math.random()}}]})}else t.$message({message:e.message,type:"error"});t.timeout.job=t.timeout.jobTitle=!1}).catch(function(){t.timeout.job=t.timeout.jobTitle=!1})},getTech:function(){var t=this;this.timeout.techTitle=!0,u.e({}).then(function(e){if(e.code===l.a){var n=[],a=!0,i=!1,o=void 0;try{for(var r,s=c()(e.items);!(a=(r=s.next()).done);a=!0){var u=r.value;n.push(u.value)}}catch(t){i=!0,o=t}finally{try{!a&&s.return&&s.return()}finally{if(i)throw o}}echarts.init(t.$refs.techMetadata).setOption({backgroundColor:"#fafbfc",color:["#61a0a8","#d48265"],title:{},tooltip:{trigger:"axis",axisPointer:{type:"shadow"}},xAxis:{type:"category",data:["Sources","Targets","Routes","Agents"],axisTick:{alignWithLabel:!0},axisLine:{lineStyle:{color:"#888"}}},yAxis:{type:"value",minInterval:1,boundaryGap:[0,.1],axisLine:{lineStyle:{color:"#888"}}},series:[{data:n,type:"bar",barWidth:"40%"}]})}else t.$message({message:e.message,type:"error"});t.timeout.tech=t.timeout.techTitle=!1}).catch(function(){t.timeout.tech=t.timeout.techTitle=!1})},fmtLine:function(t,e,n){var a={},o=r()(new i.a(t.map(function(t){return t.time}))),s={series:{},xAxis:o,legends:[]};return t.forEach(function(t){t[e].forEach(function(t){var e=t[n];if(!a.hasOwnProperty(e)){var i={};o.forEach(function(t){i[t]=0}),a[e]=i,s.legends.push(e+"")}})}),t.forEach(function(t){var i=t[e],o=t.time;i.forEach(function(t){var e=t[n];a[e][o]=t.recordSum})}),s.series=a,s},getSync:function(){var t=this;this.timeout.syncTitle=!0,u.f({}).then(function(e){if(e.code===l.a){var n=e.items||[],a=t.fmtLine(n,"routeList","routeName"),i=[];for(var o in a.series){var r=a.series[o],s=[];for(var c in r)s.push(r[c]);i.push({name:o,type:"line",data:s,smooth:!0})}echarts.init(t.$refs.dataStream).clear(),echarts.init(t.$refs.dataStream).setOption({backgroundColor:"#fafbfc",title:{},tooltip:{trigger:"axis"},legend:{data:a.legends,top:"10%",left:"10%"},grid:{left:"10%",right:"4%",bottom:"13%",top:"20%",containLabel:!0},toolbox:{feature:{}},xAxis:{type:"category",boundaryGap:!1,data:a.xAxis,axisLine:{lineStyle:{color:"#888"}},axisLabel:{rotate:40,fontSize:12}},yAxis:{type:"value",minInterval:1,boundaryGap:[0,.1],axisLine:{lineStyle:{color:"#888"}}},dataZoom:[{show:!0,start:97,end:100,bottom:18},{type:"inside",start:97,end:100}],series:i})}else t.$message({message:e.message,type:"error"});t.timeout.sync=t.timeout.syncTitle=!1}).catch(function(){t.timeout.sync=t.timeout.syncTitle=!1})},getSyncAgent:function(){var t=this;this.timeout.syncAgentLine=!0,u.g({}).then(function(e){t.timeout.syncAgentLine=!1,e.code===l.a?t.syncAgentLine=t.fmtLine(e.items,"agentList","agentName"):t.$message.error(e.message)}).catch(function(e){t.timeout.syncAgentLine=!1})},getSyncJob:function(){var t=this;this.timeout.syncJobLine=!0,u.h({}).then(function(e){t.timeout.syncJobLine=!1,e.code===l.a?t.syncJobLine=t.fmtLine(e.items,"jobList","jobName"):t.$message.error(e.message)}).catch(function(e){t.timeout.syncJobLine=!1})}}}},667:function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var a=n(621),i=n.n(a),o=n(660),r=n.n(o),s=["#c23531","#2f4554","#61a0a8","#d48265","#91c7ae","#749f83","#ca8622","#bda29a","#6e7074","#546570","#c4ccd3"];e.default={name:"dash-echarts-bar",props:{colorPool:{type:Array,default:function(){return["#A61F24","#243542"]}},width:{type:String,default:"100%"},height:{type:String,default:"100%"},chartData:{type:Array,default:[]},query:{type:Object,default:function(){return{}}}},watch:{"query.showType":function(t,e){this.initOption()},chartData:{handler:function(t,e){this.initOption()},deep:!0}},data:function(){return{elId:r()(),contentStyle:{width:"",height:""}}},created:function(){this.initStyle()},mounted:function(){this.initOption()},methods:{initStyle:function(){var t=this.width,e=this.height;this.contentStyle=i()({},this.contentStyle,{width:t,height:e})},initOption:function(){var t=[],e=[],n=[];this.chartData.forEach(function(a,i){n.push(a.finenessRate),t.push(a.normalCount),e.push(a.warnCount)});var a={color:this.colorPool||s,tooltip:{trigger:"axis",axisPointer:{type:"shadow"}},legend:{left:"center",bottom:10,data:["Normal","Critical"]},grid:{top:20,left:20,right:20,containLabel:!0},calculable:!0,xAxis:{type:"category",data:n||[]},yAxis:{type:"value"},series:[{name:"Normal",type:"bar",data:t||[],label:{normal:{show:!0,position:"insideBottom"}}},{name:"Critical",type:"bar",data:e||[],label:{normal:{color:"#666",show:!0,position:"insideBottom"}}}]};echarts.init(document.getElementById(this.elId)).setOption(a)}}}},668:function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var a=n(621),i=n.n(a),o=n(660),r=n.n(o);e.default={name:"dash-echarts-line",props:{colorPool:{type:Array,default:function(){return[]}},width:{type:String,default:"100%"},height:{type:String,default:"100%"},chartData:{type:Object,default:{}},query:{type:Object,default:function(){return{}}}},watch:{chartData:{handler:function(t,e){this.initOption()},deep:!0}},data:function(){return{elId:r()(),contentStyle:{width:"",height:""}}},created:function(){this.elId=r()(),this.initStyle()},mounted:function(){this.initOption()},methods:{initStyle:function(){var t=this.width,e=this.height;this.contentStyle=i()({},this.contentStyle,{width:t,height:e})},initOption:function(){this.chartData;var t=[];for(var e in this.chartData.series){var n=this.chartData.series[e],a=[];for(var i in n)a.push(n[i]);t.push({name:e,type:"line",data:a,smooth:!0})}var o={grid:{bottom:80},tooltip:{trigger:"axis",axisPointer:{type:"cross",animation:!1,label:{backgroundColor:"#505765"}}},legend:{data:this.chartData.legends,x:"left"},dataZoom:[{show:!0,realtime:!0,start:95,end:100},{type:"inside",realtime:!0,start:95,end:100}],xAxis:[{type:"category",boundaryGap:!1,data:this.chartData.xAxis,axisLine:{lineStyle:{color:"#888"}},axisLabel:{rotate:40,fontSize:12}}],yAxis:{type:"value"},series:t};echarts.init(document.getElementById(this.elId)).setOption(o)}}}},687:function(t,e,n){"use strict";var a=n(597);n.d(e,"d",function(){return i}),n.d(e,"e",function(){return o}),n.d(e,"f",function(){return r}),n.d(e,"c",function(){return s}),n.d(e,"b",function(){return c}),n.d(e,"a",function(){return l}),n.d(e,"g",function(){return u}),n.d(e,"h",function(){return d});var i=function(t){return a.a.setPromise("GET","/mcp/home/jobs",t)},o=function(t){return a.a.setPromise("GET","/mcp/home/tech_metadata",t)},r=function(t){return a.a.setPromise("GET","/mcp/home/sync_data",t)},s=function(t){return a.a.setPromise("GET","/mcp/home/error_sql_counts",t)},c=function(t){return a.a.setPromise("GET","/mcp/home/table_counts",t)},l=function(t){return a.a.setPromise("GET","/mcp/home/hot_tables",t)},u=function(t){return a.a.setPromise("GET","/mcp/home/sync_agent_data",t)},d=function(t){return a.a.setPromise("GET","/mcp/home/sync_job_data",t)}},704:function(t,e,n){e=t.exports=n(306)(),e.push([t.i,"\n.jy-container[data-v-16e721f4] {\r\n  width: 100%;\r\n  min-height: 440px;\n}\r\n",""])},711:function(t,e,n){e=t.exports=n(306)(),e.push([t.i,"\n.dash-agent-table .el-loading-mask {\n  z-index: 988;\n}\n",""])},712:function(t,e,n){e=t.exports=n(306)(),e.push([t.i,"\n.chart-loading[data-v-3dadd264] {\n  position: absolute;\n  top: 0;\n  left: 0;\n  color: #666;\n  background: #fafbfc;\n  width: 100%;\n  height: 100%;\n  display: -ms-flexbox;\n  display: flex;\n}\n.chart-loading .el-icon-loading[data-v-3dadd264] {\n  font-size: 24px;\n  margin-bottom: 5px;\n}\n.container[data-v-3dadd264] {\n  height: 100%;\n}\n.container .canvas-box[data-v-3dadd264] {\n  height: 100%;\n}\n.container .canvas-box h2[data-v-3dadd264] {\n  padding-top: 20px;\n  color: #666;\n  font-size: 18px;\n}\n.container .canvas-box .btn-box[data-v-3dadd264] {\n  padding: 0 10px;\n  text-align: left;\n}\n.top-wrapper[data-v-3dadd264] {\n  display: -ms-flexbox;\n  display: flex;\n  height: 500px;\n}\n.top-wrapper .left[data-v-3dadd264] {\n  background: #fafbfc;\n  margin: 0 15px 15px 20px;\n  width: 30%;\n}\n.top-wrapper .right[data-v-3dadd264] {\n  background: #fafbfc;\n  margin: 0 20px 15px 0px;\n  width: 70%;\n}\n.mid-wrapper[data-v-3dadd264] {\n  display: -ms-flexbox;\n  display: flex;\n  height: 500px;\n}\n.mid-wrapper .left[data-v-3dadd264] {\n  background: #fafbfc;\n  margin: 0 15px 15px 20px;\n  width: 50%;\n}\n.mid-wrapper .right[data-v-3dadd264] {\n  background: #fafbfc;\n  margin: 0 15px 15px 0px;\n  width: 50%;\n}\n.down-wrapper[data-v-3dadd264] {\n  /*margin: 0 20px 15px 20px;*/\n}\n.down-wrapper .wrapper[data-v-3dadd264] {\n  margin: 0 20px 15px 20px;\n  width: 97%;\n  height: 500px;\n}\n",""])},721:function(t,e,n){e=t.exports=n(306)(),e.push([t.i,"\n.jy-container[data-v-780dace3] {\r\n  width: 100%;\r\n  min-height: 440px;\n}\r\n",""])},729:function(t,e){function n(t,e){var n=e||0,i=a;return[i[t[n++]],i[t[n++]],i[t[n++]],i[t[n++]],"-",i[t[n++]],i[t[n++]],"-",i[t[n++]],i[t[n++]],"-",i[t[n++]],i[t[n++]],"-",i[t[n++]],i[t[n++]],i[t[n++]],i[t[n++]],i[t[n++]],i[t[n++]]].join("")}for(var a=[],i=0;i<256;++i)a[i]=(i+256).toString(16).substr(1);t.exports=n},730:function(t,e){var n="undefined"!=typeof crypto&&crypto.getRandomValues&&crypto.getRandomValues.bind(crypto)||"undefined"!=typeof msCrypto&&"function"==typeof window.msCrypto.getRandomValues&&msCrypto.getRandomValues.bind(msCrypto);if(n){var a=new Uint8Array(16);t.exports=function(){return n(a),a}}else{var i=new Array(16);t.exports=function(){for(var t,e=0;e<16;e++)0==(3&e)&&(t=4294967296*Math.random()),i[e]=t>>>((3&e)<<3)&255;return i}}},733:function(t,e,n){n(764);var a=n(307)(n(667),n(738),"data-v-16e721f4",null);a.options.__file="D:\\jiuye-pro\\jydata-git\\mcp-new\\mcp-ui\\src\\pages\\components\\dashEchartsBar.vue",a.esModule&&Object.keys(a.esModule).some(function(t){return"default"!==t&&"__esModule"!==t})&&console.error("named exports are not supported in *.vue files."),a.options.functional&&console.error("[vue-loader] dashEchartsBar.vue: functional components are not supported with templates, they should use render functions."),t.exports=a.exports},734:function(t,e,n){n(781);var a=n(307)(n(668),n(755),"data-v-780dace3",null);a.options.__file="D:\\jiuye-pro\\jydata-git\\mcp-new\\mcp-ui\\src\\pages\\components\\dashEchartsLine.vue",a.esModule&&Object.keys(a.esModule).some(function(t){return"default"!==t&&"__esModule"!==t})&&console.error("named exports are not supported in *.vue files."),a.options.functional&&console.error("[vue-loader] dashEchartsLine.vue: functional components are not supported with templates, they should use render functions."),t.exports=a.exports},738:function(t,e,n){t.exports={render:function(){var t=this,e=t.$createElement;return(t._self._c||e)("div",{style:t.contentStyle,attrs:{id:t.elId}})},staticRenderFns:[]},t.exports.render._withStripped=!0},745:function(t,e,n){t.exports={render:function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("section",[n("div",{staticClass:"header"},[n("div",{staticClass:"title"},[n("el-breadcrumb",{attrs:{"separator-class":"el-icon-arrow-right"}},[n("el-breadcrumb-item",[t._v("Home")])],1)],1)]),t._v(" "),n("div",{staticClass:"container"},[n("div",{staticClass:"top-wrapper"},[n("div",{staticClass:"left relative"},[n("div",{staticClass:"canvas-box flex fxcolumn"},[n("h2",{staticClass:"center"},[t._v("Job "),n("i",{directives:[{name:"show",rawName:"v-show",value:t.timeout.jobTitle,expression:"timeout.jobTitle"}],staticClass:"el-icon-loading"})]),t._v(" "),n("div",{ref:"job",staticClass:"flex1"})]),t._v(" "),n("div",{directives:[{name:"show",rawName:"v-show",value:t.timeout.job,expression:"timeout.job"}],staticClass:"chart-loading middle"},[t._m(0)])]),t._v(" "),n("div",{staticClass:"right relative"},[n("div",{staticClass:"canvas-box flex fxcolumn"},[n("h2",{staticClass:"center"},[t._v("Sync data streaming "),n("i",{directives:[{name:"show",rawName:"v-show",value:t.timeout.syncTitle,expression:"timeout.syncTitle"}],staticClass:"el-icon-loading"})]),t._v(" "),n("div",{ref:"dataStream",staticClass:"flex1"})]),t._v(" "),n("div",{directives:[{name:"show",rawName:"v-show",value:t.timeout.sync,expression:"timeout.sync"}],staticClass:"chart-loading middle"},[t._m(1)])])]),t._v(" "),n("div",{staticClass:"mid-wrapper"},[n("div",{staticClass:"left relative"},[n("div",{staticClass:"canvas-box flex fxcolumn"},[n("h2",{staticClass:"center"},[t._v("Sync agent data streaming "),n("i",{directives:[{name:"show",rawName:"v-show",value:t.timeout.techTitle,expression:"timeout.techTitle"}],staticClass:"el-icon-loading"})]),t._v(" "),n("dash-echarts-line",{attrs:{chartData:t.syncAgentLine}})],1),t._v(" "),n("div",{directives:[{name:"show",rawName:"v-show",value:t.timeout.syncAgentLine,expression:"timeout.syncAgentLine"}],staticClass:"chart-loading middle"},[t._m(2)])]),t._v(" "),n("div",{staticClass:"right relative"},[n("div",{staticClass:"canvas-box flex fxcolumn"},[n("h2",{staticClass:"center"},[t._v("Sync job data streaming "),n("i",{directives:[{name:"show",rawName:"v-show",value:t.timeout.techTitle,expression:"timeout.techTitle"}],staticClass:"el-icon-loading"})]),t._v(" "),n("dash-echarts-line",{attrs:{chartData:t.syncJobLine}})],1),t._v(" "),n("div",{directives:[{name:"show",rawName:"v-show",value:t.timeout.syncJobLine,expression:"timeout.syncJobLine"}],staticClass:"chart-loading middle"},[t._m(3)])])]),t._v(" "),n("div",{staticClass:"mid-wrapper"},[n("div",{staticClass:"left relative"},[n("div",{staticClass:"canvas-box flex fxcolumn"},[n("h2",{staticClass:"center"},[t._v("Tech metadata "),n("i",{directives:[{name:"show",rawName:"v-show",value:t.timeout.techTitle,expression:"timeout.techTitle"}],staticClass:"el-icon-loading"})]),t._v(" "),n("div",{ref:"techMetadata",staticClass:"flex1"})]),t._v(" "),n("div",{directives:[{name:"show",rawName:"v-show",value:t.timeout.tech,expression:"timeout.tech"}],staticClass:"chart-loading middle"},[t._m(4)])]),t._v(" "),n("div",{ref:"bizMetadata",staticClass:"right"})]),t._v(" "),n("div",{staticClass:"mid-wrapper"},[n("div",{staticClass:"left relative"},[n("div",{staticClass:"canvas-box flex fxcolumn"},[n("h2",{staticClass:"center"},[t._v("Agent Log Error Monitor "),n("i",{directives:[{name:"show",rawName:"v-show",value:t.timeout.agentTbale,expression:"timeout.agentTbale"}],staticClass:"el-icon-loading"})]),t._v(" "),n("el-table",{directives:[{name:"loading",rawName:"v-loading",value:t.timeout.agentTbale,expression:"timeout.agentTbale"}],staticClass:"shadow dash-agent-table",attrs:{"empty-text":"No data",data:t.agentTable,"element-loading-text":"loading..."}},[n("el-table-column",{attrs:{label:"Agent",prop:"agentName"}}),t._v(" "),n("el-table-column",{attrs:{label:"T",prop:"todayError"}}),t._v(" "),n("el-table-column",{attrs:{label:"T-1",prop:"lastdayError"}}),t._v(" "),n("el-table-column",{attrs:{label:"T-7",prop:"lastweekError"}})],1)],1)]),t._v(" "),n("div",{staticClass:"right relative"},[n("div",{staticClass:"canvas-box flex fxcolumn"},[n("h2",{staticClass:"center"},[t._v("MySQL To Phoenix (Table counts) "),n("i",{directives:[{name:"show",rawName:"v-show",value:t.timeout.mysqlEchartsBar,expression:"timeout.mysqlEchartsBar"}],staticClass:"el-icon-loading"})]),t._v(" "),n("dash-echarts-bar",{attrs:{chartData:t.mysqlChartData}})],1),t._v(" "),n("div",{directives:[{name:"show",rawName:"v-show",value:t.timeout.mysqlEchartsBar,expression:"timeout.mysqlEchartsBar"}],staticClass:"chart-loading middle"},[t._m(5)])])]),t._v(" "),n("div",{staticClass:"mid-wrapper"},[n("div",{staticClass:"left relative"},[n("div",{staticClass:"canvas-box flex fxcolumn"},[n("h2",{staticClass:"center"},[t._v("Hot Tables(TOP 10) "),n("i",{directives:[{name:"show",rawName:"v-show",value:t.timeout.hotTable,expression:"timeout.hotTable"}],staticClass:"el-icon-loading"})]),t._v(" "),n("div",{staticClass:"center btn-box"},t._l(t.hotBtnArry,function(e){return n("span",{key:e.key,style:{cursor:"pointer",paddingRight:"5px",color:t.hotBtnIsSelect===e.key?"#0000FF":"#000000"},on:{click:function(n){return t.clickHotBtn(e.key)}}},[t._v(t._s(e.label)+" "),n("i",[t._v("|")])])}),0),t._v(" "),n("el-table",{directives:[{name:"loading",rawName:"v-loading",value:t.timeout.hotTable,expression:"timeout.hotTable"}],staticClass:"shadow",attrs:{"empty-text":"No data",data:t.hotTable,"element-loading-text":"loading..."}},[n("el-table-column",{attrs:{label:"SourceDB",prop:"sourceDb","show-overflow-tooltip":!0}}),t._v(" "),n("el-table-column",{attrs:{label:"SourceTable",prop:"sourceTb","show-overflow-tooltip":!0}}),t._v(" "),n("el-table-column",{attrs:{label:"Increse(line) ",prop:"sourceCount"}}),t._v(" "),n("el-table-column",{attrs:{label:"HotRank",type:"index",width:"80"}})],1)],1)]),t._v(" "),n("div",{staticClass:"right relative"})])])])},staticRenderFns:[function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",{staticClass:"middle"},[n("p",[n("i",{staticClass:"el-icon-loading"})]),t._v(" "),n("p",[t._v("loading...")])])},function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",{staticClass:"middle"},[n("p",[n("i",{staticClass:"el-icon-loading"})]),t._v(" "),n("p",[t._v("loading...")])])},function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",{staticClass:"middle"},[n("p",[n("i",{staticClass:"el-icon-loading"})]),t._v(" "),n("p",[t._v("loading...")])])},function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",{staticClass:"middle"},[n("p",[n("i",{staticClass:"el-icon-loading"})]),t._v(" "),n("p",[t._v("loading...")])])},function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",{staticClass:"middle"},[n("p",[n("i",{staticClass:"el-icon-loading"})]),t._v(" "),n("p",[t._v("loading...")])])},function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("div",{staticClass:"middle"},[n("p",[n("i",{staticClass:"el-icon-loading"})]),t._v(" "),n("p",[t._v("loading...")])])}]},t.exports.render._withStripped=!0},755:function(t,e,n){t.exports={render:function(){var t=this,e=t.$createElement;return(t._self._c||e)("div",{style:t.contentStyle,attrs:{id:t.elId}})},staticRenderFns:[]},t.exports.render._withStripped=!0},764:function(t,e,n){var a=n(704);"string"==typeof a&&(a=[[t.i,a,""]]),a.locals&&(t.exports=a.locals);n(308)("27784e51",a,!1)},771:function(t,e,n){var a=n(711);"string"==typeof a&&(a=[[t.i,a,""]]),a.locals&&(t.exports=a.locals);n(308)("2c4c4718",a,!1)},772:function(t,e,n){var a=n(712);"string"==typeof a&&(a=[[t.i,a,""]]),a.locals&&(t.exports=a.locals);n(308)("5ceda188",a,!1)},781:function(t,e,n){var a=n(721);"string"==typeof a&&(a=[[t.i,a,""]]),a.locals&&(t.exports=a.locals);n(308)("72f89246",a,!1)}});