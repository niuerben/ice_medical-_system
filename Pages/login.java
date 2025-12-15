import javax.swing.*;
// import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;
public class login{
    public static void main(String[] args){
        // 设置 Windows 风格
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 创建标题为"欢迎使用AscentSys应用"的窗口
        JFrame frame = new JFrame("欢迎使用AscentSys应用");
        init_frame(frame);
        // 创建登录对话框
        JDialog dialog = new JDialog(frame, "登录界面");
        init_dialog(dialog, frame);
        
        
    }

    public static void init_frame(JFrame frame){
        // 设置窗口大小和关闭操作
        frame.setSize(1200, 900);
        frame.setLocation(400,300);
        // frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        System.out.println("日志信息：登录面窗口打开\n");

        // 使用 shopList 类创建药品列表面板
        shopList shop = new shopList();
        frame.getContentPane().add(shop.createShopPanel());

        frame.setVisible(true);

        // 设置关闭逻辑
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void init_dialog(JDialog dialog, JFrame frame){
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);

        // 创建按钮 
        JButton bt1 = new JButton("登录");
        JButton bt2 = new JButton("取消");
        // 创建登录面版
        JPanel panel = new JPanel();
        panel.add(bt1);
        panel.add(bt2);
        dialog.add(panel);
        // 设置关闭逻辑
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
}