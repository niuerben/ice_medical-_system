@echo off
echo ==========================================
echo 艾斯医药系统单元测试运行器
echo ==========================================
echo.

REM 检查Java环境
java -version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo 错误: 未找到Java环境，请确保已安装Java 8或更高版本
    pause
    exit /b 1
)

echo Java环境检查通过
echo.

REM 设置类路径
set CLASSPATH=.;junit-platform-console-standalone.jar;.

REM 检查是否存在JUnit依赖
if not exist "junit-platform-console-standalone.jar" (
    echo 警告: 未找到JUnit依赖文件
    echo 正在尝试下载JUnit...
    
    REM 使用curl下载JUnit（如果可用）
    curl -L -o junit-platform-console-standalone.jar https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.0/junit-platform-console-standalone-1.10.0.jar
    
    if %ERRORLEVEL% neq 0 (
        echo 错误: 无法自动下载JUnit依赖
        echo 请手动下载junit-platform-console-standalone.jar并放置在项目根目录
        echo 下载地址: https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.0/
        pause
        exit /b 1
    )
    
    echo JUnit依赖下载完成
    echo.
)

echo 正在编译测试文件...
javac -cp ".;junit-platform-console-standalone.jar" Pages\LoginFrameTest.java Pages\CartTest.java Pages\shopListTest.java TestRunner.java

if %ERRORLEVEL% neq 0 (
    echo 错误: 测试文件编译失败
    pause
    exit /b 1
)

echo 测试文件编译成功
echo.

echo 开始运行单元测试...
echo ==========================================
java -cp ".;junit-platform-console-standalone.jar" TestRunner

echo ==========================================
echo.

REM 尝试直接运行JUnit Console（如果TestRunner失败）
if %ERRORLEVEL% neq 0 (
    echo 尝试使用JUnit Console直接运行测试...
    echo.
    
    java -jar junit-platform-console-standalone.jar ^
        --class-path . ^
        --select-class LoginFrameTest ^
        --select-class CartTest ^
        --select-class shopListTest ^
        --details=summary ^
        --details-theme=unicode
)

echo.
echo 测试运行完成
pause