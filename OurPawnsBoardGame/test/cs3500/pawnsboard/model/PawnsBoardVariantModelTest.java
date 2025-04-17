package cs3500.pawnsboard.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.junit.Assert;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import cs3500.pawnsboard.view.PawnsBoardVariantTextualView;

/**
 * JUnit tests for PawnsBoardVariantModel, focusing on upgrading and devaluing influences,
 * effective card removal, and score computation with influence modifiers.
 */
public class PawnsBoardVariantModelTest {

  private PawnsBoardVariantModel model;
  private PawnsBoardVariantTextualView view;
  private ByteArrayOutputStream outContent;
  private PrintStream originalOut;

  @Before
  public void setUp() {
    // 3×3 board, deckSize=9, handSize=4
    model = new PawnsBoardVariantModel(3, 3, 9, 4);
    view = new PawnsBoardVariantTextualView(model);

    // Set up output capture for view tests
    outContent = new ByteArrayOutputStream();
    originalOut = System.out;
    System.setOut(new PrintStream(outContent));
  }

  @After
  public void restoreStreams() {
    System.setOut(originalOut);
  }

  @Test
  public void upgradingInfluenceIncrementsModifier() {
    // Create a card with 'U' at relative positions
    char[][] grid = {
            {'U', 'U', 'U', 'U', 'U'},
            {'U', 'U', 'U', 'U', 'U'},
            {'U', 'U', 'C', 'U', 'U'},
            {'U', 'U', 'U', 'U', 'U'},
            {'U', 'U', 'U', 'U', 'U'}
    };

    Card upgradeCard = new Card("UCard", 1, 1, grid);

    // Apply influence centered at (1,1) for red
    model.applyInfluence(1, 1, upgradeCard, "red");

    // The cell at (2,2) should have received one U influence from (3,1)
    Cell c = model.getBoard().getCell(2, 2);
    Assert.assertEquals("Upgrading influence",
            1, c.getInfluenceModifier());
  }

  @Test
  public void devaluingInfluenceDecrementsModifier() {
    char[][] grid = {
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'D', 'X', 'X'},
            {'X', 'X', 'C', 'X', 'X'},
            {'X', 'X', 'D', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'}
    };

    Card devalueCard = new Card("DCard", 1, 1, grid);

    model.applyInfluence(1, 1, devalueCard, "blue");

    Cell top = model.getBoard().getCell(0, 1);
    Cell bottom = model.getBoard().getCell(2, 1);
    Assert.assertEquals("Devaluing influence at top cell",
            -1, top.getInfluenceModifier());
    Assert.assertEquals("Devaluing influence at bottom cell",
            -1, bottom.getInfluenceModifier());
  }

  @Test
  public void updateCardEffectiveValueRemovesCardWhenDevaluedToZeroOrBelow() {
    // Create a card with value 1 (will be removed with -1 influence)
    char[][] cardGrid = {
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'C', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'}
    };

    // Create a devaluing card with D influence
    char[][] devalueGrid = {
            {'D', 'D', 'D', 'D', 'D'},
            {'D', 'D', 'D', 'D', 'D'},
            {'D', 'D', 'C', 'D', 'D'},
            {'D', 'D', 'D', 'D', 'D'},
            {'D', 'D', 'D', 'D', 'D'}
    };

    // Place the first card with value 1 at position (1,1)
    Cell targetCell = model.getBoard().getCell(1, 0);
    Card targetCard = new Card("LowValueCard", 1, 1, cardGrid);
    targetCell.placeCard(targetCard, "red");

    // Place the devaluing card at (2,2) - adjacent with D influence
    Card devalueCard = new Card("DevalueCard", 1, 3, devalueGrid);

    // Apply the devaluing card's influence
    model.applyInfluence(2, 2, devalueCard, "blue");

    // The target card should be removed by the devaluing influence
    Assert.assertNull("Card should be removed when effective value <= 0",
            targetCell.getCard());
    Assert.assertEquals("Pawn count should match card cost after removal",
            1, targetCell.getPawnCount());
    Assert.assertEquals("Influence should be 0 because 1 original than -1",
            0, targetCell.getInfluenceModifier());
  }

  @Test
  public void computeRowScoresAccountsForInfluence() {
    // Create a proper grid with center as 'C'
    char[][] grid = {
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'C', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'}
    };

    // Place card at (1,1) with base value 2
    Cell cell = model.getBoard().getCell(1, 1);
    Card c = new Card("V", 2, 2, grid);
    cell.placeCard(c, "red");

    // Add positive influence
    cell.addInfluence(3);

    int[][] scores = model.computeRowScores();
    // row 1: red score = 2 + 3 = 5, blue = 0
    Assert.assertEquals("Row score did not include influence modifier",
            5, scores[1][0]);
    Assert.assertEquals("Blue's row score should remain zero",
            0, scores[1][1]);

    // Negative influence should floor at zero effective value
    cell.resetInfluence();
    cell.addInfluence(-5);
    scores = model.computeRowScores();
    Assert.assertEquals("Negative effective card value should be treated as zero",
            0, scores[1][0]);
  }

