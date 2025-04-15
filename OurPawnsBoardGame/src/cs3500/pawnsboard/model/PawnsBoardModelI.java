package cs3500.pawnsboard.model;

/**
 * Interface for the mutable operations of the PawnsBoard game.
 * It extends the read-only interface so that all observation methods are available as well.
 */
public interface PawnsBoardModelI extends ReadOnlyPawnsBoardModelVariantI {
  /**
   * Processes a pass move by the current player.
   */
  void pass();

  /**
   * Applies influence to a specific cell on the board using a chosen card.
   *
   * @param row the row index of the cell to apply influence to
   * @param col the column index of the cell to apply influence to
   * @param chosenCard the card to use for applying influence
   * @param color the color representing the player applying the influence
   */
  void applyInfluence(int row, int col, Card chosenCard, String color);

  /**
   * Sets the count of consecutive passes.
   *
   * @param count the number of consecutive passes
   */
  void setConsecutivePasses(int count);

  /**
   * Switches the turn to the other player.
   */
  void switchTurn();


}