package cs3500.pawnsboard.view;

import cs3500.pawnsboard.model.PawnsBoardModel;
import cs3500.pawnsboard.model.Cell;
import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.Player;
import cs3500.pawnsboard.provider.model.PawnsWorldReadOnly;
import cs3500.pawnsboard.provider.model.BoardElement;
import cs3500.pawnsboard.provider.model.CustomCard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An adapter that wraps our PawnsBoardModel (plus the hand lists)
 * so that it implements the provider’s PawnsWorldReadOnly.
 */
public class ModelAdapter implements PawnsWorldReadOnly {

  private final PawnsBoardModel model;
  private final Player redPlayer;
  private final Player bluePlayer;

  public ModelAdapter(PawnsBoardModel model, Player redPlayer, Player bluePlayer) {
    this.model = model;
    this.redPlayer = redPlayer;
    this.bluePlayer = bluePlayer;
  }

  // The provider interface requires startGame; our model is already initialized, so we do nothing.
  @Override
  public void startGame(List<CustomCard> deckRed, List<CustomCard> deckBlue, int handSize, boolean shuffle) {
    // No operation: our model is already started.
  }

  @Override
  public int getHeight() {
    return model.getHeight();
  }

  @Override
  public int getWidth() {
    return model.getWidth();
  }

  // Our model’s getWinner() returns a String – provider expects that.
  @Override
  public cs3500.pawnsboard.provider.model.Player getWinner() {
    if (!model.isGameOver()) {
      throw new IllegalStateException("Game is not over");
    }
    if (model.getWinner().equalsIgnoreCase("red")) {
      return cs3500.pawnsboard.provider.model.Player.RED;
    } else if (model.getWinner().equalsIgnoreCase("blue")) {
      return cs3500.pawnsboard.provider.model.Player.BLUE;
    } else {
      return cs3500.pawnsboard.provider.model.Player.NONE;
    }
  }

  @Override
  public BoardElement getElement(int row, int col) {
    Cell cell = model.getCell(row, col);
    return new BoardElementAdapter(cell);
  }

  @Override
  public boolean isGameOver() {
    return model.isGameOver();
  }

  // We convert our model’s current player color to the provider’s Player.
  @Override
  public cs3500.pawnsboard.provider.model.Player getTurn() {
    String color = model.getCurrentPlayerColor();
    if (color.equalsIgnoreCase("red")) {
      return cs3500.pawnsboard.provider.model.Player.RED;
    } else if (color.equalsIgnoreCase("blue")) {
      return cs3500.pawnsboard.provider.model.Player.BLUE;
    }
    return cs3500.pawnsboard.provider.model.Player.NONE;
  }

  @Override
  public int getTotalScore(cs3500.pawnsboard.provider.model.Player player) {
    int[] scores = model.computeScores();
    if (player == cs3500.pawnsboard.provider.model.Player.RED) {
      return scores[0];
    } else if (player == cs3500.pawnsboard.provider.model.Player.BLUE) {
      return scores[1];
    }
    return 0;
  }

  // Convert our red hand (List<Card>) into a list of provider cards.
  @Override
  public List<CustomCard> getRedHand() {
    List<Card> redHand = redPlayer.getHand();
    List<CustomCard> adapted = new ArrayList<>();
    for (Card card : redHand) {
      adapted.add(new CardAdapter(card, cs3500.pawnsboard.provider.model.Player.RED));
    }

    return adapted;
  }

  @Override
  public List<CustomCard> getBlueHand() {
    List<Card> blueHand = bluePlayer.getHand();
    List<CustomCard> adapted = new ArrayList<>();
    for (Card card : blueHand) {
      adapted.add(new CardAdapter(card, cs3500.pawnsboard.provider.model.Player.BLUE));
    }

    return adapted;
  }

  @Override
  public BoardElement[][] getBoard() throws IOException {
    int rows = model.getHeight();
    int cols = model.getWidth();
    BoardElement[][] board = new BoardElement[rows][cols];
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        board[i][j] = new BoardElementAdapter(model.getCell(i, j));
      }
    }
    return board;
  }

  @Override
  public int calculateScore(int row,  cs3500.pawnsboard.provider.model.Player player) {
    int[][] rowScores = model.computeRowScores();
    if (player == cs3500.pawnsboard.provider.model.Player.RED) {
      return rowScores[row][0];
    } else if (player == cs3500.pawnsboard.provider.model.Player.BLUE) {
      return rowScores[row][1];
    }
    return 0;
  }
}