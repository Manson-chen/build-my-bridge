import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import { ConfigProvider, theme } from 'antd'
import zhCN from 'antd/locale/zh_CN'
import { useThemeStore } from './store/theme'
import Layout from './components/Layout'
import DashboardPage from './pages/DashboardPage'
import BotManagePage from './pages/BotManagePage'
import AppManagePage from './pages/AppManagePage'
import MessageLogPage from './pages/MessageLogPage'

function App() {
  const { themeMode } = useThemeStore()

  const antdTheme = {
    algorithm: themeMode === 'dark' ? theme.darkAlgorithm : theme.defaultAlgorithm,
    token: {
      colorPrimary: '#1890ff',
      borderRadius: 6,
    },
  }

  return (
    <ConfigProvider theme={antdTheme} locale={zhCN}>
      <Router>
        <Routes>
          {/* 主布局 - 所有页面 */}
          <Route element={<Layout />}>
            <Route index element={<DashboardPage />} />
            <Route path="bots" element={<BotManagePage />} />
            <Route path="apps" element={<AppManagePage />} />
            <Route path="logs" element={<MessageLogPage />} />
          </Route>

          {/* 重定向默认路由 */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Router>
    </ConfigProvider>
  )
}

export default App

