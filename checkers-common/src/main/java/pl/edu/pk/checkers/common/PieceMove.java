package pl.edu.pk.checkers.common;

import java.util.ArrayList;
import java.util.List;

public class PieceMove {
    private Position endPosition;
    private List<Position> capturedPieces;

    public PieceMove(Position endPosition) {
        this.endPosition = endPosition;
        capturedPieces = new ArrayList<>();
    }

    public PieceMove(Position endPosition, ArrayList<Position> capturedPieces) {
        this.endPosition = endPosition;
        this.capturedPieces = capturedPieces;
    }

    public Position getEndPosition() {
        return endPosition;
    }

    public List<Position> getCapturedPieces() {
        return capturedPieces;
    }
}
