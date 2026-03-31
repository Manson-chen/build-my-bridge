# Knife4j 文档访问被拒绝 - 故障排除

## 问题
访问 `http://localhost:8081/api/doc.html` 时收到 **403 Forbidden** 错误。

## 解决方案

### ✅ 已完成的修复

**SecurityConfig.java** 已更新，添加了以下公开路由：

```java
.requestMatchers(
    "/doc.html",
    "/doc/**",
    "/v3/api-docs",
    "/v3/api-docs/**",
    "/swagger-ui.html",
    "/swagger-ui/**",
    "/swagger-resources",
    "/swagger-resources/**",
    "/webjars/**",
    "/knife4j/**"
).permitAll()
```

### 📌 重要说明

- **Context Path**: 应用程序配置了 `context-path: /api`
- **完整访问路径**: `http://localhost:8081/api/doc.html`
- **Spring Security 匹配路径**: `/doc.html` (去掉 context-path 后)

---

## 🔍 验证步骤

### 1. 确保服务器已启动

```bash
# 检查端口是否监听
# Windows
netstat -ano | findstr :8081

# Linux/Mac
lsof -i :8081
```

### 2. 检查 SecurityConfig 是否生效

**重新编译并启动应用**（必须清理旧编译文件）：

```bash
cd D:/ai/build-my-bridge/backend
mvn clean compile spring-boot:run

# 或使用 IDE 的 Run/Debug 功能，确保使用最新的代码
```

### 3. 测试健康检查端点

先测试已配置为公开的端点是否有效：

```bash
curl http://localhost:8081/api/health
```

预期响应：
```json
{
  "code": "SUCCESS",
  "message": "请求成功",
  "data": "BuildMyBridge Backend is running"
}
```

### 4. 访问 Knife4j 文档

如果健康检查成功，访问文档：

- **Knife4j UI**: http://localhost:8081/api/doc.html
- **Swagger UI**: http://localhost:8081/api/swagger-ui.html
- **OpenAPI 3 JSON**: http://localhost:8081/api/v3/api-docs

---

## 📋 常见错误和解决方案

### 错误 1: 403 Forbidden

**原因**: Security Filter 拦截了请求

**解决方案**:
1. 检查 SecurityConfig 中的 `requestMatchers` 是否包含文档端点
2. ✅ 已完成 - 请重新编译并重启应用

### 错误 2: 404 Not Found

**原因**: 端点不存在

**可能的原因**:
- 应用程序没有启动
- knife4j 依赖没有正确引入
- Context-path 配置不正确

**解决方案**:
```bash
# 检查 pom.xml 中是否有 knife4j-openapi3-jakarta-spring-boot-starter
grep -A 3 "knife4j" pom.xml

# 检查 application.yml 的 context-path
grep -A 2 "context-path" src/main/resources/application.yml
```

### 错误 3: 页面加载但资源丢失

**原因**: 静态资源路径问题

**解决方案**:
使用浏览器开发者工具查看网络请求，检查是否有 4xx 错误的资源请求（如 CSS、JS）。

如果有，可能需要在 `WebMvcConfigurer` 中配置资源映射。

---

## 🔧 高级故障排除

### 启用 Spring Security 调试日志

在 `application.yml` 中添加：

```yaml
logging:
  level:
    org.springframework.security: DEBUG
    org.springframework.web: DEBUG
```

然后重启应用，查看控制台日志中关于请求的信息。

### 检查实际被匹配的路径

在 `SecurityConfig.java` 中添加临时日志：

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/login", "/api/auth/callback", "/api/health").permitAll()
                .requestMatchers(
                        "/doc.html",
                        "/doc/**",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/knife4j/**"
                ).permitAll()
                .anyRequest().authenticated()
        );
    return http.build();
}
```

---

## ✨ 验证配置已生效

运行以下命令后应该能访问文档：

```bash
# 1. 清理旧编译
mvn clean

# 2. 编译
mvn compile

# 3. 启动应用
mvn spring-boot:run

# 4. 测试访问（在另一个终端）
curl -I http://localhost:8081/api/doc.html

# 应该返回 200 OK
```

---

## 📞 如果问题仍未解决

请检查以下内容并提供信息：

1. Maven 编译是否成功？
2. 应用启动时是否有错误日志？
3. `curl -I http://localhost:8081/api/health` 返回什么？
4. `curl -I http://localhost:8081/api/doc.html` 返回什么状态码？
5. 浏览器开发者工具中的网络请求显示什么？

---

## 💡 其他访问方式

如果希望不使用 context-path，可以修改 `application.yml`：

```yaml
# 注释掉或删除
# server:
#   servlet:
#     context-path: /api

# 然后修改 controller 路由
@RequestMapping("/api/health")  # 添加 /api 前缀到每个 controller
```

但这需要修改所有的 controller，**不推荐**。

---

## 参考

- Spring Security: https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html
- Knife4j: https://doc.xiaominfo.com/
- SpringDoc: https://springdoc.org/
