package lab1.model;


import lombok.Data;

@Data
public class Message {
    private final String name;
    private final String sendMode;
    private final String routingKey;
    private final String data;

}
