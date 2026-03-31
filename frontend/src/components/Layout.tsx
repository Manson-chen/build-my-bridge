import { useState } from 'react'
import { Outlet, useNavigate } from 'react-router-dom'
import { Layout as AntLayout, Menu, Button, Dropdown, Avatar, Space } from 'antd'
import { MenuFoldOutlined, MenuUnfoldOutlined, BgColorsOutlined, LogoutOutlined } from '@ant-design/icons'
import { useAuthStore } from '../store/auth'
import { useThemeStore } from '../store/theme'
import { BuildMyBridgeLogo } from './Logo'
import { DashboardIcon, RobotIcon, AppIcon, MessageIcon } from './MenuIcons'

const { Header, Sider, Content } = AntLayout

export default function Layout() {
  const [collapsed, setCollapsed] = useState(false)
  const navigate = useNavigate()
  const { user, logout } = useAuthStore()
  const { themeMode, toggleTheme } = useThemeStore()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const menuItems = [
    {
      key: '/',
      label: collapsed ? '' : <span style={{ marginLeft: '8px' }}>仪表盘</span>,
      icon: <DashboardIcon size={20} />,
      onClick: () => navigate('/'),
      title: '仪表盘',
    },
    {
      key: '/bots',
      label: collapsed ? '' : <span style={{ marginLeft: '8px' }}>机器人管理</span>,
      icon: <RobotIcon size={20} />,
      onClick: () => navigate('/bots'),
      title: '机器人管理',
    },
    {
      key: '/apps',
      label: collapsed ? '' : <span style={{ marginLeft: '8px' }}>应用管理</span>,
      icon: <AppIcon size={20} />,
      onClick: () => navigate('/apps'),
      title: '应用管理',
    },
    {
      key: '/logs',
      label: collapsed ? '' : <span style={{ marginLeft: '8px' }}>消息日志</span>,
      icon: <MessageIcon size={20} />,
      onClick: () => navigate('/logs'),
      title: '消息日志',
    },
  ]

  return (
    <AntLayout style={{ minHeight: '100vh' }}>
      <Sider trigger={null} collapsible collapsed={collapsed} width={200} theme={themeMode}>
        <div
          style={{
            padding: '16px',
            textAlign: 'center',
            borderBottom: '1px solid #f0f0f0',
            marginBottom: '16px',
            overflow: 'hidden',
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px' }}>
            <div style={{ flexShrink: 0 }}>
              <BuildMyBridgeLogo size={32} />
            </div>
            <span style={{ color: '#1890ff', fontSize: '14px', fontWeight: 'bold', lineHeight: '32px', minWidth: collapsed ? '24px' : 'auto' }}>
              {collapsed ? 'BMB' : 'BuildMyBridge'}
            </span>
          </div>
        </div>
        <Menu theme={themeMode} mode="inline" items={menuItems} defaultSelectedKeys={['/']} />
      </Sider>

      <AntLayout>
        <Header
          style={{
            padding: '0 24px',
            background: themeMode === 'dark' ? '#141414' : '#fff',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            boxShadow: '0 2px 8px rgba(0,0,0,0.06)',
          }}
        >
          <Button
            type="text"
            icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
            onClick={() => setCollapsed(!collapsed)}
            style={{ fontSize: '16px' }}
          />

          <Space>
            <Button type="text" icon={<BgColorsOutlined />} onClick={toggleTheme} />
            <Dropdown
              menu={{
                items: [
                  { key: 'logout', label: '退出', icon: <LogoutOutlined />, onClick: handleLogout },
                ],
              }}
            >
              <Avatar style={{ cursor: 'pointer', background: '#1890ff' }}>
                {user?.username?.charAt(0) || 'U'}
              </Avatar>
            </Dropdown>
          </Space>
        </Header>

        <Content
          style={{
            margin: '24px',
            padding: '24px',
            background: themeMode === 'dark' ? '#141414' : '#fafafa',
            borderRadius: '8px',
          }}
        >
          <Outlet />
        </Content>
      </AntLayout>
    </AntLayout>
  )
}
