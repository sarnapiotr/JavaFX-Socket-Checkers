package pl.edu.pk.checkers.common.board;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private CheckerType[][] grid;
    private static final int SIZE = 8;
    private static final int[][] DIRECTIONS = { {1, 1}, {1, -1}, {-1, 1}, {-1, -1} };

    public Board() {
        grid = new CheckerType[SIZE][SIZE];
        initializeBoard();
    }

    private void initializeBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                grid[row][col] = CheckerType.NONE;

                if ((row + col) % 2 != 0) {
                    if (row < 3)
                        grid[row][col] = CheckerType.BLACK;
                    else if (row > 4)
                        grid[row][col] = CheckerType.WHITE;
                }
            }
        }
    }

    private boolean isValidPosition(Position position) {
        return position.getRow() >= 0 && position.getRow() < SIZE && position.getCol() >= 0 && position.getCol() < SIZE;
    }

    public boolean isPlayerChecker(Position position, boolean isWhitePlayer) {
        if (!isValidPosition(position))
            return false;

        CheckerType checkerType = grid[position.getRow()][position.getCol()];
        return (isWhitePlayer && (checkerType == CheckerType.WHITE || checkerType == CheckerType.WHITE_KING)) ||
                (!isWhitePlayer && (checkerType == CheckerType.BLACK || checkerType == CheckerType.BLACK_KING));
    }

    public List<CheckerMove> getAvailableMoves(Position startPosition) {
        CheckerType checkerType = grid[startPosition.getRow()][startPosition.getCol()];
        List<CheckerMove> availableMoves = new ArrayList<>();

        if (checkerType == null || checkerType == CheckerType.NONE)
            return availableMoves;

        if (checkerType == CheckerType.WHITE || checkerType == CheckerType.BLACK) { // Simple checkers
            findSimpleCaptures(startPosition, startPosition, checkerType, new ArrayList<>(), new ArrayList<>(), availableMoves);
            if (availableMoves.isEmpty())
                findSimpleMoves(startPosition, checkerType, availableMoves);

        } else if (checkerType == CheckerType.WHITE_KING || checkerType == CheckerType.BLACK_KING) { // King checkers
            findKingCaptures(startPosition, startPosition, checkerType, new ArrayList<>(), new ArrayList<>(), availableMoves);
            if (availableMoves.isEmpty())
                findKingMoves(startPosition, availableMoves);
        }

        return availableMoves;
    }

    private boolean isEnemy(CheckerType allyCheckerType, CheckerType enemyCheckerType) {
        if (enemyCheckerType == null || enemyCheckerType == CheckerType.NONE) return false;

        boolean isAllyWhite = (allyCheckerType == CheckerType.WHITE || allyCheckerType == CheckerType.WHITE_KING);
        boolean isEnemyBlack = (enemyCheckerType == CheckerType.BLACK || enemyCheckerType == CheckerType.BLACK_KING);

        return isAllyWhite == isEnemyBlack;
    }

    private void findSimpleCaptures(Position startPosition, Position currentPosition, CheckerType checkerType, List<Position> landingPositions, List<Position> capturedPositions, List<CheckerMove> availableMoves) {
        boolean foundFurtherCapture = false;

        for (int[] direction : DIRECTIONS) {
            int rowDirection = direction[0];
            int colDirection = direction[1];

            Position enemyPosition = new Position(currentPosition.getRow() + rowDirection, currentPosition.getCol() + colDirection);
            Position landingPosition = new Position(currentPosition.getRow() + 2 * rowDirection, currentPosition.getCol() + 2 * colDirection);

            if (isValidPosition(landingPosition)) {
                CheckerType enemyCheckerType = grid[enemyPosition.getRow()][enemyPosition.getCol()];
                CheckerType landingCheckerType = grid[landingPosition.getRow()][landingPosition.getCol()];

                if (isEnemy(checkerType, enemyCheckerType) && landingCheckerType == CheckerType.NONE && !capturedPositions.contains(enemyPosition)) {
                    foundFurtherCapture = true;
                    List<Position> newLandingPositions = new ArrayList<>(landingPositions);
                    newLandingPositions.add(landingPosition);
                    List<Position> newCapturedPositions = new ArrayList<>(capturedPositions);
                    newCapturedPositions.add(enemyPosition);
                    findSimpleCaptures(startPosition, landingPosition, checkerType, newLandingPositions, newCapturedPositions, availableMoves);
                }
            }
        }

        if (!foundFurtherCapture && !capturedPositions.isEmpty()) {
            CheckerMove checkerMove = new CheckerMove(startPosition, landingPositions, capturedPositions, currentPosition);

            if (!availableMoves.contains(checkerMove)) {
                availableMoves.add(checkerMove);
            }
        }
    }

    private void findKingCaptures(Position startPosition, Position currentPosition, CheckerType checkerType, List<Position> landingPositions, List<Position> capturedPositions, List<CheckerMove> availableMoves) {
        boolean foundFurtherCapture = false;

        for (int[] direction : DIRECTIONS) {
            boolean foundEnemy = false;

            int rowDirection = direction[0];
            int colDirection = direction[1];

            Position enemyPosition = new Position(currentPosition.getRow() + rowDirection, currentPosition.getCol() + colDirection);

            while (isValidPosition(enemyPosition)) {
                CheckerType enemyCheckerType = grid[enemyPosition.getRow()][enemyPosition.getCol()];

                if (enemyCheckerType != CheckerType.NONE) {
                    if (isEnemy(checkerType, enemyCheckerType) && !capturedPositions.contains(enemyPosition)) {
                        foundEnemy = true;
                    }

                    break;
                }

                enemyPosition = new Position(enemyPosition.getRow() + rowDirection, enemyPosition.getCol() + colDirection);
            }

            Position landingPosition = new Position(enemyPosition.getRow() + rowDirection, enemyPosition.getCol() + colDirection);

            if (foundEnemy) {
                while (isValidPosition(landingPosition)) {
                    if (grid[landingPosition.getRow()][landingPosition.getCol()] == CheckerType.NONE) {
                        foundFurtherCapture = true;
                        List<Position> newLandingPositions = new ArrayList<>(landingPositions);
                        newLandingPositions.add(landingPosition);
                        List<Position> newCapturedPositions = new ArrayList<>(capturedPositions);
                        newCapturedPositions.add(enemyPosition);

                        findKingCaptures(startPosition, landingPosition, checkerType, newLandingPositions, newCapturedPositions, availableMoves);
                    } else {
                        break;
                    }

                    landingPosition = new Position(landingPosition.getRow() + rowDirection, landingPosition.getCol() + colDirection);
                }
            }
        }

        if (!foundFurtherCapture && !capturedPositions.isEmpty()) {
            CheckerMove checkerMove = new CheckerMove(startPosition, landingPositions, capturedPositions, currentPosition);
            if (!availableMoves.contains(checkerMove))
                availableMoves.add(checkerMove);
        }
    }

    private void findSimpleMoves(Position startPosition, CheckerType checkerType, List<CheckerMove> availableMoves) {
        for (int[] direction : DIRECTIONS) {
            int rowDirection = direction[0];
            int colDirection = direction[1];

            if (checkerType == CheckerType.WHITE && rowDirection == 1) continue;
            if (checkerType == CheckerType.BLACK && rowDirection == -1) continue;

            Position endPosition = new Position(startPosition.getRow() + rowDirection, startPosition.getCol() + colDirection);

            if (isValidPosition(endPosition) && grid[endPosition.getRow()][endPosition.getCol()] == CheckerType.NONE) {
                availableMoves.add(new CheckerMove(startPosition, new ArrayList<>(), new ArrayList<>(), endPosition));
            }
        }
    }

    private void findKingMoves(Position startPosition, List<CheckerMove> availableMoves) {
        for (int[] direction : DIRECTIONS) {
            int rowDirection = direction[0];
            int colDirection = direction[1];

            int endRow = startPosition.getRow() + rowDirection;
            int endCol = startPosition.getCol() + colDirection;

            while (isValidPosition(new Position(endRow, endCol))) {
                if (grid[endRow][endCol] == CheckerType.NONE) {
                    availableMoves.add(new CheckerMove(startPosition, new ArrayList<>(), new ArrayList<>(), new Position(endRow, endCol)));
                } else {
                    break;
                }

                endRow += rowDirection;
                endCol += colDirection;
            }
        }
    }

    public boolean hasMandatoryCaptures(boolean isWhitePlayer) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                CheckerType checkerType = grid[row][col];
                if (checkerType != null && checkerType != CheckerType.NONE) {
                    List<CheckerMove> availableMoves = new ArrayList<>();

                    if (isWhitePlayer && checkerType == CheckerType.WHITE || !isWhitePlayer && checkerType == CheckerType.BLACK) {
                        findSimpleCaptures(new Position(row, col), new Position(row, col), checkerType, new ArrayList<>(), new ArrayList<>(), availableMoves);
                        if (!availableMoves.isEmpty()) {
                            return true;
                        }
                    } else if (isWhitePlayer && checkerType == CheckerType.WHITE_KING || !isWhitePlayer && checkerType == CheckerType.BLACK_KING) {
                        findKingCaptures(new Position(row, col), new Position(row, col), checkerType, new ArrayList<>(), new ArrayList<>(), availableMoves);
                        if (!availableMoves.isEmpty()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void executeMove(CheckerMove checkerMove) {
        int startRow = checkerMove.getStartPosition().getRow();
        int startCol = checkerMove.getStartPosition().getCol();
        int endRow = checkerMove.getEndPosition().getRow();
        int endCol = checkerMove.getEndPosition().getCol();

        CheckerType startCheckerType = grid[startRow][startCol];
        CheckerType endCheckerType = startCheckerType;

        if (startCheckerType == CheckerType.WHITE && endRow == 0) {
            endCheckerType = CheckerType.WHITE_KING;
        } else if (startCheckerType == CheckerType.BLACK && endRow == SIZE - 1) {
            endCheckerType = CheckerType.BLACK_KING;
        }

        grid[endRow][endCol] = endCheckerType;
        grid[startRow][startCol] = CheckerType.NONE;

        for (Position position : checkerMove.getCapturedPositions()) {
            grid[position.getRow()][position.getCol()] = CheckerType.NONE;
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                switch (grid[row][col]) {
                    case CheckerType.NONE:
                        str.append("N  ");
                        break;
                    case CheckerType.WHITE:
                        str.append("W  ");
                        break;
                    case CheckerType.BLACK:
                        str.append("B  ");
                        break;
                    case CheckerType.WHITE_KING:
                        str.append("WK ");
                        break;
                    case CheckerType.BLACK_KING:
                        str.append("BK ");
                        break;
                }
            }
            str.append("\n");
        }

        return str.toString();
    }
}