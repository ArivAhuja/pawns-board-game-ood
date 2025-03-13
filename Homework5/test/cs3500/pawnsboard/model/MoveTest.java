package cs3500.pawnsboard.model;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for the Move class to ensure it correctly represents
 * move coordinates and card indexes, and properly implements equals and hashCode.
 */
public class MoveTest {

  @Test
  public void testConstructor() {
    Move move = new Move(1, 2, 3);
    assertEquals(1, move.row);
    assertEquals(2, move.col);
    assertEquals(3, move.cardIndex);
  }

  @Test
  public void testToString() {
    Move move = new Move(1, 2, 3);
    assertEquals("Place card index 3 at (1, 2)", move.toString());
  }

  @Test
  public void testEquals_SameObject() {
    Move move = new Move(1, 2, 3);
    assertTrue(move.equals(move));
  }

  @Test
  public void testEquals_EqualMoves() {
    Move move1 = new Move(1, 2, 3);
    Move move2 = new Move(1, 2, 3);
    assertTrue(move1.equals(move2));
    assertTrue(move2.equals(move1));
  }

  @Test
  public void testEquals_DifferentRow() {
    Move move1 = new Move(1, 2, 3);
    Move move2 = new Move(4, 2, 3);
    assertFalse(move1.equals(move2));
    assertFalse(move2.equals(move1));
  }

  @Test
  public void testEquals_DifferentColumn() {
    Move move1 = new Move(1, 2, 3);
    Move move2 = new Move(1, 4, 3);
    assertFalse(move1.equals(move2));
    assertFalse(move2.equals(move1));
  }

  @Test
  public void testEquals_DifferentCardIndex() {
    Move move1 = new Move(1, 2, 3);
    Move move2 = new Move(1, 2, 5);
    assertFalse(move1.equals(move2));
    assertFalse(move2.equals(move1));
  }

  @Test
  public void testEquals_Null() {
    Move move = new Move(1, 2, 3);
    assertFalse(move.equals(null));
  }

  @Test
  public void testEquals_DifferentClass() {
    Move move = new Move(1, 2, 3);
    assertFalse(move.equals("Not a Move"));
  }

  @Test
  public void testHashCode_EqualObjects() {
    Move move1 = new Move(1, 2, 3);
    Move move2 = new Move(1, 2, 3);
    assertEquals(move1.hashCode(), move2.hashCode());
  }

  @Test
  public void testHashCode_DifferentObjects() {
    Move move1 = new Move(1, 2, 3);
    Move move2 = new Move(3, 4, 5);
    assertNotEquals(move1.hashCode(), move2.hashCode());
  }

  @Test
  public void testHashCode_ConsistencyAcrossCalls() {
    Move move = new Move(1, 2, 3);
    int hash1 = move.hashCode();
    int hash2 = move.hashCode();
    assertEquals(hash1, hash2);
  }

  @Test
  public void testNegativeValues() {
    Move move = new Move(-1, -2, -3);
    assertEquals(-1, move.row);
    assertEquals(-2, move.col);
    assertEquals(-3, move.cardIndex);
  }

  @Test
  public void testZeroValues() {
    Move move = new Move(0, 0, 0);
    assertEquals(0, move.row);
    assertEquals(0, move.col);
    assertEquals(0, move.cardIndex);
  }

  @Test
  public void testLargeValues() {
    Move move = new Move(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    assertEquals(Integer.MAX_VALUE, move.row);
    assertEquals(Integer.MAX_VALUE, move.col);
    assertEquals(Integer.MAX_VALUE, move.cardIndex);
  }
}