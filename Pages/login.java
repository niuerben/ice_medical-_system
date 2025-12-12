import javax.swing.*;
public class login{
    public static void main(String[] args){
        // 创建标题为"欢迎使用AscentSys应用"的窗口
        JFrame frame = new JFrame("欢迎使用AscentSys应用");

        // 设置窗口大小和关闭操作
        frame.setSize(1600, 1200);
        frame.setLocation(400,300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        System.out.println("日志信息：系统\n");

        // 添加标签
        JLabel label = new JLabel("请继续开发一下内容");
        frame.getContentPane().add(label);

        frame.pack();
        frame.setVisible(true);
    }
}