# BuildMyBridge - 系统架构

## 🏗️ 系统总体架构

```
┌─────────────────────────────────────────────────────────────┐
│                        用户层                                  │
│  ┌──────────────────┐              ┌───────────────────┐    │
│  │  前端应用         │              │  飞书客户端       │    │
│  │  (React)         │              │  (扫码登录)       │    │
│  └────────┬─────────┘              └─────────┬─────────┘    │
└───────────┼────────────────────────────────────┼─────────────┘
            │                                    │
            └────────┬─────────────────┬─────────┘
                     │                 │
        ┌────────────▼─────┐  ┌────────▼───────────┐
        │   OAuth 认证      │  │  WebSocket 长连接  │
        │   生成 JWT Token  │  │  接收飞书消息      │
        └────────┬──────────┘  └────────┬───────────┘
                 │                      │
        ┌────────▼──────────────────────▼────────┐
        │        API Gateway / REST API           │
        │  (Spring Boot 应用 - 8081 端口)        │
        ├──────────────────────────────────────────┤
        │              业务层                      │
        ├──────────────────────────────────────────┤
        │  认证模块         │  机器人管理          │
        │  ├─ OAuth         │  ├─ 创建/更新/删除  │
        │  └─ JWT           │  └─ 启用/禁用       │
        ├──────────────────────────────────────────┤
        │  消息处理         │  AI 应用集成         │
        │  ├─ 解析          │  ├─ Dify            │
        │  ├─ 路由          │  └─ (其他)          │
        │  └─ 发送          │                      │
        ├──────────────────────────────────────────┤
        │          缓存 & 队列                    │
        │  ├─ Redis Token 缓存                   │
        │  ├─ 会话状态缓存                       │
        │  └─ 消息队列                           │
        └────────┬──────────────────────┬────────┘
                 │                      │
        ┌────────▼──────┐      ┌────────▼─────────┐
        │   MySQL 数据库 │      │  Redis 缓存      │
        │   (持久化)    │      │  (会话 & Token)  │
        └───────────────┘      └──────────────────┘
```

---

## 📦 核心模块架构

### 1️⃣ 认证模块

```
飞书用户
    │
    ├─→ GET /auth/login
    │   ├─→ FeishuOAuthService.getLoginUrl()
    │   └─→ 返回飞书授权 URL
    │
    ├─→ 用户在飞书中扫码授权
    │
    ├─→ GET /auth/callback?code=xxx&state=xxx
    │   ├─→ FeishuOAuthService.exchangeToken(code)
    │   │   ├─→ 调用飞书 API 交换 access_token
    │   │   └─→ Redis 缓存 token
    │   ├─→ FeishuOAuthService.getUserInfo(token)
    │   │   └─→ 获取用户信息
    │   ├─→ UserService.createOrUpdateUser()
    │   │   └─→ 保存到数据库
    │   └─→ JwtTokenProvider.generateToken()
    │       └─→ 生成 JWT Token 返回前端
    │
    └─→ 前端保存 JWT，后续请求使用
```

**相关类**：
- `FeishuOAuthService` - OAuth 流程处理
- `AuthController` - REST 端点
- `JwtAuthenticationFilter` - JWT 验证过滤器
- `SecurityConfig` - Spring Security 配置

---

### 2️⃣ 机器人管理模块

```
┌─────────────────────────────────────────┐
│         机器人管理 API                   │
├─────────────────────────────────────────┤
│ BotAccountController                    │
│  ├─ POST /bots                 创建    │
│  ├─ GET /bots                  列表    │
│  ├─ GET /bots/{id}             详情    │
│  ├─ PUT /bots/{id}             更新    │
│  ├─ DELETE /bots/{id}          删除    │
│  └─ PUT /bots/{id}/status      切换    │
├─────────────────────────────────────────┤
│ BotAccountService                       │
│  ├─ CRUD 操作                          │
│  ├─ AppSecret 加密存储                 │
│  └─ JSON 配置序列化                    │
├─────────────────────────────────────────┤
│         数据库                          │
│  bot_account 表                        │
│  ├─ id (主键)                          │
│  ├─ bot_type (FEISHU)                 │
│  ├─ bot_name                           │
│  ├─ bot_config (JSON)                  │
│  │  ├─ app_id                          │
│  │  └─ app_secret (MD5加密)            │
│  └─ enabled                            │
└─────────────────────────────────────────┘
```

