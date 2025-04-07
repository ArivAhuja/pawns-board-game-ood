package cs3500.pawnsboard.view;

/**
 * Provides callbacks for user interactions in the Pawns Board GUI.
 */
public interface ViewFeatures {

  /**
   * Called when a key press indicates that the player wants to pass.
   */
  void passTurn();

  /**
   * Called when a card is clicked.
   * @param cardIndex the card index from the hand.
   */
  void selectedCard(int cardIndex);

  /**
   * Called when a cell is clicked.
   * @param row the row on the board.
   * @param col the column on the board.
   */
  void selectedCell(int row, int col);

  /**
   * Attempts to place a card based on the selected inputs.
   * @param row represents the current row selected
   * @param col represents the current column selected
   * @param cardIndex represents the current card selected
   */
  void placeAttempt(int row, int col, int cardIndex);

}