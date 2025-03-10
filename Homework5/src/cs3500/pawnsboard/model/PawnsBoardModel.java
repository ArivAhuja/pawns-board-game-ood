package cs3500.pawnsboard.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the main model for the Pawns game.
 *
 */
public class PawnsBoardModel {

  private Board board;
  private Player redPlayer;
  private Player bluePlayer;
  private boolean isRedTurn;
  private int consecutivePasses;


  /**
   * Constructs a new PawnsBoardModel.
   *
   * @param rows Number of rows on the board.
   * @param columns Number of columns on the board.
   * @param deck The list of cards (from the deck configuration file) to use for both players.
   * @param handSize Starting hand size for each player.
   */
  public PawnsBoardModel(int rows, int columns, List<Card> deck, int handSize) {
    if (rows <= 0 || columns <= 1 || columns % 2 == 0) {
      throw new IllegalArgumentException("Invalid board dimensions: rows must be > 0, and columns must be > 1 and odd.");
    }

    // initialize the board as a 2D array of Cells
    this.board = new Board(rows, columns);

    // initialize players using the same deck
    this.redPlayer = new Player("Red", deck, handSize);
    this.bluePlayer = new Player("Blue", deck, handSize);

    // set initial board configuration:
    // first column cells get 1 red pawn, last column cells get 1 blue pawn.
    for (int row = 0; row < rows; row++) {
      board.setCellPawns(row, 0, 1, "Red");
      board.setCellPawns(row, columns - 1, 1, "Blue");
    }

    // red always starts
    this.isRedTurn = true;
    this.consecutivePasses = 0;
  }

  /**
   * Returns the board.
   * @return The board.
   */
  public Board getBoard() {
    return board;
  }

  public boolean isGameOver() {
    return consecutivePasses >= 2;
  }

  /**
   * Returns the current player.
   */
  public Player getCurrentPlayer() {
    return isRedTurn ? redPlayer : bluePlayer;
  }

  /**
   * Processes a pass move by the current player.
   * Increments consecutive passes and switches turn.
   */
  public void pass() {
    System.out.println(getCurrentPlayer().getColor() + " passes.");
    consecutivePasses++;
    isRedTurn = !isRedTurn;
  }

  /**
   * Attempts to place a card from the current player's hand on the specified cell.
   *
   * @param row       The row of the target cell.
   * @param col       The column of the target cell.
   * @param cardIndex The index of the card in the player's hand.
   * @return true if the move was successful, false otherwise.
   */
  public boolean placeCard(int row, int col, int cardIndex) {
    Player current = getCurrentPlayer();

    if (!board.isValidPosition(row, col)) {
      System.out.println("Invalid cell position.");
      return false;
    }

    Cell cell = board.getCell(row, col);
    // Check that the cell has pawns, is owned by the current player, and does not already have a card.
    if (!cell.hasPawns() || !cell.getOwner().equals(current.getColor()) || cell.getCard() != null) {
      System.out.println("Cell is not eligible for placement.");
      return false;
    }
    // Check card index validity.
    if (cardIndex < 0 || cardIndex >= current.getHand().size()) {
      System.out.println("Invalid card index.");
      return false;
    }

    Card chosenCard = current.getHand().get(cardIndex);
    if (chosenCard.getCost() > cell.getPawnCount()) {
      System.out.println("Not enough pawns to cover the card's cost.");
      return false;
    }

    // Place the card.
    cell.placeCard(chosenCard, current.getColor());
    current.removeCardFromHand(chosenCard);
    System.out.println(current.getColor() + " plays " + chosenCard.getName() +
            " at (" + row + "," + col + ").");
    consecutivePasses = 0;  // Reset passes on a successful move.
    // Apply the card's influence.
    applyInfluence(row, col, chosenCard, current.getColor());
    isRedTurn = !isRedTurn; // Switch turn.
    return true;
  }

