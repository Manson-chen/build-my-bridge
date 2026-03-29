<template>
  <div class="binding-list-container">
    <el-alert
      title="绑定关系说明"
      type="info"
      :closable="false"
      show-icon
      style="margin-bottom: 20px"
    >
      绑定关系表示飞书机器人和 Dify 应用之间的映射。当机器人收到消息时，会路由到优先级最高的绑定的 Dify 应用。
    </el-alert>

    <el-alert
      title="优先级说明"
      type="warning"
      :closable="false"
      show-icon
      style="margin-bottom: 20px"
    >
      优先级数字越大越优先，相同优先级按创建时间排序。一个机器人可以绑定多个 Dify 应用。
    </el-alert>

    <div class="toolbar">
      <el-input
        v-model="searchQuery"
        placeholder="搜索..."
        clearable
        style="width: 240px"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-select v-model="filterBot" placeholder="机器人" clearable style="width: 150px">
        <el-option label="客服助手" value="1" />
        <el-option label="技术支持" value="2" />
        <el-option label="产品咨询" value="3" />
        <el-option label="内部助手" value="4" />
      </el-select>

      <el-select v-model="filterDify" placeholder="Dify 应用" clearable style="width: 180px">
        <el-option label="AI 问答助手" value="1" />
        <el-option label="技术文档" value="2" />
        <el-option label="产品介绍" value="3" />
      </el-select>

      <el-select v-model="filterStatus" placeholder="状态" clearable style="width: 120px">
        <el-option label="启用" value="enabled" />
        <el-option label="禁用" value="disabled" />
      </el-select>

      <el-button @click="refreshList">
        <el-icon><Refresh /></el-icon>
      </el-button>

      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        + 添加绑定
      </el-button>
    </div>

    <el-table :data="filteredBindings" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="botName" label="机器人" min-width="150">
        <template #default="{ row }">
          <el-tag type="info" size="small">
            <el-icon><ChatDotRound /></el-icon>
          </el-tag>
          <span style="margin-left: 8px">{{ row.botName }}</span>
        </template>
      </el-table-column>

      <el-table-column prop="difyAppName" label="Dify 应用" min-width="160">
        <template #default="{ row }">
          <el-tag type="success" size="small">
            <el-icon><Link /></el-icon>
          </el-tag>
          <span style="margin-left: 8px">{{ row.difyAppName }}</span>
        </template>
      </el-table-column>

      <el-table-column prop="priority" label="优先级" width="120" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.priority > 5" type="danger" size="small">{{ row.priority }} (高)</el-tag>
          <el-tag v-else-if="row.priority > 0" type="warning" size="small">{{ row.priority }} (中)</el-tag>
          <el-tag v-else type="info" size="small">{{ row.priority }} (低)</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.enabled" type="success" size="small">🟢 启用</el-tag>
          <el-tag v-else type="danger" size="small">🔴 禁用</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="150" align="center" :resizable="false">
        <template #default="{ row }">
          <div class="action-buttons">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑绑定关系' : '添加绑定关系'"
      width="600px"
    >
      <el-form :model="form" label-width="150px" :rules="rules" ref="formRef">
        <el-form-item label="选择飞书机器人" prop="botId">
          <el-select v-model="form.botId" style="width: 100%" placeholder="请选择机器人">
            <el-option
              v-for="bot in bots"
              :key="bot.id"
              :label="bot.name"
              :value="bot.id"
            >
              <span>{{ bot.name }}</span>
              <span style="color: #8492a6; font-size: 13px">({{ bot.appId }})</span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="选择 Dify 应用" prop="difyAppId">
          <el-select v-model="form.difyAppId" style="width: 100%" placeholder="请选择应用">
            <el-option
              v-for="app in difyApps"
              :key="app.id"
              :label="app.name"
              :value="app.id"
            >
              <span>{{ app.name }}</span>
              <span style="color: #8492a6; font-size: 13px">( {{ app.appType }} )</span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="优先级" prop="priority">
          <el-input-number
            v-model="form.priority"
            :min="0"
            :max="100"
            :step="1"
            style="width: 100%"
          />
          <div style="font-size: 12px; color: #909399; margin-top: 4px">
            数字越大优先级越高，默认 0
          </div>
        </el-form-item>

        <el-form-item label="启用状态" prop="enabled">
          <el-switch v-model="form.enabled" />
        </el-form-item>

        <el-alert
          title="路由规则"
          type="info"
          :closable="false"
          show-icon
        >
          当此机器人收到消息时，将优先路由到优先级最高的启用的 Dify 应用
        </el-alert>
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
import { Search, Refresh, Plus, ChatDotRound, Link } from '@element-plus/icons-vue'

