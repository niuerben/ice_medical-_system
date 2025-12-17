@echo off
echo ==========================================
echo 正在编译Java程序...
echo ==========================================
javac -encoding UTF-8 -d bin Pages\LoginFrame.java Pages\shopList.java

if %errorlevel% neq 0 (
    echo.
    echo [错误] 编译失败!
    echo 请检查上面的错误信息
    pause
    exit /b 1
)

echo.
echo [成功] 编译完成!
echo ==========================================
echo 正在运行程序...
echo ==========================================
java -cp bin LoginFrame

if %errorlevel% neq 0 (
    echo.
    echo [错误] 程序运行失败!
    pause
    exit /b 1
)
