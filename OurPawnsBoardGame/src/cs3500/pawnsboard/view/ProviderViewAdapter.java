package cs3500.pawnsboard.view;

import cs3500.pawnsboard.provider.view.PawnsWorldGUIView;
import cs3500.pawnsboard.provider.model.PawnsWorldReadOnly;
import java.io.IOException;

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
  public ProviderViewAdapter(PawnsWorldReadOnly model, cs3500.pawnsboard.provider.model.Player owner) throws IOException {
    this.providerView = new PawnsWorldGUIView(model, owner);
  }

  @Override
  public void refresh() {
    System.out.println("refresh");
    try {
      providerView.getBoardPanel().updateBoard();
      providerView.getHandPanel().updateHand();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void display(boolean show) {
    providerView.setVisible(show);
  }

  @Override
  public void addFeatureListener(ViewFeatures features) {
    // Wire the provider’s board panel cell selection to our controller's listener.
    providerView.getBoardPanel().setCellSelectionListener((row, col) ->
            features.selectedCell(row, col));
    // Wire the provider’s hand panel card selection.
    providerView.getHandPanel().setCardSelectionListener((Integer cardIndex) ->
            features.selectedCard(cardIndex));
  }

  @Override
  public void clearSelectedCard() {
    // The provider's view does not have an API to clear card selection explicitly; no action here.

  }

  @Override
  public void clearSelectedCell() {
    providerView.updateHand();
  }
}