package cs3500.pawnsboard;

import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.DeckFileParser;
import cs3500.pawnsboard.model.PawnsBoardModel;
import cs3500.pawnsboard.view.PawnsBoardTextualView;
import cs3500.pawnsboard.controller.PawnsBoardController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Main class for PawnsBoard game.
 */
public class PawnsBoardMain {

  /**
   * Main method to run the PawnsBoard game with a 3x5 board and a starting hand size of 5.
   * The deck configuration is read from a file named "deck.config" in the "docs" folder.
   * @param args command line arguments
   */
  public static void main(String[] args) {
    // Build a path to the deck config file (assumed to be in the docs folder)
    String deckFilePath = "docs" + File.separator + "deck.config";
    try {
      String deckContent = new String(Files.readAllBytes(Paths.get(deckFilePath)));
      DeckFileParser parser = new DeckFileParser();
      List<Card> deck = parser.toDeck(deckContent);

      // Create the game model with a 3x5 board and a starting hand size of 5
      PawnsBoardModel model = new PawnsBoardModel(3, 5, deck, 5);

      // Instantiate the view and controller.
      PawnsBoardTextualView view = new PawnsBoardTextualView(model);
      PawnsBoardController controller = new PawnsBoardController(model, view);

      // Start the game
      controller.startGame();

    }
    catch (IOException e) {
      System.err.println("Error reading deck file: " + e.getMessage());
    }
  }
}