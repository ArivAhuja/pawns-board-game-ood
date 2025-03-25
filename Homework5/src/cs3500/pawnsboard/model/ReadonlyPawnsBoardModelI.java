package cs3500.pawnsboard.model;

import java.util.List;

/**
 * An interface exposing only the observation methods for the PawnsBoard game.
 * Views should depend on this interface to prevent accidental mutations.
 */
public interface ReadonlyPawnsBoardModelI {
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
   * Returns the current player.
   */
  Player getCurrentPlayer();

  /**
   * Returns the list of legal moves for the current player.
   */
  List<Move> getLegalMoves();

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
   * Gets the current player hand of cards.
   */
  List<Card> getHand();

  /**
   * Determines if it is legal for the current player to play the card (specified by hand index)
   * at the given cell coordinates.
   *
   * @param row       the row coordinate of the target cell.
   * @param col       the column coordinate of the target cell.
   * @param cardIndex the index of the card in the current player's hand.
   * @return true if the move is legal, false otherwise.
   */
  boolean isLegalMove(int row, int col, int cardIndex);
}