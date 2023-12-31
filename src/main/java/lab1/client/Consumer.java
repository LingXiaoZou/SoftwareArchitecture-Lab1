package lab1.client;

import lab1.Util.JsonUtil;
import lab1.config.config;
import lab1.model.Message;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Consumer implements Runnable{
    /**
     * 启动即监听信息
     */
    private String name;

    private Integer PORT;

    public Consumer(String name) throws IOException {
        this.name = name;
    }
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            // 设置 PORT 成员变量为实际使用的端口号
            this.PORT = serverSocket.getLocalPort();
            System.out.println(name + " is running on port: " + PORT);

            while (true) {
                try (Socket socket = serverSocket.accept();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    String line;
                    StringBuilder data = new StringBuilder();

                    // 读取所有行，直到输入结束
                    while ((line = reader.readLine()) != null) {
                        data.append(line).append("\n");
                    }

                    // 这里处理接收到的消息
                    System.out.println(name + " received message: " + data);
                } catch (IOException e) {
                    System.out.println(name + " Error reading message: " + e.getMessage());
                    // Optionally continue to the next iteration after logging the error
                }
            }
        } catch (IOException e) {
            System.out.println(name + " Could not start server on port: " + PORT);
            e.printStackTrace();
        }
    }

    public InetSocketAddress getSocketAddress(){
        return new InetSocketAddress(config.LOCAL_HOST, PORT);
    }
}

