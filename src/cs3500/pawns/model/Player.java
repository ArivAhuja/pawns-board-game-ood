package cs3500.pawns.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Player class that tracks a player's color, deck, and hand.
 */
public class Player {
  private String color; // "Red" or "Blue"
  private List<Card> deck;
  private List<Card> hand;

  /**
   * Constructs a new Player.
   *
   * @param color The player's color.
   * @param deck The list of cards to use (deck configuration).
   * @param handSize The starting hand size.
   */
  public Player(String color, List<Card> deck, int handSize) {
    this.color = color;
    // create a shallow copy of the deck to avoid modifying the original deck
    this.deck = new ArrayList<>(deck);
    this.hand = new ArrayList<>();
    // deal the starting hand
    for (int i = 0; i < handSize && !this.deck.isEmpty(); i++) {
      hand.add(this.deck.remove(0));
    }
  }

  /**
   * Returns the player's color.
   * @return The player's color.
   */
  public String getColor() {
    return color;
  }

  /**
   * Returns the player's deck.
   * @return The player's deck.
   */
  public List<Card> getHand() {
    return hand;
  }

  /**
   * Removes the specified card from the hand.
   * @param card The card to remove.
   */
  public void removeCardFromHand(Card card) {
    hand.remove(card);
  }
}
