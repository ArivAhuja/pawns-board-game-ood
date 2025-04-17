package cs3500.pawnsboard.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for PawnsBoardVariantModel, focusing on upgrading and devaluing influences,
 * effective card removal, and score computation with influence modifiers.
 */
class PawnsBoardVariantModelTest {

  private PawnsBoardVariantModel model;

  @BeforeEach
  void setUp() {
    // 3×3 board, deckSize=9, handSize=4
    model = new PawnsBoardVariantModel(3, 3, 9, 4);
  }

  @Test
  void upgradingInfluenceIncrementsModifier() {
    // Create a card with 'U' at relative positions
    char[][] grid = new char[5][5];
    grid[1][1] = 'U'; // maps to board cell (1-2,1-2)=( -1,-1 ) => skip
    grid[1][3] = 'U'; // ( -1, +1 ) => skip
    grid[3][1] = 'U'; // ( +1, -1 ) => target (2, 2)
    grid[3][3] = 'U'; // ( +1, +1 ) => target (2, 2)? adjacent for testing
    Card upgradeCard = new Card("UCard", 1, 1, grid);

    // Apply influence centered at (1,1) for red
    model.applyInfluence(1, 1, upgradeCard, "red");

    // The cell at (2,2) should have received two U influences
    Cell c = model.getBoard().getCell(2, 2);
    assertEquals(2, c.getInfluenceModifier(),
            "Upgrading influence did not accumulate correctly");
  }

  @Test
  void devaluingInfluenceDecrementsModifier() {
    char[][] grid = new char[5][5];
    grid[1][2] = 'D'; // ( -1, 0 ) => target (0, 2)
    grid[3][2] = 'D'; // ( +1, 0 ) => target (2, 2)
    Card devalueCard = new Card("DCard", 1, 1, grid);

    model.applyInfluence(1, 1, devalueCard, "blue");

    Cell top = model.getBoard().getCell(0, 2);
    Cell bottom = model.getBoard().getCell(2, 2);
    assertEquals(-1, top.getInfluenceModifier(),
            "Devaluing influence at top cell was not applied");
    assertEquals(-1, bottom.getInfluenceModifier(),
            "Devaluing influence at bottom cell was not applied");
  }

  @Test
  void updateCardEffectiveValueRemovesCardWhenDevaluedToZeroOrBelow() {
    // Prepare a cell with a card and negative influence to force removal
    Cell cell = model.getBoard().getCell(1, 1);
    Card c = new Card("Test", 2, 3, new char[5][5]);
    // Place the card on the cell
    cell.placeCard(c, "red");

    // Manually set a large negative influence modifier
    cell.addInfluence(-5);
    assertTrue(cell.getInfluenceModifier() < 0);

    // Now trigger update by calling applyInfluence with a no‑op grid
    Card noop = new Card("NoOp", 0, 1, new char[5][5]);
    model.applyInfluence(1, 1, noop, "red");

    // After removal, the card should be null and pawnCount == cost (3)
    assertNull(cell.getCard(), "Card should be removed when effective value <= 0");
    assertEquals(3, cell.getPawnCount(), "Pawn count should match card cost after removal");
    assertEquals(0, cell.getInfluenceModifier(), "Influence should be reset after removal");
  }

  @Test
  void computeRowScoresAccountsForInfluence() {
    // Place card at (1,1) with base value 2
    Cell cell = model.getBoard().getCell(1, 1);
    Card c = new Card("V", 2, 1, new char[5][5]);
    cell.placeCard(c, "red");
    // Add positive influence
    cell.addInfluence(3);

    int[][] scores = model.computeRowScores();
    // row 1: red score = 2 + 3 = 5, blue = 0
    assertEquals(5, scores[1][0], "Row score did not include influence modifier");
    assertEquals(0, scores[1][1], "Blue’s row score should remain zero");

    // Negative influence should floor at zero effective value
    cell.resetInfluence();
    cell.addInfluence(-5);
    scores = model.computeRowScores();
    assertEquals(0, scores[1][0], "Negative effective card value should be treated as zero");
  }
}
