package cs3500.pawnsboard.controller;

import java.util.List;

import cs3500.pawnsboard.model.Move;
import cs3500.pawnsboard.model.PawnsBoardModel;
import cs3500.pawnsboard.model.Player;
import cs3500.pawnsboard.view.PawnsBoardGUIView;
import cs3500.pawnsboard.view.ViewFeatures;

public class PawnsBoardGUIController implements PawnsBoardGUIControllerI, ViewFeatures {
  private final PawnsBoardModel model;
  private final PawnsBoardGUIView view;

  /**
   * Constructs the GUI controller and registers it with the view.
   *
   * @param model the game model
   * @param view  the GUI view
   */
  public PawnsBoardGUIController(PawnsBoardModel model, PawnsBoardGUIView view) {
    this.model = model;
    this.view = view;
  }

  @Override
  public void startGame() {
    view.repaint();
  }

  @Override
  public void passTurn() {
    model.pass();
    view.clearSelectedCard();
    view.refresh();
  }

  @Override
  public void selectedCard(int CardIndex) {
    // Highlighting logic is in view
    view.refresh();
  }

  @Override
  public void selectedCell(int row, int col, int cardIndex) {
    Move attemptedMove = new Move(row, col, cardIndex);
    boolean success = model.placeCard(row, col, cardIndex);
  }
}
