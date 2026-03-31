# BuildMyBridge 技术栈详细说明

## 后端技术栈

### 核心框架
- **Spring Boot 3.x** - Web 应用框架
  - spring-boot-starter-web：Spring MVC Web 框架
  - spring-boot-starter-security：Spring Security 认证授权
  - spring-boot-starter-data-redis：Redis 集成
  - spring-boot-starter-validation：参数验证

### 数据库 & ORM
- **MySQL 8.0+** - 关系型数据库（InnoDB 引擎）
- **MyBatis-Plus 3.x** - ORM 框架，简化数据库操作
  - 特性：BaseMapper、CRUD 操作、分页、链式查询
  - mapper-locations: `classpath:mapper/**/*.xml`
  - type-aliases-package: `com.buildmybridge.entity`
- **Lombok** - 代码生成，简化 getter/setter/constructor
  - @Data、@Builder、@NoArgsConstructor 等注解

### 缓存 & 消息
- **Redis 7.x** - 分布式缓存 & 消息队列
  - token 缓存（2 小时 TTL）
  - 会话状态存储（可配置 TTL）
  - 简单消息队列（Phase 1）
- **Jedis 或 Lettuce** - Redis 客户端（通过 spring-boot-starter-data-redis 自动集成）

### 认证 & 授权
- **Spring Security** - 认证授权框架
  - JWT Token 生成和验证
  - JwtAuthenticationFilter 请求拦截
  - SecurityConfig 安全配置
- **JWT (io.jsonwebtoken:jjwt)** - JSON Web Token
  - Token 包含：user_id、username、exp、iat 等声明

### 飞书集成
- **lark-java** - 飞书官方 Java SDK
  - WSClient WebSocket 连接
  - API 调用（获取 token、发送消息等）
  - 事件监听

### HTTP 客户端
- **RestTemplate** 或 **OkHttp** - HTTP 请求客户端
  - 调用 Dify API
  - 调用飞书 API

### JSON 处理
- **Jackson** - JSON 序列化/反序列化
  - 自动配置在 Spring Boot 中
  - 用于 Redis 序列化、DTO 转换等

### 工具类库
- **commons-lang3** - 常用工具（String、Collection 等）
- **commons-codec** - MD5、SHA 等编码工具
  - `org.apache.commons.codec.digest.DigestUtils.md5Hex()`

### 日志
- **SLF4J + Logback** - 日志框架
  - 自动配置在 Spring Boot 中
  - 日志级别：DEBUG、INFO、WARN、ERROR
  - 日志输出：控制台 + 文件

### 构建工具
- **Maven 3.8+** - 项目构建工具
- **Java 17+** - 编程语言版本

## 前端技术栈

### 核心框架
- **React 18** - UI 框架
- **TypeScript** - 类型安全的 JavaScript
- **Vite** - 快速构建工具

### 状态管理
- **Zustand** - 轻量级状态管理库
  - stores/auth.ts - 认证状态
  - stores/theme.ts - 主题状态

### 路由
- **React Router v6** - 前端路由
  - ProtectedRoute - 路由守卫

### UI 组件库
- **Ant Design 5.x** - 企业级 UI 组件库
  - Button、Input、Modal、Table、Form 等
  - 主题定制（深色/浅色）

### HTTP 客户端
- **Axios** - HTTP 请求库
  - 请求拦截器：自动添加 JWT Token
  - 响应拦截器：401 处理

### 工具库
- **crypto-js** 或 **md5** - MD5 加密
  - 前端 AppSecret 加密

### 数据可视化
- **Recharts** - React 图表库
  - Area Chart、Line Chart 等

## 数据库设计

### 表结构（MyBatis-Plus Entity）

```sql
-- 用户表
CREATE TABLE `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(255) NOT NULL UNIQUE,
  `feishu_open_id` VARCHAR(255),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 机器人账户表
CREATE TABLE `bot_account` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `bot_type` VARCHAR(50) NOT NULL, -- FEISHU, DINGDING, WECHAT
  `bot_name` VARCHAR(255) NOT NULL,
  `bot_config` JSON NOT NULL, -- {"app_id": "...", "app_secret": "..."}
  `enabled` BOOLEAN DEFAULT true,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- AI 应用表
