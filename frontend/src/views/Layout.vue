<template>
  <div class="layout">
    <!-- 左侧导航 -->
    <div class="sidebar">
      <div class="logo">
        智慧养老系统
        <span>AI+认知衰弱管理</span>
      </div>
      <div class="nav-menu">
        <div
          v-for="item in menuItems"
          :key="item.path"
          class="nav-item"
          :class="{ active: currentRoute === item.path }"
          @click="$router.push(item.path)"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </div>
      </div>
    </div>

    <!-- 右侧内容 -->
    <div class="main-content">
      <div class="header">
        <div class="header-title">{{ currentTitle }}</div>
        <div class="header-user">
          <div class="user-info">
            <div class="user-name">{{ user.username }}</div>
            <div class="user-role">{{ user.role }}</div>
          </div>
          <div class="user-avatar">{{ user.username?.charAt(0) }}</div>
          <el-button type="danger" size="small" plain @click="handleLogout">退出</el-button>
        </div>
      </div>
      <div class="content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  HomeFilled, DataAnalysis, User, Setting, Document,
  List, EditPen, Warning, Management
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const user = computed(() => {
  try { return JSON.parse(localStorage.getItem('user') || '{}') } catch { return {} }
})

const currentRoute = computed(() => route.path)

const currentTitle = computed(() => route.meta?.title || '数据采集系统')

const allMenuItems = [
  { label: '首页概览', path: '/dashboard', icon: HomeFilled },
  { label: '老人健康档案', path: '/elder-list', icon: Document },
  { label: '认知评估管理', path: '/assessment-list', icon: EditPen },
  { label: '干预计划管理', path: '/intervention-plan', icon: List },
  { label: '干预执行记录', path: '/intervention-execution', icon: Management },
  { label: '数据采集管理', path: '/data-list', icon: DataAnalysis },
  { label: '风险评估预警', path: '/risk-warning', icon: Warning },
  { label: '用户管理', path: '/user-list', icon: User, roles: ['admin'] },
  { label: '系统管理', path: '/system', icon: Setting, roles: ['admin'] }
]

const menuItems = computed(() => {
  const role = user.value.role
  return allMenuItems.filter(item => !item.roles || item.roles.includes(role))
})

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  router.push('/login')
}
</script>

<style scoped>
.layout {
  display: flex;
  height: 100vh;
}
.sidebar {
  width: 220px;
  background: linear-gradient(180deg, #1a5c7d 0%, #0d3a52 100%);
  color: #fff;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  overflow-y: auto;
}
.logo {
  padding: 20px;
  font-size: 18px;
  font-weight: bold;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(0, 0, 0, 0.2);
}
.logo span {
  display: block;
  font-size: 12px;
  font-weight: normal;
  margin-top: 5px;
  opacity: 0.8;
}
.nav-menu {
  flex: 1;
  padding: 15px 0;
}
.nav-item {
  padding: 12px 20px;
  cursor: pointer;
  transition: all 0.3s;
  border-left: 3px solid transparent;
  display: flex;
  align-items: center;
  gap: 10px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
}
.nav-item:hover {
  background: rgba(255, 255, 255, 0.1);
}
.nav-item.active {
  background: rgba(255, 255, 255, 0.15);
  border-left-color: #4fc3f7;
  color: #4fc3f7;
}
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.header {
  height: 60px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 25px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  flex-shrink: 0;
}
.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a5c7d;
}
.header-user {
  display: flex;
  align-items: center;
  gap: 15px;
}
.user-avatar {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #1a5c7d, #4fc3f7);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 14px;
}
.user-info {
  text-align: right;
}
.user-name {
  font-size: 14px;
  font-weight: 500;
}
.user-role {
  font-size: 12px;
  color: #999;
}
.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}
</style>
