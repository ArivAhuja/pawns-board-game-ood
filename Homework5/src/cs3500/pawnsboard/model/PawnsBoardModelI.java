package cs3500.pawnsboard.model;

/**
 * Interface for the mutable operations of the PawnsBoard game.
 * It extends the read-only interface so that all observation methods are available as well.
 */
public interface PawnsBoardModelI extends ReadonlyPawnsBoardModelI {
  /**
   * Processes a pass move by the current player.
   */
  void pass();


  /**
   * Starts the Game.
   */
  void startGame();

}