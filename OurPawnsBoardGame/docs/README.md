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

### Deck File Format:

Cards are defined in a configuration file with:

* Header line: name cost value
* Five lines representing the influence grid using:
  * C: Center (card placement)
  * I: Influence area
  * X: No influence

Example:

Simple 1 2

XXXXX

XXXXX

XXCXX

XXXXX

XXXXX

### Source Orginization:

- **src/**

  - **cs3500.pawnsboard/**: Contains the main application class

    - **model**: Game data and logic components
    - **view**: Display components
    - **controller**: Input handling and game flow
- **test/**

  - **cs3500.pawnsboard/**: Contains the test suite for the project
    - **model**: Tests for the model components
    - **view**: Tests for the view components
    - **controller**: Tests for the controller component
- **docs/**

  - **deck.config**: Contains the deck for the game
  - **README.md**: Gives overview of the project

**Sample Class Invariant:** The board is a rectangle with an odd number of columns.

## Changes from HW5 -> HW6

- Creates the ReadOnlyPawnsBoardModel interface, changed the PawnsBoardModel to implement this interface
- Made it so red and blue have some same and some different cards and added non-symetrical cards
- Made autopass be handled internally by the model instead of the controller
- Added drawing abilities
- Some methods in the deeper-classes, like Player's getHand, into the model explicitly. Also added
  isLegalMove,

### Strategies

All four strategies from the Homework are implemented in the `/scr/strategy` directory, and also explained below:

- **FillFirstStrategy**: Chooses the first card and location that can be played on, and plays there. *[tested]*
- **MaximizeRowScoreStrategy**: Chooses the move that would allow the current player to win a given row.
  if there are no moves, it will pass. *[tested]*
- **ControlBoardStrategy**: Chooses a move that will give the player ownership of the most cells. *[not yet tested]*
- **MiniMaxStrategy**: Chooses the move in which minimizes the opponents possible moves. For now, it minimizes
  the opponent's number of owned cells. *[not yet tested]*
- **ChainedStrategy**: You can combine different strategies here, and the resulting move will (for now) is the one
  that provides the most cell ownership. *[not yet tested]*

> *Transcripts for the first two strategies as per the HW specs can be found: ![FillFirstTranscript](assets/strategy-transcript-first.txt) and
> ![MaxRowTranscript](assets/strategy-transcript-score.txt).* ***It is important to note that these transcripts are based off the description in the homework
> "choosing a move for the Red player on the starting board configuration of the 3 row by 5 column board using the cards you made". Thus, it is an empty 3x5 board
> and the strategy chooses a initial move for red player.***

### Controller Instructions

- Click a card to select it
- Click a cell to select it
- Click a different card or cell to change selection
- Press "p" to pass turn
- Press "c" to place card

## Changes from HW6 -> HW7

- Move placeCard logic to the Player class
- Set up Main to allow for CLI args
- Set up dual controller controller which allows for two players to play against each other
- Added AI (strategy) move functionality to controller
- Added the needed observation methods to the ReadOnlyModel (getWidth, getHeight, getCell, getOwner)

## PawnsBoard Game CLI Instructions

### Basic Command

Given the JAR included in the files, you can use the following command from the Homework5/ directory to run the game.

```
java -jar PawnsBoard.jar <redDeckPath> <blueDeckPath> <redPlayerType> <bluePlayerType>
```

Additionally, there is an optional argument at the end of the above command 'provider' which
uses the provider's view for Player 2. As seen below:

```
java -jar PawnsBoard.jar <redDeckPath> <blueDeckPath> <redPlayerType> <bluePlayerType> provider
```

### Arguments

* `redDeckPath`: Path to Red's deck file (from where you are running the command)
* `blueDeckPath`: Path to Blue's deck file
* `redPlayerType`: Type of Red player
* `bluePlayerType`: Type of Blue player
* `provider`: Optional argument to use the provider's view for Player 2

### Player Types

* `human`: Human player
* `controlboard`: AI with Control Board strategy
* `fillfirst`: AI with Fill First strategy
* `maximizerowscore`: AI with Row Score strategy
* `minimax`: AI with MiniMax strategy

### Examples

```
java -jar PawnsBoard.jar docs/red.config docs/blue.config human minimax
java -jar PawnsBoard.jar docs/red.config docs/blue.config controlboard fillfirst
```

Game uses 5 rows, 5 columns, and 5 cards per hand.

## HW8 Notes

We were able to implement all features of the provider's view. Note that the dialouge boxes are part of the controller so those look different. Note we also don't have a dialouge box for game over like the provider did for thier controller as we orignally had a game over screen drawn by the view, but there is still a system output.

We didn't delete tests, but the provider's /docs/ folder so that we can upload the ZIP. We have their
docs downloaded locally so we can refer to that. We also lowered the amount of screenshots of
our view from around 15 to the 4 required to save file space.


## HW9 Notes

- The new influence value are small numbers below the cell score.
- In the hand, the magenta in the influence grid is downgrade and green is upgrade.
- You can use the decks blueVariantDeck.config and redVariantDeck.config in docs/ to use decks with the new influence capabilities.
- If the deck given has varient influence cards, the game will use the new influence system automatically.
