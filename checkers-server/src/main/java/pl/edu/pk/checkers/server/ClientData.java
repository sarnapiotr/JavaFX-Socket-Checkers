package pl.edu.pk.checkers.server;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientData {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;

    public ClientData(Socket socket, BufferedReader in, PrintWriter out, String username) {
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.username = username;
    }

    public Socket getSocket() {
        return socket;
    }

    public BufferedReader getIn() {
        return in;
    }

    public PrintWriter getOut() {
        return out;
    }

    public String getUsername() {
        return username;
    }
}
