package pl.edu.pk.checkers.server;

import pl.edu.pk.checkers.common.message.MessageType;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerLobby implements Runnable {
    private static final Logger logger = Logger.getLogger(ServerLobby.class.getName());
    private Queue<ClientHandler> clientQueue = new ConcurrentLinkedQueue<>();
    private ExecutorService pool;
    private DatabaseManager databaseManager;

    public ServerLobby(ExecutorService pool, DatabaseManager databaseManager) {
        this.pool = pool;
        this.databaseManager = databaseManager;
    }

    public synchronized void addClientToQueue(ClientHandler clientHandler) {
        clientHandler.getMessageHandler().sendMessage(MessageType.WAITING, "");
        clientQueue.add(clientHandler);
    }

    @Override
    public void run() {
        while (true) {
            if (clientQueue.size() >= 2) {
                ClientHandler client1Handler = clientQueue.poll();
                ClientHandler client2Handler = clientQueue.poll();
                GameSession gameSession = new GameSession(client1Handler, client2Handler, databaseManager, this);
                pool.execute(gameSession);
            }

            try { Thread.sleep(100); } catch (InterruptedException e) { logger.log(Level.SEVERE, "Error caught", e); }
        }
    }
}
