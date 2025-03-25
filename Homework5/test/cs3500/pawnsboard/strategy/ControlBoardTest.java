package cs3500.pawnsboard.strategy;

import cs3500.pawnsboard.model.MockPawnsBoardModel;
import cs3500.pawnsboard.model.Move;
import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.Player;
import cs3500.pawnsboard.model.PawnsBoardModel;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for the Control Board.
 */
public class ControlBoardTest {

  private MockPawnsBoardModel model;
  private TestableControlBoardStrategy strategy;
  private List<Move> legalMoves;

  /**
   * Helper method to create a test card with a simple influence grid.
   */
  private Card createTestCard(String name, int cost, int value) {
    char[][] grid = new char[5][5];
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        grid[i][j] = 'X';
      }
    }
    grid[2][2] = 'C';
    grid[1][2] = 'I';
    grid[2][1] = 'I';
    grid[2][3] = 'I';
    grid[3][2] = 'I';
    return new Card(name, cost, value, grid);
  }

  /**
   * A testable subclass of ControlBoardStrategy that lets us override the simulated controlled
   * cell count for each move. We use a string key "row,col,cardIndex" for reliable lookup.
   */
  public static class TestableControlBoardStrategy extends ControlBoardStrategy {
    private Map<String, Integer> simulationResults = new HashMap<>();

    public void setSimulationResult(Move move, int result) {
      String key = move.getRow() + "," + move.getCol() + "," + move.getCardIndex();
      simulationResults.put(key, result);
    }

    @Override
    protected int simulateControlledCells(PawnsBoardModel model, Move move, String playerColor) {
      String key = move.getRow() + "," + move.getCol() + "," + move.getCardIndex();
      return simulationResults.getOrDefault(key, 0);
    }
  }

  @Before
  public void setUp() {
    List<Card> testDeck;
    // Create a test deck (for simplicity, one card is enough for these tests).
    testDeck = new ArrayList<>();
    testDeck.add(createTestCard("Card1", 1, 5));

    // Prepare a predetermined list of legal moves.
    legalMoves = new ArrayList<>();
    // For example, we add three moves.
    legalMoves.add(new Move(0, 0, 0)); // Move A
    legalMoves.add(new Move(0, 1, 0)); // Move B
    legalMoves.add(new Move(1, 0, 0)); // Move C

    // Create a custom mock model that returns our predetermined legal moves.
    model = new MockPawnsBoardModel(3, 5, testDeck, 1) {
      @Override
      public List<Move> getLegalMoves() {
        getTranscript().add("getLegalMoves called");
        // If the current player's hand is empty, return an empty list.
        if (getCurrentPlayer().getHand().isEmpty()) {
          return new ArrayList<>();
        }
        return legalMoves;
      }
    };
    model.enableTranscriptRecording();

    // Use our testable version of the strategy.
    strategy = new TestableControlBoardStrategy();
  }

  /**
   * Tests that the strategy returns null when there are no legal moves.
   */
  @Test
  public void testChooseMoveReturnsNullWhenNoLegalMoves() {
    // Clear the current player's hand to simulate no legal moves.
    Player currentPlayer = model.getCurrentPlayer();
    try {
      java.lang.reflect.Field handField = Player.class.getDeclaredField("hand");
      handField.setAccessible(true);
      handField.set(currentPlayer, new ArrayList<Card>());
    } catch (Exception e) {
      fail("Failed to clear player's hand: " + e.getMessage());
    }

    model.getTranscript().clear();
    List<Move> moves = model.getLegalMoves();
    assertTrue("There should be no legal moves when hand is empty", moves.isEmpty());
    Move chosenMove = strategy.chooseMove(model, "Red");
    assertNull("Strategy should return null when no legal moves exist", chosenMove);
  }

  /**
   * Tests that the strategy chooses the move with the highest simulated controlled cells count.
   */
  @Test
  public void testChooseMoveChoosesMoveWithMaxControlledCells() {
    // Set simulation results for our three moves.
    // For example:
    // Move A (0,0): 5 controlled cells.
    // Move B (0,1): 7 controlled cells.
    // Move C (1,0): 6 controlled cells.
    Move moveA = legalMoves.get(0);
    Move moveB = legalMoves.get(1);
    Move moveC = legalMoves.get(2);

    strategy.setSimulationResult(moveA, 5);
    strategy.setSimulationResult(moveB, 7);
    strategy.setSimulationResult(moveC, 6);

    // Before calling the strategy, ensure legal moves exist.
    List<Move> currentLegalMoves = model.getLegalMoves();
    assertFalse("Legal moves list should not be empty", currentLegalMoves.isEmpty());

    model.getTranscript().clear();
    Move chosenMove = strategy.chooseMove(model, "Red");
    assertNotNull("Strategy should choose a move when legal moves exist", chosenMove);
    // Expect moveB to be chosen because it has the highest count (7).
    assertEquals("Strategy should choose the move with maximum controlled cells", moveB,
            chosenMove);

    long legalMovesCallCount = model.getTranscript().stream()
            .filter(s -> s.equals("getLegalMoves called"))
            .count();
    assertEquals("Strategy should call getLegalMoves exactly once", 1,
            legalMovesCallCount);
  }

  /**
   * Tests the tie-breaker: if two moves have the same controlled cell count, choose the
   * uppermost-leftmost.
   */
  @Test
  public void testTieBreakerUppermostLeftmost() {
    // Create two moves with equal simulation results.
    // For example, moveD at (1,1) and moveE at (2,0).
    Move moveD = new Move(1, 1, 0);
    Move moveE = new Move(2, 0, 0);
    legalMoves.clear();
    legalMoves.add(moveE);
    legalMoves.add(moveD);

    // Set both simulation results equal, e.g., 8.
    strategy.setSimulationResult(moveD, 8);
    strategy.setSimulationResult(moveE, 8);

    // Ensure legal moves exist.
    List<Move> currentLegalMoves = model.getLegalMoves();
    assertFalse("Legal moves list should not be empty", currentLegalMoves.isEmpty());

    model.getTranscript().clear();
    Move chosenMove = strategy.chooseMove(model, "Red");
    assertNotNull("Strategy should choose a move when legal moves exist", chosenMove);
    // According to the tie-breaker, (1,1) is uppermost-leftmost compared to (2,0).
    assertEquals("Tie-breaker should choose the uppermost-leftmost move", moveD,
            chosenMove);

    long legalMovesCallCount = model.getTranscript().stream()
            .filter(s -> s.equals("getLegalMoves called"))
            .count();
    assertEquals("Strategy should call getLegalMoves exactly once", 1,
            legalMovesCallCount);
  }

  /**
   * Tests the tie-breaker for card index: if two moves have the same location and simulation
   * result,
   * choose the move with the lower (leftmost) card index.
   */
  @Test
  public void testTieBreakerCardIndex() {
    // Create two moves at the same location but with different card indices.
    Move moveF = new Move(0, 0, 1); // card index 1
    Move moveG = new Move(0, 0, 0); // card index 0 (should be chosen)
    legalMoves.clear();
    legalMoves.add(moveF);
    legalMoves.add(moveG);

    // Set both simulation results equal.
    strategy.setSimulationResult(moveF, 9);
    strategy.setSimulationResult(moveG, 9);

    // Ensure legal moves exist.
    List<Move> currentLegalMoves = model.getLegalMoves();
    assertFalse("Legal moves list should not be empty", currentLegalMoves.isEmpty());

    model.getTranscript().clear();
    Move chosenMove = strategy.chooseMove(model, "Red");
    assertNotNull("Strategy should choose a move when legal moves exist", chosenMove);
    assertEquals("Tie-breaker should choose the move with the lower card index", moveG,
            chosenMove);

    long legalMovesCallCount = model.getTranscript().stream()
            .filter(s -> s.equals("getLegalMoves called"))
            .count();
    assertEquals("Strategy should call getLegalMoves exactly once", 1,
            legalMovesCallCount);
  }
}