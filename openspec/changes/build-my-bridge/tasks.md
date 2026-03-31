## 1. 项目初始化

- [x] 1.1 初始化 Spring Boot 3.x 后端项目
  - [x] 1.1.1 创建 Maven 项目，配置 pom.xml
  - [x] 1.1.2 依赖：spring-boot-starter-web、spring-boot-starter-data-jpa、mysql-connector、spring-boot-starter-data-redis、spring-security、lark-java、mybatis-plus-spring-boot-starter
  - [x] 1.1.3 配置 Java 版本为 17+，Spring Boot 版本 3.x+
- [x] 1.2 初始化 React 18 前端项目（Vite + React 18 + TypeScript + React Router v6 + Zustand + Axios + Ant Design）
- [x] 1.3 创建后端目录结构
  - [x] 1.3.1 controller（REST API 层）
  - [x] 1.3.2 service（业务逻辑层）
  - [x] 1.3.3 mapper（MyBatis-Plus 数据层）
  - [x] 1.3.4 entity（数据模型）
  - [x] 1.3.5 dto（数据传输对象）
  - [x] 1.3.6 adapter（适配器模式，支持多平台扩展）
  - [x] 1.3.7 config（Spring 配置类）
  - [x] 1.3.8 exception（异常处理）
  - [x] 1.3.9 security（认证授权）
  - [x] 1.3.10 utils（工具类）
- [x] 1.4 创建前端目录结构（pages、components、api、stores、router、types）
- [x] 1.5 创建 application.yml（或 application-dev.yml、application-prod.yml）
  - [x] 1.5.1 配置 MySQL 连接：spring.datasource.url、username、password、driver-class-name
  - [x] 1.5.2 配置 MyBatis-Plus：mapper-locations、type-aliases-package
  - [x] 1.5.3 配置 Redis：spring.redis.host、port、password
  - [x] 1.5.4 配置 Logging：日志级别、输出格式
  - [x] 1.5.5 配置飞书 OAuth：app_id、app_secret
- [x] 1.6 创建 MySQL 建表脚本（执行 src/main/resources/db/schema.sql）
  - [x] 1.6.1 创建 user、bot_account、ai_app、subscription、message_log 等表

## 2. 后端基础层

- [x] 2.1 实现 RedisConfig，配置 RedisTemplate
  - [x] 2.1.1 RedisTemplate 的序列化配置（使用 Jackson 序列化）
  - [x] 2.1.2 StringRedisTemplate 用于简单 String 操作
  - [x] 2.1.3 配置连接池参数（最大连接数、超时时间等）
- [x] 2.2 实现 CorsConfig，配置跨域请求（Spring Security 配置类）
- [x] 2.3 实现 GlobalExceptionHandler，统一异常处理（@RestControllerAdvice）
  - [x] 2.3.1 处理 Exception、RuntimeException、业务异常
  - [x] 2.3.2 返回统一的错误响应格式
- [x] 2.4 创建实体类（使用 @TableName、@TableId 注解，MyBatis-Plus）
  - [x] 2.4.1 User 实体：id（@TableId）、username、feishu_open_id、created_at
  - [x] 2.4.2 BotAccount 实体：id、bot_type（FEISHU 等枚举）、bot_name、bot_config（JSON 存储）、enabled、created_at
  - [x] 2.4.3 AiApp 实体：id、app_type（DIFY 等枚举）、app_name、app_config（JSON 存储）、enabled、created_at
  - [x] 2.4.4 Subscription 实体：id、bot_account_id（外键）、ai_app_id（外键）、session_timeout_minutes、show_error_to_user、routing_rules（JSON，Phase 2）、enabled、created_at
  - [x] 2.4.5 MessageLog 实体：id、bot_account_id、ai_app_id、message_content、response_content、status、error_message、created_at
  - [x] 2.4.6 使用 @Data、@Builder、@NoArgsConstructor 等 Lombok 注解简化 getter/setter
- [x] 2.5 创建 Mapper 接口（继承 BaseMapper<T>，使用 MyBatis-Plus）
  - [x] 2.5.1 UserMapper extends BaseMapper<User>
  - [x] 2.5.2 BotAccountMapper extends BaseMapper<BotAccount>，支持自定义查询方法
  - [x] 2.5.3 AiAppMapper extends BaseMapper<AiApp>
  - [x] 2.5.4 SubscriptionMapper extends BaseMapper<Subscription>
  - [x] 2.5.5 MessageLogMapper extends BaseMapper<MessageLog>
  - [x] 2.5.6 使用 @Mapper 注解标记接口
