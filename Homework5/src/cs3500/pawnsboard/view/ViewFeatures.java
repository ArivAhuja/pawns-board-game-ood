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
   * @param CardIndex the card index from the hand.
   */
  void selectedCard(int CardIndex);

  /**
   * Called when a cell is clicked.
   * @param row the row on the board.
   * @param col the column on the board.
   * @param cardIndex current selectedCardIndex
   */
  void selectedCell(int row, int col, int cardIndex);

}