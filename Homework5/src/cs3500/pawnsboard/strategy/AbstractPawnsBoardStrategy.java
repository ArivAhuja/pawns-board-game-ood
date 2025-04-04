package cs3500.pawnsboard.strategy;

import cs3500.pawnsboard.model.Board;
import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.Cell;
import cs3500.pawnsboard.model.Move;
import cs3500.pawnsboard.model.PawnsBoardModel;
import cs3500.pawnsboard.model.Player;

/**
 * An abstract base class for all Pawns Board strategies that provides
 * common helper methods for evaluating board state and simulating influence.
 */
public abstract class AbstractPawnsBoardStrategy implements PawnsBoardStrategy {

  /**
   * Evaluates the board state from the perspective of playerColor.
   * The heuristic used here is the difference between the number of cells controlled
   * by the player and those controlled by the opponent.
   *
   * @param board the board state to evaluate.
   * @param player the current player ("red" or "blue").
   * @return the evaluation score (higher is better for the player).
   */
  protected int evaluateBoard(Board board, Player player) {
    int playerCount = 0;
    int opponentCount = 0;
    String opponentColor = player.getColor().equals("red") ? "blue" : "red";

    for (int row = 0; row < board.getRows(); row++) {
      for (int col = 0; col < board.getColumns(); col++) {
        String owner = board.getCell(row, col).getOwner();
        if (player.getColor().equals(owner)) {
          playerCount++;
        }
        else if (opponentColor.equals(owner)) {
          opponentCount++;
        }
      }
    }
    return playerCount - opponentCount;
  }

  /**
   * Simulates the application of a card's influence on the board.
   * This method applies the card's influence grid onto the board, updating cells
   * that do not already have a card.
   *
   * @param board the board on which to simulate influence.
   * @param cardRow the row where the card is played.
   * @param cardCol the column where the card is played.
   * @param card the card being played.
   * @param playerColor the player's color ("red" or "blue").
   */
  protected void simulateInfluenceOnBoard(Board board, int cardRow, int cardCol, Card card,
                                          String playerColor) {
    char[][] grid = card.getInfluenceGrid();
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        if (grid[i][j] != 'I') {
          continue;
        }
        // For Blue players, mirror the influence grid horizontally.
        int effectiveJ = playerColor.equals("blue") ? 4 - j : j;
        int dr = i - 2;
        int dc = effectiveJ - 2;
        if (dr == 0 && dc == 0) {
          continue;
        }
        int targetRow = cardRow + dr;
        int targetCol = cardCol + dc;
        if (!board.isValidPosition(targetRow, targetCol)) {
          continue;
        }
        Cell targetCell = board.getCell(targetRow, targetCol);
        // Skip cells that already have a card.
        if (targetCell.getCard() != null) {
          continue;
        }
        if (!targetCell.hasPawns()) {
          targetCell.setPawnCount(1);
          targetCell.setOwner(playerColor);
        }
        else {
          if (targetCell.getOwner().equals(playerColor)) {
            int newCount = Math.min(targetCell.getPawnCount() + 1, 3);
            targetCell.setPawnCount(newCount);
          }
          else {
            targetCell.setOwner(playerColor);
          }
        }
      }
    }
  }

  /**
   * Simulates the effect of applying a move on a clone of the board,
   * and then computes the row score for the row in which the move was played.
   *
   * @param model       the current game model.
   * @param move        the move to simulate.
   * @param player      the current player.
   * @return the simulated row score after the move is applied.
   */
  protected int simulateRowScoreAfterMove(PawnsBoardModel model, Move move, Player player) {
    // Clone the board so that the simulation does not affect the real game state.
    Board clonedBoard = model.cloneBoard();

    // Retrieve the card from the current player's hand based on the move's hand index.
    Card card = player.getHand().get(move.getCardIndex());

    // Place the card on the cloned board at the designated cell.
    Cell cell = clonedBoard.getCell(move.getRow(), move.getCol());
    cell.placeCard(card, player.getColor());

    // Simulate the influence of the card on the cloned board.
    simulateInfluenceOnBoard(clonedBoard, move.getRow(), move.getCol(), card, player.getColor());

    // Now, compute the row score for the row affected by the move.
    int simulatedScore = 0;
    int boardCols = clonedBoard.getColumns();
    for (int col = 0; col < boardCols; col++) {
      Cell currentCell = clonedBoard.getCell(move.getRow(), col);
      if (currentCell.getCard() != null && currentCell.getOwner().equals(player.getColor())) {
        simulatedScore += currentCell.getCard().getValue();
      }
    }
    return simulatedScore;
  }
}