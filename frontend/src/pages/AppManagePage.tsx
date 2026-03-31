import { useState, useEffect } from 'react'
import { Table, Button, Space, Modal, Form, Input, Select, Switch, message, Card, Tag, Badge, Row, Col, Popover } from 'antd'
import { PlusOutlined, DeleteOutlined, EditOutlined, LockOutlined } from '@ant-design/icons'
import { mockApi } from '../api/mock'
import { AiApp } from '../types'

export default function AppManagePage() {
  const [apps, setApps] = useState<AiApp[]>([])
  const [loading, setLoading] = useState(false)
  const [visible, setVisible] = useState(false)
  const [editingApp, setEditingApp] = useState<AiApp | null>(null)
  const [appType, setAppType] = useState('DIFY')
  const [form] = Form.useForm()

  useEffect(() => {
    loadApps()
  }, [])

  const loadApps = async () => {
    setLoading(true)
    try {
      const res = await mockApi.getApps()
      setApps(res.data?.data || [])
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (values: any) => {
    setLoading(true)
    try {
      message.success('操作成功')
      setVisible(false)
      form.resetFields()
      setTimeout(loadApps, 500)
    } catch {
      message.error('操作失败')
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = (id: string) => {
    Modal.confirm({
      title: '删除应用',
      content: '确定要删除这个应用吗？',
      okText: '删除',
      okType: 'danger',
      onOk: () => message.success('删除成功'),
    })
  }

  const handleDisable = (id: string, appName: string) => {
    Modal.confirm({
      title: '禁用应用',
      content: `禁用 "${appName}" 后，所有绑定该应用的机器人将无法使用。确定要禁用吗？`,
      okText: '禁用',
      okType: 'danger',
      onOk: () => message.success('禁用成功'),
    })
  }

  const columns = [
    {
      title: '应用名称',
      dataIndex: 'appName',
      key: 'name',
      width: 200,
      render: (text: string, record: AiApp) => (
        <Popover
          content={
            <Space direction="vertical" size="small">
              <div>
                <strong>API 地址:</strong> {record.appConfig?.api_url}
              </div>
              <div>
                <strong>App ID:</strong> {record.appConfig?.app_id}
              </div>
              <div>
                <strong>类型:</strong> {record.appConfig?.type}
              </div>
              <div>
                <strong>创建时间:</strong> {record.createdAt}
              </div>
            </Space>
          }
          title="应用详情"
        >
          <a style={{ cursor: 'pointer' }}>{text}</a>
        </Popover>
      ),
    },
    {
      title: '应用类型',
      dataIndex: 'appType',
      key: 'type',
      width: 100,
      render: (type: string) => {
        const colorMap: Record<string, string> = { DIFY: 'blue', OPENAI: 'green', ANTHROPIC: 'purple' }
        return <Tag color={colorMap[type] || 'default'}>{type}</Tag>
      },
    },
    {
      title: '状态',
      dataIndex: 'enabled',
      key: 'enabled',
      width: 100,
      render: (enabled: boolean) => (
        <Badge status={enabled ? 'success' : 'error'} text={enabled ? '✓ 启用' : '✗ 禁用'} />
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      render: (_: any, record: AiApp) => (
        <Space>
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => {
              setEditingApp(record)
              form.setFieldsValue(record)
              setAppType(record.appType)
              setVisible(true)
            }}
          >
            编辑
          </Button>
          {record.enabled && (
            <Button
              type="link"
              size="small"
              danger
              icon={<LockOutlined />}
              onClick={() => handleDisable(record.id, record.appName)}
            >
              禁用
            </Button>
          )}
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
            <h2 style={{ margin: 0 }}>应用管理</h2>
          </Col>
          <Col>
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => {
                setEditingApp(null)
                setAppType('DIFY')
                form.resetFields()
                setVisible(true)
              }}
              size="large"
            >
              新增应用
            </Button>
          </Col>
        </Row>

        {/* 统计卡片 */}
        <Row gutter={[16, 16]}>
          <Col xs={24} sm={8}>
            <Card style={{ textAlign: 'center', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }}>
              <div style={{ fontSize: '28px', fontWeight: 'bold', color: '#1890ff' }}>{apps.length}</div>
              <div style={{ color: '#999', marginTop: '8px' }}>应用总数</div>
            </Card>
          </Col>
          <Col xs={24} sm={8}>
            <Card style={{ textAlign: 'center', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }}>
              <div style={{ fontSize: '28px', fontWeight: 'bold', color: '#52c41a' }}>
                {apps.filter((a) => a.enabled).length}
              </div>
              <div style={{ color: '#999', marginTop: '8px' }}>已启用</div>
            </Card>
          </Col>
          <Col xs={24} sm={8}>
            <Card style={{ textAlign: 'center', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }}>
              <div style={{ fontSize: '28px', fontWeight: 'bold', color: '#faad14' }}>
                {apps.filter((a) => !a.enabled).length}
              </div>
              <div style={{ color: '#999', marginTop: '8px' }}>已禁用</div>
            </Card>
          </Col>
        </Row>

        {/* 表格 */}
        <Card style={{ borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.08)' }}>
          <Table
            columns={columns}
            dataSource={apps.map((a) => ({ ...a, key: a.id }))}
            loading={loading}
            pagination={{ pageSize: 10 }}
            size="small"
          />
        </Card>
      </Space>

      {/* 编辑弹窗 */}
      <Modal
        title={editingApp ? '编辑应用' : '新增应用'}
        open={visible}
        onCancel={() => setVisible(false)}
        onOk={() => form.submit()}
        confirmLoading={loading}
        width={600}
      >
        <Form form={form} onFinish={handleSubmit} layout="vertical">
          <Form.Item label="应用名称" name="appName" rules={[{ required: true, message: '请输入应用名称' }]}>
            <Input placeholder="输入应用名称" />
          </Form.Item>

          <Form.Item label="应用类型" name="appType" rules={[{ required: true, message: '请选择应用类型' }]}>
            <Select
              onChange={setAppType}
              options={[
                { label: 'Dify', value: 'DIFY' },
                { label: 'OpenAI', value: 'OPENAI' },
                { label: 'Anthropic', value: 'ANTHROPIC' },
              ]}
            />
          </Form.Item>

          {appType === 'DIFY' && (
            <>
              <Form.Item label="API 地址" name="api_url" rules={[{ required: true, message: '请输入 API 地址' }]}>
                <Input placeholder="https://dify.example.com" />
              </Form.Item>

              <Form.Item label="API Key" name="api_key" rules={[{ required: true, message: '请输入 API Key' }]}>
                <Input.Password placeholder="输入 API Key" />
              </Form.Item>

              <Form.Item label="App ID" name="app_id" rules={[{ required: true, message: '请输入 App ID' }]}>
                <Input placeholder="输入 App ID" />
              </Form.Item>

              <Form.Item label="应用类型" name="type" rules={[{ required: true, message: '请选择应用类型' }]}>
                <Select
                  options={[
                    { label: 'Chatflow', value: 'chatflow' },
                    { label: 'Workflow', value: 'workflow' },
                  ]}
                />
              </Form.Item>
            </>
          )}

          <Form.Item label="启用状态" name="enabled" valuePropName="checked" initialValue={true}>
            <Switch />
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}
