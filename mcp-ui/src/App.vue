<template>
  <transition name="fade" mode="out-in">
    <router-view></router-view>
  </transition>
</template>

<script>
  export default{
    data(){
      return {
        reloadPages:['dashboard','DSRouting']
      }
    },
    created(){
      window.onresize=()=>{
        if(this.reloadPages.indexOf(this.$route.name)>-1){
          location.reload()
        }
      }
    },
    methods: {
    }
  }
</script>

<style>
</style>
