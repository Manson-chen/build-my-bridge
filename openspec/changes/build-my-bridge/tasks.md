## 1. 项目初始化

- [ ] 1.1 初始化 Spring Boot 后端项目，添加依赖（Spring Web、Spring Data JPA、MySQL、Redis、Spring Security）
- [ ] 1.2 初始化 Vue3 前端项目（Vite + Vue Router + Pinia + Axios + Element Plus）
- [ ] 1.3 创建后端目录结构（controller、service、repository、entity、adapter、config、exception、security、dto）
- [ ] 1.4 创建前端目录结构（views、components、api、stores、router、types）
- [ ] 1.5 创建 application.yml，配置数据库和 Redis 连接
- [ ] 1.6 创建 MySQL 数据库并执行建表脚本

## 2. 后端基础层

- [ ] 2.1 实现 RedisConfig，配置 RedisTemplate
- [ ] 2.2 实现 CorsConfig，配置跨域请求
- [ ] 2.3 实现 GlobalExceptionHandler，统一异常处理
- [ ] 2.4 创建实体类：User、FeishuBot、DifyApp、Binding、MessageLog
- [ ] 2.5 创建 Repository 接口（继承 JpaRepository）
- [ ] 2.6 创建 DTO 类：请求类（LoginRequest、FeishuBotRequest 等）和响应类

## 3. 后端认证模块（飞书 OAuth）

- [ ] 3.1 实现 JwtTokenProvider，生成和验证 JWT 令牌
- [ ] 3.2 实现 JwtAuthenticationFilter，拦截请求进行认证
- [ ] 3.3 实现 SecurityConfig，配置 OAuth 登录端点和公开路径
- [ ] 3.4 实现 FeishuOAuthService，处理 OAuth 流程（获取授权 URL、交换授权码、获取用户信息）
- [ ] 3.5 实现 AuthController，提供 /login 和 /callback 接口
- [ ] 3.6 编写 AuthService 和 OAuth 流程的单元测试

## 4. 后端飞书集成模块

- [ ] 4.1 实现 FeishuSignatureVerifier，验证 Webhook 签名
- [ ] 4.2 实现 FeishuMessageParser，解析 Webhook 事件（文本、图片、语音）
- [ ] 4.3 实现 FeishuMessageSender，通过飞书 IM API 发送回复
- [ ] 4.4 实现 FeishuTokenService，管理 tenant_access_token 并缓存到 Redis
- [ ] 4.5 实现 FeishuBotService，提供机器人 CRUD 操作
- [ ] 4.6 实现 FeishuBotController，提供 REST API 接口
- [ ] 4.7 编写飞书适配器的单元测试

## 5. 后端 Dify 集成模块

- [ ] 5.1 实现 DifyClient 接口，定义通用调用方式
- [ ] 5.2 实现 DifyChatflowClient，支持 Chatflow API（阻塞式和流式）
- [ ] 5.3 实现 DifyWorkflowClient，支持 Workflow API
- [ ] 5.4 实现 DifyAppService，提供 Dify 应用 CRUD 和连接测试
- [ ] 5.5 实现 DifyAppController，提供 REST API 接口
- [ ] 5.6 编写 Dify 客户端的单元测试

## 6. 后端绑定管理模块

- [ ] 6.1 实现 BindingService，提供绑定 CRUD 操作
- [ ] 6.2 实现路由逻辑：按优先级查找绑定，处理无绑定情况
- [ ] 6.3 实现 BindingController，提供 REST API 接口
- [ ] 6.4 添加 Redis 缓存，缓存绑定查询结果
- [ ] 6.5 编写 BindingService 的单元测试

## 7. 后端消息处理核心模块

