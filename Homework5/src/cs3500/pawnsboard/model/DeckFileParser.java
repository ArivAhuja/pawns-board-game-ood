package cs3500.pawnsboard.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DeckFileParser {

  /**
   * Parses a single card block into a Card object.
   * The card block consists of a header line (card name, cost, value)
   * followed by one or more grid lines.
   *
   * @param cardBlock The block of text representing one card.
   * @return The parsed Card object.
   */
  public Card toCard(String cardBlock) {
    Scanner scanner = new Scanner(cardBlock);
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

    List<String> gridLines = new ArrayList<>();
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine().trim();
      if (!line.isEmpty()) {
        gridLines.add(line);
      }
    }
    if (gridLines.isEmpty()) {
      throw new IllegalArgumentException("No grid lines provided for card " + name);
    }

    int rowCount = gridLines.size();
    int colCount = gridLines.get(0).length();
    // Ensure all rows have the same length.
    for (String row : gridLines) {
      if (row.length() != colCount) {
        throw new IllegalArgumentException("Inconsistent row lengths for card " + name);
      }
    }
    // Validate that both dimensions are odd (to ensure a unique center cell).
    if (rowCount % 2 == 0 || colCount % 2 == 0) {
      throw new IllegalArgumentException("Grid dimensions must be odd for card " + name);
    }

    // Create a Board with the grid dimensions.
    CardBoard board = new Board(rowCount, colCount);
    int centerRow = rowCount / 2;
    int centerCol = colCount / 2;
    for (int r = 0; r < rowCount; r++) {
      String row = gridLines.get(r);
      for (int c = 0; c < colCount; c++) {
        char ch = row.charAt(c);
        if (r == centerRow && c == centerCol) {
          if (ch != 'C') {
            throw new IllegalArgumentException("Center cell must be 'C' for card " + name);
          }
        } else {
          if (ch == 'C') {
            throw new IllegalArgumentException("Only center cell can be 'C' for card " + name);
          }
          if (ch != 'X' && ch != 'I') {
            throw new IllegalArgumentException("Invalid character '" + ch + "' in grid for card " + name);
          }
        }
        // Set the cell's type in the Board.
        board.getCell(r, c).setType(ch);
      }
    }

    return new Card(name, cost, value, board);
  }

  /**
   * Parses an entire deck file string into a list of Card objects.
   * The file is expected to be a series of card blocks, where each card block
   * starts with a header line (three tokens) followed by one or more grid lines.
   *
   * @param deckString The complete content of the deck file.
   * @return A list of Card objects.
   */
  public List<Card> toDeck(String deckString) {
    List<Card> deck = new ArrayList<>();
    Scanner scanner = new Scanner(deckString);
    List<String> lines = new ArrayList<>();
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine().trim();
      if (!line.isEmpty()) {
        lines.add(line);
      }
    }

    int i = 0;
    while (i < lines.size()) {
      // The current line should be a header line (three tokens).
      String headerLine = lines.get(i);
      String[] headerParts = headerLine.split("\\s+");
      if (headerParts.length != 3) {
        throw new IllegalArgumentException("Expected header line at line " + (i + 1) + ": " + headerLine);
      }
      // Build the card block (header + subsequent grid lines) until the next header or end-of-file.
      StringBuilder cardBlockBuilder = new StringBuilder();
      cardBlockBuilder.append(headerLine).append("\n");
      i++;
      while (i < lines.size() && lines.get(i).split("\\s+").length != 3) {
        cardBlockBuilder.append(lines.get(i)).append("\n");
        i++;
      }
      String cardBlock = cardBlockBuilder.toString().trim();
      Card card = toCard(cardBlock);
      deck.add(card);
    }
    return deck;
  }
}

