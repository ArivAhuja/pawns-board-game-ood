package cs3500.pawnsboard.controller;

/**
 * Interface for a controller that manages the interaction between the
 * PawnsBoardModel and the user interface.
 */
public interface PawnsBoardControllerI {

  /**
   * Starts the interactive game loop.
   * Each turn, the controller:
   *  - Checks if the current player's hand is empty and auto-passes if so.
   *  - Checks if there are no legal moves available and auto-passes if so.
   *  - Otherwise, prompts the player for input.
   * Valid commands are:
   *   - "pass" (to pass the turn)
   *   - "place cardIndex row col" (to attempt a move)
   */
  void startGame();
}