<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <el-icon :size="48" color="#409EFF"><Connection /></el-icon>
        <h1 class="title">BuildMyBridge</h1>
        <p class="subtitle">飞书-Dify 集成平台</p>
      </div>

      <el-divider />

      <div class="login-form">
        <el-button type="primary" size="large" class="feishu-login-btn" @click="handleLogin">
          <el-icon :size="20"><ChatDotRound /></el-icon>
          <span>使用飞书扫码登录</span>
        </el-button>

        <div class="divider">
          <span>或者</span>
        </div>

        <el-button type="info" plain size="large" class="password-login-btn" @click="showPasswordLogin = true">
          <span>使用飞书账号密码登录</span>
        </el-button>
      </div>

      <div class="footer">
        还没有飞书账号？<a href="https://www.feishu.cn" target="_blank">立即注册</a>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Connection, ChatDotRound } from '@element-plus/icons-vue'

const router = useRouter()
const showPasswordLogin = ref(false)

const handleLogin = () => {
  // 跳转到后端 OAuth 接口
  const clientId = import.meta.env.VITE_FEISHU_APP_ID || 'cli_xxxxxx'
  const redirectUri = import.meta.env.VITE_FEISHU_REDIRECT_URI || window.location.origin + '/auth/callback'

  const authUrl = `https://open.feishu.cn/open-apis/authen/v1/index?app_id=${clientId}&redirect_uri=${encodeURIComponent(redirectUri)}`

  window.location.href = authUrl
}

// 模拟密码登录（实际应该调用后端接口）
const handlePasswordLogin = () => {
  // TODO: 调用后端登录接口
  router.push('/dashboard')
}
</script>

<style scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 420px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  padding: 40px 40px;
}

.login-header {
  text-align: center;
  margin-bottom: 20px;
}

.title {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin: 20px 0 10px;
}

.subtitle {
  font-size: 14px;
  color: #909399;
}

.login-form {
  margin-top: 30px;
}

.feishu-login-btn {
  width: 100%;
  background: linear-gradient(135deg, #1890ff 0%, #409EFF 100%);
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  height: 50px;
}

.divider {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 24px 0;
  color: #c0c4cc;
  font-size: 14px;
}

.divider::before,
.divider::after {
  content: '';
  display: inline-block;
  width: 80px;
  height: 1px;
  background: #e4e7ed;
  margin: 0 16px;
}

.password-login-btn {
  width: 100%;
  height: 48px;
}

.footer {
  margin-top: 30px;
  text-align: center;
  color: #606266;
  font-size: 13px;
}

.footer a {
  color: #409EFF;
  text-decoration: none;
}

.footer a:hover {
  text-decoration: underline;
}
</style>
