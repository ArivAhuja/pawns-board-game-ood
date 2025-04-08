package cs3500.pawnsboard.provider.textview;


import cs3500.pawnsworld.model.CustomCard;
import cs3500.pawnsboard.provider.model.PawnsWorld;
import cs3500.pawnsboard.provider.model.Player;

/**
 * Textual view for PawnsGame, Representing Red as R and Blue as B.
 */
public class PawnsTextualView implements PawnsWorldTextualView {

  private final PawnsWorld model;

  public PawnsTextualView(PawnsWorld model) {
    this.model = model;
  }

  /**
   * Textual view for PawnsGame.
   * @return textual view for PawnsGame
   */
  @Override
  public String toString() {
    StringBuilder boardString = new StringBuilder();
    for (int index = 0; index < model.getHeight(); index++) {
      boardString.append(model.calculateScore(index, Player.RED) + " ");
      for (int col = 0; col < model.getWidth(); col++) {

        boardString.append(model.getElement(index, col).toString());

      }
      boardString.append(" " + model.calculateScore(index, Player.BLUE) + "\n");
    }
    boardString.append(model.getTurn().toString() + "\n");
    for (CustomCard c : model.getRedHand()) {
      boardString.append(c.getName() + " ");
    }
    boardString.append("\n");
    for (CustomCard c : model.getBlueHand()) {
      boardString.append(c.getName() + " ");
    }
    return boardString + "";
  }
}