const searchQuery = ref('')
const filterBot = ref('')
const filterDify = ref('')
const filterStatus = ref('')
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()

// 模拟数据 - 机器人列表
const bots = ref([
  { id: 1, name: '客服助手', appId: 'cli_xxx123' },
  { id: 2, name: '技术支持', appId: 'cli_xxx456' },
  { id: 3, name: '产品咨询', appId: 'cli_xxx789' },
  { id: 4, name: '内部助手', appId: 'cli_xxx000' }
])

// 模拟数据 - Dify 应用列表
const difyApps = ref([
  { id: 1, name: 'AI 问答助手', appType: 'Chatflow' },
  { id: 2, name: '技术文档', appType: 'Workflow' },
  { id: 3, name: '产品介绍', appType: 'Chatflow' }
])

// 模拟数据 - 绑定关系
const bindings = ref([
  { id: 1, botId: 1, botName: '客服助手', difyAppId: 1, difyAppName: 'AI 问答助手', priority: 10, enabled: true },
  { id: 2, botId: 1, botName: '客服助手', difyAppId: 3, difyAppName: '产品介绍', priority: 5, enabled: true },
  { id: 3, botId: 2, botName: '技术支持', difyAppId: 2, difyAppName: '技术文档', priority: 10, enabled: true },
  { id: 4, botId: 3, botName: '产品咨询', difyAppId: 3, difyAppName: '产品介绍', priority: 10, enabled: false },
  { id: 5, botId: 4, botName: '内部助手', difyAppId: 1, difyAppName: 'AI 问答助手', priority: 0, enabled: true }
])

const filteredBindings = computed(() => {
  let result = bindings.value.filter(b =>
    b.botName.includes(searchQuery.value) ||
    b.difyAppName.includes(searchQuery.value)
  )
  if (filterBot.value) {
    result = result.filter(b => b.botId === parseInt(filterBot.value))
  }
  if (filterDify.value) {
    result = result.filter(b => b.difyAppId === parseInt(filterDify.value))
  }
  if (filterStatus.value) {
    const enabled = filterStatus.value === 'enabled'
    result = result.filter(b => b.enabled === enabled)
  }
  return result
})

const form = ref({
  id: 0,
  botId: null as number | null,
  difyAppId: null as number | null,
  priority: 0,
  enabled: true
})

const rules: FormRules = {
  botId: [{ required: true, message: '请选择机器人', trigger: 'change' }],
  difyAppId: [{ required: true, message: '请选择 Dify 应用', trigger: 'change' }]
}

const handleAdd = () => {
  isEdit.value = false
  form.value = {
    id: 0,
    botId: null,
    difyAppId: null,
    priority: 0,
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

const handleDelete = (row: any) => {
  ElMessage.warning('删除功能待实现')
}

const refreshList = () => {
  ElMessage.success('已刷新')
}
</script>

<style scoped>
.binding-list-container {
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
  flex-wrap: wrap;
  flex-shrink: 0;
}

:deep(.el-table) {
  flex: 1;
}

:deep(.el-table .action-buttons) {
  display: flex;
  gap: 8px;
  justify-content: center;
  flex-wrap: nowrap;
}

:deep(.el-table .action-buttons .el-button) {
  white-space: nowrap;
}
</style>
