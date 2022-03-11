
const ConsoleLayout = resolve => require(['@page/ConsoleLayout'], resolve);

Vue.use(VueRouter);

export default new VueRouter({
  history: true,
  hashbang: false,
  base: __dirname,
  routes: [
    {path: '/login', name: 'login', component: () => import('@page/Login')},
    {path: '/', name: 'console', component: () => import('@page/ConsoleLoading')},
    {
      path: '/console', component: ConsoleLayout,
      children: [
        {path: 'dashboard', name: 'dashboard', component: () => import('@page/Dashboard'),
          meta: {title: 'Home', noCache: true}}
      ]
    },
    {
      path: '/user', component: ConsoleLayout,
      children: [
        {
          path: 'UserManager',
          name: 'UserManager',
          component: () => import('@page/user/UserManager'),
          meta: {title: 'User Management', noCache: true}
        }
      ]
    },
    {
      path: '/error', component: ConsoleLayout,
      children: [
        {path: '404', name: 'page404', component: () => import('@page/error/404'), meta: {title: 'Page not found!'}},
      ]
    },

    //Metadata
    {
      path: '/meta', component: ConsoleLayout,
      children: [
        {
          path: 'DSConnections',
          name: 'DSConnections',
          component: () => import('@page/meta/DSConnections'),
          meta: {title: 'DS Connections', noCache: true}
        },
        {
          path: 'DSRouting',
          name: 'DSRouting',
          component: () => import('@page/meta/DSRouting'),
          meta: {title: 'DS Routing', noCache: true}
        },
        {
          path: 'DSMetadataSync',
          name: 'DSMetadataSync',
          component: () => import('@page/meta/DSMetadataSync'),
          meta: {title: 'Synchronize Source Metadata', noCache: true}
        },
        {
          path: 'GenerateTargetDDL',
          name: 'GenerateTargetDDL',
          component: () => import('@page/meta/GenerateTargetDDL'),
          meta: {title: 'Generate Target DDL', noCache: true}
        },
        {
          path: 'DSRules',
          name: 'DSRules',
          component: () => import('@page/meta/DSRules'),
          meta: {title: 'DS Rules', noCache: true}
        },
      ]
    },

    //Project
    {
      path: '/project', component: ConsoleLayout,
      children: [
        {
          path: 'NewProject',
          name: 'NewProject',
          component: () => import('@page/project/NewProject'),
          meta: {title: 'DS Connections', noCache: true}
        },
        {
          path: 'ProjectList',
          name: 'ProjectList',
          component: () => import('@page/project/ProjectList'),
          meta: {title: 'DS Routing', noCache: true}
        },
        {
          path: 'NewJob',
          name: 'NewJob',
          component: () => import('@page/project/NewJob'),
          meta: {title: 'Synchronize Source Metadata', noCache: true}
        },
        {
          path: 'JobsDefinition',
          name: 'JobsDefinition',
          component: () => import('@page/project/JobsDefinition'),
          meta: {title: 'Generate Target DDL', noCache: true},
          children:[
            {
              path: ':name',
              name: 'JobsDefinition',
            },
            {
              path: ':name/:type',
              name: 'JobsDefinition',
            },
          ]
        },
        {
          path: 'JobExecution/:id/:logId/:time',
          name: 'JobExecution',
          component: () => import('@page/project/JobExecution'),
          meta: {title: 'DS Rules', noCache: true}
        },
      ]
    },

    //User
    {
      path: '/user', component: ConsoleLayout,
      children: [
        {
          path: 'AddUser',
          name: 'AddUser',
          component: () => import('@page/user/AddUser'),
          meta: {title: 'DS Connections', noCache: true}
        },
        {
          path: 'UserList',
          name: 'UserList',
          component: () => import('@page/user/UserList'),
          meta: {title: 'DS Routing', noCache: true}
        },
        {
          path: 'MyProfile',
          name: 'MyProfile',
          component: () => import('@page/user/MyProfile'),
          meta: {title: 'Synchronize Source Metadata', noCache: true}
        },
        {
          path: 'ChangePassword',
          name: 'ChangePassword',
          component: () => import('@page/user/ChangePassword'),
          meta: {title: 'Generate Target DDL', noCache: true}
        }
      ]
    },

    {path: '*', redirect: '/error/404'}
  ]
})
