package cs3500.pawnsboard.strategy;

import java.util.List;
import cs3500.pawnsboard.model.Board;
import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.Cell;
import cs3500.pawnsboard.model.Move;
import cs3500.pawnsboard.model.PawnsBoardModel;

public class ControlBoardStrategy implements PawnsBoardStrategy {

  @Override
  public Move chooseMove(PawnsBoardModel model, String playerColor) {
    List<Move> legalMoves = model.getLegalMoves();
    if (legalMoves.isEmpty()) {
      return null;
    }

    Move bestMove = null;
    int bestControlled = -1;

    // Iterate over every legal move and simulate its effect.
    for (Move move : legalMoves) {
      int controlled = simulateControlledCells(model, move, playerColor);

      // Choose the move with the maximum number of controlled cells.
      if (controlled > bestControlled) {
        bestControlled = controlled;
        bestMove = move;
      } else if (controlled == bestControlled) {
        // Tie-break: choose the uppermost-leftmost move.
        if (move.getRow() < bestMove.getRow() ||
                (move.getRow() == bestMove.getRow() && move.getCol() < bestMove.getCol())) {
          bestMove = move;
        }
        // Tie-break between cards: choose the move that uses the lower (leftmost) card index.
        else if (move.getRow() == bestMove.getRow() && move.getCol() == bestMove.getCol()
                && move.getCardIndex() < bestMove.getCardIndex()) {
          bestMove = move;
        }
      }
    }
    return bestMove;
  }

  /**
   * Simulates playing the given move on a clone of the board and counts the number of cells
   * that will be owned by the current player after applying the card’s influence.
   *
   * @param model       the current game model
   * @param move        the move to simulate
   * @param playerColor the player's color ("Red" or "Blue")
   * @return the total count of cells owned by the player after simulation
   */
  protected int simulateControlledCells(PawnsBoardModel model, Move move, String playerColor) {
    Board clonedBoard = model.cloneBoard();
    Card card = model.getCurrentPlayer().getHand().get(move.getCardIndex());
    Cell cell = clonedBoard.getCell(move.getRow(), move.getCol());

    // Place the card on the clone.
    cell.placeCard(card, playerColor);

    // Simulate the card's influence.
    simulateInfluenceOnBoard(clonedBoard, move.getRow(), move.getCol(), card, playerColor);

    // Count cells controlled by the player.
    int count = 0;
    for (int row = 0; row < clonedBoard.getRows(); row++) {
      for (int col = 0; col < clonedBoard.getColumns(); col++) {
        if (playerColor.equals(clonedBoard.getCell(row, col).getOwner())) {
          count++;
        }
      }
    }
    return count;
  }

  /**
   * Simulates the application of a card's influence on a board. The influence grid is applied
   * similarly to the other strategies.
   *
   * @param board       the board on which to simulate influence
   * @param cardRow     the row where the card is played
   * @param cardCol     the column where the card is played
   * @param card        the card being played
   * @param playerColor the color of the player
   */
  private void simulateInfluenceOnBoard(Board board, int cardRow, int cardCol, Card card, String playerColor) {
    char[][] grid = card.getInfluenceGrid();
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        if (grid[i][j] != 'I') {
          continue;
        }
        // For Blue players, mirror the influence grid horizontally.
        int effectiveJ = playerColor.equals("Blue") ? 4 - j : j;
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
        } else {
          if (targetCell.getOwner().equals(playerColor)) {
            int newCount = Math.min(targetCell.getPawnCount() + 1, 3);
            targetCell.setPawnCount(newCount);
          } else {
            targetCell.setOwner(playerColor);
          }
        }
      }
    }
  }
}