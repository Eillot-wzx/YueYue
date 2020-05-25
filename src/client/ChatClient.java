package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

//私聊窗体
public class ChatClient extends JFrame {

    private JPanel contentPane;//底层容器

    private JPanel chatPanel;// 私聊容器
    private JTextArea chatOut;// 私聊显示面板
    private JScrollPane chatOutScroll;// 私聊显示面板滚动条
    private JTextArea chatIn;// 私聊输入面板
    private JScrollPane chatInScroll;// 私聊输入面板滚动条
    private JButton chatRemove;// 私聊清空按钮
    private JButton chatsend;// 发送聊天信息按钮


    public String myName;//发件人名字
    public String toName;//收件人名字
    private ChatClient chatClient;//自身容器
    private SocketClient socketClient;//socket对象

    //构造方法,提供发件人与收件人
    public ChatClient(String myName, String toName) {

        this.myName = myName;
        this.toName = toName;
        chatClient = this;
        socketClient = new SocketClient(chatClient);

        //窗体属性设置
        setTitle(myName + "与" + toName + "的聊天");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 500, 500);

        //底层容器
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        //聊天面板
        chatPanel = new JPanel();
        chatPanel.setLayout(null);
        chatPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        chatPanel.setBounds(20, 20, 460, 430);
        contentPane.add(chatPanel);

        //聊天输出框
        chatOut = new JTextArea();
        chatOut.setBounds(5, 5, 450, 250);
        chatOut.setEditable(false);
        chatOut.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent mouseEvent) {
                chatOut.setCursor(new Cursor(Cursor.TEXT_CURSOR));   //鼠标进入Text区后变为文本输入指针
            }

            public void mouseExited(MouseEvent mouseEvent) {
                chatOut.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));   //鼠标离开Text区后恢复默认形态
            }
        });
        chatOut.getCaret().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                chatOut.getCaret().setVisible(true);   //使Text区的文本光标显示
            }
        });

        //聊天输出滚动条
        chatOutScroll = new JScrollPane(chatOut);
        chatOutScroll.setBounds(5, 5, 450, 250);
        chatPanel.add(chatOutScroll);

        //聊天输入框
        chatIn = new JTextArea();
        chatIn.setBounds(5, 270, 450, 120);

        //聊天输入滚动条
        chatInScroll = new JScrollPane(chatIn);
        chatInScroll.setBounds(5, 270, 450, 120);
        chatPanel.add(chatInScroll);

        //清空聊天输入框按钮
        chatRemove = new JButton("清空");
        chatRemove.setBounds(240, 400, 100, 20);
        chatPanel.add(chatRemove);
        chatRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chatIn.setText("");
            }
        });

        //私聊发送按钮
        chatsend = new JButton("发送");
        chatsend.setBounds(350, 400, 100, 20);
        chatPanel.add(chatsend);

        chatsend.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String s = chatIn.getText();
                if (isTrueData(s)) {
                    String data = getMyString() + s;
                    socketClient.chat(data);
                    chatClient.chat(data.split("~")[2]);
                    chatIn.setText("");
                }
            }
        });


        //窗口关闭事件
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ViewClient.chatClients.remove(chatClient);
            }
        });

        setVisible(true);

    }


    /**
     * 判断数据有效性
     * 大于200无效,空格无效
     *
     * @param data
     * @return
     */
    public boolean isTrueData(String data) {
        if (data.length() > 200) {
            JOptionPane.showMessageDialog(null, "您发送的数据太多了,请分开发送", "", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (data.trim().equals("")) {
            JOptionPane.showMessageDialog(null, "请输入内容", "", JOptionPane.ERROR_MESSAGE);
            chatIn.setText("");
            return false;
        }
        return true;
    }

    /**
     * 获取我的字符串格式
     *
     * @return str_time + name + ": "
     */
    public String getMyString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("[HH:mm:ss] ");
        Date date = new Date();
        String str_time = dateFormat.format(date);
        return myName + "~" + toName + "~" + str_time + myName + ": ";
    }

    /**
     * 私聊面板中写数据
     *
     * @param text
     */
    public void chat(String text) {
        SimpleAttributeSet attrSet = new SimpleAttributeSet();
        StyleConstants.setForeground(attrSet, Color.BLACK);
        StyleConstants.setFontSize(attrSet, 14);
        insert(text, attrSet);
    }

    /**
     * 私聊面板中写数据
     *
     * @param str     待写入的文本,会加上时间戳处理
     * @param attrSet 传进的富文本
     */
    public void insert(String str, AttributeSet attrSet) {
        Document doc = chatOut.getDocument();
        str = str + "\n";
        try {
            doc.insertString(doc.getLength(), str, attrSet);
        } catch (BadLocationException e) {

        }
    }


}
