package cs3500.pawnsboard.provider.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a card of Pawns World. Contains a name, a cost, a value, and an influence grid
 * indicating its range of influence.
 */
public class CustomCard implements BoardElement {

  protected String name;
  protected int cost;
  protected int value;
  protected char[][] influence;

  protected final CoordOffset center = new CoordOffset(2, 2);

  protected List<CoordOffset> influenceOffset = new ArrayList<>();

  protected Player playertype;


  /**
   * Constructs a CustomCard object.
   * @param name name of the card.
   * @param cost cost of the card.
   * @param value value of the card.
   * @param influence influence of the card.
   * @param playertype playertype of the card.
   */
  public CustomCard(String name, int cost, int value, char[][] influence, Player playertype) {

    if (cost > 3 || cost < 1 || value < 0)  {
      throw new IllegalArgumentException("Invalid Card");
    }

    this.name = name;

    this.cost = cost;

    this.value = value;

    this.influence = influence;

    this.playertype = playertype;

    generateOffset();
  }

  /**
   * Generates a list of offset values from the given influence position (Read from the config file)
   * This is later used in the PawnsGame class.
   */
  private void generateOffset()  {
    for (int row = 0; row < 5; row++) {
      for (int col = 0; col < 5; col++) {
        if (influence[row][col] == 'I') {
          influenceOffset.add(new CoordOffset(row - 2, col - 2));
        }
      }
    }
  }

  protected List<CoordOffset> getInfluenceOffset()  {
    return this.influenceOffset;
  }

  @Override
  public int pawnCount() {
    return 50;
  }

  @Override
  public Player getPlayerType() {
    return this.playertype;
  }

  @Override
  public String toString()  {
    if (playertype == Player.RED) {
      return "R";
    }
    else {
      return "B";
    }
  }

  @Override
  public void updatePlayerType(Player type) {
    //This method should not do anything as Player type change must only be done to Pawns and
    //not Cards.
  }

  @Override
  public void updatePawnCount() {
    //This method is not relevant for CustomCards.
  }

  @Override
  public int getValue() {
    return this.value;
  }

  public String getName() {
    return this.name;
  }

  @Override
  public int getCost() {
    return this.cost;
  }

  @Override
  public boolean hasCard() {
    return true;
  }

  /**
   * Converts a card's influence into a 5x5 grid string representation.
   * 'C' represents the center (card placement).
   * 'I' represents affected cells.
   * 'X' represents non-influenced areas.
   *
   * @return A formatted string representing the influence grid.
   */
  public String influenceToString() {
    char[][] grid = new char[5][5];  // 5x5 influence grid

    // Initialize grid with 'X' everywhere
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        grid[i][j] = 'X';
      }
    }

    // Place the center 'C' (card position) at (2,2)
    grid[2][2] = 'C';

    // Mark influence areas with 'I'
    for (CoordOffset offset : this.influenceOffset) {
      int row = 2 + offset.row;  // Convert relative position to grid index
      int col = 2 + offset.col;
      if (row >= 0 && row < 5 && col >= 0 && col < 5) {  // Ensure within bounds
        grid[row][col] = 'I';
      }
    }

    // Convert grid to a string format
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        sb.append(grid[i][j]);
      }
      sb.append("\n");  // New line after each row
    }

    return sb.toString();
  }

}
