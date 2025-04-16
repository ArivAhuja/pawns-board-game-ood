package cs3500.pawnsboard.model;

/**
 * Represents the main model of the pawns board game, but with additional features as per the extra
 * credit homework - specifically, the ability to upgrade and devalue influences.
 */
public class PawnsBoardVariantModel extends PawnsBoardModel implements PawnsBoardModelI {

  /**
   * Constructs a new variant model that supports upgrading and devaluing influences.
   */
  public PawnsBoardVariantModel(int rows, int columns, int deckSize, int handSize) {
    super(rows, columns, deckSize, handSize);
    // Variant-specific initialization (if needed)
  }

  /**
   * Overrides the applyInfluence method so that it processes U (upgrading) and D (devaluing)
   * influence cells. The rest of the influence grid cells (e.g. 'I') behave as in the base model.
   */
  @Override
  public void applyInfluence(int cardRow, int cardCol, Card card, String playerColor) {
    char[][] grid = card.getInfluenceGrid();
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        // Skip center cell.
        if (i == 2 && j == 2) continue;

        int dr = i - 2;
        int dc = j - 2;
        int targetRow = cardRow + dr;
        int targetCol = cardCol + dc;
        if (!getBoard().isValidPosition(targetRow, targetCol)) continue;

        Cell targetCell = getBoard().getCell(targetRow, targetCol);
        char influenceChar = grid[i][j];

        // Process based on the symbol
        switch (influenceChar) {
          case 'I': // Regular influence: add a pawn or adjust ownership.
            if (targetCell.getCard() != null) {
              // If a card is already placed, simply change the owner if necessary.
              if (!targetCell.getOwner().equals(playerColor)) {
                targetCell.setOwner(playerColor);
              }
            } else {
              if (!targetCell.hasPawns()) {
                targetCell.setPawnCount(1);
                targetCell.setOwner(playerColor);
              } else {
                if (targetCell.getOwner().equals(playerColor)) {
                  int newCount = Math.min(targetCell.getPawnCount() + 1, 3);
                  targetCell.setPawnCount(newCount);
                } else {
                  targetCell.setOwner(playerColor);
                }
              }
            }
            break;
          case 'U': // Upgrading influence: increase the net modifier by +1.
            targetCell.addInfluence(+1);
            if (targetCell.getCard() != null) {
              updateCardEffectiveValue(targetCell);
            }
            break;
          case 'D': // Devaluing influence: decrease the net modifier by 1.
            targetCell.addInfluence(-1);
            if (targetCell.getCard() != null) {
              updateCardEffectiveValue(targetCell);
            }
            break;
          default:
            // For any other symbol, do nothing.
            break;
        }
      }
    }
  }

  /**
   * Helper method that recomputes the card's effective value based on the cell’s influence.
   * If the value falls to 0 or below, the card is removed and replaced with its pawn cost.
   */
  private void updateCardEffectiveValue(Cell cell) {
    Card card = cell.getCard();
    if (card == null) return;

    int effectiveValue = card.getValue() + cell.getInfluenceModifier();
    if (effectiveValue <= 0) {
      System.out.println("Card " + card.getName() + " is devalued (effective value " + effectiveValue +
              ") on cell. Removing card and placing " + card.getCost() + " pawn(s).");
      // Remove the card.
      cell.placeCard(null, "");
      // Place pawns equal to the card's cost.
      cell.setPawnCount(card.getCost());
      // Reset the cell's influence for future plays.
      cell.resetInfluence();
    }
  }

  @Override
  public int[][] computeRowScores() {
    int rows = getBoard().getRows();
    int cols = getBoard().getColumns();
    int[][] rowScores = new int[rows][2];
    for (int row = 0; row < rows; row++) {
      int redScore = 0;
      int blueScore = 0;
      for (int col = 0; col < cols; col++) {
        Cell cell = getBoard().getCell(row, col);
        if (cell.getCard() != null) {
          // Calculate the effective value using cell influence:
          int effectiveValue = cell.getCard().getValue() + cell.getInfluenceModifier();
          // Effective value below 0 is treated as 0
          if (effectiveValue < 0) {
            effectiveValue = 0;
          }
          if (cell.getOwner().equals("red")) {
            redScore += effectiveValue;
          } else if (cell.getOwner().equals("blue")) {
            blueScore += effectiveValue;
          }
        }
      }
      rowScores[row][0] = redScore;
      rowScores[row][1] = blueScore;
    }
    return rowScores;
  }
}
