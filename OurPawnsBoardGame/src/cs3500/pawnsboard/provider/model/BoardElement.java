package cs3500.pawnsboard.provider.model;

/**
 * Represents any object that can be placed on a board of Pawns World.
 */
public interface BoardElement {

  /**
   * Represents the object in a string format.
   * @return String of the object.
   */
  String toString();

  /**
   * Returns the number of pawns.
   * @return int of number of pawns
   */
  int pawnCount();

  /**
   * Returns whether the player is red or blue.
   * @return Player associated with this object.
   */
  Player getPlayerType();

  /**
   * Updates the player type passed in as a parameter for this object.
   * @param type Player to update
   */
  void updatePlayerType(Player type);

  /**
   * Increments the number of Pawns of this object by 1.
   */
  void updatePawnCount();

  /**
   * Gets the value of this object. Only relevant for the CustomCard implementation.
   * @return int of value of Card
   */
  int getValue();


  /**
   * Gets the cost of this object. Only relevant for the CustomCard implementation.
   * @return int of the cost of the Card
   */
  int getCost();

  /**
   * Indicates whether the current object is of type CustomCard.
   * @return boolean indicating object type.
   */
  boolean hasCard();

  /**
   * Gets the name for the board element.
   * @return name of the board element.
   */
  String getName();

  /**
   * gets influence of a card in string format.
   * @return influence of the board element as a string.
   */
  String influenceToString();
}
