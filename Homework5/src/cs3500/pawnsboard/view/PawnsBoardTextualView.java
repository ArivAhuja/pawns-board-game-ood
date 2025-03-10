package cs3500.pawnsboard.view;

import cs3500.pawnsboard.model.Board;
import cs3500.pawnsboard.model.PawnsBoardModel;
import cs3500.pawnsboard.model.Cell;


/**
 * Provides a textual view of the PawnsBoard game.
 */
public class PawnsBoardTextualView {
  private final PawnsBoardModel model;

  /**
   * Constructs a TextualView of the game based on the inputted model
   *
   * @param model is the game.
   */
  public PawnsBoardTextualView(PawnsBoardModel model) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }
    this.model = model;
  }

  public void render(Board board) {
    System.out.println("Board state:");
    for (int i = 0; i < board.getRows(); i++) {
      StringBuilder sb = new StringBuilder();
      for (int j = 0; j < board.getColumns(); j++) {
        Cell cell = board.getCell(i, j);
        if (cell.getCard() != null) {
          sb.append(cell.getOwner().charAt(0));
        } else if (cell.hasPawns()) {
          sb.append(cell.getPawnCount());
        } else {
          sb.append("_");
        }
        sb.append(" ");
      }
      System.out.println(sb.toString());
    }
    System.out.println();
  }
}
