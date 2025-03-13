package cs3500.pawnsboard.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class that has methods that parse the deck file.
 */
public class DeckFileParser implements DeckFileParserI {

  /**
   * Converts a block of text into a card.
   * @param cardBlock a block of text for one card.
   * @return a card object.
   */
  public Card toCard(String cardBlock) {
    Scanner scanner = new Scanner(cardBlock);

    // Parse header line.
    if (!scanner.hasNextLine()) {
      throw new IllegalArgumentException("Card block is empty");
    }
    String header = scanner.nextLine().trim();
    String[] headerParts = header.split("\\s+");
    if (headerParts.length != 3) {
      throw new IllegalArgumentException("Invalid header format: " + header);
    }
    String name = headerParts[0];
    int cost = Integer.parseInt(headerParts[1]);
    int value = Integer.parseInt(headerParts[2]);

    // Read exactly 5 grid lines.
    char[][] grid = new char[5][5];
    for (int i = 0; i < 5; i++) {
      if (!scanner.hasNextLine()) {
        throw new IllegalArgumentException("Incomplete grid for card " + name);
      }
      String row = scanner.nextLine().trim();
      if (row.length() != 5) {
        throw new IllegalArgumentException("Grid row must have exactly 5 characters for card "
                + name);
      }
      grid[i] = row.toCharArray();
    }

    return new Card(name, cost, value, grid);
  }

  /**
   * Converts a full deck file (as a String) into a list of Card objects.
   * Assumes the deck file contains a multiple of 6 non-empty lines.
   *
   * @param deckString the entire deck file as a string.
   * @return a list of Card objects parsed from the file.
   */
  public List<Card> toDeck(String deckString) {
    List<Card> deck = new ArrayList<>();
    Scanner scanner = new Scanner(deckString);
    List<String> lines = new ArrayList<>();

    // Read all non-empty lines.
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine().trim();
      if (!line.isEmpty()) {
        lines.add(line);
      }
    }

    // Each card is represented by 6 consecutive lines.
    if (lines.size() % 6 != 0) {
      throw new IllegalArgumentException("Deck file does not have a multiple of 6 lines.");
    }
    for (int i = 0; i < lines.size(); i += 6) {
      StringBuilder cardBlockBuilder = new StringBuilder();
      for (int j = 0; j < 6; j++) {
        cardBlockBuilder.append(lines.get(i + j)).append("\n");
      }
      Card card = toCard(cardBlockBuilder.toString());
      deck.add(card);
    }
    return deck;
  }
}


