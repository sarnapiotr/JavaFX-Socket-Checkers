package pl.edu.pk.checkers.client;

import com.google.gson.Gson;
import pl.edu.pk.checkers.common.Message;
import pl.edu.pk.checkers.common.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class SocketService {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 8080;
    private static final Gson gson = new Gson();

    private final String username;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private Consumer<Message> handleServerMessage;

    public SocketService(String username) {
        this.username = username;
    }

    public void setHandleServerMessage(Consumer<Message> handleServerMessage) {
        this.handleServerMessage = handleServerMessage;
    }

    public void startSocketService() {
        new Thread(() -> {
            try {
                socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                System.out.println("Connected to server");

                Message clientMessage = null;
                String jsonInput = null;
                Message serverMessage = null;

                clientMessage = new Message(MessageType.JOIN, username);
                out.println(gson.toJson(clientMessage, Message.class));

                while ((jsonInput = in.readLine()) != null) {
                    serverMessage = gson.fromJson(jsonInput, Message.class);
                    handleServerMessage.accept(serverMessage);
                }

                System.out.println("Connection terminated");

            } catch (IOException e) {
                System.err.println("Error caught: " + e.getMessage());
            }
        }).start();
    }

    public void sendMove(String content) {
        Message clientMessage = new Message(MessageType.MOVE, content);
        out.println(gson.toJson(clientMessage, Message.class));
    }
}
