# 飞书集成模块 - 编译和测试指南

## 🔨 编译前检查

### 1. 确保已安装正确的依赖

```bash
cd D:/ai/build-my-bridge/backend

# 清理旧的编译文件
mvn clean

# 查看依赖树，确保飞书 SDK 版本正确
mvn dependency:tree | grep lark
# 应该看到: com.larksuite.oapi:oapi-sdk:jar:2.5.3
```

### 2. 检查 JDK 版本

```bash
java -version
# 应该是 JDK 17 或更高版本
```

---

## 🏗️ 编译步骤

```bash
# 方式 1：使用 Maven 编译
cd D:/ai/build-my-bridge/backend
mvn clean compile
mvn spring-boot:run

# 方式 2：使用 IDE (IntelliJ IDEA / Eclipse)
# 1. 右键项目 → Maven → Reimport
# 2. 运行 BuildMyBridgeApplication.main()
```

---

## ⚙️ 配置 application.yml

在启动前，确保 `application.yml` 中的飞书配置：

```yaml
feishu:
  app-id: ${FEISHU_APP_ID:your-app-id-here}
  app-secret: ${FEISHU_APP_SECRET:your-app-secret-here}
  redirect-uri: http://localhost:8080/api/auth/callback

jwt:
  secret: ${JWT_SECRET:your-secret-key-change-this-in-production}
  expiration: 86400000  # 24 小时
```

### 推荐：使用环境变量

```bash
# Linux/Mac
export FEISHU_APP_ID="cli_xxx"
export FEISHU_APP_SECRET="xxx"
export JWT_SECRET="your-secret-key"

# Windows PowerShell
$env:FEISHU_APP_ID="cli_xxx"
$env:FEISHU_APP_SECRET="xxx"
$env:JWT_SECRET="your-secret-key"

# 启动应用
mvn spring-boot:run
```

---

## 🧪 测试 OAuth 流程

### 1. 获取授权 URL

```bash
curl -X GET http://localhost:8081/api/auth/login

# 返回:
{
  "code": "SUCCESS",
  "message": "请求成功",
  "data": {
    "url": "https://open.feishu.cn/open-apis/oauth2/v2/authorize?client_id=cli_xxx&..."
  }
}
```

### 2. 手动测试 OAuth 回调

由于 OAuth 流程需要用户在飞书中授权，测试时：

```bash
# 1. 在浏览器中打开授权 URL（从上面获取）
# 2. 在飞书中扫码并授权
# 3. 飞书会重定向到: http://localhost:8080/api/auth/callback?code=xxx&state=xxx
# 4. 应用处理回调，返回 JWT token

# 或者，模拟回调（需要有有效的 code）
curl -X GET "http://localhost:8081/api/auth/callback?code=mock_code&state=mock_state"

# 注意：这会失败，因为 mock_code 无效
# 返回: { "code": "AUTH_ERROR", "message": "登录失败：..." }
```

---

## 🤖 测试机器人管理 API

### 1. 创建机器人

```bash
curl -X POST http://localhost:8081/api/bots \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "botName": "测试机器人",
    "appId": "cli_xxx",
    "appSecret": "your-app-secret"
  }'

# 返回:
{
  "code": "SUCCESS",
  "message": "请求成功",
  "data": {
    "id": 1,
    "botType": "FEISHU",
    "botName": "测试机器人",
    "enabled": true,
    "createdAt": "2026-03-31T10:00:00.000Z"
  }
}
```

### 2. 获取机器人列表

```bash
curl -X GET http://localhost:8081/api/bots \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# 返回:
{
  "code": "SUCCESS",
  "message": "请求成功",
  "data": [
    {
      "id": 1,
      "botType": "FEISHU",
      "botName": "测试机器人",
      "enabled": true,
      "createdAt": "2026-03-31T10:00:00.000Z"
    }
  ]
}
```

### 3. 获取单个机器人

```bash
curl -X GET http://localhost:8081/api/bots/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 4. 更新机器人

```bash
curl -X PUT http://localhost:8081/api/bots/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "botName": "更新后的机器人名称",
    "appId": "cli_new_xxx",
    "appSecret": "new-app-secret"
  }'
