import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.FontUIResource;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class shopList {
    private DefaultTableModel tableModel;
    // 原始数据
    private Object[][] medicineData;
    private final String[] columnNames = {"ID", "药品名称", "类别", "价格", "库存"};

    // 从JSON文件加载数据
    private void loadData() {
        List<Object[]> dataList = new ArrayList<>();
        try {
            File file = new File("storage/data.json");
            if (!file.exists()) {
                System.err.println("数据文件不存在: " + file.getAbsolutePath());
                medicineData = new Object[0][0];
                return;
            }

            String content = new String(Files.readAllBytes(Paths.get("storage/data.json")), "UTF-8");
            content = content.trim();
            
            // 简单的JSON解析逻辑
            if (content.startsWith("[") && content.endsWith("]")) {
                content = content.substring(1, content.length() - 1);
            }
            
            // 分割对象
            String[] objects = content.split("(?<=\\}),\\s*");
            
            for (String obj : objects) {
                // trim表示去掉前后空格
                obj = obj.trim();
                if (obj.startsWith("{")) obj = obj.substring(1);
                if (obj.endsWith("}")) obj = obj.substring(0, obj.length() - 1);
                
                String[] pairs = obj.split(",");
                String id = "", name = "", category = "", price = "", stock = "";
                
                for (String pair : pairs) {
                    String[] kv = pair.split(":");
                    if (kv.length == 2) {
                        String key = kv[0].trim().replace("\"", "");
                        String value = kv[1].trim().replace("\"", "");
                        
                        switch (key) {
                            case "id": id = value; break;
                            case "name": name = value; break;
                            case "category": category = value; break;
                            case "price": price = value; break;
                            case "stock": stock = value; break;
                        }
                    }
                }
                // 只有当解析出有效数据时才添加
                if (!id.isEmpty()) {
                    dataList.add(new Object[]{id, name, category, price, stock});
                }
            }
            
            medicineData = dataList.toArray(new Object[0][]);
            
        } catch (Exception e) {
            e.printStackTrace();
            medicineData = new Object[0][0];
        }
    }

    // 创建购物面板
    public JPanel createShopPanel() {
        loadData();
        JPanel panel = new JPanel();
        panel.setSize(1200, 900);
        // 使用 BoxLayout 垂直布局
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        // 保持内边距和外边距 (32px)
        panel.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        // 创建顶部面板
        JPanel topPanel = createTopPanel();
        panel.add(topPanel);

        // 增加垂直间距
        panel.add(Box.createVerticalStrut(24)); 

        // 创建表格和滚动面板
        JScrollPane scrollPane = createScrollPane();
        panel.add(scrollPane);

        return panel;
    }

    // 创建顶部面板
    private JPanel createTopPanel() {
        // 顶部面板
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setMaximumSize(new DimensionUIResource(Integer.MAX_VALUE, 40)); 
        
        // “药品列表”标签
        JLabel titleLabel = new JLabel("药品列表");
        titleLabel.setFont(new FontUIResource("Microsoft YaHei", FontUIResource.BOLD, 24));
        topPanel.add(titleLabel);
        topPanel.add(Box.createHorizontalStrut(40)); 

        // “选择类别”标签
        JLabel categoryLabel = new JLabel("选择类别:");
        categoryLabel.setFont(new FontUIResource("Microsoft YaHei", FontUIResource.PLAIN, 16));
        topPanel.add(categoryLabel);
        topPanel.add(Box.createHorizontalStrut(10));

        // 创建类别下拉列表
        JComboBox<String> categoryBox = createCategoryTable();
        categoryBox.setMaximumSize(new DimensionUIResource(150, 30)); 
        topPanel.add(categoryBox); 
        topPanel.add(Box.createHorizontalGlue());

        return topPanel;
    }

    // 创建表格和滚动面板
    private JScrollPane createScrollPane() {
        // 创建表格模型
        tableModel = new DefaultTableModel(medicineData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // 创建表格并添加到滚动面板
        JTable table = new JTable(tableModel);
        beutifyTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        beutifyScrollPane(scrollPane);
        return scrollPane;
    }


    private JComboBox<String> createCategoryTable() {
        String[] categories = {"全部", "抗生素", "维生素", "感冒药", "心血管"};
        JComboBox<String> categoryBox = new JComboBox<>(categories);
        categoryBox.setFont(new FontUIResource("Microsoft YaHei", FontUIResource.PLAIN, 14));
        // 添加事件监听器
        categoryBox.addActionListener(e -> updateTableData((String) categoryBox.getSelectedItem()));
        return categoryBox;
    }

    // 更新表格数据
    private void updateTableData(String category) {
        // setRowCount 用于设置表格的行数
        tableModel.setRowCount(0); // 清空当前表格数据
        for (Object[] row : medicineData) {
            // 如果选择"全部"或者类别匹配，则添加该行
            if ("全部".equals(category) || row[2].equals(category)) {
                tableModel.addRow(row);
            }
        }
    }


    // 美化表格边框和滚动条
    private void beutifyTable(JTable table) {
        // 美化表格样式
        table.setFont(new FontUIResource("Microsoft YaHei", FontUIResource.PLAIN, 14));
        // 增加行高以提升阅读体验
        table.setRowHeight(40); 
        // 自定义表头渲染器
        javax.swing.table.DefaultTableCellRenderer headerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        headerRenderer.setBackground(new ColorUIResource(0, 82, 217));
        headerRenderer.setForeground(new ColorUIResource(255, 255, 255));
        headerRenderer.setFont(new FontUIResource("Microsoft YaHei", FontUIResource.BOLD, 14));
        headerRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        table.getTableHeader().setDefaultRenderer(headerRenderer);
        
        table.setSelectionBackground(new ColorUIResource(51, 153, 255)); 
        table.setSelectionForeground(new ColorUIResource(255, 255, 255)); 
        table.setGridColor(new ColorUIResource(220, 220, 220));
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setFillsViewportHeight(true);
    }
    
    // 美化滚动面板
    private void beutifyScrollPane(JScrollPane scrollPane) {
        scrollPane.getViewport().setBackground(new ColorUIResource(255, 255, 255)); 
        // 设置优美的边框粗细和圆角
        scrollPane.setBorder(new javax.swing.border.LineBorder(new ColorUIResource(220, 220, 220), 5, true));
    }
}
