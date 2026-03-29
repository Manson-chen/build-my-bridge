<template>
  <div class="dashboard-container">
    <div class="stats-cards">
      <el-card class="stat-card">
        <div class="card-icon">
          <el-icon :size="36" color="#409EFF"><ChatDotRound /></el-icon>
        </div>
        <div class="card-info">
          <div class="card-value">{{ stats.bots }}</div>
          <div class="card-label">飞书机器人</div>
        </div>
        <div class="card-trend">
          <span>+12%</span>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="card-icon">
          <el-icon :size="36" color="#67C23A"><Link /></el-icon>
        </div>
        <div class="card-info">
          <div class="card-value">{{ stats.difyApps }}</div>
          <div class="card-label">Dify 应用</div>
        </div>
        <div class="card-trend">
          <span>+8%</span>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="card-icon">
          <el-icon :size="36" color="#E6A23C"><Share /></el-icon>
        </div>
        <div class="card-info">
          <div class="card-value">{{ stats.bindings }}</div>
          <div class="card-label">绑定关系</div>
        </div>
        <div class="card-trend">
          <span>+25%</span>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="card-icon">
          <el-icon :size="36" color="#909399"><Document /></el-icon>
        </div>
        <div class="card-info">
          <div class="card-value">{{ stats.todayMessages }}</div>
          <div class="card-label">今日消息</div>
        </div>
        <div class="card-trend">
          <span>+5.2%</span>
        </div>
      </el-card>
    </div>

    <div class="content-section">
      <el-row :gutter="20">
        <el-col :span="16">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>消息趋势 (最近7天)</span>
                <el-button type="primary" size="small" @click="refreshStats">刷新</el-button>
              </div>
            </template>
            <div class="chart-placeholder">
              <div class="chart-trend">
                <div class="trend-line"></div>
              </div>
              <p class="chart-note">需要集成图表库（如 ECharts）显示详细数据</p>
            </div>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card class="chart-card">
            <template #header>
              <span>消息状态分布</span>
            </template>
            <div class="chart-placeholder">
              <div class="pie-chart">
                <div class="pie-success"></div>
                <div class="pie-failed"></div>
              </div>
              <div class="pie-legend">
                <div class="legend-item">
                  <span class="legend-color success"></span>
                  <span>成功 95%</span>
                </div>
                <div class="legend-item">
                  <span class="legend-color failed"></span>
                  <span>失败 5%</span>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="content-section">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>最近消息日志</span>
            <el-button type="text" @click="goToLogs">查看全部 →</el-button>
          </div>
        </template>
        <el-table :data="recentLogs" style="width: 100%">
          <el-table-column prop="time" label="时间" width="140">
            <template #default="{ row }">
              <span class="time-text">{{ formatTime(row.time) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="bot" label="机器人" width="120" />
          <el-table-column prop="difyApp" label="Dify 应用" width="140" />
          <el-table-column prop="user" label="用户" width="100" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag v-if="row.status === 'success'" type="success" size="small">✅</el-tag>
              <el-tag v-else type="danger" size="small">❌</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ChatDotRound, Link, Share, Document } from '@element-plus/icons-vue'

const router = useRouter()

// 统计数据
const stats = ref({
  bots: 12,
  difyApps: 8,
  bindings: 25,
  todayMessages: 1234
})

// 最近日志
const recentLogs = ref([
  { time: '2024-01-15 10:23:15', bot: '客服助手', difyApp: 'AI问答助手', user: '张三', status: 'success' },
  { time: '2024-01-15 10:20:42', bot: '技术支持', difyApp: '技术文档', user: '李四', status: 'success' },
  { time: '2024-01-15 10:15:08', bot: '客服助手', difyApp: 'AI问答助手', user: '王五', status: 'failed' },
  { time: '2024-01-15 10:10:33', bot: '产品咨询', difyApp: '产品介绍', user: '赵六', status: 'success' },
  { time: '2024-01-15 10:05:21', bot: '客服助手', difyApp: 'AI问答助手', user: '钱七', status: 'success' }
])

const refreshStats = () => {
  // TODO: 调用 API 刷新统计数据
  console.log('刷新统计数据')
}

const goToLogs = () => {
  router.push('/logs')
}

const formatTime = (time: string) => {
  return time
}
</script>

<style scoped>
.dashboard-container {
  padding: 0;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  border: none;
  cursor: pointer;
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.card-icon {
  float: left;
  margin-right: 12px;
}

.card-info {
  overflow: hidden;
}

.card-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1;
}

.card-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.card-trend {
  float: right;
  color: #67C23A;
  font-size: 12px;
}

.content-section {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-card :deep(.el-card__header) {
  padding: 16px 20px;
}

.chart-placeholder {
  min-height: 300px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
}

.chart-trend {
  width: 100%;
  height: 200px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px;
  position: relative;
  margin-bottom: 16px;
}

.trend-line {
  position: absolute;
  bottom: 20px;
  left: 40px;
  width: calc(100% - 80px);
  height: 100px;
  background: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="100%" height="100%" viewBox="0 0 100 100"><path d="M0,80 L20,60 L40,70 L60,40 L80,60 L100,50" stroke="white" stroke-width="4" fill="none" stroke-linecap="round" stroke-linejoin="round"/></svg>');
  background-size: 100% 100%;
  background-repeat: no-repeat;
}

.chart-note {
  font-size: 13px;
  color: #909399;
}

.pie-chart {
  width: 200px;
  height: 200px;
  margin: 0 auto 20px;
  position: relative;
}

.pie-success {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: conic-gradient(#67C23A 0deg 342deg, #F56C6C 342deg 360deg);
}

.pie-failed {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 140px;
  height: 140px;
  background: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #909399;
}

.pie-legend {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #606266;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.legend-color.success {
  background: #67C23A;
}

.legend-color.failed {
  background: #F56C6C;
}

.time-text {
  font-size: 13px;
  color: #606266;
}
</style>
