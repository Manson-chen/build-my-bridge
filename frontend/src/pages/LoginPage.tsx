import { useState, useEffect } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { Card, Button, Spin, Alert, Space, Typography } from 'antd'
import { LoginOutlined } from '@ant-design/icons'
import { authApi } from '../api'
import { useAuthStore } from '../store/auth'
import { useThemeStore } from '../store/theme'

const { Title, Paragraph } = Typography

export default function LoginPage() {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const { setUser, setToken } = useAuthStore()
  const { themeMode } = useThemeStore()
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const code = searchParams.get('code')
    if (code) {
      handleCallback(code)
    }
  }, [searchParams])

  const handleCallback = async (code: string) => {
    setLoading(true)
    try {
      const res = await authApi.callback(code)
      const { token, user } = res.data?.data || {}
      if (token && user) {
        setToken(token)
        setUser(user)
        navigate('/')
      }
    } catch (err: any) {
      setError(err.response?.data?.message || '登录失败')
    } finally {
      setLoading(false)
    }
  }

  const handleLogin = async () => {
    setLoading(true)
    try {
      const res = await authApi.getLoginUrl()
      const url = res.data?.data?.url
      if (url) window.location.href = url
    } catch (err: any) {
      setError(err.response?.data?.message || '获取登录链接失败')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div
      style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        background: themeMode === 'dark' ? '#141414' : 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      }}
    >
      <Card style={{ width: '100%', maxWidth: '400px' }}>
        <Space direction="vertical" size="large" style={{ width: '100%' }}>
          <div style={{ textAlign: 'center' }}>
            <Title level={2} style={{ margin: '0 0 8px 0' }}>
              BuildMyBridge
            </Title>
            <Paragraph style={{ margin: 0, color: '#8c8c8c' }}>
              飞书机器人与 Dify 集成平台
            </Paragraph>
          </div>

          {error && <Alert message={error} type="error" showIcon closable />}

          <Button type="primary" size="large" icon={<LoginOutlined />} loading={loading} onClick={handleLogin} block>
            使用飞书登录
          </Button>

          <Paragraph style={{ textAlign: 'center', margin: 0, fontSize: '12px', color: '#8c8c8c' }}>
            第一次登录将自动创建账户
          </Paragraph>
        </Space>
      </Card>
    </div>
  )
}
