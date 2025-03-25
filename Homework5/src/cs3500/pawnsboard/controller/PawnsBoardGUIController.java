package cs3500.pawnsboard.controller;

import java.util.List;

import cs3500.pawnsboard.model.Move;
import cs3500.pawnsboard.model.PawnsBoardModel;
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
    this.view.addFeatureListener(this);
  }

  public void runGame() {
    this.view.display(true);
  }

  private void updateGameState() {
    if (model.isGameOver()) {
      view.refresh();
      return;
    }
    if (model.checkAutoPass()) {
      updateGameState();
    }
    model.drawCard();
    view.clearSelectedCard();
    view.clearSelectedCell();
    view.refresh();
  }

  @Override
  public void passTurn() {
    model.pass();
    updateGameState();
  }

  @Override
  public void selectedCard(int cardIndex) {
    System.out.println("Card Index: " + cardIndex + " selected.");
    view.refresh();
  }

  @Override
  public void selectedCell(int row, int col) {
    System.out.println("Cell Row: " + row + " Cell Col: " + col + ".");
    view.refresh();
  }

  @Override
  public void placeAttempt(int row, int col, int cardIndex) {
    if (cardIndex == -1){
      System.out.println("Select a card first.");
      return;
    }
    if (row == -1){
      System.out.println("Select a cell first.");
      return;
    }
    boolean success = model.placeCard(row, col, cardIndex);
    if (success) {
      updateGameState();
    }
  }
}
