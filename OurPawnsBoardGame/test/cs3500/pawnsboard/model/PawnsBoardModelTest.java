package cs3500.pawnsboard.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
    model = new PawnsBoardModel(3, 5, testDeck.size(), 3);
  }

  @Test
  public void testConstructorInitialization() {
    // Test board dimensions
    assertEquals(3, model.getBoard().getRows());
    assertEquals(5, model.getBoard().getColumns());

    // Test initial player turn (Red starts)
    assertEquals("red", model.getCurrentPlayerColor());

    // Test initial board state - first column has red pawns
    for (int row = 0; row < 3; row++) {
      Cell leftCell = model.getBoard().getCell(row, 0);
      assertEquals(1, leftCell.getPawnCount());
      assertEquals("red", leftCell.getOwner());

      // Last column has blue pawns
      Cell rightCell = model.getBoard().getCell(row, 4);
      assertEquals(1, rightCell.getPawnCount());
      assertEquals("blue", rightCell.getOwner());
    }

    // Test initial game state (not over)
    assertFalse(model.isGameOver());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithInvalidRows() {
    new PawnsBoardModel(0, 5, testDeck.size(), 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithInvalidColumns() {
    new PawnsBoardModel(3, 4, testDeck.size(), 3); // Columns must be odd
  }

  @Test
  public void testPass() {
    // Initially it's Red's turn
    assertEquals("red", model.getCurrentPlayerColor());

    // After passing, it should be Blue's turn
    model.pass();
    assertEquals("blue", model.getCurrentPlayerColor());

    // After another pass, back to Red
    model.pass();
    assertEquals("red", model.getCurrentPlayerColor());
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
    cell1.placeCard(redCard1, "red");

    Cell cell2 = board.getCell(0, 1);
    cell2.placeCard(blueCard1, "blue");

    Cell cell3 = board.getCell(1, 0);
    cell3.placeCard(redCard2, "red");

    Cell cell4 = board.getCell(1, 1);
    cell4.placeCard(blueCard2, "blue");

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
    cell1.placeCard(redCard1, "red");

    Cell cell2 = board.getCell(0, 1);
    cell2.placeCard(blueCard1, "blue");

    Cell cell3 = board.getCell(1, 0);
    cell3.placeCard(redCard2, "red");

    Cell cell4 = board.getCell(1, 1);
    cell4.placeCard(blueCard2, "blue");

    // Compute scores
    int[] scores = model.computeScores();

    // Row 0: Red has 5, Blue has 2 -> Red wins row, scores 5
    // Row 1: Red has 3, Blue has 4 -> Blue wins row, scores 4
    // Row 2: No cards, no score
    assertEquals(5, scores[0]); // Red's score
    assertEquals(4, scores[1]); // Blue's score
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
    cell1.placeCard(redCard, "red");

    Cell cell2 = board.getCell(1, 0);
    cell2.placeCard(blueCard, "blue");

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
    cell1.placeCard(redCard, "red");

    Cell cell2 = board.getCell(1, 0);
    cell2.placeCard(blueCard, "blue");

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
    cell1.placeCard(redCard, "red");

    Cell cell2 = board.getCell(1, 0);
    cell2.placeCard(blueCard, "blue");

    // Trigger game end
    model.pass();
    model.pass();

    // Check result is a tie
    assertEquals("It's a tie!", model.getWinner());
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
    board.setCellPawns(1, 1, 1, "red");

    // Create a card with specific influence pattern
    Card testCard = createTestCard("TestCard", 1, 5);

    // Apply influence
    model.applyInfluence(1, 1, testCard, "red");

    // Check influence effects - the card's pattern should add pawns at (0,1), (1,0), (1,2), (2,1)
    // Check that pawns were added where expected
    Cell topCell = board.getCell(0, 1);
    Cell leftCell = board.getCell(1, 0);
    Cell rightCell = board.getCell(1, 2);
    Cell bottomCell = board.getCell(2, 1);

    assertEquals(1, topCell.getPawnCount());
    assertEquals("red", topCell.getOwner());

    assertEquals(1, leftCell.getPawnCount());
    assertEquals("red", leftCell.getOwner());

    assertEquals(1, rightCell.getPawnCount());
    assertEquals("red", rightCell.getOwner());

    assertEquals(1, bottomCell.getPawnCount());
    assertEquals("red", bottomCell.getOwner());
  }

  @Test
  public void testInfluenceOnOpponentPawns() {
    // Set up test condition: Red has a pawn at (1,1), Blue has a pawn at (1,2)
    Board board = model.getBoard();
    board.setCellPawns(1, 1, 1, "red");
    board.setCellPawns(1, 2, 1, "blue");

    // Create a card with specific influence pattern
    Card testCard = createTestCard("TestCard", 1, 5);

    // Apply influence
    model.applyInfluence(1, 1, testCard, "red");

    // Check that Blue's pawn at (1,2) is now Red's
    Cell rightCell = board.getCell(1, 2);
    assertEquals("red", rightCell.getOwner());
    assertEquals(1, rightCell.getPawnCount());
  }

  @Test
  public void testInfluenceOnExistingPawns() {
    // Set up: Red has 1 pawn at (1,1) and 2 pawns at (1,2)
    Board board = model.getBoard();
    board.setCellPawns(1, 1, 1, "red");
    board.setCellPawns(1, 2, 2, "red");

    // Create a card with specific influence pattern
    Card testCard = createTestCard("TestCard", 1, 5);

    // Apply influence
    model.applyInfluence(1, 1, testCard, "red");

    // Cell at (1,2) should now have 3 pawns (2+1, max is 3)
    Cell rightCell = board.getCell(1, 2);
    assertEquals(3, rightCell.getPawnCount());
    assertEquals("red", rightCell.getOwner());
  }

  @Test
  public void testInfluenceWithBluePlayer() {
    // Setup: Blue has a pawn at (1,3)
    Board board = model.getBoard();
    for (int i = 0; i < board.getRows(); i++) {
      for (int j = 0; j < board.getColumns(); j++) {
        board.setCellPawns(i, j, 0, "");
      }
    }
    board.setCellPawns(1, 3, 1, "blue");

    // Create a card with specific influence pattern
    Card testCard = createTestCard("TestCard", 1, 5);

    // Apply influence for Blue
    model.applyInfluence(1, 3, testCard, "blue");

    // Check the cells have Blue pawns in the appropriate places
    Cell topCell = board.getCell(0, 3);
    Cell leftCell = board.getCell(1, 2);
    Cell rightCell = board.getCell(1, 4);
    Cell bottomCell = board.getCell(2, 3);

    // Check cells if they're valid positions
    if (board.isValidPosition(0, 3)) {
      assertEquals(1, topCell.getPawnCount());
      assertEquals("blue", topCell.getOwner());
    }

    if (board.isValidPosition(1, 2)) {
      assertEquals(1, leftCell.getPawnCount());
      assertEquals("blue", leftCell.getOwner());
    }

    if (board.isValidPosition(1, 4)) {
      assertEquals(1, rightCell.getPawnCount());
      assertEquals("blue", rightCell.getOwner());
    }

    if (board.isValidPosition(2, 3)) {
      assertEquals(1, bottomCell.getPawnCount());
      assertEquals("blue", bottomCell.getOwner());
    }
  }
}