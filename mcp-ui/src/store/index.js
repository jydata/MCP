
import Vuex from 'vuex'
import * as actions from './actions'
import * as getters from './getters'
import common from './modules/common'

// Vue.use(Vuex)

export default new Vuex.Store({
  actions,
  getters,
  modules: {
    common
  },
  strict: process.env.NODE_ENV !== 'production'
})