  @Test
  public void mixedInfluenceCardTest() {
    // Create a card with both U and D influences plus I
    char[][] grid = {
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'U', 'X', 'X'},
            {'X', 'I', 'C', 'I', 'X'},
            {'X', 'X', 'D', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'}
    };

    Card mixedCard = new Card("MixedCard", 1, 1, grid);

    // Apply influence centered at (1,1) for red
    model.applyInfluence(1, 1, mixedCard, "red");

    // Check the cell above received positive influence
    Cell topCell = model.getBoard().getCell(0, 1);
    Assert.assertEquals("Upgrading influence not applied to top cell",
            1, topCell.getInfluenceModifier());

    // Check the cell below received negative influence
    Cell bottomCell = model.getBoard().getCell(2, 1);
    Assert.assertEquals("Devaluing influence not applied to bottom cell",
            -1, bottomCell.getInfluenceModifier());

    // Check the left and right cells received normal influence (pawns)
    Cell leftCell = model.getBoard().getCell(1, 0);
    Assert.assertEquals("Normal influence not applied to left cell",
            2, leftCell.getPawnCount());

    Cell rightCell = model.getBoard().getCell(1, 2);
    Assert.assertEquals("Normal influence not applied to right cell",
            1, rightCell.getPawnCount());
  }

  @Test
  public void testViewShowsInfluencesInCellFormat() {
    // Add influence to cells
    model.getBoard().getCell(0, 0).addInfluence(2);
    model.getBoard().getCell(0, 1).addInfluence(-1);

    // Render the board
    view.render(model.getBoard());
    String output = outContent.toString();

    // Check that influence values are shown in the cell format
    Assert.assertTrue("Output should show positive influence",
            output.contains("[\"P\", 1, 2]"));
  }

  @Test
  public void testViewAfterCardIsPlaced() {
    // Create and place a card
    char[][] grid = {
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'C', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'}
    };

    Card card = new Card("TestCard", 1, 3, grid);
    model.getBoard().getCell(1, 1).placeCard(card, "red");

    // Render the board
    view.render(model.getBoard());
    String output = outContent.toString();

    // Check that the card is shown with its value and influence
    Assert.assertTrue("Output should show card with correct value",
            output.contains("[\"C\", 3, 0]"));
  }

  @Test
  public void testViewShowsScoresWithInfluenceAccounted() {
    // Place a card and add influence
    char[][] grid = {
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'C', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'}
    };

    Card card = new Card("ScoreCard", 1, 3, grid);
    Cell cell = model.getBoard().getCell(0, 0);
    cell.placeCard(card, "red");
    cell.addInfluence(2);

    // Render the board
    view.render(model.getBoard());
    String output = outContent.toString();

    // Check the scores account for influence
    Assert.assertTrue("Player scores should include influence modifiers",
            output.contains("Red Player Scores: [5, 0, 0]"));
  }

  @Test
  public void testViewRendersBoardWithVariedInfluenceValues() {
    // Set up a complex board with various influence values
    Board board = model.getBoard();

    // Cell with positive influence and pawns
    board.getCell(0, 0).addInfluence(3);

    // Cell with negative influence and pawns
    board.getCell(0, 1).addInfluence(-2);

    // Cell with a card and positive influence
    char[][] grid = {
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'C', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'}
    };

    Card card1 = new Card("Card1", 1, 2, grid);
    board.getCell(1, 0).placeCard(card1, "red");
    board.getCell(1, 0).addInfluence(1);

    // Cell with a card and negative influence
    Card card2 = new Card("Card2", 1, 4, grid);
    board.getCell(1, 1).placeCard(card2, "blue");
    board.getCell(1, 1).addInfluence(-1);

    // Empty cell
    board.getCell(2, 2).setPawnCount(0);
    board.getCell(2, 2).setOwner("");

    // Render the board
    view.render(board);
    String output = outContent.toString();

    // Verify all cell types are rendered correctly
    Assert.assertTrue("Pawn cell with positive influence should be shown",
            output.contains("[\"P\", 1, 3]"));
    Assert.assertTrue("Card cell with positive influence should be shown",
            output.contains("[\"C\", 2, 1]"));
    Assert.assertTrue("Card cell with negative influence should be shown",
            output.contains("[\"C\", 4, -1]"));
    Assert.assertTrue("Empty cell should be shown",
            output.contains("[\"_\", \"_\", \"_\"]"));

    // Verify scores reflect the influence modifiers
    Assert.assertTrue("Red scores should include positive influence",
            output.contains("Red Player Scores:"));
    Assert.assertTrue("Blue scores should include reduced influence",
            output.contains("Blue Player Scores:"));
  }
}