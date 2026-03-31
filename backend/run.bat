@echo off
REM BuildMyBridge 后端快速启动脚本（Windows 版本）

setlocal enabledelayedexpansion

echo =========================================
echo   BuildMyBridge 后端启动脚本 (Windows)
echo =========================================
echo.

REM 检查 Java 版本
echo 检查 Java 版本...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 未检测到 Java
    echo 请确保 Java 17+ 已安装并配置到 PATH
    pause
    exit /b 1
)
echo   Java 版本检查完成 ✓
echo.

REM 检查 Maven
echo 检查 Maven...
mvn --version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 未检测到 Maven
    echo 请确保 Maven 已安装并配置到 PATH
    pause
    exit /b 1
)
echo   Maven 检查完成 ✓
echo.

REM 编译项目
echo 编译项目...
call mvn clean install
if %errorlevel% neq 0 (
    echo 错误: 编译失败
    echo 请检查依赖和代码
    pause
    exit /b 1
)
echo   编译成功 ✓
echo.

REM 启动应用
echo 启动应用服务...
echo.
echo =========================================
echo   应用启动中...
echo =========================================
echo.
echo 后端 API: http://localhost:8080/api
echo API 文档: http://localhost:8080/doc.html
echo Swagger:  http://localhost:8080/swagger-ui.html
echo.
echo 健康检查: curl http://localhost:8080/api/health
echo.
echo 按 Ctrl+C 停止应用
echo =========================================
echo.

call mvn spring-boot:run

pause
