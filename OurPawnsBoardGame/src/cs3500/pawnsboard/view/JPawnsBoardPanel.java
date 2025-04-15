package cs3500.pawnsboard.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import cs3500.pawnsboard.model.Board;
import cs3500.pawnsboard.model.Cell;
import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.Player;
import cs3500.pawnsboard.model.ReadonlyPawnsBoardModelI;
import javax.swing.JPanel;

/**
 * A JPanel that displays the board grid for the Pawns Board game.
 */
public class JPawnsBoardPanel extends JPanel {

  private final ReadonlyPawnsBoardModelI model;
  private final Player player;
  private final List<ViewFeatures> featuresListeners;
  private int selectedCardIndex;
  private int selectedCellRow;
  private int selectedCellCol;
  private ColorScheme colorScheme;


  /**
   * Constructs a JBoardPanel to display the board from the given model.
   *
   * @param model the read-only model
   */
  public JPawnsBoardPanel(ReadonlyPawnsBoardModelI model, Player player) {
    this.model = model;
    this.player = player;
    this.featuresListeners = new ArrayList<>();
    this.selectedCardIndex = -1;
    this.selectedCellRow = -1;
    this.selectedCellCol = -1;
    this.colorScheme = new DefaultColorScheme();

    setFocusable(true);
    requestFocusInWindow();

    MouseClickListener mouseListener = new MouseClickListener();
    this.addMouseListener(mouseListener);
    this.addMouseMotionListener(mouseListener);
    KeyPressListener keyListener = new KeyPressListener();
    this.addKeyListener(keyListener);

  }

  /**
   * Sets the color scheme to use for rendering.
   *
   * @param scheme the color scheme to use
   */
  public void setColorScheme(ColorScheme scheme) {
    this.colorScheme = scheme;
    repaint();  // Repaint with the new color scheme
  }

  /**
   * Adds a feature listener to this view.
   *
   * @param features the listener to add
   */
  public void addFeaturesListener(ViewFeatures features) {
    this.featuresListeners.add(features);
  }


