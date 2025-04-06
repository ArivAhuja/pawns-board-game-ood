package cs3500.pawnsboard;

import cs3500.pawnsboard.controller.PawnsBoardGUIController;
import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.DeckFileParser;
import cs3500.pawnsboard.model.HumanPlayer;
import cs3500.pawnsboard.model.MachinePlayer;
import cs3500.pawnsboard.model.PawnsBoardModel;
import cs3500.pawnsboard.model.Player;
import cs3500.pawnsboard.model.PlayerActions;
import cs3500.pawnsboard.strategy.ControlBoardStrategy;
import cs3500.pawnsboard.strategy.FillFirstStrategy;
import cs3500.pawnsboard.strategy.MaximizeRowScoreStrategy;
import cs3500.pawnsboard.strategy.MiniMaxStrategy;
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
   *             Valid player types: (human, controlboard, fillfirst, maximizerowscore, minimax)
   */
  public static void main(String[] args) {
    // can format to accept lists for the args for chain strategys
    formatCheck(args);
    List<Card> redDeck = parseDeck(args[0]);
    List<Card> blueDeck = parseDeck(args[1]);
    int totalDeckSize = redDeck.size() + blueDeck.size();
    PawnsBoardModel model = new PawnsBoardModel(5, 5, totalDeckSize, 4);
    Player redPlayer = new Player("red", redDeck, model);
    Player bluePlayer = new Player("blue", blueDeck, model);
    PlayerActions redPlayerActions = createPlayerActions(args[2], redPlayer);
    PlayerActions bluePlayerActions = createPlayerActions(args[3], bluePlayer);
    PawnsBoardGUIView viewRedPlayer = new PawnsBoardGUIView(model, redPlayer);
    PawnsBoardGUIView viewBluePlayer = new PawnsBoardGUIView(model, bluePlayer);
    PawnsBoardGUIController redController =
            new PawnsBoardGUIController(model, viewRedPlayer, redPlayer, redPlayerActions);
    PawnsBoardGUIController blueController =
            new PawnsBoardGUIController(model, viewBluePlayer, bluePlayer, bluePlayerActions);
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

  private static List<Card> parseDeck(String deckFilePath) {
    try {
      String deckContent = new String(Files.readAllBytes(Paths.get(deckFilePath)));
      DeckFileParser parser = new DeckFileParser();
      List<Card> deck = parser.toDeck(deckContent);
      Collections.shuffle(deck);
      return deck;
    } catch (IOException e) {
      throw new RuntimeException("Error reading deck file: " + e.getMessage());
    }
  }

  private static PlayerActions createPlayerActions(String playerType, Player player) {
    switch (playerType) {
      case "human":
        return new HumanPlayer(player);
      case "controlboard":
        return new MachinePlayer(player, new ControlBoardStrategy());
      case "fillfirst":
        return new MachinePlayer(player, new FillFirstStrategy());
      case "maximizerowscore":
        return new MachinePlayer(player, new MaximizeRowScoreStrategy());
      case "minimax":
        return new MachinePlayer(player, new MiniMaxStrategy());
      default:
        throw new IllegalArgumentException("Invalid player type: " + playerType);
    }
  }
}