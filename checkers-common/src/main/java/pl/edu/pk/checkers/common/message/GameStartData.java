package pl.edu.pk.checkers.common.message;

import pl.edu.pk.checkers.common.board.CheckerType;

public class GameStartData {
    private final String opponentUsername;
    private final boolean isWhitePlayer;
    private final CheckerType[][] grid;

    public GameStartData(String opponentUsername, boolean isWhitePlayer, CheckerType[][] grid) {
        this.opponentUsername = opponentUsername;
        this.isWhitePlayer = isWhitePlayer;
        this.grid = grid;
    }

    public String getOpponentUsername() {
        return opponentUsername;
    }

    public boolean isWhitePlayer() {
        return isWhitePlayer;
    }

    public CheckerType[][] getGrid() {
        return grid;
    }
}
