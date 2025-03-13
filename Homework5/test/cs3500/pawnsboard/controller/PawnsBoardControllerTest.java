package cs3500.pawnsboard.controller;

import static org.junit.Assert.assertTrue;

import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.PawnsBoardModel;
import cs3500.pawnsboard.view.PawnsBoardTextualView;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests for the PawnsBoardController class.
 */
public class PawnsBoardControllerTest {
  private PawnsBoardModel model;
  private PawnsBoardTextualView view;
  private ByteArrayOutputStream outContent;
  private PrintStream originalOut;
  private InputStream originalIn;
  private List<Card> testDeck;

  /**
   * Sets up the test environment before each test.
   */
  @Before
  public void setUp() {
    // Create a test deck
    testDeck = createTestDeck();

    // Create a default model with a 3x5 board
    model = new PawnsBoardModel(3, 5, testDeck, 3);

    // Create the view
    view = new PawnsBoardTextualView(model);

    // Set up output capture
    outContent = new ByteArrayOutputStream();
    originalOut = System.out;
    originalIn = System.in;
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
    deck.add(new Card("Card2", 1, 2, grid2));
    deck.add(new Card("Card3", 1, 3, grid1));
    deck.add(new Card("Card4", 1, 4, grid2));
    deck.add(new Card("Card5", 1, 5, grid1));
    deck.add(new Card("Card6", 1, 6, grid2));

    return deck;
  }

  /**
   * Restore the original System.out and System.in after each test.
   */
  @org.junit.After
  public void restoreStreams() {
    System.setOut(originalOut);
    System.setIn(originalIn);
  }

  /**
   * Test a simple game with a valid move.
   */
  @Test
  public void testValidMove() {
    // Simulate user input: place card 0 at position (0,0)
    String input = "place 0 0 0\npass\npass\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    PawnsBoardController controller = new PawnsBoardController(model, view);
    controller.startGame();

    String output = outContent.toString();

    // Check that the move was successful
    assertTrue(output.contains("Red plays Card1 at (0,0)"));

    // Check final scores are displayed
    assertTrue(output.contains("Final Scores:"));
    assertTrue(output.contains("Red: 1"));
    assertTrue(output.contains("Blue: 0"));
    assertTrue(output.contains("Red wins!"));
  }

  /**
   * Test passing turns.
   */
  @Test
  public void testPassingTurns() {
    // Simulate user input: both players pass
    String input = "pass\npass\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    PawnsBoardController controller = new PawnsBoardController(model, view);
    controller.startGame();

    String output = outContent.toString();

    // Check that both passes were registered
    assertTrue(output.contains("Red passes."));
    assertTrue(output.contains("Blue passes."));

    // Check game over state
    assertTrue(output.contains("Game over."));
    assertTrue(output.contains("Final Scores:"));
    assertTrue(output.contains("Red: 0"));
    assertTrue(output.contains("Blue: 0"));
    assertTrue(output.contains("It's a tie!"));
  }

  /**
   * Test invalid move format.
   */
  @Test
  public void testInvalidMoveFormat() {
    // Simulate user input: invalid command format followed by valid commands
    String input = "place 0 0\nplace 0 0 0\npass\npass\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    PawnsBoardController controller = new PawnsBoardController(model, view);
    controller.startGame();

    String output = outContent.toString();

    // Check that the invalid command was rejected
    assertTrue(output.contains("Invalid input format. Use: place cardIndex row col"));

    // Check that the game still proceeded with the valid move
    assertTrue(output.contains("Red plays Card1 at (0,0)"));
  }

  /**
   * Test invalid card index.
   */
  @Test
  public void testInvalidCardIndex() {
    // Simulate user input: invalid card index followed by valid commands
    String input = "place 10 0 0\nplace 0 0 0\npass\npass\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    PawnsBoardController controller = new PawnsBoardController(model, view);
    controller.startGame();

    String output = outContent.toString();

    // Check that the invalid card index was rejected
    assertTrue(output.contains("Illegal move."));

    // Check that the game still proceeded with the valid move
    assertTrue(output.contains("Red plays Card1 at (0,0)"));
  }

  /**
   * Test invalid position.
   */
  @Test
  public void testInvalidPosition() {
    // Simulate user input: invalid board position followed by valid commands
    String input = "place 0 10 10\nplace 0 0 0\npass\npass\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    PawnsBoardController controller = new PawnsBoardController(model, view);
    controller.startGame();

    String output = outContent.toString();

    // Check that the invalid position was rejected
    assertTrue(output.contains("Invalid cell position.") ||
            output.contains("Index out of bounds") ||
            output.contains("Illegal move"));

    // Check that the game still proceeded with the valid move
    assertTrue(output.contains("Red plays Card1 at (0,0)"));
  }

