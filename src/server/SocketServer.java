package server;

import user.User;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;


public class SocketServer {

    // UDP
    private DatagramPacket datagramPacket = null;
    private DatagramSocket datagramSocket = null;

    /**
     * 向所有用户发送
     *
     * @param data
     */
    public void sendToAll(byte[] data) {
        for (User u : DataServer.listuser) {
            datagramPacket = new DatagramPacket(data, data.length, u.getInetAddress(), u.getPort());
            try {
                datagramSocket = new DatagramSocket();
                datagramSocket.send(datagramPacket);
            } catch (IOException e) {

            } finally {
                if (datagramSocket != null) {
                    datagramSocket.close();
                }

            }
        }
    }


    /**
     * 私聊
     *
     * @param inName 发件人
     * @param toName 收件人
     * @param text   发送内容
     */
    public void chatByName(String inName, String toName, String text) {
        byte[] data = ("1~" + inName + "~" + toName + "~" + text).getBytes();
        for (User u : DataServer.listuser) {
            // 向收信人都发送数据
            if (u.getName().equals(toName)) {
                datagramPacket = new DatagramPacket(data, data.length, u.getInetAddress(), u.getPort());
                try {
                    datagramSocket = new DatagramSocket();
                    datagramSocket.send(datagramPacket);
                    break;
                } catch (IOException e) {

                } finally {
                    if (datagramSocket != null) {
                        datagramSocket.close();
                    }
                }
            }
        }
    }

    /**
     * 群聊,将string向所有人发送
     *
     * @param s
     */
    public void chatAll(String s) {
        byte[] data = ("0~" + s.toString()).getBytes();
        sendToAll(data);
    }

    /**
     * 向指定用户发送用户列表数据
     *
     * @param name
     */
    public void sendListByName(String name) {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {

        }
        StringBuffer buffer = new StringBuffer();
        for (User u : DataServer.listuser) {
            buffer.append("~" + u.getName());
        }
        byte[] data = ("2" + buffer.toString()).getBytes();

        for (User u : DataServer.listuser) {
            if (u.getName().equals(name)) {
                datagramPacket = new DatagramPacket(data, data.length, u.getInetAddress(), u.getPort());
                try {
                    datagramSocket = new DatagramSocket();
                    datagramSocket.send(datagramPacket);
                } catch (IOException e) {

                }
                return;
            }
        }

    }

    /**
     * 向全部在线用户发送用户列表
     */
    public void sendListToAll() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {

        }
        StringBuffer buffer = new StringBuffer();
        for (User u : DataServer.listuser) {
            buffer.append("~" + u.getName());
        }
        byte[] data = ("2" + buffer.toString()).getBytes();
        sendToAll(data);
    }

    /**
     * 查看本机指定端口号是否被占用
     *
     * @param port 指定端口号
     * @return 占用返回true
     */
    public boolean isLocalPortUsing(int port) {
        try {
            Socket Skt = new Socket("localhost", port);
            Skt.close();
            return true;
        } catch (IOException e) {
            return false;
        }

    }

    /**
     * 多网卡指定名字类型返回指定InetAddress 输入类型错误则输出全部网卡接口信息
     *
     * @param Name 名字
     * @param type 类型: 4--Inet4Address 6--Inet6Address
     * @return
     */
    public InetAddress getInetAddress(String Name, int type) {
        try {
            Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
            InetAddress addr;
            if (type == 4) {
                while (nifs.hasMoreElements()) {
                    NetworkInterface nif = nifs.nextElement();
                    Enumeration<InetAddress> addresses = nif.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        addr = addresses.nextElement();
                        if (nif.getName().equals(Name) && addr instanceof Inet4Address) {
                            return addr;
                        }
                    }
                }
            } else if (type == 6) {
                while (nifs.hasMoreElements()) {
                    NetworkInterface nif = nifs.nextElement();
                    Enumeration<InetAddress> addresses = nif.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        addr = addresses.nextElement();
                        if (nif.getName().equals(Name) && addr instanceof Inet6Address) {
                            return addr;
                        }
                    }
                }
            } else {

                while (nifs.hasMoreElements()) {
                    NetworkInterface nif = nifs.nextElement();
                    Enumeration<InetAddress> addresses = nif.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        addr = addresses.nextElement();
                    }
                }
                return null;
            }

        } catch (Exception e) {

        }
        return null;
    }

}
