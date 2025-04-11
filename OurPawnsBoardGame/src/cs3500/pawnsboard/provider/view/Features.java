package cs3500.pawnsboard.provider.view;



import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Represents the set of actions that the controller can request the view to perform.
 * This interface allows for communication between the controller and the view by
 * setting user interaction listeners and manipulating visual components.
 */
public interface Features {

  /**
   * Sets a listener for board cell selection.
   * This is typically triggered when the player clicks on a board cell.
   *
   * @param listener a  BiConsumer that accepts the selected cell's row and column indices.
   */
  void setCellSelectionListener(BiConsumer<Integer, Integer> listener);

  /**
   * Sets a listener for card selection in the player's hand.
   * This is triggered when the player selects a card to play.
   *
   * @param listener a Consumer that accepts the index of the selected card.
   */
  void setCardSelectionListener(Consumer<Integer> listener);

  /**
   * Enables or disables interactivity in the view.
   * Disabling the view typically occurs when it's not the player's turn.
   *
   * @param enabled true to enable interaction, false to disable it.
   */
  void setEnabled(boolean enabled);

  /**
   * Refreshes and updates the display of the player’s hand,
   * typically after a card is played or the game state changes.
   */
  void updateHand();

  /**
   * Returns the index of the currently selected card in the player's hand.
   *
   * @return the selected card index, or -1 if no card is selected.
   */
  int getSelectedCardIndex();
}


