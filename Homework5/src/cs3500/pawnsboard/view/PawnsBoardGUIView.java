package cs3500.pawnsboard.view;


import cs3500.pawnsboard.model.ReadonlyPawnsBoardModelI;
import cs3500.pawnsboard.model.Player;

import javax.swing.JFrame;
import java.awt.BorderLayout;

/**
 * A Swing-based GUI view for the PawnsBoard game.
 * This view depends only on the read-only interface of the model.
 */
public class PawnsBoardGUIView extends JFrame implements PawnsBoardGUIViewI {

  private BoardPanel boardPanel;
  private HandPanel leftHandPanel;
  private HandPanel rightHandPanel;

  /**
   * Constructor: creates the GUI window with panels.
   *
   * @param model    The read-only model.
   * @param player1  The first player (e.g. Red).
   * @param player2  The second player (e.g. Blue).
   */
  public PawnsBoardGUIView(ReadonlyPawnsBoardModelI model, Player player1, Player player2) {
    super("Pawns Board Game");
    this.setSize(800, 600);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLayout(new BorderLayout());

    boardPanel = new BoardPanel(model);
    leftHandPanel = new HandPanel(model, player1);
    rightHandPanel = new HandPanel(model, player2);

    this.add(leftHandPanel, BorderLayout.WEST);
    this.add(boardPanel, BorderLayout.CENTER);
    this.add(rightHandPanel, BorderLayout.EAST);

    // Additional setup (listeners, overlays, etc.) can be added later.
    this.setLocationRelativeTo(null);
  }

  @Override
  public void refresh() {
    boardPanel.repaint();
    leftHandPanel.repaint();
    rightHandPanel.repaint();
  }

  @Override
  public void display(boolean show) {
    this.setVisible(show);
  }

  @Override
  public void addFeaturesListener(Object features) {
    // Hook up the feature listener (for mouse/keyboard events) to all panels.
    boardPanel.addFeatureListener(features);
    leftHandPanel.addFeatureListener(features);
    rightHandPanel.addFeatureListener(features);
  }

}