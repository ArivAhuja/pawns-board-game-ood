package cs3500.pawnsboard.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import cs3500.pawnsboard.model.Board;
import cs3500.pawnsboard.model.Card;
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
    model = new PawnsBoardModel(3, 5, testDeck, 5);

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
    // Place a card at (0,0) for Red player
    model.placeCard(0, 0, 0);  // Place the first card in hand at (0,0)

    // Switch to Blue's turn (happens automatically after Red's move)
    // and place a card
    model.placeCard(0, 4, 0);  // Place the first card in Blue's hand at (0,4)

    view.render(model.getBoard());
    String output = outContent.toString();

    // The expected output should show R at (0,0) and B at (0,4)
    // with appropriate row scores (Card1 for Red has value 1 and Card1 for Blue has value 1)
    String expected = "Red plays Card1 at (0,0).\n" +
            "Blue plays Card1 at (0,4).\n" +
            "Board state with row scores:\n" +
            "1 R1_1B 1\n" +
            "0 21_12 0\n" +
            "0 1___1 0" +
            "\n" +
            "\n";

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

    // Place some cards
    model.placeCard(0, 0, 0);  // Red places a card
    model.placeCard(2, 4, 0);  // Blue places a card

    // Set some pawn configurations
    board.setCellPawns(1, 1, 2, "red");
    board.setCellPawns(1, 3, 3, "blue");

    view.render(board);
    String output = outContent.toString();

    String expected = "Red plays Card1 at (0,0).\n" +
            "Blue plays Card1 at (2,4).\n" +
            "Board state with row scores:\n" +
            "1 R1__1 0\n" +
            "0 22_32 0\n" +
            "0 1__1B 1\n" +
            "\n";

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

    // Now place cards until the board is full or we run out of cards
    // This is a simplified approach that doesn't follow game rules exactly
    boolean isRedTurn = true;
    int redCardIndex = 0;
    int blueCardIndex = 0;

    for (int i = 0; i < board.getRows(); i++) {
      for (int j = 0; j < board.getColumns(); j++) {
        if (isRedTurn) {
          if (redCardIndex < model.getCurrentPlayer().getHand().size()) {
            board.getCell(i, j).placeCard(model.getCurrentPlayer().getHand().get(redCardIndex),
                    "red");
            redCardIndex++;
          }
        } else {
          if (blueCardIndex < model.getCurrentPlayer().getHand().size()) {
            board.getCell(i, j).placeCard(model.getCurrentPlayer().getHand().get(blueCardIndex),
                    "blue");
            blueCardIndex++;
          }
        }
        isRedTurn = !isRedTurn;
      }
    }

    // Now render the board
    view.render(board);
    String output = outContent.toString();

    // Because we've placed cards manually, we need to recompute the scores
    // For simplicity, we'll assume the expected pattern and check the general format
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
        // Each cell should be R, B, or a number (no empty cells)
        assertTrue("Each cell should be R, B, or a number", c == 'R' || c == 'B'
                || Character.isDigit(c));
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
    String expected = "Red passes.\n" +
            "Blue passes.\n" +
            "Board state with row scores:\n" +
            "0 1___1 0\n" +
            "0 1___1 0\n" +
            "0 1___1 0\n" +
            "\n";

    assertEquals(expected, output);
  }
}