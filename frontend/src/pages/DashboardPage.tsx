import { useEffect, useState } from 'react'
import { Row, Col, Card, Statistic, Table, Empty, Spin, Space, Progress, Tag, Timeline } from 'antd'
import { RobotOutlined, AppstoreOutlined, MessageOutlined, CheckCircleOutlined, ExclamationOutlined } from '@ant-design/icons'
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, AreaChart, Area } from 'recharts'
import { mockApi } from '../api/mock'
import { MessageLogItem } from '../types'

// 模拟的趋势数据
const trendData = [
  { time: '00:00', messages: 12, success: 11, error: 1 },
  { time: '04:00', messages: 18, success: 17, error: 1 },
  { time: '08:00', messages: 35, success: 33, error: 2 },
  { time: '12:00', messages: 52, success: 50, error: 2 },
  { time: '16:00', messages: 48, success: 46, error: 2 },
  { time: '20:00', messages: 42, success: 40, error: 2 },
  { time: '23:59', messages: 28, success: 26, error: 2 },
]

export default function DashboardPage() {
  const [loading, setLoading] = useState(false)
  const [stats, setStats] = useState({ botsCount: 0, appsCount: 0, todayMessages: 0, availability: 100 })
  const [logs, setLogs] = useState<MessageLogItem[]>([])

  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    setLoading(true)
    try {
      const [statsRes, logsRes] = await Promise.all([mockApi.getStats(), mockApi.getLogs({ pageNo: 1, pageSize: 5 })])
      setStats(statsRes.data?.data)
      setLogs(logsRes.data?.data?.items || [])
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const columns = [
    {
      title: '时间',
      dataIndex: 'createdAt',
      key: 'time',
      width: 150,
      render: (t: string) => new Date(t).toLocaleString(),
    },
    {
      title: '机器人',
      dataIndex: 'botAccountId',
      key: 'bot',
      width: 100,
      render: (id: string) => <Tag color="blue">{id}</Tag>,
    },
    {
      title: '消息内容',
      dataIndex: 'messageContent',
      key: 'msg',
      width: 300,
      render: (text: string) => <span title={text}>{text?.substring(0, 60)}...</span>,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status: string) => (
        <Tag color={status === 'success' ? 'green' : 'red'}>
          {status === 'success' ? '✓ 成功' : '✗ 失败'}
        </Tag>
      ),
    },
  ]

  return (
    <Spin spinning={loading}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        {/* 统计卡片区域 */}
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={12} lg={6}>
            <Card
              hoverable
              style={{
                background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                color: 'white',
                borderRadius: '12px',
                boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)',
              }}
            >
              <Statistic
                title={<span style={{ color: 'rgba(255,255,255,0.8)', fontSize: '12px' }}>机器人总数</span>}
                value={stats.botsCount}
                prefix={<RobotOutlined style={{ marginRight: '8px' }} />}
                valueStyle={{ color: 'white', fontSize: '28px', fontWeight: 'bold' }}
              />
            </Card>
          </Col>
          <Col xs={24} sm={12} lg={6}>
            <Card
              hoverable
              style={{
                background: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
                color: 'white',
                borderRadius: '12px',
                boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)',
              }}
            >
              <Statistic
                title={<span style={{ color: 'rgba(255,255,255,0.8)', fontSize: '12px' }}>应用总数</span>}
                value={stats.appsCount}
                prefix={<AppstoreOutlined style={{ marginRight: '8px' }} />}
                valueStyle={{ color: 'white', fontSize: '28px', fontWeight: 'bold' }}
              />
            </Card>
          </Col>
          <Col xs={24} sm={12} lg={6}>
            <Card
              hoverable
              style={{
                background: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
                color: 'white',
                borderRadius: '12px',
                boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)',
              }}
            >
              <Statistic
                title={<span style={{ color: 'rgba(255,255,255,0.8)', fontSize: '12px' }}>今日消息数</span>}
                value={stats.todayMessages}
                prefix={<MessageOutlined style={{ marginRight: '8px' }} />}
                valueStyle={{ color: 'white', fontSize: '28px', fontWeight: 'bold' }}
              />
            </Card>
          </Col>
          <Col xs={24} sm={12} lg={6}>
            <Card
              hoverable
              style={{
                background: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
                color: 'white',
                borderRadius: '12px',
                boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)',
              }}
            >
              <Statistic
                title={<span style={{ color: 'rgba(255,255,255,0.8)', fontSize: '12px' }}>可用性</span>}
                value={stats.availability}
                suffix="%"
                prefix={<CheckCircleOutlined style={{ marginRight: '8px' }} />}
                valueStyle={{ color: 'white', fontSize: '28px', fontWeight: 'bold' }}
              />
            </Card>
          </Col>
        </Row>

        {/* 消息趋势曲线图 */}
        <Card
          title="消息处理趋势（24小时）"
          style={{ borderRadius: '12px', boxShadow: '0 2px 8px rgba(0, 0, 0, 0.08)' }}
        >
          <ResponsiveContainer width="100%" height={300}>
            <AreaChart data={trendData}>
              <defs>
                <linearGradient id="colorMessages" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#667eea" stopOpacity={0.8} />
                  <stop offset="95%" stopColor="#667eea" stopOpacity={0} />
                </linearGradient>
                <linearGradient id="colorSuccess" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#52c41a" stopOpacity={0.8} />
                  <stop offset="95%" stopColor="#52c41a" stopOpacity={0} />
                </linearGradient>
                <linearGradient id="colorError" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#f5222d" stopOpacity={0.8} />
                  <stop offset="95%" stopColor="#f5222d" stopOpacity={0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
              <XAxis dataKey="time" stroke="#999" />
              <YAxis stroke="#999" />
              <Tooltip
                contentStyle={{
                  background: 'rgba(0,0,0,0.8)',
                  border: 'none',
                  borderRadius: '8px',
                  color: '#fff',
                }}
                cursor={{ strokeDasharray: '3 3' }}
              />
              <Legend />
              <Area
                type="monotone"
                dataKey="messages"
                stroke="#667eea"
                strokeWidth={2}
                fillOpacity={1}
                fill="url(#colorMessages)"
                name="总消息数"
              />
              <Area
                type="monotone"
                dataKey="success"
                stroke="#52c41a"
                strokeWidth={2}
                fillOpacity={1}
                fill="url(#colorSuccess)"
                name="成功"
              />
              <Area
                type="monotone"
                dataKey="error"
                stroke="#f5222d"
                strokeWidth={2}
                fillOpacity={1}
                fill="url(#colorError)"
                name="失败"
              />
            </AreaChart>
          </ResponsiveContainer>
        </Card>

        {/* 健康度展示 */}
        <Row gutter={[16, 16]}>
          <Col xs={24} lg={12}>
            <Card
              title="系统健康度"
              extra={<CheckCircleOutlined style={{ color: '#52c41a' }} />}
              style={{ borderRadius: '12px', boxShadow: '0 2px 8px rgba(0, 0, 0, 0.08)' }}
            >
              <Space direction="vertical" style={{ width: '100%' }} size="large">
                <div>
                  <div style={{ marginBottom: '8px', fontSize: '12px', color: '#666' }}>
                    连接成功率 <span style={{ float: 'right', fontWeight: 'bold' }}>96%</span>
                  </div>
                  <Progress percent={96} size="small" status="success" />
                </div>
                <div>
                  <div style={{ marginBottom: '8px', fontSize: '12px', color: '#666' }}>
                    消息处理成功率 <span style={{ float: 'right', fontWeight: 'bold' }}>98.5%</span>
                  </div>
                  <Progress percent={98.5} size="small" status="success" />
                </div>
                <div>
                  <div style={{ marginBottom: '8px', fontSize: '12px', color: '#666' }}>
                    API 可用性 <span style={{ float: 'right', fontWeight: 'bold' }}>99.2%</span>
                  </div>
                  <Progress percent={99.2} size="small" status="success" />
                </div>
              </Space>
            </Card>
          </Col>

          <Col xs={24} lg={12}>
            <Card
              title="最近告警"
              extra={<ExclamationOutlined style={{ color: '#faad14' }} />}
              style={{ borderRadius: '12px', boxShadow: '0 2px 8px rgba(0, 0, 0, 0.08)' }}
            >
              <Timeline
                items={[
                  {
                    children: <span style={{ fontSize: '12px' }}>Bot-002 连接状态异常，已自动重连</span>,
                    color: 'orange',
                  },
                  {
                    children: <span style={{ fontSize: '12px' }}>消息处理延迟超过 3 秒，3 次</span>,
                    color: 'orange',
                  },
                  {
                    children: <span style={{ fontSize: '12px' }}>Dify 应用 App-003 已禁用</span>,
                    color: 'gray',
                  },
                ]}
              />
            </Card>
          </Col>
        </Row>

        {/* 最近消息表格 */}
        <Card
          title="最近消息"
          extra={<span style={{ fontSize: '12px', color: '#999' }}>共 {logs.length} 条记录</span>}
          style={{ borderRadius: '12px', boxShadow: '0 2px 8px rgba(0, 0, 0, 0.08)' }}
        >
          {logs.length > 0 ? (
            <Table
              columns={columns}
              dataSource={logs.map((l) => ({ ...l, key: l.id }))}
              pagination={false}
              size="small"
            />
          ) : (
            <Empty />
          )}
        </Card>
      </Space>
    </Spin>
  )
}
