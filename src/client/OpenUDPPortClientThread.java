package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.Callable;

import server.DataServer;

//客户端开启UDP监听线程
public class OpenUDPPortClientThread implements Callable<Boolean> {

    private ChatClient chatClient;
    private ViewClient viewClient;
    private int port;

    private DatagramSocket datagramSocket = null;
    private DatagramPacket datagramPacket = null;
    private byte[] data = new byte[1024];

    //构造方法,不提供空参构造
    public OpenUDPPortClientThread(ViewClient viewClient, int port) {
        this.viewClient = viewClient;
        this.port = port;
    }

    public void setChatClient(ChatClient client) {
        this.chatClient = client;
    }


    @Override
    public Boolean call() {

        try {

            datagramSocket = new DatagramSocket(port);
            datagramPacket = new DatagramPacket(data, data.length);
            System.out.println("客户端开始监听：" + port);
            while (true) {
                datagramSocket.receive(datagramPacket);

                int length = datagramPacket.getLength();
                String s = new String(data, 0, length);
                String[] datas = s.split("~");
                System.out.println("客户端UDP端口受到数据：" + s);

                switch (data[0]) {
                    case '0'://群发数据
                        viewClient.chatAllOut(datas[1]);
                        break;
                    case '1'://私聊数据
                        String inName = datas[1];
                        String toName = datas[2];
                        String text = datas[3];

                        boolean b = true;

                        //判断是否存在窗口,存在绑定
                        for (ChatClient chatClient : ViewClient.chatClients) {
                            if (chatClient.toName.equals(inName)) {
                                this.chatClient = chatClient;
                                b = false;
                                break;
                            }
                        }

                        //不存在新建
                        if (b) {
                            chatClient = new ChatClient(toName, inName);
                            ViewClient.chatClients.add(chatClient);
                        }

                        chatClient.chat(text);
                        break;
                    case '2'://用户列表
                        String userlist = s.substring(2, s.length());
                        viewClient.fulshuser(userlist);
                        break;
                    case '4'://关闭流数据
                        datagramSocket.close();
                        return true;
                    default:
                        break;
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
