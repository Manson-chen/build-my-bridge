<template>
  <div class="message-log-container">
    <div class="filter-section">
      <span style="font-weight: 500; margin-right: 16px">筛选条件：</span>

      <el-select v-model="filters.botId" placeholder="机器人" clearable style="width: 130px">
        <el-option label="客服助手" value="1" />
        <el-option label="技术支持" value="2" />
        <el-option label="产品咨询" value="3" />
        <el-option label="内部助手" value="4" />
      </el-select>

      <el-select v-model="filters.difyAppId" placeholder="Dify 应用" clearable style="width: 150px">
        <el-option label="AI 问答助手" value="1" />
        <el-option label="技术文档" value="2" />
        <el-option label="产品介绍" value="3" />
      </el-select>

      <el-select v-model="filters.status" placeholder="状态" clearable style="width: 120px">
        <el-option label="成功" value="success" />
        <el-option label="失败" value="failed" />
      </el-select>

      <el-date-picker
        v-model="filters.dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="YYYY-MM-DD"
        style="width: 240px"
      />

      <el-button @click="applyFilters">应用筛选</el-button>
      <el-button plain @click="clearFilters">清除筛选</el-button>
      <el-button type="primary" @click="exportLogs">导出</el-button>
    </div>

    <el-table :data="filteredLogs" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="time" label="时间" width="160" sortable />

      <el-table-column label="机器人" min-width="120">
        <template #default="{ row }">
          <el-tag type="info" size="small">
            <el-icon><ChatDotRound /></el-icon>
          </el-tag>
          <span style="margin-left: 6px">{{ row.botName }}</span>
        </template>
      </el-table-column>

      <el-table-column label="Dify 应用" min-width="140">
        <template #default="{ row }">
          <el-tag type="success" size="small">
            <el-icon><Link /></el-icon>
          </el-tag>
          <span style="margin-left: 6px">{{ row.difyAppName }}</span>
        </template>
      </el-table-column>

      <el-table-column prop="userName" label="用户" min-width="100" />

      <el-table-column prop="msgType" label="类型" width="80">
        <template #default="{ row }">
          <el-tag size="small">{{ row.msgType }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag v-if="row.status === 'success'" type="success" size="small">✅</el-tag>
          <el-tag v-else type="danger" size="small">❌</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="耗时" width="100">
        <template #default="{ row }">
          {{ row.latency }}ms
        </template>
      </el-table-column>

      <el-table-column label="操作" width="100" align="center">
        <template #default="{ row }">
          <div class="action-buttons">
            <el-button type="primary" size="small" @click="viewDetail(row)">详情</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <span style="margin-right: 16px">共 {{ total }} 条记录</span>
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
      />
      <span style="margin-left: 16px">20/页</span>
    </div>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="消息详情"
      width="700px"
    >
      <div v-if="currentLog" class="log-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="消息 ID">{{ currentLog.messageId }}</el-descriptions-item>
          <el-descriptions-item label="时间">{{ currentLog.time }}</el-descriptions-item>
          <el-descriptions-item label="耗时">{{ currentLog.latency }} ms</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag v-if="currentLog.status === 'success'" type="success">🟢 成功</el-tag>
            <el-tag v-else type="danger">🔴 失败</el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <div class="section-title">飞书信息</div>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="机器人">{{ currentLog.botName }}</el-descriptions-item>
          <el-descriptions-item label="用户">{{ currentLog.userName }} ({{ currentLog.userId }})</el-descriptions-item>
          <el-descriptions-item label="消息类型">{{ currentLog.msgType }}</el-descriptions-item>
        </el-descriptions>

        <div class="section-title">Dify 信息</div>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="应用">{{ currentLog.difyAppName }}</el-descriptions-item>
          <el-descriptions-item label="类型">{{ currentLog.difyType }}</el-descriptions-item>
          <el-descriptions-item label="响应 ID">{{ currentLog.responseId }}</el-descriptions-item>
        </el-descriptions>

        <div class="section-title">用户消息</div>
        <div class="message-box user-message">
          {{ currentLog.userMessage }}
        </div>

        <div class="section-title">机器人回复</div>
        <div class="message-box bot-message">
          {{ currentLog.botMessage }}
        </div>

        <el-alert
          v-if="currentLog.errorMessage"
          :title="currentLog.errorMessage"
          type="error"
          :closable="false"
          show-icon
        />
      </div>

      <template #footer>
        <el-button type="primary" @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { ChatDotRound, Link } from '@element-plus/icons-vue'

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(1234)
const detailVisible = ref(false)
const currentLog = ref<any>(null)

const filters = ref({
  botId: null as string | null,
  difyAppId: null as string | null,
  status: null as string | null,
  dateRange: null as string[] | null
})

// 模拟数据
const logs = ref([
  { id: 1, time: '2024-01-15 10:23:15', botName: '客服助手', difyAppName: 'AI 问答助手', userName: '张三', msgType: 'text', status: 'success', latency: 1234, messageId: 'msg_xxx001', userId: 'ou_xxx001', difyType: 'Chatflow', responseId: 'conv_xxx001', userMessage: '你好，请问如何重置密码？', botMessage: '您好！重置密码的步骤如下:\n1. 进入登录页面\n2. 点击忘记密码\n3. 输入您的邮箱地址\n4. 查收邮件并点击重置链接', errorMessage: '' },
  { id: 2, time: '2024-01-15 10:20:42', botName: '技术支持', difyAppName: '技术文档', userName: '李四', msgType: 'image', status: 'success', latency: 2345, messageId: 'msg_xxx002', userId: 'ou_xxx002', difyType: 'Workflow', responseId: 'wf_xxx001', userMessage: '[图片消息]', botMessage: '这张图片显示的是系统配置界面...', errorMessage: '' },
  { id: 3, time: '2024-01-15 10:15:08', botName: '客服助手', difyAppName: 'AI 问答助手', userName: '王五', msgType: 'text', status: 'failed', latency: 30000, messageId: 'msg_xxx003', userId: 'ou_xxx003', difyType: 'Chatflow', responseId: '', userMessage: '请问产品价格是多少？', botMessage: '', errorMessage: 'Dify API timeout after 30s' },
  { id: 4, time: '2024-01-15 10:10:33', botName: '产品咨询', difyAppName: '产品介绍', userName: '赵六', msgType: 'text', status: 'success', latency: 1567, messageId: 'msg_xxx004', userId: 'ou_xxx004', difyType: 'Chatflow', responseId: 'conv_xxx002', userMessage: '请介绍一下你们的产品', botMessage: '我们的产品主打 AI 驱动的智能服务...', errorMessage: '' },
  { id: 5, time: '2024-01-15 10:05:21', botName: '客服助手', difyAppName: 'AI 问答助手', userName: '钱七', msgType: 'text', status: 'success', latency: 987, messageId: 'msg_xxx005', userId: 'ou_xxx005', difyType: 'Chatflow', responseId: 'conv_xxx003', userMessage: '工作时间是几点到几点？', botMessage: '我们的工作时间为周一至周五 9:00-18:00', errorMessage: '' }
])

const filteredLogs = computed(() => {
  return logs.value.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value)
})

