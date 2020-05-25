package client;

import javax.swing.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.FutureTask;

public class SocketClient {

	// 服务器配置信息
	public static InetAddress InetAddress_server = null;
	public static int TcpPort_server = 0;
	public static int UdpPort_server = 0;

	// 静态代码块读取配置信息
	static {
		initializationame();
	}

	/**
	 * 初始化方法
	 *
	 * @return
	 */
	public static void initializationame() {
		try {
			initializationame_server();
		} catch (IOException e) {
			System.out.println("初始化服务器信息失败");
		}
	}

	/**
	 * 获取服务器配置信息
	 *
	 * @throws IOException
	 */
	public static void initializationame_server() throws IOException {
		Properties pro = new Properties();
		FileReader fr = new FileReader(".//src//server.properties");
		pro.load(fr);
		InetAddress_server = InetAddress.getByName(pro.getProperty("InetAddress_server"));
		TcpPort_server = Integer.valueOf(pro.getProperty("TcpPort_server"));
		UdpPort_server = Integer.valueOf(pro.getProperty("UdpPort_server"));
		fr.close();
	}

	private ViewClient viewClient;// 视图页面
	private WelcomeClient welcomeClient;// 欢迎页面
	private ChatClient chatClient;// 私聊页面

	public SocketClient() {

	}

	public SocketClient(ChatClient chatClient) {
		this.chatClient = chatClient;
	}

	public SocketClient(WelcomeClient welcomeClient) throws IOException {
		this.welcomeClient = welcomeClient;
	}

	public SocketClient(ViewClient viewClient) throws IOException {
		this.viewClient = viewClient;
	}

	// UDP
	private DatagramPacket datagramPacket = null;
	private DatagramSocket datagramSocket = null;

	// TCP
	private Socket socket = null;
	private OutputStream outputStream = null;

	/**
	 * 私聊
	 *
	 * @param data
	 */
	public void chat(String data) {

		byte[] date = ("1~" + data).getBytes();

		try {
			datagramPacket = new DatagramPacket(date, date.length, InetAddress_server, UdpPort_server);
			datagramSocket = new DatagramSocket();
			datagramSocket.send(datagramPacket);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭流资源
			if (datagramSocket != null) {
				datagramSocket.close();
			}
		}
	}

	/**
	 * 群聊
	 *
	 * @param data
	 */
	public void chatAll(String data) {
		byte[] date = ("0~" + data).getBytes();
		try {
			datagramPacket = new DatagramPacket(date, date.length, InetAddress_server, UdpPort_server);
			datagramSocket = new DatagramSocket();
			datagramSocket.send(datagramPacket);
		} catch (IOException e) {

		} finally {
			// 关闭流资源
			if (datagramSocket != null) {
				datagramSocket.close();
			}
		}
	}

	/**
	 * 向服务器发出刷新用户列表请求
	 *
	 * @param name
	 */
	public void fulshUserList(String name) {
		byte[] date = ("2~" + name).getBytes();
		try {
			datagramPacket = new DatagramPacket(date, date.length, InetAddress_server, UdpPort_server);
			datagramSocket = new DatagramSocket();
			datagramSocket.send(datagramPacket);
		} catch (IOException e) {

		} finally {
			// 关闭流资源
			if (datagramSocket != null) {
				datagramSocket.close();
			}
		}
	}

	/**
	 * 下线
	 *
	 * @param name
	 */
	public void down(String name, int udpPort) {
		// 向服务器发送下线
		byte[] date = (1 + "~" + name).getBytes();
		try {
			socket = new Socket(InetAddress_server.getHostAddress(), TcpPort_server);
			outputStream = socket.getOutputStream();
			outputStream.write(date);
		} catch (IOException e) {

		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		// 向自己发送下线
		date = "4".getBytes();
		try {
			datagramPacket = new DatagramPacket(date, date.length, InetAddress.getByName("127.0.0.1"), udpPort);
			datagramSocket = new DatagramSocket();
			datagramSocket.send(datagramPacket);
		} catch (IOException e) {
		} finally {
			// 关闭流资源
			if (datagramSocket != null) {
				datagramSocket.close();
			}
		}
	}

	/**
	 * 上线操作
	 *
	 * @param name
	 * @param UDPPort
	 */
	public void land(String name, int UDPPort) {
		byte[] date = (0 + "~" + name + "~" + UDPPort).getBytes();
		try {
			socket = new Socket(InetAddress_server.getHostAddress(), TcpPort_server);
			outputStream = socket.getOutputStream();
			outputStream.write(date);

			InputStream inputStream = socket.getInputStream();
			byte[] returndata = new byte[1024];
			inputStream.read(returndata);

			switch (returndata[1]) {
			case '0':
				JOptionPane.showMessageDialog(null, "用户名已占用", "", JOptionPane.ERROR_MESSAGE);
				break;
			case '1':
				JOptionPane.showMessageDialog(null, "登陆成功", "", JOptionPane.INFORMATION_MESSAGE);
				welcomeClient.dispose();
				// 监听UDP端口
				new Thread(new FutureTask<>(
						new OpenUDPPortClientThread(viewClient = new ViewClient(name, UDPPort), UDPPort))).start();

				break;
			default:
				break;
			}
			inputStream.close();
			outputStream.close();
			socket.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "连接服务器失败", "", JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * 获得一个有效的UDP端口
	 *
	 * @return UDP端口号
	 */
	public static int getUDPPort() {
		Random random = new Random();
		int UDPPort = random.nextInt(65536 - 10000) + 10000;
		DatagramSocket Skt = null;
		while (true) {
			try {
				Skt = new DatagramSocket(UDPPort);
				return UDPPort;
			} catch (IOException e) {
			} finally {
				if (Skt != null) {
					Skt.close();
				}
			}
			UDPPort = random.nextInt(65536 - 10000) + 10000;
		}
	}

	public ViewClient getViewClient() {
		return viewClient;
	}

	public void setViewClient(ViewClient viewClient) {
		this.viewClient = viewClient;
	}

	public WelcomeClient getWelcomeClient() {
		return welcomeClient;
	}

	public void setWelcomeClient(WelcomeClient welcomeClient) {
		this.welcomeClient = welcomeClient;
	}

	public ChatClient getChatClient() {
		return chatClient;
	}

	public void setChatClient(ChatClient chatClient) {
		this.chatClient = chatClient;
	}
}
