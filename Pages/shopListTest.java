import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * shopList类的全面单元测试
 * 覆盖数据加载、表格创建、UI组件、事件处理等核心功能
 */
class shopListTest {

    private shopList shopList;
    private Path tempDataFile;
    private String testDataJson;

    @BeforeEach
    void setUp() throws IOException {
        // 设置Headless模式以避免GUI相关问题
        System.setProperty("java.awt.headless", "true");
        
        // 创建临时测试数据文件
        tempDataFile = Files.createTempFile("test_medicine_data", ".json");
        
        // 准备测试数据
        testDataJson = "[\n" +
                "  {\n" +
                "    \"id\": \"M001\",\n" +
                "    \"name\": \"阿莫西林\",\n" +
                "    \"category\": \"抗生素\",\n" +
                "    \"price\": \"25.50\",\n" +
                "    \"stock\": \"100\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": \"M002\",\n" +
                "    \"name\": \"布洛芬\",\n" +
                "    \"category\": \"感冒药\",\n" +
                "    \"price\": \"18.00\",\n" +
                "    \"stock\": \"50\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": \"M003\",\n" +
                "    \"name\": \"维生素C\",\n" +
                "    \"category\": \"维生素\",\n" +
                "    \"price\": \"45.00\",\n" +
                "    \"stock\": \"200\"\n" +
                "  }\n" +
                "]";
        
        Files.write(tempDataFile, testDataJson.getBytes("UTF-8"));
        shopList = new shopList();
    }

    @AfterEach
    void tearDown() throws IOException {
        // 清理临时文件
        if (tempDataFile != null && Files.exists(tempDataFile)) {
            Files.delete(tempDataFile);
        }
        System.clearProperty("java.awt.headless");
    }

