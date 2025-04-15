package cs3500.pawnsboard.view;


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cs3500.pawnsboard.model.Player;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModelI;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;

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
    JMenu accessibilityMenu = new JMenu("Menu");

    // Use radio button menu items instead of checkbox items for mutually exclusive options
    JRadioButtonMenuItem defaultItem = new JRadioButtonMenuItem("Default View");
    JRadioButtonMenuItem highContrastItem = new JRadioButtonMenuItem("High Contrast View");

    // Create a button group to ensure only one view can be selected at a time
    ButtonGroup viewGroup = new ButtonGroup();
    viewGroup.add(defaultItem);
    viewGroup.add(highContrastItem);

    defaultItem.setSelected(true);

    // Add action listeners
    defaultItem.addActionListener(e -> setColorScheme(new DefaultColorScheme()));
    highContrastItem.addActionListener(e -> setColorScheme(new HighContrastColorScheme()));

    // Add a separator before the view options for better organization
    accessibilityMenu.addSeparator();
    accessibilityMenu.add(new JLabel("View Options:"));
    accessibilityMenu.addSeparator();

    // Add the menu items
    accessibilityMenu.add(defaultItem);
    accessibilityMenu.add(highContrastItem);

    // Add the accessibility menu to the menu bar
    menuBar.add(accessibilityMenu);

    // Set the menu bar
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