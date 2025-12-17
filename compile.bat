@echo off
echo ================================
echo 正在编译项目...
echo ================================
javac -encoding UTF-8 -d bin Pages\LoginFrame.java Pages\shopList.java
if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✓ 编译成功！
    echo.
) else (
    echo.
    echo ✗ 编译失败，请检查错误信息
    echo.
    pause
    exit /b 1
)
