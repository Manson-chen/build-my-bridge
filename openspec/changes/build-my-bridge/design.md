## 背景与现状

BuildMyBridge 是一个机器人与 AI 应用集成的中间件项目，当前专注于飞书机器人与 Dify 的集成。

**技术架构：**

**后端：**
- 框架：Spring Boot 3.x + Spring Web
- 持久化：MyBatis-Plus（ORM 框架，简化数据库操作）
- 数据库：MySQL 8.0+（InnoDB 引擎）
- 缓存：Redis 7.x（Token 缓存、会话状态、消息队列）
- 认证：Spring Security + JWT Token
- 异步处理：Spring Task（@Async、@Scheduled 定时任务）
- 消息队列：Redis Queue（简单异步处理，Phase 2 考虑升级到 RabbitMQ/Kafka）
- 日志：SLF4J + Logback
- JSON 处理：Jackson
- HTTP 客户端：RestTemplate 或 OkHttp（飞书、Dify API 调用）
- 飞书 SDK：lark-java（官方 SDK）
- 构建工具：Maven 3.8+
- Java 版本：JDK 17+

**前端：**
- 框架：React 18 + TypeScript + Vite
- UI 组件库：Ant Design 5.x（企业级中后台标准方案）
- 状态管理：Zustand（轻量级状态管理）
- HTTP 客户端：Axios
- 路由：React Router v6
- 图表库：Recharts
- 加密：crypto-js 或内置 MD5 库

**数据存储：**
- MySQL 8.0+：业务数据持久化
- Redis 7.x：Token 缓存、会话状态、消息队列

**认证方式：** 飞书 OAuth 扫码登录

**绑定关系：** 一对多（一个机器人绑定一个应用，一个应用可被多个机器人使用）

**设计原则：** 支持扩展性，留出接入其他机器人平台（钉钉、企业微信等）和其他 AI 应用平台的接口

**约束条件：**
- 暂不需要 Spring Cloud 微服务架构
- 暂不支持飞书卡片消息交互
- 暂不支持多租户隔离
- 一个机器人同一时间只能绑定一个应用

## 目标 / 非目标

**目标：**
- 实现飞书机器人与 Dify Chatflow/Workflow 的双向打通
- 提供 Web 管理界面用于配置机器人和 Dify 应用
- 支持一对多绑定（在机器人编辑时配置绑定的 Dify 应用）
- 记录消息日志供查看和分析
- 集成飞书 OAuth 实现管理员登录

**非目标：**
- 暂不支持微服务架构（单体 Spring Boot 足够）
- 暂不支持飞书卡片消息交互（仅支持文本消息）
- 暂不支持 Dify 知识库检索
- 暂不支持多租户隔离
- 暂不支持一个机器人绑定多个 Dify 应用（优先级路由）

## 关键设计决策

### 1. 技术栈选择

**决策：** 后端使用 Spring Boot，前端使用 React 18 + Ant Design

**理由：**
- Spring Boot 是 Java 生态最成熟的 Web 框架
- React 在行业内采用度最高，团队协作生态完整
- Ant Design 是企业级中后台的标准方案，预设完整
- Zustand 提供轻量级状态管理，不必过度设计
- 用户指定按行业标准方案实现

### 2. 数据库设计

**决策：** 使用 MySQL 存储业务数据，Redis 缓存登录信息和必要的配置

**理由：**
- MySQL 成熟稳定，适合结构化数据存储
- Redis 用于缓存飞书 token 等认证信息，降低 API 调用频率
- 为未来扩展预留空间

**表结构设计（方向 A：类型字段 + JSON 配置）：**

```
user
├── id
├── username
├── feishu_open_id
├── created_at

bot_account                      (通用机器人账户)
├── id
├── bot_type                    (枚举: FEISHU, DINGDING, WECHAT, ...)
├── bot_name
├── bot_config                  (JSON, 根据 bot_type 存储对应配置)
├── enabled
├── created_at

ai_app                           (通用 AI 应用)
├── id
├── app_type                    (枚举: DIFY, OPENAI, ANTHROPIC, ...)
├── app_name
├── app_config                  (JSON, 根据 app_type 存储对应配置)
├── enabled
├── created_at

subscription                     (机器人与应用的绑定)
├── id
├── bot_account_id  (FK)
├── ai_app_id       (FK)
├── routing_rules   (JSON, 路由规则 - Phase 1 为空，Phase 2 支持)
├── session_timeout_minutes      (会话超时时间，单位分钟，默认 120)
├── show_error_to_user           (是否向用户展示错误信息，默认 false)
├── enabled
├── created_at

message_log                      (消息日志)
├── id
├── bot_account_id
├── ai_app_id
├── message_content
├── response_content
├── status           (success, error)
├── error_message
├── created_at
```

