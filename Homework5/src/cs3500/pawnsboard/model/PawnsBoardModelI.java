package cs3500.pawnsboard.model;

/**
 * Interface for the mutable operations of the PawnsBoard game.
 * It extends the read-only interface so that all observation methods are available as well.
 */
public interface PawnsBoardModelI extends ReadonlyPawnsBoardModelI {
  /**
   * Processes a pass move by the current player.
   */
  void pass();

  /**
   * Checks if the current player's hand is empty or player has no legal moves.
   * If so, automatically passes.
   * @return true if an auto-pass occurred, false otherwise.
   */
  boolean checkAutoPass();

  /**
   * Attempts to place a card from the current player's hand on the specified cell.
   * @param row       The row of the target cell.
   * @param col       The column of the target cell.
   * @param cardIndex The index of the card in the player's hand.
   * @return true if the move was successful, false otherwise.
   */
  boolean placeCard(int row, int col, int cardIndex);
}