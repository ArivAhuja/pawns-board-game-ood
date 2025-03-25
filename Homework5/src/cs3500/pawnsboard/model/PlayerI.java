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
   * This function adds the specified card to the hand.
   *
   * @param card the Card to add.
   */
  void drawCard(Card card);

}