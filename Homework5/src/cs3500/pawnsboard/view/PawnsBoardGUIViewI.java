package cs3500.pawnsboard.view;

/**
 * Interface for a GUI view for the PawnsBoard game.
 */
public interface PawnsBoardGUIViewI {
  /**
   * Refreshes the entire view.
   */
  void refresh();

  /**
   * Makes the view visible or invisible.
   */
  void display(boolean show);

  /**
   * Adds a features listener (for mouse/keyboard events) to all panels.
   */
  void addFeaturesListener(Object features);
}