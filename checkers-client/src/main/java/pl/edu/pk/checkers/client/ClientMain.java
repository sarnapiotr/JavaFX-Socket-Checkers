package pl.edu.pk.checkers.client;

import pl.edu.pk.checkers.common.MessageType;

import java.util.Scanner;

public class ClientMain {
    public static void main(String args[]) throws InterruptedException {
        Scanner sc = new Scanner(System.in);

        String username = "";
        System.out.println("Enter username: ");
        username = sc.nextLine();
        System.out.println("Connecting to the server");

        SocketService socketService = new SocketService(username);

        socketService.setHandleServerMessage((serverMessage) -> {
            switch (serverMessage.getType()) {
                case MessageType.WAITING:
                    System.out.println(serverMessage.getContent());
                    break;
                case MessageType.GAME_START:
                    System.out.println(serverMessage.getContent());
                    break;
                case MessageType.YOUR_TURN:
                    new Thread(() -> {
                        String str = "";
                        System.out.println("Enter message: ");
                        str = sc.nextLine();
                        socketService.sendMove(str);
                    }).start();
                    break;
                case MessageType.BOARD_UPDATE:
                    System.out.println(serverMessage.getContent());
                    break;
                case MessageType.GAME_OVER:
                    break;
                default:
                    System.out.println("Unknown MessageType");
                    break;
            }
        });

        socketService.startSocketService();
    }
}
