---
name: Maven 路径配置
description: IDEA 中 Maven 的安装位置（记住此路径以便后续使用）
type: reference
---

## Maven 配置信息

**Maven 执行文件：**
```
C:\Program Files (x86)\JetBrains\IntelliJ IDEA Community Edition 2024.3.5\plugins\maven\lib\maven3\bin\mvn.cmd
```

**Maven Home：**
```
C:\Program Files (x86)\JetBrains\IntelliJ IDEA Community Edition 2024.3.5\plugins\maven\lib\maven3
```

**Maven 版本：**
```
Apache Maven 3.9.9
Java version: 21.0.1
```

## 使用方式

在 PowerShell 中执行打包：
```powershell
$mvnPath = 'C:\Program Files (x86)\JetBrains\IntelliJ IDEA Community Edition 2024.3.5\plugins\maven\lib\maven3\bin\mvn.cmd'

# 打包
& $mvnPath clean package -DskipTests

# 查看版本
& $mvnPath --version
```

或者在后端目录直接运行：
```bash
cd D:\ai\build-my-bridge\backend
"C:\Program Files (x86)\JetBrains\IntelliJ IDEA Community Edition 2024.3.5\plugins\maven\lib\maven3\bin\mvn.cmd" clean package -DskipTests
```
