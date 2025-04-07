package cs3500.pawnsboard.model;

/**
 * Represents a human player.
 * Although human players can publish player-action events via the view,
 * they do not compute moves automatically.
 */
public class HumanPlayer implements PlayerActions {
  private final Player player;

  public HumanPlayer(Player player) {
    this.player = player;
  }

  @Override
  public String getColor() {
    return player.getColor();
  }

  @Override
  public String humanOrMachine() {
    return "human";
  }

}