package cs3500.pawnsboard.provider.model;

import java.io.IOException;

/**
 * Implements the Pawns on the World Stage game. This interface consists of a mutable version
 * of PawnsWorld by extending PawnsWorldReadOnly and adding mutable methods.
 */
public interface PawnsWorld extends PawnsWorldReadOnly {

  /**
   * Place card at given indices and from the given index of the player's hand.
   * @param row index to be placed at.
   * @param col index to be placed at.
   * @param handIndex index for card to be drawn from.
   * @throws IllegalStateException if game is not started, if a card is already placed at given
   *          position, block does not have enough pawns, or does not belong to the player.
   * @throws IllegalArgumentException if row, col, or handIndex are invalid.
   */
  void placeCard(int row, int col, int handIndex);

  /**
   * Passes a turn.
   */
  void pass();

  /**
   * Places a given element at the specified row and col values.
   * @param row row value to be placed at.
   * @param col col value to be placed at.
   * @param element BoardElement to be placed.
   * @throws IOException Required for File Writing.
   */
  void placeElement(int row, int col, BoardElement element) throws IOException;

  /**
   * Switches which Player needs to make a move.
   */
  void switchTurn();

}
