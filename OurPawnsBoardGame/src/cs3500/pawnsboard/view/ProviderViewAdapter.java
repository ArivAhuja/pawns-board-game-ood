package cs3500.pawnsboard.view;

import cs3500.pawnsboard.provider.view.PawnsWorldGUIView;
import cs3500.pawnsboard.provider.model.Player;         // Provider’s Player type (from cs3500.pawnsworld.model)
import cs3500.pawnsboard.provider.model.PawnsWorldReadOnly;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * An adapter that wraps the provider’s GUI view (PawnsWorldGUIView)
 * so it conforms to our own view interface (PawnsBoardGUIViewI).
 */
public class ProviderViewAdapter implements PawnsBoardGUIViewI {

  private final PawnsWorldGUIView providerView;

  /**
   * Constructs the adapter using a provider–compatible read-only model and provider Player.
   *
   * @param model the provider–compatible read-only model.
   * @param owner the provider’s Player (e.g. Player.BLUE).
   * @throws IOException if the provider view cannot be initialized.
   */
  public ProviderViewAdapter(PawnsWorldReadOnly model, Player owner) throws IOException {
    this.providerView = new PawnsWorldGUIView(model, owner);
  }

  @Override
  public void refresh() {
    try {
      providerView.getBoardPanel().updateBoard();
      providerView.getHandPanel().updateHand();
    } catch (IOException e) {
      e.printStackTrace();
    }
    providerView.revalidate();
    providerView.repaint();
  }

  @Override
  public void display(boolean show) {
    providerView.setVisible(show);
  }

  @Override
  public void addFeatureListener(ViewFeatures features) {
    // Wire the provider’s board panel cell selection to our controller's listener.
    providerView.getBoardPanel().setCellSelectionListener((row, col) -> features.selectedCell(row, col));
    // Wire the provider’s hand panel card selection.
    providerView.getHandPanel().setCardSelectionListener((Integer cardIndex) -> features.selectedCard(cardIndex));
  }

  @Override
  public void clearSelectedCard() {
    // The provider's view does not have an API to clear card selection explicitly; no action here.
  }

  @Override
  public void clearSelectedCell() {
    // Similarly, no corresponding method in the provider view; leave as no-op.
  }
}