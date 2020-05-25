package server;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;

import user.User;

public class OpenTCPPortServerThread implements Callable<Boolean> {

	ServerSocket serverSocket = null;
	Socket socket = null;

	InputStream inputStream = null;
	OutputStream outputStream = null;

	ViewServer viewServer;
	SocketServer socketServer= new SocketServer();

	public OpenTCPPortServerThread(ViewServer viewServer) {
		this.viewServer = viewServer;
	}

	@Override
	public Boolean call() {
		try {
			serverSocket = new ServerSocket(8989);
			viewServer.writelog("开始监听9898端口");
			while (true) {
				socket = serverSocket.accept();
				inputStream = socket.getInputStream();

				byte[] data = new byte[1024];
				int len = inputStream.read(data);

				String stringdata= new String(data, 0, len);
				String[]  stringdatas=stringdata.split("~");

				System.out.println("TCP端口收到数据：" +stringdata );

				outputStream = socket.getOutputStream();

				switch (data[0]) {
				case '0':// 上线操作
					String loadname = stringdatas[1];
					int udpPort = Integer.valueOf(stringdatas[2]);
					boolean b = true;
					for (User u : DataServer.listuser) {
						if (u.getName().equals(loadname)) {
							b = false;
							break;
						}
					}
					if (b) {// 允许上线
						viewServer.addUser(new User(loadname, udpPort, socket.getInetAddress()));
						viewServer.writelog(loadname + "上线了");
						outputStream.write("01".getBytes());
						socketServer.sendListToAll();
					} else {
						outputStream.write("00".getBytes());
					}
					break;
				case '1':// 下线操作
					String downname = stringdatas[1];
					for (User u : DataServer.listuser) {
						if (u.getName().equals(downname)) {
							viewServer.delUser(u);
							viewServer.writelog(downname + "下线了");
							break;
						}
					}
					socketServer.sendListToAll();
					break;
				case '4':// 自我销毁
					outputStream.close();
					inputStream.close();
					socket.close();
					serverSocket.close();
					return true;
				default:
					viewServer.writeErrlog("TCP端口收到垃圾数据");
					break;
				}

			}

		} catch (IOException e) {
			viewServer.writeErrlog("监听9898端口失败");
			viewServer.state.setForeground(Color.RED);
		} finally {
			// 关闭所有流资源
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
				}
			}

			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}

			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {

				}
			}

		}

		return null;
	}

}