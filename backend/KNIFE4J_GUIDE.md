# Knife4j API 文档和接口调试指南

## 📚 什么是 Knife4j？

Knife4j 是基于 OpenAPI 3.0 的 API 文档管理工具，是 Swagger UI 的增强版本，提供了：

- ✅ 更美观的 UI 界面
- ✅ 强大的接口调试功能
- ✅ 接口分组管理
- ✅ 离线 HTML 导出
- ✅ 在线 API 调试
- ✅ 支持多语言

## 🚀 启动应用后访问 Knife4j

### 1. 启动后端服务

```bash
cd backend
mvn spring-boot:run
```

应用将在 `http://localhost:8080/api` 启动。

### 2. 访问 Knife4j UI

打开浏览器访问以下 URL：

| UI 版本 | URL |
|--------|-----|
| **Knife4j 增强 UI** | http://localhost:8080/doc.html |
| **Swagger UI** | http://localhost:8080/swagger-ui.html |
| **OpenAPI JSON** | http://localhost:8080/v3/api-docs |

**推荐使用：** `http://localhost:8080/doc.html` （更好的用户体验）

## 🎯 Knife4j 功能说明

### 页面布局

```
┌─────────────────────────────────────────┐
│  BuildMyBridge API 文档                  │
├─────────────────────────────────────────┤
│ 左侧菜单                │  中间文档          │  右侧操作
│                        │                    │
│ • 公开接口             │  接口详情          │ • 调试
│ • 认证模块             │  • 方法             │ • 发送请求
│ • 机器人管理           │  • 参数             │ • 响应结果
│ • 应用管理             │  • 响应格式         │ • 复制 curl
│ • 消息日志             │  • 错误代码         │ • 下载
│                        │                    │
└─────────────────────────────────────────┘
```

### 核心功能

#### 1. 接口浏览
- 左侧菜单按模块分组显示所有 API 接口
- 点击接口查看详细信息（请求方法、参数、响应等）
- 支持按标签、方法排序

#### 2. 在线调试
- **Try it out** 按钮 - 切换到调试模式
- 填写请求参数
- 点击 **Execute** 发送请求
- 查看响应内容、状态码、响应时间

#### 3. 认证配置
- 顶部 **Authorize** 按钮
- 输入 JWT Token
- 之后的所有请求会自动添加 Authorization header

#### 4. 导出功能
- 右上角 **导出** 功能
- 支持导出为 HTML、Markdown 等格式

## 📝 使用 Knife4j 调试接口

### 示例：调试健康检查接口

1. **打开 Knife4j**
   ```
   http://localhost:8080/doc.html
   ```

2. **找到接口**
   - 左侧菜单选择 "健康检查" → "GET /health"

3. **查看接口文档**
   - 中间显示接口详情、请求参数、响应格式

4. **在线调试**
   - 点击 **Try it out** 按钮
   - 点击 **Execute** 发送请求
   - 查看响应结果

5. **响应示例**
   ```json
   {
     "code": "SUCCESS",
     "message": "请求成功",
     "data": "BuildMyBridge Backend is running"
   }
   ```

### 示例：调试需要认证的接口（待实现）

1. **配置认证**
   - 右上角 **Authorize** 按钮
   - 选择 "Bearer Token"
   - 输入从 `/auth/callback` 获得的 JWT Token
   - 点击 **Authorize**

2. **调试受保护接口**
   - 所有受保护的接口现在会自动使用配置的 Token
   - 接口请求会在 Authorization header 中添加 `Bearer <token>`

## 🔑 API 分组说明

### 已配置的接口分组