```

### 5. 删除机器人

```bash
curl -X DELETE http://localhost:8081/api/bots/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 6. 切换机器人状态

```bash
curl -X PUT "http://localhost:8081/api/bots/1/status?enabled=false" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 📖 访问 Swagger 文档

启动应用后，可以访问 Knife4j 自动生成的 API 文档：

```
http://localhost:8081/api/doc.html
```

文档中包含：
- 所有 API 端点
- 请求/响应格式
- 数据模型
- JWT 认证配置

---

## 🐛 常见问题排查

### 1. 编译失败：找不到飞书 SDK

**错误**: `[ERROR] Failed to execute goal on project buildmybridge-backend: Could not find artifact com.larksuite.oapi:oapi-sdk:jar:2.5.3`

**解决**:
```bash
# 检查 Maven 设置
mvn help:describe -Ddetail=true

# 尝试强制更新依赖
mvn dependency:resolve --update-snapshots

# 清理本地仓库（如果问题持续）
rm -rf ~/.m2/repository/com/larksuite
mvn clean install
```

### 2. 启动失败：端口被占用

**错误**: `Address already in use: bind`

**解决**:
```bash
# 改变端口
# 在 application.yml 中修改：
server:
  port: 8082  # 改为其他端口

# 或者杀死现有进程 (Linux/Mac)
lsof -i :8081 | grep LISTEN | awk '{print $2}' | xargs kill -9
```

### 3. 认证失败：JWT Token 过期

**错误**: `401 Unauthorized`

**解决**:
```bash
# 重新获取授权 URL 并完成 OAuth 流程
# 获取新的 JWT token
```

### 4. 数据库连接失败

**错误**: `Access denied for user 'root'@'localhost'`

**解决**:
```bash
# 检查 MySQL 是否运行
# 检查 application.yml 中的数据库配置：
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/buildmybridge
    username: root
    password: root
```

### 5. Redis 连接失败

**错误**: `Unable to connect to Redis Server`

**解决**:
```bash
# 确保 Redis 已启动
# Windows: 运行 redis-server
# Linux: redis-server
# Docker: docker run -d -p 6379:6379 redis
```

---

## 📊 日志查看

### 启用详细日志

在 `application.yml` 中：

```yaml
logging:
  level:
    root: INFO
    com.buildmybridge: DEBUG
    org.springframework.security: DEBUG
    org.springframework.web: DEBUG
```

### 查看特定日志

```bash
# 查看飞书相关日志
mvn spring-boot:run | grep -i feishu

# 查看认证相关日志
mvn spring-boot:run | grep -i auth

# 查看完整日志（保存到文件）
mvn spring-boot:run > app.log 2>&1
tail -f app.log
```

---

## ✅ 完整测试清单

- [ ] 应用编译成功
- [ ] 应用启动成功（http://localhost:8081/api/health 返回 200）
- [ ] Knife4j 文档可访问（http://localhost:8081/api/doc.html）
- [ ] OAuth 授权 URL 可生成 (`GET /auth/login`)
- [ ] 机器人创建 API 正常（`POST /bots`）
- [ ] 机器人列表 API 正常（`GET /bots`）
- [ ] JWT 认证正常工作
- [ ] 数据库正常保存数据
- [ ] Redis 缓存正常工作

---

## 🚀 部署建议

### 开发环境
```bash
mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### 生产环境
```bash
# 打包
mvn clean package -DskipTests

# 运行
java -Dspring.profiles.active=prod \
     -DFEISHU_APP_ID=xxx \
     -DFEISHU_APP_SECRET=xxx \
     -DJWT_SECRET=your-secret-key \
     -jar target/buildmybridge-backend-0.1.0.jar
```

---

## 📞 需要帮助？

- 检查应用日志：`tail -f logs/buildmybridge.log`
- 查看 API 文档：http://localhost:8081/api/doc.html
- 检查数据库：`mysql -u root -p buildmybridge`
- 检查 Redis：`redis-cli`
