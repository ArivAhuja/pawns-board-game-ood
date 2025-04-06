package cs3500.pawnsboard.model;

import java.util.List;

/**
 * The interface for a player in the Pawns Board game.
 */
public interface PlayerI {

  /**
   * Returns the player's color.
   *
   * @return the player's color.
   */
  String getColor();

  /**
   * Returns the player's hand.
   *
   * @return a list of Card objects representing the player's hand.
   */
  List<Card> getHand();

  /**
   * Removes the specified card from the player's hand.
   *
   * @param card the Card to remove.
   */
  void removeCardFromHand(Card card);

  /**
   * This function draws a card from the deck to the hand.
   */
  void drawCard();

  /**
   * Attempts to place a card from the current player's hand on the specified cell.
   * @param row       The row of the target cell.
   * @param col       The column of the target cell.
   * @param cardIndex The index of the card in the player's hand.
   * @return true if the move was successful, false otherwise.
   */
  boolean placeCard(int row, int col, int cardIndex);


}