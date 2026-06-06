package pl.edu.pk.checkers.client;

import pl.edu.pk.checkers.common.message.Message;
import pl.edu.pk.checkers.common.message.MessageHandler;
import pl.edu.pk.checkers.common.message.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class SocketService {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 8080;

    private MessageHandler messageHandler;
    private Consumer<Message> handleServerMessage;

    public void setHandleServerMessage(Consumer<Message> handleServerMessage) {
        this.handleServerMessage = handleServerMessage;
    }

    public void startSocketService() {
        new Thread(() -> {
            try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                messageHandler = new MessageHandler(in, out);

                System.out.println("Connected to server");

                Message serverMessage = null;
                while ((serverMessage = messageHandler.receiveMessage()) != null) {
                    handleServerMessage.accept(serverMessage);
                }

                System.out.println("Connection terminated");

            } catch (IOException e) {
                System.err.println("Error caught: " + e.getMessage());
            }
        }).start();
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }
}