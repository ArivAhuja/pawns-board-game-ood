package cs3500.pawnsboard.model;

import cs3500.pawnsboard.strategy.PawnsBoardStrategy;

/**
 * Represents a machine player that computes its move using a strategy.
 */
public class MachinePlayer implements PlayerActions {
  private final Player player;
  private final PawnsBoardStrategy strategy;

  public MachinePlayer(Player player, PawnsBoardStrategy strategy) {
    this.player = player;
    this.strategy = strategy;
  }

  @Override
  public String getColor() {
    return player.getColor();
  }

  @Override
  public Move getNextMove(ReadonlyPawnsBoardModelI model) {
    // Delegate move computation to the strategy.
    return strategy.chooseMove((PawnsBoardModel) model, player);
  }

  @Override
  public String humanOrMachine() {
    return "machine";
  }
}