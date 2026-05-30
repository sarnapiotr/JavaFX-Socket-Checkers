package pl.edu.pk.checkers.server;

import pl.edu.pk.checkers.common.message.MessageHandler;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler {
    private final Socket socket;
    private final MessageHandler messageHandler;
    private final String username;

    public ClientHandler(Socket socket, MessageHandler messageHandler, String username) {
        this.socket = socket;
        this.messageHandler = messageHandler;
        this.username = username;
    }

    public Socket getSocket() {
        return socket;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public String getUsername() {
        return username;
    }
}
