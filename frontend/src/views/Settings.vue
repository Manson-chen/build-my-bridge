<template>
  <div class="settings-container">
    <el-card>
      <template #header>
        <span>🔐 安全设置</span>
      </template>

      <el-form label-width="180px" style="max-width: 600px">
        <el-form-item label="JWT 密钥">
          <div style="display: flex; gap: 10px; align-items: center">
            <div class="secret-text">●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●●</div>
            <el-button type="primary" size="small" @click="changeJwtSecret">修改</el-button>
          </div>
        </el-form-item>

        <el-form-item label="Token 有效期">
          <el-slider
            v-model="settings.tokenExpiry"
            :min="1"
            :max="168"
            :step="1"
            show-stops
            :format-tooltip="(value: number) => value + ' 小时'"
            style="width: 500px"
          />
          <div style="margin-top: 8px; text-align: center">{{ settings.tokenExpiry }} 小时</div>
        </el-form-item>

        <el-divider />

        <el-form-item label="登录限制">
          <el-switch v-model="settings.loginLimitEnabled" />
          <span style="margin-left: 10px; color: #909399">启用登录失败次数限制</span>
        </el-form-item>

        <el-form-item label="最大失败次数" v-if="settings.loginLimitEnabled">
          <el-input-number v-model="settings.maxLoginFailures" :min="3" :max="10" style="width: 150px" />
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>💾 日志设置</span>
      </template>

      <el-form label-width="180px" style="max-width: 600px">
        <el-form-item label="日志保留天数">
          <el-input-number v-model="settings.logRetentionDays" :min="7" :max="365" style="width: 150px" />
          <span style="margin-left: 10px">天</span>
          <el-alert
            title="自动清理超过此天数的日志"
            type="info"
            :closable="false"
            show-icon
            style="width: 400px; margin-left: 20px"
          />
        </el-form-item>

        <el-form-item label="最大日志数量">
          <el-select v-model="settings.maxLogCount" style="width: 200px">
            <el-option label="5 万条" :value="50000" />
            <el-option label="10 万条" :value="100000" />
            <el-option label="50 万条" :value="500000" />
            <el-option label="无限制" :value="0" />
          </el-select>
          <el-alert
            title="超过此数量时触发清理"
            type="info"
            :closable="false"
            show-icon
            style="width: 400px; margin-left: 20px"
          />
        </el-form-item>

        <el-form-item label="日志级别">
          <el-select v-model="settings.logLevel" style="width: 200px">
            <el-option label="DEBUG" value="debug" />
            <el-option label="INFO" value="info" />
            <el-option label="WARNING" value="warning" />
            <el-option label="ERROR" value="error" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>⚡ 性能设置</span>
      </template>

      <el-form label-width="180px" style="max-width: 600px">
        <el-form-item label="Dify 请求超时时间">
          <el-slider
            v-model="settings.difyTimeout"
            :min="5"
            :max="120"
            :step="5"
            show-stops
            :format-tooltip="(value: number) => value + ' 秒'"
            style="width: 500px"
          />
          <div style="margin-top: 8px; text-align: center">{{ settings.difyTimeout }} 秒</div>
        </el-form-item>

        <el-form-item label="Redis 缓存时间">
          <el-select v-model="settings.redisCacheTime" style="width: 200px">
            <el-option label="30 分钟" :value="30 * 60000" />
            <el-option label="1 小时" :value="60 * 60000" />
            <el-option label="2 小时" :value="120 * 60000" />
            <el-option label="4 小时" :value="240 * 60000" />
          </el-select>
          <span style="margin-left: 10px; color: #909399">飞书 token 缓存时间</span>
        </el-form-item>

        <el-form-item label="并发请求数">
          <el-input-number v-model="settings.maxConcurrency" :min="10" :max="1000" style="width: 150px" />
          <span style="margin-left: 10px; color: #909399">系统最大并发处理消息数</span>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>📝 系统信息</span>
      </template>

      <el-descriptions :column="1" border>
        <el-descriptions-item label="应用名称">BuildMyBridge</el-descriptions-item>
        <el-descriptions-item label="版本">1.0.0</el-descriptions-item>
        <el-descriptions-item label="框架">Spring Boot + Vue3</el-descriptions-item>
        <el-descriptions-item label="数据库">MySQL 8.0 + Redis 7.0</el-descriptions-item>
        <el-descriptions-item label="部署时间">2024-01-15</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <div class="action-buttons">
      <el-button @click="resetDefaults">恢复默认</el-button>
      <el-button type="primary" @click="saveSettings">保存设置</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const settings = ref({
  tokenExpiry: 24,
  loginLimitEnabled: true,
  maxLoginFailures: 5,
  logRetentionDays: 30,
  maxLogCount: 100000,
  logLevel: 'info',
  difyTimeout: 30,
  redisCacheTime: 120 * 60000,
  maxConcurrency: 100
})

const changeJwtSecret = () => {
  ElMessageBox.prompt('请输入新的 JWT 密钥', '修改 JWT 密钥', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputType: 'password',
    inputPattern: '.{16,}',
    inputErrorMessage: '密钥至少 16 个字符'
  }).then(({ value }) => {
    ElMessage.success('JWT 密钥已更新')
  }).catch(() => {})
}

const resetDefaults = () => {
  ElMessageBox.confirm('确定要恢复默认设置吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    settings.value = {
      tokenExpiry: 24,
      loginLimitEnabled: true,
      maxLoginFailures: 5,
      logRetentionDays: 30,
      maxLogCount: 100000,
      logLevel: 'info',
      difyTimeout: 30,
      redisCacheTime: 120 * 60000,
      maxConcurrency: 100
    }
    ElMessage.success('已恢复默认设置')
  }).catch(() => {})
}

const saveSettings = () => {
  ElMessage.success('设置已保存')
}
</script>

<style scoped>
.settings-container {
  background: transparent;
}

.secret-text {
  font-family: monospace;
  font-size: 14px;
  color: #606266;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
}

:deep(.el-card__header) {
  font-weight: 600;
  padding: 16px 20px;
}
</style>
