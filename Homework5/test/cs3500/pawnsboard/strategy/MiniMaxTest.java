package cs3500.pawnsboard.strategy;

import cs3500.pawnsboard.model.Board;
import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.Move;
import cs3500.pawnsboard.model.MockPawnsBoardModel;
import cs3500.pawnsboard.model.Player;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MiniMaxTest {

  private MockPawnsBoardModel model;
  private List<Card> testDeck;
  private List<Move> legalMoves;
  private MiniMaxStrategy strategy;

  /**
   * Helper method to create a test card with a simple influence grid.
   * The grid is set up so that the center cell (where the card is played)
   * is marked 'C', and four adjacent cells (up, left, right, down) are marked 'I'.
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
   * Set up a test deck with one test card and a predetermined list of legal moves.
   * We create three legal moves on a 3x5 board:
   *   - (0,0): placed in a corner yields fewer influenced neighbors.
   *   - (1,2): a center move that yields full influence.
   *   - (2,4): placed in the bottom-right, again with some neighbors out-of-bound.
   */
  @Before
  public void setUp() {
    // Create test deck with one card.
    testDeck = new ArrayList<>();
    testDeck.add(createTestCard("Card1", 1, 5));

    // Create a predetermined list of legal moves.
    legalMoves = new ArrayList<>();
    legalMoves.add(new Move(0, 0, 0));
    legalMoves.add(new Move(1, 2, 0));
    legalMoves.add(new Move(2, 4, 0));

    // Instantiate a MockPawnsBoardModel with 3 rows, 5 columns, test deck, and one card in hand.
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

    strategy = new MiniMaxStrategy();
  }

  /**
   * Test that the MiniMax strategy returns null when there are no legal moves.
   */
  @Test
  public void testChooseMoveReturnsNullWhenNoLegalMoves() {
    // Clear the current player's hand using reflection.
    Player current = model.getCurrentPlayer();
    try {
      java.lang.reflect.Field handField = Player.class.getDeclaredField("hand");
      handField.setAccessible(true);
      handField.set(current, new ArrayList<Card>());
    } catch (Exception e) {
      fail("Failed to clear player's hand: " + e.getMessage());
    }
    model.getTranscript().clear();
    List<Move> moves = model.getLegalMoves();
    assertTrue("There should be no legal moves when hand is empty", moves.isEmpty());
    Move chosen = strategy.chooseMove(model, "Red");
    assertNull("Strategy should return null when no legal moves exist", chosen);
  }

  /**
   * Test that the strategy returns the only legal move when exactly one exists.
   */
  @Test
  public void testChooseMoveSingleLegalMove() {
    legalMoves.clear();
    Move onlyMove = new Move(1, 2, 0);
    legalMoves.add(onlyMove);

    model.getTranscript().clear();
    Move chosen = strategy.chooseMove(model, "Red");
    assertNotNull("Strategy should choose a move when one legal move exists", chosen);
    assertEquals("The chosen move should be the only legal move", onlyMove, chosen);

    long count = model.getTranscript().stream()
            .filter(s -> s.equals("getLegalMoves called"))
            .count();
    assertEquals("getLegalMoves should be called exactly once", 1, count);
  }

  /**
   * Test that the strategy selects the move with the maximum evaluation score.
   * Given our test card's influence grid and a 3x5 board:
   *   - A move at (0,0) yields an evaluation of 3 (since some neighbors are off-board).
   *   - A move at (1,2) yields an evaluation of 5.
   *   - A move at (2,4) yields an evaluation of 3.
   * The expected best move is (1,2).
   */
  @Test
  public void testChooseMoveSelectsMoveWithMaxEvaluation() {
    model.getTranscript().clear();
    Move chosen = strategy.chooseMove(model, "Red");
    assertNotNull("Strategy should choose a move when legal moves exist", chosen);
    assertEquals("Strategy should choose move with highest evaluation (row)", 1, chosen.getRow());
    assertEquals("Strategy should choose move with highest evaluation (col)", 2, chosen.getCol());

    long count = model.getTranscript().stream()
            .filter(s -> s.equals("getLegalMoves called"))
            .count();
    assertEquals("getLegalMoves should be called exactly once", 1, count);
  }

  /**
   * Test tie-breaking: if two moves yield the same evaluation, the strategy should choose
   * the latest move in the legal moves list.
   * Here we create two moves:
   *   - move1 at (1,2) and move2 at (1,3)
   * Both yield an evaluation of 5 on an otherwise empty board.
   */
  @Test
  public void testTieBreakerWhenEvaluationsEqual() {
    legalMoves.clear();
    Move move1 = new Move(1, 2, 0);
    Move move2 = new Move(1, 3, 0);
    legalMoves.add(move1);
    legalMoves.add(move2);

    model.getTranscript().clear();
    Move chosen = strategy.chooseMove(model, "Red");
    assertNotNull("Strategy should choose a move when legal moves exist", chosen);
    // Since evaluations are equal, the last move in the list should be chosen.
    assertEquals("Tie-breaker should choose the first move when evaluations are equal", move2, chosen);
  }

  /**
   * Test that the evaluation correctly factors in pre-existing opponent–controlled cells.
   * Pre-populate the board with a Blue card at (0,0) so that a move played there
   * will not flip the cell. Then compare with a move from (1,2) which is unaffected.
   * The strategy should select the move from (1,2).
   */
  @Test
  public void testEvaluationWithOpponentCells() {
    Board board = model.getBoard();
    Card blueCard = createTestCard("BlueCard", 1, 10);
    board.getCell(0, 0).placeCard(blueCard, "Blue");

    legalMoves.clear();
    Move move1 = new Move(0, 0, 0);  // This move cannot change (0,0) because it already has a card.
    Move move2 = new Move(1, 2, 0);  // This move will yield a full influence.
    legalMoves.add(move1);
    legalMoves.add(move2);

    model.getTranscript().clear();
    Move chosen = strategy.chooseMove(model, "Red");
    assertNotNull("Strategy should choose a move when legal moves exist", chosen);
    // Expect move2 since its evaluation will be higher.
    assertEquals("Strategy should choose the move that flips more cells", move2, chosen);
  }

  /**
   * Test that the original board remains unchanged after strategy simulation.
   * This confirms that the simulation uses a clone and does not affect the actual game state.
   */
  @Test
  public void testOriginalBoardUnchanged() {
    Board originalBoard = model.getBoard();
    // Record the owner of a cell that is not affected by simulation (e.g., (0,4)).
    String originalOwner = originalBoard.getCell(0, 4).getOwner();

    model.getTranscript().clear();
    strategy.chooseMove(model, "Red");

    String newOwner = originalBoard.getCell(0, 4).getOwner();
    assertEquals("Original board should remain unchanged after simulation", originalOwner, newOwner);
  }
}