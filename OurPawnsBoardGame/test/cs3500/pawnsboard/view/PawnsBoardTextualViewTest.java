package cs3500.pawnsboard.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import cs3500.pawnsboard.model.Board;
import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.Cell;
import cs3500.pawnsboard.model.PawnsBoardModel;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests for the PawnsBoardTextualView class.
 */
public class PawnsBoardTextualViewTest {
  private PawnsBoardModel model;
  private PawnsBoardTextualView view;
  private ByteArrayOutputStream outContent;
  private PrintStream originalOut;

  /**
   * Sets up the test environment before each test.
   */
  @Before
  public void setUp() {
    // Create a test deck
    List<Card> testDeck = createTestDeck();

    // Create a default model with a 3x5 board
    model = new PawnsBoardModel(3, 5, testDeck.size(), 5);

    // Create the view
    view = new PawnsBoardTextualView(model);

    // Set up output capture
    outContent = new ByteArrayOutputStream();
    originalOut = System.out;
    System.setOut(new PrintStream(outContent));
  }

  /**
   * Creates a test deck with some cards.
   * @return A list of cards for testing
   */
  private List<Card> createTestDeck() {
    List<Card> deck = new ArrayList<>();

    // Create a simple 5x5 influence grid for test cards
    char[][] grid1 = {
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'I', 'I', 'I', 'X'},
            {'X', 'I', 'C', 'I', 'X'},
            {'X', 'I', 'I', 'I', 'X'},
            {'X', 'X', 'X', 'X', 'X'}
    };

    char[][] grid2 = {
            {'X', 'X', 'I', 'X', 'X'},
            {'X', 'X', 'I', 'X', 'X'},
            {'I', 'I', 'C', 'I', 'I'},
            {'X', 'X', 'I', 'X', 'X'},
            {'X', 'X', 'I', 'X', 'X'}
    };

    deck.add(new Card("Card1", 1, 1, grid1));
    deck.add(new Card("Card2", 2, 2, grid2));
    deck.add(new Card("Card3", 3, 3, grid1));
    deck.add(new Card("Card4", 1, 4, grid2));
    deck.add(new Card("Card5", 2, 5, grid1));
    deck.add(new Card("Card6", 3, 6, grid2));
    deck.add(new Card("Card7", 1, 7, grid1));
    deck.add(new Card("Card8", 2, 8, grid2));
    deck.add(new Card("Card9", 3, 9, grid1));
    deck.add(new Card("Card10", 1, 10, grid2));
    // Add more cards to ensure we have enough for both players and for placing on board
    for (int i = 0; i < 10; i++) {
      deck.add(new Card("ExtraCard" + i, 1, i + 1, i % 2 == 0 ? grid1 : grid2));
    }

