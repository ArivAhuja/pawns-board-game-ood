package cs3500.pawnsboard.model;

/**
 * The public interface for a Card in the Pawns Board game.
 */
public interface CardI {

  /**
   * Gets the name of the card.
   * @return the card's name.
   */
  String getName();

  /**
   * Gets the cost of the card.
   * @return the card's cost.
   */
  int getCost();

  /**
   * Gets the value score of the card.
   * @return the card's value.
   */
  int getValue();

  /**
   * Gets the influence grid of the card.
   * @return a 5x5 char array representing the card's influence grid.
   */
  char[][] getInfluenceGrid();

  /**
   * Returns a string representation of the card.
   * @return a string describing the card.
   */
  @Override
  String toString();
}