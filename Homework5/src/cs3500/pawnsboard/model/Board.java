package cs3500.pawnsboard.model;

/**
 * The Board class represents the game board as a 2D array of Cells.
 */
public class Board implements BoardI {

  /**
   * A board is represented by a 2d array of Cell objects. We also store the board size: rows
   * and columns, to make the computation and error checking faster. See @Cell class for more info.
   */
  private final int rows;
  private final int columns;
  private Cell[][] cells;

  /**
   * Constructs a new Board with the specified dimensions.
   * @param rows Number of rows (must be > 0).
   * @param columns Number of columns (must be > 1 and odd).
   */
  public Board(int rows, int columns) {
    if (rows <= 0 || columns <= 1 || columns % 2 == 0) {
      throw new IllegalArgumentException("Invalid board dimensions: rows must be > 0 and columns " +
              "must be > 1 and odd.");
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
   * Copy constructor: Creates a deep copy of the given Board.
   * @param other the Board to copy.
   */
  public Board(Board other) {
    this.rows = other.rows;
    this.columns = other.columns;
    this.cells = new Cell[rows][columns];

    // Deep copy each cell
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        this.cells[i][j] = new Cell(other.cells[i][j]); // Uses Cell's copy constructor
      }
    }
  }

  /**
   * Creates a deep copy of the board using the copy constructor.
   * @return a new Board instance.
   */
  public Board cloneBoard() {
    return new Board(this); // Uses the copy constructor
  }

  /**
   * Returns the cell at the specified position.
   * @param row the row index.
   * @param col the column index.
   * @return the cell at that position.
   * @throws IndexOutOfBoundsException if the position is invalid.
   */
  public Cell getCell(int row, int col) {
    if (!isValidPosition(row, col)) {
      throw new IndexOutOfBoundsException("Invalid cell position: " + row + ", " + col);
    }
    return cells[row][col];
  }

  /**
   * Checks whether the given position is within board bounds.
   * @param row the row index.
   * @param col the column index.
   * @return true if the position is valid, false otherwise.
   */
  public boolean isValidPosition(int row, int col) {
    return row >= 0 && row < rows && col >= 0 && col < columns;
  }

  /**
   * Sets the pawn count and owner for the cell at the specified position.
   * @param row       the row index.
   * @param col       the column index.
   * @param pawnCount the number of pawns to set.
   * @param owner     the owner of the cell.
   */
  public void setCellPawns(int row, int col, int pawnCount, String owner) {
    Cell cell = getCell(row, col);
    cell.setPawnCount(pawnCount);
    cell.setOwner(owner);
  }

  /**
   * Returns the number of columns in the board.
   * @return the number of columns.
   */
  public int getColumns() {
    return columns;
  }

  /**
   * Returns the number of rows in the board.
   * @return the number of rows.
   */
  public int getRows() {
    return rows;
  }
}