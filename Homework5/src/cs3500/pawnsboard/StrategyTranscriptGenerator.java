package cs3500.pawnsboard;

import cs3500.pawnsboard.model.MockPawnsBoardModel;
import cs3500.pawnsboard.model.Board;
import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.strategy.FillFirstStrategy;
import cs3500.pawnsboard.strategy.MaximizeRowScoreStrategy;
import cs3500.pawnsboard.strategy.PawnsBoardStrategy;
import cs3500.pawnsboard.model.Move;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class StrategyTranscriptGenerator {

  /**
   * Helper method to create a test card.
   */
  private static Card createTestCard(String name, int cost, int value) {
    char[][] grid = new char[5][5];
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        grid[i][j] = 'X';
      }
    }
    grid[2][2] = 'C';
    grid[1][2] = 'I';
    grid[2][1] = 'I';
    grid[2][3] = 'I';
    grid[3][2] = 'I';
    return new Card(name, cost, value, grid);
  }

  /**
   * Writes the transcript entries to a file.
   */
  private static void writeTranscriptToFile(List<String> transcript, String filename) {
    try (FileWriter writer = new FileWriter(filename)) {
      for (String entry : transcript) {
        writer.write(entry + "\n");
      }
      System.out.println("Transcript written to " + filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Sets up the starting board configuration and model.
   */
  private static MockPawnsBoardModel setupMockModel() {
    int rows = 3;
    int cols = 5;
    List<Card> testDeck = new ArrayList<>();
    testDeck.add(createTestCard("Card1", 1, 5));
    testDeck.add(createTestCard("Card2", 1, 5));
    // Create the mock model with 3 rows, 5 columns, and starting hand size 1.
    MockPawnsBoardModel model = new MockPawnsBoardModel(rows, cols, testDeck, 1);
    model.enableTranscriptRecording();
    return model;
  }

  public static void main(String[] args) {
    MockPawnsBoardModel model = setupMockModel();
    PawnsBoardStrategy fillFirst = new FillFirstStrategy();
    Move moveFirst = fillFirst.chooseMove(model, "Red");
    List<String> transcript = model.getTranscript();
    transcript.add("Given legal moves above, strategy chooses move: " + moveFirst.toString());
    writeTranscriptToFile(transcript, "strategy-transcript-score.txt");

    MockPawnsBoardModel model2 = setupMockModel();
    PawnsBoardStrategy maxRow = new MaximizeRowScoreStrategy();
    Move moveScore = maxRow.chooseMove(model2, "Red");
    List<String> transcript2 = model2.getTranscript();
    transcript2.add("Given legal moves above, strategy chooses move: " + moveScore.toString());
    writeTranscriptToFile(transcript2, "strategy-transcript-score.txt");

  }
}