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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import java.util.ArrayList;
import java.util.List;
import java.net.*;
import java.io.*;

public class ShopList {
    private DefaultTableModel tableModel;
    private JTable table; // 表格对象，改为成员变量以便在其他方法中访问
    // 原始数据
    private Object[][] medicineData;
    private final String[] columnNames = { "ID", "药品名称", "类别", "价格", "库存", "操作" };

    // 顶部显示总价的标签（在 createTopPanel 中初始化）
    private JLabel totalPriceLabel;

    // 从JSON文件加载数据
    private void loadData() {
        List<Object[]> dataList = new ArrayList<>();
        try {
            // 连接到服务端获取数据 (假设服务端运行在本地 8888 端口)
            Socket socket = new Socket(Constant.SERVER_IP, Constant.SERVER_PORT);

            // 发送int类型的请求选项id和String类型的请求信息message
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeInt(3);
            dos.writeUTF("获取商品列表");
            dos.flush();

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
                if (obj.startsWith("{"))
                    obj = obj.substring(1);
                if (obj.endsWith("}"))
                    obj = obj.substring(0, obj.length() - 1);

                String[] pairs = obj.split(",");
                String id = "", name = "", category = "", price = "", stock = "";

                for (String pair : pairs) {
                    String[] kv = pair.split(":");
                    if (kv.length == 2) {
                        String key = kv[0].trim().replace("\"", "");
                        String value = kv[1].trim().replace("\"", "");

                        switch (key) {
                            case "id":
                                id = value;
                                break;
                            case "name":
                                name = value;
                                break;
                            case "category":
                                category = value;
                                break;
                            case "price":
                                price = value;
                                break;
                            case "stock":
                                stock = value;
                                break;
                        }
                    }
                }
                // 只有当解析出有效数据时才添加
                if (!id.isEmpty()) {
                    dataList.add(new Object[] { id, name, category, price, stock, "操作" });
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
        panel.setSize(Constant.STD_WINDOWS_WIDTH, Constant.STD_WINDOWS_HEIGHT);
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

        // 初次更新总价显示
        updateTotalLabel();

        return panel;
    }

    // 创建顶部面板
    private JPanel createTopPanel() {
        // 顶部面板
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setMaximumSize(new DimensionUIResource(Integer.MAX_VALUE, 40));

        // "药品列表"标签
        JLabel titleLabel = new JLabel("药品列表");
        titleLabel.setFont(new FontUIResource("Microsoft YaHei", FontUIResource.BOLD, 24));
        topPanel.add(titleLabel);
        topPanel.add(Box.createHorizontalStrut(40));

        // "选择类别"标签
        JLabel categoryLabel = new JLabel("选择类别:");
        categoryLabel.setFont(new FontUIResource("Microsoft YaHei", FontUIResource.PLAIN, 16));
        topPanel.add(categoryLabel);
        topPanel.add(Box.createHorizontalStrut(10));

        // 创建类别下拉列表
        JComboBox<String> categoryBox = createCategoryTable();
        categoryBox.setMaximumSize(new DimensionUIResource(150, 30));
        topPanel.add(categoryBox);

        topPanel.add(Box.createHorizontalGlue());

        // 在"查看购物车"左侧添加总价标签
        totalPriceLabel = new JLabel("总价: 0.00元");
        totalPriceLabel.setFont(new FontUIResource("Microsoft YaHei", FontUIResource.BOLD, 14));
        totalPriceLabel.setForeground(new ColorUIResource(34, 34, 34));
        totalPriceLabel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 12));
        topPanel.add(totalPriceLabel);

        // 添加"查看购物车"按钮（文字变黑）
        JButton viewCartButton = new JButton("查看购物车");
        viewCartButton.setFont(new FontUIResource("Microsoft YaHei", FontUIResource.PLAIN, 14));
        viewCartButton.setBackground(new ColorUIResource(245, 245, 245)); // 浅背景以配合黑字
        viewCartButton.setForeground(Color.BLACK); // 文本黑色
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
                // 只有"操作"列可编辑（为了让控件响应点击/输入）
                return column == 5;
            }
        };

        // 创建表格并添加到滚动面板
        this.table = new JTable(tableModel);
        beutifyTable(this.table);

        // 设置新的渲染器和编辑器：显示 - [数量] +
        this.table.getColumnModel().getColumn(5).setCellRenderer(new QuantityCellRenderer());
        this.table.getColumnModel().getColumn(5).setCellEditor(new QuantityCellEditor(this.table));

        // 设置列宽
        this.table.getColumnModel().getColumn(0).setPreferredWidth(60); // ID列宽度缩小
        this.table.getColumnModel().getColumn(2).setPreferredWidth(80); // 类别列宽度缩小
        this.table.getColumnModel().getColumn(3).setPreferredWidth(80); // 价格列宽度缩小
        this.table.getColumnModel().getColumn(1).setPreferredWidth(200); // 药品名称列宽度增加
        this.table.getColumnModel().getColumn(4).setPreferredWidth(80); // 库存列宽度
        this.table.getColumnModel().getColumn(5).setPreferredWidth(200); // 操作列宽度

        JScrollPane scrollPane = new JScrollPane(this.table);
        beutifyScrollPane(scrollPane);
        return scrollPane;
    }

    private JComboBox<String> createCategoryTable() {
        String[] categories = { "全部", "抗生素", "维生素", "感冒药", "心血管" };
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

    // 显示购物车（增加"下单"选项）
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
        message.append("\n总价: ").append(String.format("%.2f", cart.getTotalPrice())).append("元");

        // 添加"下单"按钮作为第一个选项
        Object[] options = { "下单", "清空购物车", "取消" };
        int choice = JOptionPane.showOptionDialog(null, message.toString(), "购物车",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 1) { // 清空购物车
            // 确保所有单元格编辑已停止
            if (table.isEditing()) {
                table.getCellEditor().stopCellEditing();
            }
            cart.clear();
            updateTotalLabel();
            tableModel.fireTableDataChanged(); // 刷新表格数据，使操作列数量立即变为0
            JOptionPane.showMessageDialog(null, "购物车已清空");
        } else if (choice == 0) { // 下单流程：选择支付方式
            Object[] payOptions = { "微信付款", "支付宝付款", "取消" };
            int payChoice = JOptionPane.showOptionDialog(null, "请选择支付方式", "支付",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, payOptions, payOptions[0]);
            if (payChoice == 0 || payChoice == 1) {
                // 创建订单历史记录
                List<OrderHistory.OrderItem> orderItems = new ArrayList<>();
                for (Cart.CartItem item : cart.getItems()) {
                    // 查找商品类别
                    String category = "";
                    for (Object[] row : medicineData) {
                        if (item.getId().equals(row[0])) {
                            category = (String) row[2];
                            break;
                        }
                    }
                    orderItems.add(new OrderHistory.OrderItem(
                            item.getId(),
                            item.getName(),
                            category,
                            item.getPrice(),
                            item.getQuantity()));
                }

                // 创建订单
                double totalPrice = cart.getTotalPrice();
                OrderHistory.getInstance().createOrder(orderItems, totalPrice);

                // 确保所有单元格编辑已停止
                if (table.isEditing()) {
                    table.getCellEditor().stopCellEditing();
                }
                // 清空购物车
                cart.clear();
                updateTotalLabel();
                tableModel.fireTableDataChanged(); // 刷新表格数据，使操作列数量立即变为0

                // 显示支付成功信息
                if (payChoice == 0) {
                    JOptionPane.showMessageDialog(null, "微信付款成功，订单已生成！");
                } else if (payChoice == 1) {
                    JOptionPane.showMessageDialog(null, "支付宝付款成功，订单已生成！");
                }
            }
        }
    }

    // 渲染器：只负责显示当前购物车数量（不可交互）
    class QuantityCellRenderer extends JPanel implements TableCellRenderer {
        public QuantityCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 6));
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
            String id = (String) table.getValueAt(row, 0);
            int qty = getCartQuantity(id);

            JLabel minus = new JLabel("\u2212", SwingConstants.CENTER); // Unicode 减号
            minus.setPreferredSize(new Dimension(28, 28));
            minus.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
            minus.setBorder(BorderFactory.createLineBorder(new ColorUIResource(220, 220, 220)));
            add(minus);

            // 显示框宽度翻倍（原来约48 -> 96）
            JTextField qtyField = new JTextField(String.valueOf(qty), 4);
            qtyField.setHorizontalAlignment(SwingConstants.CENTER);
            qtyField.setEditable(false);
            qtyField.setPreferredSize(new Dimension(96, 28)); // 翻倍宽度
            qtyField.setBorder(BorderFactory.createLineBorder(new ColorUIResource(200, 200, 200)));
            add(qtyField);

            JLabel plus = new JLabel("+", SwingConstants.CENTER);
            plus.setPreferredSize(new Dimension(28, 28));
            plus.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
            plus.setBorder(BorderFactory.createLineBorder(new ColorUIResource(220, 220, 220)));
            add(plus);

            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            return this;
        }
    }

    // 编辑器：可交互的 - [数量 可输] + 控件
    // 编辑器：可交互的 - [数量 可输] + 控件（含库存检查）
    class QuantityCellEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton minusBtn;
        private JButton plusBtn;
        private JTextField qtyField;
        private JTable table;
        private int editingRow = -1;
        private int previousQty = 0;

        public QuantityCellEditor(JTable table) {
            this.table = table;
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 6));
            minusBtn = new JButton("\u2212");
            plusBtn = new JButton("+");
            qtyField = new JTextField("0", 4);

            // 样式与大小（编辑器中数量框也翻倍：56->112）
            minusBtn.setPreferredSize(new Dimension(34, 28));
            plusBtn.setPreferredSize(new Dimension(34, 28));
            qtyField.setPreferredSize(new Dimension(112, 28)); // 编辑器中文本框加宽
            qtyField.setHorizontalAlignment(SwingConstants.CENTER);
            qtyField.setBorder(BorderFactory.createLineBorder(new ColorUIResource(200, 200, 200)));
            minusBtn.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
            plusBtn.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
            qtyField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));

            // 去掉多余内边距，确保符号可见
            minusBtn.setMargin(new Insets(0, 0, 0, 0));
            plusBtn.setMargin(new Insets(0, 0, 0, 0));
            minusBtn.setFocusable(false);
            plusBtn.setFocusable(false);

            panel.add(minusBtn);
            panel.add(qtyField);
            panel.add(plusBtn);

            // + 号增加数量（受库存限制）
            plusBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editingRow < 0)
                        return;
                    String id = (String) table.getValueAt(editingRow, 0);
                    String name = (String) table.getValueAt(editingRow, 1);
                    String priceStr = (String) table.getValueAt(editingRow, 3);
                    String stockStr = (String) table.getValueAt(editingRow, 4);
                    int stock = parseStock(stockStr);
                    int current = getCartQuantity(id);

                    if (current >= stock) {
                        JOptionPane.showMessageDialog(null, "库存不足", "提示", JOptionPane.WARNING_MESSAGE);
                        qtyField.setText(String.valueOf(current));
                        return;
                    }

                    double price = 0.0;
                    try {
                        price = Double.parseDouble(priceStr);
                    } catch (Exception ex) {
                    }
                    // 增加1（再次检查不超过库存）
                    if (current + 1 > stock) {
                        JOptionPane.showMessageDialog(null, "库存不足", "提示", JOptionPane.WARNING_MESSAGE);
                        qtyField.setText(String.valueOf(current));
                        return;
                    }
                    Cart.getInstance().addItem(id, name, price, 1);
                    int qty = getCartQuantity(id);
                    qtyField.setText(String.valueOf(qty));
                    previousQty = qty;
                    updateTotalLabel();
                }
            });

            // - 号减少数量
            minusBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (editingRow < 0)
                        return;
                    String id = (String) table.getValueAt(editingRow, 0);
                    Cart cart = Cart.getInstance();
                    List<Cart.CartItem> items = cart.getItems();
                    Cart.CartItem found = null;
                    for (Cart.CartItem it : items) {
                        if (it.getId().equals(id)) {
                            found = it;
                            break;
                        }
                    }
                    if (found != null) {
                        int newQty = found.getQuantity() - 1;
                        if (newQty <= 0)
                            cart.removeItem(id);
                        else
                            found.setQuantity(newQty);
                        int qty = getCartQuantity(id);
                        qtyField.setText(String.valueOf(qty));
                        previousQty = qty;
                        updateTotalLabel();
                    } else {
                        qtyField.setText("0");
                        previousQty = 0;
                    }
                }
            });

            // 回车或失焦提交数量（输入受到库存限制）
            qtyField.addActionListener(e -> {
                commitQtyFromFieldWithStock();
                stopCellEditing();
                updateTotalLabel();
            });
            qtyField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    commitQtyFromFieldWithStock();
                    updateTotalLabel();
                }
            });
        }

        private int parseStock(String stockStr) {
            if (stockStr == null)
                return Integer.MAX_VALUE;
            try {
                return Integer.parseInt(stockStr.trim());
            } catch (Exception ex) {
                return Integer.MAX_VALUE;
            }
        }

        // 将文本框的数量解析并设置到购物车（带库存校验）
        private void commitQtyFromFieldWithStock() {
            if (editingRow < 0)
                return;
            String id = (String) table.getValueAt(editingRow, 0);
            String name = (String) table.getValueAt(editingRow, 1);
            String priceStr = (String) table.getValueAt(editingRow, 3);
            String stockStr = (String) table.getValueAt(editingRow, 4);
            int stock = parseStock(stockStr);
            double price = 0.0;
            try {
                price = Double.parseDouble(priceStr);
            } catch (Exception ex) {
            }

            int newQty;
            try {
                newQty = Integer.parseInt(qtyField.getText().trim());
                if (newQty < 0)
                    newQty = 0;
            } catch (NumberFormatException ex) {
                // 非法输入回退
                JOptionPane.showMessageDialog(null, "输入非法，已恢复为原数量", "提示", JOptionPane.WARNING_MESSAGE);
                qtyField.setText(String.valueOf(previousQty));
                return;
            }

            if (newQty > stock) {
                // 超过库存：提示并回退到编辑前数量
                JOptionPane.showMessageDialog(null, "库存不足", "提示", JOptionPane.WARNING_MESSAGE);
                qtyField.setText(String.valueOf(previousQty));
                return;
            }

            // 合法，设置数量（<=0 则移除）
            setCartQuantity(id, name, price, newQty);
            previousQty = getCartQuantity(id);
            qtyField.setText(String.valueOf(previousQty));
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            this.editingRow = row;
            String id = (String) table.getValueAt(row, 0);
            int qty = getCartQuantity(id);
            previousQty = qty;
            qtyField.setText(String.valueOf(qty));
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "操作";
        }
    }

    // 帮助函数：返回购物车中指定商品的数量
    private int getCartQuantity(String id) {
        Cart cart = Cart.getInstance();
        for (Cart.CartItem item : cart.getItems()) {
            if (item.getId().equals(id))
                return item.getQuantity();
        }
        return 0;
    }

    // 帮助函数：设置购物车中指定商品的数量（<=0 则移除）
    private void setCartQuantity(String id, String name, double price, int qty) {
        Cart cart = Cart.getInstance();
        List<Cart.CartItem> items = cart.getItems();
        Cart.CartItem found = null;
        for (Cart.CartItem it : items) {
            if (it.getId().equals(id)) {
                found = it;
                break;
            }
        }
        if (qty <= 0) {
            cart.removeItem(id);
            updateTotalLabel();
            return;
        }
        if (found != null) {
            found.setQuantity(qty);
        } else {
            cart.addItem(id, name, price, qty);
        }
        updateTotalLabel();
    }

    // 更新顶部总价标签
    private void updateTotalLabel() {
        if (totalPriceLabel == null)
            return;
        Cart cart = Cart.getInstance();
        totalPriceLabel.setText("总价: " + String.format("%.2f", cart.getTotalPrice()) + "元");
    }
}