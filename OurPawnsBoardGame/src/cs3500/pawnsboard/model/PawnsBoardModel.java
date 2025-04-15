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
  private boolean isRedTurn;
  private int consecutivePasses;
  private final List<ModelStatusListener> statusListeners;
  private final int handSize;



  /**
   * Constructs a new PawnsBoardModel.
   * @param rows Number of rows on the board.
   * @param columns Number of columns on the board.
   * @param deckSize Size of the list of cards to use for both players.
   */
  public PawnsBoardModel(int rows, int columns, int deckSize, int handSize) {
    if (rows <= 0 || columns <= 1 || columns % 2 == 0) {
      throw new IllegalArgumentException("Invalid board dimensions: rows must be > 0, and " +
              "columns must be > 1 and odd.");
    }
    if ((rows * columns) > deckSize) {
      throw new IllegalArgumentException("Must have enough cards in deck to fill board.");
    }
    this.handSize = handSize;
    // initialize the board as a 2D array of Cells
    this.board = new Board(rows, columns);
    // set initial board configuration:
    // first column cells get 1 red pawn, last column cells get 1 blue pawn.
    for (int row = 0; row < rows; row++) {
      board.setCellPawns(row, 0, 1, "red");
      board.setCellPawns(row, columns - 1, 1, "blue");
    }
    // red always starts
    this.isRedTurn = true;
    this.consecutivePasses = 0;

    // set the status listerners (features) initialized
    this.statusListeners = new ArrayList<>();
  }

  // ================== NEW Listener Methods (need to add to interface) ===================
  /**
   * Adds a listener for model status updates.
   * @param listener the listener to add.
   */
  public void addModelStatusListener(ModelStatusListener listener) {
    if (listener == null) {
      throw new IllegalArgumentException("Listener cannot be null");
    }
    this.statusListeners.add(listener);
  }

  /**
   * Notifies all registered listeners that the turn has changed.
   */
  private void notifyTurnChanged() {
    for (ModelStatusListener listener : statusListeners) {
      listener.turnChanged();
    }
  }

  /**
   * Notifies all registered listeners that the game is over.
   */
  private void notifyGameOver() {
    String result = this.getWinner();
    for (ModelStatusListener listener : statusListeners) {
      listener.gameOver(result);
    }
  }


  // =================== Observation Methods (from ReadonlyPawnsBoardModelI) ===================


  /**
   * Returns the width of the board.
   * @return the width of the board.
   */
  public int getWidth() {
    return this.board.getColumns();
  }

  /**
   * Returns the height of the board.
   * @return the height of the board.
   */
  public int getHeight() {
    return this.board.getRows();
  }

  /**
   * Returns the cell at the specified row and column.
   * @param row the row of the cell.
   * @param col the column of the cell.
   * @return the Cell object at the specified position.
   */
  public Cell getCell(int row, int col) {
    if (row < 0 || row >= board.getRows() || col < 0 || col >= board.getColumns()) {
      throw new IllegalArgumentException("Invalid cell position");
    }
    return this.board.getCell(row, col);
  }

  /**
   * Returns the owner of the cell at the specified row and column.
   * @param row the row of the cell.
   * @param col the column of the cell.
   * @return "red" if red owns the cell, "blue" if blue owns it, or null if it's empty.
   */
  public String getCellOwner(int row, int col) {
    if (row < 0 || row >= board.getRows() || col < 0 || col >= board.getColumns()) {
      throw new IllegalArgumentException("Invalid cell position");
    }
    Cell cell = this.board.getCell(row, col);
    return cell.getOwner();
  }

  /**
   * Returns the color of the current player.
   * @return "red" if it's red player's turn, otherwise "blue"
   */
  public String getCurrentPlayerColor() {
    return isRedTurn ? "red" : "blue";
  }

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
   * Returns the hand size.
   * @return the hand size for the game
   */
  public int getHandSize() {
    return handSize;
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
          if (cell.getOwner().equals("red")) {
            redScore += cell.getCard().getValue();
          }
          else if (cell.getOwner().equals("blue")) {
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

  @Override
  public int getCellInfluenceValue(Cell cell) {
    return 0;
  }

  // ======================== Mutator Methods (from PawnsBoardModelI) =========================

  /**
   * Processes a pass move.
   */
  public void pass() {
    consecutivePasses++;
    switchTurn();
  }

  public void setConsecutivePasses(int consecutivePasses) {
    this.consecutivePasses = consecutivePasses;
  }

  /**
   * Switches the turn to the other player.
   * If the game is over, notifies listeners of the game over status.
   * If not, notifies listeners of the turn change.
   */
  public void switchTurn() {
    // after updating the game state, notify listeners:
    isRedTurn = !isRedTurn;
    if (isGameOver()) {
      notifyGameOver();
    }
    else {
      notifyTurnChanged();
    }
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
   * @param playerColor The color of the current player ("red" or "blue").
   */
  public void applyInfluence(int cardRow, int cardCol, Card card, String playerColor) {
    char[][] grid = card.getInfluenceGrid();
    // Iterate over the 5x5 grid.
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        // Skip cells that are not marked with influence.
        if (grid[i][j] != 'I') {
          continue;
        }
        int effectiveJ = j;
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
