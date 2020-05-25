package server;

import javax.swing.*;
import java.awt.*;

public class MainServer {
	public static void main(String[] args) {
		// 创建服务器启动窗口
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new ViewServer();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "未知错误", "月月聊天室服务器", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		});
	}
}
