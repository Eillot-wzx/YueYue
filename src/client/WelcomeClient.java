package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.regex.Pattern;

//欢迎页面,在此页面进行注册名称
public class WelcomeClient extends JFrame {

    private WelcomeClient welcomeClient;//自身窗体对象
    private JPanel contentPane;//底层容器

    private JLabel message;//提示信息
    private JTextField jTextFieldName;//姓名输入框
    private JButton login;//登陆按钮

    private String myName;//获取到的姓名
    private int udpPort;//确定开放的UDP端口

    private SocketClient socketClient;//Socket对象

    public WelcomeClient() {

        welcomeClient = this;

        // 窗口的属性设置
        setSize(300, 250);
        setResizable(false);
        setTitle("欢迎来到月月聊天室");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 默认面板
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        // 登陆按钮
        login = new JButton("登陆");
        login.setBounds(60, 120, 180, 30);
        contentPane.add(login);

        login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                myName = jTextFieldName.getText();
                if (isTrueName(myName)) {
                    try {
                        //连接服务器
                        socketClient = new SocketClient(welcomeClient);
                        socketClient.land(myName, udpPort);
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(null, "连接服务器失败", "", JOptionPane.ERROR_MESSAGE);
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "用户名错误", "", JOptionPane.ERROR_MESSAGE);
                    message.setText("用户名为数字字母汉字组合");
                    jTextFieldName.setText("");
                }

            }
        });

        // 名称输入文本框
        jTextFieldName = new JTextField("");
        jTextFieldName.setBounds(50, 70, 200, 30);
        contentPane.add(jTextFieldName);

        // 提示信息
        message = new JLabel("请输入您的用户名字", JLabel.CENTER);
        message.setBounds(50, 30, 200, 30);
        contentPane.add(message);

        //随即获取一个可用的UDP端口
        udpPort = SocketClient.getUDPPort();

        setVisible(true);
    }

    /**
     * 判断有效网名：数字字母汉字 小于15位
     *
     * @param name
     * @return 合法返回True
     */
    public boolean isTrueName(String name) {
        if (name.length() > 15) {
            return false;
        }
        if (Pattern.compile("^[A-Za-z0-9\\u4e00-\\u9fa5]+$").matcher(name).matches())
            return true;
        return false;
    }

}
