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

    private boolean isPlayersChecker(CheckerType checkerType, boolean isWhitePlayer) {
        if (checkerType == null || checkerType == CheckerType.NONE) return false;
        return isWhitePlayer ? (checkerType == CheckerType.WHITE || checkerType == CheckerType.WHITE_KING) :
                (checkerType == CheckerType.BLACK || checkerType == CheckerType.BLACK_KING);
    }

    private boolean isKingChecker(CheckerType checkerType) {
        if (checkerType == null || checkerType == CheckerType.NONE) return false;
        return checkerType == CheckerType.WHITE_KING || checkerType == CheckerType.BLACK_KING;
    }

    private List<CheckerMove> getAllMoves(boolean isWhitePlayer) {
        List<CheckerMove> availableMoves = new ArrayList<>();

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                CheckerType checkerType = grid[row][col];
                if (!isPlayersChecker(checkerType, isWhitePlayer)) continue;

                if (isKingChecker(checkerType)) {
                    getKingMoves(new Position(row, col), availableMoves);
                } else {
                    getManMoves(new Position(row, col), availableMoves);
                }
            }
        }

        return availableMoves;
    }

    private List<CheckerMove> getAllCaptures(boolean isWhitePlayer) {
        List<CheckerMove> availableMoves = new ArrayList<>();

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                CheckerType checkerType = grid[row][col];
                if (!isPlayersChecker(checkerType, isWhitePlayer)) continue;

                if (isKingChecker(checkerType)) {
                    getKingCaptures(new Position(row, col), new Position(row, col), new ArrayList<>(), new ArrayList<>(), availableMoves);
                } else {
                    getManCaptures(new Position(row, col), new Position(row, col), new ArrayList<>(), new ArrayList<>(), availableMoves);
                }
            }
        }

        return availableMoves;
    }

    private List<CheckerMove> maxCaptures(List<CheckerMove> availableMoves) {
        int maxCaptured = 0;

        for (CheckerMove checkerMove : availableMoves) {
            maxCaptured = Math.max(maxCaptured, checkerMove.getCapturedPositions().size());
        }

        List<CheckerMove> maxCaptures = new ArrayList<>();
        for (CheckerMove checkerMove : availableMoves) {
            if (checkerMove.getCapturedPositions().size() == maxCaptured) {
                maxCaptures.add(checkerMove);
            }
        }

        return maxCaptures;
    }

    public List<CheckerMove> getAvailableMoves(boolean isWhitePlayer) {
        List<CheckerMove> availableMoves = getAllCaptures(isWhitePlayer);

        if (!availableMoves.isEmpty())
            return maxCaptures(availableMoves);

        return getAllMoves(isWhitePlayer);
    }

    private void getManMoves(Position startPosition, List<CheckerMove> availableMoves) {
        for (int[] direction : DIRECTIONS) {
            int rowDirection = direction[0];
            int colDirection = direction[1];

            CheckerType checkerType = grid[startPosition.getRow()][startPosition.getCol()];

            if (checkerType == CheckerType.WHITE && rowDirection == 1) continue;
            if (checkerType == CheckerType.BLACK && rowDirection == -1) continue;

            Position endPosition = new Position(startPosition.getRow() + rowDirection, startPosition.getCol() + colDirection);

            if (isValidPosition(endPosition) && grid[endPosition.getRow()][endPosition.getCol()] == CheckerType.NONE) {
                availableMoves.add(new CheckerMove(startPosition, new ArrayList<>(), new ArrayList<>(), endPosition));
            }
        }
    }

    private void getKingMoves(Position startPosition, List<CheckerMove> availableMoves) {
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

    private boolean isEnemy(CheckerType allyCheckerType, CheckerType enemyCheckerType) {
        if (enemyCheckerType == null || enemyCheckerType == CheckerType.NONE) return false;

        boolean isAllyWhite = (allyCheckerType == CheckerType.WHITE || allyCheckerType == CheckerType.WHITE_KING);
        boolean isEnemyBlack = (enemyCheckerType == CheckerType.BLACK || enemyCheckerType == CheckerType.BLACK_KING);

        return isAllyWhite == isEnemyBlack;
    }

    private void getManCaptures(Position startPosition, Position currentPosition, List<Position> landingPositions, List<Position> capturedPositions, List<CheckerMove> availableMoves) {
        boolean foundFurtherCapture = false;

        for (int[] direction : DIRECTIONS) {
            int rowDirection = direction[0];
            int colDirection = direction[1];

            Position enemyPosition = new Position(currentPosition.getRow() + rowDirection, currentPosition.getCol() + colDirection);
            Position landingPosition = new Position(currentPosition.getRow() + 2 * rowDirection, currentPosition.getCol() + 2 * colDirection);

            if (isValidPosition(landingPosition)) {
                CheckerType allyCheckerType = grid[startPosition.getRow()][startPosition.getCol()];
                CheckerType enemyCheckerType = grid[enemyPosition.getRow()][enemyPosition.getCol()];
                CheckerType landingCheckerType = grid[landingPosition.getRow()][landingPosition.getCol()];

                if (isEnemy(allyCheckerType, enemyCheckerType) && landingCheckerType == CheckerType.NONE && !capturedPositions.contains(enemyPosition)) {
                    foundFurtherCapture = true;
                    List<Position> newLandingPositions = new ArrayList<>(landingPositions);
                    newLandingPositions.add(landingPosition);
                    List<Position> newCapturedPositions = new ArrayList<>(capturedPositions);
                    newCapturedPositions.add(enemyPosition);
                    getManCaptures(startPosition, landingPosition, newLandingPositions, newCapturedPositions, availableMoves);
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

    private void getKingCaptures(Position startPosition, Position currentPosition, List<Position> landingPositions, List<Position> capturedPositions, List<CheckerMove> availableMoves) {
        boolean foundFurtherCapture = false;

        for (int[] direction : DIRECTIONS) {
            boolean foundEnemy = false;

            int rowDirection = direction[0];
            int colDirection = direction[1];

            Position enemyPosition = new Position(currentPosition.getRow() + rowDirection, currentPosition.getCol() + colDirection);

            while (isValidPosition(enemyPosition)) {
                CheckerType allyCheckerType = grid[startPosition.getRow()][startPosition.getCol()];
                CheckerType enemyCheckerType = grid[enemyPosition.getRow()][enemyPosition.getCol()];

                if (enemyCheckerType != CheckerType.NONE) {
                    if (isEnemy(allyCheckerType, enemyCheckerType) && !capturedPositions.contains(enemyPosition)) {
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

                        getKingCaptures(startPosition, landingPosition, newLandingPositions, newCapturedPositions, availableMoves);
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