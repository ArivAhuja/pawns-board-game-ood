package cs3500.pawnsboard.strategy;

import cs3500.pawnsboard.model.Move;
import cs3500.pawnsboard.model.PawnsBoardModel;

/**
 * The {@code PawnsBoardStrategy} interface defines the contract for implementing strategies
 * for a Pawns Board game. Implementations of this interface are responsible for analyzing
 * the current game state and selecting the most appropriate move for a given player color.
 *
 * <p>Implementations should consider various aspects of the game state provided by the
 * {@code PawnsBoardModel} and return a {@code Move} that represents the chosen row, column,
 * and hand index for the computer's move. If no valid move is possible, the strategy should
 * return {@code null}.
 *
 * <p>This interface can be extended or implemented by different strategies (e.g., aggressive,
 * defensive) to allow for varied AI behavior during gameplay.
 *
 * @see PawnsBoardModel
 * @see Move
 */
public interface PawnsBoardStrategy {
  /**
   * Given the current game model and the player’s color, returns a Move object that represents
   * the chosen move for the computer.
   *
   * @param model The current PawnsBoardModel (or a read-only view of it).
   * @param playerColor The color for which the strategy is making a decision ("Red" or "Blue").
   * @return A Move representing the row, column, and hand index, or null if no valid move exists.
   */
  Move chooseMove(PawnsBoardModel model, String playerColor);
}
