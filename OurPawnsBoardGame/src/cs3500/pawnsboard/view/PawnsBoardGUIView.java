package cs3500.pawnsboard.view;


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cs3500.pawnsboard.model.Player;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModelI;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

/**
 * A Swing-based GUI view for the PawnsBoard game.
 * This view depends only on the read-only interface of the model.
 */
public class PawnsBoardGUIView extends JFrame implements PawnsBoardGUIViewI {

  private final JPawnsBoardPanel panel;
  private ColorScheme colorScheme;


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
    this.colorScheme = new DefaultColorScheme();
    setupMenuBar();

    // Additional setup (listeners, overlays, etc.) can be added later.
    this.setLocationRelativeTo(null);
  }

  /**
   * Sets up the menu bar with accessibility options.
   */
  private void setupMenuBar() {
    JMenuBar menuBar = new JMenuBar();

    // Create Accessibility menu
    JMenu accessibilityMenu = new JMenu("Accessibility");

    // Create High Contrast toggle item
    JCheckBoxMenuItem highContrastItem = new JCheckBoxMenuItem("High Contrast Mode");
    highContrastItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setColorScheme(new HighContrastColorScheme());
      }
    });

    accessibilityMenu.add(highContrastItem);
    menuBar.add(accessibilityMenu);

    this.setJMenuBar(menuBar);
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

  public void setColorScheme(ColorScheme colorScheme) {
    this.panel.setColorScheme(colorScheme);
  }
}