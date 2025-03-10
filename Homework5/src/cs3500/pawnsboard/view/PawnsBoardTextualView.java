package cs3500.pawnsboard.view;

import cs3500.pawnsboard.model.Board;
import cs3500.pawnsboard.model.PawnsBoardModel;

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

  /**
   * Creates the viewable version of the board.
   *
   * @return a string that shows the state of the world.
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    Board board = model.getBoard();
    int rows = board.getRows();
    int cols = board.getColumns();
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        board.getCell(i, j).toString();
      }
      sb.append("\n");
    }
    return sb.toString();
  }
}
