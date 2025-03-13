package cs3500.pawnsboard.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;


/**
 * Test class for the Board class to ensure it correctly initializes and manages
 * the game board with proper validation of cell positions and operations.
 */
public class BoardTest {

  @Test
  public void testConstructor_ValidDimensions() {
    Board board = new Board(3, 5);
    assertEquals(3, board.getRows());
    assertEquals(5, board.getColumns());

    // Verify all cells are properly initialized
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 5; j++) {
        Cell cell = board.getCell(i, j);
        assertNotNull(cell);
        assertEquals(0, cell.getPawnCount());
        assertEquals("", cell.getOwner());
        assertNull(cell.getCard());
      }
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_ZeroRows() {
    new Board(0, 5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_NegativeRows() {
    new Board(-1, 5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_ZeroColumns() {
    new Board(3, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_SingleColumn() {
    new Board(3, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_EvenColumns() {
    new Board(3, 4);
  }

  @Test
  public void testGetCell_ValidPosition() {
    Board board = new Board(3, 5);
    Cell cell = board.getCell(1, 2);
    assertNotNull(cell);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testGetCell_NegativeRow() {
    Board board = new Board(3, 5);
    board.getCell(-1, 2);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testGetCell_NegativeColumn() {
    Board board = new Board(3, 5);
    board.getCell(1, -1);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testGetCell_RowTooLarge() {
    Board board = new Board(3, 5);
    board.getCell(3, 2);
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testGetCell_ColumnTooLarge() {
    Board board = new Board(3, 5);
    board.getCell(1, 5);
  }

  @Test
  public void testIsValidPosition() {
    Board board = new Board(3, 5);

    // Valid positions
    assertTrue(board.isValidPosition(0, 0));
    assertTrue(board.isValidPosition(2, 4));
    assertTrue(board.isValidPosition(0, 4));
    assertTrue(board.isValidPosition(2, 0));

    // Invalid positions
    assertFalse(board.isValidPosition(-1, 0));
    assertFalse(board.isValidPosition(0, -1));
    assertFalse(board.isValidPosition(3, 0));
    assertFalse(board.isValidPosition(0, 5));
    assertFalse(board.isValidPosition(3, 5));
  }

  @Test
  public void testSetCellPawns_ValidInput() {
    Board board = new Board(3, 5);
    board.setCellPawns(1, 2, 2, "Red");

    Cell cell = board.getCell(1, 2);
    assertEquals(2, cell.getPawnCount());
    assertEquals("Red", cell.getOwner());
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testSetCellPawns_InvalidPosition() {
    Board board = new Board(3, 5);
    board.setCellPawns(3, 2, 2, "Red");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetCellPawns_InvalidPawnCount() {
    Board board = new Board(3, 5);
    board.setCellPawns(1, 2, 4, "Red");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetCellPawns_InvalidOwner() {
    Board board = new Board(3, 5);
    board.setCellPawns(1, 2, 2, "Green");
  }

  @Test
  public void testGetRows() {
    Board board = new Board(3, 5);
    assertEquals(3, board.getRows());

    Board largeBoard = new Board(10, 7);
    assertEquals(10, largeBoard.getRows());
  }

  @Test
  public void testGetColumns() {
    Board board = new Board(3, 5);
    assertEquals(5, board.getColumns());

    Board largeBoard = new Board(10, 7);
    assertEquals(7, largeBoard.getColumns());
  }

  @Test
  public void testLargeBoard() {
    Board board = new Board(100, 101);
    assertEquals(100, board.getRows());
    assertEquals(101, board.getColumns());
    assertTrue(board.isValidPosition(99, 100));
    assertFalse(board.isValidPosition(100, 100));
  }

  @Test
  public void testMinimumValidBoard() {
    Board board = new Board(1, 3);
    assertEquals(1, board.getRows());
    assertEquals(3, board.getColumns());
    assertTrue(board.isValidPosition(0, 2));
    assertFalse(board.isValidPosition(1, 0));
  }

  @Test
  public void testCellReference() {
    Board board = new Board(3, 5);
    Cell cell1 = board.getCell(1, 2);
    Cell cell2 = board.getCell(1, 2);

    // Both references should point to the same cell
    assertSame(cell1, cell2);

    // Modifying the cell through one reference should be visible through the other
    cell1.setPawnCount(2);
    assertEquals(2, cell2.getPawnCount());
  }
}