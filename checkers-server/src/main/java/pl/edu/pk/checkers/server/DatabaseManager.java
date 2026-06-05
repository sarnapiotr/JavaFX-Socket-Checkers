package pl.edu.pk.checkers.server;

import org.mindrot.jbcrypt.BCrypt;
import pl.edu.pk.checkers.common.message.ClientStats;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL_SERVER = "jdbc:mysql://localhost:3306/";
    private static final String URL_DB = "jdbc:mysql://localhost:3306/checkers";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public DatabaseManager() {
        initDatabase();
    }

    private void initDatabase() {
        try (Connection conn = DriverManager.getConnection(URL_SERVER, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS checkers;");
            System.out.println("Database 'checkers' checked/created");

        } catch (SQLException e) {
            System.err.println("Error caught: " + e.getMessage());
        }

        try (Connection conn = DriverManager.getConnection(URL_DB, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS clients (" +
                    "clientId INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(255) UNIQUE NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "gamesWon INT DEFAULT 0, " +
                    "gamesPlayed INT DEFAULT 0)");

            System.out.println("Table 'clients' checked/created");
        } catch (SQLException e) {
            System.err.println("Error caught: " + e.getMessage());
        }
    }

    public int authenticateUser(String username, String password) {
        String sql = "SELECT clientId, password FROM clients WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(URL_DB, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");

                    if (BCrypt.checkpw(password, hashedPassword)) {
                        return rs.getInt("clientId");
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error caught: " + e.getMessage());
        }

        return -1;
    }

    public boolean registerUser(String username, String password) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String sql = "INSERT INTO clients (username, password) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(URL_DB, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.err.println("Error caught: " + e.getMessage());
            return false;
        }
    }

    public void updateStats(int clientId, boolean isWinner) {
        String sql = "UPDATE clients SET gamesPlayed = gamesPlayed + 1, gamesWon = gamesWon + ? WHERE clientId = ?";

        try (Connection conn = DriverManager.getConnection(URL_DB, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, isWinner ? 1 : 0);
            pstmt.setInt(2, clientId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error caught: " + e.getMessage());
        }
    }

    public List<ClientStats> getClientStats() {
        List<ClientStats> leaderboard = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL_DB, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT username, gamesWon, gamesPlayed FROM clients ORDER BY gamesWon DESC, gamesPlayed ASC")) {

            while (rs.next()) {
                leaderboard.add(new ClientStats(
                        rs.getString("username"),
                        rs.getInt("gamesWon"),
                        rs.getInt("gamesPlayed")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error caught: " + e.getMessage());
        }

        return leaderboard;
    }
}