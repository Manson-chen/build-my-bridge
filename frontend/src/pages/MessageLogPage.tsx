import { useState, useEffect } from 'react'
import { Table, Button, Space, Modal, Input, Select, Card, Badge, Row, Col, Tag, Drawer, Divider } from 'antd'
import { EyeOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { mockApi } from '../api/mock'
import { MessageLogItem } from '../types'

export default function MessageLogPage() {
  const [logs, setLogs] = useState<MessageLogItem[]>([])
  const [loading, setLoading] = useState(false)
  const [drawerVisible, setDrawerVisible] = useState(false)
  const [selectedLog, setSelectedLog] = useState<MessageLogItem | null>(null)
  const [filters, setFilters] = useState({ pageNo: 1, pageSize: 10 })
  const [total, setTotal] = useState(0)

  useEffect(() => {
    loadLogs()
  }, [])

  const loadLogs = async () => {
    setLoading(true)
    try {
      const res = await mockApi.getLogs(filters)
      setLogs(res.data?.data?.items || [])
      setTotal(res.data?.data?.total || 0)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const handleViewDetail = (log: MessageLogItem) => {
    setSelectedLog(log)
    setDrawerVisible(true)
  }

  const columns = [
    {
      title: '时间',
      dataIndex: 'createdAt',
      key: 'time',
      width: 150,
      render: (date: string) => dayjs(date).format('YYYY-MM-DD HH:mm:ss'),
    },
    {
      title: '机器人ID',
      dataIndex: 'botAccountId',
      key: 'bot',
      width: 100,
      render: (id: string) => <Tag color="blue">{id}</Tag>,
    },
    {
      title: '消息内容',
      dataIndex: 'messageContent',
      key: 'msg',
      width: 250,
      render: (text: string) => <span title={text}>{text?.substring(0, 60)}...</span>,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status: string) => (
        <Badge status={status === 'success' ? 'success' : 'error'} text={status === 'success' ? '✓ 成功' : '✗ 失败'} />
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 80,
      render: (_: any, record: MessageLogItem) => (
        <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => handleViewDetail(record)}>
          详情
        </Button>
      ),
    },
  ]

  const stats = {
    total: total,
    success: logs.filter((l) => l.status === 'success').length,
    error: logs.filter((l) => l.status === 'error').length,
    successRate: logs.length > 0 ? ((logs.filter((l) => l.status === 'success').length / logs.length) * 100).toFixed(1) : 0,
  }

  return (
    <>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        {/* 标题 */}
        <h2 style={{ margin: 0 }}>消息日志</h2>

        {/* 统计卡片 */}
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={6}>
            <Card style={{ textAlign: 'center', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }}>
              <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#1890ff' }}>{stats.total}</div>
              <div style={{ color: '#999', marginTop: '8px', fontSize: '12px' }}>消息总数</div>
            </Card>
          </Col>
          <Col xs={24} sm={6}>
            <Card style={{ textAlign: 'center', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }}>
              <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#52c41a' }}>{stats.success}</div>
              <div style={{ color: '#999', marginTop: '8px', fontSize: '12px' }}>成功</div>
            </Card>
          </Col>
          <Col xs={24} sm={6}>
            <Card style={{ textAlign: 'center', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }}>
              <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#f5222d' }}>{stats.error}</div>
              <div style={{ color: '#999', marginTop: '8px', fontSize: '12px' }}>失败</div>
            </Card>
          </Col>
          <Col xs={24} sm={6}>
            <Card style={{ textAlign: 'center', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }}>
              <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#faad14' }}>{stats.successRate}%</div>
              <div style={{ color: '#999', marginTop: '8px', fontSize: '12px' }}>成功率</div>
            </Card>
          </Col>
        </Row>

        {/* 筛选区域 */}
        <Card style={{ borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }}>
          <Space wrap>
            <Input.Search placeholder="搜索机器人ID" style={{ width: 150 }} onSearch={(v) => setFilters({ ...filters, pageNo: 1 })} />
            <Select style={{ width: 120 }} placeholder="状态" options={[{ label: '全部', value: '' }, { label: '成功', value: 'success' }, { label: '失败', value: 'error' }]} />
            <Button type="primary" onClick={loadLogs}>
              搜索
            </Button>
            <Button onClick={() => { setFilters({ pageNo: 1, pageSize: 10 }); loadLogs() }}>重置</Button>
          </Space>
        </Card>

        {/* 表格 */}
        <Card style={{ borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }}>
          <Table
            columns={columns}
            dataSource={logs.map((l) => ({ ...l, key: l.id }))}
            loading={loading}
            pagination={{
              current: filters.pageNo,
              pageSize: filters.pageSize,
              total: total,
              onChange: (pageNo) => setFilters({ ...filters, pageNo }),
            }}
            size="small"
            scroll={{ x: 800 }}
          />
        </Card>
      </Space>

      {/* 详情抽屉 */}
      <Drawer
        title="消息详情"
        placement="right"
        onClose={() => setDrawerVisible(false)}
        open={drawerVisible}
        width={600}
      >
        {selectedLog && (
          <Space direction="vertical" size="large" style={{ width: '100%' }}>
            <div>
              <div style={{ fontWeight: 'bold', marginBottom: '8px' }}>时间</div>
              <div style={{ color: '#666' }}>
                {dayjs(selectedLog.createdAt).format('YYYY-MM-DD HH:mm:ss')}
              </div>
            </div>

            <Divider />

            <div>
              <div style={{ fontWeight: 'bold', marginBottom: '8px' }}>机器人ID</div>
              <Tag color="blue">{selectedLog.botAccountId}</Tag>
            </div>

            <Divider />

            <div>
              <div style={{ fontWeight: 'bold', marginBottom: '8px' }}>应用ID</div>
              <Tag color="cyan">{selectedLog.aiAppId}</Tag>
            </div>

            <Divider />

            <div>
              <div style={{ fontWeight: 'bold', marginBottom: '8px' }}>状态</div>
              <Badge status={selectedLog.status === 'success' ? 'success' : 'error'} text={selectedLog.status === 'success' ? '✓ 成功' : '✗ 失败'} />
            </div>

            <Divider />

            <div>
              <div style={{ fontWeight: 'bold', marginBottom: '8px' }}>用户消息</div>
              <div
                style={{
                  background: '#fafafa',
                  padding: '12px',
                  borderRadius: '6px',
                  maxHeight: '300px',
                  overflow: 'auto',
                  whiteSpace: 'pre-wrap',
                  wordBreak: 'break-word',
                  lineHeight: '1.6',
                }}
              >
                {selectedLog.messageContent}
              </div>
            </div>

            <Divider />

            <div>
              <div style={{ fontWeight: 'bold', marginBottom: '8px' }}>AI 回复</div>
              <div
                style={{
                  background: selectedLog.status === 'success' ? '#f6ffed' : '#fff1f0',
                  padding: '12px',
                  borderRadius: '6px',
                  maxHeight: '300px',
                  overflow: 'auto',
                  whiteSpace: 'pre-wrap',
                  wordBreak: 'break-word',
                  lineHeight: '1.6',
                  border: `1px solid ${selectedLog.status === 'success' ? '#b7eb8f' : '#ffccc7'}`,
                  color: selectedLog.status === 'success' ? '#389e0d' : '#d4380d',
                }}
              >
                {selectedLog.responseContent || (selectedLog.errorMessage ? `错误: ${selectedLog.errorMessage}` : '(无回复)')}
              </div>
            </div>
          </Space>
        )}
      </Drawer>
    </>
  )
}
