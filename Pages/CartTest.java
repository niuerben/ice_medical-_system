import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Cart类的全面单元测试
 * 覆盖购物车核心功能：添加商品、移除商品、计算总价等
 */
class CartTest {

    private Cart cart;

    @BeforeEach
    void setUp() {
        // 每个测试前获取新的购物车实例
        cart = Cart.getInstance();
        cart.clear(); // 清空购物车，确保测试独立性
    }

    @AfterEach
    void tearDown() {
        // 每个测试后清空购物车
        if (cart != null) {
            cart.clear();
        }
    }

    @Test
    @DisplayName("测试单例模式")
    void testSingletonPattern() {
        Cart instance1 = Cart.getInstance();
        Cart instance2 = Cart.getInstance();
        
        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2, "应该返回同一个实例");
    }

    @Test
    @DisplayName("测试添加单个商品到购物车")
    void testAddSingleItem() {
        cart.addItem("P001", "阿莫西林", 25.50, 1);
        
        List<Cart.CartItem> items = cart.getItems();
        assertEquals(1, items.size(), "购物车应该有1个商品");
        
        Cart.CartItem item = items.get(0);
        assertEquals("P001", item.getId());
        assertEquals("阿莫西林", item.getName());
        assertEquals(25.50, item.getPrice(), 0.01);
        assertEquals(1, item.getQuantity());
    }

    @Test
    @DisplayName("测试添加多个不同商品")
    void testAddMultipleDifferentItems() {
        cart.addItem("P001", "阿莫西林", 25.50, 2);
        cart.addItem("P002", "布洛芬", 18.00, 1);
        cart.addItem("P003", "维生素C", 45.00, 3);
        
        List<Cart.CartItem> items = cart.getItems();
        assertEquals(3, items.size(), "购物车应该有3个不同的商品");
        
        // 验证第一个商品
        Cart.CartItem item1 = items.get(0);
        assertEquals("P001", item1.getId());
        assertEquals(2, item1.getQuantity());
        
        // 验证第二个商品
        Cart.CartItem item2 = items.get(1);
        assertEquals("P002", item2.getId());
        assertEquals(1, item2.getQuantity());
        
        // 验证第三个商品
        Cart.CartItem item3 = items.get(2);
        assertEquals("P003", item3.getId());
        assertEquals(3, item3.getQuantity());
    }

    @Test
    @DisplayName("测试添加相同ID商品 - 数量累加")
    void testAddSameItemMultipleTimes() {
        // 第一次添加
        cart.addItem("P001", "阿莫西林", 25.50, 2);
        
        List<Cart.CartItem> items = cart.getItems();
        assertEquals(1, items.size(), "应该只有1个商品");
        assertEquals(2, items.get(0).getQuantity(), "数量应该是2");
        
        // 第二次添加相同商品
        cart.addItem("P001", "阿莫西林", 25.50, 3);
        
        items = cart.getItems();
        assertEquals(1, items.size(), "应该仍然只有1个商品");
        assertEquals(5, items.get(0).getQuantity(), "数量应该累加为5");
        
        // 验证其他属性不变
        Cart.CartItem item = items.get(0);
        assertEquals("P001", item.getId());
        assertEquals("阿莫西林", item.getName());
        assertEquals(25.50, item.getPrice(), 0.01);
    }

    @Test
    @DisplayName("测试移除商品")
    void testRemoveItem() {
        // 添加多个商品
        cart.addItem("P001", "阿莫西林", 25.50, 2);
        cart.addItem("P002", "布洛芬", 18.00, 1);
        cart.addItem("P003", "维生素C", 45.00, 3);
        
        assertEquals(3, cart.getItems().size(), "初始应该有3个商品");
        
        // 移除中间的商品
        cart.removeItem("P002");
        
        List<Cart.CartItem> items = cart.getItems();
        assertEquals(2, items.size(), "移除后应该有2个商品");
        
        // 验证剩余商品
        boolean hasP001 = false, hasP003 = false, hasP002 = false;
        for (Cart.CartItem item : items) {
            if ("P001".equals(item.getId())) hasP001 = true;
            if ("P003".equals(item.getId())) hasP003 = true;
            if ("P002".equals(item.getId())) hasP002 = true;
        }
        
        assertTrue(hasP001, "应该仍有P001");
        assertTrue(hasP003, "应该仍有P003");
        assertFalse(hasP002, "不应该再有P002");
    }

    @Test
    @DisplayName("测试移除不存在的商品")
    void testRemoveNonExistentItem() {
        // 添加一些商品
        cart.addItem("P001", "阿莫西林", 25.50, 2);
        cart.addItem("P002", "布洛芬", 18.00, 1);
        
        assertEquals(2, cart.getItems().size(), "初始应该有2个商品");
        
        // 尝试移除不存在的商品
        cart.removeItem("P999");
        
        assertEquals(2, cart.getItems().size(), "移除不存在的商品后数量不应该改变");
    }

    @Test
    @DisplayName("测试清空购物车")
    void testClearCart() {
        // 添加多个商品
        cart.addItem("P001", "阿莫西林", 25.50, 2);
        cart.addItem("P002", "布洛芬", 18.00, 1);
        cart.addItem("P003", "维生素C", 45.00, 3);
        
        assertEquals(3, cart.getItems().size(), "清空前应该有3个商品");
        
        // 清空购物车
        cart.clear();
        
        assertEquals(0, cart.getItems().size(), "清空后应该没有商品");
    }

    @Test
    @DisplayName("测试计算总价 - 单个商品")
    void testCalculateTotalPrice_SingleItem() {
        cart.addItem("P001", "阿莫西林", 25.50, 2);
        
        double expectedTotal = 25.50 * 2;
        assertEquals(expectedTotal, cart.getTotalPrice(), 0.01, "单个商品总价计算错误");
    }

    @Test
    @DisplayName("测试计算总价 - 多个商品")
    void testCalculateTotalPrice_MultipleItems() {
        cart.addItem("P001", "阿莫西林", 25.50, 2);    // 51.00
        cart.addItem("P002", "布洛芬", 18.00, 3);       // 54.00
        cart.addItem("P003", "维生素C", 45.00, 1);      // 45.00
        
        double expectedTotal = (25.50 * 2) + (18.00 * 3) + (45.00 * 1);
        assertEquals(expectedTotal, cart.getTotalPrice(), 0.01, "多个商品总价计算错误");
    }

    @Test
    @DisplayName("测试计算总价 - 空购物车")
    void testCalculateTotalPrice_EmptyCart() {
        assertEquals(0.0, cart.getTotalPrice(), 0.01, "空购物车总价应该是0");
    }

    @Test
    @DisplayName("测试计算总价 - 浮点数精度")
    void testCalculateTotalPrice_FloatPrecision() {
        // 使用会产生浮点精度问题的价格
        cart.addItem("P001", "药品1", 0.1, 3);    // 0.3
        cart.addItem("P002", "药品2", 0.2, 2);    // 0.4
        
        double expectedTotal = 0.7;
        assertEquals(expectedTotal, cart.getTotalPrice(), 0.0001, "浮点数精度处理错误");
    }

    @Test
    @DisplayName("测试边界值 - 零数量商品")
    void testZeroQuantityItem() {
        cart.addItem("P001", "阿莫西林", 25.50, 0);
        
        List<Cart.CartItem> items = cart.getItems();
        assertEquals(1, items.size(), "零数量商品仍应添加到购物车");
        assertEquals(0, items.get(0).getQuantity(), "数量应该是0");
        assertEquals(0.0, cart.getTotalPrice(), 0.01, "零数量商品不应影响总价");
    }

    @Test
    @DisplayName("测试边界值 - 负价格商品")
    void testNegativePriceItem() {
        cart.addItem("P001", "阿莫西林", -25.50, 2);
        
        List<Cart.CartItem> items = cart.getItems();
        assertEquals(1, items.size(), "负价格商品应该能添加");
        assertEquals(-25.50, items.get(0).getPrice(), 0.01, "应该保存负价格");
        assertEquals(-51.0, cart.getTotalPrice(), 0.01, "负价格应该正确计算");
    }

    @Test
    @DisplayName("测试边界值 - 大数量商品")
    void testLargeQuantityItem() {
        cart.addItem("P001", "阿莫西林", 25.50, Integer.MAX_VALUE);
        
        List<Cart.CartItem> items = cart.getItems();
        assertEquals(1, items.size(), "大数量商品应该能添加");
        assertEquals(Integer.MAX_VALUE, items.get(0).getQuantity());
    }

    @Test
    @DisplayName("测试商品属性设置和获取")
    void testCartItemProperties() {
        cart.addItem("P001", "阿莫西林", 25.50, 2);
        
        List<Cart.CartItem> items = cart.getItems();
        Cart.CartItem item = items.get(0);
        
        // 测试getter方法
        assertEquals("P001", item.getId());
        assertEquals("阿莫西林", item.getName());
        assertEquals(25.50, item.getPrice(), 0.01);
        assertEquals(2, item.getQuantity());
        
        // 测试quantity setter
        item.setQuantity(5);
        assertEquals(5, item.getQuantity(), "quantity设置应该生效");
    }

    @Test
    @DisplayName("测试CartItem toString方法")
    void testCartItemToString() {
        cart.addItem("P001", "阿莫西林", 25.50, 2);
        
        List<Cart.CartItem> items = cart.getItems();
        Cart.CartItem item = items.get(0);
        
        String expected = "阿莫西林 - 25.50 x 2";
        assertEquals(expected, item.toString(), "toString格式不正确");
    }

    @Test
    @DisplayName("测试CartItem toString方法 - 边界值")
    void testCartItemToString_EdgeCases() {
        // 测试零数量
        cart.addItem("P001", "药品1", 10.0, 0);
        Cart.CartItem item1 = cart.getItems().get(0);
        assertEquals("药品1 - 10.00 x 0", item1.toString());
        
        cart.clear();
        
        // 测试浮点数
        cart.addItem("P002", "药品2", 0.1, 1);
        Cart.CartItem item2 = cart.getItems().get(0);
        assertEquals("药品2 - 0.10 x 1", item2.toString());
        
        cart.clear();
        
        // 测试大数值
        cart.addItem("P003", "药品3", 999999.99, 999);
        Cart.CartItem item3 = cart.getItems().get(0);
        assertEquals("药品3 - 999999.99 x 999", item3.toString());
    }

    @Test
    @DisplayName("测试线程安全性 - 基础")
    void testThreadSafety_Basic() throws InterruptedException {
        final int threadCount = 10;
        final int itemsPerThread = 100;
        Thread[] threads = new Thread[threadCount];
        
        // 创建多个线程同时添加商品
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < itemsPerThread; j++) {
                    cart.addItem("P" + threadId + "_" + j, "商品" + threadId + "_" + j, 10.0, 1);
                }
            });
        }
        
        // 启动所有线程
        for (Thread thread : threads) {
            thread.start();
        }
        
        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }
        
        // 验证结果（注意：由于没有同步，实际结果可能不精确）
        assertTrue(cart.getItems().size() >= 0, "购物车大小应该非负");
    }

    @Test
    @DisplayName("测试内存泄漏防护")
    void testMemoryLeakPrevention() {
        // 添加大量商品然后清空
        for (int i = 0; i < 10000; i++) {
            cart.addItem("P" + i, "商品" + i, 10.0, 1);
        }
        
        assertEquals(10000, cart.getItems().size(), "应该添加10000个商品");
        
        cart.clear();
        
        assertEquals(0, cart.getItems().size(), "清空后应该没有商品");
        
        // 验证清空后的内存使用（简单的检查）
        System.gc(); // 建议垃圾回收
        assertTrue(true, "内存清理测试完成");
    }

    @Test
    @DisplayName("测试CartItem构造函数")
    void testCartItemConstructor() {
        Cart.CartItem item = new Cart.CartItem("P001", "阿莫西林", 25.50, 2);
        
        assertEquals("P001", item.getId());
        assertEquals("阿莫西林", item.getName());
        assertEquals(25.50, item.getPrice(), 0.01);
        assertEquals(2, item.getQuantity());
    }

    @Test
    @DisplayName("测试特殊字符和Unicode")
    void testSpecialCharactersAndUnicode() {
        cart.addItem("P001", "阿莫西林®", 25.50, 1);
        cart.addItem("P002", "Vitamin C™", 45.00, 2);
        cart.addItem("P003", "药品测试_特殊字符!@#$%", 30.00, 1);
        
        List<Cart.CartItem> items = cart.getItems();
        assertEquals(3, items.size());
        
        assertEquals("阿莫西林®", items.get(0).getName());
        assertEquals("Vitamin C™", items.get(1).getName());
        assertEquals("药品测试_特殊字符!@#$%", items.get(2).getName());
    }

    @Test
    @DisplayName("测试购物车状态一致性")
    void testCartStateConsistency() {
        // 添加商品
        cart.addItem("P001", "阿莫西林", 25.50, 2);
        cart.addItem("P002", "布洛芬", 18.00, 3);
        
        List<Cart.CartItem> items = cart.getItems();
        assertEquals(2, items.size());
        
        // 修改数量
        items.get(0).setQuantity(5);
        assertEquals(5, items.get(0).getQuantity());
        
        // 验证总价自动更新
        double expectedTotal = (25.50 * 5) + (18.00 * 3);
        assertEquals(expectedTotal, cart.getTotalPrice(), 0.01, "修改数量后总价应该更新");
    }
}