package cs3500.pawnsboard.model;

import java.util.List;

/**
 * Represents the main model for the Pawns game.
 *
 */
public class PawnsBoardModel {

  private Board board;
  private Player redPlayer;
  private Player bluePlayer;
  private boolean isRedTurn;


  /**
   * Constructs a new PawnsBoardModel.
   *
   * @param rows Number of rows on the board.
   * @param columns Number of columns on the board.
   * @param deck The list of cards (from the deck configuration file) to use for both players.
   * @param handSize Starting hand size for each player.
   */
  public PawnsBoardModel(int rows, int columns, List<Card> deck, int handSize) {
    if (rows <= 0 || columns <= 1 || columns % 2 == 0) {
      throw new IllegalArgumentException("Invalid board dimensions: rows must be > 0, and columns must be > 1 and odd.");
    }

    // initialize the board as a 2D array of Cells
    this.board = new Board(rows, columns);

    // initialize players using the same deck
    this.redPlayer = new Player("Red", deck, handSize);
    this.bluePlayer = new Player("Blue", deck, handSize);

    // set initial board configuration:
    // first column cells get 1 red pawn, last column cells get 1 blue pawn.
    for (int row = 0; row < rows; row++) {
      board.setCellPawns(row, 0, 1, "Red");
      board.setCellPawns(row, columns - 1, 1, "Blue");
    }

    // red always starts
    this.isRedTurn = true;
  }

}
