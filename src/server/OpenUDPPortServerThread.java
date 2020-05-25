package server;

import java.awt.Color;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.Callable;

public class OpenUDPPortServerThread implements Callable<Boolean> {

    private DatagramSocket ds = null;
    private DatagramPacket dp = null;
    private byte[] data = new byte[1024];

    private ViewServer viewServer;
    private SocketServer socketServer = new SocketServer();

    public OpenUDPPortServerThread(ViewServer viewServer) {
        this.viewServer = viewServer;
    }

    @Override
    public Boolean call() {

        try {
            ds = new DatagramSocket(9898);
            viewServer.writelog("开始监听9898端口");
            dp = new DatagramPacket(data, data.length);
            while (true) {
                ds.receive(dp);

                int length = dp.getLength();
                String stringdata = new String(data, 0, length);
                String[] stringdatas = stringdata.split("~");

                System.out.println("UDP端口受到数据：" + stringdata);

                switch (data[0]) {
                    case '0':// 群聊
                        socketServer.chatAll(stringdatas[1]);
                        break;
                    case '1'://私聊
                        socketServer.chatByName(stringdatas[1], stringdatas[2], stringdatas[3]);
                        break;
                    case '2'://手动刷新请求
                        socketServer.sendListByName(stringdatas[1]);
                        break;
                    case '4':// 4关闭服务器
                        ds.close();
                        return true;
                    default:
                        viewServer.writelog("UDP端口收到垃圾数据");
                        break;
                }

            }
        } catch (IOException e) {
            viewServer.state.setForeground(Color.RED);
            viewServer.writeErrlog("监听9898端口失败");
        }

        return false;
    }


}
