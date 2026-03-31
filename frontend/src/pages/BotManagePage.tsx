import { useState, useEffect } from 'react'
import { Table, Button, Space, Modal, Form, Input, Select, InputNumber, Switch, message, Badge, Card, Popover, Row, Col, Tag } from 'antd'
import { PlusOutlined, DeleteOutlined, EditOutlined, LinkOutlined } from '@ant-design/icons'
import MD5 from 'md5'
import { mockApi } from '../api/mock'
import { BotAccount, AiApp } from '../types'

const StatusColorMap = {
  INIT: 'default',
  CONNECTING: 'processing',
  CONNECTED: 'success',
  FAILED: 'error',
  DISCONNECTED: 'warning',
}

const StatusLabelMap = {
  INIT: '初始化',
  CONNECTING: '连接中',
  CONNECTED: '已连接',
  FAILED: '连接失败',
  DISCONNECTED: '已断开',
}

export default function BotManagePage() {
  const [bots, setBots] = useState<BotAccount[]>([])
  const [apps, setApps] = useState<AiApp[]>([])
  const [loading, setLoading] = useState(false)
  const [visible, setVisible] = useState(false)
  const [editingBot, setEditingBot] = useState<BotAccount | null>(null)
  const [form] = Form.useForm()

  useEffect(() => {
    loadData()
    const interval = setInterval(loadData, 2000)
    return () => clearInterval(interval)
  }, [])

  const loadData = async () => {
    try {
      const [botsRes, appsRes] = await Promise.all([mockApi.getBots(), mockApi.getApps()])
      setBots(botsRes.data?.data || [])
      setApps(appsRes.data?.data || [])
    } catch (err) {
      console.error(err)
    }
  }

  const handleSubmit = async (values: any) => {
    setLoading(true)
    try {
      message.success('操作成功')
      setVisible(false)
      form.resetFields()
      setTimeout(loadData, 500)
    } catch {
      message.error('操作失败')
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = (id: string) => {
    Modal.confirm({
      title: '删除机器人',
      content: '确定要删除这个机器人吗？删除后无法恢复。',
      okText: '删除',
      okType: 'danger',
      cancelText: '取消',
      onOk: () => {
        message.success('删除成功')
      },
    })
  }

  const columns = [
    {
      title: '机器人名称',
      dataIndex: 'botName',
      key: 'name',
      width: 150,
      render: (text: string, record: BotAccount) => (
        <Popover
          content={
            <Space direction="vertical" size="small">
              <div>
                <strong>App ID:</strong> {record.botConfig?.app_id}
              </div>
              <div>
                <strong>类型:</strong> {record.botType}
              </div>
              <div>
                <strong>创建时间:</strong> {record.createdAt}
              </div>
            </Space>
          }
          title="机器人详情"
        >
          <a style={{ cursor: 'pointer' }}>
            <LinkOutlined /> {text}
          </a>
        </Popover>
      ),
    },
    {
      title: '连接状态',
      dataIndex: 'connectionStatus',
      key: 'status',
      width: 120,
      render: (status: string) => (
        <Badge
          status={StatusColorMap[status as keyof typeof StatusColorMap]}
          text={StatusLabelMap[status as keyof typeof StatusLabelMap]}
        />
      ),
    },
    {
      title: '启用',
      dataIndex: 'enabled',
      key: 'enabled',
      width: 80,
      render: (enabled: boolean) => (
        <Tag color={enabled ? 'green' : 'red'}>{enabled ? '✓ 启用' : '✗ 禁用'}</Tag>
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_: any, record: BotAccount) => (
        <Space>
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => {
              setEditingBot(record)
              form.setFieldsValue(record)
              setVisible(true)
            }}
          >
            编辑
          </Button>
          <Button type="link" size="small" danger icon={<DeleteOutlined />} onClick={() => handleDelete(record.id)}>
            删除
          </Button>
        </Space>
      ),
    },
  ]

  return (
    <>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        {/* 操作按钮 */}
        <Row justify="space-between" align="middle">
          <Col>
            <h2 style={{ margin: 0 }}>机器人管理</h2>
          </Col>
          <Col>
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => {
                setEditingBot(null)
                form.resetFields()
                setVisible(true)
              }}
              size="large"
            >
              新增机器人
            </Button>
          </Col>
        </Row>

        {/* 统计卡片 */}
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={8}>
            <Card style={{ textAlign: 'center', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }}>
              <div style={{ fontSize: '28px', fontWeight: 'bold', color: '#1890ff' }}>{bots.length}</div>
              <div style={{ color: '#999', marginTop: '8px' }}>机器人总数</div>
            </Card>
          </Col>
          <Col xs={24} sm={8}>
            <Card style={{ textAlign: 'center', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }}>
              <div style={{ fontSize: '28px', fontWeight: 'bold', color: '#52c41a' }}>
                {bots.filter((b) => b.connectionStatus === 'CONNECTED').length}
              </div>
              <div style={{ color: '#999', marginTop: '8px' }}>已连接</div>
            </Card>
          </Col>
          <Col xs={24} sm={8}>
            <Card style={{ textAlign: 'center', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }}>
              <div style={{ fontSize: '28px', fontWeight: 'bold', color: '#f5222d' }}>
                {bots.filter((b) => b.connectionStatus === 'FAILED').length}
              </div>
              <div style={{ color: '#999', marginTop: '8px' }}>连接失败</div>
            </Card>
          </Col>
        </Row>

        {/* 表格 */}
        <Card style={{ borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }}>
          <Table
            columns={columns}
            dataSource={bots.map((b) => ({ ...b, key: b.id }))}
            loading={loading}
            pagination={{ pageSize: 10 }}
            size="small"
          />
        </Card>
      </Space>

      {/* 编辑弹窗 */}
      <Modal
        title={editingBot ? '编辑机器人' : '新增机器人'}
        open={visible}
        onCancel={() => setVisible(false)}
        onOk={() => form.submit()}
        confirmLoading={loading}
        width={600}
      >
        <Form form={form} onFinish={handleSubmit} layout="vertical">
          <Form.Item label="机器人名称" name="botName" rules={[{ required: true, message: '请输入机器人名称' }]}>
            <Input placeholder="输入机器人名称" />
          </Form.Item>

          <Form.Item label="App ID" name="appId" rules={[{ required: true, message: '请输入 App ID' }]}>
            <Input placeholder="输入飞书机器人 App ID" />
          </Form.Item>

          <Form.Item label="App Secret" name="appSecret" rules={[{ required: true, message: '请输入 App Secret' }]}>
            <Input.Password placeholder="输入飞书机器人 App Secret" />
          </Form.Item>

          <Form.Item label="绑定应用" name="aiAppId" rules={[{ required: true, message: '请选择应用' }]}>
            <Select
              placeholder="选择要绑定的应用"
              options={apps.map((a) => ({ label: `${a.appName}(${a.appType})`, value: a.id }))}
            />
          </Form.Item>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item label="会话超时(分钟)" name="sessionTimeout" initialValue={120}>
                <InputNumber min={5} max={1440} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item label="显示错误信息" name="showError" valuePropName="checked" initialValue={false}>
                <Switch />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>
    </>
  )
}
