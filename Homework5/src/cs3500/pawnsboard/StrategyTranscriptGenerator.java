//package cs3500.pawnsboard;
//
//import cs3500.pawnsboard.model.MockPawnsBoardModel;
//import cs3500.pawnsboard.model.Card;
//import cs3500.pawnsboard.strategy.FillFirstStrategy;
//import cs3500.pawnsboard.strategy.MaximizeRowScoreStrategy;
//import cs3500.pawnsboard.strategy.PawnsBoardStrategy;
//import cs3500.pawnsboard.model.Move;
//
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.List;
//import java.util.ArrayList;
//
///**
// * StrategyTranscriptGenerator is a utility class that sets up a mock PawnsBoard game configuration,
// * applies different strategies to choose moves, and generates a transcript of the moves. The
// * transcript entries are then written to a file.
// *
// * <p>This class uses a mock model configured with a test deck and board, applies strategies such as
// * {@link FillFirstStrategy} and {@link MaximizeRowScoreStrategy}, and records the transcript of the
// * moves chosen by these strategies. It is designed to help with testing and debugging strategy
// * behavior.
// * </p>
// */
//public class StrategyTranscriptGenerator {
//
//  /**
//   * Helper method to create a test card.
//   */
//  private static Card createTestCard(String name, int cost, int value) {
//    char[][] grid = new char[5][5];
//    for (int i = 0; i < 5; i++) {
//      for (int j = 0; j < 5; j++) {
//        grid[i][j] = 'X';
//      }
//    }
//    grid[2][2] = 'C';
//    grid[1][2] = 'I';
//    grid[2][1] = 'I';
//    grid[2][3] = 'I';
//    grid[3][2] = 'I';
//    return new Card(name, cost, value, grid);
//  }
//
//  /**
//   * Writes the transcript entries to a file.
//   */
//  private static void writeTranscriptToFile(List<String> transcript, String filename) {
//    try (FileWriter writer = new FileWriter(filename)) {
//      for (String entry : transcript) {
//        writer.write(entry + "\n");
//      }
//      System.out.println("Transcript written to " + filename);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//  }
//
//  /**
//   * Sets up the starting board configuration and model.
//   */
//  private static MockPawnsBoardModel setupMockModel() {
//    int rows = 3;
//    int cols = 5;
//    List<Card> testDeck = new ArrayList<>();
//    testDeck.add(createTestCard("Card1", 1, 5));
//    testDeck.add(createTestCard("Card2", 1, 5));
//    // Create the mock model with 3 rows, 5 columns, and starting hand size 1.
//    MockPawnsBoardModel model = new MockPawnsBoardModel(rows, cols, testDeck, 1);
//    model.enableTranscriptRecording();
//    return model;
//  }
//
//  /**
//   * Main method to generate strategy transcripts for different strategies.
//   *
//   * <p>This method sets up a mock board model and applies two different strategies to choose a
//   * move for the "red" player. It then appends the chosen moves to the transcript and writes the
//   * transcript to a file named "strategy-transcript-score.txt".</p>
//   *
//   * @param args command-line arguments (not used)
//   */
//  public static void main(String[] args) {
//    MockPawnsBoardModel model = setupMockModel();
//    PawnsBoardStrategy fillFirst = new FillFirstStrategy();
//    Move moveFirst = fillFirst.chooseMove(model, "red");
//    List<String> transcript = model.getTranscript();
//    transcript.add("Given legal moves above, strategy chooses move: " + moveFirst.toString());
//    writeTranscriptToFile(transcript, "strategy-transcript-score.txt");
//
//    MockPawnsBoardModel model2 = setupMockModel();
//    PawnsBoardStrategy maxRow = new MaximizeRowScoreStrategy();
//    Move moveScore = maxRow.chooseMove(model2, "red");
//    List<String> transcript2 = model2.getTranscript();
//    transcript2.add("Given legal moves above, strategy chooses move: " + moveScore.toString());
//    writeTranscriptToFile(transcript2, "strategy-transcript-score.txt");
//
//  }
//}