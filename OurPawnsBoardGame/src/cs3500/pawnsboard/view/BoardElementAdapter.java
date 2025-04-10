package cs3500.pawnsboard.view;

import cs3500.pawnsboard.model.Cell;
import cs3500.pawnsboard.provider.model.BoardElement;
import cs3500.pawnsboard.provider.model.Player;

public class BoardElementAdapter implements BoardElement {
  private final Cell cell;

  public BoardElementAdapter(Cell cell) {
    this.cell = cell;
  }

  @Override
  public String toString() {
    return cell.toString();
  }

  @Override
  public int pawnCount() {
    return cell.getPawnCount();
  }

  @Override
  public Player getPlayerType() {
    String owner = cell.getOwner();
    if (owner.equalsIgnoreCase("red")) {
      return cs3500.pawnsboard.provider.model.Player.RED;
    } else if (owner.equalsIgnoreCase("blue")) {
      return cs3500.pawnsboard.provider.model.Player.BLUE;
    } else {
      return cs3500.pawnsboard.provider.model.Player.NONE;
    }
  }

  @Override
  public void updatePlayerType(Player type) {
    cell.setOwner(type.toString().toLowerCase());
  }

  @Override
  public void updatePawnCount() {
    cell.incrementPawnCount();
  }

  @Override
  public int getValue() {
    return (cell.getCard() != null) ? cell.getCard().getValue() : 0;
  }

  @Override
  public int getCost() {
    return (cell.getCard() != null) ? cell.getCard().getCost() : 0;
  }

  @Override
  public boolean hasCard() {
    return cell.getCard() != null;
  }

  @Override
  public String getName() {
    return (cell.getCard() != null) ? cell.getCard().getName() : "";
  }

  @Override
  public String influenceToString() {
    if (cell.getCard() != null) {
      char[][] grid = cell.getCard().getInfluenceGrid();
      StringBuilder sb = new StringBuilder();
      for (char[] row : grid) {
        for (char c : row) {
          sb.append(c);
        }
        sb.append("\n");
      }
      return sb.toString();
    }
    return "";
  }
}