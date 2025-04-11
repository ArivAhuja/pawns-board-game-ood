package cs3500.pawnsboard.provider.view;


import java.io.IOException;

/**
 * Represents the main view interface for the Pawns game.
 * Defines the essential GUI components and actions that the controller can invoke.
 */
public interface PawnsView {

  /**
   * Returns the panel that displays the game board.
   *
   * @return the board panel component
   */
  PawnsBoardPanel getBoardPanel();

  /**
   * Returns the panel that displays the current player's hand of cards.
   *
   * @return the hand panel component
   */
  PlayerHandPanel getHandPanel();

  /**
   * Refreshes and updates all components of the GUI view, such as the board and hand,
   * after a move or game state change. This ensures the displayed state matches the model.
   *
   * @throws IOException if an error occurs during rendering or data access
   */
  void updateView() throws IOException;
}


