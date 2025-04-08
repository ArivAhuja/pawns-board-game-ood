package cs3500.pawnsboard.provider.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * This class extracts information from the config file and sets up a deck of cards for the
 * red player and blue player.
 */
public class ConfigReader {

  protected List<CustomCard> deck = new ArrayList<>();

  int count = 0;

  int duplicateCheck = 0;

  /**
   * This method is responsible for reading the config file and adding cards to the deck.
   */
  public void read(String path, Player playerType, Boolean mirror)  {
    deck.clear();
    count = 0;
    String filePath = getFilePath(path);
    File config = new File(filePath);
    Scanner scanner = null;
    try {
      scanner = new Scanner(config);
      while (scanner.hasNextLine()) {
        duplicateCheck = 0;
        String line = scanner.nextLine();
        String[] parts = line.split(" ");
        if (parts.length < 3) {
          continue;
        }
        String name = parts[0];
        int cost = Integer.parseInt(parts[1]);
        int value = Integer.parseInt(parts[2]);

        List<String> gridLines = new ArrayList<>();
        while (scanner.hasNextLine()) {
          count ++;
          if (count == 6) {
            count = 0;
            break;
          }
          String gridLine = scanner.nextLine();
          gridLines.add(gridLine);
        }

        int rows = gridLines.size();
        int cols = gridLines.get(0).length();
        char[][] grid = new char[rows][cols];

        for (int i = 0; i < rows; i++) {
          grid[i] = gridLines.get(i).toCharArray();
        }
        CustomCard currentCard = new CustomCard(name, cost, value, grid, playerType);
        checkDuplicate(currentCard);
        deck.add(mirror ? new CustomCard(name, cost, value, mirrorGrid(grid), playerType) :
                currentCard);
      }
    } catch (FileNotFoundException e) {
      throw new IllegalArgumentException("File not found: " + filePath + " OR Issue with deck");
    } finally {
      if (scanner != null) {
        scanner.close();
      }
    }
  }

  private void checkDuplicate(CustomCard currentCard) throws FileNotFoundException {
    for (CustomCard card : deck)  {
      if (Objects.equals(currentCard.getName(), card.getName()))  {
        duplicateCheck++;
      }
      if (duplicateCheck == 2)  {
        throw new FileNotFoundException();
      }
    }
  }

  /**
   * Method only for debugging by logging information in terminal.
   */
  public void printInfo() {
    for (CustomCard element : deck)  {
      System.out.println("Name:" + element.name);
      System.out.println("Cost:" + element.cost);
      System.out.println("Value:" + element.value);
      System.out.println("Player:" + element.playertype);
      StringBuilder wow = new StringBuilder();
      for (int row = 0; row < 5; row++) {
        for (int col = 0; col < 5; col++) {
          wow.append(element.influence[row][col]);
        }
        wow.append("\n");
      }
      System.out.println(wow);

      StringBuilder wow2 = new StringBuilder();
      wow2.append(element.getInfluenceOffset().toString());
      System.out.println(wow2);
    }
  }

  private char[][] mirrorGrid(char[][] grid) {
    int rows = grid.length;
    int cols = grid[0].length;
    char[][] mirroredGrid = new char[rows][cols];

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        mirroredGrid[i][j] = grid[i][cols - 1 - j]; // Swap left and right
      }
    }
    return mirroredGrid;
  }

  /**
   * Returns the deck belonging to the red player.
   * @return list of CustomCards.
   */
  public List<CustomCard> getResultingDeck()  {
    List<CustomCard> deckCopy = new ArrayList<>();
    for (CustomCard card : this.deck) {
      deckCopy.add(card);
    }
    return deckCopy;
  }

  private String getFilePath(String path) {
    StringBuilder newPath = new StringBuilder();
    for (int index = 0; index < path.length(); index++) {
      if (path.charAt(index) == '/' ||  path.charAt(index) == '\\') {
        newPath.append(File.separator);
      }
      else {
        newPath.append(path.charAt(index));
      }
    }
    return newPath.toString();
  }
}
