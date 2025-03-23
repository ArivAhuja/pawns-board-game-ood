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

public class FillFirstTest {

  private MockPawnsBoardModel model;
  private List<Card> testDeck;
  private FillFirstStrategy strategy;

  /**
   * Helper method to create a test card with a simple surrounding influence grid.
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
    // Mark some influence cells with 'I'
    grid[1][2] = 'I';
    grid[2][1] = 'I';
    grid[2][3] = 'I';
    grid[3][2] = 'I';
    return new Card(name, cost, value, grid);
  }

  /**
   * Sets up the model and strategy for testing.
   * This creates a test deck with cards of cost 1 and initializes a 3x5 board.
   * Note that we now use the MockPawnsBoardModel and enable its transcript recording.
   */
  @Before
  public void setUp() {
    // Create a test deck with three cards that all have cost 1.
    testDeck = new ArrayList<>();
    testDeck.add(createTestCard("Card1", 1, 5));
    testDeck.add(createTestCard("Card2", 1, 6));
    testDeck.add(createTestCard("Card3", 1, 7));

    // Use the mock model instead of the real model.
    model = new MockPawnsBoardModel(3, 5, testDeck, 3);
    model.enableTranscriptRecording();

    strategy = new FillFirstStrategy();
  }

  /**
   * Tests that FillFirstStrategy returns the first legal move when moves are available.
   */
  @Test
  public void testChooseMoveReturnsFirstLegalMove() {
    // Clear transcript to isolate the strategy call.
    model.getTranscript().clear();
    List<Move> legalMoves = model.getLegalMoves();
    assertFalse("There should be at least one legal move, no moves have been made yet",
            legalMoves.isEmpty());

    // Clear transcript again so only the strategy's call is recorded.
    model.getTranscript().clear();
    Move chosenMove = strategy.chooseMove(model, "Red");
    assertNotNull("Strategy should choose a move when legal moves exist", chosenMove);
    assertEquals("FillFirstStrategy should choose the first legal move",
            legalMoves.get(0), chosenMove);

    // Verify that the strategy called getLegalMoves exactly once.
    List<String> transcript = model.getTranscript();
    assertEquals("Strategy should call getLegalMoves exactly once", 1,
            transcript.stream().filter(s -> s.equals("getLegalMoves called")).count());
    assertEquals("getLegalMoves called", transcript.get(0));
  }

  /**
   * Tests that FillFirstStrategy returns null (indicating pass) when no legal moves are available.
   */
  @Test
  public void testChooseMoveReturnsNullWhenNoLegalMoves() {
    // To simulate no legal moves, clear the current player's hand.
    Player redPlayer = model.getCurrentPlayer();
    try {
      java.lang.reflect.Field handField = Player.class.getDeclaredField("hand");
      handField.setAccessible(true);
      handField.set(redPlayer, new ArrayList<Card>());
    }
    catch (Exception e) {
      fail("Failed to clear Red player's hand: " + e.getMessage());
    }

    // Now, getLegalMoves should be empty.
    model.getTranscript().clear();
    List<Move> legalMoves = model.getLegalMoves();
    assertTrue("There should be no legal moves when the hand is empty", legalMoves.isEmpty());

    model.getTranscript().clear();
    // Strategy should return null to indicate no valid move.
    Move chosenMove = strategy.chooseMove(model, "Red");
    assertNull("Strategy should return null when no legal moves exist", chosenMove);

    List<String> transcript = model.getTranscript();
    assertEquals("Strategy should call getLegalMoves exactly once", 1,
            transcript.stream().filter(s -> s.equals("getLegalMoves called")).count());
    assertEquals("getLegalMoves called", transcript.get(0));
  }

  /**
   * Tests that if the very first cell (row 0, col 0) is made illegal (by having a card placed),
   * the strategy skips it and returns the next legal move.
   */
  @Test
  public void testChooseMoveSkipsIllegalFirstCell() {
    Board board = model.getBoard();
    // Place a dummy card directly into cell (0,0) to mark it as illegal.
    Card dummy = createTestCard("Dummy", 1, 1);
    board.getCell(0, 0).placeCard(dummy, "Red");

    // Get the legal moves after modifying the board.
    model.getTranscript().clear();
    List<Move> legalMoves = model.getLegalMoves();
    // Verify that none of the legal moves come from (0,0)
    for (Move m : legalMoves) {
      assertFalse("Cell (0,0) should not be legal once a card is placed there",
              m.getRow() == 0 && m.getCol() == 0);
    }

    // Now let the strategy choose a move.
    model.getTranscript().clear();
    Move chosenMove = strategy.chooseMove(model, "Red");
    assertNotNull("Strategy should choose a move when legal moves exist", chosenMove);
    // In our default configuration, if (0,0) is illegal then the next legal cell is (1,0)
    assertEquals("Strategy should choose the next available move (row)", 1, chosenMove.getRow());
    assertEquals("The chosen move should be on column 0", 0, chosenMove.getCol());

    List<String> transcript = model.getTranscript();
    assertEquals("Strategy should call getLegalMoves exactly once", 1,
            transcript.stream().filter(s -> s.equals("getLegalMoves called")).count());
    assertEquals("getLegalMoves called", transcript.get(0));
  }

  /**
   * Tests that when the very first card in the player's hand is not playable (due to its cost),
   * the legal move is generated for a later card in the hand.
   */
  @Test
  public void testChooseMoveWithNonPlayableFirstCard() {
    // Create a new hand: first card has cost 2 (not playable on a cell with 1 pawn),
    // and second card has cost 1 (playable).
    List<Card> newHand = new ArrayList<>();
    newHand.add(createTestCard("ExpensiveCard", 2, 10));
    newHand.add(createTestCard("CheapCard", 1, 5));

    // Use reflection to update the current player's hand.
    Player redPlayer = model.getCurrentPlayer();
    try {
      java.lang.reflect.Field handField = Player.class.getDeclaredField("hand");
      handField.setAccessible(true);
      handField.set(redPlayer, newHand);
    }
    catch (Exception e) {
      fail("Failed to update Red player's hand: " + e.getMessage());
    }

    model.getTranscript().clear();
    List<Move> legalMoves = model.getLegalMoves();
    assertFalse("There should be at least one legal move", legalMoves.isEmpty());

    model.getTranscript().clear();
    // Since the first card is not playable, the only legal move for (0,0) should come from the second card.
    Move chosenMove = strategy.chooseMove(model, "Red");
    assertNotNull("Strategy should choose a move when a legal move exists", chosenMove);
    assertEquals("The move should use the second card in hand (index 1)",
            1, chosenMove.getCardIndex());
    // And because (0,0) is legal, the move should come from that cell.
    assertEquals("The move should be in row 0", 0, chosenMove.getRow());
    assertEquals("The move should be in column 0", 0, chosenMove.getCol());

    List<String> transcript = model.getTranscript();
    assertEquals("Strategy should call getLegalMoves exactly once", 1,
            transcript.stream().filter(s -> s.equals("getLegalMoves called")).count());
    assertEquals("getLegalMoves called", transcript.get(0));
  }
}