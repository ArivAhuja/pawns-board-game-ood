package cs3500.pawnsboard.model;

import java.util.Objects;

/**
 * A move represents a possible placement:
 * placing the card at index cardIndex from the current player's hand
 * onto cell (row, col).
 */
public class Move implements MoveI {
  public final int row;
  public final int col;
  public final int cardIndex;

  public Move(int row, int col, int cardIndex) {
    this.row = row;
    this.col = col;
    this.cardIndex = cardIndex;
  }

  @Override
  public String toString() {
    return "Place card index " + cardIndex + " at (" + row + ", " + col + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Move move = (Move) o;
    return row == move.row && col == move.col && cardIndex == move.cardIndex;
  }

  @Override
  public int hashCode() {
    return Objects.hash(row, col, cardIndex);
  }
}