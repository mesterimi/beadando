package hu.nye;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;


public class Connect4Game {
    private final Player player1;

    private final Player player2;

    private Player currentPlayer;

    private final GameBoard gameBoard;

    private static final String HIGH_SCORE_FILE = "highscores.txt";

    public Connect4Game(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = player1;
        this.gameBoard = new GameBoard();
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        String filePath = "gameboard.txt";

        while (true) {
            displayBoard();
            System.out.println("Press 0 to save the game, h to view high scores, q to quit, "
                    + "or choose a column (a,b,c,d,e,f,g): ");

            if (currentPlayer.equals(player1)) {
                String input = scanner.nextLine();
                switch (input) {
                    case "0" -> {
                        try {
                            saveGameBoard(filePath);
                            System.out.println("Game saved successfully.");
                        }
                        catch (IOException e) {
                            System.out.println("Failed to save the game.");
                        }
                        continue;
                    }
                    case "q" -> {
                        System.out.println("Goodbye!");
                        System.exit(0);
                    }
                    case "h" -> {
                        try {
                            System.out.println("Showing highscores:");
                            showHighScores();
                            System.exit(0);
                        }
                        catch (IOException e) {
                            System.out.println("Error loading highscores.");
                        }
                        continue;
                    }
                }

                int col = input.charAt(0) - 'a';
                if (!gameBoard.playMove(col, currentPlayer.getToken())) {
                    System.out.println("Invalid move. Try again.");
                    continue;
                }
            }
            else {

                int col = generateComputerMove();
                if (gameBoard.playMove(col, currentPlayer.getToken())) {
                    System.out.println("Computer plays in column: " + (char) ('a' + col));
                }
                else {
                    System.out.println("Computer tried an invalid move. Re-trying...");
                    continue;
                }
            }

            if (checkWin()) {
                displayBoard();
                System.out.println(currentPlayer.getName() + " wins!");

                updateHighScores(currentPlayer.getName());

                try {
                    clearGameBoardFile(filePath);
                    System.out.println("Game board file cleared.");
                }
                catch (IOException e) {
                    System.out.println("Failed to clear the game board file.");
                }
                break;
            }

            if (checkDraw()) {
                displayBoard();
                System.out.println("It's a draw!");
                break;
            }

            switchPlayer();
        }
    }

    void clearGameBoardFile(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 0; i < GameBoard.ROWS; i++) {
                for (int j = 0; j < GameBoard.COLS; j++) {
                    writer.write('.');  // Write empty cell
                }
                writer.newLine();
            }
            writer.flush();
        }
        catch (IOException e) {
            System.out.println("Error clearing game board file: " + e.getMessage());
            throw e;
        }
    }

    public void switchPlayer() {
        currentPlayer = (currentPlayer.equals(player1)) ? player2 : player1;
    }

    public boolean checkWin() {
        char[][] board = gameBoard.getBoard();
        char token = currentPlayer.getToken();

        for (int row = 0; row < GameBoard.ROWS; row++) {
            for (int col = 0; col < GameBoard.COLS - 3; col++) {
                if (board[row][col] == token &&
                        board[row][col + 1] == token &&
                        board[row][col + 2] == token &&
                        board[row][col + 3] == token) {
                    return true;
                }
            }
        }

        for (int col = 0; col < GameBoard.COLS; col++) {
            for (int row = 0; row < GameBoard.ROWS - 3; row++) {
                if (board[row][col] == token &&
                        board[row + 1][col] == token &&
                        board[row + 2][col] == token &&
                        board[row + 3][col] == token) {
                    return true;
                }
            }
        }

        for (int row = 3; row < GameBoard.ROWS; row++) {
            for (int col = 0; col < GameBoard.COLS - 3; col++) {
                if (board[row][col] == token &&
                        board[row - 1][col + 1] == token &&
                        board[row - 2][col + 2] == token &&
                        board[row - 3][col + 3] == token) {
                    return true;
                }
            }
        }

        for (int row = 0; row < GameBoard.ROWS - 3; row++) {
            for (int col = 0; col < GameBoard.COLS - 3; col++) {
                if (board[row][col] == token &&
                        board[row + 1][col + 1] == token &&
                        board[row + 2][col + 2] == token &&
                        board[row + 3][col + 3] == token) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean checkDraw() {
        char[][] board = gameBoard.getBoard();
        for (int col = 0; col < GameBoard.COLS; col++) {
            if (board[0][col] == '.') {
                return false;
            }
        }
        return true;
    }

    public int generateComputerMove() {
        Random rand = new Random();
        return rand.nextInt(GameBoard.COLS);
    }

    void displayBoard() {
        char[][] board = gameBoard.getBoard();
        for (int i = 0; i < GameBoard.ROWS; i++) {
            for (int j = 0; j < GameBoard.COLS; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void loadGameBoard(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        for (int i = 0; i < gameBoard.getBoard().length; i++) {
            String line = reader.readLine();
            for (int j = 0; j < gameBoard.getBoard()[i].length; j++) {
                gameBoard.getBoard()[i][j] = line.charAt(j);
            }
        }
        reader.close();
    }

    public void saveGameBoard(String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        for (char[] row : gameBoard.getBoard()) {
            for (char cell : row) {
                writer.write(cell);
            }
            writer.newLine();
        }
        writer.close();
    }

    public void updateHighScores(String winnerName) {
        Map<String, Integer> scores = loadHighScores();
        scores.put(winnerName, scores.getOrDefault(winnerName, 0) + 1);
        saveHighScores(scores);
    }

    public Map<String, Integer> loadHighScores() {
        Map<String, Integer> scores = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGH_SCORE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String name = parts[0].trim();
                    int count = Integer.parseInt(parts[1].trim());
                    scores.put(name, count);
                }
            }
        }
        catch (IOException e) {
            System.out.println("Could not load high scores: " + e.getMessage());
        }
        return scores;
    }

    public void saveHighScores(Map<String, Integer> scores) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGH_SCORE_FILE))) {
            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue());
                writer.newLine();
            }
        }
        catch (IOException e) {
            System.out.println("Could not save high scores: " + e.getMessage());
        }
    }

    void showHighScores() throws IOException {
        File file = new File(HIGH_SCORE_FILE);
        if (!file.exists()) {
            System.out.println("No high scores available.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(HIGH_SCORE_FILE))) {
            String line;
            System.out.println("High Scores:");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
        catch (IOException e) {
            System.out.println("Error reading high scores: " + e.getMessage());
            throw e;
        }
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter player 1 name: ");
        String player1Name = scanner.nextLine();
        Player player1 = new Player(player1Name, 'Y');
        Player player2 = new Player("Computer", 'R');

        Connect4Game game = new Connect4Game(player1, player2);
        String filePath = "game_board.txt";
        File file = new File(filePath);
        if (file.exists()) {
            game.loadGameBoard(filePath);
        }
        game.start();
    }
}
