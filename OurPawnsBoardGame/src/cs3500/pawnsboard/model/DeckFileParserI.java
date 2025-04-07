package cs3500.pawnsboard.model;

import java.util.List;

/**
 * The interface for parsing a deck configuration file.
 */
public interface DeckFileParserI {

  /**
   * Converts a block of text representing a single card into a Card object.
   * The card block consists of a header line followed by 5 grid lines.
   *
   * @param cardBlock a block of text for one card.
   * @return the parsed Card object.
   */
  Card toCard(String cardBlock);

  /**
   * Converts a full deck file (as a String) into a list of Card objects.
   * Assumes the deck file contains a multiple of 6 non-empty lines.
   *
   * @param deckString the entire deck file as a string.
   * @return a list of Card objects parsed from the file.
   */
  List<Card> toDeck(String deckString);
}