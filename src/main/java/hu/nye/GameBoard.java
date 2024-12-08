package hu.nye;

import java.util.Arrays;

public class GameBoard {
    static final int ROWS = 6;
    static final int COLS = 7;
    private final char[][] board;

    public GameBoard() {
        this.board = new char[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = '.';
            }
        }
    }

    public boolean playMove(int col, char token) {
        if (col < 0 || col >= COLS || board[0][col] != '.') {
            return false;
        }
        for (int i = ROWS - 1; i >= 0; i--) {
            if (board[i][col] == '.') {
                board[i][col] = token;
                return true;
            }
        }
        return false;
    }

    public char[][] getBoard() {
        return board.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameBoard gameBoard = (GameBoard) o;
        return Arrays.deepEquals(board, gameBoard.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("GameBoard {\n");
        for (char[] row : board) {
            sb.append(Arrays.toString(row)).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}