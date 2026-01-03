import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final String PREF_NODE = "AscentSysLogin";
    private static final String PREF_USERNAME_KEY = "rememberedUsername";

    private final Preferences userPreferences = Preferences.userRoot().node(PREF_NODE);

    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JCheckBox rememberAccountCheckBox = new JCheckBox("记住账号");
    private final JLabel statusLabel = new JLabel("请输入账号与密码，解锁药品销售管理系统。");

    private final JButton loginButton = new JButton("立即登录");
    private final JButton registerButton = new JButton("注册账号");
    private final JButton resetButton = new JButton("快速清空");
    private final JButton exitButton = new JButton("退出系统");
    private final JToggleButton showPasswordToggle = new JToggleButton("显示密码");

    private char defaultEchoChar;

    public LoginFrame() {
        configureFrame();
        setContentPane(createRootPanel());

        defaultEchoChar = passwordField.getEchoChar();

        preloadRememberedAccount();
        getRootPane().setDefaultButton(loginButton);
    }

    private void configureFrame() {
        setTitle("AscentSys统一登录");
        // 修改：使用常量设置大小
        setSize(Constant.STD_WINDOWS_WIDTH, Constant.STD_WINDOWS_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(960, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JPanel createRootPanel() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.add(createHeroPanel(), BorderLayout.WEST);
        rootPanel.add(createFormPanel(), BorderLayout.CENTER);
        return rootPanel;
    }

    private JPanel createHeroPanel() {
        GradientPanel heroPanel = new GradientPanel();
        // 不固定高度，避免挤压右侧；宽度给个合理首选值
        heroPanel.setPreferredSize(new Dimension(340, 0));
        heroPanel.setLayout(new BoxLayout(heroPanel, BoxLayout.Y_AXIS));
        heroPanel.setBorder(new EmptyBorder(60, 40, 60, 40));

        JLabel brandLabel = new JLabel("AscentSys", SwingConstants.LEFT);
        brandLabel.setForeground(Color.WHITE);
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));

        JLabel sloganLabel = new JLabel("药品销售管理系统", SwingConstants.LEFT);
        sloganLabel.setForeground(new Color(236, 244, 255));
        sloganLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 20));

        JLabel featureLabel = new JLabel("智能库存 · 极速下单 · 全程可追", SwingConstants.LEFT);
        featureLabel.setForeground(new Color(214, 236, 255));
        featureLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));

        heroPanel.add(brandLabel);
        heroPanel.add(Box.createVerticalStrut(16));
        heroPanel.add(sloganLabel);
        heroPanel.add(Box.createVerticalStrut(24));
        heroPanel.add(featureLabel);
        heroPanel.add(Box.createVerticalGlue());

        return heroPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(40, 60, 40, 60));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1;

        JLabel titleLabel = new JLabel("统一认证中心", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 26));
        gbc.gridy = 0;
        gbc.weighty = 0;
        formPanel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("一键畅享 AscentSys 全链路服务", SwingConstants.LEFT);
        subtitleLabel.setForeground(new Color(75, 85, 99));
        subtitleLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        gbc.gridy = 1;
        formPanel.add(subtitleLabel, gbc);

        JLabel usernameLabel = new JLabel("账号", SwingConstants.LEFT);
        usernameLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        gbc.gridy = 2;
        formPanel.add(usernameLabel, gbc);

        usernameField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        usernameField.setColumns(20);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(55, 65, 81), 2, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        gbc.gridy = 3;
        formPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("密码", SwingConstants.LEFT);
        passwordLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        gbc.gridy = 4;
        formPanel.add(passwordLabel, gbc);

        passwordField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(55, 65, 81), 2, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        gbc.gridy = 5;
        formPanel.add(passwordField, gbc);

        JPanel helperRow = new JPanel(new BorderLayout());
        helperRow.setOpaque(false);

        rememberAccountCheckBox.setOpaque(false);
        rememberAccountCheckBox.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        helperRow.add(rememberAccountCheckBox, BorderLayout.WEST);

        showPasswordToggle.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        helperRow.add(showPasswordToggle, BorderLayout.EAST);

        gbc.gridy = 6;
        formPanel.add(helperRow, gbc);

        statusLabel.setForeground(new Color(71, 85, 105));
        statusLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        gbc.gridy = 7;
        formPanel.add(statusLabel, gbc);

        // 关键修复：插入"占位行"，吃掉多余高度，避免按钮行被挤扁导致文字不显示
        gbc.gridy = 8;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(Box.createVerticalGlue(), gbc);

        JPanel actionRow = createActionRow();
        gbc.gridy = 9;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(actionRow, gbc);

        installActions();
        return formPanel;
    }

    private JPanel createActionRow() {
        JPanel actionRow = new JPanel(new GridBagLayout());
        actionRow.setOpaque(false);

        GridBagConstraints ac = new GridBagConstraints();
        ac.insets = new Insets(6, 10, 6, 10);
        ac.fill = GridBagConstraints.HORIZONTAL;
        ac.weightx = 1;
        ac.gridy = 0;

        stylePrimaryButton(loginButton);
        styleSuccessButton(registerButton);
        styleSecondaryButton(resetButton);
        styleDangerButton(exitButton);

        ac.gridx = 0;
        actionRow.add(loginButton, ac);
        ac.gridx = 1;
        actionRow.add(registerButton, ac);
        ac.gridx = 2;
        actionRow.add(resetButton, ac);
        ac.gridx = 3;
        actionRow.add(exitButton, ac);

        return actionRow;
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        button.setForeground(new Color(75, 85, 99));
        button.setBackground(new Color(37, 99, 235));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(140, 48));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(156, 163, 175), 2, true),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
    }

    private void styleSuccessButton(JButton button) {
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        button.setForeground(new Color(75, 85, 99));
        button.setBackground(new Color(22, 163, 74));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(140, 48));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(156, 163, 175), 2, true),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
    }

    private void styleSecondaryButton(JButton button) {
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        button.setForeground(new Color(75, 85, 99));
        button.setBackground(new Color(243, 244, 246));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(140, 48));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(156, 163, 175), 2, true),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
    }

    private void styleDangerButton(JButton button) {
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        button.setForeground(new Color(75, 85, 99));
        button.setBackground(new Color(220, 38, 38));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(140, 48));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(156, 163, 175), 2, true),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
    }

    private void installActions() {
        loginButton.addActionListener(this::handleLogin);

        registerButton.addActionListener(event -> showRegistrationDialog());

        resetButton.addActionListener(event -> {
            usernameField.setText("");
            passwordField.setText("");
            rememberAccountCheckBox.setSelected(false);
            statusLabel.setText("输入已清空，重新尝试吧。");
        });

        exitButton.addActionListener(event -> System.exit(0));

        showPasswordToggle.addActionListener(event -> {
            if (showPasswordToggle.isSelected()) {
                passwordField.setEchoChar((char) 0);
                showPasswordToggle.setText("隐藏密码");
            } else {
                passwordField.setEchoChar(defaultEchoChar);
                showPasswordToggle.setText("显示密码");
            }
        });
    }

    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        char[] passwordChars = passwordField.getPassword();

        if (username.isEmpty() || passwordChars.length == 0) {
            statusLabel.setText("账号与密码均不能为空");
            JOptionPane.showMessageDialog(this, "请输入完整的登录信息", "信息提示",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String normalized = username.toLowerCase();

        // 输入密码 -> hash
        String password = new String(passwordChars);

        boolean isValid = false;
        try {
            java.net.Socket socket = new java.net.Socket(Constant.SERVER_IP, Constant.SERVER_PORT);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            dos.writeInt(1);
            dos.writeUTF(username + "\n" + password);
            dos.flush();

            isValid = dis.readBoolean();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "连接服务器失败", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 修复：变量名 + 使用哈希比对
        if (!isValid) {
            statusLabel.setText("账号或密码不正确，请重新输入。");
            JOptionPane.showMessageDialog(this, "账号或密码错误", "登录失败",
                    JOptionPane.ERROR_MESSAGE);
            Arrays.fill(passwordChars, '\0');
            return;
        }

        rememberAccount(username);
        statusLabel.setText("登录成功，正在为您调度主界面...");
        Arrays.fill(passwordChars, '\0');

        openMainWindow(username);
        dispose();
    }

    private void showRegistrationDialog() {
        JDialog dialog = new JDialog(this, "注册账号", true);
        dialog.setSize(620, 520);
        dialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(24, 32, 24, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel headerLabel = new JLabel("创建新账号", SwingConstants.LEFT);
        headerLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 20));
        gbc.gridy = 0;
        contentPanel.add(headerLabel, gbc);

        JLabel tipLabel = new JLabel("<html>密码要求:长度≥6位,必须包含字母和数字</html>", SwingConstants.LEFT);
        tipLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        tipLabel.setForeground(new Color(107, 114, 128));
        gbc.gridy = 1;
        contentPanel.add(tipLabel, gbc);

        JTextField newUsernameField = new JTextField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        gbc.gridy = 2;
        contentPanel.add(new JLabel("账号"), gbc);
        gbc.gridy = 3;
        contentPanel.add(newUsernameField, gbc);

        gbc.gridy = 4;
        contentPanel.add(new JLabel("密码"), gbc);
        gbc.gridy = 5;
        contentPanel.add(newPasswordField, gbc);

        gbc.gridy = 6;
        contentPanel.add(new JLabel("确认密码"), gbc);
        gbc.gridy = 7;
        contentPanel.add(confirmPasswordField, gbc);

        JPanel buttonRow = new JPanel(new GridBagLayout());
        GridBagConstraints bc = new GridBagConstraints();
        bc.insets = new Insets(0, 8, 0, 8);
        bc.fill = GridBagConstraints.HORIZONTAL;
        bc.weightx = 1;

        JButton submitButton = new JButton("创建账号");
        JButton cancelButton = new JButton("取消");
        styleSuccessButton(submitButton);
        styleSecondaryButton(cancelButton);

        bc.gridx = 0;
        buttonRow.add(submitButton, bc);
        bc.gridx = 1;
        buttonRow.add(cancelButton, bc);

        gbc.gridy = 8;
        contentPanel.add(buttonRow, gbc);

        submitButton.addActionListener(event -> {
            String rawUsername = newUsernameField.getText().trim();
            char[] passwordChars = newPasswordField.getPassword();
            char[] confirmChars = confirmPasswordField.getPassword();

            if (rawUsername.isEmpty() || passwordChars.length == 0 || confirmChars.length == 0) {
                JOptionPane.showMessageDialog(dialog, "账号和密码均不能为空", "注册提示",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String normalizedUsername = rawUsername.toLowerCase();

            String password = new String(passwordChars);
            String confirmPassword = new String(confirmChars);

            // 密码强度验证:长度≥6,必须包含字母和数字
            if (password.length() < 6) {
                JOptionPane.showMessageDialog(dialog, "密码长度必须至少为6位", "密码强度不足",
                        JOptionPane.WARNING_MESSAGE);
                Arrays.fill(passwordChars, '\0');
                Arrays.fill(confirmChars, '\0');
                return;
            }

            boolean hasLetter = password.matches(".*[a-zA-Z].*");
            boolean hasDigit = password.matches(".*\\d.*");

            if (!hasLetter || !hasDigit) {
                JOptionPane.showMessageDialog(dialog,
                        "密码必须同时包含字母和数字\n当前密码强度不足,请重新设置",
                        "密码强度不足", // ;.;.' ;'. ;vbl;'bc;lvl; '
                        JOptionPane.WARNING_MESSAGE);
                Arrays.fill(passwordChars, '\0');
                Arrays.fill(confirmChars, '\0');
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(dialog, "两次输入的密码不一致", "注册提示",
                        JOptionPane.WARNING_MESSAGE);
                Arrays.fill(passwordChars, '\0');
                Arrays.fill(confirmChars, '\0');
                return;
            }

            boolean isRegistered = false;
            try {
                java.net.Socket socket = new java.net.Socket(Constant.SERVER_IP, Constant.SERVER_PORT);
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                DataInputStream dis = new DataInputStream(socket.getInputStream());

                dos.writeInt(2);
                dos.writeUTF(rawUsername + "\n" + password);
                dos.flush();

                isRegistered = dis.readBoolean();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "连接服务器失败", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (isRegistered) {
                statusLabel.setText("账号 " + rawUsername + " 注册成功,请使用新账号登录。");
                JOptionPane.showMessageDialog(this, "注册成功,立即使用新账号登录吧!", "注册成功",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "注册失败，账号可能已存在", "注册失败",
                        JOptionPane.ERROR_MESSAGE);
            }

            Arrays.fill(passwordChars, '\0');
            Arrays.fill(confirmChars, '\0');
        });

        cancelButton.addActionListener(event -> dialog.dispose());

        dialog.setContentPane(contentPanel);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    private void rememberAccount(String username) {
        if (rememberAccountCheckBox.isSelected()) {
            userPreferences.put(PREF_USERNAME_KEY, username);
        } else {
            userPreferences.remove(PREF_USERNAME_KEY);
        }
    }

    private void preloadRememberedAccount() {
        String rememberedUsername = userPreferences.get(PREF_USERNAME_KEY, "");
        if (!rememberedUsername.isEmpty()) {
            usernameField.setText(rememberedUsername);
            rememberAccountCheckBox.setSelected(true);
        }
    }

    private void openMainWindow(String username) {
        JFrame frame = new JFrame("欢迎使用AscentSys应用 - 当前用户: " + username);
        // 修改：使用常量设置大小
        frame.setSize(Constant.STD_WINDOWS_WIDTH, Constant.STD_WINDOWS_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);

        // 创建主面板，使用BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // 创建内容面板，用于显示主内容
        JPanel contentPanel = new JPanel(new BorderLayout());

        // 创建药品列表面板
        ShopList shop = new ShopList(username);
        JPanel shopPanel = shop.createShopPanel();
        contentPanel.add(shopPanel, BorderLayout.CENTER);

        // 创建侧边栏
        JPanel sidebar = createSidebar(contentPanel, shop);
        mainPanel.add(sidebar, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // 创建侧边栏组件
    private JPanel createSidebar(JPanel contentPanel, ShopList shop) {
        // 使用与登录面板相同的渐变背景
        GradientPanel sidebar = new GradientPanel();
        sidebar.setPreferredSize(new Dimension(250, Constant.STD_WINDOWS_HEIGHT));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(32, 16, 32, 16));

        // 欢迎标题
        JLabel welcomeLabel = new JLabel("欢迎使用AscentSys系统");
        welcomeLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE); // 白色文字，在深蓝色背景上可见
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 32, 0));
        sidebar.add(welcomeLabel);

        // 药物列表按钮
        JButton medicineListButton = new JButton("药物列表");
        medicineListButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        medicineListButton.setPreferredSize(new Dimension(200, 40));
        medicineListButton.setMaximumSize(new Dimension(200, 40));
        medicineListButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        medicineListButton.setBackground(new Color(255, 255, 255)); // 白色背景
        medicineListButton.setForeground(Color.BLACK); // 黑色文字
        medicineListButton.setFocusPainted(false);
        medicineListButton.setBorder(BorderFactory.createLineBorder(new Color(156, 163, 175), 2, true));
        medicineListButton.addActionListener(e -> {
            // 切换到药物列表界面
            contentPanel.removeAll();
            contentPanel.add(shop.createShopPanel(), BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
        });
        sidebar.add(medicineListButton);
        sidebar.add(Box.createVerticalStrut(16));

        // 我的订单按钮
        JButton orderHistoryButton = new JButton("我的订单");
        orderHistoryButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        orderHistoryButton.setPreferredSize(new Dimension(200, 40));
        orderHistoryButton.setMaximumSize(new Dimension(200, 40));
        orderHistoryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        orderHistoryButton.setBackground(new Color(255, 255, 255)); // 白色背景
        orderHistoryButton.setForeground(Color.BLACK); // 黑色文字
        orderHistoryButton.setFocusPainted(false);
        orderHistoryButton.setBorder(BorderFactory.createLineBorder(new Color(156, 163, 175), 2, true));
        orderHistoryButton.addActionListener(e -> {
            // 切换到购买历史界面
            contentPanel.removeAll();
            contentPanel.add(createOrderHistoryPanel(), BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
        });
        sidebar.add(orderHistoryButton);

        return sidebar;
    }

    // 创建购买历史表格界面
    private JPanel createOrderHistoryPanel() {
        JPanel panel = new JPanel();
        panel.setSize(Constant.STD_WINDOWS_WIDTH - 250, Constant.STD_WINDOWS_HEIGHT);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        // 创建标题
        JLabel titleLabel = new JLabel("我的订单");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));
        panel.add(titleLabel);

        // 获取订单历史数据
        List<OrderHistory.Order> orders = OrderHistory.getInstance().getOrders();

        // 如果没有订单
        if (orders.isEmpty()) {
            JLabel noOrderLabel = new JLabel("暂无购买记录");
            noOrderLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
            noOrderLabel.setForeground(new Color(107, 114, 128));
            noOrderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(noOrderLabel);
            return panel;
        }

        // 创建表格数据
        Object[][] tableData = new Object[orders.size()][5];
        // 创建SimpleDateFormat对象，使用：分隔所有时间单位
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
        for (int i = 0; i < orders.size(); i++) {
            OrderHistory.Order order = orders.get(i);
            tableData[i][0] = order.getOrderId();
            tableData[i][1] = sdf.format(order.getOrderDate());
            tableData[i][2] = order; // 将Order对象存入表格，用于自定义渲染
            tableData[i][3] = "¥" + String.format("%.2f", order.getTotalPrice());
            tableData[i][4] = order.getStatus();
        }

        // 列名
        String[] columnNames = { "订单ID", "下单时间", "购买商品", "总价", "状态" };

        // 创建表格模型
        DefaultTableModel tableModel = new DefaultTableModel(tableData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 允许"购买商品"列(索引2)进行编辑，以便触发滚动条交互
                return column == 2;
            }
        };

        // 创建表格
        JTable table = new JTable(tableModel);

        // 创建自定义的商品列表渲染器
        javax.swing.table.TableCellRenderer itemListRenderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                // 如果value是Order对象，生成商品列表
                if (value instanceof OrderHistory.Order) {
                    OrderHistory.Order order = (OrderHistory.Order) value;
                    // 创建文本区域显示商品列表
                    JTextArea textArea = new JTextArea();
                    textArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
                    textArea.setEditable(false);
                    textArea.setLineWrap(true);
                    textArea.setWrapStyleWord(true);

                    // 生成商品列表文本
                    StringBuilder sb = new StringBuilder();
                    for (OrderHistory.OrderItem item : order.getItems()) {
                        sb.append(item.getName()).append(" - ").append(item.getQuantity()).append("个\n");
                    }
                    textArea.setText(sb.toString());

                    // 创建滚动面板
                    JScrollPane scrollPane = new JScrollPane(textArea);
                    scrollPane.setPreferredSize(new Dimension(200, 100));
                    scrollPane.setBorder(BorderFactory.createEmptyBorder());
                    // 设置滚动条策略，确保需要时显示
                    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    // 设置滚动面板的背景色
                    scrollPane.getViewport().setBackground(textArea.getBackground());

                    // 设置选中状态的背景色
                    if (isSelected) {
                        textArea.setBackground(new Color(51, 153, 255));
                        textArea.setForeground(Color.WHITE);
                        scrollPane.getViewport().setBackground(new Color(51, 153, 255));
                    } else {
                        textArea.setBackground(Color.WHITE);
                        textArea.setForeground(Color.BLACK);
                        scrollPane.getViewport().setBackground(Color.WHITE);
                    }

                    return scrollPane;
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };

        // 美化表格
        table.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        table.setRowHeight(100); // 增加行高以容纳滚动面板
        javax.swing.table.DefaultTableCellRenderer headerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(0, 82, 217));
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        table.getTableHeader().setDefaultRenderer(headerRenderer);
        table.setSelectionBackground(new Color(51, 153, 255));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(220, 220, 220));
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setFillsViewportHeight(true);

        // 定义编辑器，支持滚动交互
        class ItemListCellEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor {
            private JTextArea textArea;
            private JScrollPane scrollPane;
            private OrderHistory.Order currentOrder;

            public ItemListCellEditor() {
                textArea = new JTextArea();
                textArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
                textArea.setEditable(false);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                
                scrollPane = new JScrollPane(textArea);
                scrollPane.setBorder(BorderFactory.createEmptyBorder());
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                if (value instanceof OrderHistory.Order) {
                    currentOrder = (OrderHistory.Order) value;
                    StringBuilder sb = new StringBuilder();
                    for (OrderHistory.OrderItem item : currentOrder.getItems()) {
                        sb.append(item.getName()).append(" - ").append(item.getQuantity()).append("个\n");
                    }
                    textArea.setText(sb.toString());
                    textArea.setCaretPosition(0);
                    
                    textArea.setBackground(Color.WHITE);
                    textArea.setForeground(Color.BLACK);
                    scrollPane.getViewport().setBackground(Color.WHITE);
                }
                return scrollPane;
            }

            @Override
            public Object getCellEditorValue() {
                return currentOrder;
            }
        }

        // 设置"购买商品"列的渲染器和编辑器
        table.getColumnModel().getColumn(2).setCellRenderer(itemListRenderer);
        table.getColumnModel().getColumn(2).setCellEditor(new ItemListCellEditor());

        // 创建滚动面板
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(new javax.swing.border.LineBorder(new Color(220, 220, 220), 5, true));

        panel.add(scrollPane);
        return panel;
    }

    private static class GradientPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2 = (Graphics2D) graphics;
            GradientPaint paint = new GradientPaint(
                    0, 0, new Color(24, 90, 219),
                    getWidth(), getHeight(), new Color(58, 132, 247));
            g2.setPaint(paint);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}