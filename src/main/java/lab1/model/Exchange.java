package lab1.model;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Exchange {
    private String name;
    private Map<String, BlockingQueue<Message>> bindings;

    public Exchange(String name, ConcurrentHashMap<String, BlockingQueue<Message>> bindings) {
        this.name = name;
        this.bindings = bindings;
    }

    public String getName() {
        return name;
    }

    public Map<String, BlockingQueue<Message>> getBindings() {
        return bindings;
    }

    public void bind(BlockingQueue<Message> queue, String routingkey){
        bindings.put(routingkey, queue);
    }

    // 路由，根据路由信息将消息送入正确的队列

    /**
     * 根据路由信息以及发布模式将消息送入正确的队列
     * 模式包括：广播fanout，点对点direct，订阅模式subscribe，默认为subscribe
     * @param message
     */
    public void routeMessage(Message message){
        String type = message.getDataType();
        String mode = message.getSendMode();

        if( mode.equals("fanout")) {
            // 广播给所有队列
            for (BlockingQueue<Message> queue : bindings.values()) {
                try {
                    queue.put(message);
                } catch (InterruptedException e) {
                    // 处理异常
                    System.out.println("Exchange.routeMessage.fanout error\n");
                }
            }
        } else {
            // 点对点/订阅模式，发送给特定的队列
            try {
                BlockingQueue<Message> queue = bindings.get(type);
                queue.put(message);
            } catch (InterruptedException e) {
                System.out.println("Exchange.routeMessage.direct/sub error\n");
            }
        }

        }

}
