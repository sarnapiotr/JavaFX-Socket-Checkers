package pl.edu.pk.checkers.common;

import java.util.ArrayList;

public class Board {
    private Piece[][] grid;
    private static final int SIZE = 8;

    public Board() {
        grid = new Piece[SIZE][SIZE];
        initializeBoard();
    }

    public Piece getPiece(Position position) {
        if (isValidCoordinate(position)) return grid[position.getRow()][position.getCol()];
        return null;
    }

    private void initializeBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                grid[row][col] = Piece.EMPTY;

                if ((row + col) % 2 != 0) {
                    if (row < 3)
                        grid[row][col] = Piece.BLACK;
                    else if (row > 4)
                        grid[row][col] = Piece.WHITE;
                }
            }
        }
    }

    private boolean isValidCoordinate(Position position) {
        return position.getRow() >= 0 && position.getRow() < SIZE && position.getCol() >= 0 && position.getCol() < SIZE;
    }

    public ArrayList<PieceMove> getAvilableMoves(Position startPosition) {
        

        return new ArrayList<PieceMove>();
    }

    public void executeMove(Position startPosition, PieceMove pieceMove) {
        if (grid[startPosition.getRow()][startPosition.getCol()] == Piece.WHITE) {
            if (pieceMove.getEndPosition().getRow() == 0) {
                grid[pieceMove.getEndPosition().getRow()][pieceMove.getEndPosition().getCol()] = Piece.WHITE_KING;
                grid[startPosition.getRow()][startPosition.getCol()] = Piece.EMPTY;
            } else {
                grid[pieceMove.getEndPosition().getRow()][pieceMove.getEndPosition().getCol()] = Piece.WHITE;
                grid[startPosition.getRow()][startPosition.getCol()] = Piece.EMPTY;
            }
        } else if (grid[startPosition.getRow()][startPosition.getCol()] == Piece.BLACK) {
            if (pieceMove.getEndPosition().getRow() == SIZE-1) {
                grid[pieceMove.getEndPosition().getRow()][pieceMove.getEndPosition().getCol()] = Piece.BLACK_KING;
                grid[startPosition.getRow()][startPosition.getCol()] = Piece.EMPTY;
            } else {
                grid[pieceMove.getEndPosition().getRow()][pieceMove.getEndPosition().getCol()] = Piece.BLACK;
                grid[startPosition.getRow()][startPosition.getCol()] = Piece.EMPTY;
            }
        } else {
            grid[pieceMove.getEndPosition().getRow()][pieceMove.getEndPosition().getCol()] = grid[startPosition.getRow()][startPosition.getCol()];
            grid[startPosition.getRow()][startPosition.getCol()] = Piece.EMPTY;
        }
        
        for (Position position : pieceMove.getCapturedPieces()) {
            grid[position.getRow()][position.getCol()] = Piece.EMPTY;
        }
    }

    @Override
    public String toString() {
        String str = "";
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                switch (grid[row][col]) {
                    case Piece.EMPTY:
                        str += "E  ";
                        break;
                    case Piece.WHITE:
                        str += "W  ";
                        break;
                    case Piece.BLACK:
                        str += "B  ";
                        break;
                    case Piece.WHITE_KING:
                        str += "WK ";
                        break;
                    case Piece.BLACK_KING:
                        str += "BK ";
                        break;
                }
            }
            str += "\n";
        }

        return str;
    }
}
