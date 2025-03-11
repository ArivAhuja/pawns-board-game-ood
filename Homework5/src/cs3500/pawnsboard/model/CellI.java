package cs3500.pawnsboard.model;

/**
 * The public interface for a Cell in the Pawns Board game.
 */
public interface CellI {

  /**
   * Gets the pawn count of the cell.
   * @return the number of pawns in the cell.
   */
  int getPawnCount();

  /**
   * Sets the pawn count of the cell.
   * @param count the number of pawns (must be between 0 and 3).
   */
  void setPawnCount(int count);

  /**
   * Gets the owner of the cell.
   * @return the owner ("Red", "Blue", or an empty string if no owner).
   */
  String getOwner();

  /**
   * Sets the owner of the cell.
   * @param owner the owner ("Red", "Blue", or an empty string).
   */
  void setOwner(String owner);

  /**
   * Gets the card placed in the cell, if any.
   * @return the Card placed in the cell, or null if none.
   */
  Card getCard();

  /**
   * Places a card in the cell.
   * When a card is placed, the pawn count is reset.
   * @param card the card to place.
   * @param owner the owner of the card.
   */
  void placeCard(Card card, String owner);

  /**
   * Checks if the cell is empty (no pawns and no card).
   * @return true if the cell is empty, false otherwise.
   */
  boolean isEmpty();

  /**
   * Checks if the cell currently contains any pawns.
   * @return true if there are one or more pawns, false otherwise.
   */
  boolean hasPawns();

  /**
   * Increments the pawn count by one, if it is less than 3.
   */
  void incrementPawnCount();
}