  /**
   * Test invalid command.
   */
  @Test
  public void testInvalidCommand() {
    // Simulate user input: invalid command followed by valid commands
    String input = "invalid command\nplace 0 0 0\npass\npass\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    PawnsBoardController controller = new PawnsBoardController(model, view);
    controller.startGame();

    String output = outContent.toString();

    // Check that the invalid command was rejected
    assertTrue(output.contains("Invalid command. Try again."));

    // Check that the game still proceeded with the valid move
    assertTrue(output.contains("Red plays Card1 at (0,0)"));
  }

  /**
   * Test placing on a cell that doesn't belong to the current player.
   */
  @Test
  public void testInvalidCellOwner() {
    // Simulate user input: attempt to place on opponent's cell
    String input = "place 0 0 4\nplace 0 0 0\npass\npass\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    PawnsBoardController controller = new PawnsBoardController(model, view);
    controller.startGame();

    String output = outContent.toString();

    // Check that the move was rejected (either because the cell is not owned by the player
    // or because it's not in the list of legal moves)
    assertTrue(output.contains("Cell is not eligible for placement.") ||
            output.contains("Illegal move"));

    // Check that the game still proceeded with the valid move
    assertTrue(output.contains("Red plays Card1 at (0,0)"));
  }

  /**
   * Test auto-passing when there are no legal moves.
   */
  @Test
  public void testAutoPassNoLegalMoves() {
    // Create a custom model where we can manipulate the board state
    PawnsBoardModel customModel = new PawnsBoardModel(3, 5, testDeck, 0);
    PawnsBoardTextualView customView = new PawnsBoardTextualView(customModel);

    // Since hand size is 0, there should be no legal moves
    String input = ""; // No input needed as auto-pass should happen
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    PawnsBoardController controller = new PawnsBoardController(customModel, customView);
    controller.startGame();

    String output = outContent.toString();

    // Check that auto-pass happened for both players
    assertTrue(output.contains("Red has no cards left. Automatically passing."));
    assertTrue(output.contains("Blue has no cards left. Automatically passing."));

    // Check game over state
    assertTrue(output.contains("Game over."));
  }

  /**
   * Test a complete game with multiple moves.
   */
  @Test
  public void testCompleteGame() {
    // Simulate a complete game with multiple moves
    String input = "place 0 0 0\nplace 0 2 4\nplace 1 0 2\nplace 1 2 0\npass\npass\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    PawnsBoardController controller = new PawnsBoardController(model, view);
    controller.startGame();

    String output = outContent.toString();

    // Check that all moves were processed
    assertTrue(output.contains("Red plays Card1 at (0,0)"));
    assertTrue(output.contains("Blue plays")); // Blue's card placement
    assertTrue(output.contains("Red plays Card")); // Red's second card placement
    assertTrue(output.contains("Blue plays")); // Blue's second card placement

    // Check for passes
    assertTrue(output.contains("Red passes."));
    assertTrue(output.contains("Blue passes."));

    // Check game over state
    assertTrue(output.contains("Game over."));
    assertTrue(output.contains("Final Scores:"));
  }

  /**
   * Test handling non-integer inputs.
   */
  @Test
  public void testNonIntegerInputs() {
    // Simulate user input with non-integer values followed by valid commands
    String input = "place a b c\nplace 0 0 0\npass\npass\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    PawnsBoardController controller = new PawnsBoardController(model, view);
    controller.startGame();

    String output = outContent.toString();

    // Check that the invalid input was rejected
    assertTrue(output.contains("Invalid number format") ||
            output.contains("NumberFormatException"));

    // Check that the game still proceeded with the valid move
    assertTrue(output.contains("Red plays Card1 at (0,0)"));
  }

  /**
   * Test displaying legal moves when an illegal move is attempted.
   */
  @Test
  public void testDisplayLegalMoves() {
    // Create a board state where there's a limited set of legal moves
    // Then attempt an illegal move to trigger display of legal moves

    // Assuming initial board setup has legal moves at (0,0) for first player
    String input = "place 0 0 1\nplace 0 0 0\npass\npass\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    PawnsBoardController controller = new PawnsBoardController(model, view);
    controller.startGame();

    String output = outContent.toString();

    // Check that the illegal move was rejected and legal moves were displayed
    assertTrue(output.contains("Illegal move. Legal moves are:"));

    // Check that at least one legal move is shown
    assertTrue(output.contains("Place card index"));

    // Check that the game still proceeded with the valid move
    assertTrue(output.contains("Red plays Card1 at (0,0)"));
  }
}