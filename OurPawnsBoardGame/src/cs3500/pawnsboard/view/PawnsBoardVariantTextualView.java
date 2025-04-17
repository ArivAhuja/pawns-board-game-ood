package cs3500.pawnsboard.view;

import cs3500.pawnsboard.model.Board;
import cs3500.pawnsboard.model.Cell;
import cs3500.pawnsboard.model.ReadOnlyPawnsBoardModelI;

/**
 * Provides a more detailed textual view of the PawnsBoard game for the variant.
 * Shows cells in a table format with detailed cell information and separate score displays.
 */
public class PawnsBoardVariantTextualView implements PawnsBoardTextualViewI {
  private final ReadOnlyPawnsBoardModelI model;

  /**
   * Constructs a Variant TextualView of the game based on the inputted model.
   * @param model is the game.
   */
  public PawnsBoardVariantTextualView(ReadOnlyPawnsBoardModelI model) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }
    this.model = model;
  }

  @Override
  public void render(Board board) {
    int rows = board.getRows();
    int columns = board.getColumns();
    int[][] rowScores = model.computeRowScores();

    // Create and display the board
    for (int i = 0; i < rows; i++) {
      StringBuilder rowBuilder = new StringBuilder();

      for (int j = 0; j < columns; j++) {
        Cell cell = board.getCell(i, j);
        rowBuilder.append(formatCell(cell));

        // Add separator between cells, except for the last cell
        if (j < columns - 1) {
          rowBuilder.append(" | ");
        }
      }

      System.out.println(rowBuilder.toString());

      // Add a line between rows, except after the last row
      if (i < rows - 1) {
        System.out.println();
      }
    }

    // Print a blank line before scores
    System.out.println("\n");

    // Display scores for both players
    printPlayerScores("Red", rowScores, 0);
    printPlayerScores("Blue", rowScores, 1);
  }

  /**
   * Formats a cell based on its content.
   * Returns a string in the format ["Type", Value, InfluenceValue]
   *
   * @param cell the cell to format
   * @return formatted string representation of the cell
   */
  private String formatCell(Cell cell) {
    StringBuilder cellBuilder = new StringBuilder("[");

    if (cell.getCard() != null) {
      // Cell has a card
      cellBuilder.append("\"C\", ");
      cellBuilder.append(cell.getCard().getValue());
      cellBuilder.append(", ");
      cellBuilder.append(cell.getInfluenceModifier());
    } else if (cell.hasPawns()) {
      // Cell has pawns
      cellBuilder.append("\"P\", ");
      cellBuilder.append(cell.getPawnCount());
      cellBuilder.append(", ");
      cellBuilder.append(cell.getInfluenceModifier());
    } else {
      // Empty cell
      cellBuilder.append("\"_\", \"_\", \"_\"");
    }

    cellBuilder.append("]");
    return cellBuilder.toString();
  }

  /**
   * Prints the scores for a player.
   *
   * @param playerName the name of the player
   * @param rowScores the 2D array containing row scores
   * @param playerIndex the index of the player in the rowScores array (0 for Red, 1 for Blue)
   */
  private void printPlayerScores(String playerName, int[][] rowScores, int playerIndex) {
    StringBuilder scoresBuilder = new StringBuilder(playerName);
    scoresBuilder.append(" Player Scores: [");

    for (int i = 0; i < rowScores.length; i++) {
      scoresBuilder.append(rowScores[i][playerIndex]);

      // Add comma between scores, except for the last score
      if (i < rowScores.length - 1) {
        scoresBuilder.append(", ");
      }
    }

    scoresBuilder.append("]");
    System.out.println(scoresBuilder.toString());
  }
}