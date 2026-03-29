<template>
  <div class="breadcrumb">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item v-for="(item, index) in breadcrumbItems" :key="index">
        {{ item }}
      </el-breadcrumb-item>
    </el-breadcrumb>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const breadcrumbItems = computed(() => {
  const path = route.path.slice(1) // 去除开头的 '/'
  const name = route.name as string

  const mappings: Record<string, string> = {
    'Dashboard': '仪表盘',
    'BotList': '机器人管理',
    'DifyAppList': 'Dify 应用',
    'BindingList': '绑定管理',
    'MessageLog': '消息日志',
    'Settings': '系统设置'
  }

  return [mappings[name] || name]
})
</script>

<style scoped>
.breadcrumb {
  padding: 8px 0;
}

:deep(.el-breadcrumb__inner) {
  color: #606266;
}

:deep(.el-breadcrumb__separator) {
  color: #c0c4cc;
}
</style>
