@echo off
echo ================================
echo AscentSys 一键编译运行脚本
echo ================================
echo.
echo [1/2] 正在编译...
javac -encoding UTF-8 -d bin Pages\LoginFrame.java Pages\shopList.java
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ✗ 编译失败！
    pause
    exit /b 1
)
echo ✓ 编译成功！
echo.
echo [2/2] 正在启动程序...
java -cp bin LoginFrame
