package cs3500.pawnsboard.model;

/**
 * An interface exposing only the observation methods for the PawnsBoard game.
 * Views should depend on this interface to prevent accidental mutations.
 */
public interface ReadonlyPawnsBoardModelI {

  /**
   * Gets the width of the board.
   * @return the width of the board
   */
  int getWidth();

  /**
   * Gets the height of the board.
   * @return the height of the board
   */
  int getHeight();

  /**
   * Gets the contents of the cell.
   * @return the cell object with contents
   */
  Cell getCell(int row, int col);

  /**
   * Return the owner of the cell at the given row and column.
   * @param row the row
   * @param col the column
   * @return either "red" or "blue" if the cell is owned by a player,
   */
  String getCellOwner(int row, int col);

  /**
   * Returns the color of the current player.
   * @return A string representing the current player's color ("red" or "blue")
   */
  String getCurrentPlayerColor();

  /**
   * Returns the current board.
   */
  Board getBoard();

  /**
   * Returns a deep copy of the current board.
   */
  Board cloneBoard();

  /**
   * Returns true if the game is over.
   */
  boolean isGameOver();

  /**
   * Computes and returns the overall scores for both players.
   * Index 0 is Red's score; index 1 is Blue's score.
   */
  int[] computeScores();

  /**
   * Computes and returns the row-by-row scores.
   * For each row, index 0 is Red's row score and index 1 is Blue's row score.
   */
  int[][] computeRowScores();

  /**
   * Determines the winner of the game.
   * @return "Red wins!", "Blue wins!", or "It's a tie!".
   */
  String getWinner();


  /**
   * Returns the handSize of the model.
   */
  int getHandSize();

}