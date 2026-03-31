# 🚀 快速启动检查清单

## ⚠️ 启动前必需项

### 1️⃣ 环境检查
- [x] **Java** ✅ Java 21 已安装
- [ ] **Maven** ⚠️ **需要安装** (Maven 3.8+)
- [ ] **MySQL** 需要验证 (MySQL 8.0+)
- [ ] **Redis** 需要验证 (Redis 7.x)

### 2️⃣ 安装 Maven

**快速安装指令：**

**Windows (Chocolatey):**
```powershell
choco install maven
```

**macOS (Homebrew):**
```bash
brew install maven
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt update && sudo apt install maven
```

**验证安装：**
```bash
mvn -version
```

---

## 📋 启动前准备

### 3️⃣ 验证数据库连接

**MySQL 是否运行？**
```bash
# 测试连接
mysql -u root -p -e "SELECT 1"
# 如果提示输入密码，输入 "root"
```

**如果 MySQL 未启动：**
- Windows: 在服务中启动 MySQL
- macOS: `brew services start mysql`
- Linux: `sudo systemctl start mysql`

### 4️⃣ 验证 Redis 连接

**Redis 是否运行？**
```bash
redis-cli ping
# 应该返回 PONG
```

**如果 Redis 未启动：**
- Windows: 在服务中启动 Redis
- macOS: `brew services start redis`
- Linux: `sudo systemctl start redis-server`

### 5️⃣ 执行数据库建表

```bash
# 进入后端目录
cd D:\ai\build-my-bridge\backend

# 执行建表脚本
mysql -u root -p buildmybridge < src/main/resources/db/schema.sql
# 如果提示输入密码，输入 "root"
```

---

## 🎬 启动应用

### 方式 A: Maven 启动（推荐）

**第 1 步：编译**
```bash
cd D:\ai\build-my-bridge\backend
mvn clean install -DskipTests
```
✅ 完成后继续

**第 2 步：启动**
```bash
mvn spring-boot:run
```

### 方式 B: 构建 JAR 后启动

```bash
cd D:\ai\build-my-bridge\backend
mvn clean package -DskipTests
java -jar target/buildmybridge-backend-0.1.0.jar
```

### 方式 C: IDE 启动（IntelliJ IDEA）

1. 打开项目
2. 右键 `BuildMyBridgeApplication.java`
3. 选择 "Run" 或按 Shift+F10

---

## ✅ 验证启动成功

### 检查 1: 健康检查接口

**在浏览器中打开：**
```
http://localhost:8080/api/health
```

**应该返回：**
```json
{
  "code": "SUCCESS",
  "message": "请求成功",
  "data": "BuildMyBridge Backend is running"
}
```

### 检查 2: 查看 API 文档

**在浏览器中打开：**
```
http://localhost:8080/doc.html
```

**应该看到：**
- Knife4j UI 界面
- 左侧接口分组菜单
- 健康检查接口

### 检查 3: 查看日志

应用输出应该包含：
```
21:18:00.XXX  INFO  o.s.b.w.e.t.TomcatWebServer    : Tomcat initialized
21:18:00.XXX  INFO  o.s.b.w.e.t.TomcatWebServer    : Tomcat started on port(s): 8080 (http)
21:18:00.XXX  INFO  o.b.BuildMyBridgeApplication   : Started BuildMyBridgeApplication in X.XXX seconds
```

---

## 🎯 启动成功标志

| 项目 | 标志 |
|------|------|
| **应用启动** | ✅ 日志显示 "Started in X seconds" |
| **API 可用** | ✅ http://localhost:8080/api/health 返回 200 |
| **文档可用** | ✅ http://localhost:8080/doc.html 显示 UI |
| **数据库连接** | ✅ 应用日志无数据库连接错误 |

---

## 📊 预期启动时间

| 步骤 | 耗时 |
|------|------|
| Maven 清理编译 | 2-5 分钟（首次较慢）|
| 下载依赖 | 1-3 分钟（首次） |
| 应用启动 | 10-30 秒 |
| **总计** | **3-10 分钟（首次）** |

---

## 🛑 常见问题

### ❌ Maven 命令未找到
**解决：** 需要先安装 Maven（见上方"安装 Maven"部分）

### ❌ 编译失败 - 依赖下载失败
**解决：**
```bash
# 清理缓存
rm -rf ~/.m2/repository
# 重新尝试
mvn clean install
```

### ❌ 端口 8080 已被占用
**解决：** 修改 `application.yml`
```yaml
server:
  port: 8081  # 改为其他端口
```

### ❌ 数据库连接失败
**解决：**
1. 确保 MySQL 已启动
2. 检查 `application.yml` 中的数据库配置

### ❌ Redis 连接失败
**解决：**
1. 确保 Redis 已启动
2. 检查 Redis 端口（默认 6379）

---

## 🎓 启动后的 URL 导航

| 功能 | URL |
|------|-----|
| 🏥 健康检查 | http://localhost:8080/api/health |
| 📚 Knife4j 文档 | http://localhost:8080/doc.html |
| 📖 Swagger UI | http://localhost:8080/swagger-ui.html |
| 📋 OpenAPI JSON | http://localhost:8080/v3/api-docs |

---

## 🚀 现在开始！

**第 1 步：** 安装 Maven（如果还没有）

**第 2 步：** 验证 MySQL 和 Redis 运行

**第 3 步：** 执行建表脚本

**第 4 步：** 运行以下命令启动应用
```bash
cd D:\ai\build-my-bridge\backend
mvn clean install
mvn spring-boot:run
```

**第 5 步：** 打开浏览器验证
```
http://localhost:8080/doc.html
```

---

**准备好了吗？🚀 让我们启动应用！**

如有问题，参考 [STARTUP_GUIDE.md](./STARTUP_GUIDE.md) 获取详细帮助。
