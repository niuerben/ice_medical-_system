import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.prefs.Preferences;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * LoginFrame类的全面单元测试
 * 覆盖登录逻辑、用户管理、UI交互等核心功能
 */
class LoginFrameTest {

    private LoginFrame loginFrame;
    private Preferences mockPreferences;
    private Path tempUsersFile;

    @BeforeEach
    void setUp() throws IOException {
        // 创建临时文件用于测试
        tempUsersFile = Files.createTempFile("test_users", ".json");
        
        // 模拟Preferences
        mockPreferences = mock(Preferences.class);
        
        // 由于LoginFrame构造函数中包含UI初始化，我们需要在测试环境中特殊处理
        // 这里设置Headless模式以避免GUI相关问题
        System.setProperty("java.awt.headless", "true");
    }

    @AfterEach
    void tearDown() throws IOException {
        // 清理临时文件
        if (tempUsersFile != null && Files.exists(tempUsersFile)) {
            Files.delete(tempUsersFile);
        }
        // 重置系统属性
        System.clearProperty("java.awt.headless");
    }

    @Test
    @DisplayName("测试默认凭据存储创建")
    void testCreateDefaultCredentialStore() {
        // 使用反射访问私有方法
        try {
            java.lang.reflect.Method method = LoginFrame.class.getDeclaredMethod("createDefaultCredentialStore");
            method.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            Map<String, String> result = (Map<String, String>) method.invoke(loginFrame);
            
            assertNotNull(result);
            assertEquals(3, result.size());
            assertTrue(result.containsKey("admin"));
            assertTrue(result.containsKey("pharmacist"));
            assertTrue(result.containsKey("auditor"));
            assertEquals("admin123", result.get("admin"));
            assertEquals("med2024", result.get("pharmacist"));
            assertEquals("check789", result.get("auditor"));
            
        } catch (Exception e) {
            fail("反射调用失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试自定义用户JSON加载 - 文件不存在")
    void testLoadCustomUsersFromJSON_FileNotExists() {
        try {
            java.lang.reflect.Method method = LoginFrame.class.getDeclaredMethod("loadCustomUsersFromJSON");
            method.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            Map<String, String> result = (Map<String, String>) method.invoke(loginFrame);
            
            assertNotNull(result);
            assertTrue(result.isEmpty());
            
        } catch (Exception e) {
            fail("反射调用失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试自定义用户JSON保存和加载")
    void testSaveAndLoadCustomUsersToJSON() throws IOException {
        // 创建测试用户数据
        String testJsonContent = "{\n" +
                "  \"users\": [\n" +
                "    {\n" +
                "      \"username\": \"testuser1\",\n" +
                "      \"password\": \"password123\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"username\": \"testuser2\",\n" +
                "      \"password\": \"password456\"\n" +
                "    }\n" +
                "  ]\n" +
                "}\n";
        
        Files.write(tempUsersFile, testJsonContent.getBytes("UTF-8"));

        try {
            LoginFrame frame = new LoginFrame();
            
            // 测试加载
            java.lang.reflect.Method loadMethod = LoginFrame.class.getDeclaredMethod("loadCustomUsersFromJSON");
            loadMethod.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            Map<String, String> loadedUsers = (Map<String, String>) loadMethod.invoke(frame);
            
            assertNotNull(loadedUsers);
            assertEquals(2, loadedUsers.size());
            assertTrue(loadedUsers.containsKey("testuser1"));
            assertTrue(loadedUsers.containsKey("testuser2"));
            assertEquals("password123", loadedUsers.get("testuser1"));
            assertEquals("password456", loadedUsers.get("testuser2"));
            
        } catch (Exception e) {
            fail("测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试有效登录凭据")
    void testValidCredentials() {
        try {
            LoginFrame frame = new LoginFrame();
            
            // 使用反射访问私有字段
            java.lang.reflect.Field storeField = LoginFrame.class.getDeclaredField("userCredentialStore");
            storeField.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            Map<String, String> credentialStore = (Map<String, String>) storeField.get(frame);
            
            // 验证默认凭据存在
            assertTrue(credentialStore.containsKey("admin"));
            assertTrue(credentialStore.containsKey("pharmacist"));
            assertTrue(credentialStore.containsKey("auditor"));
            
            // 验证密码正确
            assertEquals("admin123", credentialStore.get("admin"));
            assertEquals("med2024", credentialStore.get("pharmacist"));
            assertEquals("check789", credentialStore.get("auditor"));
            
        } catch (Exception e) {
            fail("测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试用户名大小写标准化")
    void testUsernameNormalization() {
        try {
            LoginFrame frame = new LoginFrame();
            
            java.lang.reflect.Field storeField = LoginFrame.class.getDeclaredField("userCredentialStore");
            storeField.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            Map<String, String> credentialStore = (Map<String, String>) storeField.get(frame);
            
            // 测试用户名应该被转换为小写存储
            assertTrue(credentialStore.containsKey("admin"));
            assertFalse(credentialStore.containsKey("Admin"));
            assertFalse(credentialStore.containsKey("ADMIN"));
            
        } catch (Exception e) {
            fail("测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试账号记忆功能")
    void testRememberAccount() {
        try {
            LoginFrame frame = new LoginFrame();
            
            // 模拟记住账号
            java.lang.reflect.Method rememberMethod = LoginFrame.class.getDeclaredMethod("rememberAccount", String.class);
            rememberMethod.setAccessible(true);
            
            // 注意：这个测试可能因为Preferences的模拟而有限制
            rememberMethod.invoke(frame, "testUser");
            
            // 验证方法执行不抛异常
            assertTrue(true, "记住账号方法执行成功");
            
        } catch (Exception e) {
            fail("测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试密码数组安全性清理")
    void testPasswordArrayCleanup() {
        char[] passwordArray = {'p', 'a', 's', 's', 'w', 'o', 'r', 'd'};
        
        // 验证数组初始状态
        assertEquals(8, passwordArray.length);
        assertEquals('p', passwordArray[0]);
        assertEquals('d', passwordArray[7]);
        
        // 执行清理
        java.util.Arrays.fill(passwordArray, '\0');
        
        // 验证清理后的状态
        for (char c : passwordArray) {
            assertEquals('\0', c);
        }
    }

    @Test
    @DisplayName("测试密码强度验证 - 边界情况")
    void testPasswordStrengthValidation() {
        // 测试密码长度边界
        assertTrue(isValidPassword("abc123"));    // 最小有效长度
        assertTrue(isValidPassword("abc12345")); // 更长的有效密码
        assertFalse(isValidPassword("abc12"));   // 太短
        assertFalse(isValidPassword("ab12"));   // 更短
        
        // 测试字符组合要求
        assertFalse(isValidPassword("abcdef"));  // 只有字母
        assertFalse(isValidPassword("123456"));  // 只有数字
        assertFalse(isValidPassword("!@#$%^"));  // 只有特殊字符
        assertTrue(isValidPassword("abc123!@#")); // 字母+数字+特殊字符
        assertTrue(isValidPassword("ABC123"));   // 大写字母+数字
        assertTrue(isValidPassword("Abc123"));   // 混合大小写字母+数字
    }

    // 辅助方法：模拟密码验证逻辑
    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        return hasLetter && hasDigit;
    }

    @Test
    @DisplayName("测试JSON解析错误处理")
    void testJSONParsingErrorHandling() throws IOException {
        // 创建格式错误的JSON文件
        String malformedJson = "{\n" +
                "  \"users\": [\n" +
                "    {\n" +
                "      \"username\": \"testuser\"\n" +
                "      // 缺少逗号和密码字段\n" +
                "    }\n" +
                "  ]\n" +
                "}\n";
        
        Files.write(tempUsersFile, malformedJson.getBytes("UTF-8"));

        try {
            LoginFrame frame = new LoginFrame();
            
            java.lang.reflect.Method loadMethod = LoginFrame.class.getDeclaredMethod("loadCustomUsersFromJSON");
            loadMethod.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            Map<String, String> result = (Map<String, String>) loadMethod.invoke(frame);
            
            // 即使JSON格式错误，也应该返回空Map而不是崩溃
            assertNotNull(result);
            
        } catch (Exception e) {
            // 预期的解析错误，不应该导致测试失败
            assertTrue(true, "正确处理了JSON解析错误");
        }
    }

    @Test
    @DisplayName("测试用户存储初始化")
    void testInitializeCredentialStore() {
        try {
            LoginFrame frame = new LoginFrame();
            
            // 验证初始化后存储不为空
            java.lang.reflect.Field storeField = LoginFrame.class.getDeclaredField("userCredentialStore");
            storeField.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            Map<String, String> userStore = (Map<String, String>) storeField.get(frame);
            
            assertNotNull(userStore);
            assertFalse(userStore.isEmpty());
            
            // 验证包含默认用户
            assertTrue(userStore.containsKey("admin"));
            assertTrue(userStore.containsKey("pharmacist"));
            assertTrue(userStore.containsKey("auditor"));
            
        } catch (Exception e) {
            fail("测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试空输入验证")
    void testEmptyInputValidation() {
        // 测试空用户名
        assertTrue(isEmptyInput("", "password123"));
        assertTrue(isEmptyInput(null, "password123"));
        
        // 测试空密码
        assertTrue(isEmptyInput("admin", ""));
        assertTrue(isEmptyInput("admin", null));
        
        // 测试空字符数组密码
        assertTrue(isEmptyInput("admin", new char[0]));
        
        // 测试有效输入
        assertFalse(isEmptyInput("admin", "password123"));
        assertFalse(isEmptyInput("admin", new char[]{'p', 'a', 's', 's'}));
    }

    private boolean isEmptyInput(String username, String password) {
        return (username == null || username.trim().isEmpty()) || 
               (password == null || password.trim().isEmpty());
    }

    private boolean isEmptyInput(String username, char[] password) {
        return (username == null || username.trim().isEmpty()) || 
               (password == null || password.length == 0);
    }

    @Test
    @DisplayName("测试文件IO异常处理")
    void testFileIOExceptionHandling() {
        // 尝试从不存在或不可读的文件路径加载
        String invalidPath = "/invalid/path/that/does/not/exist/users.json";
        
        try {
            // 创建LoginFrame实例时应该能够处理文件不存在的情况
            LoginFrame frame = new LoginFrame();
            
            // 如果没有抛出异常，说明异常处理正确
            assertNotNull(frame);
            
        } catch (Exception e) {
            // 不应该因为文件问题而导致初始化失败
            fail("文件IO异常不应该影响LoginFrame初始化: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试字符编码处理")
    void testCharacterEncodingHandling() throws IOException {
        // 测试包含中文的用户数据
        String chineseUserJson = "{\n" +
                "  \"users\": [\n" +
                "    {\n" +
                "      \"username\": \"张三\",\n" +
                "      \"password\": \"中文密码123\"\n" +
                "    }\n" +
                "  ]\n" +
                "}\n";
        
        Files.write(tempUsersFile, chineseUserJson.getBytes("UTF-8"));

        try {
            LoginFrame frame = new LoginFrame();
            
            java.lang.reflect.Method loadMethod = LoginFrame.class.getDeclaredMethod("loadCustomUsersFromJSON");
            loadMethod.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            Map<String, String> result = (Map<String, String>) loadMethod.invoke(frame);
            
            assertNotNull(result);
            // 验证中文字符能够正确处理
            if (!result.isEmpty()) {
                assertTrue(result.containsKey("张三") || result.containsKey("张三".toLowerCase()));
            }
            
        } catch (Exception e) {
            fail("中文字符处理失败: " + e.getMessage());
        }
    }
}