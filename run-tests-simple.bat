@echo off
echo ==========================================
echo 艾斯医药系统单元测试 - 简单运行模式
echo ==========================================
echo.

echo 注意：此脚本需要手动配置JUnit依赖
echo.

REM 检查Java环境
java -version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo 错误: 未找到Java环境
    pause
    exit /b 1
)

echo Java环境检查通过
echo.

REM 尝试编译（如果没有JUnit依赖，会失败但继续）
echo 正在尝试编译测试文件...

REM 首先编译主类
javac Pages\LoginFrame.java Pages\Cart.java Pages\shopList.java

REM 然后编译测试类（可能因缺少JUnit而失败）
javac Pages\LoginFrameTest.java 2>nul
javac Pages\CartTest.java 2>nul
javac Pages\shopListTest.java 2>nul

echo.
echo 如果编译成功，可以使用以下命令运行测试：
echo.
echo java -jar junit-platform-console-standalone.jar --class-path . --select-class LoginFrameTest
echo java -jar junit-platform-console-standalone.jar --class-path . --select-class CartTest  
echo java -jar junit-platform-console-standalone.jar --class-path . --select-class shopListTest
echo.
echo 或者使用TestRunner:
echo java -cp ".;junit-platform-console-standalone.jar" TestRunner
echo.
echo ==========================================
echo 测试文件说明
echo ==========================================
echo.
echo 1. LoginFrameTest.java - 测试登录功能
echo    ✓ 默认凭据验证
echo    ✓ 用户数据JSON加载/保存
echo    ✓ 密码强度验证
echo    ✓ 输入验证
echo    ✓ 异常处理
echo.
echo 2. CartTest.java - 测试购物车功能
echo    ✓ 单例模式
echo    ✓ 商品添加/移除
echo    ✓ 数量累加逻辑
echo    ✓ 价格计算
echo    ✓ 边界条件测试
echo.
echo 3. shopListTest.java - 测试商品列表功能
echo    ✓ JSON数据加载
echo    ✓ 表格创建和更新
echo    ✓ 类别筛选
echo    ✓ UI组件创建
echo    ✓ 数据格式处理
echo.
echo 获取JUnit依赖:
echo 1. 访问 https://junit.org/junit5/
echo 2. 下载 junit-platform-console-standalone.jar
echo 3. 将其放在项目根目录
echo.

pause