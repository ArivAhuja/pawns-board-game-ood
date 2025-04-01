package cs3500.pawnsboard.model;

/**
 * Represents a human player.
 * Although human players can publish player-action events via the view,
 * they do not compute moves automatically.
 */
public class HumanPlayer implements PlayerActions {
  private final String color;

  public HumanPlayer(String color) {
    this.color = color;
  }

  @Override
  public String getColor() {
    return color;
  }
}