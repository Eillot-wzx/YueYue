package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//聊天的主页面
public class ViewClient extends JFrame {

    private ViewClient viewClient;//自身窗体

    private JPanel contentPane;//底层容器

    private JPanel chatAllPanel;// 群聊容器
    private JTextArea chatAllOut;// 群聊显示面板
    private JScrollPane chatAllOutScroll;// 群聊显示面板滚动条
    private JTextArea chatAllIn;// 群聊输入面板
    private JScrollPane chatAllInScroll;// 群聊输入面板滚动条
    private JButton chatAllRemove;// 群聊清空按钮
    private JButton chatAllsend;// 发送聊天信息按钮

    private JPanel usersPanel;// 用户容器
    private JLabel usersLabel;// 用户信息标签
    private JButton usersflush;// 用户手动刷新
    private JList<String> usersList;// 显示用户列表

    private JPopupMenu popupMenu;// 右键菜单
    private JMenuItem menuItem;// 菜单项

    private String myName;//发件人姓名
    private String toName = null;//收件人姓名

    private SocketClient socketClient = new SocketClient();//socket对象

    public static DefaultListModel<String> listusermodel = new DefaultListModel<String>();// 用户列表容器
    public static ArrayList<ChatClient> chatClients = new ArrayList<ChatClient>();//聊天用户容器

    /**
     * 构造方法,需要提供自己的姓名与开放的UDP端口号
     *
     * @param myName
     * @param udpPort
     */
    public ViewClient(String myName, int udpPort) {

        viewClient = this;
        this.myName = myName;

        //窗体的属性设置
        setTitle("月月聊天工具客户端");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(730, 500);
        setResizable(false);
        setLocationRelativeTo(null);

        //底层容器
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        //群聊容器
        chatAllPanel = new JPanel();
        chatAllPanel.setLayout(null);
        chatAllPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        chatAllPanel.setBounds(20, 20, 460, 430);
        contentPane.add(chatAllPanel);

        //群聊显示面板
        chatAllOut = new JTextArea();
        chatAllOut.setBounds(5, 5, 450, 250);
        chatAllOut.setLineWrap(true);
        chatAllOut.setEditable(false);
        chatAllOut.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent mouseEvent) {
                chatAllOut.setCursor(new Cursor(Cursor.TEXT_CURSOR));   //鼠标进入Text区后变为文本输入指针
            }

            public void mouseExited(MouseEvent mouseEvent) {
                chatAllOut.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));   //鼠标离开Text区后恢复默认形态
            }
        });
        chatAllOut.getCaret().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                chatAllOut.getCaret().setVisible(true);   //使Text区的文本光标显示
            }
        });

        //群聊滚动条
        chatAllOutScroll = new JScrollPane(chatAllOut);
        chatAllOutScroll.setBounds(5, 5, 450, 250);
        chatAllPanel.add(chatAllOutScroll);

        //群聊输入面板
        chatAllIn = new JTextArea();
        chatAllIn.setBounds(5, 270, 450, 120);
        chatAllIn.setLineWrap(true);

        //群聊输入滚动条
        chatAllInScroll = new JScrollPane(chatAllIn);
        chatAllInScroll.setBounds(5, 270, 450, 120);
        chatAllPanel.add(chatAllInScroll);

        //群聊清空输入框
        chatAllRemove = new JButton("清空");
        chatAllRemove.setBounds(240, 400, 100, 20);
        chatAllPanel.add(chatAllRemove);
        chatAllRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chatAllIn.setText("");
            }
        });

        //群聊发送按钮
        chatAllsend = new JButton("发送");
        chatAllsend.setBounds(350, 400, 100, 20);
        chatAllPanel.add(chatAllsend);
        chatAllsend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = chatAllIn.getText();
                if (isTrueData(s)) {
                    String data = getMyString() + s.trim();
                    socketClient.chatAll(data);
                    chatAllIn.setText("");
                }

            }
        });

        //用户显示容器
        usersPanel = new JPanel();
        usersPanel.setLayout(null);
        usersPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        usersPanel.setBounds(500, 20, 210, 430);
        contentPane.add(usersPanel);

        //用户面板提示信息
        usersLabel = new JLabel("在线用户列表");
        usersLabel.setBounds(10, 5, 100, 20);
        usersPanel.add(usersLabel);

        //用户面板手动刷新按钮
        usersflush = new JButton("刷新");
        usersflush.setBounds(120, 5, 70, 20);
        usersPanel.add(usersflush);
        usersflush.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                socketClient.fulshUserList(myName);
            }
        });

        //用户列表
        usersList = new JList<String>();
        usersList.setBounds(15, 30, 180, 380);
        usersPanel.add(usersList);

        //右键菜单
        popupMenu = new JPopupMenu();
        addPopup(usersList, popupMenu);

        //建立聊天菜单项
        menuItem = new JMenuItem("建立聊天");
        popupMenu.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!myName.equals(toName)) {
                    boolean b = true;
                    for (ChatClient chatClient : ViewClient.chatClients) {
                        if (chatClient.toName.equals(toName)) {
                            b = false;
                            break;
                        }
                    }
                    if (b) {
                        ChatClient chatClient = new ChatClient(myName, toName);
                        ViewClient.chatClients.add(chatClient);
                    } else {
                        JOptionPane.showMessageDialog(null, "你已经与他建立了聊天哦", "", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "不要自言自语哦", "", JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        //窗体关闭事件
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SocketClient socketClient = new SocketClient();
                socketClient.down(myName, udpPort);
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
            chatAllIn.setText("");
            return false;
        }
        return true;
    }


    /**
     * 群聊面板中写数据
     *
     * @param str     待写入的文本,会加上时间戳处理
     * @param attrSet 传进的富文本
     */
    public void insert(String str, AttributeSet attrSet) {
        Document doc = chatAllOut.getDocument();
        str = str + "\n";
        try {
            doc.insertString(doc.getLength(), str, attrSet);
        } catch (BadLocationException e) {

        }
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
        return str_time + myName + ": ";
    }

    /**
     * 群聊面板中写数据
     *
     * @param text
     */
    public void chatAllOut(String text) {
        SimpleAttributeSet attrSet = new SimpleAttributeSet();
        StyleConstants.setForeground(attrSet, Color.BLACK);
        StyleConstants.setFontSize(attrSet, 14);
        insert(text, attrSet);
    }

    /**
     * 刷新用户列表方法, 将指定格式的字符串解析为用户列表数据
     *
     * @param userlist
     */
    public void fulshuser(String userlist) {
        String[] list = userlist.split("~");
        ViewClient.listusermodel.removeAllElements();
        for (String string : list) {
            ViewClient.listusermodel.addElement(string);
        }
        usersList.setModel(listusermodel);
    }

    /**
     * 调用右键菜单方法
     *
     * @param component
     * @param popup
     */
    private void addPopup(Component component, final JPopupMenu popup) {
        component.addMouseListener(new MouseAdapter() {
            private void showMenu(MouseEvent e) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                int i = e.getButton();
                if (i == MouseEvent.BUTTON1) {
                    toName = usersList.getSelectedValue();
                }
                if (i == MouseEvent.BUTTON3) {
                    toName = usersList.getSelectedValue();
                    if (toName != null) {
                        showMenu(e);
                    }
                }
            }
        });
    }
}
