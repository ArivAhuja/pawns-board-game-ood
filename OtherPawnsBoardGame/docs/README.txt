Overview:
This codebase attempts to create a two player (or 1 person and 1 AI) game of Pawns World. The
codebase is split into 3 main sections to handle the logic, visualization, and controllability
of objects and fields, At this stage, only the model and a basic text visualization has been
created. This codebase assumes contributors are aware of how to play Pawns World, the specific
format of the config file, the MVC design, and implementation of interfaces.

Quick start:
To start a game of Pawns World, simply open the MainGame.java file (located in:
src/cs3500/pawnsworld/MainGame), run the ConfigReader class, initialize the model and view, and
run the startGame method in the model. At this stage, the user must write their own print statement
to view any changes to the model. Here is an example:

ConfigReader configtest = new ConfigReader();
configtest.read();
PawnsGame model = new PawnsGame(3, 5);
model.startGame(configtest.getRedDeck(), configtest.getBlueDeck(), 5);
PawnsTextualView view = new PawnsTextualView(model);

System.out.println(view.toString());
model.placeCard(0, 0, 0);
System.out.println(view.toString());
model.pass();
System.out.println(view.toString());

Key Components:
The methods that drive the program are as follows (omitting parameters):
model:
    placeCard();
    removeCard();
    pass();

view:
    toString();

These are the MAIN methods that a player might use most frequently, there are more methods
that are useful for determining the state of the game (ex: isGameOver(), getWinner()), however
these methods have a tangible impact on the board and the hands of the player(s).

Driven methods include:
    isGameOver();
    getScoreRed();
    getScoreBlue();
    getElement();
    getHeight()
    getWidth();
    getTotalScore();

These methods might be called from other methods and may operate independently. It is
important to examine where they are called from and understand their purpose. They are usually
used in the toString method located in view or for setting the bounds of a nested for loop.

Key subcomponents:
    pawnCount();
    getPlayerType();
    updatePlayerType();
    getCost();
    hasCard();

These subcomponents are responsible for extracting information of the elements located on the
main game board. These properties are necessary for arithmetic done in the PawnsGame class. They
are also used to differentiate between pawns and cards as they both are element that can be placed
on the board.

Source Organization
So far, the organization of this codebase is basic. Under src/cs3500/pawnsworld, there are two
main directories, the model and view. The deck.config file is located in the root directory docs.


Players:
This design allows both human and AI players to seamlessly interact with the model.
The model does not need to know whether a player is human or AI, it only implements
the logic and instruction of a certain method. It’s only WHEN the method is called is what
differs between humans and AI. This design allows to keep the PawnsWorld logic separate from
how the moves are decided. PlayerController encapsulates information from PawnsWorld whether
it’s human or AI. It is also an example of composition



Part 2 Changes:
In hW6, two methods: getElement() and setElement() were added to the model. This is for retrieving
BoardElements on the PawnsBoard as well as being able to set certain Elements are specific
positions. The latter move is especially important to erase the changes done by the strategies
when picking out a co-ordinate to play on.

Strategies Implementation:
All Strategy objects imlpement the PawnStrategy interface which contains one method: chooseMove.
As of now, 2 Strategies were implemented in their respective classes: FillFirst and Maximize-
RowScore. The FillFirst implementation looks for the first square that can be filled with ANY
card that is available on the current players hand. This means that the strategy cycles through
every card in the player's hand before moving on to the next cell. The MaximumRowScore
strategy works the same way, except it looks for a cell which results in the current player having
a higher score than the opponent for a specific row. When a strategy can't or does not need to be
implemented, it will throw an IllegalStateException.

View Implementation:




Extra credit has NOT been attempted.


Part 3 Changes:

Controller Redesign:
In Part 3, the PawnsBoardControllerImpl was redesigned to support both human and AI players within
the same controller structure. The controller now accepts a Player and the shared game model in
all constructors. For human players, it also takes in a PawnsWorldGUIView, which connects the
controller to the user interface. For AI players, it accepts a PawnsStrategy instead, with no
view involved.

This separation allows the game to support any combination of human and machine players.
The controller listens to model and view events and handles move confirmation, passing, and state
updates accordingly. Each controller only interacts with the view associated with its corresponding
player.

AI Player Integration:
To support AI behavior, a second constructor was added to PawnsBoardControllerImpl which accepts a
PawnsStrategy. When it is the AI player's turn, the controller calls chooseMove() on the strategy
object. Strategies were updated to return a StrategyMove object containing the row, column, and
hand index for the intended move.

If the strategy cannot find a valid move, it returns null, and the controller responds by passing
the turn. This logic is fully separated from the GUI, allowing machine players to act
programmatically without requiring any user input.

View and Turn Management:
Each player’s controller only displays their own hand and can only interact with the board when it
is their turn. This design ensures that the correct deck is shown to the appropriate player and
prevents any visibility or interference with the opponent's hand. The controller enables or
disables card and cell selection dynamically based on turn notifications from the model.

Game Over Handling:
When the game ends—either by filling the board or both players passing consecutively—the model
notifies both controllers. The gameOver() method is then triggered, which disables all player input
and displays a message dialog showing the game result. The message indicates whether it was a win
or a draw, and includes the winning player's score if applicable. The board and hand views are
also frozen to prevent any further interaction once the game has concluded.

Using Command Line Arguments to Setup Decks and Players:
This program accepts exactly 4 command line arguments, nothing more nothing less. It can be used
to specify the red player's config deck, the blue player's config deck, the type of red player
and the type of blue player. To specify the deck config, simply input the file path as a single
string, every subsequent argument must be separated by a space. To specify a human player, simply
say "human" in the argument. For a computer player, state whether you would want to use
"strategy1" or "strategy2". An example command line argument would look like this:

docs/deck.config docs/deck2.config human strategy2

The entire file path must be given if you are using your own deck files located somewhere else.

If the command line argument is given incorrectly, the program will use the default arguments,
with red being the human player and blue being the strategy 2 player.

IN ORDER FOR THE JAR FILE TO RUN, IT MUST BE LOCATED IN THE SAME FOLDER AS THE DECK CONFIG FILES.

JAR FILE LOCATED UNDER <root>/docs/

SCREENSHOTS OF THE VIEW ARE LOCATED UNDER <root>/ViewScreenShots/


