package server;

import user.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewServer extends JFrame {

	private ViewServer viewServer;// this窗体

	private JPanel contentPane;// 默认底层容器

	private JPanel messagePanel;// 显示信息容器
	private JLabel messageLabelIP;// IP标签
	private JLabel meaasgeIP;// 显示IP信息
	private JLabel messageLabelTCP;// TCP标签
	private JLabel messageTCP;// 显示TCP信息
	private JLabel messageLabelUDP;// UDP标签
	private JLabel messageUDP;// 显示UDP信息

	private JPanel functionPanel;// 功能容器
	private JButton functionOpen;// 开启服务器
	private JButton functionClose;// 关闭服务器

	private JPanel statePanel;// 状态容器
	private JLabel stateLabel;// 服务器状态标签
	public JLabel state;// 显示服务器状态

	private JPanel logPanel;// 日志容器
	private JTextPane log;// 显示日志信息
	private JScrollPane ScrollPane;// 日志滚动条

	private JPanel usersPanel;// 用户容器
	private JLabel usersLabel;// 用户信息标签
	private JList<String> usersList;// 显示用户列表

	private SocketServer socketServer = new SocketServer();// socket对象

	private DefaultListModel<String> listusermodel = new DefaultListModel<String>();// 用户列表容器

	public ViewServer() {

		viewServer = this;

		setTitle("月月聊天工具服务器端");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(730, 500);
		setResizable(false);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);

		messagePanel = new JPanel();
		messagePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		messagePanel.setBounds(15, 20, 250, 130);
		contentPane.add(messagePanel);
		messagePanel.setLayout(null);

		messageLabelIP = new JLabel("IP:");
		messageLabelIP.setBounds(45, 30, 40, 15);
		messagePanel.add(messageLabelIP);

		messageLabelTCP = new JLabel("TCP开放端口：");
		messageLabelTCP.setBounds(45, 55, 100, 15);
		messagePanel.add(messageLabelTCP);

		messageLabelUDP = new JLabel("UDP开放端口：");
		messageLabelUDP.setBounds(45, 80, 100, 15);
		messagePanel.add(messageLabelUDP);

		meaasgeIP = new JLabel("127.0.0.1");
		meaasgeIP.setBounds(95, 30, 130, 15);
		messagePanel.add(meaasgeIP);

		messageTCP = new JLabel("8989");
		messageTCP.setBounds(155, 55, 60, 15);
		messagePanel.add(messageTCP);

		messageUDP = new JLabel("9898");
		messageUDP.setBounds(155, 80, 60, 15);
		messagePanel.add(messageUDP);

		functionPanel = new JPanel();
		functionPanel.setLayout(null);
		functionPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		functionPanel.setBounds(280, 20, 200, 130);
		contentPane.add(functionPanel);

		functionOpen = new JButton("开启服务器");
		functionOpen.setBounds(50, 25, 100, 30);
		functionPanel.add(functionOpen);

		functionOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				state.setForeground(Color.GREEN);
				// 创建tcp端口监听
				DataServer.pool.submit(new OpenTCPPortServerThread(viewServer));
				// 创建udp 端口监听
				DataServer.pool.submit(new OpenUDPPortServerThread(viewServer));
				functionOpen.setEnabled(false);
				functionClose.setEnabled(true);
			}
		});

		functionClose = new JButton("关闭服务器");
		functionClose.setBounds(50, 75, 100, 30);
		functionClose.setEnabled(false);
		functionPanel.add(functionClose);

		functionClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				state.setForeground(Color.black);
				// 开启关闭服务器线程
				DataServer.pool.submit(new CloseServerThread(viewServer));
				DataServer.listuser.clear();
				fulshuser();
				functionClose.setEnabled(false);
				functionOpen.setEnabled(true);
			}
		});

		statePanel = new JPanel();
		statePanel.setLayout(null);
		statePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		statePanel.setBounds(15, 160, 465, 30);
		contentPane.add(statePanel);

		state = new JLabel("◆");
		state.setBounds(260, 8, 30, 15);
		statePanel.add(state);

		stateLabel = new JLabel("服务器运行状态");
		stateLabel.setBounds(150, 8, 100, 15);
		statePanel.add(stateLabel);

		logPanel = new JPanel();
		logPanel.setLayout(null);
		logPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		logPanel.setBounds(15, 200, 465, 250);
		contentPane.add(logPanel);

		log = new JTextPane();
		log.setBounds(10, 15, 445, 225);

		ScrollPane = new JScrollPane(log);
		ScrollPane.setBounds(10, 15, 445, 225);
		logPanel.add(ScrollPane);

		usersPanel = new JPanel();
		usersPanel.setLayout(null);
		usersPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		usersPanel.setBounds(500, 20, 210, 430);
		contentPane.add(usersPanel);

		usersLabel = new JLabel("在线用户列表");
		usersLabel.setBounds(10, 5, 100, 20);
		usersPanel.add(usersLabel);

		usersList = new JList<String>();
		usersList.setBounds(15, 30, 180, 380);
		usersPanel.add(usersList);

		// 初始化方法
		initialize();

		setVisible(true);
	}

	/**
	 * 初始化信息
	 */
	public void initialize() {

		String networkName = "en0";

		InetAddress address = socketServer.getInetAddress(networkName, 4);

		if (address == null) {
			meaasgeIP.setForeground(Color.RED);
			writelog("获取" + networkName + "接口" + "Ipv" + 4 + "协议失败");
		} else {
			meaasgeIP.setText(address.getHostAddress());
			writelog("获取" + networkName + "接口" + "Ipv" + 4 + "协议成功");
		}

		// 确定端口是否被占用
		isLocalPortUsing(8989);
		isLocalPortUsing(9898);
		// isLocalPortUsing(3306);//测试isLocalPortUsing()方法

	}

	/**
	 * 查看端口是否被占用
	 *
	 * @param port
	 */
	public void isLocalPortUsing(int port) {
		if (socketServer.isLocalPortUsing(port)) {
			writeErrlog(port + "端口已被占用");
			functionOpen.setEnabled(false);
			viewServer.state.setForeground(Color.RED);
		} else {
			writelog(port + "端口未被占用");
		}
	}

	/**
	 * 从数据中删除用户
	 *
	 * @param user
	 */
	public void delUser(User user) {
		DataServer.listuser.remove(user);
		fulshuser();
	}

	/**
	 * 向数据中增加用户
	 *
	 * @param user
	 */
	public void addUser(User user) {
		DataServer.listuser.add(user);
		fulshuser();
	}

	/**
	 * 刷新用户在线面板
	 */
	public void fulshuser() {
		listusermodel.removeAllElements();
		for (User u : DataServer.listuser) {
			listusermodel.addElement(u.getName());
		}
		usersList.setModel(listusermodel);
	}

	/**
	 * 获取当前时间的格式化时间戳
	 *
	 * @return String时间戳
	 */
	public String getMyDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("[yyyy/MM/dd-HH:mm:ss] ");
		Date date = new Date();
		String str_time = dateFormat.format(date);
		return str_time;
	}

	/**
	 * 写日志方法
	 *
	 * @param str     待写入的文本,会加上时间戳处理
	 * @param attrSet 传进的富文本
	 */
	public void insert(String str, AttributeSet attrSet) {
		Document doc = log.getDocument();
		str = str + "\n";
		try {
			doc.insertString(doc.getLength(), str, attrSet);
		} catch (BadLocationException e) {
			System.out.println("BadLocationException:   " + e);
		}
	}

	/**
	 * 写错误日志--红色加粗字体 构造需要些的文本传入insert方法
	 *
	 * @param text 待写的日志内容
	 */
	public void writeErrlog(String text) {
		text = getMyDate() + text;
		SimpleAttributeSet attrSet = new SimpleAttributeSet();
		StyleConstants.setForeground(attrSet, Color.RED);
		StyleConstants.setBold(attrSet, true);
		StyleConstants.setFontSize(attrSet, 14);
		insert(text, attrSet);
	}

	/**
	 * 写日志--黑色正常字体 构造需要些的文本传入insert方法
	 *
	 * @param text 待写的日志内容
	 */
	public void writelog(String text) {
		text = getMyDate() + text;
		SimpleAttributeSet attrSet = new SimpleAttributeSet();
		StyleConstants.setForeground(attrSet, Color.BLACK);
		StyleConstants.setFontSize(attrSet, 14);
		insert(text, attrSet);
	}
}
