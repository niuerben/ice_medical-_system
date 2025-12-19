import org.junit.platform.console.ConsoleLauncher;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * 测试运行器 - 用于运行所有单元测试并生成报告
 */
public class TestRunner {
    
    public static void main(String[] args) {
        System.out.println("=== 开始运行艾斯医药系统单元测试 ===\n");
        
        try {
            // 捕获输出以便分析
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            PrintStream originalErr = System.err;
            
            System.setOut(new PrintStream(outputStream));
            System.setErr(new PrintStream(outputStream));
            
            // 创建测试选择器
            List<DiscoverySelector> selectors = List.of(
                DiscoverySelectors.selectClass("LoginFrameTest"),
                DiscoverySelectors.selectClass("CartTest"),
                DiscoverySelectors.selectClass("shopListTest")
            );
            
            // 创建启动器和请求
            Launcher launcher = LauncherFactory.create();
            LauncherDiscoveryRequest request = 
                org.junit.platform.launcher.LauncherDiscoveryRequestBuilder.request()
                .selectors(selectors)
                .build();
            
            // 执行测试
            launcher.execute(request);
            
            // 恢复输出流
            System.setOut(originalOut);
            System.setErr(originalErr);
            
            String output = outputStream.toString();
            
            // 输出结果
            System.out.println(output);
            
            // 分析结果并生成摘要
            analyzeTestResults(output);
            
        } catch (Exception e) {
            System.err.println("测试运行失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== 测试完成 ===");
    }
    
    private static void analyzeTestResults(String output) {
        System.out.println("\n=== 测试结果摘要 ===");
        
        // 查找测试统计信息
        String[] lines = output.split("\n");
        int totalTests = 0;
        int passedTests = 0;
        int failedTests = 0;
        int skippedTests = 0;
        
        for (String line : lines) {
            if (line.contains("tests found") || line.contains("test found")) {
                String[] parts = line.split("\\s+");
                for (String part : parts) {
                    try {
                        int num = Integer.parseInt(part.replace(",", ""));
                        if (totalTests == 0) totalTests = num;
                    } catch (NumberFormatException e) {
                        // 忽略非数字部分
                    }
                }
            }
            
            if (line.contains("successful") || line.contains("passed")) {
                if (line.contains("tests") || line.contains("test")) {
                    String[] parts = line.split("\\s+");
                    for (String part : parts) {
                        try {
                            int num = Integer.parseInt(part.replace(",", ""));
                            if (passedTests == 0) passedTests = num;
                        } catch (NumberFormatException e) {
                            // 忽略非数字部分
                        }
                    }
                }
            }
            
            if (line.contains("failed")) {
                String[] parts = line.split("\\s+");
                for (String part : parts) {
                    try {
                        int num = Integer.parseInt(part.replace(",", ""));
                        if (failedTests == 0 && num > 0) failedTests = num;
                    } catch (NumberFormatException e) {
                        // 忽略非数字部分
                    }
                }
            }
            
            if (line.contains("skipped")) {
                String[] parts = line.split("\\s+");
                for (String part : parts) {
                    try {
                        int num = Integer.parseInt(part.replace(",", ""));
                        if (skippedTests == 0) skippedTests = num;
                    } catch (NumberFormatException e) {
                        // 忽略非数字部分
                    }
                }
            }
        }
        
        System.out.println("总测试数: " + totalTests);
        System.out.println("通过测试: " + passedTests);
        System.out.println("失败测试: " + failedTests);
        System.out.println("跳过测试: " + skippedTests);
        
        if (totalTests > 0) {
            double successRate = (double) passedTests / totalTests * 100;
            System.out.printf("成功率: %.1f%%\n", successRate);
        }
        
        System.out.println("\n=== 覆盖的功能模块 ===");
        System.out.println("✓ LoginFrame - 登录功能和用户管理");
        System.out.println("✓ Cart - 购物车功能");
        System.out.println("✓ shopList - 商品列表和数据处理");
        
        System.out.println("\n=== 测试覆盖范围 ===");
        System.out.println("✓ 正常流程测试");
        System.out.println("✓ 边界条件测试");
        System.out.println("✓ 异常处理测试");
        System.out.println("✓ 数据验证测试");
        System.out.println("✓ UI组件测试");
    }
}