package cs3500.pawnsboard.strategy;

import java.util.List;
import cs3500.pawnsboard.model.Board;
import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.Cell;
import cs3500.pawnsboard.model.Move;
import cs3500.pawnsboard.model.PawnsBoardModel;

public class ControlBoardStrategy extends AbstractPawnsBoardStrategy {

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

}