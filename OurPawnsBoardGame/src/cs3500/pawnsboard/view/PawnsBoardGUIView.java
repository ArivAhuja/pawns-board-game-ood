package cs3500.pawnsboard.view;


import java.awt.Component;

import cs3500.pawnsboard.model.Player;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModelI;

import javax.swing.JFrame;

/**
 * A Swing-based GUI view for the PawnsBoard game.
 * This view depends only on the read-only interface of the model.
 */
public class PawnsBoardGUIView extends JFrame implements PawnsBoardGUIViewI {

  private final JPawnsBoardPanel panel;

  /**
   * Constructor: creates the GUI window with panels.
   *
   * @param model    The read-only model.
   */
  public PawnsBoardGUIView(ReadonlyPawnsBoardModelI model, Player player) {
    super("Pawns Board Game");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.panel = new JPawnsBoardPanel(model, player);
    this.add(panel);
    this.pack();

    // Additional setup (listeners, overlays, etc.) can be added later.
    this.setLocationRelativeTo(null);
  }

  @Override
  public void refresh() {
    this.panel.repaint();
  }

  @Override
  public void display(boolean show) {
    this.setVisible(show);
  }

  @Override
  public void addFeatureListener(ViewFeatures features) {
    // Hook up the feature listener (for mouse/keyboard events) to all panels.
    this.panel.addFeaturesListener(features);
  }

  @Override
  public void clearSelectedCard() {
    this.panel.clearSelectedCard();
  }

  public void clearSelectedCell() {
    this.panel.clearSelectedCell();
  }

  @Override
  public Component getDialogParent() {
    return this;
  }
}