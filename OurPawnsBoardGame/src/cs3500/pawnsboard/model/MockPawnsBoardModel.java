package cs3500.pawnsboard.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A mock version of PawnsBoardModel that records a transcript of method calls,
 * including detailed information about which board positions were inspected and
 * what cards were considered.
 */
public class MockPawnsBoardModel extends PawnsBoardModel {
  private final List<String> transcript;
  private boolean recordTranscript;

  /**
   * Constructs a MockPawnsBoardModel using the same parameters as the real model.
   *
   * @param rows the number of rows on the board.
   * @param columns the number of columns on the board.
   * @param deck the deck of cards.
   * @param handSize the starting hand size.
   */
  public MockPawnsBoardModel(int rows, int columns, List<Card> deck, int handSize) {
    super(rows, columns, deck.size(), handSize);
    this.transcript = new ArrayList<>();
    this.recordTranscript = false;
    List<Card> deck1 = new ArrayList<>(deck);
  }

  /**
   * Enables transcript recording.
   */
  public void enableTranscriptRecording() {
    recordTranscript = true;
  }

  /**
   * Returns the recorded transcript.
   *
   * @return a List of strings representing the detailed method calls.
   */
  public List<String> getTranscript() {
    return transcript;
  }

  /**
   * Gets the current player.
   *
   * @return the current player
   */
  public Player getCurrentPlayer() {
    String color = getCurrentPlayerColor();
    Player p = new Player(color, new ArrayList<>());
    if (recordTranscript) {
      StringBuilder sb = new StringBuilder();
      sb.append("getCurrentPlayer called: " + color + ", hand=[");
      for (int i = 0; i < p.getHand().size(); i++) {
        Card card = p.getHand().get(i);
        sb.append(card.getName() + "(cost=" + card.getCost() + ")");
        if (i < p.getHand().size() - 1) {
          sb.append(", ");
        }
      }
      sb.append("]");
      transcript.add(sb.toString());
    }
    return p;
  }

  /**
   * Gets the list of legal moves for the current player.
   *
   * @return a list of legal moves
   */
  public List<Move> getLegalMoves() {
    if (recordTranscript) {
      transcript.add("getLegalMoves called");
    }
    List<Move> moves = new ArrayList<>();
    Player current = getCurrentPlayer();
    Board board = super.getBoard().cloneBoard();
    for (int row = 0; row < board.getRows(); row++) {
      for (int col = 0; col < board.getColumns(); col++) {
        Cell cell = board.getCell(row, col);
        if (recordTranscript) {
          transcript.add("Inspecting cell (" + row + ", " + col + "): owner="
                  + cell.getOwner() + ", pawnCount=" + cell.getPawnCount()
                  + ", cardPresent=" + (cell.getCard() != null));
        }
        // Only consider cells that have pawns, are owned by the current player, and do not hold a
        // card.
        if (!cell.hasPawns() || !cell.getOwner().equals(current.getColor()) ||
                cell.getCard() != null) {
          continue;
        }
        // For each card in the player's hand, check if it can be legally played here.
        for (int cardIndex = 0; cardIndex < current.getHand().size(); cardIndex++) {
          Card card = current.getHand().get(cardIndex);
          if (recordTranscript) {
            transcript.add("Considering card " + card.getName() + " (cost=" + card.getCost()
                    + ") at hand index " + cardIndex + " for cell (" + row + ", " + col
                    + ") with pawnCount=" + cell.getPawnCount());
          }
          if (card.getCost() <= cell.getPawnCount()) {
            Move move = new Move(row, col, cardIndex);
            moves.add(move);
            if (recordTranscript) {
              transcript.add("Legal move added: Place card " + card.getName()
                      + " (index " + cardIndex + ") at (" + row + ", " + col + ")");
            }
          }
        }
      }
    }
    return moves;
  }

  @Override
  public void applyInfluence(int cardRow, int cardCol, Card card, String playerColor) {
    if (recordTranscript) {
      transcript.add("applyInfluence called with cardRow=" + cardRow + ", cardCol=" + cardCol
              + ", card=" + card.getName() + ", playerColor=" + playerColor);
    }
    super.applyInfluence(cardRow, cardCol, card, playerColor);
  }

  @Override
  public int[][] computeRowScores() {
    if (recordTranscript) {
      transcript.add("computeRowScores called");
    }
    return super.computeRowScores();
  }

  @Override
  public Board getBoard() {
    if (recordTranscript) {
      transcript.add("getBoard called");
    }
    return super.getBoard();
  }

  @Override
  public Board cloneBoard() {
    if (recordTranscript) {
      transcript.add("cloneBoard called");
    }
    return super.cloneBoard();
  }

  /**
   * Represents a move in the game.
   */
  public static class Move {
    private final int row;
    private final int col;
    private final int cardIndex;

    /**
     * Creates a new move.
     *
     * @param row the row
     * @param col the column
     * @param cardIndex the index of the card in the player's hand
     */
    public Move(int row, int col, int cardIndex) {
      this.row = row;
      this.col = col;
      this.cardIndex = cardIndex;
    }

    public int getRow() {
      return row;
    }

    public int getCol() {
      return col;
    }

    public int getCardIndex() {
      return cardIndex;
    }
  }

  /**
   * Represents a player in the game.
   */
  public static class Player {
    private final String color;
    private final List<Card> hand;

    /**
     * Creates a new player.
     *
     * @param color the player's color
     * @param hand the player's hand
     */
    public Player(String color, List<Card> hand) {
      this.color = color;
      this.hand = hand;
    }

    public String getColor() {
      return color;
    }

    public List<Card> getHand() {
      return hand;
    }
  }
}