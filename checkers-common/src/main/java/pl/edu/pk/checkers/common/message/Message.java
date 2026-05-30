package pl.edu.pk.checkers.common.message;

import com.google.gson.JsonElement;
import com.google.gson.Gson;

public class Message {
    private MessageType type;
    private JsonElement content;
    private static final transient Gson GSON = new Gson();

    public Message(MessageType type, JsonElement content) {
        this.type = type;
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public JsonElement getContent() {
        return content;
    }

    public <T> T getContentAs(Class<T> tClass) {
        return GSON.fromJson(content, tClass);
    }
}
