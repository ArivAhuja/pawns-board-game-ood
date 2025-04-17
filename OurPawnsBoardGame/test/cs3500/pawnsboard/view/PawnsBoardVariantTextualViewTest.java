package cs3500.pawnsboard.view;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import cs3500.pawnsboard.model.Board;
import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.Cell;
import cs3500.pawnsboard.model.PawnsBoardModel;
import cs3500.pawnsboard.model.PawnsBoardVariantModel;
import cs3500.pawnsboard.model.ReadOnlyPawnsBoardModelI;

/**
 * Tests for the PawnsBoardVariantTextualView class.
 */
public class PawnsBoardVariantTextualViewTest {
  private ReadOnlyPawnsBoardModelI model;
  private PawnsBoardVariantTextualView view;
  private ByteArrayOutputStream outContent;
  private PrintStream originalOut;

  /**
   * Sets up the test environment before each test.
   */
  @Before
  public void setUp() {
    // Create a test deck
    List<Card> testDeck = createTestDeck();

    // Create a default model with a 3x3 board
    model = new PawnsBoardVariantModel(3, 3, testDeck.size(), 3);

    // Create the view
    view = new PawnsBoardVariantTextualView(model);

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
            {'X', 'X', 'U', 'X', 'X'},
            {'X', 'I', 'C', 'I', 'X'},
            {'X', 'X', 'D', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'}
    };

    char[][] grid2 = {
            {'X', 'X', 'I', 'X', 'X'},
            {'X', 'X', 'U', 'X', 'X'},
            {'I', 'U', 'C', 'D', 'I'},
            {'X', 'X', 'D', 'X', 'X'},
            {'X', 'X', 'I', 'X', 'X'}
    };

    // Add cards to the deck
    for (int i = 0; i < 10; i++) {
      deck.add(new Card("Card" + (i + 1), i % 3 + 1, i % 5 + 1, i % 2 == 0 ? grid1 : grid2));
    }

    return deck;
  }

  /**
   * Restore the original System.out after each test.
   */
  @After
  public void restoreStreams() {
    System.setOut(originalOut);
  }

  /**
   * Test the constructor with a null model.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithNullModel() {
    new PawnsBoardVariantTextualView(null);
  }

  /**
   * Test rendering the initial board state.
   */
  @Test
  public void testRenderInitialBoard() {
    view.render(model.getBoard());
    String output = outContent.toString();

    // Check that the output contains the expected cell format
    Assert.assertTrue("Output should contain cell format markers",
            output.contains("[\"P\", 1, 0]") || output.contains("[\"_\", \"_\", \"_\"]"));

    // Check that player scores are displayed
    Assert.assertTrue("Output should contain Red player scores", output.contains("Red Player Scores:"));
    Assert.assertTrue("Output should contain Blue player scores", output.contains("Blue Player Scores:"));
  }

  /**
   * Test rendering a board with empty cells.
   */
  @Test
  public void testRenderBoardWithEmptyCells() {
    // Create a board with some empty cells
    Board board = new Board(2, 3);

    view.render(board);
    String output = outContent.toString();

    // All cells should be empty with the format ["_", "_", "_"]
    Assert.assertTrue("Empty cells should be formatted properly",
            output.contains("[\"_\", \"_\", \"_\"]"));

    // Count occurrences of empty cell format
    int emptyCount = countOccurrences(output, "[\"_\", \"_\", \"_\"]");
    Assert.assertEquals("Should have 4 empty cells in a 2x2 board", 6, emptyCount);
  }

  /**
   * Test rendering a board with pawns.
   */
  @Test
  public void testRenderBoardWithPawns() {
    Board board = model.getBoard();

    // Add some pawns with influence
    Cell cell = board.getCell(1, 1);
    cell.setPawnCount(2);
    cell.setOwner("red");
    cell.addInfluence(3);

    view.render(board);
    String output = outContent.toString();

    // Check that pawn cell is formatted correctly with influence
    Assert.assertTrue("Pawn cell should show correct count and influence",
            output.contains("[\"P\", 2, 3]"));
  }

  /**
   * Test rendering a board with cards.
   */
  @Test
  public void testRenderBoardWithCards() {
    Board board = model.getBoard();

    // Create a card and place it
    char[][] grid = {
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'U', 'X', 'X'},
            {'X', 'I', 'C', 'I', 'X'},
            {'X', 'X', 'D', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'}
    };
    Card card = new Card("TestCard", 2, 5, grid);

    Cell cell = board.getCell(1, 1);
    cell.placeCard(card, "red");
    cell.addInfluence(2);

    view.render(board);
    String output = outContent.toString();

    // Check that card cell is formatted correctly with value and influence
    Assert.assertTrue("Card cell should show correct value and influence",
            output.contains("[\"C\", 5, 2]"));
  }

  /**
   * Test rendering a board with a mix of cards, pawns, and empty cells.
   */
  @Test
  public void testRenderMixedBoard() {
    Board board = model.getBoard();

    // Create a card
    char[][] grid = {
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'U', 'X', 'X'},
            {'X', 'I', 'C', 'I', 'X'},
            {'X', 'X', 'D', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'}
    };
    Card card = new Card("TestCard", 2, 5, grid);

    // Setup mixed board with card, pawns, and empty cells
    board.getCell(0, 0).placeCard(card, "red");
    board.getCell(0, 0).addInfluence(1);

    board.getCell(1, 1).setPawnCount(3);
    board.getCell(1, 1).setOwner("blue");
    board.getCell(1, 1).addInfluence(-2);

    view.render(board);
    String output = outContent.toString();

    // Check for card, pawn, and empty cell formats
    Assert.assertTrue("Card cell should be formatted correctly",
            output.contains("[\"C\", 5, 1]"));
    Assert.assertTrue("Pawn cell should be formatted correctly",
            output.contains("[\"P\", 3, -2]"));
    Assert.assertTrue("Empty cell should be formatted correctly",
            output.contains("[\"_\", \"_\", \"_\"]"));
  }

