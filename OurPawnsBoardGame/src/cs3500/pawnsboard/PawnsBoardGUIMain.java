package cs3500.pawnsboard;

import cs3500.pawnsboard.model.PawnsBoardModel;
import cs3500.pawnsboard.model.PawnsBoardVariantModel;
import cs3500.pawnsboard.view.ModelAdapter;

import cs3500.pawnsboard.controller.PawnsBoardGUIController;
import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.DeckFileParser;
import cs3500.pawnsboard.model.HumanPlayer;
import cs3500.pawnsboard.model.MachinePlayer;
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
 * Command-line arguments:
 *   args[0] - path to Red's deck configuration file.
 *   args[1] - path to Blue's deck configuration file.
 *   args[2] - Red player type (human, controlboard, fillfirst, maximizerowscore, minimax).
 *   args[3] - Blue player type (same valid types as above).
 *   args[4] (optional) - "provider" to use the provider view.
 *   args[5] (optional) - "variant" to use the variant model.
 *                      — you can pass both in any order.
 */
public class PawnsBoardGUIMain {

  public static void main(String[] args) {
    // enforce correct argument count and validate flags
    formatCheck(args);

    // parse optional flags
    boolean useProvider = false;
    boolean useVariant = false;
    for (int i = 4; i < args.length; i++) {
      String flag = args[i].toLowerCase();
      switch (flag) {
        case "provider":
          if (useProvider) {
            throw new IllegalArgumentException("Duplicate option: provider");
          }
          useProvider = true;
          break;
        case "variant":
          if (useVariant) {
            throw new IllegalArgumentException("Duplicate option: variant");
          }
          useVariant = true;
          break;
        default:
          // this should never happen because formatCheck already filtered invalid flags
          throw new IllegalArgumentException("Unknown option: " + args[i]);
      }
    }

    // Load decks
    List<Card> redDeck = parseDeck(args[0], /*mirror=*/false);
    List<Card> blueDeck = parseDeck(args[1], /*mirror=*/true);
    if (isVariantDeck(redDeck) || isVariantDeck(blueDeck)){
      useVariant = true;
    }

    int totalDeckSize = redDeck.size() + blueDeck.size();

    // Instantiate either the base model or the variant model
    PawnsBoardModel model;
    if (useVariant) {
      model = new PawnsBoardVariantModel(5, 5, totalDeckSize, 4);
    } else {
      model = new PawnsBoardModel(5, 5, totalDeckSize, 4);
    }

    // Wrap players around the model
    Player redPlayer = new Player("red", redDeck, model);
    Player bluePlayer = new Player("blue", blueDeck, model);

    // Create the corresponding PlayerActions
    PlayerActions redActions = createPlayerActions(args[2], redPlayer);
    PlayerActions blueActions = createPlayerActions(args[3], bluePlayer);

    // Create both GUI views
    PawnsBoardGUIViewI viewRed = new PawnsBoardGUIView(model, redPlayer);
    PawnsBoardGUIViewI viewBlue = new PawnsBoardGUIView(model, bluePlayer);

    // If requested, swap out the Blue‐view for the provider adapter
    if (useProvider) {
      ModelAdapter adapter = new ModelAdapter(model, redPlayer, bluePlayer);
      try {
        viewBlue = new ProviderViewAdapter(
                adapter,
                cs3500.pawnsboard.provider.model.Player.BLUE
        );
      } catch (IOException e) {
        e.printStackTrace();
        System.exit(1);
      }
    }

    // start both controllers
    PawnsBoardGUIController redCtrl =
            new PawnsBoardGUIController(model, viewRed, redPlayer, redActions);
    PawnsBoardGUIController blueCtrl =
            new PawnsBoardGUIController(model, viewBlue, bluePlayer, blueActions);

    redCtrl.runGame();
    blueCtrl.runGame();
  }


  private static void formatCheck(String[] args) {
    if (args.length < 4) {
      throw new IllegalArgumentException(
              "Expected at least 4 arguments:\n" +
                      "  redDeckPath, blueDeckPath, redPlayerType, bluePlayerType"
      );
    }
    if (args.length > 6) {
      throw new IllegalArgumentException(
              "Expected at most 6 arguments:\n" +
                      "  redDeckPath, blueDeckPath, redPlayerType, bluePlayerType, [provider], [variant]"
      );
    }

    // Check deck-file existence
    if (!Files.exists(Paths.get(args[0]))) {
      throw new IllegalArgumentException("Red deck file not found: " + args[0]);
    }
    if (!Files.exists(Paths.get(args[1]))) {
      throw new IllegalArgumentException("Blue deck file not found: " + args[1]);
    }

    // Validate player types
    List<String> validTypes = List.of(
            "human", "controlboard", "fillfirst", "maximizerowscore", "minimax"
    );
    if (!validTypes.contains(args[2].toLowerCase())) {
      throw new IllegalArgumentException(
              "Invalid red player type: " + args[2] +
                      ". Valid types: " + String.join(", ", validTypes)
      );
    }
    if (!validTypes.contains(args[3].toLowerCase())) {
      throw new IllegalArgumentException(
              "Invalid blue player type: " + args[3] +
                      ". Valid types: " + String.join(", ", validTypes)
      );
    }

    // Validate any optional flags
    for (int i = 4; i < args.length; i++) {
      String opt = args[i].toLowerCase();
      if (!opt.equals("provider") && !opt.equals("variant")) {
        throw new IllegalArgumentException(
                "Unknown option: " + args[i] +
                        ". Valid options are 'provider' and 'variant'."
        );
      }
    }
  }


  private static List<Card> parseDeck(String deckFilePath, boolean mirror) {
    try {
      String content = Files.readString(Paths.get(deckFilePath));
      DeckFileParser parser = new DeckFileParser();
      List<Card> deck = parser.toDeck(content, mirror);
      Collections.shuffle(deck);
      return deck;
    } catch (IOException e) {
      throw new RuntimeException("Error reading deck file: " + e.getMessage(), e);
    }
  }


  private static PlayerActions createPlayerActions(String playerType, Player player) {
    switch (playerType.toLowerCase()) {
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
        // formatCheck should have caught this already
        throw new IllegalArgumentException("Invalid player type: " + playerType);
    }
  }

  private static boolean isVariantDeck(List<Card> deck) {
    boolean variantDeck = false;
    for (Card card : deck) {
      char[][] grid = card.getInfluenceGrid();
      // Iterate through the 2D array to check for 'U' or 'D'
      for (int i = 0; i < grid.length; i++) {
        for (int j = 0; j < grid[i].length; j++) {
          if (grid[i][j] == 'U' || grid[i][j] == 'D') {
            variantDeck = true;
            break;
          }
        }
        if (variantDeck) {
          break; // Exit the outer loop if we found a variant character
        }
      }
      if (variantDeck) {
        break; // No need to check further cards once we find a variant card
      }
    }
    return variantDeck;
  }
}