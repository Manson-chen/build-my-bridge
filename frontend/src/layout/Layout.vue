<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside width="220px" class="sidebar">
      <router-link to="/dashboard" class="logo-link">
        <el-icon :size="28" color="#409EFF"><Connection /></el-icon>
        <span class="logo-text">BuildMyBridge</span>
      </router-link>

      <el-menu
        mode="vertical"
        class="sidebar-menu"
        :default-active="defaultActive"
      >
        <el-menu-item index="/dashboard" @click="$router.push('/dashboard')">
          <el-icon><DataLine /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>

        <el-menu-item index="/bots" @click="$router.push('/bots')">
          <el-icon><ChatDotRound /></el-icon>
          <span>机器人管理</span>
        </el-menu-item>

        <el-menu-item index="/dify-apps" @click="$router.push('/dify-apps')">
          <el-icon><Link /></el-icon>
          <span>Dify 应用</span>
        </el-menu-item>

        <el-menu-item index="/bindings" @click="$router.push('/bindings')">
          <el-icon><Share /></el-icon>
          <span>绑定管理</span>
        </el-menu-item>

        <el-menu-item index="/logs" @click="$router.push('/logs')">
          <el-icon><Document /></el-icon>
          <span>消息日志</span>
        </el-menu-item>

        <el-divider />

        <el-menu-item index="/settings" @click="$router.push('/settings')">
          <el-icon><Setting /></el-icon>
          <span>系统设置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container class="main-container">
      <!-- 顶部栏 -->
      <el-header class="header">
        <div class="header-left">
          <breadcrumb :key="route.path" />
        </div>
        <div class="header-right">
          <el-badge :value="3" class="message-badge">
            <el-icon :size="20"><Bell /></el-icon>
          </el-badge>

          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :icon="UserFilled" />
              <span class="username">管理员</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="settings">账号设置</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区域 -->
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <keep-alive>
            <component :is="Component" :key="route.path" />
          </keep-alive>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Connection,
  DataLine,
  ChatDotRound,
  Link,
  Share,
  Document,
  Setting,
  Bell,
  UserFilled,
  ArrowDown
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => {
  // 处理根路径默认显示仪表盘
  if (route.path === '/') return '/dashboard'
  return route.path
})

const defaultActive = computed(() => {
  const path = route.path
  // 如果是子路由页面，返回当前路径
  if (path !== '/' && !path.startsWith('/dashboard')) {
    return path
  }
  // 根路径和仪表盘都返回 /dashboard
  return '/dashboard'
})

const handleCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      router.push('/settings')
      break
    case 'logout':
      ElMessage.success('已退出登录')
      router.push('/login')
      break
  }
}
</script>

<style scoped>
.layout-container {
  display: flex;
  width: 100%;
  height: 100vh;
}

.sidebar {
  background-color: #304156;
  color: #fff;
  flex-shrink: 0;
}

.logo-link {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  height: 60px;
  border-bottom: 1px solid #1f2d3d;
  color: inherit;
  text-decoration: none;
}

.sidebar-menu {
  border-right: none;
  background-color: transparent;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
}

.sidebar-menu :deep(.el-menu-item) {
  color: #bfcbd9;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background-color: #263445;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  color: #409EFF;
  background-color: #263445;
}

.main-container {
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow: hidden;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  height: 60px !important;
  flex-shrink: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.message-badge {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  font-size: 14px;
  color: #606266;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
  flex: 1;
  overflow-y: auto;
}
</style>
