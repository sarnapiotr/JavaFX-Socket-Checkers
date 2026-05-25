package pl.edu.pk.checkers.server;

import com.google.gson.Gson;
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
        ExecutorService pool = Executors.newCachedThreadPool();

        try ( ServerSocket serverSocket = new ServerSocket(PORT); ) {
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
                    String tempUsername = clientMessage.getContent();
                    System.out.println("[CLIENT " + tempUsername + " CONNECTED]");

                    if (client1Handler == null) {
                        serverMessage = new Message(MessageType.WAITING, "Waiting for opponent");
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
        } finally {
            pool.shutdown();
            System.out.println("Server terminated");
        }
    }
}
