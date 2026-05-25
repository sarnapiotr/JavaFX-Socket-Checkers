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
            findSimpleCaptures(startPosition, checkerType, new ArrayList<>(), availableMoves);
            if (availableMoves.isEmpty())
                findSimpleMoves(startPosition, checkerType, availableMoves);

        } else if (checkerType == CheckerType.WHITE_KING || checkerType == CheckerType.BLACK_KING) { // King checkers
            findKingCaptures(startPosition, checkerType, new ArrayList<>(), availableMoves);
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

    private void findSimpleCaptures(Position currentPosition, CheckerType checkerType, List<Position> capturedCheckers, List<CheckerMove> availableMoves) {
        boolean foundFurtherJump = false;

        for (int[] direction : DIRECTIONS) {
            int rowDirection = direction[0];
            int colDirection = direction[1];

            Position enemyPosition = new Position(currentPosition.getRow() + rowDirection, currentPosition.getCol() + colDirection);
            Position landingPosition = new Position(currentPosition.getRow() + 2 * rowDirection, currentPosition.getCol() + 2 * colDirection);

            if (isValidPosition(landingPosition)) {
                CheckerType enemyCheckerType = grid[enemyPosition.getRow()][enemyPosition.getCol()];
                CheckerType landingCheckerType = grid[landingPosition.getRow()][landingPosition.getCol()];

                if (isEnemy(checkerType, enemyCheckerType) && landingCheckerType == CheckerType.NONE && !capturedCheckers.contains(enemyPosition)) {
                    foundFurtherJump = true;
                    List<Position> newCapturedCheckers = new ArrayList<>(capturedCheckers);
                    newCapturedCheckers.add(enemyPosition);
                    findSimpleCaptures(landingPosition, checkerType, newCapturedCheckers, availableMoves);
                }
            }
        }

        if (!foundFurtherJump && !capturedCheckers.isEmpty()) {
            CheckerMove checkerMove = new CheckerMove(currentPosition, capturedCheckers);

            if (!availableMoves.contains(checkerMove)) {
                availableMoves.add(checkerMove);
            }
        }
    }

    private void findKingCaptures(Position currentPosition, CheckerType checkerType, List<Position> capturedCheckers, List<CheckerMove> availableMoves) {
        boolean foundFurtherJump = false;

        for (int[] direction : DIRECTIONS) {
            int rowDirection = direction[0];
            int colDirection = direction[1];

            int enemyRow = currentPosition.getRow() + rowDirection;
            int enemyCol = currentPosition.getCol() + colDirection;

            Position enemyPosition = null;

            while (isValidPosition(new Position(enemyRow, enemyCol))) {
                CheckerType enemyChekcerType = grid[enemyRow][enemyCol];

                if (enemyChekcerType != CheckerType.NONE) {
                    if (isEnemy(checkerType, enemyChekcerType) && !capturedCheckers.contains(new Position(enemyRow, enemyCol))) {
                        enemyPosition = new Position(enemyRow, enemyCol);
                    }

                    break;
                }
                enemyRow += rowDirection;
                enemyCol += colDirection;
            }

            if (enemyPosition != null) {
                int landingRow = enemyPosition.getRow() + rowDirection;
                int landingCol = enemyPosition.getCol() + colDirection;

                while (isValidPosition(new Position(landingRow, landingCol))) {
                    if (grid[landingRow][landingCol] == CheckerType.NONE) {
                        foundFurtherJump = true;

                        List<Position> newCapturedCheckers = new ArrayList<>(capturedCheckers);
                        newCapturedCheckers.add(enemyPosition);

                        Position landingPosition = new Position(landingRow, landingCol);

                        findKingCaptures(landingPosition, checkerType, newCapturedCheckers, availableMoves);
                    } else {
                        break;
                    }

                    landingRow += rowDirection;
                    landingCol += colDirection;
                }
            }
        }

        if (!foundFurtherJump && !capturedCheckers.isEmpty()) {
            CheckerMove checkerMove = new CheckerMove(currentPosition, capturedCheckers);
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

            Position landingPosition = new Position(startPosition.getRow() + rowDirection, startPosition.getCol() + colDirection);

            if (isValidPosition(landingPosition) && grid[landingPosition.getRow()][landingPosition.getCol()] == CheckerType.NONE) {
                availableMoves.add(new CheckerMove(landingPosition, new ArrayList<>()));
            }
        }
    }

    private void findKingMoves(Position startPosition, List<CheckerMove> aviableMoves) {
        for (int[] direction : DIRECTIONS) {
            int rowDirection = direction[0];
            int colDirection = direction[1];

            int currentRow = startPosition.getRow() + rowDirection;
            int currentCol = startPosition.getCol() + colDirection;

            while (isValidPosition(new Position(currentRow, currentCol))) {
                if (grid[currentRow][currentCol] == CheckerType.NONE) {
                    aviableMoves.add(new CheckerMove(new Position(currentRow, currentCol), new ArrayList<>()));
                } else {
                    break;
                }

                currentRow += rowDirection;
                currentCol += colDirection;
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
                        findSimpleCaptures(new Position(row, col), checkerType, new ArrayList<>(), availableMoves);
                        if (!availableMoves.isEmpty()) {
                            return true;
                        }
                    } else if (isWhitePlayer && checkerType == CheckerType.WHITE_KING || !isWhitePlayer && checkerType == CheckerType.BLACK_KING) {
                        findKingCaptures(new Position(row, col), checkerType, new ArrayList<>(), availableMoves);
                        if (!availableMoves.isEmpty()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void executeMove(Position startPosition, CheckerMove checkerMove) {
        int startRow = startPosition.getRow();
        int startCol = startPosition.getCol();
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

        for (Position position : checkerMove.getCapturedCheckers()) {
            grid[position.getRow()][position.getCol()] = CheckerType.NONE;
        }
    }

    @Override
    public String toString() {
        String str = "";
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                switch (grid[row][col]) {
                    case CheckerType.NONE:
                        str += "N  ";
                        break;
                    case CheckerType.WHITE:
                        str += "W  ";
                        break;
                    case CheckerType.BLACK:
                        str += "B  ";
                        break;
                    case CheckerType.WHITE_KING:
                        str += "WK ";
                        break;
                    case CheckerType.BLACK_KING:
                        str += "BK ";
                        break;
                }
            }
            str += "\n";
        }

        return str;
    }
}