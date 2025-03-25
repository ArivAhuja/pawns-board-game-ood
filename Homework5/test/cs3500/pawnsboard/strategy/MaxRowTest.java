package cs3500.pawnsboard.strategy;

import cs3500.pawnsboard.model.Board;
import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.Move;
import cs3500.pawnsboard.model.MockPawnsBoardModel;
import cs3500.pawnsboard.model.Player;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the MaxRowScore strategy.
 */
public class MaxRowTest {

  private MockPawnsBoardModel model;
  private List<Card> testDeck;
  private MaximizeRowScoreStrategy strategy;

  /**
   * Helper method to create a test card with a simple influence grid.
   *
   * @param name  the card's name
   * @param cost  the card's cost
   * @param value the card's value
   * @return a new Card instance
   */
  private Card createTestCard(String name, int cost, int value) {
    char[][] grid = new char[5][5];
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        grid[i][j] = 'X';
      }
    }
    // Set the center to 'C'
    grid[2][2] = 'C';
    // Mark influence cells with 'I'
    grid[1][2] = 'I';
    grid[2][1] = 'I';
    grid[2][3] = 'I';
    grid[3][2] = 'I';
    return new Card(name, cost, value, grid);
  }

  /**
   * Sets up the model and strategy for testing.
   * Creates a test deck with cards of cost 1 and initializes a 3x5 board.
   * Uses the MockPawnsBoardModel with transcript recording enabled.
   */
  @Before
  public void setUp() {
    testDeck = new ArrayList<>();
    testDeck.add(createTestCard("Card1", 1, 5));
    testDeck.add(createTestCard("Card2", 1, 6));
    testDeck.add(createTestCard("Card3", 1, 7));

    model = new MockPawnsBoardModel(3, 5, testDeck, 3);
    model.enableTranscriptRecording();
    strategy = new MaximizeRowScoreStrategy();
  }

  /**
   * Tests that the strategy returns null when no legal moves exist.
   */
  @Test
  public void testChooseMoveReturnsNullWhenNoLegalMoves() {
    // Clear the player's hand to force no legal moves
    Player redPlayer = model.getCurrentPlayer();
    try {
      java.lang.reflect.Field handField = Player.class.getDeclaredField("hand");
      handField.setAccessible(true);
      handField.set(redPlayer, new ArrayList<Card>());
    } catch (Exception e) {
      fail("Failed to clear Red player's hand: " + e.getMessage());
    }

    model.getTranscript().clear();
    List<Move> legalMoves = model.getLegalMoves();
    assertTrue("There should be no legal moves when the hand is empty", legalMoves.isEmpty());

    model.getTranscript().clear();
    Move chosenMove = strategy.chooseMove(model, "Red");
    assertNull("Strategy should return null when no legal moves exist", chosenMove);

    List<String> transcript = model.getTranscript();
    // Expect at least a call to getLegalMoves and getCurrentPlayer
    assertTrue("Transcript should contain 'getLegalMoves called'", transcript.contains("getLegalMoves called"));
    boolean hasPlayer = transcript.stream().anyMatch(s -> s.startsWith("getCurrentPlayer called: Red"));
    assertTrue("Transcript should record current player details", hasPlayer);
  }

  /**
   * Tests that when a move in an earlier row (row 0) does not flip the row's score
   * but a move in a lower row (row 1) would, the strategy returns the move from row 1.
   *
   * Setup: Place a high-value Blue card in row 0 so that any legal move there will not win the row.
   * Then, a move in row 1 (with no Blue interference) should win the row.
   */
  @Test
  public void testChooseMoveWinsRow() {
    Board board = model.getBoard();
    // Place a Blue card in row 0 at column 2
    Card blueHighCard = createTestCard("BlueHigh", 1, 10);
    board.getCell(0, 2).placeCard(blueHighCard, "Blue");

    model.getTranscript().clear();
    Move chosenMove = strategy.chooseMove(model, "Red");
    assertNotNull("Strategy should choose a move when one produces a winning simulated score", chosenMove);
    assertEquals("Chosen move should come from row 1", 1, chosenMove.getRow());

    List<String> transcript = model.getTranscript();
    // Check that the transcript records key calls
    assertTrue("Transcript should record 'getLegalMoves called'", transcript.contains("getLegalMoves called"));
    assertTrue("Transcript should record 'computeRowScores called'", transcript.contains("computeRowScores called"));
    assertTrue("Transcript should record 'getBoard called'", transcript.contains("getBoard called"));
    long cloneCalls = transcript.stream().filter(s -> s.equals("cloneBoard called")).count();
    assertTrue("Transcript should record at least one 'cloneBoard called'", cloneCalls >= 1);
    boolean hasPlayer = transcript.stream().anyMatch(s -> s.startsWith("getCurrentPlayer called: Red"));
    assertTrue("Transcript should record getCurrentPlayer call", hasPlayer);
    // Also check that at least one cell inspection is recorded (e.g., for row 0)
    boolean inspectedRow0 = transcript.stream().anyMatch(s -> s.contains("Inspecting cell (0,"));
    assertTrue("Transcript should record cell inspections in row 0", inspectedRow0);
  }

  /**
   * Tests that when no move in any row can flip the score, the strategy returns null.
   *
   * Setup: In every row, place a high-value Blue card so that the opponent's row score is high.
   * Any simulated move with a Red card will not flip the row.
   */
  @Test
  public void testReturnsNullWhenNoRowFlipPossible() {
    Board board = model.getBoard();
    Card blueCard = createTestCard("BlueCard", 1, 10);
    // Place Blue cards in each row
    board.getCell(0, 2).placeCard(blueCard, "Blue");
    board.getCell(1, 2).placeCard(blueCard, "Blue");
    board.getCell(2, 2).placeCard(blueCard, "Blue");

    model.getTranscript().clear();
    List<Move> legalMoves = model.getLegalMoves();
    assertFalse("There should be legal moves available", legalMoves.isEmpty());

    model.getTranscript().clear();
    Move chosenMove = strategy.chooseMove(model, "Red");
    assertNull("When no move flips any row, strategy should return null (indicating pass)", chosenMove);

    List<String> transcript = model.getTranscript();
    assertTrue("Transcript should contain a call to computeRowScores", transcript.contains("computeRowScores called"));
    assertTrue("Transcript should contain a call to getLegalMoves", transcript.contains("getLegalMoves called"));
  }

  /**
   * Tests that the strategy processes rows in top-to-bottom order.
   *
   * Setup: Modify row 0 so that it cannot be flipped (by placing a Blue card).
   * Then, the strategy should skip row 0 and choose a move from a later row.
   */
  @Test
  public void testMultipleRowsSimulationOrder() {
    Board board = model.getBoard();
    Card blueCard = createTestCard("BlueCard", 1, 10);
    // Place a Blue card in row 0 at column 2
    board.getCell(0, 2).placeCard(blueCard, "Blue");

    model.getTranscript().clear();
    Move chosenMove = strategy.chooseMove(model, "Red");
    assertNotNull("Strategy should choose a move when legal moves exist", chosenMove);
    assertTrue("Chosen move should not be from row 0", chosenMove.getRow() > 0);

    // Verify that the transcript records expected calls
    List<String> transcript = model.getTranscript();
    assertTrue("Transcript should record 'getLegalMoves called'", transcript.contains("getLegalMoves called"));
    assertTrue("Transcript should record 'computeRowScores called'", transcript.contains("computeRowScores called"));
    // Also, check that cell inspections for row 0 are present
    boolean inspectedRow0 = transcript.stream().anyMatch(s -> s.contains("Inspecting cell (0,"));
    assertTrue("Transcript should record inspections for row 0", inspectedRow0);
  }


}