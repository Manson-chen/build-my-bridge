## 新增需求

### 需求：飞书 Webhook 消息接收

系统 SHALL 接收飞书机器人的 Webhook 事件，并处理文本、图片和语音消息。

#### 场景：URL 验证
- **WHEN** 飞书发送 GET 请求到 /api/webhook/verify 携带 challenge 参数
- **THEN** 系统直接返回 challenge 值

#### 场景：文本消息处理
- **WHEN** 飞书发送 POST 请求包含文本消息事件
- **THEN** 系统从请求中提取消息内容、发送者 ID 和消息 ID

#### 场景：签名验证（带加密密钥）
- **WHEN** 飞书发送请求带有 X-Lark-Signature 请求头且配置了 encrypt_key
- **THEN** 系统使用 SHA256(encrypt_key + timestamp + nonce + raw_body) 验证签名
- **THEN** 签名不匹配时返回 401

### 需求：飞书消息发送

系统 SHALL 通过飞书 IM API 向用户发送回复消息。

#### 场景：发送文本回复
- **WHEN** Dify 返回响应内容
- **THEN** 系统使用 POST /open-apis/im/v1/messages/{message_id}/reply 向发送者的 open_id 发送文本消息

### 需求：飞书 OAuth 登录

系统 SHALL 通过飞书 OAuth 2.0 认证管理员身份。

#### 场景：OAuth 重定向
- **WHEN** 用户访问 /api/auth/login
- **THEN** 系统重定向到飞书 OAuth 授权 URL，携带 app_id 和 redirect_uri

#### 场景：OAuth 回调
- **WHEN** 飞书重定向到 /api/auth/callback 携带授权码
- **THEN** 系统用授权码换取用户访问令牌
- **THEN** 系统获取用户信息（open_id、name、avatar）
- **THEN** 系统在数据库中创建或更新用户记录
- **THEN** 系统返回 JWT 令牌给前端

### 需求：飞书令牌管理

系统 SHALL 管理飞书 tenant_access_token 并进行缓存。

#### 场景：令牌缓存
- **WHEN** 请求获取机器人的访问令牌
- **THEN** 系统首先检查 Redis 缓存
- **THEN** 如果缓存中存在令牌，直接返回
- **THEN** 如果未缓存，调用飞书 API 获取令牌
- **THEN** 系统缓存令牌并设置 2 小时过期时间

### 需求：飞书机器人配置管理

系统 SHALL 允许管理员对飞书机器人配置进行增删改查。

#### 场景：创建机器人配置
- **WHEN** 管理员提交机器人配置（app_id、app_secret、encrypt_key、name）
- **THEN** 系统验证必填字段
- **THEN** 系统保存到数据库
- **THEN** 系统返回创建的机器人记录（包含 ID）

#### 场景：列出机器人配置
- **WHEN** 管理员请求 GET /api/bots
- **THEN** 系统返回所有机器人配置（app_secret 脱敏）

#### 场景：删除机器人配置
- **WHEN** 管理员删除一个机器人
- **THEN** 系统首先删除关联的绑定关系
- **THEN** 系统删除机器人记录