package cs3500.pawnsboard.model;

import cs3500.pawnsboard.strategy.PawnsBoardStrategy;

/**
 * Represents a machine player that computes its move using a strategy.
 */
public class MachinePlayer implements PlayerActions {
  private final String color;
  private final PawnsBoardStrategy strategy;

  public MachinePlayer(String color, PawnsBoardStrategy strategy) {
    this.color = color;
    this.strategy = strategy;
  }

  @Override
  public String getColor() {
    return color;
  }

  @Override
  public Move getNextMove(ReadonlyPawnsBoardModelI model) {
    // Delegate move computation to the strategy.
    return strategy.chooseMove((PawnsBoardModel) model, color);
  }
}