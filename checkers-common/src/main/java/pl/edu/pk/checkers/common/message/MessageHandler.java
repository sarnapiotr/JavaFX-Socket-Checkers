package pl.edu.pk.checkers.common.message;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class MessageHandler {
    private final BufferedReader in;
    private final PrintWriter out;
    private static final Gson gson = new Gson();

    public MessageHandler(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }

    public void sendMessage(MessageType messageType, Object object) {
        Message message = new Message(messageType, gson.toJsonTree(object));
        out.println(gson.toJson(message, Message.class));
    }

    public Message receiveMessage() throws IOException {
        String jsonInput = in.readLine();
        if (jsonInput == null) return null;
        return gson.fromJson(jsonInput, Message.class);
    }
}

/*
Receive
String jsonInput = in.readLine();
Message message = gson.fromJson(jsonInput, Message.class);
MessageType type = message.getType();
String str = gson.fromJson(message.getContent(), String.class);

Send
Message message = new Message(MessageType.TYPE, gson.toJsonTree(sentObject));
out.println(gson.toJson(message));
*/