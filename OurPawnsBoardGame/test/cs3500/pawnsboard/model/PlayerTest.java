package cs3500.pawnsboard.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test class for the Player class to ensure it correctly initializes
 * with a color and hand of cards, and properly manages the player's hand.
 */
public class PlayerTest {

  private MockPawnsBoardModel mockModel;
  private List<Card> testDeck;

  /**
   * Helper method to create a test card with given parameters.
   */
  private Card createTestCard(String name, int cost, int value) {
    char[][] grid = new char[5][5];
    // Initialize with X's
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        grid[i][j] = 'X';
      }
    }
    // Set center to 'C'
    grid[2][2] = 'C';
    return new Card(name, cost, value, grid);
  }

  /**
   * Helper method to create a deck of test cards.
   */
  private List<Card> createTestDeck(int size) {
    List<Card> deck = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      deck.add(createTestCard("Card" + i, 1, i + 1));
    }
    return deck;
  }

  @Before
  public void setUp() {
    testDeck = createTestDeck(30); // Create a large test deck
    mockModel = new MockPawnsBoardModel(5, 5, testDeck, 5);
  }

  @Test
  public void testConstructor_BasicInitialization() {
    List<Card> deckCopy = new ArrayList<>(testDeck);
    Player player = new Player("red", deckCopy, mockModel);

    assertEquals("red", player.getColor());
    assertNotNull(player.getHand());
    assertEquals(5, player.getHand().size()); // Should match model's handSize
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_HandSizeTooLarge() {
    // Create a model with a hand size that's too large (> deck.size()/3)
    MockPawnsBoardModel largeHandModel = new MockPawnsBoardModel(5, 5, testDeck, 15);
    List<Card> deckCopy = new ArrayList<>(testDeck);

    // This should throw an exception since handSize > deck.size()/3
    Player player = new Player("red", deckCopy, largeHandModel);
  }

  @Test
  public void testConstructor_EmptyDeck() {
    List<Card> emptyDeck = new ArrayList<>();
    MockPawnsBoardModel emptyDeckModel = new MockPawnsBoardModel(5, 5, testDeck, 0);

    Player player = new Player("red", emptyDeck, emptyDeckModel);
    assertTrue(player.getHand().isEmpty());
  }

  @Test
  public void testGetColor() {
    List<Card> deckCopy = new ArrayList<>(testDeck);
    Player redPlayer = new Player("red", deckCopy, mockModel);
    Player bluePlayer = new Player("blue", deckCopy, mockModel);

    assertEquals("red", redPlayer.getColor());
    assertEquals("blue", bluePlayer.getColor());
  }

  @Test
  public void testGetHand() {
    List<Card> deckCopy = new ArrayList<>(testDeck);
    List<Card> originalDeckCopy = new ArrayList<>(deckCopy);
    Player player = new Player("red", deckCopy, mockModel);

    List<Card> hand = player.getHand();
    assertNotNull(hand);
    assertEquals(5, hand.size());

    // Verify that the first 5 cards from the deck were used
    for (int i = 0; i < 5; i++) {
      assertEquals(originalDeckCopy.get(i).getName(), hand.get(i).getName());
    }

    // Verify the deck was modified (cards were removed)
    assertEquals(originalDeckCopy.size() - 5, deckCopy.size());
  }

  @Test
  public void testRemoveCardFromHand() {
    List<Card> deckCopy = new ArrayList<>(testDeck);
    Player player = new Player("red", deckCopy, mockModel);

    Card cardToRemove = player.getHand().get(2);
    player.removeCardFromHand(cardToRemove);

    // Hand should now have 4 cards
    assertEquals(4, player.getHand().size());

    // The removed card should no longer be in the hand
    assertFalse(player.getHand().contains(cardToRemove));
  }

  @Test
  public void testRemoveCardFromHand_CardNotInHand() {
    List<Card> deckCopy = new ArrayList<>(testDeck);
    Player player = new Player("red", deckCopy, mockModel);

    // Create a card that's not in the player's hand
    Card notInHand = createTestCard("NotInHand", 1, 10);

    // Initial hand size
    int initialSize = player.getHand().size();

    // Try to remove a card that's not in the hand
    player.removeCardFromHand(notInHand);

    // Hand size should not change
    assertEquals(initialSize, player.getHand().size());
  }

  @Test
  public void testDrawCard() {
    List<Card> deckCopy = new ArrayList<>(testDeck);
    Player player = new Player("red", deckCopy, mockModel);
    int initialHandSize = player.getHand().size();
    int initialDeckSize = deckCopy.size();

    // Draw a card
    player.drawCard();

    // Hand should have one more card
    assertEquals(initialHandSize + 1, player.getHand().size());

    // Deck should have one less card
    assertEquals(initialDeckSize - 1, deckCopy.size());
  }

  @Test
  public void testDrawCard_EmptyDeck() {
    List<Card> emptyDeck = new ArrayList<>();
    MockPawnsBoardModel emptyDeckModel = new MockPawnsBoardModel(5, 5, testDeck, 0);

    Player player = new Player("red", emptyDeck, emptyDeckModel);
    int initialHandSize = player.getHand().size();

    // Try to draw from empty deck
    player.drawCard();

    // Hand size should not change
    assertEquals(initialHandSize, player.getHand().size());
  }

  /**
   * A mock model class specifically for testing the Player class.
   */
  private class MockPawnsBoardModel extends PawnsBoardModel {
    private final int handSize;

    public MockPawnsBoardModel(int rows, int columns, List<Card> deck, int handSize) {
      super(rows, columns, deck.size(), handSize);
      this.handSize = handSize;
    }

    @Override
    public int getHandSize() {
      return handSize;
    }
  }
}