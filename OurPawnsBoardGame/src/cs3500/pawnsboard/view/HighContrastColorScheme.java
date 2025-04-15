package cs3500.pawnsboard.view;

import java.awt.Color;

/**
 * A high contrast color scheme for the Pawns Board game to improve accessibility.
 * Provides colors with maximum contrast for better visibility.
 */
public class HighContrastColorScheme implements ColorScheme {

  @Override
  public Color getCellBackground() {
    return Color.BLACK;  // Pure black background for cells
  }

  @Override
  public Color getSelectedCellBackground() {
    return Color.YELLOW;  // Yellow for highlighted cells
  }

  @Override
  public Color getPlayerCellBackground(String playerColor) {
    if ("red".equalsIgnoreCase(playerColor)) {
      return Color.RED;  // Pure red for the Red player
    } else if ("blue".equalsIgnoreCase(playerColor)) {
      return Color.CYAN;  // Cyan for the Blue player
    } else {
      return Color.BLACK;  // Default to black
    }
  }

  @Override
  public Color getCellForeground(Color backgroundColor) {
    // Use black text on light backgrounds, white text on dark backgrounds
    if (backgroundColor.equals(Color.YELLOW) ||
            backgroundColor.equals(Color.RED) ||
            backgroundColor.equals(Color.CYAN)) {
      return Color.BLACK;  // Black text on highlighted or player-owned cells
    } else {
      return Color.WHITE;  // White text on black cells for maximum contrast
    }
  }

  @Override
  public Color getPawnColor(String playerColor) {
    if ("red".equalsIgnoreCase(playerColor)) {
      return Color.RED;  // Pure red for Red player's pawns
    } else if ("blue".equalsIgnoreCase(playerColor)) {
      return Color.CYAN;  // Cyan for Blue player's pawns
    } else {
      return Color.WHITE;  // Default to white for maximum contrast
    }
  }

  @Override
  public Color getCardFillColor(String playerColor, boolean selected) {
    // Note: The cards aren't part of the board, so we're keeping the original colors
    // as per the assignment requirements
    if (selected) {
      return Color.GREEN;
    } else {
      if ("red".equalsIgnoreCase(playerColor)) {
        return Color.RED;
      } else if ("blue".equalsIgnoreCase(playerColor)) {
        return Color.BLUE;
      } else {
        return Color.GRAY;
      }
    }
  }

  @Override
  public Color getCardForeground(Color backgroundColor) {
    // Keep the original card text color behavior since cards aren't part of the board
    return Color.BLACK;
  }

  @Override
  public Color getInfluenceGridCellColor(char cellType) {
    if (cellType == 'c' || cellType == 'C') {
      return Color.ORANGE;
    } else if (cellType == 'I') {
      return Color.CYAN;
    } else if (cellType == 'X') {
      return Color.DARK_GRAY;
    } else if (cellType == 'D') {
      return Color.MAGENTA;
    } else if (cellType == 'U') {
      return Color.GREEN;
    } else {
      return Color.WHITE;
    }
  }

  @Override
  public Color getBorderCellColor() {
    return Color.WHITE;
  }
}
