# BuildMyBridge 后端项目

## 项目结构

```
backend/
├── pom.xml                          # Maven 配置文件
├── src/main/
│   ├── java/com/buildmybridge/
│   │   ├── BuildMyBridgeApplication.java    # 主程序入口
│   │   ├── controller/              # REST API 控制层
│   │   │   ├── HealthController.java
│   │   │   ├── AuthController.java
│   │   │   ├── BotAccountController.java
│   │   │   ├── AiAppController.java
│   │   │   └── ...
│   │   ├── service/                 # 业务逻辑层
│   │   │   ├── UserService.java
│   │   │   ├── BotAccountService.java
│   │   │   ├── AiAppService.java
│   │   │   ├── SubscriptionService.java
│   │   │   ├── MessageLogService.java
│   │   │   ├── JwtTokenProvider.java
│   │   │   ├── FeishuOAuthService.java
│   │   │   ├── FeishuWSConnectionManager.java
│   │   │   └── ...
│   │   ├── mapper/                  # 数据访问层（MyBatis-Plus）
│   │   │   ├── UserMapper.java
│   │   │   ├── BotAccountMapper.java
│   │   │   ├── AiAppMapper.java
│   │   │   ├── SubscriptionMapper.java
│   │   │   ├── MessageLogMapper.java
│   │   │   └── ...
│   │   ├── entity/                  # 数据模型
│   │   │   ├── User.java
│   │   │   ├── BotAccount.java
│   │   │   ├── AiApp.java
│   │   │   ├── Subscription.java
│   │   │   ├── MessageLog.java
│   │   │   └── ...
│   │   ├── dto/                     # 数据传输对象
│   │   │   ├── ApiResponse.java
│   │   │   ├── AuthDto.java
│   │   │   ├── BotAccountDto.java
│   │   │   ├── AiAppDto.java
│   │   │   ├── SubscriptionDto.java
│   │   │   └── ...
│   │   ├── config/                  # 配置类
│   │   │   ├── RedisConfig.java
│   │   │   ├── SecurityConfig.java
│   │   │   ├── AsyncConfig.java
│   │   │   └── ...
│   │   ├── security/                # 认证授权
│   │   │   ├── JwtTokenProvider.java
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   └── ...
│   │   ├── exception/               # 异常处理
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── BusinessException.java
│   │   │   └── ...
│   │   ├── adapter/                 # 适配器（支持多平台扩展）
│   │   │   ├── BotAdapter.java
│   │   │   ├── AiAppAdapter.java
│   │   │   ├── FeishuAdapter.java
│   │   │   ├── DifyAdapter.java
│   │   │   └── ...
│   │   └── utils/                   # 工具类
│   │       ├── MD5Util.java
│   │       └── ...
│   └── resources/
│       ├── application.yml          # 应用配置文件
│       ├── application-dev.yml      # 开发环境配置
│       ├── application-prod.yml     # 生产环境配置
│       ├── db/
│       │   └── schema.sql           # 数据库建表脚本
│       ├── mapper/                  # MyBatis XML 映射文件（如需要）
│       └── logback.xml              # 日志配置（可选）
└── README.md                        # 项目说明

```

## 环境要求

- Java 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 7.x
- Spring Boot 3.x

## 快速开始

### 1. 安装依赖

```bash
cd backend
mvn clean install
```

### 2. 配置数据库

在 MySQL 中执行建表脚本：
```bash
mysql -u root -p < src/main/resources/db/schema.sql
```

或者通过 MySQL 客户端执行 `src/main/resources/db/schema.sql` 中的所有 SQL。

### 3. 配置环境变量（可选）

```bash
# 飞书配置
export FEISHU_APP_ID=your_app_id
export FEISHU_APP_SECRET=your_app_secret

# JWT 配置
export JWT_SECRET=your_jwt_secret_key
```

### 4. 启动应用

```bash
mvn spring-boot:run

# 或者
java -jar target/buildmybridge-backend-0.1.0.jar
```

应用将在 `http://localhost:8080/api` 启动。

### 5. 验证启动

```bash
curl http://localhost:8080/api/health
```

响应示例：
```json
{
  "code": "SUCCESS",
  "message": "请求成功",
  "data": "BuildMyBridge Backend is running"
}
```

### 6. 访问 Knife4j API 文档

应用启动后，可通过以下 URL 访问 API 文档：

| UI 版本 | URL | 说明 |
|--------|-----|------|
| **Knife4j 增强版（推荐）** | http://localhost:8080/doc.html | 功能更强大，支持在线调试 |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | 官方标准 UI |
| **OpenAPI JSON** | http://localhost:8080/v3/api-docs | API 定义 JSON 格式 |

