package cs3500.pawnsboard;

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
import cs3500.pawnsboard.view.ProviderViewAdapter;
import cs3500.pawnsboard.view.PawnsBoardGUIViewI;

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
 *   args[4] (optional) - if present and equals "provider", then Player2 (Blue) uses the provider view.
 */
public class PawnsBoardGUIMain {
  public static void main(String[] args) {
    // Accept either 4 or 5 arguments.
    if (args.length < 4 || args.length > 5) {
      throw new IllegalArgumentException(
              "Expected 4 or 5 arguments: redDeckPath, blueDeckPath, redPlayerType, bluePlayerType, [providerForBlue]");
    }
    formatCheck(args);

    List<Card> redDeck = parseDeck(args[0]);
    List<Card> blueDeck = parseDeck(args[1]);
    int totalDeckSize = redDeck.size() + blueDeck.size();
    PawnsBoardModel model = new PawnsBoardModel(5, 5, totalDeckSize, 4);
    cs3500.pawnsboard.model.Player redPlayer = new cs3500.pawnsboard.model.Player("red", redDeck, model);
    cs3500.pawnsboard.model.Player bluePlayer = new cs3500.pawnsboard.model.Player("blue", blueDeck, model);
    PlayerActions redPlayerActions = createPlayerActions(args[2], redPlayer);
    PlayerActions bluePlayerActions = createPlayerActions(args[3], bluePlayer);

    // Use your custom view for the red player.
    PawnsBoardGUIViewI viewRedPlayer = new cs3500.pawnsboard.view.PawnsBoardGUIView(model, redPlayer);

    PawnsBoardGUIViewI viewBluePlayer;
    if (args.length == 5 && args[4].equalsIgnoreCase("provider")) {
      try {
        // Wrap our model via ModelAdapter; note that we pass the hand lists from our Player objects.
        ModelAdapter adaptedModel = new ModelAdapter(model, redPlayer.getHand(), bluePlayer.getHand());
        // Convert blue player's color (a String) into the provider's Player type.
        cs3500.pawnsboard.provider.model.Player providerBlue = convertPlayer(bluePlayer.getColor());
        viewBluePlayer = new ProviderViewAdapter(adaptedModel, providerBlue);
      } catch (IOException e) {
        throw new RuntimeException("Error creating provider view: " + e.getMessage());
      }
    } else {
      viewBluePlayer = new PawnsBoardGUIView(model, bluePlayer);
    }

    PawnsBoardGUIController redController =
            new PawnsBoardGUIController(model, viewRedPlayer, redPlayer, redPlayerActions);
    PawnsBoardGUIController blueController =
            new PawnsBoardGUIController(model, viewBluePlayer, bluePlayer, bluePlayerActions);
    redController.runGame();
    blueController.runGame();
  }

  private static cs3500.pawnsboard.provider.model.Player convertPlayer(String color) {
    if (color.equalsIgnoreCase("red")) {
      return cs3500.pawnsboard.provider.model.Player.RED;
    } else if (color.equalsIgnoreCase("blue")) {
      return cs3500.pawnsboard.provider.model.Player.BLUE;
    }
    return cs3500.pawnsboard.provider.model.Player.NONE;
  }

  private static void formatCheck(String[] args) {
    // Check if there are at least 4 arguments.
    if (args.length < 4) {
      throw new IllegalArgumentException(
              "Expected at least 4 arguments: redDeckPath, blueDeckPath, redPlayerType, bluePlayerType");
    }
    // Check if deck files exist.
    if (!Files.exists(Paths.get(args[0]))) {
      throw new IllegalArgumentException("Red deck file not found: " + args[0]);
    }
    if (!Files.exists(Paths.get(args[1]))) {
      throw new IllegalArgumentException("Blue deck file not found: " + args[1]);
    }
    // Check if player types are valid.
    List<String> validTypes = List.of("human", "controlboard", "fillfirst", "maximizerowscore", "minimax");
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