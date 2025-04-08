package cs3500.pawnsboard.provider.view;

import cs3500.pawnsboard.provider.model.BoardElement;
import cs3500.pawnsboard.provider.model.Player;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.function.BiConsumer;

import cs3500.pawnsboard.provider.model.PawnsWorldReadOnly;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Represents the game board panel in the GUI. Displays the board grid and allows cell selection.
 */
public class PawnsBoardPanel extends JPanel {

  private final PawnsWorldReadOnly model;
  private final int rows;
  private final int cols;
  private JPanel[][] cellPanels;
  private int selectedRow = -1;
  private int selectedCol = -1;

  /**
   * Constructs the board panel.
   *
   * @param model The read-only game model.
   */
  public PawnsBoardPanel(PawnsWorldReadOnly model, Player owner) throws IOException {
    this.model = model;
    this.rows = model.getHeight();
    this.cols = model.getWidth();
    this.cellPanels = new JPanel[rows][cols];

    this.setLayout(new GridLayout(rows, cols + 2));
    this.setBackground(Color.DARK_GRAY);

    initializeBoard();
  }



  /**
   * Initializes the board with JPanels representing cells.
   */
  private void initializeBoard() throws IOException {
    for (int r = 0; r < rows; r++) {
      addScoreLabel(r, Player.RED, Color.RED);

      for (int c = 0; c < cols; c++) {
        JPanel cellPanel = new JPanel(new BorderLayout());
        cellPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        cellPanel.setBackground(Color.LIGHT_GRAY);
        cellPanels[r][c] = cellPanel;

        BoardElement element = model.getElement(r, c);
        if (element.hasCard()) {
          String formattedInfluence =
                  element.influenceToString().replace("\n", "<br/>");
          String cardText = String.format(
                  "<html>%s<br/>Cost: %d | Value: %d<br/><pre>%s</pre></html>",
                  element.getName(), element.getCost(), element.getValue(), formattedInfluence);

          JLabel cardLabel = new JLabel(cardText, SwingConstants.CENTER);
          cardLabel.setFont(new Font("Arial", Font.BOLD, 10));
          cardLabel.setForeground(getPlayerColor(element.getPlayerType()));

          cellPanel.add(cardLabel, BorderLayout.CENTER);
        } else {
          int pawnCount = element.pawnCount();
          if (pawnCount > 0) {
            JLabel pawnLabel = createPawnLabel(pawnCount, element.getPlayerType());
            cellPanel.add(pawnLabel, BorderLayout.CENTER);
          }
        }

        int row = r;
        int col = c;
        cellPanel.addMouseListener(new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            handleCellSelection(row, col);
          }
        });

        this.add(cellPanel);
      }

      addScoreLabel(r, Player.BLUE, Color.BLUE);
    }
  }

  /**
   * Adds a score label for a player at a specific row.
   */
  private void addScoreLabel(int row, Player player, Color bgColor) {
    JLabel scoreLabel = new JLabel(String.valueOf(model.calculateScore(row, player)),
            SwingConstants.CENTER);
    scoreLabel.setOpaque(true);
    scoreLabel.setBackground(bgColor);
    scoreLabel.setForeground(Color.WHITE);
    this.add(scoreLabel);
  }

  /**
   * Creates a JLabel for pawns with appropriate styling.
   */
  private JLabel createPawnLabel(int pawnCount, Player owner) {
    JLabel pawnLabel = new JLabel(String.valueOf(pawnCount), SwingConstants.CENTER);
    pawnLabel.setFont(new Font("Arial", Font.BOLD, 16));
    pawnLabel.setForeground(getPlayerColor(owner));

    return pawnLabel;
  }

  /**
   * Returns the appropriate color for a given player.
   */
  private Color getPlayerColor(Player player) {
    if (Player.RED.equals(player)) {
      return Color.RED;
    }
    if (Player.BLUE.equals(player)) {
      return Color.BLUE;
    }
    return Color.BLACK;
  }

  private BiConsumer<Integer, Integer> cellSelectionListener;

  public void setCellSelectionListener(BiConsumer<Integer, Integer> listener) {
    this.cellSelectionListener = listener;
  }

  /**
   * Handles cell selection and highlights the selected cell.
   */
  public void handleCellSelection(int row, int col) {

    if (!this.isEnabled()) {
      return;
    }
    if (selectedRow == row && selectedCol == col) {
      resetCellColor(row, col);
      selectedRow = -1;
      selectedCol = -1;
      System.out.println("Cell deselected.");
    } else {
      if (selectedRow != -1 && selectedCol != -1) {
        resetCellColor(selectedRow, selectedCol);
      }
      cellPanels[row][col].setBackground(Color.WHITE);
      selectedRow = row;
      selectedCol = col;
      System.out.println("Selected cell: (" + row + ", " + col + ")");
    }

    if (cellSelectionListener != null) {
      cellSelectionListener.accept(row, col);
    }
  }

  /**
   * Resets a cell's background to its original color.
   */
  private void resetCellColor(int row, int col) {
    cellPanels[row][col].setBackground(Color.LIGHT_GRAY);
  }

  /**
   * Updates the board view.
   */
  public void updateBoard() throws IOException {
    this.removeAll();
    initializeBoard();
    this.revalidate();
    this.repaint();
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    for (Component c : this.getComponents()) {
      c.setEnabled(enabled);
    }
  }
}

