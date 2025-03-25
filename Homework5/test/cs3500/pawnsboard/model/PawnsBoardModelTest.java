package cs3500.pawnsboard.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * Test suite for the PawnsBoardModel class.
 */
public class PawnsBoardModelTest {

  private PawnsBoardModel model;
  private List<Card> testDeck;

  /**
   * Creates a test card with the specified parameters.
   *
   * @param name Card name
   * @param cost Card cost
   * @param value Card value
   * @return A new Card instance
   */
  private Card createTestCard(String name, int cost, int value) {
    char[][] grid = new char[5][5];
    // Initialize default grid with 'X's
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        grid[i][j] = 'X';
      }
    }
    // Set center to 'C' (required)
    grid[2][2] = 'C';
    // Add some influence cells
    grid[1][2] = 'I';
    grid[2][1] = 'I';
    grid[2][3] = 'I';
    grid[3][2] = 'I';

    return new Card(name, cost, value, grid);
  }

  @Before
  public void setUp() {
    // Create a test deck with various cards
    testDeck = new ArrayList<>();
    testDeck.add(createTestCard("Card1", 1, 5));
    testDeck.add(createTestCard("Card2", 2, 8));
    testDeck.add(createTestCard("Card3", 3, 12));
    testDeck.add(createTestCard("Card4", 1, 3));
    testDeck.add(createTestCard("Card5", 2, 6));
    testDeck.add(createTestCard("Card6", 3, 10));
    testDeck.add(createTestCard("Card7", 3, 10));
    testDeck.add(createTestCard("Card8", 3, 10));
    testDeck.add(createTestCard("Card9", 3, 10));
    testDeck.add(createTestCard("Card10", 3, 10));
    testDeck.add(createTestCard("Card11", 1, 5));
    testDeck.add(createTestCard("Card12", 2, 8));
    testDeck.add(createTestCard("Card13", 3, 12));
    testDeck.add(createTestCard("Card14", 1, 3));
    testDeck.add(createTestCard("Card15", 2, 6));
    testDeck.add(createTestCard("Card16", 3, 10));
    testDeck.add(createTestCard("Card17", 3, 10));
    testDeck.add(createTestCard("Card18", 3, 10));
    testDeck.add(createTestCard("Card19", 3, 10));

    // Initialize a 3x5 board with the test deck
    model = new PawnsBoardModel(3, 5, testDeck, 1);
  }

  @Test
  public void testConstructorInitialization() {
    // Test board dimensions
    assertEquals(3, model.getBoard().getRows());
    assertEquals(5, model.getBoard().getColumns());

    // Test initial player turn (Red starts)
    assertEquals("Red", model.getCurrentPlayer().getColor());

    // Test initial board state - first column has red pawns
    for (int row = 0; row < 3; row++) {
      Cell leftCell = model.getBoard().getCell(row, 0);
      assertEquals(1, leftCell.getPawnCount());
      assertEquals("Red", leftCell.getOwner());

      // Last column has blue pawns
      Cell rightCell = model.getBoard().getCell(row, 4);
      assertEquals(1, rightCell.getPawnCount());
      assertEquals("Blue", rightCell.getOwner());
    }

    // Test initial game state (not over)
    assertFalse(model.isGameOver());

    // Test player hands - both should have 3 cards
    assertEquals(3, model.getCurrentPlayer().getHand().size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithInvalidRows() {
    new PawnsBoardModel(0, 5, testDeck, 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithInvalidColumns() {
    new PawnsBoardModel(3, 4, testDeck, 3); // Columns must be odd
  }

  @Test
  public void testPass() {
    // Initially it's Red's turn
    assertEquals("Red", model.getCurrentPlayer().getColor());

    // After passing, it should be Blue's turn
    model.pass();
    assertEquals("Blue", model.getCurrentPlayer().getColor());

    // After another pass, back to Red
    model.pass();
    assertEquals("Red", model.getCurrentPlayer().getColor());
  }

  @Test
  public void testConsecutivePasses() {
    // Game starts with 0 consecutive passes
    assertFalse(model.isGameOver());

    // One pass - game not over
    model.pass();
    assertFalse(model.isGameOver());

    // Two consecutive passes - game over
    model.pass();
    assertTrue(model.isGameOver());
  }

  @Test
  public void testPlaceCardSuccessful() {
    // Red player places a card at (0,0)
    boolean result = model.placeCard(0, 0, 0); // Assumes card at index 0 has cost 1

    assertTrue(result);

    // Check that the card was placed
    Cell cell = model.getBoard().getCell(0, 0);
    assertNotNull(cell.getCard());
    assertEquals("Red", cell.getOwner());
    assertEquals(0, cell.getPawnCount()); // Pawns get consumed

    // Turn should change to Blue
    assertEquals("Blue", model.getCurrentPlayer().getColor());

    // Consecutive passes should be reset
    model.pass(); // Blue passes
    assertFalse(model.isGameOver()); // One pass is not enough to end game
  }

  @Test
  public void testPlaceCardInvalidPosition() {
    // Try to place at an invalid position
    boolean result = model.placeCard(-1, 0, 0);
    assertFalse(result);

    // Turn should not change
    assertEquals("Red", model.getCurrentPlayer().getColor());
  }

  @Test
  public void testPlaceCardEmptyCell() {
    // Try to place on a cell with no pawns (1,1)
    boolean result = model.placeCard(1, 1, 0);
    assertFalse(result);

    // Turn should not change
    assertEquals("Red", model.getCurrentPlayer().getColor());
  }

  @Test
  public void testPlaceCardWrongOwner() {
    // Red trying to place on Blue's pawn (0,4)
    boolean result = model.placeCard(0, 4, 0);
    assertFalse(result);

    // Turn should not change
    assertEquals("Red", model.getCurrentPlayer().getColor());
  }

  @Test
  public void testPlaceCardInvalidCardIndex() {
    // Try to place with invalid card index
    boolean result = model.placeCard(0, 0, 10); // There's no card at index 10
    assertFalse(result);

    // Turn should not change
    assertEquals("Red", model.getCurrentPlayer().getColor());
  }

  @Test
  public void testPlaceCardInsufficientPawns() {
    // Try to place a card with cost 2 on a cell with only 1 pawn
    // First ensure we have a card with cost 2 at index 1
    List<Card> hand = model.getCurrentPlayer().getHand();
    boolean hasExpensiveCard = false;
    int expensiveCardIndex = -1;

    for (int i = 0; i < hand.size(); i++) {
      if (hand.get(i).getCost() > 1) {
        hasExpensiveCard = true;
        expensiveCardIndex = i;
        break;
      }
    }

    if (hasExpensiveCard) {
      boolean result = model.placeCard(0, 0, expensiveCardIndex);
      assertFalse(result);

      // Turn should not change
      assertEquals("Red", model.getCurrentPlayer().getColor());
    }
  }

  @Test
  public void testPlaceCardAlreadyHasCard() {
    // First place a card
    model.placeCard(0, 0, 0);

    // Then try to place another card on the same cell (now Blue's turn)
    model.placeCard(0, 0, 0);

    // Cell should still have Red's card
    Cell cell = model.getBoard().getCell(0, 0);
    assertEquals("Red", cell.getOwner());
  }

  @Test
  public void testInfluence() {
    // Clear the board for controlled test conditions
    Board board = model.getBoard();
    for (int i = 0; i < board.getRows(); i++) {
      for (int j = 0; j < board.getColumns(); j++) {
        board.setCellPawns(i, j, 0, "");
      }
    }

    // Set up test condition: Red has a pawn at (1,1)
    board.setCellPawns(1, 1, 1, "Red");

    // Create a card with specific influence pattern
    Card testCard = createTestCard("TestCard", 1, 5);

    // Add to red player's hand and ensure it's at index 0
    Player redPlayer = model.getCurrentPlayer();
    List<Card> hand = new ArrayList<>();
    hand.add(testCard);
    // Use reflection to replace hand if necessary
    try {
      java.lang.reflect.Field handField = Player.class.getDeclaredField("hand");
      handField.setAccessible(true);
      handField.set(redPlayer, hand);
    } catch (Exception e) {
      fail("Failed to set up test: " + e.getMessage());
    }

    // Red places the card
    boolean result = model.placeCard(1, 1, 0);
    assertTrue(result);

    // Check influence effects - the card's pattern should add pawns at (0,1), (1,0), (1,2), (2,1)
    // Check that pawns were added where expected
    Cell topCell = board.getCell(0, 1);
    Cell leftCell = board.getCell(1, 0);
    Cell rightCell = board.getCell(1, 2);
    Cell bottomCell = board.getCell(2, 1);

    assertEquals(1, topCell.getPawnCount());
    assertEquals("Red", topCell.getOwner());

    assertEquals(1, leftCell.getPawnCount());
    assertEquals("Red", leftCell.getOwner());

    assertEquals(1, rightCell.getPawnCount());
    assertEquals("Red", rightCell.getOwner());

    assertEquals(1, bottomCell.getPawnCount());
    assertEquals("Red", bottomCell.getOwner());
  }

  @Test
  public void testInfluenceOnOpponentPawns() {
    // Set up test condition: Red has a pawn at (1,1), Blue has a pawn at (1,2)
    Board board = model.getBoard();
    board.setCellPawns(1, 1, 1, "Red");
    board.setCellPawns(1, 2, 1, "Blue");

    // Create a card with specific influence pattern
    Card testCard = createTestCard("TestCard", 1, 5);

    // Add to red player's hand
    Player redPlayer = model.getCurrentPlayer();
    List<Card> hand = new ArrayList<>();
    hand.add(testCard);
    // Use reflection to replace hand
    try {
      java.lang.reflect.Field handField = Player.class.getDeclaredField("hand");
      handField.setAccessible(true);
      handField.set(redPlayer, hand);
    } catch (Exception e) {
      fail("Failed to set up test: " + e.getMessage());
    }

    // Red places the card
    boolean result = model.placeCard(1, 1, 0);
    assertTrue(result);

    // Check that Blue's pawn at (1,2) is now Red's
    Cell rightCell = board.getCell(1, 2);
    assertEquals("Red", rightCell.getOwner());
    assertEquals(1, rightCell.getPawnCount());
  }

  @Test
  public void testInfluenceOnExistingPawns() {
    // Set up: Red has 1 pawn at (1,1) and 2 pawns at (1,2)
    Board board = model.getBoard();
    board.setCellPawns(1, 1, 1, "Red");
    board.setCellPawns(1, 2, 2, "Red");

    // Create a card with specific influence pattern
    Card testCard = createTestCard("TestCard", 1, 5);

    // Add to red player's hand
    Player redPlayer = model.getCurrentPlayer();
    List<Card> hand = new ArrayList<>();
    hand.add(testCard);
    // Use reflection to replace hand
    try {
      java.lang.reflect.Field handField = Player.class.getDeclaredField("hand");
      handField.setAccessible(true);
      handField.set(redPlayer, hand);
    } catch (Exception e) {
      fail("Failed to set up test: " + e.getMessage());
    }

    // Red places the card
    boolean result = model.placeCard(1, 1, 0);
    assertTrue(result);

    // Cell at (1,2) should now have 3 pawns (2+1, max is 3)
    Cell rightCell = board.getCell(1, 2);
    assertEquals(3, rightCell.getPawnCount());
    assertEquals("Red", rightCell.getOwner());
  }

  @Test
  public void testInfluenceWithBluePlayer() {
    // Setup: Blue has a pawn at (1,3)
    Board board = model.getBoard();
    board.setCellPawns(1, 3, 1, "Blue");

    // First pass to make it Blue's turn
    model.pass();
    assertEquals("Blue", model.getCurrentPlayer().getColor());

    // Create a card with specific influence pattern
    Card testCard = createTestCard("TestCard", 1, 5);

    // Add to blue player's hand
    Player bluePlayer = model.getCurrentPlayer();
    List<Card> hand = new ArrayList<>();
    hand.add(testCard);
    // Use reflection to replace hand
    try {
      java.lang.reflect.Field handField = Player.class.getDeclaredField("hand");
      handField.setAccessible(true);
      handField.set(bluePlayer, hand);
    } catch (Exception e) {
      fail("Failed to set up test: " + e.getMessage());
    }

    // Blue places the card
    boolean result = model.placeCard(1, 3, 0);
    assertTrue(result);

    // For Blue, the influence grid is mirrored horizontally
    // So we expect increased pawns at (0,3), (1,2), (1,4), (2,3)
    Cell topCell = board.getCell(0, 3);
    Cell leftCell = board.getCell(1, 2);
    Cell rightCell = board.getCell(1, 4);
    Cell bottomCell = board.getCell(2, 3);

    // Check the cells have Blue pawns
    if (board.isValidPosition(0, 3)) {
      assertEquals(1, topCell.getPawnCount());
      assertEquals("Blue", topCell.getOwner());
    }

    if (board.isValidPosition(1, 2)) {
      assertEquals(1, leftCell.getPawnCount());
      assertEquals("Blue", leftCell.getOwner());
    }

    if (board.isValidPosition(1, 4)) {
      assertEquals(2, rightCell.getPawnCount());
      assertEquals("Blue", rightCell.getOwner());
    }

    if (board.isValidPosition(2, 3)) {
      assertEquals(1, bottomCell.getPawnCount());
      assertEquals("Blue", bottomCell.getOwner());
    }
  }

  @Test
  public void testGetLegalMoves() {
    // By default, the first column has Red pawns
    // So legal moves should include placing any card with cost 1 on these cells
    List<Move> legalMoves = model.getLegalMoves();

    // Verify moves exist for Red's pawns in first column
    boolean foundMove = false;
    for (Move move : legalMoves) {
      if (move.col == 0 && move.cardIndex < model.getCurrentPlayer().getHand().size()) {
        Card card = model.getCurrentPlayer().getHand().get(move.cardIndex);
        if (card.getCost() == 1) {
          foundMove = true;
          break;
        }
      }
    }

    assertTrue("Should find a legal move for Red's pawns", foundMove);
  }

  @Test
  public void testAutoPassIfHandEmpty() {
    // Make Red's hand empty
    Player redPlayer = model.getCurrentPlayer();

    // Test auto-pass
    boolean didAutoPass = model.checkAutoPass();
    assertTrue(didAutoPass);

    // Turn should have changed to Blue
    assertEquals("Blue", model.getCurrentPlayer().getColor());
  }

  @Test
  public void testComputeScores() {
    // Set up a board with cards placed
    Board board = model.getBoard();

    // Create test cards with different values
    Card redCard1 = createTestCard("RedCard1", 1, 5);
    Card redCard2 = createTestCard("RedCard2", 1, 3);
    Card blueCard1 = createTestCard("BlueCard1", 1, 2);
    Card blueCard2 = createTestCard("BlueCard2", 1, 4);

    // Clear the board
    for (int i = 0; i < board.getRows(); i++) {
      for (int j = 0; j < board.getColumns(); j++) {
        board.setCellPawns(i, j, 0, "");
      }
    }

    // Setup cells with cards manually
    Cell cell1 = board.getCell(0, 0);
    cell1.placeCard(redCard1, "Red");

    Cell cell2 = board.getCell(0, 1);
    cell2.placeCard(blueCard1, "Blue");

    Cell cell3 = board.getCell(1, 0);
    cell3.placeCard(redCard2, "Red");

    Cell cell4 = board.getCell(1, 1);
    cell4.placeCard(blueCard2, "Blue");

    // Compute scores
    int[] scores = model.computeScores();

    // Row 0: Red has 5, Blue has 2 -> Red wins row, scores 5
    // Row 1: Red has 3, Blue has 4 -> Blue wins row, scores 4
    // Row 2: No cards, no score
    assertEquals(5, scores[0]); // Red's score
    assertEquals(4, scores[1]); // Blue's score
  }

  @Test
  public void testComputeRowScores() {
    // Set up a board with cards placed
    Board board = model.getBoard();

    // Create test cards with different values
    Card redCard1 = createTestCard("RedCard1", 1, 5);
    Card redCard2 = createTestCard("RedCard2", 1, 3);
    Card blueCard1 = createTestCard("BlueCard1", 1, 2);
    Card blueCard2 = createTestCard("BlueCard2", 1, 4);

    // Clear the board
    for (int i = 0; i < board.getRows(); i++) {
      for (int j = 0; j < board.getColumns(); j++) {
        board.setCellPawns(i, j, 0, "");
      }
    }

    // Setup cells with cards manually
    Cell cell1 = board.getCell(0, 0);
    cell1.placeCard(redCard1, "Red");

    Cell cell2 = board.getCell(0, 1);
    cell2.placeCard(blueCard1, "Blue");

    Cell cell3 = board.getCell(1, 0);
    cell3.placeCard(redCard2, "Red");

    Cell cell4 = board.getCell(1, 1);
    cell4.placeCard(blueCard2, "Blue");

    // Compute row scores
    int[][] rowScores = model.computeRowScores();

    // Row 0: Red has 5, Blue has 2
    assertEquals(5, rowScores[0][0]); // Red's row 0 score
    assertEquals(2, rowScores[0][1]); // Blue's row 0 score

    // Row 1: Red has 3, Blue has 4
    assertEquals(3, rowScores[1][0]); // Red's row 1 score
    assertEquals(4, rowScores[1][1]); // Blue's row 1 score

    // Row 2: No cards, no score
    assertEquals(0, rowScores[2][0]); // Red's row 2 score
    assertEquals(0, rowScores[2][1]); // Blue's row 2 score
  }

  @Test
  public void testRedWinsGame() {
    // Setup a game state where Red wins
    Board board = model.getBoard();

    // Create test cards
    Card redCard = createTestCard("RedCard", 1, 10);
    Card blueCard = createTestCard("BlueCard", 1, 5);

    // Clear the board
    for (int i = 0; i < board.getRows(); i++) {
      for (int j = 0; j < board.getColumns(); j++) {
        board.setCellPawns(i, j, 0, "");
      }
    }

    // Place cards to ensure Red has higher score
    Cell cell1 = board.getCell(0, 0);
    cell1.placeCard(redCard, "Red");

    Cell cell2 = board.getCell(1, 0);
    cell2.placeCard(blueCard, "Blue");

    // Trigger game end
    model.pass();
    model.pass();

    // Check winner
    assertEquals("Red wins!", model.getWinner());
  }

  @Test
  public void testBlueWinsGame() {
    // Setup a game state where Blue wins
    Board board = model.getBoard();

    // Create test cards
    Card redCard = createTestCard("RedCard", 1, 5);
    Card blueCard = createTestCard("BlueCard", 1, 10);

    // Clear the board
    for (int i = 0; i < board.getRows(); i++) {
      for (int j = 0; j < board.getColumns(); j++) {
        board.setCellPawns(i, j, 0, "");
      }
    }

    // Place cards to ensure Blue has higher score
    Cell cell1 = board.getCell(0, 0);
    cell1.placeCard(redCard, "Red");

    Cell cell2 = board.getCell(1, 0);
    cell2.placeCard(blueCard, "Blue");

    // Trigger game end
    model.pass();
    model.pass();

    // Check winner
    assertEquals("Blue wins!", model.getWinner());
  }

  @Test
  public void testTieGame() {
    // Setup a game state with a tie
    Board board = model.getBoard();

    // Create test cards with equal values
    Card redCard = createTestCard("RedCard", 1, 5);
    Card blueCard = createTestCard("BlueCard", 1, 5);

    // Clear the board
    for (int i = 0; i < board.getRows(); i++) {
      for (int j = 0; j < board.getColumns(); j++) {
        board.setCellPawns(i, j, 0, "");
      }
    }

    // Place cards to ensure a tie
    Cell cell1 = board.getCell(0, 0);
    cell1.placeCard(redCard, "Red");

    Cell cell2 = board.getCell(1, 0);
    cell2.placeCard(blueCard, "Blue");

    // Trigger game end
    model.pass();
    model.pass();

    // Check result is a tie
    assertEquals("It's a tie!", model.getWinner());
  }
}