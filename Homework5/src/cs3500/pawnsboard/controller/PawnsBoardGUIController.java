package cs3500.pawnsboard.controller;


import javax.swing.JOptionPane;

import cs3500.pawnsboard.model.ModelStatusListener;
import cs3500.pawnsboard.model.Move;
import cs3500.pawnsboard.model.PawnsBoardModel;
import cs3500.pawnsboard.model.Player;
import cs3500.pawnsboard.model.PlayerActions;
import cs3500.pawnsboard.view.PawnsBoardGUIView;
import cs3500.pawnsboard.view.ViewFeatures;

/**
 * The {@code PawnsBoardGUIController} class handles user interactions in the GUI for the Pawns
 * Board game.
 * It coordinates between the game model and the GUI view, processing user actions and updating the
 * game state.
 * This controller implements both {@link PawnsBoardGUIControllerI} and {@link ViewFeatures} to
 * ensure proper
 * communication with the model and view.
 */
public class PawnsBoardGUIController implements PawnsBoardGUIControllerI, ViewFeatures,
        ModelStatusListener {
  private final PawnsBoardModel model;
  private final PawnsBoardGUIView view;
  private final Player player;
  private final PlayerActions playerActions;

  /**
   * Constructs the GUI controller and registers it with the view.
   *
   * @param model the game model
   * @param view  the GUI view
   */
  public PawnsBoardGUIController(PawnsBoardModel model, PawnsBoardGUIView view, Player player,
                                 PlayerActions playerActions) {
    this.model = model;
    this.view = view;
    this.player = player;
    this.playerActions = playerActions;
    this.view.addFeatureListener(this);
    // Register as a listener for model-status events:
    this.model.addModelStatusListener(this);
  }

  public void runGame() {
    this.view.display(true);
    updateGameState();
  }

  private void AIMove() {
    // can add delays and highlighting view to make it seem like really playing
    Move aiMove = playerActions.getNextMove(model);
    if (aiMove != null) {
      player.placeCard(aiMove.getRow(), aiMove.getCol(), aiMove.getCardIndex());
    } else if (playerActions.humanOrMachine().equals("machine")) {
      passTurn();
    }
  }

  private void updateGameState() {
    if (model.isGameOver()) {
      view.refresh();
      return;
    }
    if (model.getCurrentPlayerColor().equals(player.getColor())) {
      this.view.display(true);
      player.drawCard();
      // checkAutoPass checks as well as triggers the pass if necessary
      boolean autoPass = player.checkAutoPass();
      if (!autoPass) {
        AIMove();
      }
      view.clearSelectedCard();
      view.clearSelectedCell();
      view.refresh();
    } else {
      this.view.display(false);
    }
  }

  @Override
  public void passTurn() {
    System.out.println(player.getColor() + " passes.");
    model.pass();
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
    try {
      if (cardIndex == -1) {
        throw new IllegalStateException("Select a card first.");
      }
      if (row == -1) {
        throw new IllegalStateException("Select a cell first.");
      }
      player.placeCard(row, col, cardIndex);
    } catch (IllegalStateException | IllegalArgumentException e) {
      JOptionPane.showMessageDialog(
              view,
              e.getMessage(),
              "Invalid Move",
              JOptionPane.WARNING_MESSAGE
      );
    }
  }

  @Override
  public void turnChanged() {
    if (model.getCurrentPlayerColor().equals(player.getColor())) {
      updateGameState();
    }
  }

  @Override
  public void gameOver(String result) {
    // so will only print once
    if (model.getCurrentPlayerColor().equals(player.getColor())) {
      System.out.println("Game over: " + result);
      view.refresh();
    }
  }
}
