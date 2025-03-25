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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * Test for the FillFirst strategy.
 */
public class FillFirstTest {

  private MockPawnsBoardModel model;
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
   * We now use the MockPawnsBoardModel with detailed transcript recording enabled.
   */
  @Before
  public void setUp() {
    List<Card> testDeck;
    // Create a test deck with three cards.
    testDeck = new ArrayList<>();
    testDeck.add(createTestCard("Card1", 1, 5));
    testDeck.add(createTestCard("Card2", 1, 6));
    testDeck.add(createTestCard("Card3", 1, 7));

    model = new MockPawnsBoardModel(3, 5, testDeck, 3);
    model.enableTranscriptRecording();

    strategy = new FillFirstStrategy();
  }

  /**
   * Tests that FillFirstStrategy returns the FIRST legal move and logs detailed transcript entries.
   */
  @Test
  public void testChooseMoveReturnsFirstLegalMove() {
    // Clear transcript to make sure we start clean
    model.getTranscript().clear();

    // Call getLegalMoves to record detailed inspection of cells and cards
    List<Move> legalMoves = model.getLegalMoves();
    assertFalse("There should be at least one legal move", legalMoves.isEmpty());

    // Clear transcript again to only look at strat's chooseMove process since we confirmed already
    model.getTranscript().clear();
    Move chosenMove = strategy.chooseMove(model, "Red");
    assertNotNull("Strategy should choose a move when legal moves exist", chosenMove);
    // Expect the first legal move
    assertEquals("FillFirstStrategy should choose the first legal move",
            legalMoves.get(0), chosenMove);

    // Get the transcript
    List<String> transcript = model.getTranscript();

    // The transcript should include a call to getLegalMoves
    assertTrue("Transcript should contain 'getLegalMoves called'",
            transcript.contains("getLegalMoves called"));

    // The transcript should include details from getCurrentPlayer:
    boolean hasPlayerDetails = false;
    for (String s : transcript) {
      if (s.startsWith("getCurrentPlayer called: Red")) {
        hasPlayerDetails = true;
        break;
      }
    }
    assertTrue("Transcript should record details of current player and hand",
            hasPlayerDetails);

    // Check that at least one cell was inspected, for example cell (0, 0)
    boolean inspectedCell00 = false;
    for (String s : transcript) {
      if (s.contains("Inspecting cell (0, 0):") && s.contains("owner=Red")) {
        inspectedCell00 = true;
        break;
      }
    }
    assertTrue("Transcript should record inspection of cell (0, 0) with owner Red",
            inspectedCell00);

    // Check that the transcript shows a card was considered for cell (0, 0)
    boolean consideredCard = false;
    for (String s : transcript) {
      if (s.contains("Considering card Card1") && s.contains("hand index 0")
              && s.contains("cell (0, 0)")) {
        consideredCard = true;
        break;
      }
    }
    assertTrue("Transcript should show that Card1 was considered for cell (0, 0)",
            consideredCard);

    // Finally, check that a legal move was added for cell (0, 0)
    boolean addedLegalMove = false;
    for (String s : transcript) {
      if (s.contains("Legal move added: Place card Card1 (index 0) at (0, 0)")) {
        addedLegalMove = true;
        break;
      }
    }
    assertTrue("Transcript should record that a legal move was added for cell (0,0)",
            addedLegalMove);
  }

  /**
   * Tests that FillFirstStrategy returns null (indicating pass) when no legal moves are available.
   */
  @Test
  public void testChooseMoveReturnsNullWhenNoLegalMoves() {
    // Clear the current player's hand to simulate no legal moves
    Player redPlayer = model.getCurrentPlayer();
    try {
      java.lang.reflect.Field handField = Player.class.getDeclaredField("hand");
      handField.setAccessible(true);
      handField.set(redPlayer, new ArrayList<Card>());
    } catch (Exception e) {
      fail("Failed to clear player's hand: " + e.getMessage());
    }

    // Clear transcript and call getLegalMoves
    model.getTranscript().clear();
    List<Move> legalMoves = model.getLegalMoves();
    assertTrue("There should be no legal moves when the hand is empty",
            legalMoves.isEmpty());

    // Clear transcript and attempt to choose a move
    model.getTranscript().clear();
    Move chosenMove = strategy.chooseMove(model, "Red");
    assertNull("Strategy should return null when no legal moves exist", chosenMove);

    // Check that the transcript contains detailed logging of getLegalMoves
    List<String> transcript = model.getTranscript();
    assertTrue("Transcript should contain 'getLegalMoves called'",
            transcript.contains("getLegalMoves called"));
  }

