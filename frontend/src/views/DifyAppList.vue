<template>
  <div class="dify-app-list-container">
    <div class="toolbar">
      <el-input
        v-model="searchQuery"
        placeholder="搜索应用名称..."
        clearable
        style="width: 240px"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-select v-model="filterType" placeholder="类型" clearable style="width: 120px">
        <el-option label="Chatflow" value="chatflow" />
        <el-option label="Workflow" value="workflow" />
      </el-select>

      <el-select v-model="filterStatus" placeholder="状态" clearable style="width: 120px">
        <el-option label="启用" value="enabled" />
        <el-option label="禁用" value="disabled" />
      </el-select>

      <el-button-group>
        <el-button @click="refreshList">
          <el-icon><Refresh /></el-icon>
        </el-button>
      </el-button-group>

      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        + 添加应用
      </el-button>
    </div>

    <el-table :data="filteredApps" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="name" label="名称" min-width="160">
        <template #default="{ row }">
          <el-tag type="success" size="small">
            <el-icon><Link /></el-icon>
          </el-tag>
          <span style="margin-left: 8px">{{ row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="appType" label="类型" width="120">
        <template #default="{ row }">
          <el-tag v-if="row.appType === 'chatflow'" type="info" size="small">Chatflow</el-tag>
          <el-tag v-else type="warning" size="small">Workflow</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="baseUrl" label="基础 URL" min-width="220">
        <template #default="{ row }">
          <span class="url-text">{{ shortenUrl(row.baseUrl) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.enabled" type="success" size="small">🟢 启用</el-tag>
          <el-tag v-else type="danger" size="small">🔴 禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" :resizable="false">
        <template #default="{ row }">
          <div class="action-buttons">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" size="small" @click="handleTest(row)">测试</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑 Dify 应用' : '添加 Dify 应用'"
      width="600px"
    >
      <el-form :model="form" label-width="120px" :rules="rules" ref="formRef">
        <el-form-item label="应用名称" prop="name">
          <el-input v-model="form.name" placeholder="例如：AI 问答助手" />
        </el-form-item>

        <el-form-item label="应用类型" prop="appType">
          <el-select v-model="form.appType" style="width: 100%">
            <el-option label="Chatflow (对话式)" value="chatflow" />
            <el-option label="Workflow (工作流)" value="workflow" />
          </el-select>
        </el-form-item>

        <el-form-item label="基础 URL" prop="baseUrl">
          <el-input v-model="form.baseUrl" placeholder="http://localhost/v1" />
        </el-form-item>

        <el-form-item label="API Key" prop="apiKey">
          <el-input
            v-model="form.apiKey"
            type="password"
            show-password
            placeholder="app-xxxxxxxxxxxxxxxxxxxxx"
          />
        </el-form-item>

        <el-form-item label="App ID" prop="appId">
          <el-input v-model="form.appId" placeholder="可选" />
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="用于客服场景的 AI 问答助手"
          />
        </el-form-item>

        <el-form-item label="启用状态" prop="enabled">
          <el-switch v-model="form.enabled" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { Search, Refresh, Plus, Link } from '@element-plus/icons-vue'

const searchQuery = ref('')
const filterType = ref('')
const filterStatus = ref('')
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()

// 模拟数据
const apps = ref([
  { id: 1, name: 'AI 问答助手', appType: 'chatflow', baseUrl: 'http://localhost:5001/v1', enabled: true },
  { id: 2, name: '技术文档', appType: 'workflow', baseUrl: 'http://localhost:5001/v1', enabled: true },
  { id: 3, name: '产品介绍', appType: 'chatflow', baseUrl: 'http://localhost:5001/v1', enabled: false }
])

const filteredApps = computed(() => {
  let result = apps.value.filter(app =>
    app.name.includes(searchQuery.value)
  )
  if (filterType.value) {
    result = result.filter(app => app.appType === filterType.value)
  }
  if (filterStatus.value) {
    const enabled = filterStatus.value === 'enabled'
    result = result.filter(app => app.enabled === enabled)
  }
  return result
})

const form = ref({
  id: 0,
  name: '',
  appType: 'chatflow',
  baseUrl: '',
  apiKey: '',
  appId: '',
  description: '',
  enabled: true
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入应用名称', trigger: 'blur' }],
  baseUrl: [{ required: true, message: '请输入基础 URL', trigger: 'blur' }],
  apiKey: [{ required: true, message: '请输入 API Key', trigger: 'blur' }]
}

const handleAdd = () => {
  isEdit.value = false
  form.value = {
    id: 0,
    name: '',
    appType: 'chatflow',
    baseUrl: '',
    apiKey: '',
    appId: '',
    description: '',
    enabled: true
  }
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate((valid) => {
    if (valid) {
      ElMessage.success(isEdit.value ? '保存成功' : '添加成功')
      dialogVisible.value = false
    }
  })
}

const handleTest = (row: any) => {
  ElMessage.info(`正在测试 ${row.name} 的连接...`)
}

const handleDelete = (row: any) => {
  ElMessage.warning('删除功能待实现')
}

const refreshList = () => {
  ElMessage.success('已刷新')
}

const shortenUrl = (url: string) => {
  if (url.length <= 30) return url
  return url.substring(0, 27) + '...'
}
</script>

<style scoped>
.dify-app-list-container {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.toolbar {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
}

:deep(.el-table) {
  flex: 1;
}

:deep(.el-table .action-buttons) {
  display: flex;
  gap: 4px;
  justify-content: center;
  flex-wrap: nowrap;
}

:deep(.el-table .action-buttons .el-button) {
  white-space: nowrap;
  padding: 5px 8px;
  font-size: 12px;
}

.url-text {
  color: #409EFF;
  font-size: 13px;
}
</style>
