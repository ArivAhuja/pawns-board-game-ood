## Welcome to the Pawns Game

<h6> Northeastern University Object Oriented Programming, Spring 2025
Authors: Ariv Ahuja, Max Mayer </h6>


### Overview

Welcome to the Pawns game! Pawns is a two player game in which players take turns placing their
cards on a board. The goal of the game is to have the most points at the end of the game. Points are
awarded based on each player’s cards on the board. The game ends when the board is full. The player
with the most points wins!

### Quick Start

You can play this game with default settings by running the Main class. Navigate into the src folder
and run the following commands:
```shell
javac PawnsBoardMain.java
java PawnsBoardMain
```

You will be able to interact with the game via your terminal.

### Key Components
Within the `/src` directory, you will find the following key components:
- `PawnsBoardMain.java`: The main class that runs a sample of the game.
- `/controller`: Contains the controller classes that manage the game, working with user input, 
moves, cards, and the board.
- `/model`: Contains the model classes that represent the game’s data, including the board, cards, 
and main logic of the game.
- `/view`: Contains the view classes that display the game to the user.

#### Quick Start [complex]:
You can also start the game, with more customization, by seperately instantiating the controller, 
model, and view classes. *The following code is for demonstration purposes only and is not meant to
be run in the current state. See ./src/PawnsBoardMain.java for a working example.*
```java
// Create the game model.
//    Args: number of rows, number of columns, deck of cards, starting hand size
PawnsBoardModel model = new PawnsBoardModel(3, 5, deck, 5);

// Instantiate the view.
PawnsBoardTextualView view = new PawnsBoardTextualView(model);

// Instantiate the controller.
PawnsBoardController controller = new PawnsBoardController(model, view);

// Start the game.
controller.startGame();
```

**Sample Class Invariant:** The board is a rectangle with an odd number of columns.



