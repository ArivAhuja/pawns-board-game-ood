package cs3500.pawnsboard.view;

import java.awt.Color;

/**
 * Interface defining a color scheme for the Pawns Board game UI.
 * Different implementations can provide different visual appearances.
 */
public interface ColorScheme {
  /**
   * Gets the background color for a regular cell.
   * @return the background color
   */
  Color getCellBackground();

  /**
   * Gets the background color for a selected cell.
   * @return the background color
   */
  Color getSelectedCellBackground();

  /**
   * Gets the background color for a cell owned by a player.
   * @param playerColor the color of the player ("red" or "blue")
   * @return the background color
   */
  Color getPlayerCellBackground(String playerColor);

  /**
   * Gets the foreground color for text on cells.
   * @param backgroundColor the background color of the cell
   * @return the foreground color
   */
  Color getCellForeground(Color backgroundColor);

  /**
   * Gets the color for pawns belonging to a player.
   * @param playerColor the color of the player ("red" or "blue")
   * @return the pawn color
   */
  Color getPawnColor(String playerColor);

  /**
   * Gets the color for the card fill based on player color and selection state.
   * @param playerColor the color of the player ("red" or "blue")
   * @param selected whether the card is selected
   * @return the card fill color
   */
  Color getCardFillColor(String playerColor, boolean selected);

  /**
   * Gets the foreground color for text on cards.
   * @param backgroundColor the background color of the card
   * @return the foreground color
   */
  Color getCardForeground(Color backgroundColor);

  /**
   * Gets the color for influence grid cells on cards.
   * @param cellType the type of cell ('c', 'C', 'I', 'X', or other)
   * @return the cell color
   */
  Color getInfluenceGridCellColor(char cellType);

  /**
   * Gets the color for the border of Cells.
   * @return the border cell color.
   */
  public Color getBorderCellColor();
}
