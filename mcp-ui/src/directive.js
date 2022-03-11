// Directive
Vue.directive('focus', {
  inserted: function (el) {
    el.focus();
  }
});

//SQL代码高亮
import hljs from '../static/js/highlight.js';
/*import '../static/css/sql.css';*/
hljs.registerLanguage('sql', require('../static/js/sql'));
Vue.directive('highlight',function (el) {
  let blocks = el.querySelectorAll('pre code');
  setTimeout(() =>{
    blocks.forEach((block)=>{
      hljs.highlightBlock(block)
    })
  }, 200)
})