- [x] 2.6 创建 DTO 类（用于 API 请求/响应）
  - [x] 2.6.1 LoginRequestDTO、LoginResponseDTO
  - [x] 2.6.2 BotAccountRequestDTO、BotAccountResponseDTO
  - [x] 2.6.3 AiAppRequestDTO、AiAppResponseDTO
  - [x] 2.6.4 SubscriptionRequestDTO、SubscriptionResponseDTO
  - [x] 2.6.5 通用 ApiResponse<T> 响应包装类
  - [x] 2.6.6 使用 @Data、@Builder 等 Lombok 注解

## 3. 后端认证模块（飞书 OAuth）

- [x] 3.1 实现 JwtTokenProvider，生成和验证 JWT 令牌（Spring Security）
  - [x] 3.1.1 生成 JWT Token（包含 user_id、username、过期时间等）
  - [x] 3.1.2 验证 JWT Token 的有效性
  - [x] 3.1.3 从 Token 中提取 user_id 和其他声明
- [x] 3.2 实现 JwtAuthenticationFilter，拦截请求进行认证（OncePerRequestFilter）
  - [x] 3.2.1 从 Authorization Header 中解析 Bearer Token
  - [x] 3.2.2 验证 Token 有效性，设置 SecurityContext
  - [x] 3.2.3 处理 Token 过期、无效等异常
- [x] 3.3 实现 SecurityConfig，配置 OAuth 登录端点和公开路径（@Configuration）
  - [x] 3.3.1 配置 HttpSecurity：@EnableWebSecurity
  - [x] 3.3.2 配置公开路径：/login、/callback、/health 等
  - [x] 3.3.3 注册 JwtAuthenticationFilter
  - [x] 3.3.4 配置 CORS 和防 CSRF
- [ ] 3.4 实现 FeishuOAuthService，处理 OAuth 流程（使用 lark-java SDK）
  - [ ] 3.4.1 生成飞书 OAuth 授权 URL
  - [ ] 3.4.2 使用授权码交换 access_token
  - [ ] 3.4.3 获取登录用户信息（user_id、name 等）
  - [ ] 3.4.4 缓存 access_token 到 Redis（带过期时间）
- [ ] 3.5 实现 AuthController，提供 REST API
  - [ ] 3.5.1 POST /login - 生成 OAuth 授权 URL
  - [ ] 3.5.2 GET /callback - OAuth 回调处理，返回 JWT Token
  - [ ] 3.5.3 POST /logout - 登出（可选，清理 Token）
- [ ] 3.6 编写认证模块的单元测试
  - [ ] 3.6.1 测试 JWT Token 生成和验证
  - [ ] 3.6.2 测试 OAuth 流程
  - [ ] 3.6.3 测试权限校验

## 4. 后端飞书长连接集成模块（Phase 1）

- [ ] 4.1 实现 FeishuTokenService，管理 tenant_access_token 并缓存到 Redis（使用 lark-java SDK）
  - [ ] 4.1.1 初始化 Lark Client
  - [ ] 4.1.2 获取 tenant_access_token（App 访问令牌）
  - [ ] 4.1.3 缓存到 Redis，设置 TTL（2 小时）
  - [ ] 4.1.4 Token 过期时自动刷新
- [ ] 4.2 实现 FeishuMessageParser，解析飞书 WebSocket 事件
  - [ ] 4.2.1 解析消息事件（Message_Received 等）
  - [ ] 4.2.2 提取消息内容、发送者、会话信息
  - [ ] 4.2.3 支持文本、图片、语音等消息类型
- [ ] 4.3 实现 FeishuMessageSender，通过飞书 IM API 发送回复
  - [ ] 4.3.1 使用 lark-java SDK 发送文本消息
  - [ ] 4.3.2 处理发送失败和重试
- [ ] 4.4 实现 FeishuWSConnectionManager，管理多个 WebSocket 连接（使用 lark-java 中的 WSClient）
  - [ ] 4.4.1 createConnection(botId, appId, appSecret) - 异步创建 WSClient
  - [ ] 4.4.2 重试策略：初始立即尝试（0ms），2秒、5秒各重试一次（共3次）
  - [ ] 4.4.3 使用 @Async 和 CompletableFuture 实现异步创建
  - [ ] 4.4.4 连接失败3次后标记 Bot 状态为 FAILED，记录错误日志
  - [ ] 4.4.5 closeConnection(botId) - 正常关闭连接
  - [ ] 4.4.6 getConnection(botId) - 获取已连接的 Client
  - [ ] 4.4.7 connectionMap 存储所有活跃连接（Map<String, WSClient>）
