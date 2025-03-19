package cs3500.pawnsboard.strategy;

import java.util.List;

import cs3500.pawnsboard.model.Board;
import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.Cell;
import cs3500.pawnsboard.model.Move;
import cs3500.pawnsboard.model.PawnsBoardModel;
import cs3500.pawnsboard.strategy.PawnsBoardStrategy;

/**
 * The MaximizeRowScoreStrategy examines every legal move and selects the first one that, when simulated,
 * produces a row score for the current player that is strictly greater than the opponent's row score.
 * It processes rows from top to bottom.
 */
public class MaximizeRowScoreStrategy implements PawnsBoardStrategy {

  @Override
  public Move chooseMove(PawnsBoardModel model, String playerColor) {
    List<Move> legalMoves = model.getLegalMoves();
    if (legalMoves.isEmpty()) {
      return null; // No valid moves available: the player should pass.
    }

    // Get the current row scores from the model.
    int[][] currentRowScores = model.computeRowScores();
    int boardRows = model.getBoard().cloneBoard().getRows();
    // Determine indices: assume row score array uses index 0 for Red and 1 for Blue.
    int playerIdx = playerColor.equals("Red") ? 0 : 1;
    int opponentIdx = playerColor.equals("Red") ? 1 : 0;

    // Process rows from top to bottom.
    for (int row = 0; row < boardRows; row++) {
      // Only consider rows where the current player's score is less than or equal to the opponent's.
      if (currentRowScores[row][playerIdx] <= currentRowScores[row][opponentIdx]) {
        // For each legal move in this row, simulate its effect.
        for (Move move : legalMoves) {
          if (move.getRow() == row) {
            int simulatedScore = simulateRowScoreAfterMove(model, move, playerColor);
            // If the simulated score wins the row (i.e. becomes strictly greater than the opponent's score), choose this move.
            if (simulatedScore > currentRowScores[row][opponentIdx]) {
              return move;
            }
          }
        }
      }
    }

    // If no move can flip a row's score, fall back to returning the first legal move.
    return legalMoves.get(0);
  }

  /**
   * Simulates the effect of applying a move on a clone of the board,
   * and then computes the row score for the row in which the move was played.
   *
   * @param model       the current game model.
   * @param move        the move to simulate.
   * @param playerColor the color of the current player.
   * @return the simulated row score after the move is applied.
   */
  private int simulateRowScoreAfterMove(PawnsBoardModel model, Move move, String playerColor) {
    // Clone the board so that the simulation does not affect the real game state.
    Board clonedBoard = model.cloneBoard();

    // Retrieve the card from the current player's hand based on the move's hand index.
    Card card = model.getCurrentPlayer().getHand().get(move.getCardIndex());

    // Place the card on the cloned board at the designated cell.
    Cell cell = clonedBoard.getCell(move.getRow(), move.getCol());
    cell.placeCard(card, playerColor);

    // Simulate the influence of the card on the cloned board.
    simulateInfluenceOnBoard(clonedBoard, move.getRow(), move.getCol(), card, playerColor);

    // Now, compute the row score for the row affected by the move.
    int simulatedScore = 0;
    int boardCols = clonedBoard.getColumns();
    for (int col = 0; col < boardCols; col++) {
      Cell currentCell = clonedBoard.getCell(move.getRow(), col);
      if (currentCell.getCard() != null && currentCell.getOwner().equals(playerColor)) {
        simulatedScore += currentCell.getCard().getValue();
      }
    }
    return simulatedScore;
  }

  /**
   * Simulates the application of a card's influence on a given board.
   * This mimics the applyInfluence logic in the model.
   *
   * @param clonedBoard the cloned board on which to simulate influence.
   * @param cardRow     the row where the card is placed.
   * @param cardCol     the column where the card is placed.
   * @param card        the card being played.
   * @param playerColor the color of the player.
   */
  private void simulateInfluenceOnBoard(Board clonedBoard, int cardRow, int cardCol, Card card, String playerColor) {
    char[][] grid = card.getInfluenceGrid();
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        // Only process cells marked with 'I' for influence.
        if (grid[i][j] != 'I') {
          continue;
        }
        // For Blue players, mirror the influence grid horizontally.
        int effectiveJ = playerColor.equals("Blue") ? 4 - j : j;
        int dr = i - 2;
        int dc = effectiveJ - 2;
        // Skip the center cell (where the card is placed).
        if (dr == 0 && dc == 0) {
          continue;
        }
        int targetRow = cardRow + dr;
        int targetCol = cardCol + dc;
        if (!clonedBoard.isValidPosition(targetRow, targetCol)) {
          continue;
        }
        Cell targetCell = clonedBoard.getCell(targetRow, targetCol);
        // Do not change cells that already have a card.
        if (targetCell.getCard() != null) {
          continue;
        }
        // If the cell is empty, add one pawn.
        if (!targetCell.hasPawns()) {
          targetCell.setPawnCount(1);
          targetCell.setOwner(playerColor);
        } else {
          // If the cell already has pawns:
          if (targetCell.getOwner().equals(playerColor)) {
            int newCount = Math.min(targetCell.getPawnCount() + 1, 3);
            targetCell.setPawnCount(newCount);
          } else {
            // Switch the ownership to the current player.
            targetCell.setOwner(playerColor);
          }
        }
      }
    }
  }
}