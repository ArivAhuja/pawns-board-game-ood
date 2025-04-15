package cs3500.pawnsboard.view;

import java.awt.Color;

/**
 * The default color scheme for the Pawns Board game.
 * Maintains the original color appearance.
 */
public class DefaultColorScheme implements ColorScheme {

  @Override
  public Color getCellBackground() {
    return Color.WHITE;
  }

  @Override
  public Color getSelectedCellBackground() {
    return Color.GREEN;
  }

  @Override
  public Color getPlayerCellBackground(String playerColor) {
    if ("red".equalsIgnoreCase(playerColor)) {
      return new Color(255, 200, 200);  // light red
    } else if ("blue".equalsIgnoreCase(playerColor)) {
      return new Color(200, 200, 255);  // light blue
    } else {
      return Color.WHITE;
    }
  }

  @Override
  public Color getCellForeground(Color backgroundColor) {
    return Color.BLACK;  // Always black in the default scheme
  }

  @Override
  public Color getPawnColor(String playerColor) {
    if ("red".equalsIgnoreCase(playerColor)) {
      return Color.RED;
    } else if ("blue".equalsIgnoreCase(playerColor)) {
      return Color.BLUE;
    } else {
      return Color.BLACK;
    }
  }

  @Override
  public Color getCardFillColor(String playerColor, boolean selected) {
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
    return Color.BLACK;  // Always black in the default scheme
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
    return Color.BLACK;
  }
}