package cs3500.pawnsboard;

import cs3500.pawnsboard.model.PawnsBoardVariantModel;
import cs3500.pawnsboard.view.ModelAdapter;

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
import cs3500.pawnsboard.view.PawnsBoardGUIViewI;
import cs3500.pawnsboard.view.ProviderViewAdapter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * Main class for the game.
 *
 * Command-line arguments:
 *   args[0] - path to Red's deck configuration file.
 *   args[1] - path to Blue's deck configuration file.
 *   args[2] - Red player type (e.g. human, controlboard, fillfirst, maximizerowscore, minimax).
 *   args[3] - Blue player type (same valid types as above).
 *   args[4] (optional) - if present and equals "provider", then uses the provider view.
 */
public class PawnsBoardGUIMain {
  public static void main(String[] args) {
    formatCheck(args);
    List<Card> redDeck = parseDeck(args[0], false);
    List<Card> blueDeck = parseDeck(args[1], true);
    int totalDeckSize = redDeck.size() + blueDeck.size();
    PawnsBoardVariantModel model = new PawnsBoardVariantModel(5, 5, totalDeckSize, 4);
    Player redPlayer = new Player("red", redDeck, model);
    Player bluePlayer = new Player("blue", blueDeck, model);
    PlayerActions redPlayerActions = createPlayerActions(args[2], redPlayer);
    PlayerActions bluePlayerActions = createPlayerActions(args[3], bluePlayer);
    PawnsBoardGUIViewI viewRedPlayer = new PawnsBoardGUIView(model, redPlayer);
    PawnsBoardGUIViewI viewBluePlayer = new PawnsBoardGUIView(model, bluePlayer);
    if (args.length == 5) {
      if (args[4].equalsIgnoreCase("provider")) {
        ModelAdapter adaptedModel = new ModelAdapter(model, redPlayer, bluePlayer);
        try {
          viewBluePlayer = new ProviderViewAdapter(adaptedModel,
                  cs3500.pawnsboard.provider.model.Player.BLUE);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    PawnsBoardGUIController redController =
            new PawnsBoardGUIController(model, viewRedPlayer, redPlayer, redPlayerActions);
    PawnsBoardGUIController blueController =
            new PawnsBoardGUIController(model, viewBluePlayer, bluePlayer, bluePlayerActions);
    redController.runGame();
    blueController.runGame();
  }

  private static void formatCheck(String[] args) {
    // Check if there are at least 4 arguments.
    if (args.length < 4) {
      throw new IllegalArgumentException(
              "Expected at least 4 arguments: redDeckPath, blueDeckPath, " +
                      "redPlayerType, bluePlayerType");
    }
    if (args.length > 5) {
      throw new IllegalArgumentException(
              "Expected at max 5 arguments: redDeckPath, blueDeckPath, redPlayerType, " +
                      "bluePlayerType, 'provider'");
    }
    // Check if deck files exist.
    if (!Files.exists(Paths.get(args[0]))) {
      throw new IllegalArgumentException("Red deck file not found: " + args[0]);
    }
    if (!Files.exists(Paths.get(args[1]))) {
      throw new IllegalArgumentException("Blue deck file not found: " + args[1]);
    }
    // Check if player types are valid.
    List<String> validTypes = List.of("human", "controlboard", "fillfirst",
            "maximizerowscore", "minimax");
    if (!validTypes.contains(args[2])) {
      throw new IllegalArgumentException("Invalid red player type: " + args[2] +
              ". Valid types are: " + String.join(", ", validTypes));
    }
    if (!validTypes.contains(args[3])) {
      throw new IllegalArgumentException("Invalid blue player type: " + args[3] +
              ". Valid types are: " + String.join(", ", validTypes));
    }
    if (args.length == 5) {
      if (!"provider".equals(args[4])) {
        throw new IllegalArgumentException("5th argument must be 'provider'.");
      }
    }
  }

  private static List<Card> parseDeck(String deckFilePath, boolean mirror) {
    try {
      String deckContent = new String(Files.readAllBytes(Paths.get(deckFilePath)));
      DeckFileParser parser = new DeckFileParser();
      List<Card> deck = parser.toDeck(deckContent, mirror);
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