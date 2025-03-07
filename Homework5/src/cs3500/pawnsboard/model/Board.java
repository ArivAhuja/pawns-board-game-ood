package cs3500.pawnsboard.model;

/**
 * The Board class represents the game board as a 2D array of Cells.
 */
public class Board {
  private int rows;
  private int columns;
  private Cell[][] cells;

  /**
   * Constructs a new Board with the specified dimensions.
   *
   * @param rows Number of rows (must be > 0).
   * @param columns Number of columns (must be > 1 and odd).
   */
  public Board(int rows, int columns) {
    if (rows <= 0 || columns <= 1 || columns % 2 == 0) {
      throw new IllegalArgumentException("Invalid board dimensions: rows must be > 0 and columns must be > 1 and odd.");
    }
    this.rows = rows;
    this.columns = columns;
    cells = new Cell[rows][columns];
    // initialize each cell in the 2D array
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        cells[i][j] = new Cell();
      }
    }
  }

  /**
   * Returns the cell at the specified position.
   *
   * @param row Row index.
   * @param col Column index.
   * @return The Cell object at that position.
   */
  public Cell getCell(int row, int col) {
    if (!isValidPosition(row, col)) {
      throw new IndexOutOfBoundsException("Invalid cell position: " + row + ", " + col);
    }
    return cells[row][col];
  }

  /**
   * Checks whether the given position is within board bounds.
   */
  public boolean isValidPosition(int row, int col) {
    return row >= 0 && row < rows && col >= 0 && col < columns;
  }

  /**
   * Utility method to set a cell's pawn count and owner.
   *
   * @param row Row index.
   * @param col Column index.
   * @param pawnCount Number of pawns to set.
   * @param owner The owner of the pawns.
   */
  public void setCellPawns(int row, int col, int pawnCount, String owner) {
    Cell cell = getCell(row, col);
    cell.setPawnCount(pawnCount);
    cell.setOwner(owner);
  }
  /**
   *
   * Returns the number of columns.
   * @return The number of columns.
   */
  public int getColumns() {
    return columns;
  }

  /**
   * Returns the number of rows.
   * @return The number of rows.
   */
  public int getRows() {
    return rows;
  }
}