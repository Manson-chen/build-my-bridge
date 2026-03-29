<template>
  <div class="bot-list-container">
    <div class="toolbar">
      <el-input
        v-model="searchQuery"
        placeholder="搜索机器人名称..."
        clearable
        style="width: 240px"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-select v-model="filterStatus" placeholder="状态" clearable style="width: 120px">
        <el-option label="启用" value="enabled" />
        <el-option label="禁用" value="disabled" />
      </el-select>

      <el-button-group>
        <el-button @click="refreshList">
          <el-icon><Refresh /></el-icon>
        </el-button>
        <el-button type="success" @click="exportData">
          <el-icon><Download /></el-icon>
        </el-button>
      </el-button-group>

      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        + 添加机器人
      </el-button>
    </div>

    <el-table :data="filteredBots" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="name" label="名称" width="160">
        <template #default="{ row }">
          <el-tag type="info" size="small">
            <el-icon><ChatDotRound /></el-icon>
          </el-tag>
          <span style="margin-left: 8px">{{ row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="appId" label="AppID" width="200" />
      <el-table-column prop="bindingCount" label="绑定数" width="100" align="center" />
      <el-table-column label="状态" width="120" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.enabled" type="success">🟢 启用</el-tag>
          <el-tag v-else type="danger">🔴 禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
      />
    </div>

    <!-- 添加/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑飞书机器人' : '添加飞书机器人'"
      width="600px"
    >
      <el-form :model="form" label-width="120px" :rules="rules" ref="formRef">
        <el-form-item label="机器人名称" prop="name">
          <el-input v-model="form.name" placeholder="例如：客服助手" />
        </el-form-item>

        <el-form-item label="AppID" prop="appId">
          <el-input v-model="form.appId" placeholder="cli_xxxxxxxxx" />
        </el-form-item>

        <el-form-item label="AppSecret" prop="appSecret">
          <el-input
            v-model="form.appSecret"
            type="password"
            show-password
            placeholder="●●●●●●●●●●●●●●●●"
          />
        </el-form-item>

        <el-form-item label="Encrypt Key" prop="encryptKey">
          <el-input v-model="form.encryptKey" placeholder="可选，用于加密验证" />
        </el-form-item>

        <el-form-item label="Webhook URL" prop="webhookUrl">
          <el-input v-model="form.webhookUrl" placeholder="可选" />
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="用于客服场景的飞书机器人"
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
import { Search, Refresh, Download, Plus, ChatDotRound } from '@element-plus/icons-vue'

const searchQuery = ref('')
const filterStatus = ref('')
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(4)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()

// 模拟数据
const bots = ref([
  { id: 1, name: '客服助手', appId: 'cli_xxx123', bindingCount: 3, enabled: true },
  { id: 2, name: '技术支持', appId: 'cli_xxx456', bindingCount: 2, enabled: true },
  { id: 3, name: '产品咨询', appId: 'cli_xxx789', bindingCount: 1, enabled: false },
  { id: 4, name: '内部助手', appId: 'cli_xxx000', bindingCount: 0, enabled: true }
])

const filteredBots = computed(() => {
  let result = bots.value.filter(bot =>
    bot.name.includes(searchQuery.value) ||
    bot.appId.includes(searchQuery.value)
  )
  if (filterStatus.value) {
    const enabled = filterStatus.value === 'enabled'
    result = result.filter(bot => bot.enabled === enabled)
  }
  return result
})

const form = ref({
  id: 0,
  name: '',
  appId: '',
  appSecret: '',
  encryptKey: '',
  webhookUrl: '',
  description: '',
  enabled: true
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入机器人名称', trigger: 'blur' }],
  appId: [{ required: true, message: '请输入 AppID', trigger: 'blur' }],
  appSecret: [{ required: true, message: '请输入 AppSecret', trigger: 'blur' }]
}

const handleAdd = () => {
  isEdit.value = false
  form.value = {
    id: 0,
    name: '',
    appId: '',
    appSecret: '',
    encryptKey: '',
    webhookUrl: '',
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
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (isEdit.value) {
        ElMessage.success('保存成功')
      } else {
        ElMessage.success('添加成功')
      }
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

const exportData = () => {
  ElMessage.success('导出功能待实现')
}
</script>

<style scoped>
.bot-list-container {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.toolbar {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
