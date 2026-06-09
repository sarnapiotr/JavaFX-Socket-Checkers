package pl.edu.pk.checkers.server;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ServerLogger {
    public static void setupServerLogger() {
        Logger logger = Logger.getLogger("pl.edu.pk.checkers.server");

        try {
            FileHandler fileHandler = new FileHandler("serverRaport.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            System.err.println("Error caught: " + e.getMessage());
        }
    }
}
