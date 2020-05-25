package server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Callable;

public class CloseServerThread implements Callable<Boolean> {

    //视图页面对象
    private ViewServer viewServer;

    //UDP
    private DatagramPacket datagramPacket = null;
    private DatagramSocket datagramSocket = null;

    //TCP
    private Socket socket = null;
    private OutputStream outputStream = null;

    /**
     * 构造方法,将此线程与视图页面绑定
     *
     * @param viewServer
     */
    public CloseServerThread(ViewServer viewServer) {
        this.viewServer = viewServer;
    }

    @Override
    public Boolean call() {

        byte[] date = "4".getBytes();

        try {
            socket = new Socket("127.0.0.1", 8989);
            outputStream = socket.getOutputStream();
            outputStream.write(date);
            viewServer.writelog("TCP端口关闭成功");
        } catch (IOException e) {
            viewServer.writeErrlog("TCP端口关闭失败");
        } finally {
            //关闭流资源
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {

                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {

                }
            }
        }

        try {
            datagramPacket = new DatagramPacket(date, date.length, InetAddress.getByName("127.0.0.1"), 9898);
            datagramSocket = new DatagramSocket();
            datagramSocket.send(datagramPacket);
            viewServer.writelog("UDP端口关闭成功");
        } catch (IOException e) {
            viewServer.writeErrlog("UDP端口关闭失败");
        } finally {
            //关闭流资源
            if (datagramSocket != null) {
                datagramSocket.close();
            }
        }

        return null;

    }
}
