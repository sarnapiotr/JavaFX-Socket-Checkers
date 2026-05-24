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
        String jsonInput = null;
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

            boolean isRunning = true;
            while (isRunning && (jsonInput = in.readLine()) != null) {
                serverMessage = gson.fromJson(jsonInput, Message.class);

                switch (serverMessage.getType()) {
                    case MessageType.WAITING:
                        System.out.println(serverMessage.getContent());
                        break;
                    case MessageType.GAME_START:
                        System.out.println(serverMessage.getContent());
                        break;
                    case MessageType.YOUR_TURN:
                        String msg = "";
                        System.out.println("Enter message: ");
                        msg = sc.nextLine();
                        clientMessage = new Message(MessageType.MOVE, msg);
                        out.println(gson.toJson(clientMessage));
                        break;
                    case MessageType.BOARD_UPDATE:
                        System.out.println(serverMessage.getContent());
                        break;
                    case MessageType.GAME_OVER:
                        System.out.println(serverMessage.getContent());
                        isRunning = false;
                        break;
                    default:
                        System.out.println("Unknown MessageType");
                        break;
                }
            }

            System.out.println("Connection terminated");

        } catch (IOException e) {
            System.err.println("Error caugth: " + e.getMessage());
        } finally {
            sc.close();
        }
    }
}
