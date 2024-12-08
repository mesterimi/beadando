package hu.nye;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

class Connect4GameTest {

    private Connect4Game game;
    private Player player1;
    private Player player2;




    @BeforeEach
    public void setUp() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("highscores.txt"));
        writer.write("Player: 10\nAnotherPlayer: 5\n");
        writer.close();
        game = new Connect4Game(new Player("Player1", 'Y'), new Player("Computer", 'R'));
        player1 = new Player("Player1", 'Y');
        player2 = new Player("Computer", 'R');
    }

    @Test
    void testCheckWinHorizontal() {
        GameBoard board = game.getGameBoard();
        char[][] grid = board.getBoard();

        grid[0][0] = 'Y';
        grid[0][1] = 'Y';
        grid[0][2] = 'Y';
        grid[0][3] = 'Y';

        assertTrue(game.checkWin(), "Horizontal win should be detected.");
    }

    @Test
    void testCheckWinVertical() {
        GameBoard board = game.getGameBoard();
        char[][] grid = board.getBoard();

        grid[0][0] = 'Y';
        grid[1][0] = 'Y';
        grid[2][0] = 'Y';
        grid[3][0] = 'Y';

        assertTrue(game.checkWin(), "Vertical win should be detected.");
    }

    @Test
    void testCheckWinDiagonalBottomLeftToTopRight() {
        GameBoard board = game.getGameBoard();
        char[][] grid = board.getBoard();

        grid[3][0] = 'Y';
        grid[2][1] = 'Y';
        grid[1][2] = 'Y';
        grid[0][3] = 'Y';

        assertTrue(game.checkWin(), "Diagonal win (bottom-left to top-right) should be detected.");
    }

    @Test
    void testCheckWinDiagonalTopLeftToBottomRight() {
        GameBoard board = game.getGameBoard();
        char[][] grid = board.getBoard();

        grid[0][0] = 'Y';
        grid[1][1] = 'Y';
        grid[2][2] = 'Y';
        grid[3][3] = 'Y';

        assertTrue(game.checkWin(), "Diagonal win (top-left to bottom-right) should be detected.");
    }

    @Test
    void testCheckDraw() {
        GameBoard board = game.getGameBoard();
        char[][] grid = board.getBoard();

        for (int row = 0; row < GameBoard.ROWS; row++) {
            for (int col = 0; col < GameBoard.COLS; col++) {
                grid[row][col] = (row + col) % 2 == 0 ? 'Y' : 'R';
            }
        }

        assertTrue(game.checkDraw(), "The game should be a draw when the board is full and no one has won.");
    }

    @Test
    void testSaveAndLoadGameBoard() throws IOException {
        String filePath = "test_game_board.txt";
        GameBoard board = game.getGameBoard();
        char[][] grid = board.getBoard();

        grid[0][0] = 'Y';
        grid[1][1] = 'R';
        game.saveGameBoard(filePath);

        Connect4Game loadedGame = new Connect4Game(player1, player2);
        loadedGame.loadGameBoard(filePath);

        char[][] loadedGrid = loadedGame.getGameBoard().getBoard();
        assertEquals('Y', loadedGrid[0][0]);
        assertEquals('R', loadedGrid[1][1]);
        assertEquals('.', loadedGrid[2][2]);

        new File(filePath).delete();
    }

    @Test
    public void testUpdateHighScores() throws IOException {
        Connect4Game gameSpy = Mockito.spy(game);
        Map<String, Integer> scores = new HashMap<>();
        scores.put("Player", 2);
        doReturn(scores).when(gameSpy).loadHighScores();

        gameSpy.updateHighScores("Player");
        verify(gameSpy).saveHighScores(anyMap());
        Assertions.assertEquals(3, scores.get("Player")); // Updated count
    }

    @Test
    void testGenerateComputerMove() {
        int col = game.generateComputerMove();
        assertTrue(col >= 0 && col < GameBoard.COLS, "Computer move should be within valid column range.");
    }

    @Test
    public void testSwitchPlayer() {
        Assertions.assertEquals(player1, game.getCurrentPlayer());

        game.switchPlayer();
        Assertions.assertEquals(player2, game.getCurrentPlayer());

        game.switchPlayer();
        Assertions.assertEquals(player1, game.getCurrentPlayer());
    }

    @Test
    public void testShowHighScores() throws IOException {
        File file = new File("highscores.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Player: 5\n");
            writer.write("Computer: 3\n");
        }
        game.showHighScores();
    }

    @Test
    public void testLoadGameBoardFileNotFound() {
        File file = new File("nonexistent_game_board.txt");

        assertThrows(IOException.class, () -> game.loadGameBoard(file.getPath()), "IOException should be thrown when the file does not exist.");
    }

    @Test
    public void testShowHighScoresEmptyFile() throws IOException {
        File file = new File("empty_highscores.txt");
        file.deleteOnExit();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
        }

        game.showHighScores();
    }

    @Test
    public void testCheckDrawWhenBoardIsFull() {
        GameBoard board = game.getGameBoard();

        for (int col = 0; col < GameBoard.COLS; col++) {
            for (int row = 0; row < GameBoard.ROWS; row++) {
                board.playMove(col, (col + row) % 2 == 0 ? 'Y' : 'R');
            }
        }

        assertTrue(game.checkDraw(), "The game should end in a draw when the board is full and no player has won.");
    }

    @Test
    public void testLoadHighScores() {
        Map<String, Integer> highScores = game.loadHighScores();
        assertNotNull(highScores);
        assertEquals(10, highScores.get("Player"));
        assertEquals(5, highScores.get("AnotherPlayer"));
    }

    @AfterEach
    public void tearDown() {
        File file = new File("highscores.txt");
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testCheckWinAfterMove() {
        GameBoard board = game.getGameBoard();
        char[][] grid = board.getBoard();

        grid[0][0] = 'Y';
        grid[0][1] = 'Y';
        grid[0][2] = 'Y';
        grid[0][3] = 'Y';

        assertTrue(game.checkWin(), "A win should be detected after a valid move.");
    }

    @Test
    void testSaveHighScoresAfterGameEnds() throws IOException {
        Connect4Game gameSpy = Mockito.spy(game);
        Map<String, Integer> highScores = new HashMap<>();
        highScores.put("Player1", 10);
        doReturn(highScores).when(gameSpy).loadHighScores();

        gameSpy.updateHighScores("Player1");

        verify(gameSpy).saveHighScores(anyMap());
        assertEquals(11, highScores.get("Player1"), "High score should be updated correctly.");
    }

    @Test
    void testHighScoresAreSorted() throws IOException {
        Connect4Game gameSpy = Mockito.spy(game);
        Map<String, Integer> scores = new HashMap<>();
        scores.put("Player1", 12);
        scores.put("Player2", 15);
        doReturn(scores).when(gameSpy).loadHighScores();

        gameSpy.updateHighScores("Player1");
        gameSpy.updateHighScores("Player2");

        assertTrue(scores.get("Player2") > scores.get("Player1"), "High scores should be sorted from highest to lowest.");
    }


}

