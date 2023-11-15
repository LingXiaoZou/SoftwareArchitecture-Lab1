package lab1.model;

import java.util.Map;
import java.util.Queue;

public class Exchange {
    private String name;
    private String type;
    private Map<String, Queue> bindings;

    public Exchange(String name, String type, Map<String, Queue> bindings) {
        this.name = name;
        this.type = type;
        this.bindings = bindings;
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
    public void routeMessage(Message message, String type){
        switch (type) {
            case "Broadcast":
                // Direct routing logic
                break;
            case "P2P":
                // Topic routing logic
                break;
            case "Subscribe":
                // Fan-out routing logic
                break;
            // ... other types
        }
    }

    public void enQueue(String type, Message msg){

    }
}
