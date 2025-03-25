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
   * @param deck the red deck of cards.
   * @param handSize the starting hand size.
   */
  public MockPawnsBoardModel(int rows, int columns, List<Card> deck, int handSize) {
    super(rows, columns, deck, handSize);
    this.transcript = new ArrayList<>();
    this.recordTranscript = false;
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

  @Override
  public Player getCurrentPlayer() {
    Player p = super.getCurrentPlayer();
    if (recordTranscript) {
      StringBuilder sb = new StringBuilder();
      sb.append("getCurrentPlayer called: " + p.getColor() + ", hand=[");
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

  @Override
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
        // Only consider cells that have pawns, are owned by the current player, and do not hold a card.
        if (!cell.hasPawns() || !cell.getOwner().equals(current.getColor()) || cell.getCard() != null) {
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
}