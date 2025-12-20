import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.net.*;
import java.io.*;

public class shopList {
    private DefaultTableModel tableModel;
    // 原始数据
    private Object[][] medicineData;
    private final String[] columnNames = {"ID", "药品名称", "类别", "价格", "库存", "操作"};

    // 从JSON文件加载数据
    private void loadData() {
        List<Object[]> dataList = new ArrayList<>();
        try {
            // 连接到服务端获取数据 (假设服务端运行在本地 8888 端口)
            Socket socket = new Socket(Constant.SERVER_IP, Constant.SERVER_PORT);
            // 发送特殊请求标志以区分普通客户端请求
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            // stringbuilder 用于拼接多行数据
            StringBuilder sb = new StringBuilder();
            String line;
            // 设置UTF-8 编码读取数据
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            socket.close();

            String content = sb.toString();
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
                    dataList.add(new Object[]{id, name, category, price, stock, "购买"});
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

        // 添加“查看购物车”按钮
        JButton viewCartButton = new JButton("查看购物车");
        viewCartButton.setFont(new FontUIResource("Microsoft YaHei", FontUIResource.PLAIN, 14));
        viewCartButton.setBackground(new Color(0, 82, 217));
        viewCartButton.setForeground(Color.WHITE);
        viewCartButton.setFocusPainted(false);
        viewCartButton.addActionListener(e -> showCart());
        topPanel.add(viewCartButton);

        return topPanel;
    }

    // 创建表格和滚动面板
    private JScrollPane createScrollPane() {
        // 创建表格模型
        tableModel = new DefaultTableModel(medicineData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 只有“操作”列可编辑（为了让按钮响应点击）
                return column == 5;
            }
        };

        // 创建表格并添加到滚动面板
        JTable table = new JTable(tableModel);
        beutifyTable(table);
        
        // 设置按钮渲染器和编辑器
        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox(), table));

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

    // 显示购物车
    private void showCart() {
        Cart cart = Cart.getInstance();
        List<Cart.CartItem> items = cart.getItems();
        
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(null, "购物车是空的", "购物车", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder message = new StringBuilder("购物车内容:\n\n");
        for (Cart.CartItem item : items) {
            message.append(item.toString()).append("\n");
        }
        message.append("\n总价: ").append(String.format("%.2f", cart.getTotalPrice()));

        // 这里可以使用更复杂的对话框，但为了简单起见，使用 MessageDialog
        // 也可以添加清空购物车的选项
        Object[] options = {"确定", "清空购物车"};
        int choice = JOptionPane.showOptionDialog(null, message.toString(), "购物车",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        
        if (choice == 1) { // 清空购物车
            cart.clear();
            JOptionPane.showMessageDialog(null, "购物车已清空");
        }
    }

    // 按钮渲染器
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(new Color(0, 150, 0)); // 绿色按钮
            setForeground(Color.WHITE);
            setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "购买" : value.toString());
            return this;
        }
    }

    // 按钮编辑器
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private JTable table;

        public ButtonEditor(JCheckBox checkBox, JTable table) {
            super(checkBox);
            this.table = table;
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(new Color(0, 150, 0));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "购买" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                // 执行购买逻辑
                int row = table.getSelectedRow();
                // 注意：如果有排序或过滤，需要转换 row index。
                // 这里暂时假设没有排序，直接使用 table.getValueAt
                // 但因为我们用了 filtering in updateTableData which clears and re-adds rows, 
                // the row index in model matches view if we use table.getValueAt directly on the view's data
                
                String id = (String) table.getValueAt(row, 0);
                String name = (String) table.getValueAt(row, 1);
                String priceStr = (String) table.getValueAt(row, 3);
                double price = 0.0;
                try {
                    price = Double.parseDouble(priceStr);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                // 添加到购物车
                Cart.getInstance().addItem(id, name, price, 1);
                JOptionPane.showMessageDialog(null, "已添加 " + name + " 到购物车");
            }
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}
