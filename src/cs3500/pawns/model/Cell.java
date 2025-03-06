package cs3500.pawns.model;

/**
 * A Cell represents a single square on the board.
 * It holds the pawn count, owner information, and an optional placed card.
 */
public class Cell {
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

  /**
   * Gets the pawn count of the cell
   * @return the number of pawns in the cell
   */
  public int getPawnCount() {
    return pawnCount;
  }

  /**
   * Sets the pawn count ensuring it's between 0 and 3.
   *
   * @param count Number of pawns.
   */
  public void setPawnCount(int count) {
    if (count < 0 || count > 3) {
      throw new IllegalArgumentException("Pawn count must be between 0 and 3.");
    }
    this.pawnCount = count;
  }

  /**
   * Gets the owner of the cell, being either "Red" or "Blue", or an empty string if no owner.
   * @return the owner of the cell.
   */
  public String getOwner() {
    return owner;
  }

  /**
   * Sets the owner of the cell.
   * @param owner the owner of the cell.
   */
  public void setOwner(String owner) {
    if (!owner.equals("Red") && !owner.equals("Blue") && !owner.isEmpty()) {
      throw new IllegalArgumentException("Owner must be 'Red', 'Blue', or an empty string.");
    }
    this.owner = owner;
  }

  /**
   * Gets the card placed in the cell, if any.
   * @return The card placed in the cell, or null if no card.
   */
  public Card getCard() {
    return card;
  }

  /**
   * Places a card in the cell.
   * When a card is placed, the pawn count is reset.
   *
   * @param card  The card to place.
   * @param owner The owner of the card.
   */
  public void placeCard(Card card, String owner) {
    this.card = card;
    this.owner = owner;
    this.pawnCount = 0;
  }

  /**
   * Checks if the cell is empty (no pawns and no card).
   */
  public boolean isEmpty() {
    return pawnCount == 0 && card == null;
  }

  /**
   * Checks if the cell currently contains pawns.
   */
  public boolean hasPawns() {
    return pawnCount > 0;
  }

  /**
   * Increments the pawn count by one (if less than 3).
   * Doesn't increment above 3.
   */
  public void incrementPawnCount() {
    if (pawnCount < 3) {
      pawnCount++;
    }
  }
}