# 后端应用启动指南

## 🚨 当前状态

系统检测到：
- ✅ Java 21 已安装
- ❌ Maven 未安装或不在 PATH 中

## 📋 启动要求

### 必须安装
1. **Java 17+** ✅ 已安装 (Java 21)
2. **Maven 3.8+** ⚠️ 需要安装
3. **MySQL 8.0+** - 需要验证
4. **Redis 7.x** - 需要验证

---

## 🔧 安装 Maven

### Windows
```powershell
# 使用 Chocolatey
choco install maven

# 或从官网下载
# https://maven.apache.org/download.cgi
# 下载后添加到 PATH
```

### macOS
```bash
# 使用 Homebrew
brew install maven

# 验证安装
mvn -version
```

### Linux (Ubuntu/Debian)
```bash
# 使用包管理器
sudo apt update
sudo apt install maven

# 验证安装
mvn -version
```

### Linux (CentOS/RHEL)
```bash
# 使用 yum
sudo yum install maven

# 验证安装
mvn -version
```

---

## ✅ 验证环境

安装 Maven 后，运行以下命令验证：

```bash
# 检查 Java
java -version
# 应该显示 Java 17+

# 检查 Maven
mvn -version
# 应该显示 Maven 版本和 Java 版本
```

---

## 🚀 启动应用（三种方式）

### 方式 1: Maven 直接运行（推荐）
```bash
cd D:\ai\build-my-bridge\backend
mvn clean install
mvn spring-boot:run
```

### 方式 2: 编译后运行 JAR
```bash
cd D:\ai\build-my-bridge\backend
mvn clean package -DskipTests
java -jar target/buildmybridge-backend-0.1.0.jar
```

### 方式 3: IDE 运行（Spring Boot Run）
- 在 IDE（IntelliJ IDEA 或 Eclipse）中打开项目
- 右键点击 `BuildMyBridgeApplication.java`
- 选择 "Run" 或 "Debug as Spring Boot App"

---

## 📊 启动过程

```
第 1 步：编译项目
   mvn clean install
   ↓ (需要 2-5 分钟，取决于网络)

第 2 步：启动 Spring Boot
   mvn spring-boot:run
   ↓ (需要 10-30 秒)

第 3 步：应用就绪
   ✅ 后端 API: http://localhost:8080/api
   ✅ Knife4j 文档: http://localhost:8080/doc.html
   ✅ 健康检查: http://localhost:8080/api/health
```

---

## 🔍 验证启动成功

### 方式 1: 浏览器访问
```
http://localhost:8080/api/health
```
应该返回：
```json
{
  "code": "SUCCESS",
  "message": "请求成功",
  "data": "BuildMyBridge Backend is running"
}
```

### 方式 2: curl 命令
```bash
curl http://localhost:8080/api/health
```

### 方式 3: Knife4j UI
```
http://localhost:8080/doc.html
```
应该显示完整的 API 文档界面

---

## 🛠️ 故障排除

### 问题 1: Maven 命令不找到
**症状：** `mvn: command not found`

**解决方案：**
1. 确认 Maven 已安装
2. 检查 Maven 是否在 PATH 中
3. 重启终端/IDE
4. 在 IDE 中配置 Maven 路径

### 问题 2: Java 版本不兼容
**症状：** `Cannot run Spring Boot 3.x on Java 8`

**解决方案：**
- 升级 Java 到 17+
- 在 IDE 中配置项目 SDK

### 问题 3: 编译失败（依赖问题）
**症状：** `Failed to download artifacts`

**解决方案：**
```bash
# 清理 Maven 缓存
rm -rf ~/.m2/repository

# 重新下载依赖
mvn clean install
```

### 问题 4: 端口 8080 已被占用
**症状：** `Address already in use: localhost:8080`

**解决方案：**
- 查找占用端口的进程并关闭
- 或者修改 `application.yml` 中的 `server.port`

### 问题 5: 数据库连接失败
**症状：** `Connection refused: localhost:3306`

**解决方案：**
1. 确保 MySQL 已启动
2. 检查用户名密码（默认 root/root）
3. 运行 `db/schema.sql` 建表

### 问题 6: Redis 连接失败
**症状：** `Connection refused: localhost:6379`

**解决方案：**
1. 确保 Redis 已启动
2. 检查 Redis 端口（默认 6379）
3. 确保 Redis 没有设置密码（或在 application.yml 中配置）

---

## 📱 应用启动后的 URL

| 功能 | URL | 说明 |
|------|-----|------|
| 🏥 **健康检查** | http://localhost:8080/api/health | 验证应用运行状态 |
| 📚 **API 文档** | http://localhost:8080/doc.html | Knife4j 增强 UI |
| 📖 **Swagger UI** | http://localhost:8080/swagger-ui.html | 标准 Swagger 界面 |
| 📋 **OpenAPI JSON** | http://localhost:8080/v3/api-docs | API 定义文件 |

---

## 💾 数据库准备

在启动应用前，确保已执行建表脚本：

```bash
# 方式 1: 命令行
mysql -u root -p < backend/src/main/resources/db/schema.sql

# 方式 2: MySQL 客户端（如 Navicat、DBeaver）
# 打开 backend/src/main/resources/db/schema.sql
# 执行所有 SQL 语句
```

---

## 🎓 首次启动建议

1. **安装依赖**
   ```bash
   mvn clean install
   ```

2. **执行数据库建表**
   ```bash
   mysql -u root -p buildmybridge < backend/src/main/resources/db/schema.sql
   ```

3. **启动应用**
   ```bash
   mvn spring-boot:run
   ```

4. **验证成功**
   ```bash
   curl http://localhost:8080/api/health
   ```

5. **查看文档**
   ```
   打开浏览器访问 http://localhost:8080/doc.html
   ```

---

## 📞 获取帮助

如果遇到问题：
1. 查看应用日志（控制台输出）
2. 参考上面的"故障排除"部分
3. 检查 `README.md` 和 `KNIFE4J_GUIDE.md`

---

**准备好了吗？现在开始启动应用！** 🚀
