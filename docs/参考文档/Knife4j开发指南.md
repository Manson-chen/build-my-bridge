# Knife4j 开发指南

Knife4j 是基于 Swagger/OpenAPI 的增强型 API 文档工具，用于展示和测试后端 API。

本项目使用 **Knife4j 4.4.0** + **Spring Boot 3** + **Jakarta EE**

---

## 📚 使用 OpenAPI 3 注解

### 基础 Controller 示例

```java
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {

    @PostMapping
    @Operation(summary = "创建用户", description = "创建新的用户记录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "参数错误")
    })
    public ApiResponse<User> create(@RequestBody User user) {
        return ApiResponse.success(user);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户", description = "根据 ID 获取用户信息")
    @Parameters({
        @Parameter(name = "id", description = "用户 ID", example = "123")
    })
    public ApiResponse<User> getById(@PathVariable Long id) {
        return ApiResponse.success(null);
    }
}
```

### DTO 注解示例

```java
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户信息")
public class User {
    
    @Schema(description = "用户 ID", example = "123")
    private Long id;
    
    @Schema(description = "用户名", example = "john_doe")
    private String username;
    
    @Schema(description = "邮箱", example = "john@example.com")
    private String email;
}
```

---

## 🎯 常见注解速查表

| 注解 | 位置 | 用途 | 示例 |
|-----|-----|------|------|
| `@Tag` | Controller 类 | 标记接口分组 | `@Tag(name="用户管理")` |
| `@Operation` | 方法 | 标记操作说明 | `@Operation(summary="创建用户")` |
| `@Parameter` | 参数 | 标记单个参数 | `@Parameter(name="id", description="用户ID")` |
| `@Parameters` | 方法 | 标记多个参数 | `@Parameters({...})` |
| `@RequestBody` | 参数 | 标记请求体 | `@RequestBody User user` |
| `@ApiResponse` | 方法 | 标记响应 | `@ApiResponse(responseCode="200")` |
| `@ApiResponses` | 方法 | 标记多个响应 | `@ApiResponses({...})` |
| `@Schema` | 类/字段 | 标记数据模型 | `@Schema(description="用户")` |

---

## 🌐 访问文档

### 本地开发环境
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **Knife4j UI**: http://localhost:8081/doc.html

### 通过反向代理
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **Knife4j UI**: http://localhost:8080/api/doc.html

---

## 🔧 常见配置调整

### 隐藏某个 Controller 的文档
```java
import io.swagger.v3.oas.annotations.Hidden;

@RestController
@Hidden  // 添加此注解
public class InternalController {
    // ...
}
```

### 为方法标记废弃
```java
@Deprecated
@Operation(summary = "旧接口（已废弃）")
@GetMapping("/old-endpoint")
public ApiResponse<String> oldMethod() {
    // ...
}
```

### 添加全局认证要求
在 `Knife4jConfig.java` 中已配置 JWT Bearer 认证，所有接口默认需要提供 Token。

### 自定义接口分组
修改 `application.yml` 中的 `springdoc.group-configs`:
```yaml
springdoc:
  group-configs:
    - group: '用户管理'
      paths-to-match: '/api/users/**'
      packages-to-scan: com.buildmybridge.controller.user
    
    - group: '机器人管理'
      paths-to-match: '/api/bots/**'
      packages-to-scan: com.buildmybridge.controller.bot
```

---

## ✨ 建议检查项

当添加新的接口时，请确保：

- [ ] Controller 类有 `@Tag` 注解
- [ ] 每个方法有 `@Operation` 注解
- [ ] 方法有 `@ApiResponse` 或 `@ApiResponses` 注解
- [ ] 参数有 `@Parameter` 注解（如需要）
- [ ] DTO 类有 `@Schema` 注解
- [ ] DTO 的字段有 `@Schema` 注解和 `example` 值

这样可以在 Knife4j UI 中展示完整、专业的 API 文档。

---

## 📝 配置清单

### application.yml 配置示例

```yaml
# SpringDoc OpenAPI 配置
springdoc:
  swagger-ui:
    path: /swagger-ui.html
    tags-sorter: alpha
    operations-sorter: alpha
  api-docs:
    path: /v3/api-docs
  group-configs:
    - group: 'default'
      paths-to-match: '/**'
      packages-to-scan: com.buildmybridge

# Knife4j 增强配置
knife4j:
  enable: true
  setting:
    language: zh_cn
    enableSwaggerModels: true
    swaggerModelsName: "数据模型"
    enableDocumentManage: true
    enableReload: true
    enableFooter: true
    footerCustom: "<div style='text-align: center;'>BuildMyBridge © 2026</div>"
    enableSearch: true
```

### pom.xml 依赖

```xml
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
    <version>4.4.0</version>
</dependency>
```

---

**最后更新**: 2026-03-31
