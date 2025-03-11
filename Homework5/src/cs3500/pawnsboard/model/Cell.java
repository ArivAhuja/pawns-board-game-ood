package cs3500.pawnsboard.model;

/**
 * A Cell represents a single square on the board.
 * It holds the pawn count, owner information, and an optional placed card.
 */
public class Cell implements CellI {
  private int pawnCount;
  private String owner; // "Red" or "Blue" - empty string if no owner
  private Card card;    // a card placed in the cell, if any (null if no card)

  /**
   * Initializes a Cell with 0 pawns, no owner, and no card placed.
   */
  public Cell() {
    this.pawnCount = 0;
    this.owner = "";
    this.card = null;
  }

  public int getPawnCount() {
    return pawnCount;
  }

  public void setPawnCount(int count) {
    if (count < 0 || count > 3) {
      throw new IllegalArgumentException("Pawn count must be between 0 and 3.");
    }
    this.pawnCount = count;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    if (!owner.equals("Red") && !owner.equals("Blue") && !owner.isEmpty()) {
      throw new IllegalArgumentException("Owner must be 'Red', 'Blue', or an empty string.");
    }
    this.owner = owner;
  }

  public Card getCard() {
    return card;
  }

  public void placeCard(Card card, String owner) {
    this.card = card;
    this.owner = owner;
    this.pawnCount = 0;
  }

  public boolean isEmpty() {
    return pawnCount == 0 && card == null;
  }

  public boolean hasPawns() {
    return pawnCount > 0;
  }

  public void incrementPawnCount() {
    if (pawnCount < 3) {
      pawnCount++;
    }
  }
}