package cs3500.pawnsboard;

import cs3500.pawnsboard.controller.PawnsBoardGUIController;
import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.DeckFileParser;
import cs3500.pawnsboard.model.PawnsBoardModel;
import cs3500.pawnsboard.model.Player;
import cs3500.pawnsboard.view.PawnsBoardGUIView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Initializes the Model, View and Controller and then runs the game.
 */
public class PawnsBoardGUIMain {
  /**
   * Main class for the game.
   *
   * @param args command line arguments:
   *             args[0] - path to Red's deck configuration file.
   *             args[1] - path to Blue's deck configuration file.
   *             args[2] - Red player type.
   *             args[3] - Blue player type.
   *             Valid player types: (controlboard, fillfirst, maximizerowscore, minimax)
   */
  public static void main(String[] args) {
    formatCheck(args);
    List<List<Card>> decks = parseDecks(args);
    List<Card> redDeck = decks.get(0);
    List<Card> blueDeck = decks.get(1);
    int totalDeckSize = redDeck.size() + blueDeck.size();
    PawnsBoardModel model = new PawnsBoardModel(5, 5, totalDeckSize, 5);
    Player redPlayer = createPlayer(args[2], "red", redDeck, model);
    Player bluePlayer = createPlayer(args[3], "blue", blueDeck, model);
    PawnsBoardGUIView viewRedPlayer = new PawnsBoardGUIView(model, redPlayer);
    PawnsBoardGUIView viewBluePlayer = new PawnsBoardGUIView(model, bluePlayer);
    PawnsBoardGUIController redController =
            new PawnsBoardGUIController(model, viewRedPlayer, redPlayer);
    PawnsBoardGUIController blueController =
            new PawnsBoardGUIController(model, viewBluePlayer, bluePlayer);
    redController.runGame();
    blueController.runGame();
  }

  /**
   * Validates the command line arguments format.
   *
   * @param args command line arguments to check
   * @throws IllegalArgumentException if the arguments are invalid
   */
  private static void formatCheck(String[] args) {
    // Check if there are exactly 4 arguments
    if (args.length != 4) {
      throw new IllegalArgumentException(
              "Expected 4 arguments: redDeckPath, blueDeckPath, redPlayerType, bluePlayerType");
    }

    // Check if deck files exist
    if (!Files.exists(Paths.get(args[0]))) {
      throw new IllegalArgumentException("Red deck file not found: " + args[0]);
    }

    if (!Files.exists(Paths.get(args[1]))) {
      throw new IllegalArgumentException("Blue deck file not found: " + args[1]);
    }

    // Check if player types are valid
    List<String> validTypes = List.of("human", "controlboard", "fillfirst", "maximizerowscore",
            "minimax");

    if (!validTypes.contains(args[2])) {
      throw new IllegalArgumentException("Invalid red player type: " + args[2] +
              ". Valid types are: " + String.join(", ", validTypes));
    }

    if (!validTypes.contains(args[3])) {
      throw new IllegalArgumentException("Invalid blue player type: " + args[3] +
              ". Valid types are: " + String.join(", ", validTypes));
    }
  }

  private static List<List<Card>> parseDecks(String[] args) {
    String redFilePath = args[0];
    String blueFilePath = args[1];
    try {
      String redDeckContent = new String(Files.readAllBytes(Paths.get(redFilePath)));
      String blueDeckContent = new String(Files.readAllBytes(Paths.get(blueFilePath)));
      DeckFileParser parser = new DeckFileParser();
      List<Card> redDeck = parser.toDeck(redDeckContent);
      List<Card> blueDeck = parser.toDeck(blueDeckContent);
      Collections.shuffle(redDeck);
      Collections.shuffle(blueDeck);
      List<List<Card>> decks = new ArrayList<>();
      decks.add(redDeck);
      decks.add(blueDeck);
      return decks;
    } catch (IOException e) {
      throw new RuntimeException("Error reading deck file: " + e.getMessage());
    }
  }

  private static Player createPlayer(String playerType, String playerName,
                                     List<Card> deck, PawnsBoardModel model) {
    switch (playerType) {
      case "human":
        return new Player(playerName, deck, model);
      case "controlboard":
        //placeholders below
        return new Player(playerName, deck, model);
      case "fillfirst":
        return new Player(playerName, deck, model);
      case "maximizerowscore":
        return new Player(playerName, deck, model);
      case "minimax":
        return new Player(playerName, deck, model);
      default:
        throw new IllegalArgumentException("Invalid player type: " + playerType);
    }
  }
}