package cs3500.pawnsboard.view;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import cs3500.pawnsboard.model.Board;
import cs3500.pawnsboard.model.Cell;
import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.Player;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModelI;

import javax.swing.*;


/**
 * A JPanel that displays the board grid for the Pawns Board game.
 */
public class JPawnsBoardPanel extends JPanel {

  private final ReadonlyPawnsBoardModelI model;
  private final List<ViewFeatures> featuresListeners;
  private int selectedCardIndex;


  /**
   * Constructs a JBoardPanel to display the board from the given model.
   *
   * @param model the read-only model
   */
  public JPawnsBoardPanel(ReadonlyPawnsBoardModelI model) {
    this.model = model;
    this.featuresListeners = new ArrayList<>();
    this.selectedCardIndex = -1;

    setFocusable(true);
    requestFocusInWindow();

    MouseClickListener mouseListener = new MouseClickListener();
    this.addMouseListener(mouseListener);
    this.addMouseMotionListener(mouseListener);
    KeyPressListener keyListener = new KeyPressListener();
    this.addKeyListener(keyListener);

  }

  /**
   * Adds a feature listener (typically the controller) to this view.
   *
   * @param features the listener to add
   */
  public void addFeaturesListener(ViewFeatures features) {
    this.featuresListeners.add(features);
  }


  /**
   * Specifies the logical coordinate system size.
   */
  private Dimension getPreferredLogicalSize() {
    return new Dimension(100, 100);
  }

  /**
   * Specifies the logical coordinate system size.
   */
  @Override
  public Dimension getPreferredSize() {
    return new Dimension(800, 600);
  }

  /**
   * Creates the transformation that converts from logical coordinates
   * (with the origin at the center and a fixed logical size) to physical screen coordinates.
   *
   * @return the AffineTransform for logical to physical conversion
   */
  private AffineTransform transformLogicalToPhysical() {
    AffineTransform ret = new AffineTransform();
    Dimension preferred = getPreferredLogicalSize();
    // Step 1: move the origin to the center of the panel
    ret.translate(getWidth() / 2.0, getHeight() / 2.0);
    // Step 2: scale to match the logical size
    ret.scale(getWidth() / preferred.getWidth(), getHeight() / preferred.getHeight());
    // Step 3: flip the y-axis so that positive y goes upward
    ret.scale(1, -1);
    return ret;
  }