  /**
   * Tests that if the very first cell (row 0, col 0) is made illegal (by having a card placed),
   * the strategy skips it and returns the next legal move: (1, 0) card ind 0.
   */
  @Test
  public void testChooseMoveSkipsIllegalFirstCell() {
    Board board = model.getBoard();
    // Place a dummy card into cell (0,0) so that it becomes illegal
    Card dummy = createTestCard("Dummy", 1, 1);
    board.getCell(0, 0).placeCard(dummy, "Red");

    // Clear transcript and call getLegalMoves
    model.getTranscript().clear();
    List<Move> legalMoves = model.getLegalMoves();
    for (Move m : legalMoves) {
      assertFalse("Cell (0,0) should not be legal once a card is placed there",
              m.getRow() == 0 && m.getCol() == 0);
    }

    // Clear transcript and let the strategy choose a move
    model.getTranscript().clear();
    Move chosenMove = strategy.chooseMove(model, "Red");
    assertNotNull("Strategy should choose a move when legal moves exist", chosenMove);
    // Based on our configuration, the next available move should be in cell (1,0)
    assertEquals("Strategy should choose the next available move (row)", 1,
            chosenMove.getRow());
    assertEquals("Strategy should choose the move at column 0", 0,
            chosenMove.getCol());

    // Verify transcript contains expected cell inspection details
    List<String> transcript = model.getTranscript();
    boolean inspectedIllegalCell = false;
    for (String s : transcript) {
      if (s.contains("Inspecting cell (0, 0):") && s.contains("cardPresent=true")) {
        inspectedIllegalCell = true;
        break;
      }
    }
    assertTrue("Transcript should record inspection of cell (0,0) showing a card is " +
            "present", inspectedIllegalCell);
  }

  /**
   * Tests that when the very first card in the player's hand is not playable due to its cost,
   * the legal move is generated using a later card in the hand.
   */
  @Test
  public void testChooseMoveWithNonPlayableFirstCard() {
    // first card has cost 2 (not playable on a cell with 1 pawn),
    // and second card has cost 1 (playable)
    List<Card> newHand = new ArrayList<>();
    newHand.add(createTestCard("ExpensiveCard", 2, 10));
    newHand.add(createTestCard("CheapCard", 1, 5));

    // Update the current player's hand via reflection
    Player redPlayer = model.getCurrentPlayer();
    try {
      java.lang.reflect.Field handField = Player.class.getDeclaredField("hand");
      handField.setAccessible(true);
      handField.set(redPlayer, newHand);
    } catch (Exception e) {
      fail("Failed to update Red player's hand: " + e.getMessage());
    }

    // Clear transcript and generate legal moves
    model.getTranscript().clear();
    List<Move> legalMoves = model.getLegalMoves();
    assertFalse("There should be at least one legal move", legalMoves.isEmpty());

    // Clear transcript and choose move
    model.getTranscript().clear();
    Move chosenMove = strategy.chooseMove(model, "Red");
    assertNotNull("Strategy should choose a move when a legal move exists", chosenMove);
    // Expect that the chosen move uses the second card in hand (index 1)
    assertEquals("The move should use the second card in hand (index 1)", 1,
            chosenMove.getCardIndex());
    // And because cell (0,0) is legal, the move should be from (0,0)
    assertEquals("The move should be in row 0", 0, chosenMove.getRow());
    assertEquals("The move should be in column 0", 0, chosenMove.getCol());

    // Check transcript for details about considering both cards
    List<String> transcript = model.getTranscript();
    boolean consideredExpensive = false;
    boolean consideredCheap = false;
    for (String s : transcript) {
      if (s.contains("Considering card ExpensiveCard") && s.contains("hand index 0")) {
        consideredExpensive = true;
      }
      if (s.contains("Considering card CheapCard") && s.contains("hand index 1")) {
        consideredCheap = true;
      }
    }
    assertTrue("Transcript should record that ExpensiveCard was considered",
            consideredExpensive);
    assertTrue("Transcript should record that CheapCard was considered",
            consideredCheap);
  }
}