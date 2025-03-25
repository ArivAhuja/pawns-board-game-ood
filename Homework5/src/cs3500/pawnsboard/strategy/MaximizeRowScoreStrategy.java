package cs3500.pawnsboard.strategy;

import java.util.List;

import cs3500.pawnsboard.model.Move;
import cs3500.pawnsboard.model.PawnsBoardModel;

/**
 * The MaximizeRowScoreStrategy examines every legal move and selects the first one that, when
 * simulated,produces a row score for the current player that is strictly greater than the
 * opponent's row score. It processes rows from top to bottom.
 */
public class MaximizeRowScoreStrategy extends AbstractPawnsBoardStrategy {

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
      // Only consider rows where the current player's score is less than or equal to the
      // opponent's.
      if (currentRowScores[row][playerIdx] <= currentRowScores[row][opponentIdx]) {
        // For each legal move in this row, simulate its effect.
        for (Move move : legalMoves) {
          if (move.getRow() == row) {
            int simulatedScore = simulateRowScoreAfterMove(model, move, playerColor);
            // If the simulated score wins the row (i.e. becomes strictly greater than the
            // opponent's score), choose this move.
            if (simulatedScore > currentRowScores[row][opponentIdx]) {
              return move;
            }
          }
        }
      }
    }

    // If no move in any row produces a winning simulated row score, return null (indicating a
    // pass).
    return null;
  }
}