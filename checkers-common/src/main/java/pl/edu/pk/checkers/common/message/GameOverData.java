package pl.edu.pk.checkers.common.message;

import java.util.List;

public class GameOverData {
    private final boolean isWinner;
    private final List<ClientStats> leaderboard;

    public GameOverData(boolean isWinner, List<ClientStats> leaderboard) {
        this.isWinner = isWinner;
        this.leaderboard = leaderboard;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public List<ClientStats> getLeaderboard() {
        return leaderboard;
    }
}
