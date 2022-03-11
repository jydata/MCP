<template>
  <div class="loading" v-loading="loading" element-loading-text="loading..."></div>
</template>

<script>
  import * as Code from '@config/code';
  import * as Base from '@api/Base';
  export default {
    name: 'consoleLoading',
    data () {
      return {
        loading: true
      }
    },
    beforeCreate () {
      Base.menus().then((res) => {
          this.$router.push('/login');
      }, (err) => {
        console.log(err);
      });
    }
  }
</script>

<style>
  .loading {
    margin-top: 18%;
  }
</style>
