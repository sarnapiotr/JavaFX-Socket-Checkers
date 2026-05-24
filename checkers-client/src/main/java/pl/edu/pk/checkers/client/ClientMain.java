package pl.edu.pk.checkers.client;

import java.util.Scanner;

public class ClientMain {
    public static void main(String args[]) throws InterruptedException {
        Scanner sc = new Scanner(System.in);

        String username = "";
        System.out.println("Enter username: ");
        username = sc.nextLine();
        System.out.println("Connecting to the server");

        SocketService socketService = new SocketService(username);
        socketService.startSocketService();

        Thread.sleep(500);

        while (socketService.isRunning()) {
            if (socketService.isMyTurn()) {
                String str = "";
                System.out.println("Enter message: ");
                str = sc.nextLine();
                socketService.sendMove(str);
            } else {
                Thread.sleep(100);
            }
        }
    }
}
