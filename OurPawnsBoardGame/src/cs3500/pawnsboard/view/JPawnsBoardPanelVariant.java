package cs3500.pawnsboard.view;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import cs3500.pawnsboard.model.Cell;
import cs3500.pawnsboard.model.Player;
import cs3500.pawnsboard.model.ReadOnlyPawnsBoardModelVariantI;

public class JPawnsBoardPanelVariant extends JPawnsBoardPanel {

  /**
   * Constructs a variant JBoardPanel.
   *
   * @param model  the read-only model.
   * @param player the view for the player.
   */
  public JPawnsBoardPanelVariant(ReadOnlyPawnsBoardModelVariantI model, Player player) {
    super(model, player);
  }

  /**
   * Override the drawCell method to change its behavior.
   */
  @Override
  protected void drawCell(Cell cell, Graphics2D g2d, int x, int y, int cellWidth, int cellHeight,
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
    // Display influence modifier if it's not zero
    int cellInfluence = cell.getInfluenceModifier();
    if (cellInfluence != 0) {
      // Format the influence value with + or - sign
      String influenceText = (cellInfluence > 0 ? "+" : "") + cellInfluence;

      // Set text color (reuse the same foreground color logic)
      g2d.setColor(colorScheme.getCellForeground(backgroundColor));

      // Calculate position for the text at the bottom of the cell
      FontMetrics fm = g2d.getFontMetrics();
      int textWidth = fm.stringWidth(influenceText);
      int textX = x + (cellWidth - textWidth) / 2;
      int textY = y + cellHeight - fm.getDescent() - 5; // 5 pixels padding from bottom

      // Draw the influence modifier text
      g2d.drawString(influenceText, textX, textY);
    }
  }
}