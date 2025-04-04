package cs3500.pawnsboard.model;

import java.util.List;

/**
 * An interface exposing only the observation methods for the PawnsBoard game.
 * Views should depend on this interface to prevent accidental mutations.
 */
public interface ReadonlyPawnsBoardModelI {
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