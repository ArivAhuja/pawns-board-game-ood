package cs3500.pawnsboard.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Test class for the Cell class.
 */
public class CellTest {

  private Cell cell;
  private Card testCard;

  @Before
  public void setUp() {
    cell = new Cell();

    // Create a simple card for testing
    char[][] grid = new char[][]{
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'I', 'I', 'I', 'X'},
            {'X', 'I', 'C', 'I', 'X'},
            {'X', 'I', 'I', 'I', 'X'},
            {'X', 'X', 'X', 'X', 'X'}
    };
    testCard = new Card("TestCard", 2, 5, grid);
  }

  @Test
  public void testDefaultConstructor() {
    assertEquals(0, cell.getPawnCount());
    assertEquals("", cell.getOwner());
    assertNull(cell.getCard());
    assertTrue(cell.isEmpty());
    assertFalse(cell.hasPawns());
  }

  @Test
  public void testSetAndGetPawnCount() {
    cell.setPawnCount(2);
    assertEquals(2, cell.getPawnCount());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetPawnCountNegative() {
    cell.setPawnCount(-1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetPawnCountTooHigh() {
    cell.setPawnCount(4);
  }

  @Test
  public void testSetAndGetOwner() {
    cell.setOwner("Red");
    assertEquals("Red", cell.getOwner());

    cell.setOwner("Blue");
    assertEquals("Blue", cell.getOwner());

    cell.setOwner("");
    assertEquals("", cell.getOwner());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetInvalidOwner() {
    cell.setOwner("Green");
  }

  @Test
  public void testPlaceCard() {
    cell.placeCard(testCard, "Red");
    assertEquals(testCard, cell.getCard());
    assertEquals("Red", cell.getOwner());
    assertEquals(0, cell.getPawnCount());
  }

  @Test
  public void testIsEmptyWithPawns() {
    cell.setPawnCount(1);
    cell.setOwner("Red");
    assertFalse(cell.isEmpty());
  }

  @Test
  public void testIsEmptyWithCard() {
    cell.placeCard(testCard, "Blue");
    assertFalse(cell.isEmpty());
  }

  @Test
  public void testHasPawns() {
    assertFalse(cell.hasPawns());

    cell.setPawnCount(1);
    assertTrue(cell.hasPawns());

    cell.setPawnCount(0);
    assertFalse(cell.hasPawns());
  }

  @Test
  public void testIncrementPawnCount() {
    // Start at 0
    assertEquals(0, cell.getPawnCount());

    // First increment
    cell.incrementPawnCount();
    assertEquals(1, cell.getPawnCount());

    // Second increment
    cell.incrementPawnCount();
    assertEquals(2, cell.getPawnCount());

    // Third increment
    cell.incrementPawnCount();
    assertEquals(3, cell.getPawnCount());

    // Fourth increment should not change value (max is 3)
    cell.incrementPawnCount();
    assertEquals(3, cell.getPawnCount());
  }

  @Test
  public void testBoundaryPawnCounts() {
    // Test minimum valid pawn count
    cell.setPawnCount(0);
    assertEquals(0, cell.getPawnCount());

    // Test maximum valid pawn count
    cell.setPawnCount(3);
    assertEquals(3, cell.getPawnCount());
  }

  @Test
  public void testCellStateAfterPlacingCard() {
    // Setup cell with pawns
    cell.setPawnCount(3);
    cell.setOwner("Red");
    assertTrue(cell.hasPawns());

    // Place a card
    cell.placeCard(testCard, "Red");

    // Verify state changes
    assertEquals(0, cell.getPawnCount());
    assertEquals(testCard, cell.getCard());
    assertEquals("Red", cell.getOwner());
    assertFalse(cell.hasPawns());
    assertFalse(cell.isEmpty());
  }

  @Test
  public void testChangingOwnerWithoutPawns() {
    cell.setOwner("Red");
    assertEquals("Red", cell.getOwner());

    cell.setOwner("Blue");
    assertEquals("Blue", cell.getOwner());

    cell.setOwner("");
    assertEquals("", cell.getOwner());
  }

  @Test
  public void testPlaceCardChangesOwnership() {
    // Initial setup
    cell.setPawnCount(2);
    cell.setOwner("Red");

    // Place a card with different owner
    cell.placeCard(testCard, "Blue");

    // Verify ownership changed
    assertEquals("Blue", cell.getOwner());
  }

  @Test
  public void testIncrementPawnCountFromZero() {
    assertEquals(0, cell.getPawnCount());
    assertFalse(cell.hasPawns());

    cell.incrementPawnCount();

    assertEquals(1, cell.getPawnCount());
    assertTrue(cell.hasPawns());
  }
}