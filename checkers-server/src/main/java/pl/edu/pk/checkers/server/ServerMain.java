package pl.edu.pk.checkers.server;

import pl.edu.pk.checkers.common.message.Message;
import pl.edu.pk.checkers.common.message.MessageHandler;
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

    public static void main(String args[]) {
        System.out.println("Server started");

        try ( ServerSocket serverSocket = new ServerSocket(PORT);
              ExecutorService pool = Executors.newCachedThreadPool(); ) {

            System.out.println("Server listening on port " + PORT);

            Message clientMessage = null;

            ClientHandler client1Handler = null;
            ClientHandler client2Handler = null;

            while (true) {
                Socket tempSocket = serverSocket.accept();
                BufferedReader tempIn = new BufferedReader(new InputStreamReader(tempSocket.getInputStream()));
                PrintWriter tempOut = new PrintWriter(tempSocket.getOutputStream(), true);
                MessageHandler tempMessageHandler = new MessageHandler(tempIn, tempOut);

                clientMessage = tempMessageHandler.receiveMessage();
                if (clientMessage != null && clientMessage.getType() == MessageType.JOIN) {
                    String tempUsername = clientMessage.getContentAs(String.class);
                    System.out.println("[CLIENT " + tempUsername + " CONNECTED]");

                    if (client1Handler == null) {
                        tempMessageHandler.sendMessage(MessageType.WAITING, "Waiting for opponent");
                        client1Handler = new ClientHandler(tempSocket, tempMessageHandler, tempUsername);
                    } else {
                        client2Handler = new ClientHandler(tempSocket, tempMessageHandler, tempUsername);
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