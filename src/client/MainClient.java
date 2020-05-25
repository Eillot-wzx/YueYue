package client;

import java.awt.EventQueue;
import javax.swing.JOptionPane;

/*
 * 客户端启动类,用于开启客户端
 */
public class MainClient {
    public static void main(String[] args) {
        //创建客户端启动窗口
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new WelcomeClient();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null,"未知错误", "月月聊天室客户端",JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
