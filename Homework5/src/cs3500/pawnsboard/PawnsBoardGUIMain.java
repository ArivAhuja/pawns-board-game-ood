package cs3500.pawnsboard;

import cs3500.pawnsboard.controller.PawnsBoardController;
import cs3500.pawnsboard.controller.PawnsBoardGUIController;
import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.DeckFileParser;
import cs3500.pawnsboard.model.PawnsBoardModel;
import cs3500.pawnsboard.view.PawnsBoardGUIView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class PawnsBoardGUIMain {
  public static void main(String[] args) {
    String deckFilePath = "docs" + File.separator + "deck.config";
    try {
      String deckContent = new String(Files.readAllBytes(Paths.get(deckFilePath)));
      DeckFileParser parser = new DeckFileParser();
      List<Card> deck = parser.toDeck(deckContent);
      // Create the game model with a 3x5 board and a starting hand size of 5.
      PawnsBoardModel model = new PawnsBoardModel(3, 5, deck, 5);
      // Instantiate the GUI view using the read-only interface.
      PawnsBoardGUIView guiView = new PawnsBoardGUIView(model);
      PawnsBoardGUIController controller = new PawnsBoardGUIController(model, guiView);
      controller.runGame();
    } catch (IOException e) {
      System.err.println("Error reading deck file: " + e.getMessage());
    }
  }
}