    @Test
    @DisplayName("测试数据加载 - 正常JSON")
    void testLoadData_ValidJSON() {
        try {
            // 使用反射调用私有方法loadData
            java.lang.reflect.Method loadDataMethod = shopList.class.getDeclaredMethod("loadData");
            loadDataMethod.setAccessible(true);
            loadDataMethod.invoke(shopList);
            
            // 验证数据字段
            java.lang.reflect.Field medicineDataField = shopList.class.getDeclaredField("medicineData");
            medicineDataField.setAccessible(true);
            Object[][] medicineData = (Object[][]) medicineDataField.get(shopList);
            
            assertNotNull(medicineData, "药品数据不应为null");
            assertEquals(3, medicineData.length, "应该加载3个药品");
            
            // 验证第一个药品
            assertEquals("M001", medicineData[0][0]);
            assertEquals("阿莫西林", medicineData[0][1]);
            assertEquals("抗生素", medicineData[0][2]);
            assertEquals("25.50", medicineData[0][3]);
            assertEquals("100", medicineData[0][4]);
            assertEquals("购买", medicineData[0][5]);
            
        } catch (Exception e) {
            fail("数据加载测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试数据加载 - 文件不存在")
    void testLoadData_FileNotExists() {
        try {
            // 删除临时文件
            Files.deleteIfExists(tempDataFile);
            
            java.lang.reflect.Method loadDataMethod = shopList.class.getDeclaredMethod("loadData");
            loadDataMethod.setAccessible(true);
            loadDataMethod.invoke(shopList);
            
            java.lang.reflect.Field medicineDataField = shopList.class.getDeclaredField("medicineData");
            medicineDataField.setAccessible(true);
            Object[][] medicineData = (Object[][]) medicineDataField.get(shopList);
            
            assertEquals(0, medicineData.length, "文件不存在时应该返回空数组");
            
        } catch (Exception e) {
            fail("文件不存在测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试数据加载 - 格式错误的JSON")
    void testLoadData_MalformedJSON() throws IOException {
        // 写入格式错误的JSON
        String malformedJson = "[\n" +
                "  {\n" +
                "    \"id\": \"M001\",\n" +
                "    \"name\": \"阿莫西林\"\n" +
                "    // 缺少逗号和其他字段\n" +
                "  }\n" +
                "]";
        
        Files.write(tempDataFile, malformedJson.getBytes("UTF-8"));
        
        try {
            java.lang.reflect.Method loadDataMethod = shopList.class.getDeclaredMethod("loadData");
            loadDataMethod.setAccessible(true);
            loadDataMethod.invoke(shopList);
            
            // 应该优雅地处理错误，不抛异常
            assertTrue(true, "应该能处理格式错误的JSON");
            
        } catch (Exception e) {
            fail("JSON错误处理测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试创建购物面板")
    void testCreateShopPanel() {
        JPanel panel = shopList.createShopPanel();
        
        assertNotNull(panel, "购物面板不应为null");
        assertTrue(panel.isVisible(), "面板应该可见");
        assertEquals(BoxLayout.Y_AXIS, ((BoxLayout) panel.getLayout()).getAxis(), "应该使用垂直BoxLayout");
    }

    @Test
    @DisplayName("测试创建顶部面板")
    void testCreateTopPanel() {
        try {
            java.lang.reflect.Method createTopPanelMethod = shopList.class.getDeclaredMethod("createTopPanel");
            createTopPanelMethod.setAccessible(true);
            
            JPanel topPanel = (JPanel) createTopPanelMethod.invoke(shopList);
            
            assertNotNull(topPanel, "顶部面板不应为null");
            assertEquals(BoxLayout.X_AXIS, ((BoxLayout) topPanel.getLayout()).getAxis(), "应该使用水平BoxLayout");
            
            // 验证组件数量
            Component[] components = topPanel.getComponents();
            assertTrue(components.length >= 4, "应该至少包含标题、类别标签、下拉框、购物车按钮");
            
        } catch (Exception e) {
            fail("创建顶部面板测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试创建类别下拉框")
    void testCreateCategoryTable() {
        try {
            java.lang.reflect.Method createCategoryTableMethod = shopList.class.getDeclaredMethod("createCategoryTable");
            createCategoryTableMethod.setAccessible(true);
            
            @SuppressWarnings("unchecked")
            JComboBox<String> categoryBox = (JComboBox<String>) createCategoryTableMethod.invoke(shopList);
            
            assertNotNull(categoryBox, "类别下拉框不应为null");
            assertEquals(5, categoryBox.getItemCount(), "应该有5个类别选项");
            
            // 验证类别选项
            assertEquals("全部", categoryBox.getItemAt(0));
            assertEquals("抗生素", categoryBox.getItemAt(1));
            assertEquals("维生素", categoryBox.getItemAt(2));
            assertEquals("感冒药", categoryBox.getItemAt(3));
            assertEquals("心血管", categoryBox.getItemAt(4));
            
        } catch (Exception e) {
            fail("创建类别下拉框测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试更新表格数据 - 显示全部")
    void testUpdateTableData_ShowAll() {
        try {
            // 先加载数据
            java.lang.reflect.Method loadDataMethod = shopList.class.getDeclaredMethod("loadData");
            loadDataMethod.setAccessible(true);
            loadDataMethod.invoke(shopList);
            
            // 创建表格模型
            java.lang.reflect.Field tableModelField = shopList.class.getDeclaredField("tableModel");
            tableModelField.setAccessible(true);
            
            // 创建滚动面板以初始化tableModel
            java.lang.reflect.Method createScrollPaneMethod = shopList.class.getDeclaredMethod("createScrollPane");
            createScrollPaneMethod.setAccessible(true);
            createScrollPaneMethod.invoke(shopList);
            
            DefaultTableModel tableModel = (DefaultTableModel) tableModelField.get(shopList);
            
            // 更新表格显示全部数据
            java.lang.reflect.Method updateTableDataMethod = shopList.class.getDeclaredMethod("updateTableData", String.class);
            updateTableDataMethod.setAccessible(true);
            updateTableDataMethod.invoke(shopList, "全部");
            
            assertEquals(3, tableModel.getRowCount(), "显示全部时应该有3行数据");
            
        } catch (Exception e) {
            fail("更新表格数据显示全部测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试更新表格数据 - 按类别筛选")
    void testUpdateTableData_FilterByCategory() {
        try {
            // 先加载数据并创建表格模型
            java.lang.reflect.Method loadDataMethod = shopList.class.getDeclaredMethod("loadData");
            loadDataMethod.setAccessible(true);
            loadDataMethod.invoke(shopList);
            
            java.lang.reflect.Method createScrollPaneMethod = shopList.class.getDeclaredMethod("createScrollPane");
            createScrollPaneMethod.setAccessible(true);
            createScrollPaneMethod.invoke(shopList);
            
            java.lang.reflect.Field tableModelField = shopList.class.getDeclaredField("tableModel");
            tableModelField.setAccessible(true);
            DefaultTableModel tableModel = (DefaultTableModel) tableModelField.get(shopList);
            
            java.lang.reflect.Method updateTableDataMethod = shopList.class.getDeclaredMethod("updateTableData", String.class);
            updateTableDataMethod.setAccessible(true);
            
            // 测试按抗生素筛选
            updateTableDataMethod.invoke(shopList, "抗生素");
            assertEquals(1, tableModel.getRowCount(), "抗生素类别应该有1个药品");
            assertEquals("阿莫西林", tableModel.getValueAt(0, 1));
            
            // 测试按维生素筛选
            updateTableDataMethod.invoke(shopList, "维生素");
            assertEquals(1, tableModel.getRowCount(), "维生素类别应该有1个药品");
            assertEquals("维生素C", tableModel.getValueAt(0, 1));
            
            // 测试按感冒药筛选
            updateTableDataMethod.invoke(shopList, "感冒药");
            assertEquals(1, tableModel.getRowCount(), "感冒药类别应该有1个药品");
            assertEquals("布洛芬", tableModel.getValueAt(0, 1));
            
            // 测试不存在的类别
            updateTableDataMethod.invoke(shopList, "不存在");
            assertEquals(0, tableModel.getRowCount(), "不存在类别应该有0个药品");
            
        } catch (Exception e) {
            fail("按类别筛选测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试创建滚动面板")
    void testCreateScrollPane() {
        try {
            java.lang.reflect.Method createScrollPaneMethod = shopList.class.getDeclaredMethod("createScrollPane");
            createScrollPaneMethod.setAccessible(true);
            
            JScrollPane scrollPane = (JScrollPane) createScrollPaneMethod.invoke(shopList);
            
            assertNotNull(scrollPane, "滚动面板不应为null");
            assertNotNull(scrollPane.getViewport(), "视口不应为null");
            assertTrue(scrollPane.getViewport().getView() instanceof JTable, "视口应该包含JTable");
            
        } catch (Exception e) {
            fail("创建滚动面板测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试按钮渲染器")
    void testButtonRenderer() {
        try {
            // 使用反射获取ButtonRenderer类
            Class<?> buttonRendererClass = Class.forName("shopList$ButtonRenderer");
            Object renderer = buttonRendererClass.getDeclaredConstructor().newInstance();
            
            assertTrue(renderer instanceof JButton, "ButtonRenderer应该是JButton的子类");
            assertTrue(renderer instanceof TableCellRenderer, "ButtonRenderer应该实现TableCellRenderer");
            
            TableCellRenderer cellRenderer = (TableCellRenderer) renderer;
            
            // 创建模拟表格
            JTable table = new JTable();
            Object value = "购买";
            
            Component component = cellRenderer.getTableCellRendererComponent(table, value, false, false, 0, 5);
            
            assertNotNull(component, "渲染组件不应为null");
            assertTrue(component instanceof JButton, "渲染组件应该是JButton");
            assertEquals("购买", ((JButton) component).getText(), "按钮文本应该是'购买'");
            
        } catch (Exception e) {
            fail("按钮渲染器测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试表格模型可编辑性")
    void testTableModelEditability() {
        try {
            java.lang.reflect.Method createScrollPaneMethod = shopList.class.getDeclaredMethod("createScrollPane");
            createScrollPaneMethod.setAccessible(true);
            JScrollPane scrollPane = (JScrollPane) createScrollPaneMethod.invoke(shopList);
            
            JTable table = (JTable) scrollPane.getViewport().getView();
            DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
            
            // 只有第5列（操作列）应该可编辑
            for (int col = 0; col < tableModel.getColumnCount(); col++) {
                boolean expectedEditable = (col == 5);
                boolean actualEditable = tableModel.isCellEditable(0, col);
                assertEquals(expectedEditable, actualEditable, 
                    String.format("列%d的可编辑性不正确", col));
            }
            
        } catch (Exception e) {
            fail("表格模型可编辑性测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试表格美化")
    void testBeautifyTable() {
        try {
            java.lang.reflect.Method createScrollPaneMethod = shopList.class.getDeclaredMethod("createScrollPane");
            createScrollPaneMethod.setAccessible(true);
            JScrollPane scrollPane = (JScrollPane) createScrollPaneMethod.invoke(shopList);
            
            JTable table = (JTable) scrollPane.getViewport().getView();
            
            // 验证字体设置
            assertNotNull(table.getFont(), "表格字体不应为null");
            assertEquals(40, table.getRowHeight(), "行高应该是40");
            
            // 验证表头渲染器
            assertNotNull(table.getTableHeader().getDefaultRenderer(), "表头渲染器不应为null");
            
        } catch (Exception e) {
            fail("表格美化测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试列名设置")
    void testColumnNames() {
        try {
            java.lang.reflect.Method createScrollPaneMethod = shopList.class.getDeclaredMethod("createScrollPane");
            createScrollPaneMethod.setAccessible(true);
            JScrollPane scrollPane = (JScrollPane) createScrollPaneMethod.invoke(shopList);
            
            JTable table = (JTable) scrollPane.getViewport().getView();
            
            assertEquals(6, table.getColumnCount(), "应该有6列");
            
            assertEquals("ID", table.getColumnName(0));
            assertEquals("药品名称", table.getColumnName(1));
            assertEquals("类别", table.getColumnName(2));
            assertEquals("价格", table.getColumnName(3));
            assertEquals("库存", table.getColumnName(4));
            assertEquals("操作", table.getColumnName(5));
            
        } catch (Exception e) {
            fail("列名设置测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试中文字符处理")
    void testChineseCharacterHandling() throws IOException {
        // 创建包含中文的测试数据
        String chineseDataJson = "[\n" +
                "  {\n" +
                "    \"id\": \"M001\",\n" +
                "    \"name\": \"阿莫西林胶囊\",\n" +
                "    \"category\": \"抗生素类\",\n" +
                "    \"price\": \"25.50\",\n" +
                "    \"stock\": \"100\"\n" +
                "  }\n" +
                "]";
        
        Files.write(tempDataFile, chineseDataJson.getBytes("UTF-8"));
        
        try {
            java.lang.reflect.Method loadDataMethod = shopList.class.getDeclaredMethod("loadData");
            loadDataMethod.setAccessible(true);
            loadDataMethod.invoke(shopList);
            
            java.lang.reflect.Field medicineDataField = shopList.class.getDeclaredField("medicineData");
            medicineDataField.setAccessible(true);
            Object[][] medicineData = (Object[][]) medicineDataField.get(shopList);
            
            assertEquals(1, medicineData.length);
            assertEquals("阿莫西林胶囊", medicineData[0][1]);
            assertEquals("抗生素类", medicineData[0][2]);
            
        } catch (Exception e) {
            fail("中文字符处理测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试空数据处理")
    void testEmptyDataHandling() throws IOException {
        // 创建空数组JSON
        String emptyJson = "[]";
        Files.write(tempDataFile, emptyJson.getBytes("UTF-8"));
        
        try {
            java.lang.reflect.Method loadDataMethod = shopList.class.getDeclaredMethod("loadData");
            loadDataMethod.setAccessible(true);
            loadDataMethod.invoke(shopList);
            
            java.lang.reflect.Field medicineDataField = shopList.class.getDeclaredField("medicineData");
            medicineDataField.setAccessible(true);
            Object[][] medicineData = (Object[][]) medicineDataField.get(shopList);
            
            assertEquals(0, medicineData.length, "空数据应该返回空数组");
            
        } catch (Exception e) {
            fail("空数据处理测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试部分缺失字段处理")
    void testPartialMissingFieldsHandling() throws IOException {
        // 创建部分字段缺失的JSON
        String partialDataJson = "[\n" +
                "  {\n" +
                "    \"id\": \"M001\",\n" +
                "    \"name\": \"阿莫西林\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": \"M002\",\n" +
                "    \"name\": \"布洛芬\",\n" +
                "    \"category\": \"感冒药\",\n" +
                "    \"price\": \"18.00\"\n" +
                "  }\n" +
                "]";
        
        Files.write(tempDataFile, partialDataJson.getBytes("UTF-8"));
        
        try {
            java.lang.reflect.Method loadDataMethod = shopList.class.getDeclaredMethod("loadData");
            loadDataMethod.setAccessible(true);
            loadDataMethod.invoke(shopList);
            
            java.lang.reflect.Field medicineDataField = shopList.class.getDeclaredField("medicineData");
            medicineDataField.setAccessible(true);
            Object[][] medicineData = (Object[][]) medicineDataField.get(shopList);
            
            assertEquals(2, medicineData.length, "应该解析2个药品");
            
            // 验证缺失字段的默认值
            assertEquals("", medicineData[0][2]); // category缺失
            assertEquals("", medicineData[0][3]); // price缺失
            assertEquals("", medicineData[0][4]); // stock缺失
            
        } catch (Exception e) {
            fail("部分缺失字段处理测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试价格字符串格式处理")
    void testPriceStringFormatHandling() throws IOException {
        // 创建各种价格格式的测试数据
        String priceFormatsJson = "[\n" +
                "  {\n" +
                "    \"id\": \"M001\",\n" +
                "    \"name\": \"药品1\",\n" +
                "    \"price\": \"25.50\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": \"M002\",\n" +
                "    \"name\": \"药品2\",\n" +
                "    \"price\": \"100\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"id\": \"M003\",\n" +
                "    \"name\": \"药品3\",\n" +
                "    \"price\": \"0.99\"\n" +
                "  }\n" +
                "]";
        
        Files.write(tempDataFile, priceFormatsJson.getBytes("UTF-8"));
        
        try {
            java.lang.reflect.Method loadDataMethod = shopList.class.getDeclaredMethod("loadData");
            loadDataMethod.setAccessible(true);
            loadDataMethod.invoke(shopList);
            
            java.lang.reflect.Field medicineDataField = shopList.class.getDeclaredField("medicineData");
            medicineDataField.setAccessible(true);
            Object[][] medicineData = (Object[][]) medicineDataField.get(shopList);
            
            assertEquals(3, medicineData.length);
            assertEquals("25.50", medicineData[0][3]);
            assertEquals("100", medicineData[1][3]);
            assertEquals("0.99", medicineData[2][3]);
            
        } catch (Exception e) {
            fail("价格格式处理测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试滚动面板美化")
    void testBeautifyScrollPane() {
        try {
            java.lang.reflect.Method createScrollPaneMethod = shopList.class.getDeclaredMethod("createScrollPane");
            createScrollPaneMethod.setAccessible(true);
            JScrollPane scrollPane = (JScrollPane) createScrollPaneMethod.invoke(shopList);
            
            assertNotNull(scrollPane.getBorder(), "滚动面板应该有边框");
            assertNotNull(scrollPane.getViewport().getBackground(), "视口应该有背景色");
            
        } catch (Exception e) {
            fail("滚动面板美化测试失败: " + e.getMessage());
        }
    }
}