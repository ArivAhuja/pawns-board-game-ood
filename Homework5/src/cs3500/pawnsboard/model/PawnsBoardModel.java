package cs3500.pawnsboard.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the main model for the Pawns game. This model represents the board, both players,
 * and the deck. It provides methods for the logic of the game, such as whether the game is over,
 * who's turn it is, or what the score is, for example.
 */
public class PawnsBoardModel implements PawnsBoardModelI {

  private final Board board;
  private final Player redPlayer;
  private final Player bluePlayer;
  private boolean isRedTurn;
  private int consecutivePasses;
  private List<Card> deck;


  /**
   * Constructs a new PawnsBoardModel.
   * @param rows Number of rows on the board.
   * @param columns Number of columns on the board.
   * @param deck The list of cards (from the deck configuration file) to use for both players.
   * @param handSize Starting hand size for each player.
   */
  public PawnsBoardModel(int rows, int columns, List<Card> deck, int handSize) {
    if (rows <= 0 || columns <= 1 || columns % 2 == 0) {
      throw new IllegalArgumentException("Invalid board dimensions: rows must be > 0, and " +
              "columns must be > 1 and odd.");
    }
    if (handSize > (deck.size()/3)) {
      throw new IllegalArgumentException
              ("Hand size cannot be greater than a third of the deck size.");
    }
    if ((rows * columns) > deck.size()) {
      throw new IllegalArgumentException
              ("Must have enough cards in deck to fill board.");
    }
    // initialize the board as a 2D array of Cells
    this.board = new Board(rows, columns);
    this.deck = deck;
    List<Card> redHand = new ArrayList<Card>();
    for (int i = 0; i < handSize; i++) {
      redHand.add(deck.remove(0)); // removes the first card each time
    }
    List<Card> blueHand = new ArrayList<Card>();
    for (int i = 0; i < handSize; i++) {
      blueHand.add(deck.remove(0)); // now the deck has shifted, so this gets the next hand
    }
    this.redPlayer = new Player("Red", redHand, handSize);
    this.bluePlayer = new Player("Blue", blueHand, handSize);

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

  // =================== Observation Methods (from ReadonlyPawnsBoardModelI) ===================

  /**
   * Returns the board.
   * @return the current Board.
   */
  public Board getBoard() {
    return board;
  }

  /**
   * Creates a deep copy of the board using the copy constructor.
   * @return a new Board instance.
   */
  public Board cloneBoard() {
    return new Board(this.board); // Uses Board's copy constructor
  }

  /**
   * Returns true if the game is over (i.e. both players have passed consecutively).
   * @return true if game over, false otherwise.
   */
  public boolean isGameOver() {
    return consecutivePasses >= 2;
  }


  /**
   * Returns the current player.
   * @return the Player whose turn it is.
   */
  public Player getCurrentPlayer() {
    return isRedTurn ? redPlayer : bluePlayer;
  }

  /**
   * Enumerates all legal moves for the current player.
   * @return a list of legal moves available.
   */
  public List<Move> getLegalMoves() {
    List<Move> moves = new ArrayList<>();
    Player current = getCurrentPlayer();
    for (int row = 0; row < board.getRows(); row++) {
      for (int col = 0; col < board.getColumns(); col++) {
        Cell cell = board.getCell(row, col);
        // Only consider cells that contain pawns owned by the current player and do not already
        // hold a card.
        if (!cell.hasPawns() || !cell.getOwner().equals(current.getColor()) ||
                cell.getCard() != null) {
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
   * Computes the overall scores for both players.
   * @return an int array of length 2 where index 0 is Red's score and index 1 is Blue's score.
   */
  public int[] computeScores() {
    int redTotal = 0;
    int blueTotal = 0;
    int[][] rowScores = computeRowScores();
    for (int[] rowScore : rowScores) {
      if (rowScore[0] > rowScore[1]) {
        redTotal += rowScore[0];
      }
      else if (rowScore[1] > rowScore[0]) {
        blueTotal += rowScore[1];
      }
    }
    return new int[] { redTotal, blueTotal };
  }

  /**
   * Computes the row-by-row scores.
   * For each row, sums the value scores of cards owned by Red and Blue separately.
   * @return a 2D int array where for each row i,
   *         result[i][0] is Red's row score and result[i][1] is Blue's row score.
   */
  public int[][] computeRowScores() {
    int rows = board.getRows();
    int cols = board.getColumns();
    int[][] rowScores = new int[rows][2];
    for (int row = 0; row < rows; row++) {
      int redScore = 0;
      int blueScore = 0;
      for (int col = 0; col < cols; col++) {
        Cell cell = board.getCell(row, col);
        if (cell.getCard() != null) {
          if (cell.getOwner().equals("Red")) {
            redScore += cell.getCard().getValue();
          }
          else if (cell.getOwner().equals("Blue")) {
            blueScore += cell.getCard().getValue();
          }
        }
      }
      rowScores[row][0] = redScore;
      rowScores[row][1] = blueScore;
    }
    return rowScores;
  }

  /**
   * Determines the winner based on the computed scores.
   * @return "Red wins!", "Blue wins!", or "It's a tie!".
   */
  public String getWinner() {
    int[] scores = computeScores();
    if (scores[0] > scores[1]) {
      return "Red wins!";
    }
    else if (scores[1] > scores[0]) {
      return "Blue wins!";
    }
    else {
      return "It's a tie!";
    }
  }

  // ======================== Mutator Methods (from PawnsBoardModelI) =========================


  public void drawCard() {
    Card card = deck.remove(0);
    this.getCurrentPlayer().drawCard(card);
  }

  /**
   * Checks if the current player's hand is empty.
   * If so, automatically passes.
   * @return true if an auto-pass occurred, false otherwise.
   */
  public boolean checkAutoPass() {
    if (getCurrentPlayer().getHand().isEmpty()) {
      System.out.println(getCurrentPlayer().getColor() + " has no cards left." +
              " Auto-passing...");
      pass();
      return true;
    }
    if (getLegalMoves().isEmpty()) {
      System.out.println(getCurrentPlayer().getColor() + " has no legal moves available." +
              " Auto-passing...");
      pass();
      return true;
    }
    return false;
  }

  /**
   * Processes a pass move by the current player.
   */
  public void pass() {
    System.out.println(getCurrentPlayer().getColor() + " passes.");
    consecutivePasses++;
    isRedTurn = !isRedTurn;
  }

  /**
   * Attempts to place a card from the current player's hand on the specified cell.
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
    // Check that the cell has pawns, is owned by the current player, and does not already
    // have a card.
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
   *   - If the cell has pawns owned by the current player, the pawn count increases by one (upto3).
   *   - If the cell has pawns owned by the opponent, the cell's owner is switched to the current
   *   player's color.
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
}
