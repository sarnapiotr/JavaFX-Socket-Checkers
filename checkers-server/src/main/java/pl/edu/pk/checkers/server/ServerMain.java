package pl.edu.pk.checkers.server;

import com.google.gson.Gson;

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
            Socket tempSocket = null;
            BufferedReader tempIn = null;
            PrintWriter tempOut = null;
            String tempUsername = null;
            ClientData client1Data = null;
            ClientData client2Data = null;

            while (true) {
                tempSocket = serverSocket.accept();
                tempIn = new BufferedReader(new InputStreamReader(tempSocket.getInputStream()));
                tempOut = new PrintWriter(tempSocket.getOutputStream(), true);
                tempUsername = tempIn.readLine();
                System.out.println("[CLIENT " + tempUsername + " CONNECTED]");

                if (client1Data == null) {
                    tempOut.println("Waiting for opponent");
                    client1Data = new ClientData(tempSocket, tempIn, tempOut, tempUsername);
                } else {
                    client2Data = new ClientData(tempSocket, tempIn, tempOut, tempUsername);
                    GameSession gameSession = new GameSession(client1Data, client2Data);
                    pool.execute(gameSession);
                    client1Data = null;
                    client2Data = null;
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
