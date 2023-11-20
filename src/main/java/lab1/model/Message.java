package lab1.model;


import lombok.Data;

@Data
public class Message {
    private final String name;
    private final String sendMode;
    private final String queueKey;
    private final String data;

    public Message(String name, String sendMode, String dataType, String data) {
        this.name = name;
        this.sendMode = sendMode;
        this.queueKey = dataType;
        this.data = data;
    }
}
