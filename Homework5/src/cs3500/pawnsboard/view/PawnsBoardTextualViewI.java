package cs3500.pawnsboard.view;

import cs3500.pawnsboard.model.Board;

/**
 * Interface for textual view of the PawnsBoard game.
 */
public interface PawnsBoardTextualViewI {

  /**
   * Renders the board state along with the row scores.
   * Each row is printed as:
   *    <red row score> <cell representations> <blue row score>
   * The cell representation is:
   *   - "R" or "B" if a card is present (depending on owner),
   *   - a number (1, 2, or 3) if only pawns are present,
   *   - "_" if the cell is empty.
   */
  void render(Board board);
}