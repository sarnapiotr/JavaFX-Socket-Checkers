package pl.edu.pk.checkers.common.message;

import pl.edu.pk.checkers.common.board.CheckerType;

public class GameStartData {
    private final boolean isWhitePlayer;
    private final String opponentUsername;
    private final CheckerType[][] grid;

    public GameStartData(boolean isWhitePlayer, String opponentUsername, CheckerType[][] grid) {
        this.isWhitePlayer = isWhitePlayer;
        this.opponentUsername = opponentUsername;
        this.grid = grid;
    }

    public boolean isWhitePlayer() {
        return isWhitePlayer;
    }

    public String getOpponentUsername() {
        return opponentUsername;
    }

    public CheckerType[][] getGrid() {
        return grid;
    }
}
