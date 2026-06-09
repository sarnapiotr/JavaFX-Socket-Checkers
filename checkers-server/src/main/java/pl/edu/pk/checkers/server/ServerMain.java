package pl.edu.pk.checkers.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerMain {
    private static final int PORT = 8080;
    private static final Logger logger = Logger.getLogger(ServerMain.class.getName());

    public static void main(String[] args) {
        ServerLogger.setupServerLogger();

        logger.info("Server started");
        DatabaseManager databaseManager = new DatabaseManager();

        try (ServerSocket serverSocket = new ServerSocket(PORT);
              ExecutorService pool = Executors.newCachedThreadPool()) {

            logger.info("Server listening on port " + PORT);
            ServerLobby serverLobby = new ServerLobby(pool, databaseManager);
            pool.execute(serverLobby);

            while (true) {
                Socket socket = serverSocket.accept();
                pool.execute(new LoginSession(socket, databaseManager, serverLobby));
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error caught", e);
        }

        logger.info("Server terminated");
    }
}