| 分组 | 路径 | 说明 |
|------|------|------|
| **公开接口** | /api/** | 所有公开 API |
| **认证模块** | /api/auth/** | 登录、授权、Token 相关 |
| **机器人管理** | /api/bots/** | Bot CRUD、连接管理 |
| **应用管理** | /api/apps/** | App CRUD、配置管理 |
| **消息日志** | /api/logs/** | 消息查询、统计 |

### 快速导航

左侧菜单显示分组，点击展开查看该分组的所有接口。

## 💡 实用技巧

### 1. 复制 curl 命令
- 在调试响应下方有 **curl** 复制按钮
- 可直接在终端运行测试

```bash
# 示例
curl -X GET "http://localhost:8080/api/health" \
  -H "accept: application/json"
```

### 2. 快速搜索
- 左侧菜单上方有搜索框
- 输入接口名称快速查找

### 3. 请求历史
- Knife4j 会记录最近的请求
- 方便快速重复调试

### 4. 导出文档
- 右上角有导出选项
- 可导出离线 HTML 文档分享

### 5. 黑暗模式
- 右上角有主题切换
- 支持深色主题

## 🔒 安全性说明

### Bearer Token 认证

所有受保护的接口需要在 Authorization header 中提供 JWT Token：

```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

Knife4j 中的 Authorize 功能会自动处理这一点。

### Token 来源

1. 调用 `/auth/login` 获得授权 URL
2. 用户访问授权 URL，飞书会重定向到 `/auth/callback`
3. `/auth/callback` 返回 JWT Token
4. 在 Knife4j 中的 Authorize 功能输入该 Token

## 🐛 常见问题

### Q: 访问 doc.html 显示 404？
**A:** 确保：
- 应用已启动并监听 8080 端口
- Knife4j 依赖已正确添加到 pom.xml
- 运行了 `mvn clean install` 重新编译

### Q: 接口列表为空？
**A:** 确保：
- Controller 类使用了 `@Tag` 注解
- API 方法使用了 `@Operation` 注解
- 应用已重新启动

### Q: 调试时返回 401？
**A:** 可能原因：
- 未配置认证 Token
- Token 已过期
- 点击 Authorize 按钮重新输入有效的 Token

### Q: 如何离线使用 Knife4j？
**A:**
- 点击右上角 **导出** 按钮
- 选择 HTML 格式
- 下载后可在任何浏览器中打开（无需网络）

## 📖 相关文档

- [Knife4j 官方文档](https://knife4j.gitee.io/)
- [OpenAPI 3.0 规范](https://spec.openapis.org/oas/v3.0.0)
- [Spring Doc OpenAPI](https://springdoc.org/)

## 🚀 开发工作流

### 1. 开发新接口
```java
@RestController
@Tag(name = "模块名", description = "模块描述")
public class XxxController {

    @GetMapping("/xxx")
    @Operation(summary = "接口摘要", description = "接口详细描述")
    public ApiResponse<?> xxx() {
        return ApiResponse.success(...);
    }
}
```

### 2. 重新启动应用
```bash
mvn spring-boot:run
```

### 3. 访问 Knife4j 查看新接口
```
http://localhost:8080/doc.html
```

### 4. 在线调试和验证
- 使用 Knife4j 的 Try it out 功能测试

## 📋 Knife4j 注解速查表

### 常用注解

```java
// Controller 级别
@Tag(name = "模块名", description = "模块描述")
public class XxxController {

    // 方法级别
    @Operation(
        summary = "接口摘要",
        description = "接口详细描述"
    )
    @ApiResponse(
        responseCode = "200",
        description = "成功响应"
    )
    public ApiResponse<?> xxx(
        @RequestParam(description = "参数描述") String param
    ) {
        ...
    }
}

// DTO 字段级别
@Schema(description = "字段描述", example = "示例值")
private String field;
```

## 🎓 学习资源

### 快速入门
- 打开 Knife4j UI
- 浏览所有接口的文档
- 使用 Try it out 进行调试

### 深入学习
- 查看源代码中的 `@Tag`、`@Operation` 注解
- 阅读响应示例和错误代码说明
- 导出 HTML 文档研究 API 结构

---

**提示：** Knife4j 会在每次应用启动时自动扫描 Controller 和注解，动态生成 API 文档，无需手动配置！

**下一步：** 在实现新的 API 接口时，记得添加 `@Tag`、`@Operation`、`@Schema` 等注解，这样 Knife4j 会自动显示在文档中。