  /**
   * Test the player scores display format.
   */
  @Test
  public void testPlayerScoresDisplay() {
    // Setup a board with cards to generate scores
    Board board = model.getBoard();

    char[][] grid = {
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'C', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'}
    };

    // Place red card in row 0
    Card redCard = new Card("RedCard", 1, 3, grid);
    board.getCell(0, 0).placeCard(redCard, "red");

    // Place blue card in row 1
    Card blueCard = new Card("BlueCard", 1, 4, grid);
    board.getCell(1, 1).placeCard(blueCard, "blue");

    view.render(board);
    String output = outContent.toString();

    // Check that scores are displayed correctly
    Assert.assertTrue("Red scores should be displayed",
            output.contains("Red Player Scores: [3, 0, 0]"));
    Assert.assertTrue("Blue scores should be displayed",
            output.contains("Blue Player Scores: [0, 4, 0]"));
  }

  /**
   * Helper method to count occurrences of a substring in a string.
   */
  private int countOccurrences(String str, String findStr) {
    int lastIndex = 0;
    int count = 0;

    while (lastIndex != -1) {
      lastIndex = str.indexOf(findStr, lastIndex);

      if (lastIndex != -1) {
        count++;
        lastIndex += findStr.length();
      }
    }

    return count;
  }

  /**
   * Test rendering a random/complex board and displaying the full output.
   * This test intentionally fails to show the complete output in the test log.
   */
  @Test
  public void testRandomBoardDisplay() {
    PawnsBoardModel model = new PawnsBoardVariantModel(3, 3, 12, 1);
    PawnsBoardVariantTextualView newView = new PawnsBoardVariantTextualView(model);
    Board board = model.getBoard();
    // Create cards with different influence patterns
    char[][] gridWithU = {
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'U', 'X', 'X'},
            {'X', 'I', 'C', 'I', 'X'},
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'}
    };

    char[][] gridWithD = {
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'C', 'X', 'X'},
            {'X', 'X', 'D', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'}
    };

    char[][] gridWithMixed = {
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'U', 'X', 'U', 'X'},
            {'X', 'I', 'C', 'I', 'X'},
            {'X', 'D', 'X', 'D', 'X'},
            {'X', 'X', 'X', 'X', 'X'}
    };

    // Create cards
    Card cardWithU = new Card("Upgrader", 2, 3, gridWithU);
    Card cardWithD = new Card("Devaluer", 1, 2, gridWithD);
    Card cardWithMixed = new Card("Hybrid", 3, 5, gridWithMixed);

    // Populate board with mixed content
    // Top row: one card with influence, one empty cell, one pawn with influence
    board.getCell(0, 0).placeCard(cardWithU, "red");
    board.getCell(0, 0).addInfluence(2);

    // Middle cell is empty
    board.getCell(0, 1).setPawnCount(0);
    board.getCell(0, 1).setOwner("");

    board.getCell(0, 2).setPawnCount(3);
    board.getCell(0, 2).setOwner("blue");
    board.getCell(0, 2).addInfluence(-1);

    // Middle row: pawn with no influence, card with negative influence, pawn with positive influence
    board.getCell(1, 0).setPawnCount(1);
    board.getCell(1, 0).setOwner("red");

    board.getCell(1, 1).placeCard(cardWithD, "blue");
    board.getCell(1, 1).addInfluence(-3);

    board.getCell(1, 2).setPawnCount(2);
    board.getCell(1, 2).setOwner("blue");
    board.getCell(1, 2).addInfluence(4);

    // Bottom row: pawn with influence, card with influence, empty cell
    board.getCell(2, 0).setPawnCount(2);
    board.getCell(2, 0).setOwner("red");
    board.getCell(2, 0).addInfluence(1);

    board.getCell(2, 1).placeCard(cardWithMixed, "red");
    board.getCell(2, 1).addInfluence(3);

    board.getCell(2, 2).setPawnCount(0);
    board.getCell(2, 2).setOwner("");

    // Set up new output capture for this test specifically
    ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(testOutput));

    // Render using the new view connected to the new model
    newView.render(board);
    String output = testOutput.toString();

    // Restore the original output stream
    System.setOut(originalOut);

    // Print the output to the test log by intentionally failing the test
    Assert.assertEquals("[\"C\", 3, 2] | [\"_\", \"_\", \"_\"] | [\"P\", 3, -1]\n" +
            "\n" +
            "[\"P\", 1, 0] | [\"C\", 2, -3] | [\"P\", 2, 4]\n" +
            "\n" +
            "[\"P\", 2, 1] | [\"C\", 5, 3] | [\"_\", \"_\", \"_\"]\n" +
            "\n" +
            "\n" +
            "Red Player Scores: [5, 0, 8]\n" +
            "Blue Player Scores: [0, 0, 0]\n", output);
  }
}