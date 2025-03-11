package cs3500.pawnsboard.model;

import java.util.List;

/**
 * Represents the public interface for the Pawns Board game model.
 */
public interface PawnsBoardModelI {

  /**
   * Returns the board.
   * @return the current Board.
   */
  Board getBoard();

  /**
   * Returns true if the game is over (i.e. both players have passed consecutively).
   * @return true if game over, false otherwise.
   */
  boolean isGameOver();

  /**
   * Returns the current player.
   * @return the Player whose turn it is.
   */
  Player getCurrentPlayer();

  /**
   * Processes a pass move by the current player.
   */
  void pass();

  /**
   * Checks if the current player's hand is empty.
   * If so, automatically passes.
   * @return true if an auto-pass occurred, false otherwise.
   */
  boolean autoPassIfHandEmpty();

  /**
   * Attempts to place a card from the current player's hand on the specified cell.
   *
   * @param row       The row of the target cell.
   * @param col       The column of the target cell.
   * @param cardIndex The index of the card in the player's hand.
   * @return true if the move was successful, false otherwise.
   */
  boolean placeCard(int row, int col, int cardIndex);

  /**
   * Enumerates all legal moves for the current player.
   *
   * @return a list of legal moves available.
   */
  List<Move> getLegalMoves();

  /**
   * Computes the overall scores for both players.
   *
   * @return an int array of length 2 where index 0 is Red's score and index 1 is Blue's score.
   */
  int[] computeScores();

  /**
   * Computes the row-by-row scores.
   * For each row, sums the value scores of cards owned by Red and Blue separately.
   *
   * @return a 2D int array where for each row i,
   *         result[i][0] is Red's row score and result[i][1] is Blue's row score.
   */
  int[][] computeRowScores();

  /**
   * Determines the winner based on the computed scores.
   *
   * @return "Red wins!", "Blue wins!", or "It's a tie!".
   */
  String getWinner();



}