  /**
   * Draws a single card at the given position using dynamic dimensions.
   *
   * @param card       the card to draw
   * @param g2d        the graphics context
   * @param x          the x-coordinate where the card is drawn
   * @param y          the y-coordinate where the card is drawn
   * @param cardWidth  the dynamic width of the card
   * @param cardHeight the dynamic height of the card
   * @param selected   true if this card is currently selected
   */
  private void drawCard(Card card, Graphics2D g2d, int x, int y, int cardWidth, int cardHeight,
                        boolean selected) {
    // Choose fill color based on selection state.
    Color fillColor;
    if (selected) {
      fillColor = Color.GREEN;
    } else {
      String pColor = model.getCurrentPlayer().getColor();
      if (pColor.equalsIgnoreCase("Red")) {
        fillColor = Color.RED;
      } else if (pColor.equalsIgnoreCase("Blue")) {
        fillColor = Color.BLUE;
      } else {
        fillColor = Color.GRAY; // Fallback if player's color isn't recognized.
      }
    }

    // Fill the card's background.
    g2d.setColor(fillColor);
    g2d.fillRect(x, y, cardWidth, cardHeight);

    // Draw the card border.
    g2d.setColor(Color.BLACK);
    g2d.drawRect(x, y, cardWidth, cardHeight);

    // Draw card details (name, cost, value).
    int fontOffset = 5;
    g2d.drawString(card.getName(), x + fontOffset, y + 15);
    g2d.drawString("Cost: " + card.getCost(), x + fontOffset, y + 30);
    g2d.drawString("Val: " + card.getValue(), x + fontOffset, y + 45);

    // Reserve vertical space for the text.
    int textAreaHeight = 50;  // Adjust as needed.
    int availableHeight = cardHeight - textAreaHeight;
    int availableWidth = cardWidth;

    // Determine grid cell size so the 5x5 grid fits.
    int gridCellSize = Math.min(availableWidth, availableHeight) / 5;
    int gridSize = gridCellSize * 5;

    // Center the grid in the remaining area.
    int gridStartX = x + (cardWidth - gridSize) / 2;
    int gridStartY = y + textAreaHeight + ((availableHeight - gridSize) / 2);

    // Draw the 5x5 influence grid.
    char[][] grid = card.getInfluenceGrid();
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        int cellX = gridStartX + j * gridCellSize;
        int cellY = gridStartY + i * gridCellSize;
        g2d.drawRect(cellX, cellY, gridCellSize, gridCellSize);
        char ch = grid[i][j];
        if (ch != 'X') {
          // Center the character within the grid cell.
          FontMetrics fm = g2d.getFontMetrics();
          int charWidth = fm.charWidth(ch);
          int charAscent = fm.getAscent();
          int charX = cellX + (gridCellSize - charWidth) / 2;
          int charY = cellY + (gridCellSize + charAscent) / 2;
          g2d.drawString(String.valueOf(ch), charX, charY);
        }
      }
    }
  }


  /**
   * Draws the entire hand of cards within the given rectangular area.
   * Cards are laid out horizontally with equal spacing, and the dimensions
   * are computed relative to the available area.
   *
   * @param cards  the list of cards to draw
   * @param g2d    the graphics context
   * @param x      the x-coordinate of the hand area
   * @param y      the y-coordinate of the hand area
   * @param width  the width available for drawing the hand
   * @param height the height available for drawing the hand
   */
  private void drawHand(List<Card> cards, Graphics2D g2d, int x, int y, int width, int height) {
    int spacing = 10;
    int n = cards.size();
    // Calculate dynamic card width: available width minus total spacing divided by number of cards.
    int totalSpacing = (n + 1) * spacing;
    int cardWidth = (width - totalSpacing) / n;
    // Option 1: Use the available hand height (minus some vertical spacing) as card height.
    int cardHeight = height - 2 * spacing;
    // Alternatively, if you want a fixed ratio (e.g., 1.4 times wider than tall), you can do:
    // int cardHeight = (int) (cardWidth * 1.4);

    // Center the hand horizontally.
    int startX = x + Math.max(0, (width - (n * cardWidth + totalSpacing)) / 2);

    int currentX = startX + spacing;
    int cardY = y + (height - cardHeight) / 2; // vertical centering
    for (int i = 0; i < cards.size(); i++) {
      Card card = cards.get(i);
      if (selectedCardIndex == i) {
        drawCard(card, g2d, currentX, cardY, cardWidth, cardHeight, true);
      } else {
        drawCard(card, g2d, currentX, cardY, cardWidth, cardHeight, false);
      }
      currentX += cardWidth + spacing;
    }
  }

  /**
   * Draws a single cell of the board at the specified position.
   * If the cell contains a card, its name is drawn;
   * otherwise, if it has pawns, the pawn count is displayed.
   * Also fills the background with a light color based on ownership.
   *
   * @param cell       the cell to draw
   * @param g2d        the graphics context
   * @param x          the x-coordinate where the cell is drawn
   * @param y          the y-coordinate where the cell is drawn
   * @param cellWidth  the width of the cell
   * @param cellHeight the height of the cell
   */
  private void drawCell(Cell cell, Graphics2D g2d, int x, int y, int cellWidth, int cellHeight) {
    // Draw cell border.
    g2d.drawRect(x, y, cellWidth, cellHeight);

    // Fill the background based on the cell's owner.
    String owner = cell.getOwner();
    if ("Red".equals(owner)) {
      g2d.setColor(new Color(255, 200, 200));  // light red
      g2d.fillRect(x, y, cellWidth, cellHeight);
      g2d.setColor(Color.BLACK);
    } else if ("Blue".equals(owner)) {
      g2d.setColor(new Color(200, 200, 255));  // light blue
      g2d.fillRect(x, y, cellWidth, cellHeight);
      g2d.setColor(Color.BLACK);
    }

    // Draw cell content: if a card is placed, show its name; if not, show pawn count.
    if (cell.getCard() != null) {
      g2d.drawString(cell.getCard().getName(), x + 5, y + 15);
    } else if (cell.hasPawns()) {
      g2d.drawString(String.valueOf(cell.getPawnCount()), x + cellWidth / 2 - 5, y + cellHeight / 2);
    }
  }

  /**
   * Draws a score cell (a grey box) and displays the given score centered within it.
   *
   * @param g2d   the graphics context
   * @param score the score to display
   * @param x     the x-coordinate where the score cell is drawn
   * @param y     the y-coordinate where the score cell is drawn
   * @param width the width of the score cell
   * @param height the height of the score cell
   */
  private void drawScoreCell(int score, Graphics2D g2d, int x, int y, int width, int height) {
    // Fill the cell with a grey color.
    Color grey = new Color(200, 200, 200);
    g2d.setColor(grey);
    g2d.fillRect(x, y, width, height);

    // Draw a border around the cell.
    g2d.setColor(Color.BLACK);
    g2d.drawRect(x, y, width, height);

    // Center the score text in the cell.
    String scoreStr = String.valueOf(score);
    FontMetrics fm = g2d.getFontMetrics();
    int textWidth = fm.stringWidth(scoreStr);
    int textHeight = fm.getAscent();  // approximate text height
    int textX = x + (width - textWidth) / 2;
    int textY = y + (height + textHeight) / 2 - fm.getDescent();
    g2d.drawString(scoreStr, textX, textY);
  }


  /**
   * Draws the entire board by iterating over its cells.
   * The board is drawn within the specified rectangle.
   *
   * @param board  the board to draw
   * @param g2d    the graphics context
   * @param x      the x-coordinate where the board drawing starts
   * @param y      the y-coordinate where the board drawing starts
   * @param width  the width available for the board
   * @param height the height available for the board
   */
  private void drawBoard(Board board, Graphics2D g2d, int x, int y, int width, int height) {
    int rows = board.getRows();
    int cols = board.getColumns();
    int cellWidth = width / (cols + 2);
    int cellHeight = height / rows;

    int[][] rowScores = model.computeRowScores();

    for (int row = 0; row < rows; row++) {
      for (int col = -1; col < cols + 1; col++) {
        // Shift by one cellWidth so that col=-1 is drawn at x.
        int cellX = x + (col + 1) * cellWidth;
        int cellY = y + row * cellHeight;
        if (col == -1) {
          drawScoreCell(rowScores[row][0], g2d, cellX, cellY, cellWidth, cellHeight);
        } else if (col == cols) {
          drawScoreCell(rowScores[row][1], g2d, cellX, cellY, cellWidth, cellHeight);
        } else {
          drawCell(board.getCell(row, col), g2d, cellX, cellY, cellWidth, cellHeight);
        }
      }
    }
  }

  /**
   * The panel's painting method.
   * It divides the panel vertically so that the top 65% is for the board and
   * the bottom 35% is for the hand.
   */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g.create();

    int width = getWidth();
    int height = getHeight();

    // Calculate regions: top 65% for the board, bottom 35% for the hand.
    int boardHeight = (int) (height * 0.65);
    int handHeight = height - boardHeight;

    // Draw the board in the top region.
    drawBoard(model.getBoard(), g2d, 0, 0, width, boardHeight);

    // Draw the hand in the bottom region.
    drawHand(model.getCurrentPlayer().getHand(), g2d, 0, boardHeight, width, handHeight);

    g2d.dispose();
  }

  public void clearSelectedCard() {
    this.selectedCardIndex = -1;
  }

  /**
   * Mouse click listener to handle clicks for placing cards.
   * It divides the panel into board region (top 65%) and hand region (bottom 35%)
   * and then computes which board cell or which card was clicked.
   */
  private class MouseClickListener extends MouseAdapter {
    @Override
    public void mouseClicked(MouseEvent e) {
      int panelWidth = getWidth();
      int panelHeight = getHeight();
      int boardHeight = (int) (panelHeight * 0.65);
      int clickX = e.getX();
      int clickY = e.getY();

      // If the click is in the board region.
      if (clickY < boardHeight) {
        // The board region includes two extra columns for scores.
        int cols = model.getBoard().getColumns();
        int rows = model.getBoard().getRows();
        int cellWidth = panelWidth / (cols + 2);
        int cellHeight = boardHeight / rows;
        // Ignore clicks on the left/right score columns.
        if (clickX < cellWidth || clickX > cellWidth * (cols + 1)) {
          return;
        }
        // Adjust clickX by subtracting the left score column.
        int adjustedX = clickX - cellWidth;
        int col = adjustedX / cellWidth;
        int row = clickY / cellHeight;
        for (ViewFeatures vf : featuresListeners) {
          vf.selectedCell(row, col, JPawnsBoardPanel.this.selectedCardIndex);
        }
      } else { // Click in the hand region.
        int handY = boardHeight;
        int handHeight = panelHeight - boardHeight;
        int spacing = 10;
        int n = model.getCurrentPlayer().getHand().size();
        int totalSpacing = (n + 1) * spacing;
        int cardWidth = (panelWidth - totalSpacing) / n;
        int startX = Math.max(0, (panelWidth - (n * cardWidth + totalSpacing)) / 2) + spacing;
        if (clickX < startX || clickX > startX + n * (cardWidth + spacing)) {
          return;
        }
        int cardIndex = (clickX - startX) / (cardWidth + spacing);
        JPawnsBoardPanel.this.selectedCardIndex = cardIndex;
        for (ViewFeatures vf : featuresListeners) {
          vf.selectedCard(cardIndex);
        }
        repaint();
      }
    }
  }

  /**
   * Private inner class for handling key events.
   * When the "P" key is pressed, it triggers a pass action.
   */
  private class KeyPressListener extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent e) {
      if (e.getKeyCode() == KeyEvent.VK_P) {
        // Notify all registered feature listeners that a pass was triggered.
        for (ViewFeatures vf : featuresListeners) {
          vf.passTurn();
        }
        repaint();
      }
    }
  }
}
