#!/bin/bash

# BuildMyBridge 后端快速启动脚本

echo "========================================="
echo "  BuildMyBridge 后端启动脚本"
echo "========================================="
echo ""

# 检查 Java 版本
echo "✓ 检查 Java 版本..."
JAVA_VERSION=$(java -version 2>&1 | grep -oP 'version "?\K[0-9]+')
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "✗ 错误：需要 Java 17+ (当前版本: $JAVA_VERSION)"
    exit 1
fi
echo "  Java 版本: $JAVA_VERSION ✓"
echo ""

# 检查 MySQL
echo "✓ 检查 MySQL 连接..."
if mysql -h localhost -u root -proot -e "SELECT 1" > /dev/null 2>&1; then
    echo "  MySQL 连接成功 ✓"
else
    echo "  ⚠ 警告: MySQL 连接失败，请检查:"
    echo "    - MySQL 服务是否启动"
    echo "    - 用户名密码是否正确"
fi
echo ""

# 检查 Redis
echo "✓ 检查 Redis 连接..."
if redis-cli ping > /dev/null 2>&1; then
    echo "  Redis 连接成功 ✓"
else
    echo "  ⚠ 警告: Redis 连接失败，请检查:"
    echo "    - Redis 服务是否启动"
    echo "    - 端口是否为 6379"
fi
echo ""

# 编译项目
echo "✓ 编译项目..."
mvn clean install
if [ $? -ne 0 ]; then
    echo "✗ 编译失败，请检查依赖和代码"
    exit 1
fi
echo ""

# 启动应用
echo "✓ 启动应用服务..."
echo ""
echo "========================================="
echo "  应用启动中..."
echo "========================================="
echo ""
echo "• 后端 API: http://localhost:8080/api"
echo "• API 文档: http://localhost:8080/doc.html"
echo "• Swagger:  http://localhost:8080/swagger-ui.html"
echo ""
echo "• 健康检查: curl http://localhost:8080/api/health"
echo ""
echo "按 Ctrl+C 停止应用"
echo "========================================="
echo ""

mvn spring-boot:run
