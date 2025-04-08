package cs3500.pawnsboard.provider.model;

import java.io.IOException;
import java.util.List;

/**
 * Read only version of the PawnsWorld interface. Only contains observer methods.
 */

public interface PawnsWorldReadOnly {

  /**
   * Starts the game and initializes hands.
   * @param deckRed deck for Red player.
   * @param deckBlue deck for Blue player.
   * @param handSize hand size for each player.
   * @throws IllegalArgumentException if either deck is null or handsize is greater than
   *          1/3 of the deck size.
   * @throws IllegalStateException if the game is already started.
   */
  void startGame(List<CustomCard> deckRed, List<CustomCard> deckBlue,
                 int handSize, boolean shuffle);

  /**
   * Gets height of the board.
   * @return height of the board.
   */
  int getHeight();

  /**
   * Gets width of the board.
   * @return width of the board.
   */
  int getWidth();

  /**
   * Returns the winner of the game.
   * @return winner player of the game.
   * @throws IllegalStateException if game is not over.
   */
  Player getWinner();

  /**
   * Gets the board element at the row and column.
   * @param row row index.
   * @param col column index.
   * @return BoardElement at the index.
   */
  BoardElement getElement(int row, int col);

  /**
   * Checks if the game is over.
   * @return if the game is over.
   */
  boolean isGameOver();

  /**
   * Method to get turn.
   * @return player whose current turn it is.
   */
  Player getTurn();

  /**
   * Total score to determine winner only counting max of each row.
   * @param player whose score is being calculated.
   * @return total score for given player.
   */
  int getTotalScore(Player player);

  /**
   * Get hand for player Red.
   * @return hand for player Red.
   */
  List<CustomCard> getRedHand();

  /**
   * Get hand for player Blue.
   * @return hand for player Blue.
   */
  List<CustomCard> getBlueHand();

  /**
   * Gets a copy of the current game board state.
   * @return a 2D array of the board of type BoardElement.
   * @throws IOException Required for file writing implementation.
   */
  BoardElement[][] getBoard() throws IOException;

  /**
   * Calculates the score of the given player at the specified row.
   * @param row row of the board.
   * @param player player whose score to be calculated.
   * @return an int value of the score.
   */
  int calculateScore(int row, Player player);

}
