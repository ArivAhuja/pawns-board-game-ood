package cs3500.pawnsboard.provider.view;

import cs3500.pawnsboard.provider.model.Player;
import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Component;

import java.util.function.Consumer;
import cs3500.pawnsboard.provider.model.PawnsWorldReadOnly;

/**
 * Represents the player's hand panel in the GUI.
 * Displays the current player's hand and allows card selection.
 */
public class PlayerHandPanel extends JPanel {
  private final PawnsWorldReadOnly model;
  private JButton[] cardButtons;
  private int selectedCardIndex = -1;
  private Consumer<Integer> cardSelectionListener;
  private final Player owner;

  /**
   * Constructs the player hand panel.
   * @param model The read-only game model.
   */
  public PlayerHandPanel(PawnsWorldReadOnly model, Player owner) {
    this.model = model;
    this.owner = owner;

    int handSize = model.getBlueHand().size();
    int columns = Math.max(1, handSize);
    this.setLayout(new GridLayout(0, columns, 2, 4));
    this.setBackground(Color.DARK_GRAY);

    initializeHand();
  }



  /**
   * Sets a listener for card selection events.
   * @param listener A consumer that accepts the selected card index.
   */
  public void setCardSelectionListener(Consumer<Integer> listener) {
    this.cardSelectionListener = listener;
  }

  /**
   * Initialise hand based on Player playing.
   */
  private void initializeHand() {
    this.removeAll();
    int handSize = model.getBlueHand().size();
    cardButtons = new JButton[handSize];
    for (int i = 0; i < handSize; i++) {
      String cardName;
      int cardCost;
      int cardValue;
      Color cardColor;
      String influenceGrid;
      if (owner == Player.BLUE) {
        cardName = model.getBlueHand().get(i).getName();
        cardCost = model.getBlueHand().get(i).getCost();
        cardValue = model.getBlueHand().get(i).getValue();
        influenceGrid = model.getBlueHand().get(i).influenceToString();
        cardColor = Color.BLUE;
      } else {
        cardName = model.getRedHand().get(i).getName();
        cardCost = model.getRedHand().get(i).getCost();
        cardValue = model.getRedHand().get(i).getValue();
        influenceGrid = model.getRedHand().get(i).influenceToString();
        cardColor = Color.RED;
      }
      String formattedInfluence = influenceGrid.replace("\n", "<br/>");
      String buttonText = String.format(
              "<html>%s<br/>Cost: %d | Value: %d<br/><pre>%s</pre></html>",
              cardName, cardCost, cardValue, formattedInfluence);
      JButton cardButton = new JButton(buttonText);
      cardButton.setOpaque(true);
      cardButton.setBorderPainted(false);
      cardButton.setBackground(cardColor);
      cardButton.setForeground(Color.WHITE);
      int cardIndex = i;
      cardButton.addActionListener(e -> handleCardSelection(cardIndex));
      cardButtons[i] = cardButton;
      this.add(cardButton);
    }
    this.revalidate();
    this.repaint();
  }

  /**
   * Handles card selection and updates UI.
   *
   * @param index The selected card index.
   */
  private void handleCardSelection(int index) {
    if (selectedCardIndex == index) {
      resetCardColor(index);
      selectedCardIndex = -1;
      System.out.println("Card deselected: " + index);
    } else {
      if (selectedCardIndex != -1) {
        resetCardColor(selectedCardIndex);
      }
      cardButtons[index].setBackground(Color.WHITE);
      cardButtons[index].setForeground(Color.BLACK);
      selectedCardIndex = index;
      System.out.println("Selected card: " + selectedCardIndex);
    }

    if (cardSelectionListener != null) {
      cardSelectionListener.accept(selectedCardIndex);
    }

    this.revalidate();
    this.repaint();
  }



  /**
   * Resets a card's color based on the player's team.
   * @param index The index of the card in the hand.
   */
  private void resetCardColor(int index) {
    Player currentPlayer = owner;
    if (currentPlayer == Player.BLUE) {
      cardButtons[index].setBackground(Color.BLUE);
    } else {
      cardButtons[index].setBackground(Color.RED);
    }
    cardButtons[index].setForeground(Color.WHITE);
  }

  /**
   * Method to update hand after turn change to display current hand.
   */
  public void updateHand() {
    initializeHand();
  }

  /**
   * Method to get the selected card index.
   * @return selected card index.
   */
  public int getSelectedCardIndex() {
    return selectedCardIndex;
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    for (Component c : this.getComponents()) {
      c.setEnabled(enabled);
    }
  }
}





