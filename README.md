# BuildMyBridge

🤖 一个开源的飞书 AI 机器人集成平台，快速将 Dify 等 AI 应用集成到飞书消息系统。

## 🚀 快速开始

### 📋 前置要求
- Java 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 7.x+
- Node.js 16+（可选，如需前端）

### ⚡ 5分钟启动

```bash
# 1. 配置环境变量
export FEISHU_APP_ID="your-app-id"
export FEISHU_APP_SECRET="your-app-secret"
export JWT_SECRET="your-secret-key"

# 2. 启动后端
cd backend
mvn clean spring-boot:run

# 3. 访问 API 文档
# http://localhost:8081/api/doc.html
```

详见 **[快速启动指南](docs/01-快速启动.md)**

---

## 📚 文档导航

| 文档 | 说明 |
|-----|------|
| **[项目简介](docs/00-项目简介.md)** | 项目目标、功能和结构 |
| **[快速启动](docs/01-快速启动.md)** | 环境配置、启动步骤、常见问题 |
| **[系统架构](docs/ARCHITECTURE.md)** | 系统设计、模块架构、数据流 |
| **[技术栈](docs/TECH_STACK.md)** | 后端/前端技术栈、依赖版本 |
| **[Feishu 集成](docs/参考文档/Feishu集成总结.md)** | OAuth、Token 管理、已完成功能 |
| **[Knife4j 指南](docs/参考文档/Knife4j开发指南.md)** | API 文档、注解使用、测试 |
| **[后端实现](docs/指南/后端实现总结.md)** | 项目结构、核心类、业务流程 |

---

## ✨ 主要功能

### ✅ 已实现
- 飞书 OAuth 认证
- 机器人账户管理
- 消息处理基础设施
- API 文档（Knife4j）
- JWT 认证

### 🔄 进行中
- AI 应用集成（Dify）
- 消息路由和处理
- 前端管理界面

### ⏳ 计划中
- WebSocket 长连接优化
- 消息重试机制
- 监控告警系统

---

## 🏗️ 项目结构

```
build-my-bridge/
├── docs/                          # 完整文档
│   ├── 00-项目简介.md
│   ├── 01-快速启动.md
│   ├── ARCHITECTURE.md
│   ├── TECH_STACK.md
│   ├── 设计与原型/
│   ├── 参考文档/
│   │   ├── Feishu集成总结.md
│   │   ├── Knife4j开发指南.md
│   │   └── 飞书机器人长连接.md
│   └── 指南/
│       └── 后端实现总结.md
├── backend/                        # Spring Boot 后端
│   ├── src/main/java/
│   ├── src/main/resources/
│   ├── pom.xml
│   └── README.md
├── frontend/                       # React 前端
│   ├── src/
│   ├── package.json
│   └── README.md
├── openspec/                       # OpenSpec 工作流
│   └── changes/build-my-bridge/
├── CLAUDE.md                       # Claude Code 开发指导
└── README.md                       # 本文件
```

---

## 🔗 API 端点

### 认证
- `GET /api/auth/login` - 获取飞书授权 URL
- `GET /api/auth/callback` - OAuth 回调处理
- `POST /api/auth/logout` - 登出

### 机器人管理
- `GET /api/bots` - 获取机器人列表
- `GET /api/bots/{id}` - 获取机器人详情
- `POST /api/bots` - 创建机器人
- `PUT /api/bots/{id}` - 更新机器人
- `DELETE /api/bots/{id}` - 删除机器人
- `PUT /api/bots/{id}/status` - 切换启用状态

**完整 API 文档**: http://localhost:8081/api/doc.html

---

## 🛠️ 开发

### 使用 OpenSpec 工作流

本项目采用规范驱动的开发流程，所有工作通过 OpenSpec 跟踪：

```bash
# 查看活跃的变更
openspec list --json

# 查看变更状态
openspec status --change "build-my-bridge" --json

# 探索和设计
/opsx:explore

# 提议新变更
/opsx:propose <name>

# 实施变更
/opsx:apply

# 归档完成的变更
/opsx:archive
```

详见 [CLAUDE.md](CLAUDE.md)

### 开发建议

- **后端开发** - 参考 [后端实现总结](docs/指南/后端实现总结.md)
- **API 文档** - 使用 [Knife4j 开发指南](docs/参考文档/Knife4j开发指南.md)
- **系统设计** - 查看 [系统架构](docs/ARCHITECTURE.md)

---

## 📦 技术栈

### 后端
- **框架**: Spring Boot 3.x
- **ORM**: MyBatis-Plus 3.x
- **缓存**: Redis 7.x
- **数据库**: MySQL 8.0+
- **文档**: Knife4j 4.4.0
- **认证**: JWT + Spring Security
- **集成**: 飞书官方 SDK 2.5.3

### 前端
- **框架**: React 18
- **语言**: TypeScript
- **构建**: Vite
- **UI**: Ant Design 5.x
- **HTTP**: Axios
- **状态**: Zustand

---

## 🤝 贡献

我们欢迎贡献！请按照以下步骤：

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

---

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

---

## 💬 获取帮助

- **快速开始遇到问题** → 查看 [快速启动指南](docs/01-快速启动.md) 的常见问题部分
- **想了解系统架构** → 参考 [系统架构](docs/ARCHITECTURE.md)
- **需要 API 文档** → 访问 http://localhost:8081/api/doc.html
- **开发相关问题** → 查看 [后端实现总结](docs/指南/后端实现总结.md)

---

## 🎯 路线图

### Phase 1（已完成）✅
- Feishu OAuth 认证
- 机器人账户管理
- 基础消息框架

### Phase 2（进行中）🔄
- WebSocket 长连接
- Dify 集成
- 消息路由
- 前端管理界面

### Phase 3（计划中）⏳
- 消息重试机制
- 监控告警
- 支持钉钉、企业微信
- 高级路由规则

---

**最后更新**: 2026-03-31

Made with ❤️ for Feishu integration