**相关类**：
- `BotAccountController` - REST 端点
- `BotAccountService` - 业务逻辑
- `BotAccountRepository` - 数据访问
- `BotAccount` - 数据实体

---

### 3️⃣ 消息处理模块

```
飞书服务器
    │
    ├─→ WebSocket 事件
    │   ├─→ message_received（用户消息）
    │   ├─→ message_create（机器人消息）
    │   └─→ ... 其他事件
    │
    ├─→ FeishuEventHandler (待实现)
    │   ├─→ 验证消息签名（3秒内）
    │   └─→ 异步入队 Redis Queue
    │
    ├─→ MessageHandlerService (待实现)
    │   ├─→ 1. FeishuMessageParser
    │   │   └─→ 解析事件 JSON，提取内容
    │   ├─→ 2. MessageRouterService
    │   │   └─→ 根据路由规则确定 AI 应用
    │   ├─→ 3. DifyChatflowClient
    │   │   └─→ 调用 Dify Chatflow API
    │   ├─→ 4. FeishuMessageSender
    │   │   └─→ 将 AI 回复发回飞书
    │   └─→ 5. MessageLogService
    │       └─→ 记录交互日志
    │
    └─→ 返回确认信号
```

**相关类**：
- `FeishuEventHandler` - 事件处理入口（待实现）
- `FeishuMessageParser` - 消息解析
- `FeishuMessageSender` - 消息发送
- `MessageHandlerService` - 异步处理（待实现）
- `MessageRouterService` - 消息路由（待实现）
- `MessageLogService` - 日志记录（待实现）

---

### 4️⃣ AI 应用集成模块

```
┌───────────────────────────────┐
│    AI 应用管理                 │
├───────────────────────────────┤
│ AiAppController               │
│  ├─ POST /ai-apps    创建     │
│  ├─ GET /ai-apps     列表     │
│  ├─ GET /ai-apps/{id} 详情   │
│  ├─ PUT /ai-apps/{id} 更新   │
│  └─ DELETE /ai-apps/{id} 删除 │
├───────────────────────────────┤
│ AiAppService                  │
│  └─ CRUD 操作                 │
├───────────────────────────────┤
│ Dify 适配器 (待实现)          │
│  ├─ DifyChatflowClient       │
│  │  └─ Chatflow API         │
│  └─ DifyWorkflowClient       │
│     └─ Workflow API          │
├───────────────────────────────┤
│         数据库                │
│  ai_app 表                   │
│  ├─ app_type (DIFY)         │
│  ├─ app_config (JSON)       │
│  │  ├─ api_url              │
│  │  └─ api_key              │
│  └─ enabled                 │
└───────────────────────────────┘
```

**相关类**：
- `AiAppController` - REST 端点（待实现）
- `AiAppService` - 业务逻辑（待实现）
- `DifyChatflowClient` - Dify 客户端（待实现）
- `DifyWorkflowClient` - Workflow 客户端（待实现）

---

### 5️⃣ 绑定关系管理模块

