package cs3500.pawnsboard.model;

/**
 * Represents a card in the game.
 */
public class Card implements CardI {
  private final String name;
  private final int cost;
  private final int value;
  /**
   * Store the influence in a char array, where 'X' represents no influence, 'I' represents
   * influence, and 'C' represents the center cell, just like the config file.
   */
  private final char[][] influenceGrid;

  /**
   * Constructs a new Card.
   *
   * @param name The card's name (no spaces).
   * @param cost The cost of the card (must be 1, 2, or 3).
   * @param value The value score of the card (must be > 0).
   * @param influenceGrid The card's influence grid represented as a 2D char array.
   *                      It must be 5x5 and follow the rules:
   *                      - Only 'X' (no influence), 'I' (influence), or 'C' (center) are allowed.
   *                      - The center cell (row 2, column 2 using 0-indexing) must be 'C'.
   *                      - 'C' must not appear in any other cell.
   */
  public Card(String name, int cost, int value, char[][] influenceGrid) {
    if (name == null) {
      throw new IllegalArgumentException("Name cannot be null");
    }
    if (cost < 1 || cost > 3) {
      throw new IllegalArgumentException("Cost must be 1, 2, or 3");
    }
    if (value <= 0) {
      throw new IllegalArgumentException("Value must be positive");
    }
    if (influenceGrid == null || influenceGrid.length != 5 || influenceGrid[0].length != 5) {
      throw new IllegalArgumentException("Influence grid must be a 5x5 array");
    }
    // Validate grid: center cell and allowed characters.
    for (int i = 0; i < 5; i++) {
      if (influenceGrid[i].length != 5) {
        throw new IllegalArgumentException("Each row in the influence grid must have exactly 5 " +
                "characters");
      }
      for (int j = 0; j < 5; j++) {
        char ch = influenceGrid[i][j];
        if (i == 2 && j == 2) {
          if (ch != 'C') {
            throw new IllegalArgumentException("Center cell must be 'C'");
          }
        } else {
          // Allow X, I, U, and D in non-center positions.
          if (ch != 'X' && ch != 'I' && ch != 'U' && ch != 'D') {
            throw new IllegalArgumentException("Invalid character in grid: " + ch);
          }
        }
      }
    }
    this.name = name;
    this.cost = cost;
    this.value = value;
    this.influenceGrid = influenceGrid;
  }

  /**
   * Copy constructor for deep copying a Card object.
   * @param other The Card to copy.
   */
  public Card(Card other) {
    this.name = other.name;
    this.cost = other.cost;
    this.value = other.value;
    this.influenceGrid = deepCopyGrid(other.influenceGrid);
  }


  public String getName() {
    return name;
  }

  public int getCost() {
    return cost;
  }

  public int getValue() {
    return value;
  }

  public char[][] getInfluenceGrid() {
    return influenceGrid;
  }

  @Override
  public String toString() {
    return name + " (Cost: " + cost + ", Value: " + value + ")";
  }

  private char[][] deepCopyGrid(char[][] grid) {
    if (grid == null) {
      return null;
    }
    char[][] copy = new char[grid.length][];
    for (int i = 0; i < grid.length; i++) {
      copy[i] = grid[i].clone();
    }
    return copy;
  }
}
