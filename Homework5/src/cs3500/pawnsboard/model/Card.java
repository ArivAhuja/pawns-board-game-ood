package cs3500.pawnsboard.model;

import java.util.List;

/**
 * Represents a Card that has a name, cost, value and influence grid for the game.
 */
public class Card {
  String name;
  int cost;
  int value;
  CardBoard influenceBoard;

  /**
   * Constructs a new Card.
   *
   * @param name           The name of the card (no spaces).
   * @param cost           The cost of the card (must be 1, 2, or 3).
   * @param value          The value score of the card (must be > 0).
   * @param influenceBoard The board representing the card’s influence grid.
   */
  public Card(String name, int cost, int value, CardBoard influenceBoard) {
    if (name == null) {
      throw new IllegalArgumentException("Name cannot be null");
    }
    if (cost != 1 && value != 2 && value != 3) {
      throw new IllegalArgumentException("Cost must be 1, 2 or 3");
    }
    if (value <= 0) {
      throw new IllegalArgumentException("Value must be positive");
    }
    this.name = name;
    this.cost = cost;
    this.value = value;
    this.influenceBoard = influenceBoard;
  }

  /**
   * Converts the card to a string.
   * @return the card as a string.
   */
  @Override
  public String toString() {
    return null;
  }
}
