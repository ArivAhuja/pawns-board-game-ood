package cs3500.pawnsboard.strategy;

import cs3500.pawnsboard.model.Move;
import cs3500.pawnsboard.model.PawnsBoardModel;

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
