import React from 'react'

interface LogoProps {
  size?: number
  style?: React.CSSProperties
}

export const BuildMyBridgeLogo: React.FC<LogoProps> = ({ size = 48, style }) => {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 48 48"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      style={style}
    >
      <defs>
        {/* 上面蓝色渐变 */}
        <linearGradient id="topGradient" x1="0%" y1="0%" x2="100%" y2="0%">
          <stop offset="0%" style={{ stopColor: '#667eea', stopOpacity: 1 }} />
          <stop offset="100%" style={{ stopColor: '#764ba2', stopOpacity: 1 }} />
        </linearGradient>
        {/* 下面粉红渐变 */}
        <linearGradient id="bottomGradient" x1="0%" y1="0%" x2="100%" y2="0%">
          <stop offset="0%" style={{ stopColor: '#f093fb', stopOpacity: 1 }} />
          <stop offset="100%" style={{ stopColor: '#f5576c', stopOpacity: 1 }} />
        </linearGradient>
      </defs>

      {/* 上面的圆角长方形 */}
      <rect
        x="4"
        y="8"
        width="28"
        height="16"
        rx="2.5"
        ry="2.5"
        stroke="url(#topGradient)"
        strokeWidth="2"
        fill="none"
      />

      {/* 下面的圆角长方形 - 向右错开，与上面重叠更多 */}
      <rect
        x="16"
        y="16"
        width="28"
        height="16"
        rx="2.5"
        ry="2.5"
        stroke="url(#bottomGradient)"
        strokeWidth="2"
        fill="none"
      />
    </svg>
  )
}

export default BuildMyBridgeLogo






