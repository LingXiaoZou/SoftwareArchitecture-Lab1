package lab1.model;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class Exchange {
    private String name;
    private String type;
    private Map<String, Queue> bindings;

    public Exchange(String name, String type) {
        this.name = name;
        this.type = type;
        this.bindings = new ConcurrentHashMap<String, Queue>();
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Map<String, Queue> getBindings() {
        return bindings;
    }

    public void bind(Queue queue, String routingkey){
        bindings.put(routingkey, queue);
    }

    // 路由，根据路由信息将消息送入正确的队列
    public void routeMessage(Message message){
        String mode = message.getSendMode();
        switch (mode) {
            case "Broadcast":

                break;
            case "P2P":

                break;
            case "Subscribe":

                break;
            // ... other types
        }
    }

    public void enQueue(String type, Message msg){

    }
}
