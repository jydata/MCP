import * as types from '../mutation-types'

const state = {
  tabs: [/*{
    name: 'dashboard',
    path: '/console/dashboard',
    meta: {
      title: '首页'
    }
  }*/],
  dashboardActive: true,
  viewNames: {
    release: 'release',
    sign: 'sign',
    role: 'role',
    business: 'business'
  }
};

const getters = {
  dashboardActive: state => state.dashboardActive,
  allTabs: state => state.tabs,
  releaseViewName: state => state.viewNames.release,
  signViewName: state => state.viewNames.sign,
  roleViewName: state => state.viewNames.role,
  businessViewName: state => state.viewNames.business
};

const actions = {
  addToTab ({commit}, tab) {
    commit(types.ADD_TO_TAB, tab);
  },
  removeFromTab ({commit}, tab) {
    commit(types.REMOVE_FROM_TAB, tab);
  },
  setDashboardActive ({commit}) {
    commit(types.SET_DASHBOARD_ACTIVE);
  },
  setTabActive ({commit}, tab) {
    commit(types.SET_TAB_ACTIVE, tab);
  },
  setTabViewName ({commit}, [bizName, viewName]) {
    commit(types.SET_TAB_VIEW_NAME, [bizName, viewName]);
  },
  setTabLabel ({commit}, [tabName, label]) {
    commit(types.SET_TAB_LABEL, [tabName, label]);
  }
}

const mutations = {
  [types.ADD_TO_TAB] (state, tab) {
    // Dashboard is a static tab, prevent add and remove
    if (tab.name === 'dashboard' || tab.name === 'page404') {
      return false;
    }
    let t = state.tabs.find((tempTab) => {
      return tempTab.name === tab.name;
    });
    // Add if not exists
    if (!t) {
      state.tabs.push(tab);
    }
  },
  [types.REMOVE_FROM_TAB] (state, tab) {
    if (tab.name === 'dashboard') {
      return false;
    }
    for (const [i, v] of state.tabs.entries()) {
      if (v.path === tab.path) {
        state.tabs.splice(i, 1);
        break;
      }
    }
    for (const i of state.tabs) {
      if (i === tab.name) {
        const index = state.tabs.indexOf(i);
        state.tabs.splice(index, 1);
        break;
      }
    }

    for (let k in state.viewNames) {
      if (k === tab.name) {
        state.viewNames[k] = k;
      }
    }

    // let index = state.tabs.findIndex((tempTab) => {
    //   return tempTab.name === tab.name;
    // });
    // if (index) {
    //   state.tabs.splice(index,1);
    //   console.log(state.tabs);
    // }
  },
  [types.SET_DASHBOARD_ACTIVE] (state) {
    state.tabs.forEach(function (tabTemp) {
      tabTemp.isActive = false;
    });
    state.dashboardActive = true;
  },
  [types.SET_TAB_ACTIVE] (state, tab) {
    state.dashboardActive = false;
    state.tabs.forEach(function (tabTemp) {
      if (tabTemp.name === tab.name) {
        tabTemp.isActive = true;
      } else {
        tabTemp.isActive = false;
      }
    })
  },
  [types.SET_TAB_VIEW_NAME] (state, [bizName, viewName]) {
    state.viewNames[bizName] = viewName;
  },
  [types.SET_TAB_LABEL] (state, [tabName, label]) {
    let t = state.tabs.find((tempTab) => {
      return tempTab.name === tabName;
    });
    if (t) {
      t.label = label;
    }
  }
};

export default {
  state,
  getters,
  actions,
  mutations
}