    return deck;
  }

  /**
   * Restore the original System.out after each test.
   */
  @org.junit.After
  public void restoreStreams() {
    System.setOut(originalOut);
  }

  /**
   * Test the constructor with a null model.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithNullModel() {
    new PawnsBoardTextualView(null);
  }

  /**
   * Test rendering the initial board state.
   */
  @Test
  public void testRenderInitialBoard() {
    view.render(model.getBoard());
    String output = outContent.toString();

    String expected = "Board state with row scores:\n" +
            "0 1___1 0\n" +
            "0 1___1 0\n" +
            "0 1___1 0\n\n";

    assertEquals(expected, output);
  }

  /**
   * Test rendering a board with placed cards.
   */
  @Test
  public void testRenderBoardWithCards() {
    // Manually place cards on the board instead of using placeCard method
    Board board = model.getBoard();

    // Get the first card from the deck to place
    Card redCard = new Card("Card1", 1, 1, new char[5][5]);
    Card blueCard = new Card("Card1", 1, 1, new char[5][5]);

    // Place cards manually
    board.getCell(0, 0).placeCard(redCard, "red");
    board.getCell(0, 4).placeCard(blueCard, "blue");

    view.render(board);
    String output = outContent.toString();

    // The expected output should show R at (0,0) and B at (0,4)
    String expected = "Board state with row scores:\n" +
            "1 R___B 1\n" +
            "0 1___1 0\n" +
            "0 1___1 0\n\n";

    assertEquals(expected, output);
  }

  /**
   * Test rendering a board with various pawn counts.
   */
  @Test
  public void testRenderBoardWithVariousPawnCounts() {
    Board board = model.getBoard();

    // Modify some pawn counts
    board.setCellPawns(1, 1, 2, "red");
    board.setCellPawns(1, 3, 3, "blue");

    view.render(board);
    String output = outContent.toString();

    String expected = "Board state with row scores:\n" +
            "0 1___1 0\n" +
            "0 12_31 0\n" +
            "0 1___1 0\n\n";

    assertEquals(expected, output);
  }

  /**
   * Test rendering a board with a mix of cards and pawns.
   */
  @Test
  public void testRenderBoardWithMixedCardsAndPawns() {
    // Set up a more complex board state
    Board board = model.getBoard();

    // Manually place cards
    Card redCard = new Card("Card1", 1, 1, new char[5][5]);
    Card blueCard = new Card("Card1", 1, 1, new char[5][5]);

    board.getCell(0, 0).placeCard(redCard, "red");
    board.getCell(2, 4).placeCard(blueCard, "blue");

    // Set some pawn configurations
    board.setCellPawns(1, 1, 2, "red");
    board.setCellPawns(1, 3, 3, "blue");

    view.render(board);
    String output = outContent.toString();

    String expected = "Board state with row scores:\n" +
            "1 R___1 0\n" +
            "0 12_31 0\n" +
            "0 1___B 1\n\n";

    assertEquals(expected, output);
  }

  /**
   * Test rendering an empty board (no pawns or cards).
   */
  @Test
  public void testRenderEmptyBoard() {
    // Create a new empty board
    Board emptyBoard = new Board(3, 5);

    view.render(emptyBoard);
    String output = outContent.toString();

    // Since the board is empty, all cells should be represented with '_'
    String expected = "Board state with row scores:\n" +
            "0 _____ 0\n" +
            "0 _____ 0\n" +
            "0 _____ 0\n\n";

    assertEquals(expected, output);
  }

  /**
   * Test rendering a board with all cells occupied.
   */
  @Test
  public void testRenderFullyOccupiedBoard() {
    // Create a new board
    Board board = model.getBoard();

    // Make moves to fill the board with cards
    // First, setup pawns for all cells
    for (int i = 0; i < board.getRows(); i++) {
      for (int j = 1; j < board.getColumns() - 1; j++) {
        if (j % 2 == 0) {
          board.setCellPawns(i, j, 1, "red");
        } else {
          board.setCellPawns(i, j, 1, "blue");
        }
      }
    }

    // Now place cards manually
    for (int i = 0; i < board.getRows(); i++) {
      for (int j = 0; j < board.getColumns(); j++) {
        // Alternate between red and blue cards
        String owner = (i + j) % 2 == 0 ? "red" : "blue";
        int value = (i * board.getColumns() + j) % 10 + 1; // Values from 1-10
        Card card = new Card("Card" + value, 1, value, new char[5][5]);
        board.getCell(i, j).placeCard(card, owner);
      }
    }

    // Now render the board
    view.render(board);
    String output = outContent.toString();

    // Because we've placed cards manually, we need to check the general format
    String[] lines = output.split("\n");
    assertEquals("Board state with row scores:", lines[0]);
    assertEquals(4, lines.length); // Header + 3 rows

    for (int i = 1; i <= 3; i++) {
      // Ensure each row has the score-board-score format
      String[] parts = lines[i].split(" ");
      assertEquals(3, parts.length);

      // Check board representation part
      assertEquals(5, parts[1].length()); // 5 columns
      for (char c : parts[1].toCharArray()) {
        // Each cell should be R or B (no empty cells)
        assertTrue("Each cell should be R or B: " + c, c == 'R' || c == 'B');
      }
    }
  }

  /**
   * Test rendering after a game is over.
   */
  @Test
  public void testRenderAfterGameOver() {
    // Make the game end by having both players pass
    model.pass();
    model.pass();

    // Make sure the game is over
    assertTrue("Game should be over after consecutive passes", model.isGameOver());

    // Render the board
    view.render(model.getBoard());
    String output = outContent.toString();

    // The board should still be rendered correctly
    String expected = "Board state with row scores:\n" +
            "0 1___1 0\n" +
            "0 1___1 0\n" +
            "0 1___1 0\n\n";

    assertEquals(expected, output);
  }
}