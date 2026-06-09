package pl.edu.pk.checkers.server;

import pl.edu.pk.checkers.common.message.AuthData;
import pl.edu.pk.checkers.common.message.Message;
import pl.edu.pk.checkers.common.message.MessageHandler;
import pl.edu.pk.checkers.common.message.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginSession implements Runnable {
    private static final Logger logger = Logger.getLogger(LoginSession.class.getName());
    private final Socket socket;
    private final DatabaseManager databaseManager;
    private final ServerLobby serverLobby;

    public LoginSession(Socket socket, DatabaseManager databaseManager, ServerLobby serverLobby) {
        this.socket = socket;
        this.databaseManager = databaseManager;
        this.serverLobby = serverLobby;
    }

    @Override
    public void run() {
        boolean isLoggedIn = false;

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            MessageHandler messageHandler = new MessageHandler(in, out);

            Message clientMessage;
            while ((clientMessage = messageHandler.receiveMessage()) != null) {
                if (clientMessage.getType() == MessageType.LOGIN) {
                    AuthData authData = clientMessage.getContentAs(AuthData.class);
                    int clientId = databaseManager.loginUser(authData.getUsername(), authData.getPassword());

                    if (clientId != -1) {
                        messageHandler.sendMessage(MessageType.LOGIN_SUCCESS, "");

                        ClientHandler clientHandler = new ClientHandler(socket, messageHandler, clientId, authData.getUsername());
                        serverLobby.addClientToQueue(clientHandler);

                        isLoggedIn = true;
                        break;
                    } else {
                        messageHandler.sendMessage(MessageType.ERROR, "Login error");
                    }
                } else if (clientMessage.getType() == MessageType.REGISTER) {
                    AuthData authData = clientMessage.getContentAs(AuthData.class);
                    boolean isRegistered = databaseManager.registerUser(authData.getUsername(), authData.getPassword());

                    if (isRegistered) {
                        messageHandler.sendMessage(MessageType.REGISTER_SUCCESS, "");
                    } else {
                        messageHandler.sendMessage(MessageType.ERROR, "Register error");
                    }
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error caught", e);
        } finally {
            if (!isLoggedIn) {
                try { socket.close(); } catch (IOException e) { logger.log(Level.SEVERE, "Error caught", e); }
                logger.info("Client disconnected before logging in");
            }
        }
    }
}
