<template>
  <div class='wrapper flex fxcolumn'>
    <main-header ref="mHeader"></main-header>
    <div class="content-wrapper flex1" @click="hideMenu">
      <div class="tab-content">
        <transition name="fade-transform" mode="out-in">
          <keep-alive :include="cachedViews">
            <router-view :key="key" class="content-area"></router-view>
          </keep-alive>
        </transition>
      </div>
    </div>
    <div class="footer shrink0">
      <ul class="ft-area">
        <li class="f12 pt10 gray">© 2018 MCP, JYdata</li>
        <li>
          <a href="https://github.com/jydata/MCP" target="_blank">
            <i class="icon-github" title="Github"></i>
          </a>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
  import MainHeader from '@components/MainHeader'

  export default {
    name: 'consoleLayout',
    data() {
      return {};
    },
    components: {
      MainHeader
    },
    methods:{
      hideMenu(){
        this.$refs.mHeader.handleClose()
      }
    },
    computed: {
      cachedViews() {
        const tabs = this.$store.state.common.tabs;
        return tabs && tabs.map(item => {
          console.log(item)
          if (!item.meta.noCache) {
            return item.name;
          }
        })
      },
      key() {
        return this.$route.fullPath;
      }
    }
  }
</script>

<style lang="less" scoped>
  .wrapper {
    height: 100%;
    .content-wrapper{
      flex-shrink: 0;
    }
    .tab-content {
      .content-area {
        padding: 0px;
      }
    }
  }
</style>