- [ ] 4.5 实现 FeishuEventHandler，处理飞书事件回调
  - [ ] 4.5.1 快速验证消息签名并返回 200（在 3 秒内完成）
  - [ ] 4.5.2 事件消息异步入队到 Redis 消息队列处理
- [ ] 4.6 实现 BotAccountService，提供机器人账户的 CRUD 操作（@Service）
  - [ ] 4.6.1 使用 BotAccountMapper（MyBatis-Plus）进行数据库操作
  - [ ] 4.6.2 支持 bot_type 和 bot_config（JSON 存储在数据库）
  - [ ] 4.6.3 AppSecret 使用 MD5 加密存储（工具类 MD5Util）
  - [ ] 4.6.4 创建 Bot 时自动调用 FeishuWSConnectionManager.createConnection()
  - [ ] 4.6.5 删除 Bot 时自动调用 FeishuWSConnectionManager.closeConnection()
  - [ ] 4.6.6 getBotsWithStatus() 返回 Bot 列表及其当前连接状态（INIT、CONNECTING、CONNECTED、FAILED）
- [ ] 4.7 实现 BotAccountController，提供 REST API 接口（@RestController）
  - [ ] 4.7.1 GET /bots - 获取 Bot 列表（包括连接状态）
  - [ ] 4.7.2 GET /bots/{id} - 获取单个 Bot 详情
  - [ ] 4.7.3 POST /bots - 创建新 Bot
  - [ ] 4.7.4 PUT /bots/{id} - 编辑 Bot
  - [ ] 4.7.5 DELETE /bots/{id} - 删除 Bot
  - [ ] 4.7.6 GET /bots/available-apps - 获取所有启用的 AiApp 列表
  - [ ] 4.7.7 GET /bots/{id}/status - 查询单个 Bot 的连接状态（支持前端轮询）
- [ ] 4.8 编写飞书集成的单元测试
  - [ ] 4.8.1 测试连接创建和重试逻辑
  - [ ] 4.8.2 测试消息解析
  - [ ] 4.8.3 测试消息发送

## 5. 后端 AI 应用集成模块

- [ ] 5.1 实现 AiAppService，提供应用账户的 CRUD 操作
  - [ ] 5.1.1 使用 AiAppMapper（MyBatis-Plus）进行数据库操作
  - [ ] 5.1.2 支持 app_type（DIFY、OPENAI 等）和 app_config（JSON 存储）
  - [ ] 5.1.3 支持应用启用/禁用
- [ ] 5.2 实现 DifyAppAdapter，将 Dify 特定操作适配到通用 AiApp 接口
  - [ ] 5.2.1 Adapter 模式实现，支持多平台扩展
  - [ ] 5.2.2 从 app_config 中解析 Dify API URL、API Key、App ID
- [ ] 5.3 实现 DifyChatflowClient，支持 Chatflow API
  - [ ] 5.3.1 调用 Dify Chatflow 同步接口
  - [ ] 5.3.2 处理请求超时（可配置，建议 30 秒）
  - [ ] 5.3.3 返回 AI 响应内容
- [ ] 5.4 实现 DifyWorkflowClient，支持 Workflow API
  - [ ] 5.4.1 调用 Dify Workflow 接口
  - [ ] 5.4.2 支持流式和非流式模式
  - [ ] 5.4.3 错误处理和超时管理
- [ ] 5.5 实现 AiAppController，提供 REST API 接口
  - [ ] 5.5.1 GET /apps - 获取应用列表（支持分页、筛选）
  - [ ] 5.5.2 GET /apps/{id} - 获取应用详情
  - [ ] 5.5.3 POST /apps - 创建应用
  - [ ] 5.5.4 PUT /apps/{id} - 编辑应用
  - [ ] 5.5.5 DELETE /apps/{id} - 删除应用
  - [ ] 5.5.6 PUT /apps/{id}/disable - 禁用应用（返回受影响的 Bot 列表）
  - [ ] 5.5.7 GET /apps/{id}/affected-bots - 查询受影响的机器人列表
- [ ] 5.6 编写 AI 应用客户端的单元测试

## 6. 后端绑定关系管理模块（Phase 1）

