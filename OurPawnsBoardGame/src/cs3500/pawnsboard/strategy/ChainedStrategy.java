package cs3500.pawnsboard.strategy;

import java.util.List;
import cs3500.pawnsboard.model.Board;
import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.model.Move;
import cs3500.pawnsboard.model.PawnsBoardModel;
import cs3500.pawnsboard.model.Player;

/**
 * A chained strategy that combines multiple strategies.
 * It calls each sub-strategy to get a candidate move and then uses a common evaluation
 * to choose the best move.
 */
public class ChainedStrategy extends AbstractPawnsBoardStrategy {
  private List<PawnsBoardStrategy> strategies;

  /**
   * Constructs a ChainedStrategy from an ordered list of strategies.
   * @param strategies The list of strategies to combine.
   */
  public ChainedStrategy(List<PawnsBoardStrategy> strategies) {
    this.strategies = strategies;
  }

  @Override
  public Move chooseMove(PawnsBoardModel model, Player player) {
    Move best = null;
    int bestEval = Integer.MIN_VALUE;

    // Iterate over each sub-strategy to obtain candidate moves.
    for (PawnsBoardStrategy strat : strategies) {
      Move candidate = strat.chooseMove(model, player);
      if (candidate != null) {
        int eval = evaluateMove(model, candidate, player);
        if (best == null || eval > bestEval || (eval == bestEval && tieBreaker(candidate, best))) {
          best = candidate;
          bestEval = eval;
        }
      }
    }
    return best;
  }

  /**
   * Simulates a candidate move on a cloned board and returns its evaluation.
   */
  private int evaluateMove(PawnsBoardModel model, Move move, Player player) {
    Board boardClone = model.cloneBoard();
    Card card = player.getHand().get(move.getCardIndex());
    boardClone.getCell(move.getRow(), move.getCol()).placeCard(card, player.getColor());
    simulateInfluenceOnBoard(boardClone, move.getRow(), move.getCol(), card, player.getColor());
    return evaluateBoard(boardClone, player);
  }

  /**
   * Tie-breaker: returns true if the candidate move should replace the current move.
   * The tie-breaker prefers moves that are uppermost (lower row number), then leftmost
   * (lower column number), and then using a lower card index.
   */
  private boolean tieBreaker(Move candidate, Move current) {
    if (candidate.getRow() < current.getRow()) {
      return true;
    }
    if (candidate.getRow() == current.getRow()) {
      if (candidate.getCol() < current.getCol()) {
        return true;
      }
      if (candidate.getCol() == current.getCol() &&
              candidate.getCardIndex() < current.getCardIndex()) {
        return true;
      }
    }
    return false;
  }
}