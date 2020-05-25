package user;

import java.net.InetAddress;

public class User {

	private String name;// 名字
	private int udpPort;// UDP端口
	private InetAddress inetAddress;// IP地址

	// 构造方法,不提供空参构造
	public User(String name, int udpPort, InetAddress inetAddress) {
		this.name = name;
		this.udpPort = udpPort;
		this.inetAddress = inetAddress;
	}

	// getter方法
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPort() {
		return udpPort;
	}

	public void setPort(int port) {
		this.udpPort = port;
	}

	public InetAddress getInetAddress() {
		return inetAddress;
	}

	public void setInetAddress(InetAddress inetAddress) {
		this.inetAddress = inetAddress;
	}

	// 重写toString方法
	public String toString() {
		return "user [name=" + name + ", udpPort=" + udpPort + ", inetAddress=" + inetAddress + "]";
	}

}
