package cs3500.pawnsboard.provider.view;

import cs3500.pawnsboard.provider.model.PawnsWorldReadOnly;
import cs3500.pawnsboard.provider.model.Player;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The main GUI view for the Pawns game.
 * Displays the game board and the player's hand,
 * and delegates user interactions to internal panels.
 */
public class PawnsWorldGUIView extends JFrame implements PawnsView, Features {

  private final PawnsBoardPanel boardPanel;
  private final PlayerHandPanel handPanel;

  /**
   * Constructs the main GUI view window for the game.
   *
   * @param model The read-only game model.
   * @param owner The player who owns this view (used to show the correct hand).
   * @throws IOException if rendering or image loading fails
   */
  public PawnsWorldGUIView(PawnsWorldReadOnly model, Player owner) throws IOException {
    super("Pawns Board Game");

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
   * Refreshes the board and hand panels after a move or turn change.
   *
   * @throws IOException if rendering fails
   */
  @Override
  public void updateView() throws IOException {
    this.boardPanel.updateBoard();
    this.handPanel.updateHand();
    this.revalidate();
    this.repaint();
  }

  @Override
  public PawnsBoardPanel getBoardPanel() {
    return boardPanel;
  }

  @Override
  public PlayerHandPanel getHandPanel() {
    return handPanel;
  }

  @Override
  public void setCellSelectionListener(BiConsumer<Integer, Integer> listener) {
    boardPanel.setCellSelectionListener(listener);
  }

  @Override
  public void setCardSelectionListener(Consumer<Integer> listener) {
    handPanel.setCardSelectionListener(listener);
  }

  @Override
  public void setEnabled(boolean enabled) {
    boardPanel.setEnabled(enabled);
    handPanel.setEnabled(enabled);
  }

  @Override
  public void updateHand() {
    handPanel.updateHand();
  }

  @Override
  public int getSelectedCardIndex() {
    return handPanel.getSelectedCardIndex();
  }
}


