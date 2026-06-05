package pl.edu.pk.checkers.common.message;

public class ClientStats {
    private String username;
    private int gamesWon;
    private int gamesPlayed;

    public ClientStats(String username, int gamesWon, int gamesPlayed) {
        this.username = username;
        this.gamesWon = gamesWon;
        this.gamesPlayed = gamesPlayed;
    }

    public String getUsername() {
        return username;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }
}
