import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  // 患者手机端路由
  {
    path: '/mobile/login',
    name: 'PatientLogin',
    component: () => import('../views/mobile/PatientLogin.vue')
  },
  {
    path: '/mobile/portal',
    name: 'PatientPortal',
    component: () => import('../views/mobile/PatientPortal.vue')
  },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '首页概览' }
      },
      {
        path: 'elder-list',
        name: 'ElderList',
        component: () => import('../views/ElderList.vue'),
        meta: { title: '老人健康档案' }
      },
      {
        path: 'assessment-list',
        name: 'AssessmentList',
        component: () => import('../views/AssessmentList.vue'),
        meta: { title: '认知评估管理' }
      },
      {
        path: 'intervention-plan',
        name: 'InterventionPlanList',
        component: () => import('../views/InterventionPlanList.vue'),
        meta: { title: '干预计划管理' }
      },
      {
        path: 'intervention-execution',
        name: 'InterventionExecutionList',
        component: () => import('../views/InterventionExecutionList.vue'),
        meta: { title: '干预执行记录' }
      },
      {
        path: 'data-list',
        name: 'DataList',
        component: () => import('../views/DataList.vue'),
        meta: { title: '数据采集管理' }
      },
      {
        path: 'risk-warning',
        name: 'RiskWarning',
        component: () => import('../views/RiskWarning.vue'),
        meta: { title: '风险评估预警' }
      },
      {
        path: 'user-list',
        name: 'UserList',
        component: () => import('../views/UserList.vue'),
        meta: { title: '用户管理', roles: ['admin'] }
      },
      {
        path: 'system',
        name: 'SystemManage',
        component: () => import('../views/SystemManage.vue'),
        meta: { title: '系统管理', roles: ['admin'] }
      },
      {
        path: 'homecare/visit-plan',
        name: 'HomecareVisitPlan',
        component: () => import('../views/HomecareVisitPlan.vue'),
        meta: { title: '随访计划管理' }
      },
      {
        path: 'homecare/health-monitor',
        name: 'HomecareHealthMonitor',
        component: () => import('../views/HomecareHealthMonitor.vue'),
        meta: { title: '健康监测' }
      },
      {
        path: 'homecare/health-alert',
        name: 'HomecareHealthAlert',
        component: () => import('../views/HomecareHealthAlert.vue'),
        meta: { title: '健康预警' }
      },
      {
        path: 'homecare/message',
        name: 'HomecareMessage',
        component: () => import('../views/HomecareMessage.vue'),
        meta: { title: '医患沟通' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  let user = {}
  try { user = JSON.parse(localStorage.getItem('user') || '{}') } catch { }

  // 移动端路由处理
  if (to.path.startsWith('/mobile/')) {
    if (to.path === '/mobile/login') {
      // 已登录患者跳转到门户
      if (token && user.role === 'patient') {
        next('/mobile/portal')
        return
      }
      // 已登录非患者跳转到PC端
      if (token && user.role !== 'patient') {
        next('/dashboard')
        return
      }
    } else {
      // 未登录跳转到登录页
      if (!token) {
        next('/mobile/login')
        return
      }
      // 非患者不允许访问手机端
      if (user.role !== 'patient') {
        next('/dashboard')
        return
      }
    }
    next()
    return
  }

  // PC端路由处理
  if (to.path !== '/login' && !token) {
    next('/login')
    return
  }

  // 患者不允许访问PC端，强制跳转手机端
  if (token && user.role === 'patient') {
    next('/mobile/portal')
    return
  }

  // 已登录用户访问登录页跳转到首页
  if (to.path === '/login' && token) {
    next('/dashboard')
    return
  }

  const requiredRoles = to.meta?.roles
  if (requiredRoles && requiredRoles.length > 0) {
    if (!requiredRoles.includes(user.role)) {
      next('/dashboard')
      return
    }
  }
  next()
})

export default router