```
┌────────────────────────────────┐
│    绑定关系管理                  │
├────────────────────────────────┤
│ SubscriptionController         │
│  ├─ POST /subscriptions 创建   │
│  ├─ GET /subscriptions  列表   │
│  ├─ GET /subscriptions/{id}    │
│  ├─ PUT /subscriptions/{id}    │
│  └─ DELETE /subscriptions/{id} │
├────────────────────────────────┤
│ SubscriptionService            │
│  └─ CRUD 操作                  │
├────────────────────────────────┤
│         数据库                 │
│  subscription 表              │
│  ├─ bot_account_id  ───→┐    │
│  ├─ ai_app_id       ───→┤    │
│  ├─ session_timeout │    │
│  ├─ show_error      │    │
│  ├─ routing_rules   │    │
│  └─ enabled         │    │
└────────────────────────────────┘
     关联 bot_account
     和 ai_app 表
```

**相关类**：
- `SubscriptionController` - REST 端点（待实现）
- `SubscriptionService` - 业务逻辑（待实现）
- `Subscription` - 数据实体（待实现）

---

## 🗄️ 数据库设计

### E-R 图

```
┌─────────────────┐
│     user        │
├─────────────────┤
│ id (PK)         │
│ username        │
│ feishu_open_id  │
│ created_at      │
└─────────────────┘
        │
        │ (1:N 关系可能)
        │
        └─────┐
              │
              ├──────────────────┐
              │                  │
    ┌──────────────────┐  ┌─────────────────┐
    │  bot_account     │  │   ai_app        │
    ├──────────────────┤  ├─────────────────┤
    │ id (PK)          │  │ id (PK)         │
    │ bot_type         │  │ app_type        │
    │ bot_name         │  │ app_name        │
    │ bot_config (JSON)│  │ app_config(JSON)│
    │ enabled          │  │ enabled         │
    │ created_at       │  │ created_at      │
    └──────────────────┘  └─────────────────┘
            │                      │
            │ (N:M 关系)           │
            │                      │
            └─────┬────────────────┘
                  │
         ┌────────▼─────────┐
         │  subscription    │
         ├──────────────────┤
         │ id (PK)          │
         │ bot_account_id   │◄─── FK
         │ ai_app_id        │◄─── FK
         │ session_timeout  │
         │ show_error       │
         │ routing_rules    │
         │ enabled          │
         │ created_at       │
         └──────────────────┘
                  │
                  │ (1:N 关系)
                  │
         ┌────────▼──────────┐
         │  message_log      │
         ├───────────────────┤
         │ id (PK)           │
         │ bot_account_id    │
         │ ai_app_id         │
         │ message_content   │
         │ response_content  │
         │ status            │
         │ error_message     │
         │ created_at        │
         └───────────────────┘
```

---

## 🔄 关键数据流

### OAuth 认证流程

```
用户
  │
  ├─→ 访问前端应用
  │   ├─→ 前端检测到未认证
  │   └─→ 前端调用 GET /auth/login
  │       │
  │       └─→ 后端返回授权 URL
  │           https://open.feishu.cn/oauth2/v2/authorize?...
  │
  ├─→ 前端重定向到授权 URL
  │   └─→ 用户在飞书中扫码
  │
  ├─→ 飞书重定向回：/auth/callback?code=xxx&state=xxx
  │   │
  │   └─→ 后端处理：
  │       ├─→ 1. 验证 state（防止 CSRF）
  │       ├─→ 2. 交换 code 获取 access_token
  │       ├─→ 3. 使用 token 获取用户信息
  │       ├─→ 4. 保存用户到数据库
  │       └─→ 5. 生成 JWT Token
  │
  ├─→ 后端返回 JWT Token
  │   └─→ 前端保存到 localStorage/Cookie
  │
  └─→ 后续请求都在 Authorization header 中携带 JWT
```

### 消息处理流程

