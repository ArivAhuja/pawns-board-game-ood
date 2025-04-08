package cs3500.pawnsboard.provider.controller;

import cs3500.pawnsboard.provider.model.Player;
import cs3500.pawnsboard.provider.view.PawnsWorldGUIView;


/**
 * Interface for listening to model events in the PawnsWorld game.
 * Implementations of this interface are for when controllers
 * react to the model changes.
 */
public interface ModelListener {

  /**
   * Called by the model to notify that it's the specified player's turn.
   *
   * @param p the player whose turn it is.
   */
  void yourTurn(Player p);

  /**
   * Called by the model to notify that the game is over.
   *
   */
  void gameOver();

  /**
   * Called by the model to prompt the controller to update its view.
   *
   */
  void updateView();


  /**
   * Returns the current view this controller is working with. Strictly used for testing purposes
   * to ensure the controller makes the necessary changes to the view after a change in game
   * state.
   *
   * @return a PawnsWorldGUIView object which is the assigned view to the controller.
   */
  PawnsWorldGUIView getView();



}


