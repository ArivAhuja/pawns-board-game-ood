package cs3500.pawnsboard.model;

/**
 * Represents a possible move in the game.
 */
public interface MoveI {


  /**
   * Returns string representation of a possible move.
   * @return string representation of a possible move.
   */
  String toString();

  /**
   * Compares two moves for equality.
   * @param o the object to compare to.
   * @return true if the moves are equal, false otherwise.
   */
  boolean equals(Object o);

  /**
   * Returns the hashcode of a move.
   * @return the hashcode of a move.
   */
  int hashCode();
}