CREATE TABLE `ai_app` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `app_type` VARCHAR(50) NOT NULL, -- DIFY, OPENAI, ANTHROPIC
  `app_name` VARCHAR(255) NOT NULL,
  `app_config` JSON NOT NULL, -- {"api_url": "...", "api_key": "..."}
  `enabled` BOOLEAN DEFAULT true,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 绑定关系表
CREATE TABLE `subscription` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `bot_account_id` BIGINT NOT NULL,
  `ai_app_id` BIGINT NOT NULL,
  `session_timeout_minutes` INT DEFAULT 120, -- 5-1440
  `show_error_to_user` BOOLEAN DEFAULT false,
  `routing_rules` JSON, -- Phase 2 使用
  `enabled` BOOLEAN DEFAULT true,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`bot_account_id`) REFERENCES `bot_account`(`id`),
  FOREIGN KEY (`ai_app_id`) REFERENCES `ai_app`(`id`)
);

-- 消息日志表
CREATE TABLE `message_log` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `bot_account_id` BIGINT NOT NULL,
  `ai_app_id` BIGINT NOT NULL,
  `message_content` TEXT,
  `response_content` TEXT,
  `status` VARCHAR(50), -- success, error
  `error_message` TEXT,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX (`bot_account_id`),
  INDEX (`ai_app_id`),
  INDEX (`created_at`)
);
```

## 配置文件示例

### application.yml
```yaml
spring:
  application:
    name: buildmybridge

  datasource:
    url: jdbc:mysql://localhost:3306/buildmybridge?useSSL=false&serverTimezone=UTC
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver

  redis:
    host: localhost
    port: 6379
    password:
    timeout: 2000ms
    jedis:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

  jackson:
    serialization:
      write-dates-as-timestamps: false

mybatis-plus:
  mapper-locations: classpath:mapper/**/*.xml
  type-aliases-package: com.buildmybridge.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl

logging:
  level:
    root: INFO
    com.buildmybridge: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

## 关键实现细节

### JWT Token 格式
```
header.payload.signature

payload 包含：
{
  "user_id": 123,
  "username": "user_name",
  "exp": 1234567890,
  "iat": 1234567890
}
```

### Redis Key 格式
```
token:{user_id} -> JWT Token（TTL: 2小时）
session:{user_id}:{bot_account_id} -> 会话状态（TTL: 可配置）
```

### 错误响应格式
```json
{
  "code": "ERROR_CODE",
  "message": "错误描述",
  "data": null
}
```

### 成功响应格式
```json
{
  "code": "SUCCESS",
  "message": "请求成功",
  "data": {
    // 具体数据
  }
}
```

## 依赖管理

### pom.xml 关键依赖
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
  <version>3.x.x</version>
</dependency>

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<dependency>
  <groupId>com.baomidou</groupId>
  <artifactId>mybatis-plus-spring-boot-starter</artifactId>
  <version>3.5.x</version>
</dependency>

<dependency>
  <groupId>com.lark</groupId>
  <artifactId>lark</artifactId>
  <version>latest</version>
</dependency>

<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt</artifactId>
  <version>0.11.x</version>
</dependency>

<dependency>
  <groupId>org.projectlombok</groupId>
  <artifactId>lombok</artifactId>
  <optional>true</optional>
</dependency>
```

## 性能优化建议

1. **数据库查询优化**
   - 创建适当的索引（bot_account_id、ai_app_id、created_at）
   - 使用 MyBatis-Plus 的分页功能

2. **缓存策略**
   - Token 缓存（2 小时）
   - 会话状态缓存（可配置）
   - Dify API 响应缓存（可选）

3. **异步处理**
   - 消息处理异步化（@Async）
   - 日志写入异步化
   - 使用 Spring Task 线程池

4. **连接池管理**
   - MySQL 连接池：HikariCP（默认）
   - Redis 连接池：Lettuce（默认）

## 安全考虑

1. **密钥管理**
   - JWT 密钥存储在环境变量或配置文件（加密）
   - AppSecret MD5 加密存储

2. **请求验证**
   - 所有 API 需要 JWT Token
   - 飞书事件消息签名验证
   - 参数校验（@Valid）

3. **HTTPS & CORS**
   - 生产环境启用 HTTPS
   - 配置 CORS 白名单

4. **SQL 注入防护**
   - 使用 MyBatis-Plus 参数化查询
   - 避免字符串拼接 SQL
