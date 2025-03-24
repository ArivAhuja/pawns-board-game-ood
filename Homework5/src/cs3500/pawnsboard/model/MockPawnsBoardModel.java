package cs3500.pawnsboard.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A mock version of PawnsBoardModel that records a transcript of method calls.
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
   * @return a List of strings representing the method calls.
   */
  public List<String> getTranscript() {
    return transcript;
  }

  @Override
  public List<Move> getLegalMoves() {
    if (recordTranscript) {
      transcript.add("getLegalMoves called");
    }
    return super.getLegalMoves();
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

  @Override
  public Player getCurrentPlayer() {
    if (recordTranscript) {
      transcript.add("getCurrentPlayer called: " + super.getCurrentPlayer().getColor());
    }
    return super.getCurrentPlayer();
  }

}