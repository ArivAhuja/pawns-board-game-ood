package cs3500.pawnsboard.provider.view;


import cs3500.pawnsboard.provider.model.Player;

/**
 * An interface that listens to changes in the model and creates an action based on the kind
 * of change.
 */
public interface ModelListener {

  /**
   * Notified by the model that it's this controller's player's turn.
   *
   * @param p the player whose turn it is.
   */
  void yourTurn(Player p);

  /**
   * Refreshes the view to display new changes made to the model after a player interaction.
   */
  void refresh();

  /**
   * Displays the given Strinvg message as a UI pop up.
   * @param msg String message to be displayed.
   */
  void showMessage(String msg);
}

