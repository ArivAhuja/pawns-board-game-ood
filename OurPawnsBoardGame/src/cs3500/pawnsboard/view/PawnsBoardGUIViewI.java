package cs3500.pawnsboard.view;

import java.awt.Component;

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
  void addFeatureListener(ViewFeatures features);

  /**
   * Clears the current selected card.
   */
  void clearSelectedCard();

  /**
   * Clears the current selected cell.
   */
  void clearSelectedCell();

  /**
   * Returns the underlying Component that can be used as a parent for dialogs.
   * @return The Component that represents this view
   */
  Component getDialogParent();

  /**
   * Sets the color scheme for the panel.
   * @param colorScheme the colorScheme desired.
   */
  void setColorScheme(ColorScheme colorScheme);

}