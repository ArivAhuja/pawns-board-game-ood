package cs3500.pawnsboard.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Player class that tracks a player's color, deck, and hand.
 */
public class Player implements PlayerI {
  private final String color;
  private List<Card> hand;
  private final List<Card> deck;
  private PawnsBoardModel model;

  /**
   * Constructs a new Player.
   * @param color The player's color.
   * @param deck The list of cards to use (deck configuration).
   * @param model The model for the game.
   */
  public Player(String color, List<Card> deck, PawnsBoardModel model) {
    this.model = model;
    int initialHandSize = model.getHandSize();
    if (initialHandSize > (deck.size() / 3)) {
      throw new IllegalArgumentException("Hand size cannot be greater than a third " +
              "of the deck size.");
    }
    this.deck = deck;
    this.color = color;
    this.hand = new ArrayList<>();
    for (int i = 0; i < initialHandSize && !deck.isEmpty(); i++) {
      hand.add(deck.remove(0));
    }
  }

  public String getColor() {
    return color;
  }

  public List<Card> getHand() {
    return hand;
  }

  public void removeCardFromHand(Card card) {
    hand.remove(card);
  }

  public void drawCard() {
    if (!deck.isEmpty()) {
      hand.add(deck.remove(0));
    }
  }

  /**
   * Enumerates all legal moves for the current player.
   * @return a list of legal moves available.
   */
  public List<Move> getLegalMoves() {
    List<Move> moves = new ArrayList<>();
    for (int row = 0; row < model.getBoard().getRows(); row++) {
      for (int col = 0; col < model.getBoard().getColumns(); col++) {
        Cell cell = model.getBoard().getCell(row, col);
        // Only consider cells that contain pawns owned by the current player and do not already
        // hold a card.
        if (!cell.hasPawns() || !cell.getOwner().equals(color) ||
                cell.getCard() != null) {
          continue;
        }
        // For each card in the player's hand, check if it can be legally played here.
        for (int cardIndex = 0; cardIndex < hand.size(); cardIndex++) {
          Card card = hand.get(cardIndex);
          if (card.getCost() <= cell.getPawnCount()) {
            moves.add(new Move(row, col, cardIndex));
          }
        }
      }
    }
    return moves;
  }

  /**
   * Attempts to place a card from the current player's hand on the specified cell.
   * @param row       The row of the target cell.
   * @param col       The column of the target cell.
   * @param cardIndex The index of the card in the player's hand.
   * @return true if the move was successful.
   * @throws IllegalArgumentException if any of the move parameters are invalid.
   * @throws IllegalStateException if the move is not allowed by game rules.
   */
  public boolean placeCard(int row, int col, int cardIndex) {
    if (!model.getBoard().isValidPosition(row, col)) {
      throw new IllegalArgumentException("Invalid cell position.");
    }

    Cell cell = model.getBoard().getCell(row, col);
    // Check that the cell has pawns, is owned by the current player, and does not already
    // have a card.
    if (!cell.hasPawns()) {
      throw new IllegalStateException("Cell has no pawns.");
    }

    if (!cell.getOwner().equals(color)) {
      throw new IllegalStateException("Cell is not owned by the current player.");
    }

    if (cell.getCard() != null) {
      throw new IllegalStateException("Cell already has a card.");
    }
    // Check card index validity.
    if (cardIndex < 0 || cardIndex >= hand.size()) {
      throw new IllegalArgumentException("Invalid card index.");
    }

    Card chosenCard = hand.get(cardIndex);
    if (chosenCard.getCost() > cell.getPawnCount()) {
      throw new IllegalStateException("Not enough pawns to cover the card's cost.");
    }

    // Place the card.
    cell.placeCard(chosenCard, color);
    removeCardFromHand(chosenCard);
    System.out.println(color + " plays " + chosenCard.getName() +
            " at (" + row + "," + col + ").");

    model.applyInfluence(row, col, chosenCard, color);
    model.setConsecutivePasses(0);
    model.switchTurn();

    return true;
  }

  /**
   * Checks if the current player's hand is empty.
   * If so, automatically passes.
   * @return true if an auto-pass occurred, false otherwise.
   */
  public boolean checkAutoPass() {
    if (hand.isEmpty()) {
      System.out.println(color + " has no cards. Auto passsing.");
      model.pass();
      return true;
    }
    if (getLegalMoves().isEmpty()) {
      System.out.println(color + " has no moves. Auto passsing.");
      model.pass();
      return true;
    }
    return false;
  }
}