  /**
   * Specifies the logical coordinate system size.
   */
  @Override
  public Dimension getPreferredSize() {
    return new Dimension(800, 600);
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
    Color fillColor = colorScheme.getCardFillColor(player.getColor(), selected);

    // Fill the card's background.
    g2d.setColor(fillColor);
    g2d.fillRect(x, y, cardWidth, cardHeight);

    // Draw the card border.
    g2d.setColor(Color.BLACK);
    g2d.drawRect(x, y, cardWidth, cardHeight);

    g2d.setColor(colorScheme.getCardForeground(fillColor));

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

        // Get the cell color from the color scheme
        char cellType = grid[i][j];
        Color cellColor = colorScheme.getInfluenceGridCellColor(cellType);

        // Fill the cell with the determined color.
        g2d.setColor(cellColor);
        g2d.fillRect(cellX, cellY, gridCellSize, gridCellSize);


        // Draw the cell border.
        g2d.setColor(Color.BLACK);
        g2d.drawRect(cellX, cellY, gridCellSize, gridCellSize);
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
      drawCard(card, g2d, currentX, cardY, cardWidth, cardHeight, selectedCardIndex == i);
      currentX += cardWidth + spacing;
    }
  }

  /**
   * Draws a single cell of the board at the specified position.
   * If the cell contains a card, its value is drawn in the center;
   * otherwise, if it has pawns, circles are drawn to represent the pawns.
   * The cell's background is only colored when a card is placed.
   *
   * @param cell       the cell to draw
   * @param g2d        the graphics context
   * @param x          the x-coordinate where the cell is drawn
   * @param y          the y-coordinate where the cell is drawn
   * @param cellWidth  the width of the cell
   * @param cellHeight the height of the cell
   */
  private void drawCell(Cell cell, Graphics2D g2d, int x, int y, int cellWidth, int cellHeight,
                        boolean selected) {
    Color backgroundColor;
    if (selected) {
      backgroundColor = colorScheme.getSelectedCellBackground();
    } else if (cell.getCard() != null) {
      backgroundColor = colorScheme.getPlayerCellBackground(cell.getOwner());
    } else {
      backgroundColor = colorScheme.getCellBackground();
    }

    // Fill the cell with the background color
    g2d.setColor(backgroundColor);
    g2d.fillRect(x, y, cellWidth, cellHeight);

    // Always draw the cell border in black.
    g2d.setColor(colorScheme.getBorderCellColor());
    g2d.drawRect(x, y, cellWidth, cellHeight);

    // Set foreground color based on background
    g2d.setColor(colorScheme.getCellForeground(backgroundColor));

    // Draw cell content: if a card is placed, show its value; if not, draw pawn circles.
    if (cell.getCard() != null) {
      // Draw the card's value in the center of the cell.
      String valueString = String.valueOf(cell.getCard().getValue());
      FontMetrics fm = g2d.getFontMetrics();
      int textWidth = fm.stringWidth(valueString);
      int textHeight = fm.getAscent(); // for better vertical alignment
      int textX = x + (cellWidth - textWidth) / 2;
      int textY = y + (cellHeight + textHeight) / 2;
      g2d.drawString(valueString, textX, textY);
    } else if (cell.hasPawns()) {
      int pawnCount = cell.getPawnCount();
      // Use a smaller diameter for pawn circles.
      int diameter = Math.min(cellWidth, cellHeight) / 4;

      // Set pawn color based on owner using the color scheme
      g2d.setColor(colorScheme.getPawnColor(cell.getOwner()));

      if (pawnCount == 1) {
        // Center a single circle.
        int circleX = x + (cellWidth - diameter) / 2;
        int circleY = y + (cellHeight - diameter) / 2;
        g2d.fillOval(circleX, circleY, diameter, diameter);
      } else if (pawnCount == 2) {
        // Two circles arranged horizontally.
        int spacing = (cellWidth - 2 * diameter) / 3;
        int circleY = y + (cellHeight - diameter) / 2;
        int circleX1 = x + spacing;
        int circleX2 = x + spacing * 2 + diameter;
        g2d.fillOval(circleX1, circleY, diameter, diameter);
        g2d.fillOval(circleX2, circleY, diameter, diameter);
      } else if (pawnCount == 3) {
        // Three circles in a row.
        int spacing = (cellWidth - 3 * diameter) / 4;
        int circleY = y + (cellHeight - diameter) / 2;
        int circleX1 = x + spacing;
        int circleX2 = x + spacing * 2 + diameter;
        int circleX3 = x + spacing * 3 + 2 * diameter;
        g2d.fillOval(circleX1, circleY, diameter, diameter);
        g2d.fillOval(circleX2, circleY, diameter, diameter);
        g2d.fillOval(circleX3, circleY, diameter, diameter);
      }
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
          if ((selectedCellRow == row) && (selectedCellCol == col)) {
            drawCell(board.getCell(row, col), g2d, cellX, cellY, cellWidth, cellHeight, true);
          } else {
            drawCell(board.getCell(row, col), g2d, cellX, cellY, cellWidth, cellHeight, false);
          }
        }
      }
    }
  }

  /**
   * Draws a game-over screen with the winner's message and final scores.
   */
  private void drawGameOver(Graphics2D g2d) {
    int width = getWidth();
    int height = getHeight();

    // Compute final scores and determine the winner.
    int[] scores = model.computeScores();
    String winnerMessage = model.getWinner(); // e.g., "Red wins!", "Blue wins!" or "It's a tie!"

    // Determine background color based on the winner.
    Color bgColor;
    if (winnerMessage.contains("Red")) {
      bgColor = Color.RED;
    } else if (winnerMessage.contains("Blue")) {
      bgColor = Color.BLUE;
    } else {
      bgColor = Color.GRAY;
    }

    // Fill the entire screen with the background color.
    g2d.setColor(bgColor);
    g2d.fillRect(0, 0, width, height);

    // Prepare messages to display.
    String gameOverStr = "Game Over.";
    String scoresTitle = "Final Scores:";
    String redScoreStr = "Red: " + scores[0];
    String blueScoreStr = "Blue: " + scores[1];

    // Set up fonts.
    Font titleFont = new Font("SansSerif", Font.BOLD, 48);
    Font scoreFont = new Font("SansSerif", Font.PLAIN, 24);

    // Draw game over and winner messages in white.
    g2d.setColor(Color.WHITE);
    g2d.setFont(titleFont);
    FontMetrics fmTitle = g2d.getFontMetrics();

    // Calculate starting Y position for centered text.
    int totalTitleHeight = fmTitle.getHeight() * 2; // for "Game Over." and the winner message
    int startY = height / 2 - totalTitleHeight;

    // Center and draw "Game Over."
    int gameOverWidth = fmTitle.stringWidth(gameOverStr);
    int gameOverX = (width - gameOverWidth) / 2;
    int gameOverY = startY;
    g2d.drawString(gameOverStr, gameOverX, gameOverY);

    // Center and draw winner message just below.
    int winnerWidth = fmTitle.stringWidth(winnerMessage);
    int winnerX = (width - winnerWidth) / 2;
    int winnerY = gameOverY + fmTitle.getHeight();
    g2d.drawString(winnerMessage, winnerX, winnerY);

    // Draw final scores below the winner message.
    g2d.setFont(scoreFont);
    FontMetrics fmScore = g2d.getFontMetrics();

    int scoresTitleWidth = fmScore.stringWidth(scoresTitle);
    int scoresTitleX = (width - scoresTitleWidth) / 2;
    int scoresTitleY = winnerY + fmScore.getHeight() + 20;
    g2d.drawString(scoresTitle, scoresTitleX, scoresTitleY);

    int redScoreWidth = fmScore.stringWidth(redScoreStr);
    int redScoreX = (width - redScoreWidth) / 2;
    int redScoreY = scoresTitleY + fmScore.getHeight() + 10;
    g2d.drawString(redScoreStr, redScoreX, redScoreY);

    int blueScoreWidth = fmScore.stringWidth(blueScoreStr);
    int blueScoreX = (width - blueScoreWidth) / 2;
    int blueScoreY = redScoreY + fmScore.getHeight() + 10;
    g2d.drawString(blueScoreStr, blueScoreX, blueScoreY);
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

    if (model.isGameOver()) {
      drawGameOver(g2d);
    }
    else {
      // Calculate regions: top 65% for the board, bottom 35% for the hand.
      int boardHeight = (int) (height * 0.65);
      int handHeight = height - boardHeight;

      // Draw the board in the top region.
      drawBoard(model.getBoard(), g2d, 0, 0, width, boardHeight);

      // Draw the hand in the bottom region.
      drawHand(player.getHand(), g2d, 0, boardHeight, width, handHeight);

      g2d.dispose();
    }
  }

  public void clearSelectedCard() {
    this.selectedCardIndex = -1;
  }

  public void clearSelectedCell() {
    this.selectedCellRow = -1;
    this.selectedCellCol = -1;
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
        JPawnsBoardPanel.this.selectedCellRow = row;
        JPawnsBoardPanel.this.selectedCellCol = col;
        for (ViewFeatures vf : featuresListeners) {
          vf.selectedCell(row, col);
        }
        repaint();
      } else { // Click in the hand region.
        int spacing = 10;
        int n = player.getHand().size();
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
      if (e.getKeyCode() == KeyEvent.VK_C) {
        for (ViewFeatures vf : featuresListeners) {
          vf.placeAttempt(JPawnsBoardPanel.this.selectedCellRow,
                  JPawnsBoardPanel.this.selectedCellCol, JPawnsBoardPanel.this.selectedCardIndex);
        }
        repaint();
      }
    }
  }
}