Knife4j 提供：
- 📚 完整的 API 文档
- 🔍 接口分组展示
- 🧪 在线接口调试
- 🔑 JWT Token 认证配置
- 📥 接口导出功能

详见 [KNIFE4J_GUIDE.md](./KNIFE4J_GUIDE.md)。

## API 文档

### Knife4j 在线文档

启动应用后，访问以下地址查看和调试 API：

**推荐：http://localhost:8080/doc.html** (Knife4j 增强 UI)

Knife4j 功能：
- 📚 完整的接口文档（自动从代码注解生成）
- 🔍 按模块分类的接口导航
- 🧪 在线接口调试（Try it out）
- 🔑 JWT Token 认证配置
- 📥 导出接口文档（HTML/Markdown）
- 🌙 深色主题支持

详细使用指南见 [KNIFE4J_GUIDE.md](./KNIFE4J_GUIDE.md)。

### 接口分类

| 分组 | 路由前缀 | 说明 |
|------|--------|------|
| 健康检查 | /api/health | 系统状态检查 |
| 认证模块 | /api/auth/** | OAuth 登录、Token 管理 |
| 机器人管理 | /api/bots/** | Bot CRUD 操作 |
| 应用管理 | /api/apps/** | App CRUD 操作 |
| 消息日志 | /api/logs/** | 消息查询统计 |

### 公开接口

#### 1. 健康检查
```http
GET /api/health
```
无需认证，可验证服务是否正常运行。

### 受保护接口

所有其他接口都需要在 `Authorization` 头提供有效的 JWT Token：

```http
GET /api/bots
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

在 Knife4j 的 **Authorize** 功能中配置 Token 后，所有后续请求会自动添加认证头。

## 技术栈

### 框架
- Spring Boot 3.x
- Spring Security
- Spring Data Redis

### ORM
- MyBatis-Plus 3.x

### 数据库
- MySQL 8.0+
- Redis 7.x

### 认证
- JWT (io.jsonwebtoken)

### API 文档
- Knife4j 4.4.0（增强的 Swagger UI）
- OpenAPI 3.0

### 集成
- lark-java (飞书 SDK)
- Lombok (代码生成)

### 构建工具
- Maven 3.8+
- Java 17+

## 配置说明

### application.yml 主要配置项

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/buildmybridge
    username: root
    password: root
  redis:
    host: localhost
    port: 6379

jwt:
  secret: your-secret-key
  expiration: 86400000  # 24 小时

feishu:
  app-id: your_app_id
  app-secret: your_app_secret
```

## 开发指南

### 创建新的 Service

1. 创建实体类（Entity）
2. 创建 Mapper 接口
3. 创建 DTO 类（可选）
4. 创建 Service 类
5. 创建 Controller 类

### 添加新的 API 端点

```java
@RestController
@RequestMapping("/api/your-resource")
public class YourController {

    @Autowired
    private YourService service;

    @GetMapping
    public ApiResponse<?> list() {
        return ApiResponse.success(service.list());
    }

    @PostMapping
    public ApiResponse<?> create(@RequestBody YourDto dto) {
        return ApiResponse.success(service.create(dto));
    }
}
```

### 处理业务异常

```java
if (condition) {
    throw new BusinessException("ERROR_CODE", "错误信息");
}
```

## 常见问题

### Q: 如何修改数据库连接信息？
A: 在 `application.yml` 中修改 `spring.datasource.url`、`username`、`password`。

### Q: 如何修改 Redis 连接信息？
A: 在 `application.yml` 中修改 `spring.redis.host`、`port`、`password`。

### Q: 如何在生产环境运行？
A: 使用 `mvn clean package -Pprod` 打包，然后运行 jar 文件，同时配置环境变量。

## 进度追踪

### Phase 1 - 核心功能（当前）
- [x] 项目初始化与配置
- [x] 数据库设计与建表
- [x] 基础框架（Redis、异常处理、Entity、Mapper）
- [x] 认证模块（JWT、OAuth）
- [ ] 飞书长连接集成
- [ ] AI 应用集成
- [ ] 消息处理与路由
- [ ] 消息日志 API

### Phase 2 - 可靠性与监控（待实现）
- [ ] 连接监测与自动恢复
- [ ] 消息失败重试与死信队列
- [ ] 监控与告警

## 许可证

MIT License

## 贡献指南

欢迎提交 Issue 和 PR！

## 联系方式

如有问题，请提交 Issue 或联系开发团队。
