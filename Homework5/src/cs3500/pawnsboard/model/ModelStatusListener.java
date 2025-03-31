package cs3500.pawnsboard.model;

/**
 * Interface for objects that wish to be notified about game status changes in the model.
 */
public interface ModelStatusListener {
  /**
   * Called when the active player changes.
   */
  void turnChanged();

  /**
   * Called when the game is over.
   * @param result a message indicating the outcome (e.g., "Red wins!", "Blue wins!", or "It's a tie!").
   */
  void gameOver(String result);
}