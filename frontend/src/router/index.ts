import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/Login.vue')
    },
    {
      path: '/',
      component: () => import('../layout/Layout.vue'),
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('../views/Dashboard.vue')
        },
        {
          path: 'bots',
          name: 'BotList',
          component: () => import('../views/BotList.vue')
        },
        {
          path: 'dify-apps',
          name: 'DifyAppList',
          component: () => import('../views/DifyAppList.vue')
        },
        {
          path: 'bindings',
          name: 'BindingList',
          component: () => import('../views/BindingList.vue')
        },
        {
          path: 'logs',
          name: 'MessageLog',
          component: () => import('../views/MessageLog.vue')
        },
        {
          path: 'settings',
          name: 'Settings',
          component: () => import('../views/Settings.vue')
        }
      ]
    }
  ],
})

export default router
