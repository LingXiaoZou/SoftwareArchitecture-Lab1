package lab1.client;

import lab1.config.config;
import lab1.model.Message;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class producer {
    private final String name;

    public producer(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void send(String type, String message) throws Exception {
        Socket socket = new Socket(InetAddress.getLocalHost(), config.SERVICE_PORT);
        try(PrintWriter out = new PrintWriter((socket.getOutputStream()))) {
            Message msg = new Message(this.getName(), type, message);
            out.println("PUBLISH" + '\t' + msg.toString());
            out.flush();
        }
    }
}