  /**
   * Applies the influence of a placed card to the board.
   * The card's 5x5 influence grid is overlaid on the board, centered at (cardRow, cardCol).
   * For each cell in the grid marked 'I':
   *   - If the cell is empty, a single pawn of the current player's color is added.
   *   - If the cell has pawns owned by the current player, the pawn count increases by one (up to 3).
   *   - If the cell has pawns owned by the opponent, the cell's owner is switched to the current player's color.
   * The influence grid is mirrored horizontally if the current player is Blue.
   *
   * @param cardRow    The row where the card was placed.
   * @param cardCol    The column where the card was placed.
   * @param card       The card that was placed.
   * @param playerColor The color of the current player ("Red" or "Blue").
   */
  private void applyInfluence(int cardRow, int cardCol, Card card, String playerColor) {
    char[][] grid = card.getInfluenceGrid();
    // Iterate over the 5x5 grid.
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        // Skip cells that are not marked with influence.
        if (grid[i][j] != 'I') {
          continue;
        }
        // For Blue, mirror the column.
        int effectiveJ = playerColor.equals("Blue") ? 4 - j : j;
        // Compute offset from the center (2,2).
        int dr = i - 2;
        int dc = effectiveJ - 2;
        // Skip the center cell (the card's own cell).
        if (dr == 0 && dc == 0) {
          continue;
        }
        int targetRow = cardRow + dr;
        int targetCol = cardCol + dc;
        if (!board.isValidPosition(targetRow, targetCol)) {
          continue;
        }
        Cell targetCell = board.getCell(targetRow, targetCol);
        // If the cell has a card, no influence is applied.
        if (targetCell.getCard() != null) {
          continue;
        }
        // If the cell is empty, add one pawn.
        if (!targetCell.hasPawns()) {
          targetCell.setPawnCount(1);
          targetCell.setOwner(playerColor);
        } else {
          // If the cell has pawns:
          if (targetCell.getOwner().equals(playerColor)) {
            // Increase pawn count by one (up to 3).
            int newCount = Math.min(targetCell.getPawnCount() + 1, 3);
            targetCell.setPawnCount(newCount);
          } else {
            // Otherwise, switch ownership to the current player.
            targetCell.setOwner(playerColor);
          }
        }
      }
    }
  }

  /**
   * Enumerates all legal moves for the current player.
   * A legal move is defined as placing any card from the player's hand onto any cell
   * that contains the player's pawns, has no card, and where the pawn count is at least
   * the cost of the card.
   *
   * @return A list of legal moves available to the current player.
   */
  public List<Move> getLegalMoves() {
    List<Move> moves = new ArrayList<>();
    Player current = getCurrentPlayer();
    for (int row = 0; row < board.getRows(); row++) {
      for (int col = 0; col < board.getColumns(); col++) {
        Cell cell = board.getCell(row, col);
        // Only consider cells that contain pawns owned by the current player and do not already hold a card.
        if (!cell.hasPawns() || !cell.getOwner().equals(current.getColor()) || cell.getCard() != null) {
          continue;
        }
        // For each card in the player's hand, check if it can be legally played here.
        for (int cardIndex = 0; cardIndex < current.getHand().size(); cardIndex++) {
          Card card = current.getHand().get(cardIndex);
          if (card.getCost() <= cell.getPawnCount()) {
            moves.add(new Move(row, col, cardIndex));
          }
        }
      }
    }
    return moves;
  }

  /**
   * Computes the scores for both players.
   * For each row:
   *   - Sum the value scores of the cards owned by Red and Blue.
   *   - If one player's sum is higher, add that sum to their total score.
   *   - If the sums are equal, neither player gets points for that row.
   *
   * @return An int array of length 2 where index 0 is Red's score and index 1 is Blue's score.
   */
  public int[] computeScores() {
    int redTotal = 0;
    int blueTotal = 0;
    for (int row = 0; row < board.getRows(); row++) {
      int rowRed = 0;
      int rowBlue = 0;
      for (int col = 0; col < board.getColumns(); col++) {
        Cell cell = board.getCell(row, col);
        if (cell.getCard() != null) {
          if (cell.getOwner().equals("Red")) {
            rowRed += cell.getCard().getValue();
          } else if (cell.getOwner().equals("Blue")) {
            rowBlue += cell.getCard().getValue();
          }
        }
      }
      if (rowRed > rowBlue) {
        redTotal += rowRed;
      } else if (rowBlue > rowRed) {
        blueTotal += rowBlue;
      }
    }
    return new int[]{redTotal, blueTotal};
  }

  /**
   * Determines the winner based on the computed scores.
   *
   * @return "Red wins!", "Blue wins!", or "It's a tie!".
   */
  public String getWinner() {
    int[] scores = computeScores();
    if (scores[0] > scores[1]) {
      return "Red wins!";
    } else if (scores[1] > scores[0]) {
      return "Blue wins!";
    } else {
      return "It's a tie!";
    }
  }

  /**
   * A move represents a possible placement:
   * placing the card at index cardIndex from the current player's hand
   * onto cell (row, col).
   */
  public static class Move {
    public final int row;
    public final int col;
    public final int cardIndex;

    public Move(int row, int col, int cardIndex) {
      this.row = row;
      this.col = col;
      this.cardIndex = cardIndex;
    }

    @Override
    public String toString() {
      return "Place card index " + cardIndex + " at (" + row + ", " + col + ")";
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Move move = (Move) o;
      return row == move.row && col == move.col && cardIndex == move.cardIndex;
    }

    @Override
    public int hashCode() {
      return Objects.hash(row, col, cardIndex);
    }
  }

}
