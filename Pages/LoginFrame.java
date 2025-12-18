import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final String PREF_NODE = "AscentSysLogin";
    private static final String PREF_USERNAME_KEY = "rememberedUsername";
    private static final String USERS_JSON_FILE = "users.json";

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

    private final Map<String, String> userCredentialStore = new LinkedHashMap<>();
    private final Map<String, String> customCredentialStore = new LinkedHashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Windows 外观（如果不可用就用系统默认）
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (Exception ignored) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored2) {}
            }

            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }

    public LoginFrame() {
        configureFrame();
        initializeCredentialStore();
        setContentPane(createRootPanel());

        defaultEchoChar = passwordField.getEchoChar();

        preloadRememberedAccount();
        getRootPane().setDefaultButton(loginButton);
    }

    private void configureFrame() {
        setTitle("AscentSys统一登录");
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(960, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initializeCredentialStore() {
        userCredentialStore.clear();
        userCredentialStore.putAll(createDefaultCredentialStore());

        customCredentialStore.clear();
        customCredentialStore.putAll(loadCustomUsersFromJSON());

        // 合并（自定义用户覆盖默认同名账号）
        userCredentialStore.putAll(customCredentialStore);
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
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        gbc.gridy = 3;
        formPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("密码", SwingConstants.LEFT);
        passwordLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        gbc.gridy = 4;
        formPanel.add(passwordLabel, gbc);

        passwordField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(55, 65, 81), 2, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
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
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
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
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
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
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
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
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
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
        String storedPassword = userCredentialStore.get(normalized);
        String password = new String(passwordChars);

        if (storedPassword == null || !storedPassword.equals(password)) {
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
            if (userCredentialStore.containsKey(normalizedUsername)) {
                JOptionPane.showMessageDialog(dialog, "该账号已存在，请直接登录或更换账号名", "注册提示",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

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
                        "密码强度不足",
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

            customCredentialStore.put(normalizedUsername, password);
            userCredentialStore.put(normalizedUsername, password);
            saveCustomUsersToJSON();

            Arrays.fill(passwordChars, '\0');
            Arrays.fill(confirmChars, '\0');

            statusLabel.setText("账号 " + rawUsername + " 注册成功,请使用新账号登录。");
            JOptionPane.showMessageDialog(this, "注册成功,立即使用新账号登录吧!", "注册成功",
                    JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
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

    private void saveCustomUsersToJSON() {
        try {
            File file = new File(USERS_JSON_FILE);
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"users\": [\n");
            
            int count = 0;
            for (Map.Entry<String, String> entry : customCredentialStore.entrySet()) {
                if (count > 0) json.append(",\n");
                json.append("    {\n");
                json.append("      \"username\": \"").append(entry.getKey()).append("\",\n");
                json.append("      \"password\": \"").append(entry.getValue()).append("\"\n");
                json.append("    }");
                count++;
            }
            
            json.append("\n  ]\n");
            json.append("}\n");
            
            Files.write(file.toPath(), json.toString().getBytes("UTF-8"));
        } catch (IOException e) {
            System.err.println("保存用户数据失败: " + e.getMessage());
        }
    }

    private Map<String, String> loadCustomUsersFromJSON() {
        Map<String, String> customUsers = new LinkedHashMap<>();
        File file = new File(USERS_JSON_FILE);
        
        if (!file.exists()) {
            return customUsers;
        }
        
        try {
            String content = new String(Files.readAllBytes(file.toPath()), "UTF-8");
            
            // 简单的JSON解析(不依赖外部库)
            String[] lines = content.split("\n");
            String currentUsername = null;
            
            for (String line : lines) {
                line = line.trim();
                
                if (line.contains("\"username\":")) {
                    int start = line.indexOf(":") + 1;
                    int firstQuote = line.indexOf("\"", start);
                    int lastQuote = line.lastIndexOf("\"");
                    if (firstQuote != -1 && lastQuote > firstQuote) {
                        currentUsername = line.substring(firstQuote + 1, lastQuote);
                    }
                } else if (line.contains("\"password\":") && currentUsername != null) {
                    int start = line.indexOf(":") + 1;
                    int firstQuote = line.indexOf("\"", start);
                    int lastQuote = line.lastIndexOf("\"");
                    if (firstQuote != -1 && lastQuote > firstQuote) {
                        String password = line.substring(firstQuote + 1, lastQuote);
                        customUsers.put(currentUsername.toLowerCase(), password);
                        currentUsername = null;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("读取用户数据失败: " + e.getMessage());
        }
        
        return customUsers;
    }

    private Map<String, String> createDefaultCredentialStore() {
        Map<String, String> store = new LinkedHashMap<>();
        store.put("admin", "admin123");
        store.put("pharmacist", "med2024");
        store.put("auditor", "check789");
        return store;
    }

    private void openMainWindow(String username) {
        JFrame frame = new JFrame("欢迎使用AscentSys应用 - 当前用户: " + username);
        frame.setSize(1100, 720);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);

        shopList shop = new shopList();
        frame.getContentPane().add(shop.createShopPanel(), BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private static class GradientPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2 = (Graphics2D) graphics;
            GradientPaint paint = new GradientPaint(
                    0, 0, new Color(24, 90, 219),
                    getWidth(), getHeight(), new Color(58, 132, 247)
            );
            g2.setPaint(paint);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}