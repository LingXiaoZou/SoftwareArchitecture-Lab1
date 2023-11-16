package lab1.model;

import com.google.gson.Gson;

public class Message {
    private final String name;
    private final String sendMode;
    private final String dataType;
    private final String data;

    public Message(String name, String sendMode, String dataType, String data) {
        this.name = name;
        this.sendMode = sendMode;
        this.dataType = dataType;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public String getSendMode() {
        return sendMode;
    }

    public String getDataType() {
        return dataType;
    }

    public String getData() {
        return data;
    }



}
