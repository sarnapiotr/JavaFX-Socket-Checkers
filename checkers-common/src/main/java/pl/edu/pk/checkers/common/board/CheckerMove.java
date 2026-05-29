package pl.edu.pk.checkers.common.board;

import java.util.ArrayList;
import java.util.List;

public class CheckerMove {
    private final Position startPosition;
    private final List<Position> landingPositions;
    private final List<Position> capturedPositions;
    private final Position endPosition;

    public CheckerMove(Position startPosition, List<Position> landingPositions, List<Position> capturedPositions, Position endPosition) {
        this.startPosition = startPosition;
        this.landingPositions = new ArrayList<>(landingPositions);
        this.capturedPositions = new ArrayList<>(capturedPositions);
        this.endPosition = endPosition;
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public List<Position> getLandingPositions() {
        return landingPositions;
    }

    public List<Position> getCapturedPositions() {
        return capturedPositions;
    }

    public Position getEndPosition() {
        return endPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckerMove checkerMove = (CheckerMove) o;
        return java.util.Objects.equals(startPosition, checkerMove.startPosition) && java.util.Objects.equals(landingPositions, checkerMove.landingPositions) &&
                java.util.Objects.equals(capturedPositions, checkerMove.capturedPositions) && java.util.Objects.equals(endPosition, checkerMove.endPosition);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(startPosition, landingPositions, capturedPositions, endPosition);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(startPosition).append("\n");
        for (Position position : landingPositions) {
            str.append(position).append(" | ");
        }
        str.append("\n");
        for (Position position : capturedPositions) {
            str.append(position).append(" | ");
        }
        str.append("\n");
        str.append(endPosition);

        return str.toString();
    }
}
