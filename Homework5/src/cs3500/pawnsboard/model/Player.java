package cs3500.pawnsboard.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Player class that tracks a player's color, deck, and hand.
 */
public class Player implements PlayerI {
  private final String color;
  private List<Card> hand;

  /**
   * Constructs a new Player.
   * @param color The player's color.
   * @param deck The list of cards to use (deck configuration).
   * @param handSize The starting hand size.
   */
  public Player(String color, List<Card> deck, int handSize) {
    this.color = color;
    this.hand = new ArrayList<>();
    // deal the starting hand
    for (int i = 0; i < handSize && !deck.isEmpty(); i++) {
      hand.add(deck.remove(0));
    }
  }

  public String getColor() {
    return color;
  }

  public List<Card> getHand() {
    return new ArrayList<>(hand);
  }

  public void removeCardFromHand(Card card) {
    hand.remove(card);
  }

  public void drawCard(Card card) {
    hand.add(card);
  }
}
