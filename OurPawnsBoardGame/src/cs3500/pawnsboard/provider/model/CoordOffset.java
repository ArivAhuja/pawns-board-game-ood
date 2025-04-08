package cs3500.pawnsboard.provider.model;

import java.util.Objects;

/**
 * A class to represent a pair of co-ordinates.
 */
public class CoordOffset {

  protected int row;

  protected int col;

  /**
   * Constructs a CoordOffset object.
   * @param row row.
   * @param col col.
   */
  public CoordOffset(int row, int col)  {
    this.row = row;
    this.col = col;
  }

  @Override
  public String toString()  {
    return "(" + row + "," + col + ")";
  }

  public int getRow() {
    return this.row;
  }

  public int getCol() {
    return this.col;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof CoordOffset)) {
      return false;
    }

    CoordOffset that = (CoordOffset) other;
    return that.getCol() == this.getCol() && that.getRow() == this.getRow();
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.row, this.col);
  }

}
