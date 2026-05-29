package pl.edu.pk.checkers.common.message;

import com.google.gson.JsonElement;

public class Message {
    private MessageType type;
    private JsonElement content;

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
}
