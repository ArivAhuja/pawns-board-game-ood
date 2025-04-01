package cs3500.pawnsboard.model;


/**
 * Represents the actions a player can perform.
 * Machine players will compute moves via a strategy,
 * whereas human players wait for view events.
 */
public interface PlayerActions {
  /**
   * Returns the player's color.
   * @return the color as a String.
   */
  String getColor();

  /**
   * For machine players, computes the next move given the current game state.
   * For human players, this method is not used.
   * @param model the current read-only model.
   * @return the computed Move, or null if not applicable.
   */
  default Move getNextMove(ReadonlyPawnsBoardModelI model) {
    return null;
  }
}