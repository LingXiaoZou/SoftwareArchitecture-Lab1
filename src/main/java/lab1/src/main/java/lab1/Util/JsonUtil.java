package lab1.Util;

import com.google.gson.Gson;
import lab1.model.Message;

public class JsonUtil {

    private static final Gson gson = new Gson();

    public static String serializeMessage(Message message) {
        return gson.toJson(message);
    }

    public static Message deserializeMessage(String json) {
        return gson.fromJson(json, Message.class);
    }
}
