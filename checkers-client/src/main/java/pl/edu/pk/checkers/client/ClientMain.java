package pl.edu.pk.checkers.client;

import com.google.gson.Gson;
import pl.edu.pk.checkers.common.Message;
import pl.edu.pk.checkers.common.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientMain {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 8080;
    private static final Gson gson = new Gson();

    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        Message clientMessage = null;
        Message serverMessage = null;

        String username = "";
        System.out.println("Enter username: ");
        username = sc.nextLine();
        System.out.println("Connecting to the server");

        try ( Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
              BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
              PrintWriter out = new PrintWriter(socket.getOutputStream(), true); ) {

            System.out.println("Connected to server");
            clientMessage = new Message(MessageType.JOIN, username);
            out.println(gson.toJson(clientMessage));

            while ((serverMessage = gson.fromJson(in.readLine(), Message.class)) != null) {
                if (serverMessage.getType() == MessageType.GAME_OVER)
                    break;

                System.out.println(serverMessage.getContent());
            }

            System.out.println("Connection terminated");

        } catch (IOException e) {
            System.err.println("Error caugth: " + e.getMessage());
        } finally {
            sc.close();
        }
    }
}
