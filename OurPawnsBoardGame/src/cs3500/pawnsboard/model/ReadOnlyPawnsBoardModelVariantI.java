package cs3500.pawnsboard.model;

/**
 * An interface exposing only the observation methods for the PawnsBoard game.
 */
public interface ReadOnlyPawnsBoardModelVariantI extends ReadOnlyPawnsBoardModelI {
  /**
   * Gets the Cell Influence Value.
   * @param cell the cell.
   * @return the influence value.
   */
  int getCellInfluenceValue(Cell cell);
}
