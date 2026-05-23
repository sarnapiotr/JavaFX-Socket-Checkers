package pl.edu.pk.checkers.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientMain {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 8080;

    public static void main(String args[]) {
        System.out.println("Connecting to the server");

        try ( Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
              BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
              PrintWriter out = new PrintWriter(socket.getOutputStream(), true); ){

            System.out.println("Connected to server");

            String serverMessage;
            while ((serverMessage = in.readLine()) != null && !serverMessage.equals("---TERMINATE---")) {
                System.out.println(serverMessage);
            }

            System.out.println("Connection terminated");

        } catch (IOException e) {
            System.err.println("Error caugth: " + e.getMessage());
        }
    }
}
