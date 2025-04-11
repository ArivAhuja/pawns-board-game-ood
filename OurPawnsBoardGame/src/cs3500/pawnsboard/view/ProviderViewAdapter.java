package cs3500.pawnsboard.view;

import cs3500.pawnsboard.provider.view.PawnsWorldGUIView;
import cs3500.pawnsboard.provider.model.PawnsWorldReadOnly;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * An adapter that wraps the provider's GUI view (PawnsWorldGUIView)
 * so it conforms to our own view interface (PawnsBoardGUIViewI).
 */
public class ProviderViewAdapter implements PawnsBoardGUIViewI {
  private final PawnsWorldGUIView providerView;
  private ViewFeatures features;
  private int selectedRow = -1;
  private int selectedCol = -1;
  private int selectedCard = -1;

  /**
   * Constructs the adapter using a provider–compatible read-only model and provider Player.
   *
   * @param model the provider–compatible read-only model.
   * @param owner the provider's Player (e.g. Player.BLUE).
   * @throws IOException if the provider view cannot be initialized.
   */
  public ProviderViewAdapter(PawnsWorldReadOnly model, cs3500.pawnsboard.provider.model.Player owner) throws IOException {
    this.providerView = new PawnsWorldGUIView(model, owner);

    // Add key bindings to the provider view's root pane
    JRootPane rootPane = providerView.getRootPane();

    // Add 'C' key binding for placement confirmation
    InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap actionMap = rootPane.getActionMap();

    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, 0), "placeCard");
    actionMap.put("placeCard", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (features != null && selectedRow != -1 && selectedCol != -1 && selectedCard != -1) {
          features.placeAttempt(selectedRow, selectedCol, selectedCard);
        }
      }
    });

    // Add 'P' key binding for passing turn
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), "passTurn");
    actionMap.put("passTurn", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (features != null) {
          features.passTurn();
        }
      }
    });
  }

  @Override
  public void refresh() {
    try {
      providerView.updateView();
      providerView.repaint();
    } catch (IOException e) {
      throw new RuntimeException("Failed to update view", e);
    }
  }

  @Override
  public void display(boolean show) {
    providerView.setVisible(show);
    providerView.setEnabled(show);
  }

  @Override
  public void addFeatureListener(ViewFeatures features) {
    this.features = features;

    // Wire the provider's board panel cell selection
    providerView.getBoardPanel().setCellSelectionListener((row, col) -> {
      selectedRow = row;
      selectedCol = col;
      features.selectedCell(row, col);
      providerView.getBoardPanel().repaint();
    });

    // Wire the provider's hand panel card selection
    providerView.getHandPanel().setCardSelectionListener(cardIndex -> {
      selectedCard = cardIndex;
      features.selectedCard(cardIndex);
      providerView.getHandPanel().repaint();
    });
  }

  @Override
  public void clearSelectedCard() {
    selectedCard = -1;
    try {
      providerView.updateHand();
      providerView.getHandPanel().repaint();
    } catch (Exception e) {
      this.refresh();
    }
  }

  @Override
  public void clearSelectedCell() {
    selectedRow = -1;
    selectedCol = -1;
    providerView.getBoardPanel().repaint();
    this.refresh();
  }
}