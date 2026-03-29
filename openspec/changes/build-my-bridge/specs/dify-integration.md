## 新增需求

### 需求：Dify Chatflow API 调用

系统 SHALL 调用 Dify Chatflow API 获取 AI 响应。

#### 场景：Chatflow 阻塞式调用
- **WHEN** 用户消息需要发送到 Chatflow 类型的 Dify 应用
- **THEN** 系统发送 POST 请求到 {base_url}/v1/chat-messages
- **THEN** 系统在请求头中携带 Authorization: Bearer {api_key}
- **THEN** 系统在请求体中包含 query、user 和 response_mode
- **THEN** 系统返回 Dify 响应中的 "answer" 字段

#### 场景：Chatflow 流式调用
- **WHEN** response_mode 设置为 streaming
- **THEN** 系统处理 Server-Sent Events (SSE) 流
- **THEN** 系统聚合数据块并返回完整响应

### 需求：Dify Workflow API 调用

系统 SHALL 调用 Dify Workflow API 执行工作流。

#### 场景：Workflow 执行
- **WHEN** 用户消息需要发送到 Workflow 类型的 Dify 应用
- **THEN** 系统发送 POST 请求到 {base_url}/v1/workflows/run
- **THEN** 系统在请求头中携带 Authorization: Bearer {api_key}
- **THEN** 系统在请求体中包含 inputs（query 和 user 字段）
- **THEN** 系统返回 workflow 响应中的 "data.outputs.text"

### 需求：Dify 应用配置管理

系统 SHALL 允许管理员管理 Dify 应用配置。

#### 场景：创建 Dify 应用配置
- **WHEN** 管理员提交 Dify 应用配置（name、base_url、api_key、app_type、app_id）
- **THEN** 系统验证必填字段
- **THEN** 系统验证 app_type 为 "chatflow" 或 "workflow"
- **THEN** 系统保存到数据库
- **THEN** 系统返回创建的应用记录（包含 ID）

#### 场景：测试 Dify 应用连接
- **WHEN** 管理员点击 Dify 应用的"测试"按钮
- **THEN** 系统调用 Dify /info 端点验证 api_key
- **THEN** 如果响应有效，返回成功
- **THEN** 如果连接失败，返回错误信息

#### 场景：列出 Dify 应用
- **WHEN** 管理员请求 GET /api/dify-apps
- **THEN** 系统返回所有 Dify 应用配置（api_key 脱敏）