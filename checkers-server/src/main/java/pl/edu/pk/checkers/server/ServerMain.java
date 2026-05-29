package pl.edu.pk.checkers.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import pl.edu.pk.checkers.common.message.Message;
import pl.edu.pk.checkers.common.message.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMain {
    private static final int PORT = 8080;
    private static final Gson gson = new Gson();

    public static void main(String args[]) {
        System.out.println("Server started");

        try ( ServerSocket serverSocket = new ServerSocket(PORT);
              ExecutorService pool = Executors.newCachedThreadPool(); ) {

            System.out.println("Server listening on port " + PORT);

            String jsonInput = null;
            Message clientMessage = null;
            Message serverMessage = null;

            ClientHandler client1Handler = null;
            ClientHandler client2Handler = null;

            while (true) {
                Socket tempSocket = serverSocket.accept();
                BufferedReader tempIn = new BufferedReader(new InputStreamReader(tempSocket.getInputStream()));
                PrintWriter tempOut = new PrintWriter(tempSocket.getOutputStream(), true);

                jsonInput = tempIn.readLine();
                clientMessage = gson.fromJson(jsonInput, Message.class);

                if (clientMessage != null && clientMessage.getType() == MessageType.JOIN) {
                    String tempUsername = gson.fromJson(clientMessage.getContent(), String.class);
                    System.out.println("[CLIENT " + tempUsername + " CONNECTED]");

                    if (client1Handler == null) {
                        serverMessage = new Message(MessageType.WAITING, gson.toJsonTree("Waiting for opponent"));
                        tempOut.println(gson.toJson(serverMessage));
                        client1Handler = new ClientHandler(tempSocket, tempIn, tempOut, tempUsername);
                    } else {
                        client2Handler = new ClientHandler(tempSocket, tempIn, tempOut, tempUsername);
                        GameSession gameSession = new GameSession(client1Handler, client2Handler);
                        pool.execute(gameSession);
                        client1Handler = null;
                        client2Handler = null;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error caught: " + e.getMessage());
        }

        System.out.println("Server terminated");
    }
}

/*
Recieve
String jsonInput = in.readLine();
Message message = gson.fromJson(jsonInput, Message.class);
MessageType type = message.getType();
String str = gson.fromJson(message.getContent(), String.class);

Send
Message message = new Message(MessageType.TYPE, gson.toJsonTree(sentObject));
out.println(gson.toJson(message));
*/