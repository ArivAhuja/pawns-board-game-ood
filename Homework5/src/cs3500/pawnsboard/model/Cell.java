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

  /**
   * Deep copies a Cell object.
   * @param other the Cell to copy.
   */
  public Cell(Cell other) {
    this.pawnCount = other.pawnCount;
    this.owner = other.owner;
    this.card = (other.card != null) ? new Card(other.card) : null; // Uses Card copy constructor
  }

  /**
   * Creates a deep copy of the cell.
   * @return a new Cell instance with the same properties.
   */
  public Cell clone() {
    return new Cell(this); // Uses copy constructor
  }

  /**
   * Gets the pawn count of the cell.
   * @return the number of pawns in the cell.
   */
  public int getPawnCount() {
    return pawnCount;
  }

  /**
   * Sets the pawn count of the cell.
   * @param count the number of pawns (must be between 0 and 3).
   */
  public void setPawnCount(int count) {
    if (count < 0 || count > 3) {
      throw new IllegalArgumentException("Pawn count must be between 0 and 3.");
    }
    this.pawnCount = count;
  }

  /**
   * Gets the owner of the cell.
   * @return the owner ("Red", "Blue", or an empty string if no owner).
   */
  public String getOwner() {
    return owner;
  }

  /**
   * Sets the owner of the cell.
   * @param owner the owner ("Red", "Blue", or an empty string).
   */
  public void setOwner(String owner) {
    if (!owner.equals("Red") && !owner.equals("Blue") && !owner.isEmpty()) {
      throw new IllegalArgumentException("Owner must be 'Red', 'Blue', or an empty string.");
    }
    this.owner = owner;
  }

  /**
   * Gets the card placed in the cell, if any.
   * @return the Card placed in the cell, or null if none.
   */
  public Card getCard() {
    return card;
  }

  /**
   * Places a card in the cell.
   * When a card is placed, the pawn count is reset.
   * @param card the card to place.
   * @param owner the owner of the card.
   */
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

  /**
   * Increments the pawn count by one, if it is less than 3.
   */
  public void incrementPawnCount() {
    if (pawnCount < 3) {
      pawnCount++;
    }
  }
}