const applyFilters = () => {
  ElMessage.success('应用筛选')
}

const clearFilters = () => {
  filters.value = {
    botId: null,
    difyAppId: null,
    status: null,
    dateRange: null
  }
  ElMessage.success('已清除筛选')
}

const exportLogs = () => {
  ElMessage.success('导出功能待实现')
}

const viewDetail = (row: any) => {
  currentLog.value = row
  detailVisible.value = true
}

const formatTime = (time: string) => {
  return time
}
</script>

<style scoped>
.message-log-container {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.filter-section {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  align-items: center;
  flex-wrap: wrap;
  flex-shrink: 0;
}

:deep(.el-table) {
  flex: 1;
}

:deep(.el-table .action-buttons) {
  display: flex;
  justify-content: center;
  white-space: nowrap;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  flex-shrink: 0;
}

.log-detail {
  max-height: 600px;
  overflow-y: auto;
}

.section-title {
  font-weight: 600;
  margin: 20px 0 10px;
  color: #303133;
}

.message-box {
  padding: 16px;
  border-radius: 8px;
  line-height: 1.6;
  white-space: pre-wrap;
}

.user-message {
  background: #f0f9ff;
  border-left: 4px solid #409EFF;
}

.bot-message {
  background: #f0f9f0;
  border-left: 4px solid #67C23A;
}
</style>
