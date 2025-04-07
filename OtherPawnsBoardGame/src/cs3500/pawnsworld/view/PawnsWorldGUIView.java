package cs3500.pawnsworld.view;

import cs3500.pawnsworld.model.Player;
import java.io.IOException;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import cs3500.pawnsworld.model.PawnsWorldReadOnly;

/**
 * The main GUI view for the Pawns game.
 * Displays the board and the player's hand.
 */
public class PawnsWorldGUIView extends JFrame {
  public final PawnsBoardPanel boardPanel;
  public final PlayerHandPanel handPanel;
  public final Player owner;

  /**
   * Constructs the main game window.
   * @param model The read-only game model.
   */
  public PawnsWorldGUIView(PawnsWorldReadOnly model, Player owner) throws IOException {
    super("Pawns Board Game");
    this.owner = owner;


    this.setLayout(new BorderLayout());


    this.boardPanel = new PawnsBoardPanel(model, owner);
    this.add(boardPanel, BorderLayout.CENTER);


    this.handPanel = new PlayerHandPanel(model, owner);
    this.add(handPanel, BorderLayout.SOUTH);


    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(800, 600);
    this.setVisible(true);
  }

  /**
   * Updates the board and hand views after a move is made.
   */
  protected void updateView() throws IOException {
    this.boardPanel.updateBoard();
    this.handPanel.updateHand();
    this.revalidate();
    this.repaint();
  }

  public PawnsBoardPanel getBoardPanel() {
    return boardPanel;
  }

  public PlayerHandPanel getHandPanel() {
    return handPanel;
  }


}