- [ ] 7.1 实现 MessageHandlerService，使用 @Async 异步处理消息
- [ ] 7.2 实现完整消息流程：Webhook 接收 → 解析 → 路由 → 调用 Dify → 发送回复
- [ ] 7.3 实现 MessageLogService，记录成功/失败日志
- [ ] 7.4 实现 FeishuWebhookController，提供 /verify 和 /event 接口
- [ ] 7.5 添加错误处理，失败时向用户发送友好提示
- [ ] 7.6 编写消息处理流程的单元测试

## 8. 后端消息日志 API 模块

- [ ] 8.1 实现 MessageLogService，提供查询和统计方法
- [ ] 8.2 实现 MessageLogController，提供 /logs 和 /logs/stats 接口
- [ ] 8.3 实现日志清理任务，定时删除 30 天前的旧日志（超过 10 万条时触发）
- [ ] 8.4 编写日志服务的单元测试

## 9. 前端基础模块

- [ ] 9.1 配置 Vite 代理，转发 API 请求到后端
- [ ] 9.2 配置 Vue Router，添加登录守卫
- [ ] 9.3 配置 Pinia Store（auth、bot、difyApp、binding）
- [ ] 9.4 创建 API 客户端（Axios），添加请求拦截器自动附加 JWT Token
- [ ] 9.5 创建公共组件：Layout（布局）、Sidebar（侧边栏）、Header（顶部栏）、DataTable（数据表格）

## 10. 前端认证页面

- [ ] 10.1 创建 Login.vue 登录页面，显示飞书登录按钮
- [ ] 10.2 实现登录跳转和回调处理
- [ ] 10.3 创建 Header.vue，显示用户信息和退出按钮
- [ ] 10.4 为需要认证的页面添加路由守卫

## 11. 前端机器人管理页面

- [ ] 11.1 创建 BotList.vue，显示机器人列表表格和 CRUD 操作按钮
- [ ] 11.2 创建 BotForm.vue 弹窗，用于添加/编辑机器人
- [ ] 11.3 对接后端 API（GET/POST/PUT/DELETE）
- [ ] 11.4 添加表单验证（必填字段校验）

## 12. 前端 Dify 应用管理页面

- [ ] 12.1 创建 DifyAppList.vue，显示 Dify 应用列表
- [ ] 12.2 创建 DifyAppForm.vue 弹窗，包含应用类型下拉选择
- [ ] 12.3 对接后端 API（GET/POST/PUT/DELETE）
- [ ] 12.4 添加连接测试功能

## 13. 前端绑定管理页面

- [ ] 13.1 创建 BindingList.vue，显示绑定关系列表
- [ ] 13.2 创建 BindingForm.vue 弹窗，包含机器人和 Dify 应用下拉选择
- [ ] 13.3 对接后端 API（GET/POST/PUT/DELETE）
- [ ] 13.4 添加优先级输入和启用/禁用切换

## 14. 前端仪表盘和日志页面

- [ ] 14.1 创建 Dashboard.vue，显示统计卡片（机器人数、应用数、绑定数、今日消息数）
- [ ] 14.2 创建 MessageLog.vue，显示分页日志表格
- [ ] 14.3 添加筛选功能（机器人、状态、日期范围）
- [ ] 14.4 创建 LogDetail.vue 弹窗，查看完整日志详情

## 15. 集成测试

- [ ] 15.1 使用真实飞书应用测试 OAuth 登录流程
- [ ] 15.2 测试 Webhook URL 验证接口
- [ ] 15.3 测试完整消息流程（飞书 → BuildMyBridge → Dify → 飞书）
- [ ] 15.4 测试多 Dify 应用绑定的路由逻辑
- [ ] 15.5 测试错误处理（Dify 超时、绑定缺失）

## 16. 文档和部署

- [ ] 16.1 编写 README.md，包含项目介绍和部署说明
- [ ] 16.2 创建 docker-compose.yml，包含 MySQL、Redis、后端、前端、Nginx
- [ ] 16.3 创建 .env.example，列出所有环境变量
- [ ] 16.4 编写 API 接口文档（可选：使用 Swagger/OpenAPI）