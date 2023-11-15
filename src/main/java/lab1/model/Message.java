package lab1.model;

public class Message {
    private final String name;
    private final String type;
    private final String data;

    public Message(String name, String type, String data){
        this.name = name;
        this.type = type;
        this.data = data;
    }

    @Override
    public String toString() {
        return "Message{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getData() {
        return data;
    }
}
