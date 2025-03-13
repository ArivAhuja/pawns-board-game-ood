package cs3500.pawnsboard.model;

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

  @Test
  public void testConstructor_BasicInitialization() {
    List<Card> deck = createTestDeck(10);
    Player player = new Player("Red", deck, 5);

    assertEquals("Red", player.getColor());
    assertNotNull(player.getHand());
    assertEquals(5, player.getHand().size());
  }

  @Test
  public void testConstructor_HandSizeLargerThanDeck() {
    List<Card> deck = createTestDeck(3);
    Player player = new Player("Blue", deck, 5);

    // Hand should only have 3 cards since the deck only has 3
    assertEquals(3, player.getHand().size());
  }

  @Test
  public void testConstructor_EmptyDeck() {
    List<Card> deck = new ArrayList<>();
    Player player = new Player("Red", deck, 5);

    assertTrue(player.getHand().isEmpty());
  }

  @Test
  public void testConstructor_ZeroHandSize() {
    List<Card> deck = createTestDeck(5);
    Player player = new Player("Blue", deck, 0);

    assertTrue(player.getHand().isEmpty());
  }

  @Test
  public void testConstructor_DeckNotModified() {
    List<Card> originalDeck = createTestDeck(10);
    List<Card> deckCopy = new ArrayList<>(originalDeck);

    Player player = new Player("Red", originalDeck, 5);

    // Original deck should not be modified
    assertEquals(10, originalDeck.size());
    assertEquals(deckCopy, originalDeck);
  }

  @Test
  public void testGetColor() {
    Player redPlayer = new Player("Red", createTestDeck(5), 3);
    Player bluePlayer = new Player("Blue", createTestDeck(5), 3);

    assertEquals("Red", redPlayer.getColor());
    assertEquals("Blue", bluePlayer.getColor());
  }

  @Test
  public void testGetHand() {
    List<Card> deck = createTestDeck(10);
    Player player = new Player("Red", deck, 5);

    List<Card> hand = player.getHand();
    assertNotNull(hand);
    assertEquals(5, hand.size());

    // Verify the first 5 cards from the deck are in the hand
    for (int i = 0; i < 5; i++) {
      assertEquals(deck.get(i).getName(), hand.get(i).getName());
    }
  }

  @Test
  public void testRemoveCardFromHand() {
    List<Card> deck = createTestDeck(5);
    Player player = new Player("Red", deck, 5);

    Card cardToRemove = player.getHand().get(2);
    player.removeCardFromHand(cardToRemove);

    // Hand should now have 4 cards
    assertEquals(4, player.getHand().size());

    // The removed card should no longer be in the hand
    assertFalse(player.getHand().contains(cardToRemove));
  }

  @Test
  public void testRemoveCardFromHand_CardNotInHand() {
    List<Card> deck = createTestDeck(5);
    Player player = new Player("Red", deck, 3);

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
  public void testHandIndependenceFromDeck() {
    List<Card> deck = createTestDeck(10);
    Player player = new Player("Red", deck, 5);

    // Modify the original deck
    deck.remove(0);
    deck.add(createTestCard("NewCard", 2, 20));

    // Player's hand should remain unchanged
    assertEquals(5, player.getHand().size());
    assertEquals("Card0", player.getHand().get(0).getName());
  }

  @Test
  public void testHandMutability() {
    List<Card> deck = createTestDeck(5);
    Player player = new Player("Red", deck, 5);

    List<Card> hand = player.getHand();
    int initialSize = hand.size();

    // Try to modify the hand directly
    hand.remove(0);

    // Check if the player's hand was actually modified
    assertEquals(initialSize - 1, player.getHand().size());
  }
}