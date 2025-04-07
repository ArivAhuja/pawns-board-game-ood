package cs3500.pawnsboard.strategy;

import java.util.List;

import cs3500.pawnsboard.model.Move;
import cs3500.pawnsboard.model.PawnsBoardModel;
import cs3500.pawnsboard.model.Player;

/**
 * A strategy that always chooses the first legal move and places it in the first available space.
 */
public class FillFirstStrategy extends AbstractPawnsBoardStrategy {
  @Override
  public Move chooseMove(PawnsBoardModel model, Player player) {
    List<Move> legalMoves = player.getLegalMoves();
    if (legalMoves.isEmpty()) {
      return null; // indicates a pass
    }
    // Assuming legalMoves are already in the order of board traversal.
    return legalMoves.get(0);
  }
}
