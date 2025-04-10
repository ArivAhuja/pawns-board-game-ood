package cs3500.pawnsboard.provider.model;

import java.util.List;

//import cs3500.pawnsworld.controller.PawnsBoardControllerImpl;
import cs3500.pawnsboard.provider.view.PawnsWorldGUIView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

/**
 * Class for performing actions on command line arguments given through the main function.
 * Determines which players to configure and which strategies to use.
 */
public class PawnsWorldDecider {

  private final String pathOne;

  private final String pathTwo;

  private final String playerRed;

  private final String playerBlue;

  PawnsBoardControllerImpl redController;

  PawnsBoardControllerImpl blueController;


  /**
   * Constructs a PawnWorldDecider object.
   * @param pathOne the file path of the red deck.
   * @param pathTwo the file path of the blue deck.
   * @param playerRed the type of red player.
   * @param playerBlue the type of blue player.
   */

  public PawnsWorldDecider(String pathOne, String pathTwo, String playerRed, String playerBlue) {
    this.pathOne = pathOne;
    this.pathTwo = pathTwo;
    this.playerRed = playerRed;
    this.playerBlue = playerBlue;
  }


  /**
   * Parses the input of the command line by determining which players to use and which strategy
   * to pick.
   * @throws IOException when IO fails to occur.
   * @throws IllegalArgumentException when the given arguments in the constructor are invalid.
   */
  public void parseInput() throws IOException, IllegalArgumentException {
    ConfigReader configReader = new ConfigReader();
    try {
      configReader.read(pathOne, Player.RED, false);
      List<CustomCard> redDeck = configReader.getResultingDeck();

      configReader.read(pathTwo, Player.BLUE, true);
      List<CustomCard> blueDeck = configReader.getResultingDeck();

      checkPlayerLegality(playerRed, playerBlue);

      PawnsGame model = new PawnsGame(5, 7);
      model.startGame(redDeck, blueDeck, 5, false);
      PawnsWorldGUIView redView = new PawnsWorldGUIView(model, Player.RED);
      PawnsWorldGUIView blueView = new PawnsWorldGUIView(model, Player.BLUE);

      if (Objects.equals(playerRed, "human")) {
        this.redController =
                new PawnsBoardControllerImpl(Player.RED, model, redView);
      }
      else {
        this.redController =
                new PawnsBoardControllerImpl(Player.RED, model, stratDecider(playerRed));
      }

      if (Objects.equals(playerBlue, "human")) {
        this.blueController =
                new PawnsBoardControllerImpl(Player.BLUE, model, blueView);
      }
      else {
        this.blueController =
                new PawnsBoardControllerImpl(Player.BLUE, model, stratDecider(playerBlue));
      }

      model.addModelListener(redController);
      model.addModelListener(blueController);

      Player currentTurn = model.getTurn();
      redController.yourTurn(currentTurn);
      blueController.yourTurn(currentTurn);

    } catch (IllegalArgumentException e) {
      throw new FileNotFoundException(e.getMessage());
    }
  }

  private void checkPlayerLegality(String playerRed, String playerBlue)
          throws IllegalArgumentException  {
    if ((checkPlayerLegalityHelper(playerRed) + checkPlayerLegalityHelper(playerBlue)) <= 0) {
      throw new IllegalArgumentException("Bad Player Arguments");
    }
  }

  private int checkPlayerLegalityHelper(String player) {
    switch (player) {
      case "human":
        return 1;
      case "strategy1":
        return 0;
      case "strategy2":
        return 0;
      default:
        return -1;
    }
  }

  private PawnsStrategy stratDecider(String player)  {
    if (Objects.equals(player, "Strategy1"))  {
      return new FillFirst();
    }
    else {
      return new MaximizeRowScore();
    }
  }
}
