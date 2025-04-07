package cs3500.pawnsworld.model;

/**
 * Interface to house all Strategy implementations. Has one method that all strategy objects
 * must implement. Main goal is to choose co-ordinates based on the strategy implementation.
 */
public interface PawnsStrategy {

  /**
   * Chooses a StrategyMove to decide card and position to
   * place on board, depending on the implementation of the strategy.
   * @param model the game model.
   * @param player the current player that needs to make a turn.
   * @return a StrategyMove.
   */
  StrategyMove chooseMove(PawnsWorldReadOnly model, Player player);
}