- [ ] 6.1 实现 SubscriptionService，提供绑定关系的 CRUD 操作
  - [ ] 6.1.1 支持创建和更新 subscription（包括 session_timeout_minutes 和 show_error_to_user 配置）
  - [ ] 6.1.2 提供查询接口获取绑定关系及其配置
  - [ ] 6.1.3 支持按机器人查询应用，按应用查询受影响的机器人
- [ ] 6.2 实现 SubscriptionController，提供 REST API
  - [ ] 6.2.1 POST /subscriptions - 创建绑定关系（包括 session_timeout_minutes 和 show_error_to_user）
  - [ ] 6.2.2 PUT /subscriptions/{id} - 更新绑定关系配置
  - [ ] 6.2.3 GET /subscriptions/{bot_id} - 查询机器人的绑定关系
  - [ ] 6.2.4 GET /subscriptions/app/{app_id}/affected-bots - 查询禁用应用时的受影响机器人列表

## 7. 后端消息路由和处理模块（Phase 1）

- [ ] 7.1 实现 SessionStateService，管理会话状态（Redis 存储）
  - [ ] 7.1.1 会话 Key 格式：session:{user_id}:{bot_account_id}
  - [ ] 7.1.2 会话 TTL 由 subscription.session_timeout_minutes 决定（默认 120 分钟）
  - [ ] 7.1.3 会话数据结构：conversation_id、context、metadata、timestamps（JSON 序列化存储）
  - [ ] 7.1.4 支持会话创建、查询、更新、延期、过期操作（使用 RedisTemplate）
- [ ] 7.2 实现 MessageRouterService，根据 subscription 关系路由消息
  - [ ] 7.2.1 根据 bot_account_id 查询 Subscription
  - [ ] 7.2.2 验证应用状态（enabled）
  - [ ] 7.2.3 返回路由目标（ai_app_id、配置等）
- [ ] 7.3 实现消息校验和错误处理
  - [ ] 7.3.1 检查无绑定关系的情况
  - [ ] 7.3.2 检查禁用应用的情况
- [ ] 7.4 实现 MessageHandlerService，异步处理消息（@Service 配合 @Async）
  - [ ] 7.4.1 创建消息任务队列（使用 Redis Queue 或 Java BlockingQueue）
  - [ ] 7.4.2 从 subscription 查询配置（session_timeout_minutes、show_error_to_user）
  - [ ] 7.4.3 根据 show_error_to_user 决定错误消息内容
  - [ ] 7.4.4 根据 session_timeout_minutes 设置 Redis 会话 TTL
  - [ ] 7.4.5 @Async 异步调用 Dify API（使用 RestTemplate 或 OkHttp）
  - [ ] 7.4.6 @Async 异步发送回复到飞书
  - [ ] 7.4.7 @Async 异步记录日志到数据库
- [ ] 7.5 实现完整消息流程
  - [ ] 7.5.1 WebSocket 接收 → FeishuEventHandler 验证
  - [ ] 7.5.2 异步入队 → MessageRouterService 路由
  - [ ] 7.5.3 MessageHandlerService 并发处理
  - [ ] 7.5.4 调用 Dify → 发送回复 → 记录日志
- [ ] 7.6 实现 MessageLogService，记录成功/失败日志
  - [ ] 7.6.1 使用 MessageLogMapper 写入数据库
  - [ ] 7.6.2 记录详细的错误信息
  - [ ] 7.6.3 支持日志查询和统计
- [ ] 7.7 添加错误处理机制
  - [ ] 7.7.1 捕获 Dify API 调用异常（超时、服务不可用等）
  - [ ] 7.7.2 根据 show_error_to_user 配置返回错误消息
  - [ ] 7.7.3 错误消息返回给飞书用户
- [ ] 7.8 编写消息处理流程的单元测试

## 8. 后端消息日志 API 模块（Phase 1）

- [ ] 8.1 实现 MessageLogService，提供查询和统计方法
  - [ ] 8.1.1 使用 MessageLogMapper（MyBatis-Plus）进行查询
  - [ ] 8.1.2 支持分页、筛选（bot_id、app_id、status、date range）
  - [ ] 8.1.3 支持搜索（消息内容关键词）
  - [ ] 8.1.4 统计方法：总消息数、成功率、失败率等
