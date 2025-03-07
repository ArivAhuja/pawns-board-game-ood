package cs3500.pawnsboard.model;

public class Card {
  String name;
  int cost;
  int value;

  public Card(String name, int cost, int value, Board board) {
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
  }

  @Override
  public String toString() {
    return null;
  }
}