```
飞书用户
  │
  ├─→ 发送消息给机器人
  │   │
  │   └─→ 飞书服务器
  │       └─→ WebSocket 事件：message_received
  │
  ├─→ 后端 FeishuWSConnectionManager 接收
  │   │
  │   └─→ FeishuEventHandler 处理
  │       ├─→ 1. 验证消息签名（需在 3 秒内）
  │       ├─→ 2. 快速返回 challenge 确认
  │       └─→ 3. 异步入队 Redis
  │
  ├─→ MessageHandlerService 异步处理
  │   │
  │   ├─→ 1. FeishuMessageParser.parse()
  │   │   └─→ 提取消息内容、用户、会话等
  │   │
  │   ├─→ 2. SessionStateService.getSessionState()
  │   │   └─→ 从 Redis 获取会话状态
  │   │
  │   ├─→ 3. MessageRouterService.route()
  │   │   └─→ 根据路由规则确定 AI 应用
  │   │
  │   ├─→ 4. DifyChatflowClient.chat()
  │   │   └─→ 调用 Dify API，获取 AI 回复
  │   │
  │   ├─→ 5. FeishuMessageSender.sendMessage()
  │   │   └─→ 将回复发送回飞书
  │   │
  │   ├─→ 6. SessionStateService.updateSessionState()
  │   │   └─→ 更新会话状态（上次对话等）
  │   │
  │   └─→ 7. MessageLogService.logMessage()
  │       └─→ 记录到数据库
  │
  └─→ 飞书用户收到 AI 回复
```

---

## 🔐 安全架构

### 认证和授权

```
Request
  │
  ├─→ JwtAuthenticationFilter
  │   ├─→ 提取 Authorization header
  │   ├─→ 解析 JWT Token
  │   ├─→ 验证签名和过期时间
  │   └─→ 设置 SecurityContext
  │
  ├─→ Spring Security
  │   ├─→ 检查权限
  │   └─→ 放行或拒绝
  │
  └─→ Controller 业务逻辑
```

### Token 安全

- **JWT 密钥**：存储在环境变量，不上传代码
- **Token 过期时间**：24 小时（可配置）
- **刷新机制**：需要重新 OAuth（短期方案）
- **传输**：仅在 HTTPS 上传输（生产环境）

### 敏感信息加密

- **AppSecret**：MD5 加密存储（生产环境建议使用 AES）
- **用户信息**：从不在日志中打印

---

## 📊 缓存策略

### Redis 缓存设计

```
┌─────────────────────────────────┐
│         Redis 缓存              │
├─────────────────────────────────┤
│ Key                 │ TTL       │
├─────────────────────┼───────────┤
│ token:{user_id}     │ 2 小时    │
│ (OAuth token)       │           │
├─────────────────────┼───────────┤
│ session:{user_id}:  │ 可配置    │
│ {bot_id}            │ (120-1440)│
│ (会话状态)          │ 分钟      │
├─────────────────────┼───────────┤
│ message:queue       │ 无 TTL    │
│ (消息队列)          │ (list)    │
└─────────────────────┴───────────┘
```

### 缓存失效策略

- **手动失效**：用户登出时删除 token
- **自动失效**：TTL 过期自动删除
- **预热**：应用启动时可预加载配置

---

## 🚀 扩展性设计

### 模块化架构

- 每个功能模块独立
- 通过接口解耦
- 易于添加新的 AI 应用类型（Dify、OpenAI等）
- 易于支持新的机器人平台（钉钉、企业微信等）

### 异步处理

- 关键路径同步：认证、数据验证
- 非关键路径异步：消息处理、日志记录
- 使用 Redis Queue 解耦

### 数据库设计

- 预留字段用于扩展（如 routing_rules）
- JSON 字段存储灵活配置
- 合理的索引优化查询性能

---

## 📈 性能考虑

### 连接管理

- **数据库连接池**：HikariCP（8-20 个连接）
- **Redis 连接池**：Lettuce（预热 5 个）
- **HTTP 客户端**：OkHttp（连接复用）

### 缓存和CDN

- 静态资源（JS/CSS）：CDN 分发
- API 响应：根据需要缓存（如机器人列表）
- 数据库查询：一级缓存（Redis）

### 监控和告警

- 应用日志：INFO / DEBUG 级别
- 性能指标：响应时间、错误率、缓存命中率
- 数据库慢查询日志

---

**最后更新**: 2026-03-31
