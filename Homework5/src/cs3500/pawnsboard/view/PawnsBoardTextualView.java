package cs3500.pawnsboard.view;

import cs3500.pawnsboard.model.Board;
import cs3500.pawnsboard.model.PawnsBoardModel;
import cs3500.pawnsboard.model.Cell;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModelI;


/**
 * Provides a textual view of the PawnsBoard game.
 */
public class PawnsBoardTextualView implements PawnsBoardTextualViewI {
  private final ReadonlyPawnsBoardModelI model;

  /**
   * Constructs a TextualView of the game based on the inputted model.
   * @param model is the game.
   */
  public PawnsBoardTextualView(ReadonlyPawnsBoardModelI model) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }
    this.model = model;
  }

  /**
   * Renders the board state along with the row scores.
   * Each row is printed as:
   *    redRowScore cellRepresentations blueRow Score
   * The cell representation is:
   *   - "R" or "B" if a card is present (depending on owner),
   *   - a number (1, 2, or 3) if only pawns are present,
   *   - "_" if the cell is empty.
   */
  public void render(Board board) {
    int[][] rowScores = model.computeRowScores();
    System.out.println("Board state with row scores:");
    for (int i = 0; i < board.getRows(); i++) {
      StringBuilder sb = new StringBuilder();
      // Left score (Red's row score)
      sb.append(rowScores[i][0]).append(" ");
      // Board cells for the row.
      for (int j = 0; j < board.getColumns(); j++) {
        Cell cell = board.getCell(i, j);
        if (cell.getCard() != null) {
          sb.append(cell.getOwner().charAt(0));
        } else if (cell.hasPawns()) {
          sb.append(cell.getPawnCount());
        } else {
          sb.append("_");
        }
      }
      // Right score (Blue's row score)
      sb.append(" ").append(rowScores[i][1]);
      System.out.println(sb.toString());
    }
    System.out.println();
  }
}