**JSON 配置示例：**

飞书机器人配置 (`bot_type = FEISHU`):
```json
{
  "app_id": "cli_xxx",
  "app_secret": "xxx",
  "webhook_url": "https://domain.com/webhook/feishu"
}
```

Dify 应用配置 (`app_type = DIFY`):
```json
{
  "api_url": "https://dify.domain.com",
  "api_key": "xxx",
  "app_id": "xxx",
  "type": "chatflow"  或 "workflow"
}
```

**设计优势：**
- 当前实现飞书 + Dify，无需修改表结构
- 添加新机器人平台：只需新增 bot_type 枚举值和 bot_config JSON 格式
- 添加新应用平台：只需新增 app_type 枚举值和 app_config JSON 格式
- 使用 Adapter 模式在代码层实现类型转换和处理

### 3. 消息路由策略

**决策：** Hybrid 路由模型 - Phase 1 简单直接，Phase 2 支持灵活规则

**理由：**
- Phase 1：一对多绑定简化了路由逻辑，一个 Bot 直接对应一个 Dify App
- Phase 2：支持条件过滤，一个 Bot 可根据消息内容分流到多个 Dify App
- 业内通用方案，平衡简单和灵活

**路由模型说明：**

**Phase 1（当前）：直接绑定**
```
Bot-1 → subscription → Dify-App-1（default_app_id）
Bot-2 → subscription → Dify-App-1
Bot-3 → subscription → Dify-App-2
```

**Phase 2（后期）：条件过滤**
```
subscription.routing_rules:
{
  "default_app_id": "app_1",
  "rules": [
    {
      "priority": 10,
      "type": "keyword",
      "keywords": ["超时", "重试"],
      "app_id": "app_2"
    },
    {
      "priority": 5,
      "type": "pattern",
      "pattern": ".*查询.*",
      "app_id": "app_3"
    }
  ]
}

路由逻辑：
1. 按 priority 排序规则
2. 逐个评估，第一个匹配的规则对应的 app_id
3. 都不匹配，使用 default_app_id
```

### 4. 会话状态管理

**决策：** 短期会话状态存储在 Redis，超时时间由 subscription 配置决定

**理由：**
- 用户对话需要上下文理解（Dify 会话 ID）
- 超时时间可配置（默认 120 分钟），适应不同场景需求
- 长期记忆（Phase 2）交由数据库和向量库处理

**会话状态设计：**

```
Redis Key: session:{user_id}:{bot_account_id}
TTL: 由 subscription.session_timeout_minutes 决定（默认 120 分钟）

Value:
{
  "conversation_id": "xxx",      # Dify 会话 ID
  "context": [                    # 最近消息上下文
    {
      "role": "user",
      "content": "...",
      "timestamp": 1234567890
    },
    {
      "role": "assistant",
      "content": "...",
      "timestamp": 1234567891
    }
  ],
  "metadata": {
    "user_name": "xxx",
    "chat_type": "p2p",          # p2p 或 group
    "last_app_id": "xxx"
  },
  "created_at": 1234567890,
  "last_updated": 1234567890,
  "expires_at": 1234567890 + session_timeout_minutes*60
}
```

**会话状态使用场景：**
- 延续对话：同用户在会话超时时间内的对话保持连续
- 上下文理解：Dify 能理解之前的对话背景
- 个性化响应：基于会话历史调整回复风格
- 用户体验：不需要重复解释背景信息

### 7. 故障处理机制（Phase 2 - 后期实现）

**决策：** 分层故障处理 + 自动恢复 + 死信队列

**理由：**
- 提高系统可靠性和可维护性
- 降低人工干预成本
- 完整的故障追踪和恢复