- [ ] 8.2 实现 MessageLogController，提供 REST API
  - [ ] 8.2.1 GET /logs - 获取消息日志（分页、筛选）
  - [ ] 8.2.2 GET /logs/{id} - 获取日志详情
  - [ ] 8.2.3 GET /logs/stats - 统计信息（总数、成功率等）
  - [ ] 8.2.4 GET /logs/export - 导出日志（可选，返回 CSV）
- [ ] 8.3 实现日志清理任务（@Scheduled 定时任务）
  - [ ] 8.3.1 定时删除 30 天前的旧日志
  - [ ] 8.3.2 当日志记录超过 10 万条时触发清理
  - [ ] 8.3.3 使用 @Scheduled(cron = "0 0 2 * * *") 每天凌晨 2 点执行
- [ ] 8.4 编写日志服务的单元测试

## 9. 后端故障处理模块（Phase 2 - 后期实现）

- [ ] 9.1 实现运行时连接监测和自动恢复
  - [ ] 9.1.1 心跳机制（每 30 秒一次）
  - [ ] 9.1.2 自动重连逻辑（指数退避 [1s, 10s, 30s]）
  - [ ] 9.1.3 连接状态机管理
  - [ ] 9.1.4 关闭 DISCONNECTED 状态时的告警
- [ ] 9.2 实现消息失败重试和死信队列
  - [ ] 9.2.1 Dify 调用失败重试 [5分钟, 15分钟]
  - [ ] 9.2.2 回复发送失败重试 [2分钟, 5分钟]
  - [ ] 9.2.3 失败消息移入死信队列表
  - [ ] 9.2.4 实现死信队列管理 API
  - [ ] 9.2.5 支持人工重试和 30 天自动清理
- [ ] 9.3 添加监控和告警
  - [ ] 9.3.1 连接可用性监控
  - [ ] 9.3.2 消息处理失败率监控
  - [ ] 9.3.3 死信队列积压监控
- [ ] 9.4 编写故障处理的单元测试

## 10. 前端基础模块

- [x] 10.1 初始化 React 项目（Vite + React 18 + TypeScript）
- [x] 10.2 配置 Vite 代理，转发 API 请求到后端
- [x] 10.3 集成 Ant Design 和样式主题（简约高级风格）
- [x] 10.4 配置 React Router v6，实现路由和登录守卫
- [x] 10.5 配置 Zustand 状态管理 stores（auth、theme、bot、aiApp、subscription）
- [x] 10.6 创建 Axios 实例，添加请求拦截器（自动附加 JWT Token）和响应拦截器（401 处理）
- [x] 10.7 创建公共组件：Layout（布局）、Sidebar（侧边栏）、Header（顶部栏）、MenuIcons（自定义菜单图标）
- [x] 10.8 创建公共 hooks：useRequest、useLocalStorage、useTheme（深色/浅色主题）

## 11. 前端认证页面

- [x] 11.1 创建 Login 页面
  - [x] 11.1.1 简约高级的卡片布局，品牌色亮点
  - [x] 11.1.2 飞书登录按钮（扫码登录）
  - [x] 11.1.3 登录 loading 和错误提示
- [x] 11.2 实现 OAuth 回调处理和 JWT 存储
- [x] 11.3 创建 Header 组件，显示用户信息和退出按钮
- [x] 11.4 为需要认证的页面添加路由守卫

## 12. 前端机器人管理页面

- [x] 12.1 创建 BotList 页面
  - [x] 12.1.1 机器人表格展示
  - [x] 12.1.2 连接状态展示（使用 Badge + icon，INIT、CONNECTING、CONNECTED、FAILED、DISCONNECTED）
  - [x] 12.1.3 创建、编辑、删除按钮
  - [x] 12.1.4 实时轮询连接状态（每 2 秒查询一次）

- [x] 12.2 创建 BotForm Modal 组件（新增/编辑机器人）
  - [x] 12.2.1 机器人名称输入
  - [x] 12.2.2 AppID 和 AppSecret 输入（前端 MD5 加密）
  - [x] 12.2.3 AI 应用下拉选择（必填）
  - [x] 12.2.4 会话超时时间输入（范围 5-1440 分钟，默认 120）
  - [x] 12.2.5 错误信息展示切换开关（默认关闭）
  - [x] 12.2.6 连接状态实时展示（CONNECTING 时显示加载态）

- [x] 12.3 表单验证（必填、AppID 格式、会话时间范围）
- [ ] 12.4 对接后端 API（GET /bots、POST /bots、PUT /bots/{id}、DELETE /bots/{id}）
- [x] 12.5 实现流畅的加载和错误处理

