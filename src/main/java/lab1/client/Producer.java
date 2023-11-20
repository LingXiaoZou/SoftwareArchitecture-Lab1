package lab1.client;

import lab1.Util.JsonUtil;
import lab1.config.Config;
import lab1.model.Message;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Producer {
    /**
     * 发送消息
     */
    private final String name;

    public Producer(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void send(String sendMode, String routingKey, String message) throws Exception {
        // socket
        Socket socket = new Socket(InetAddress.getLocalHost(), Config.SERVICE_PORT);

        try(PrintWriter out = new PrintWriter((socket.getOutputStream()))) {
            Message msg = new Message(this.getName(), sendMode, routingKey, message);
            // JSON 序列化
            String jsonMsg = JsonUtil.serializeMessage(msg);
            // 发送
            out.println(jsonMsg);
            System.out.println(name + " send msg:" + sendMode + "," + routingKey + "," + message);
            out.flush();

        }
    }
}
