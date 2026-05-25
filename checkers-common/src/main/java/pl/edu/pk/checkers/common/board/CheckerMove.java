package pl.edu.pk.checkers.common.board;

import java.util.List;

public class CheckerMove {
    private Position endPosition;
    private List<Position> capturedCheckers;

    public CheckerMove(Position endPosition, List<Position> capturedCheckers) {
        this.endPosition = endPosition;
        this.capturedCheckers = capturedCheckers;
    }

    public Position getEndPosition() {
        return endPosition;
    }

    public List<Position> getCapturedCheckers() {
        return capturedCheckers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckerMove checkerMove = (CheckerMove) o;
        return java.util.Objects.equals(endPosition, checkerMove.endPosition) && java.util.Objects.equals(capturedCheckers, checkerMove.capturedCheckers);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(endPosition, capturedCheckers);
    }
}
