package cs3500.pawnsboard.controller;

import cs3500.pawnsboard.model.PawnsBoardModel;
import cs3500.pawnsboard.model.Player;
import cs3500.pawnsboard.view.PawnsBoardTextualView;
import cs3500.pawnsboard.model.Move;

import java.util.List;
import java.util.Scanner;

/**
 * Controller for the PawnsBoard game.
 */
public class PawnsBoardController implements PawnsBoardControllerI {
  private final PawnsBoardModel model;
  private final PawnsBoardTextualView view;

  /**
   * Constructs a PawnsBoardController with the given model and view.
   * @param model the model
   * @param view the view
   */
  public PawnsBoardController(PawnsBoardModel model, PawnsBoardTextualView view) {
    this.model = model;
    this.view = view;
  }

  /**
   * Starts the interactive game loop.
   * Each turn, the controller:
   *  - Checks if the current player's hand is empty and auto-passes if so.
   *  - Checks if there are no legal moves available and auto-passes if so.
   *  - Otherwise, prompts the player for input.
   * Valid commands are:
   *   - "pass" (to pass the turn)
   *   - "place cardIndex row col" (to attempt a move)
   */
  public void startGame() {
    Scanner sc = new Scanner(System.in);

    // Render the initial board.
    view.render(model.getBoard());

    while (!model.isGameOver()) {
      // Auto-pass if the current player's hand is empty.
      if (model.autoPassIfHandEmpty()) {
        view.render(model.getBoard());
        continue;
      }

      // Auto-pass if there are no legal moves available.
      List<Move> legalMoves = model.getLegalMoves();
      if (legalMoves.isEmpty()) {
        System.out.println(model.getCurrentPlayer().getColor() + " has no legal moves available. " +
                "Auto-passing.");
        model.pass();
        view.render(model.getBoard());
        continue;
      }

      Player current = model.getCurrentPlayer();
      System.out.println(current.getColor() + "'s turn.");

      // Display the current player's hand.
      System.out.println("Your hand:");
      for (int i = 0; i < current.getHand().size(); i++) {
        System.out.println(i + ": " + current.getHand().get(i));
      }

      System.out.println("Enter your move (type 'pass' or 'place cardIndex row col'):");
      String input = sc.nextLine().trim();

      if (input.equalsIgnoreCase("pass")) {
        model.pass();
      }
      else if (input.toLowerCase().startsWith("place")) {
        String[] tokens = input.split("\\s+");
        if (tokens.length != 4) {
          System.out.println("Invalid input format. Use: place cardIndex row col");
          continue;
        }
        try {
          int cardIndex = Integer.parseInt(tokens[1]);
          int row = Integer.parseInt(tokens[2]);
          int col = Integer.parseInt(tokens[3]);

          // Check if the attempted move is legal.
          Move attemptedMove = new Move(row, col, cardIndex);
          if (!legalMoves.contains(attemptedMove)) {
            System.out.println("Illegal move. Legal moves are:");
            for (Move move : legalMoves) {
              System.out.println(move);
            }
            continue;
          }

          boolean success = model.placeCard(row, col, cardIndex);
          if (!success) {
            System.out.println("Move failed. Try again.");
            continue;
          }
        }
        catch (NumberFormatException e) {
          System.out.println("Invalid number format. Try again.");
          continue;
        }
      }
      else {
        System.out.println("Invalid command. Try again.");
        continue;
      }

      // Render the updated board.
      view.render(model.getBoard());
    }

    System.out.println("Game over.");
    int[] scores = model.computeScores();
    System.out.println("Final Scores:");
    System.out.println("Red: " + scores[0]);
    System.out.println("Blue: " + scores[1]);
    System.out.println(model.getWinner());
    sc.close();
  }
}