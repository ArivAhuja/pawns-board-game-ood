package cs3500.pawnsboard.provider.model;

/**
 * Enum representing a Player, Red, Blue and None(for Game Draw).
 */
public enum Player {
  RED("R"), BLUE("B"), NONE("_");

  private final String disp;

  Player(String disp) {
    this.disp = disp;
  }

  @Override
  public String toString() {
    return disp;
  }

}
