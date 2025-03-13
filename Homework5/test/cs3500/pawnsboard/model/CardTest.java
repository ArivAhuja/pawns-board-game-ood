package cs3500.pawnsboard.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Test class for the Card class.
 */
public class CardTest {

  private char[][] validGrid;

  @Before
  public void setUp() {
    // Create a valid 5x5 influence grid with 'C' in the center
    validGrid = new char[][]{
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'I', 'I', 'I', 'X'},
            {'X', 'I', 'C', 'I', 'X'},
            {'X', 'I', 'I', 'I', 'X'},
            {'X', 'X', 'X', 'X', 'X'}
    };
  }

  @Test
  public void testValidCardConstruction() {
    Card card = new Card("TestCard", 2, 5, validGrid);
    assertEquals("TestCard", card.getName());
    assertEquals(2, card.getCost());
    assertEquals(5, card.getValue());
    assertArrayEquals(validGrid, card.getInfluenceGrid());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullNameThrowsException() {
    new Card(null, 2, 5, validGrid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCostLowThrowsException() {
    new Card("TestCard", 0, 5, validGrid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCostHighThrowsException() {
    new Card("TestCard", 4, 5, validGrid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidValueThrowsException() {
    new Card("TestCard", 2, 0, validGrid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullGridThrowsException() {
    new Card("TestCard", 2, 5, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongGridDimensionsThrowsException() {
    char[][] smallGrid = new char[4][5];
    new Card("TestCard", 2, 5, smallGrid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMissingCenterCellThrowsException() {
    char[][] gridWithoutCenter = new char[][]{
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'I', 'I', 'I', 'X'},
            {'X', 'I', 'X', 'I', 'X'}, // Center is 'X' instead of 'C'
            {'X', 'I', 'I', 'I', 'X'},
            {'X', 'X', 'X', 'X', 'X'}
    };
    new Card("TestCard", 2, 5, gridWithoutCenter);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMultipleCenterCellsThrowsException() {
    char[][] gridWithMultipleCs = new char[][]{
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'I', 'I', 'I', 'X'},
            {'X', 'I', 'C', 'I', 'X'},
            {'X', 'I', 'C', 'I', 'X'}, // Another 'C' on row 3, col 2
            {'X', 'X', 'X', 'X', 'X'}
    };
    new Card("TestCard", 2, 5, gridWithMultipleCs);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCharactersInGridThrowsException() {
    char[][] gridWithInvalidChar = new char[][]{
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'Z', 'I', 'I', 'X'}, // 'Z' is invalid
            {'X', 'I', 'C', 'I', 'X'},
            {'X', 'I', 'I', 'I', 'X'},
            {'X', 'X', 'X', 'X', 'X'}
    };
    new Card("TestCard", 2, 5, gridWithInvalidChar);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNonUniformGridRowsThrowsException() {
    // Create a grid with row of different length
    char[][] nonUniformGrid = new char[5][];
    nonUniformGrid[0] = new char[]{'X', 'X', 'X', 'X', 'X'};
    nonUniformGrid[1] = new char[]{'X', 'I', 'I', 'I'};  // Only 4 chars
    nonUniformGrid[2] = new char[]{'X', 'I', 'C', 'I', 'X'};
    nonUniformGrid[3] = new char[]{'X', 'I', 'I', 'I', 'X'};
    nonUniformGrid[4] = new char[]{'X', 'X', 'X', 'X', 'X'};

    new Card("TestCard", 2, 5, nonUniformGrid);
  }

  @Test
  public void testValidBoundaryCosts() {
    // Test minimum and maximum valid costs
    Card minCostCard = new Card("MinCost", 1, 5, validGrid);
    Card maxCostCard = new Card("MaxCost", 3, 5, validGrid);

    assertEquals(1, minCostCard.getCost());
    assertEquals(3, maxCostCard.getCost());
  }

  @Test
  public void testMinimumValidValue() {
    Card minValueCard = new Card("MinValue", 2, 1, validGrid);
    assertEquals(1, minValueCard.getValue());
  }

  @Test
  public void testToString() {
    Card card = new Card("TestCard", 2, 5, validGrid);
    String expected = "TestCard (Cost: 2, Value: 5)";
    assertEquals(expected, card.toString());
  }

  @Test
  public void testDifferentValidPatterns() {
    // Test a card with only 'X's (except center 'C')
    char[][] allXGrid = new char[][]{
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'C', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'X', 'X', 'X', 'X'}
    };
    Card xCard = new Card("XCard", 1, 1, allXGrid);
    assertArrayEquals(allXGrid, xCard.getInfluenceGrid());

    // Test a card with only 'I's (except center 'C')
    char[][] allIGrid = new char[][]{
            {'I', 'I', 'I', 'I', 'I'},
            {'I', 'I', 'I', 'I', 'I'},
            {'I', 'I', 'C', 'I', 'I'},
            {'I', 'I', 'I', 'I', 'I'},
            {'I', 'I', 'I', 'I', 'I'}
    };
    Card iCard = new Card("ICard", 3, 10, allIGrid);
    assertArrayEquals(allIGrid, iCard.getInfluenceGrid());
  }
}