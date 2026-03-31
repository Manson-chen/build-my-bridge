import React from 'react'

interface MenuIconProps {
  size?: number
  style?: React.CSSProperties
}

// 仪表盘 Icon - 监控屏幕/数据展示
export const DashboardIcon: React.FC<MenuIconProps> = ({ size = 24, style }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" style={style}>
    {/* 屏幕框架 */}
    <rect x="2" y="3" width="20" height="14" rx="1.5" stroke="currentColor" strokeWidth="1.5" />
    {/* 屏幕底部 */}
    <rect x="10" y="17" width="4" height="1" rx="0.5" fill="currentColor" />
    {/* 屏幕支架 */}
    <line x1="11" y1="18" x2="9" y2="21" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
    <line x1="13" y1="18" x2="15" y2="21" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
    {/* 内部曲线 - 向上的趋势 */}
    <path d="M5 15C7 13 9 11 11 10C13 9 15 9 17 8C18 7.5 19 7.5 19 8" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" fill="none" />
  </svg>
)

// 机器人管理 Icon - 简化专业风格
export const RobotIcon: React.FC<MenuIconProps> = ({ size = 24, style }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" style={style}>
    {/* 头部 */}
    <rect x="6" y="2" width="12" height="9" rx="1.5" stroke="currentColor" strokeWidth="1.5" />
    {/* 左眼 */}
    <circle cx="9.5" cy="5.5" r="1" fill="currentColor" />
    {/* 右眼 */}
    <circle cx="14.5" cy="5.5" r="1" fill="currentColor" />
    {/* 中心嘴线 */}
    <line x1="9.5" y1="8.5" x2="14.5" y2="8.5" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
    {/* 身体 */}
    <rect x="8" y="11" width="8" height="7" rx="1" stroke="currentColor" strokeWidth="1.5" />
    {/* 左臂 */}
    <line x1="8" y1="13.5" x2="2" y2="13.5" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
    {/* 右臂 */}
    <line x1="16" y1="13.5" x2="22" y2="13.5" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
    {/* 左腿 */}
    <line x1="10.5" y1="18" x2="10.5" y2="22" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
    {/* 右腿 */}
    <line x1="13.5" y1="18" x2="13.5" y2="22" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
  </svg>
)

// 应用管理 Icon - 方形网格
export const AppIcon: React.FC<MenuIconProps> = ({ size = 24, style }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" style={style}>
    {/* 左上 */}
    <rect x="2" y="2" width="8" height="8" rx="1" stroke="currentColor" strokeWidth="1.5" />
    {/* 右上 */}
    <rect x="14" y="2" width="8" height="8" rx="1" stroke="currentColor" strokeWidth="1.5" />
    {/* 左下 */}
    <rect x="2" y="14" width="8" height="8" rx="1" stroke="currentColor" strokeWidth="1.5" />
    {/* 右下 */}
    <rect x="14" y="14" width="8" height="8" rx="1" stroke="currentColor" strokeWidth="1.5" />
  </svg>
)

// 消息日志 Icon - 文本和消息
export const MessageIcon: React.FC<MenuIconProps> = ({ size = 24, style }) => (
  <svg width={size} height={size} viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" style={style}>
    {/* 对话框 */}
    <path d="M3 11C3 6.58172 6.58172 3 12 3C17.4183 3 21 6.58172 21 12C21 17.4183 17.4183 21 12 21H6L2 23V11Z" stroke="currentColor" strokeWidth="1.5" />
    {/* 消息行 1 */}
    <line x1="7" y1="9" x2="17" y2="9" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
    {/* 消息行 2 */}
    <line x1="7" y1="13" x2="17" y2="13" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
    {/* 消息行 3 */}
    <line x1="7" y1="17" x2="13" y2="17" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
  </svg>
)
