package cs3500.pawnsboard.strategy;

import java.util.List;
import cs3500.pawnsboard.model.Board;
import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.Cell;
import cs3500.pawnsboard.model.Move;
import cs3500.pawnsboard.model.PawnsBoardModel;
import cs3500.pawnsboard.model.Player;

/**
 * The {@code MiniMaxStrategy} class implements a simple minimax-based strategy for the Pawns Board
 * game.
 * <p>
 * This strategy evaluates each legal move by simulating its effect on a cloned board, applying the
 * card's influence,
 * and then using a heuristic evaluation function. The heuristic used here is defined as:
 * (number of cells owned by the player) - (number of cells owned by the opponent). The move with
 * the highest evaluation
 * is selected. In the event of a tie, the first move in the legal moves list is chosen.
 * </p>
 */
public class MiniMaxStrategy extends AbstractPawnsBoardStrategy {

  @Override
  public Move chooseMove(PawnsBoardModel model, Player player) {
    List<Move> legalMoves = player.getLegalMoves();
    if (legalMoves.isEmpty()) {
      return null;
    }

    // set our current best to minimum
    Move bestMove = null;
    int bestValue = Integer.MIN_VALUE;

    // iterate over every legal move and simulate its effect
    for (Move move : legalMoves) {
      Board boardClone = model.cloneBoard();

      // retreive the card from the current player's hand that will be played
      Card card = player.getHand().get(move.getCardIndex());

      // place the card at the designated cell on the cloned board
      Cell cell = boardClone.getCell(move.getRow(), move.getCol());
      cell.placeCard(card, player.getColor());

      // simulate the card’s influence on surrounding cells
      simulateInfluenceOnBoard(boardClone, move.getRow(), move.getCol(), card, player.getColor());

      // evaluate the board state using a simple heuristic, in this iteration:
      // (number of cells owned by the player) - (number of cells owned by the opponent)
      // in the future we can add more complex, but for now, it chooses the move in which the
      // opponent has the least owned spaced
      int currentValue = evaluateBoard(boardClone, player);

      // only update best move if the current move is strictly better
      // in a tie, the first move in the legal moves list is chosen
      if (currentValue > bestValue) {
        bestValue = currentValue;
        bestMove = move;
      }
    }

    return bestMove;
  }

}