## 13. 前端 AI 应用管理页面

- [x] 13.1 创建 AiAppList 页面
  - [x] 13.1.1 应用表格展示
  - [x] 13.1.2 支持按 app_type 筛选
  - [x] 13.1.3 启用/禁用状态展示
  - [x] 13.1.4 创建、编辑、删除按钮

- [x] 13.2 创建 AiAppForm Modal 组件（新增/编辑应用）
  - [x] 13.2.1 应用类型下拉选择
  - [x] 13.2.2 应用配置动态表单（根据 app_type 显示不同字段）
  - [x] 13.2.3 启用/禁用切换

- [x] 13.3 禁用应用确认对话
  - [x] 13.3.1 显示受影响的机器人列表
  - [x] 13.3.2 需要管理员确认
  - [x] 13.3.3 对接后端接口查询受影响机器人

- [ ] 13.4 对接后端 API（GET /apps、POST /apps、PUT /apps/{id}、DELETE /apps/{id}）

## 14. 前端仪表盘页面

- [x] 14.1 创建 Dashboard 页面
  - [x] 14.1.1 统计卡片：机器人总数、应用总数、今日消息数、连接可用性
  - [x] 14.1.2 连接状态展示（状态分布）
  - [x] 14.1.3 今日消息趋势图表（area chart，显示总消息、成功、失败）
  - [x] 14.1.4 系统健康度展示（连接成功率、消息处理成功率、API 可用性）
  - [x] 14.1.5 最近告警展示（timeline）
  - [x] 14.1.6 最近消息列表（快速预览，含分页）

## 15. 前端消息日志页面

- [x] 15.1 创建 MessageLog 页面
  - [x] 15.1.1 分页表格展示消息日志
  - [x] 15.1.2 支持筛选：机器人、应用、状态、日期范围
  - [x] 15.1.3 支持搜索：关键词搜索消息内容
  - [x] 15.1.4 日志详情 Drawer，展示完整请求和响应（含错误高亮显示）
  - [x] 15.1.5 统计卡片：总消息数、成功数、失败数、成功率

- [ ] 15.2 对接后端 API（GET /logs，支持分页和筛选）
- [x] 15.3 支持深色/浅色主题下的表格展示

## 16. 前端死信队列管理页面（Phase 2）

- [ ] 16.1 创建 DeadLetterQueue 页面
  - [ ] 16.1.1 失败消息列表
  - [ ] 16.1.2 显示失败原因和失败时间
  - [ ] 16.1.3 支持手动重试
  - [ ] 16.1.4 支持删除失败消息

## 17. 前端样式和交互优化

- [x] 17.1 实现深色/浅色主题切换（Ant Design 主题定制 + localStorage 持久化）
- [x] 17.2 全局加载状态管理
- [x] 17.3 全局错误和成功提示
- [x] 17.4 流畅的页面过渡和动画
- [x] 17.5 响应式设计（支持各种屏幕尺寸）
- [x] 17.6 自定义 Logo 设计（SVG 自定义品牌图标）
- [x] 17.7 菜单图标设计（仪表盘、机器人、应用、消息日志）

## 18. 集成测试（Phase 1）

- [ ] 18.1 使用真实飞书应用测试 OAuth 登录流程
- [ ] 18.2 测试长连接建立和连接管理
- [ ] 18.3 测试完整消息流程（飞书 → BuildMyBridge → Dify → 飞书）
- [ ] 18.4 测试不同的绑定场景（无绑定、应用禁用等）
- [ ] 18.5 测试禁用应用时的影响检查和确认流程
- [ ] 18.6 测试错误处理（应用超时、绑定缺失等）
- [ ] 18.7 测试错误信息展示配置的生效

## 19. 集成测试 Phase 2（Phase 2 - 后期实现）

- [ ] 19.1 测试长连接断开和自动重连
- [ ] 19.2 测试消息处理失败和重试机制
- [ ] 19.3 测试死信队列的处理和清理

## 20. 文档和部署（Phase 1）

- [ ] 20.1 编写 README.md，包含项目介绍和部署说明
- [ ] 20.2 创建 docker-compose.yml，包含 MySQL、Redis、后端、前端、Nginx
- [ ] 20.3 创建 .env.example，列出所有环境变量
- [ ] 20.4 编写 API 接口文档（可选：使用 Swagger/OpenAPI）
- [ ] 20.5 编写飞书长连接配置指南