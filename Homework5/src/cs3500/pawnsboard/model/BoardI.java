package cs3500.pawnsboard.model;

/**
 * The public interface for a game board.
 */
public interface BoardI {

  /**
   * Returns the cell at the specified position.
   *
   * @param row the row index.
   * @param col the column index.
   * @return the cell at that position.
   * @throws IndexOutOfBoundsException if the position is invalid.
   */
  CellI getCell(int row, int col);

  /**
   * Checks whether the given position is within board bounds.
   *
   * @param row the row index.
   * @param col the column index.
   * @return true if the position is valid, false otherwise.
   */
  boolean isValidPosition(int row, int col);

  /**
   * Sets the pawn count and owner for the cell at the specified position.
   *
   * @param row       the row index.
   * @param col       the column index.
   * @param pawnCount the number of pawns to set.
   * @param owner     the owner of the cell.
   */
  void setCellPawns(int row, int col, int pawnCount, String owner);

  /**
   * Returns the number of columns in the board.
   *
   * @return the number of columns.
   */
  int getColumns();

  /**
   * Returns the number of rows in the board.
   *
   * @return the number of rows.
   */
  int getRows();
}