**处理层次：**

**第 1 层：初始连接失败恢复**
- 创建 WSClient 失败时进行重试
- 重试策略：指数退避 [0ms, 2s, 5s]
- 3 次重试失败后标记 Bot 状态为 FAILED
- 用户手动重新保存才能重试

**第 2 层：运行时连接监测和自动恢复**
- 心跳机制：每 30 秒发送一次 ping
- 连接异常检测：捕获断开事件
- 自动重连：指数退避 [1s, 10s, 30s]
- 3 次重连失败后标记为 DISCONNECTED，触发告警
- 连接状态机：INIT → CONNECTED ↔ RECONNECTING → DISCONNECTED

**第 3 层：消息处理失败恢复**
- 异步消息队列处理
- Dify 调用失败重试：[5分钟, 15分钟]
- 回复发送失败重试：[2分钟, 5分钟]
- 失败消息移入死信队列
- 死信队列支持人工重试和 30 天自动清理

**数据库支撑（Phase 2）：**
- bot_account 表新增连接状态相关字段（connection_status, last_error_message 等）
- 新增 message_task 表（异步处理队列）
- 新增 dead_letter_queue 表（失败消息存储）

**监控告警（Phase 2）：**
- 连接可用性 < 95% 告警
- 24 小时断开次数 > 5 次告警
- 消息处理失败率 > 5% 告警
- 死信队列累积 > 100 条告警

## 配置与可观测性

### 绑定关系配置

每个 subscription 支持以下可配置项，在 BuildMyBridge 管理界面进行设置：

**1. 会话超时时间（session_timeout_minutes）**
- 默认值：120 分钟
- 范围：5 ~ 1440 分钟（5 分钟 ~ 24 小时）
- 用途：控制 Redis 中会话状态的有效期，用户超过此时间未交互将开启新对话

**2. 错误信息展示（show_error_to_user）**
- 默认值：false
- 为 true 时：向飞书用户展示详细错误信息（如"应用被禁用"、"应用调用超时"等）
- 为 false 时：向用户展示通用错误提示（如"暂时无法处理，请稍后重试"）
- 用途：生产环境可隐藏技术细节，提升用户体验；开发环境可展示便于调试

**实现说明：**
- 消息处理失败时，根据 subscription.show_error_to_user 决定错误提示的具体内容
- 会话创建或检索时，使用 subscription.session_timeout_minutes 作为 Redis TTL

## 风险与权衡

- **风险：长连接建立失败**
  - 应对措施：异步重试 3 次（间隔 0ms/2s/5s），失败后标记 Bot 状态为 FAILED

- **风险：长连接运行时断开（Phase 2）**
  - 应对措施：心跳检测 + 自动重连（指数退避）+ 人工告警

- **风险：消息处理超过 3 秒**
  - 应对措施：EventHandler 快速返回，实际处理异步进行

- **风险：Dify 响应时间过长**
  - 应对措施：设置超时时间，超时返回友好错误提示；失败消息入死信队列（Phase 2）

- **风险：机器人绑定应用缺失或应用被禁用**
  - 应对措施：消息处理时检查应用状态，如无效则返回错误提示给用户

- **风险：禁用应用时影响已绑定的机器人**
  - 应对措施：禁用应用前弹出确认对话，显示受影响的机器人列表，需管理员确认

## 部署计划

**Phase 1（核心功能）：**
1. 初始化项目结构（Spring Boot + Vue3）
2. 创建数据库表（bot_account、ai_app、subscription、message_log）
3. 实现飞书 OAuth 登录
4. 实现 Bot/DifyApp CRUD API
5. 实现飞书长连接集成（创建 WSClient 并注册 EventHandler）
6. 实现异步消息处理和 Dify 调用
7. 开发 Web 管理界面
8. 基础集成测试

**Phase 2（可靠性和监控）：**
1. 实现运行时连接监测和自动恢复（心跳 + 重连）
2. 实现消息失败重试和死信队列
3. 添加监控和告警
4. 完整的故障处理测试

## 待确定问题

- 是否需要支持飞书卡片消息的交互（如按钮点击事件）？
- Dify 会话状态管理如何处理？（当前每个请求独立）
- 日志保留策略？（当前无自动清理机制）