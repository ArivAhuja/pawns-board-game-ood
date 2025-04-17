package cs3500.pawnsboard.model;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Test class for the Deck File Parser class.
 */
public class DeckFileParserTest {

  private DeckFileParser parser;
  private String testCardBlock;
  private String testDeckString;

  @Before
  public void setUp() {
    parser = new DeckFileParser();

    // A single valid card block
    testCardBlock = "Knight 2 3\n" +
            "XXXXX\n" +
            "XIIIX\n" +
            "XICIX\n" +
            "XIIIX\n" +
            "XXXXX\n";

    // A complete deck string with 3 cards
    testDeckString = "Knight 2 3\n" +
            "XXXXX\n" +
            "XIIIX\n" +
            "XICIX\n" +
            "XIIIX\n" +
            "XXXXX\n" +
            "\n" +  // Empty line between cards
            "Archer 1 2\n" +
            "XXXXX\n" +
            "XXXXX\n" +
            "XXCXX\n" +
            "XIIIX\n" +
            "XIIIX\n" +
            "\n" +
            "Castle 3 5\n" +
            "IIXII\n" +
            "IIXII\n" +
            "XXCXX\n" +
            "IIXII\n" +
            "IIXII\n";
  }

  @Test
  public void testParsingValidCard() {
    Card card = parser.toCard(testCardBlock);

    assertEquals("Knight", card.getName());
    assertEquals(2, card.getCost());
    assertEquals(3, card.getValue());

    char[][] expectedGrid = {
            {'X', 'X', 'X', 'X', 'X'},
            {'X', 'I', 'I', 'I', 'X'},
            {'X', 'I', 'C', 'I', 'X'},
            {'X', 'I', 'I', 'I', 'X'},
            {'X', 'X', 'X', 'X', 'X'}
    };

    char[][] actualGrid = card.getInfluenceGrid();
    assertArrayEquals(expectedGrid, actualGrid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParsingEmptyCardBlock() {
    parser.toCard("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParsingInvalidHeaderFormat() {
    String invalidHeader = "Knight 2\n" +  // Missing value
            "XXXXX\n" +
            "XIIIX\n" +
            "XICIX\n" +
            "XIIIX\n" +
            "XXXXX\n";
    parser.toCard(invalidHeader);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParsingInvalidGridSize() {
    String invalidGrid = "Knight 2 3\n" +
            "XXXXX\n" +
            "XIIIX\n" +
            "XICIX\n" +  // Only 4 rows instead of 5
            "XIIIX\n";
    parser.toCard(invalidGrid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParsingInvalidRowLength() {
    String invalidRowLength = "Knight 2 3\n" +
            "XXXXX\n" +
            "XIIX\n" +  // Only 4 characters
            "XICIX\n" +
            "XIIIX\n" +
            "XXXXX\n";
    parser.toCard(invalidRowLength);
  }

  @Test
  public void testParsingValidDeck() {
    List<Card> deck = parser.toDeck(testDeckString, false);

    // Check deck size
    assertEquals(3, deck.size());

    // Check first card
    Card card1 = deck.get(0);
    assertEquals("Knight", card1.getName());
    assertEquals(2, card1.getCost());
    assertEquals(3, card1.getValue());

    // Check second card
    Card card2 = deck.get(1);
    assertEquals("Archer", card2.getName());
    assertEquals(1, card2.getCost());
    assertEquals(2, card2.getValue());

    // Check third card
    Card card3 = deck.get(2);
    assertEquals("Castle", card3.getName());
    assertEquals(3, card3.getCost());
    assertEquals(5, card3.getValue());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParsingDeckWithInvalidLineCount() {
    String invalidDeckString = "Knight 2 3\n" +
            "XXXXX\n" +
            "XIIIX\n" +
            "XICIX\n" +
            "XIIIX\n" +
            "XXXXX\n" +
            "\n" +
            "Archer 1 2\n" +  // This card only has 4 grid rows instead of 5
            "XXXXX\n" +
            "XXXXX\n" +
            "XXCXX\n" +
            "XIIIX\n";
    parser.toDeck(invalidDeckString, false);
  }

  @Test
  public void testParsingDeckWithEmptyLines() {
    // Add extra empty lines that should be ignored
    String deckWithEmptyLines = "\n\n" + testDeckString + "\n\n";
    List<Card> deck = parser.toDeck(deckWithEmptyLines, false);

    // Should still parse correctly
    assertEquals(3, deck.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParsingDeckWithInvalidCard() {
    String deckWithInvalidCard = "Knight 2 3\n" +
            "XXXXX\n" +
            "XIIIX\n" +
            "XICIX\n" +
            "XIIIX\n" +
            "XXXXX\n" +
            "\n" +
            "Archer 0 2\n" +  // Invalid cost (0)
            "XXXXX\n" +
            "XXXXX\n" +
            "XXCXX\n" +
            "XIIIX\n" +
            "XIIIX\n";
    parser.toDeck(deckWithInvalidCard, false);
  }

  @Test(expected = NumberFormatException.class)
  public void testParsingCardWithNonNumericValues() {
    String invalidNumbers = "Knight two three\n" +  // Non-numeric cost and value
            "XXXXX\n" +
            "XIIIX\n" +
            "XICIX\n" +
            "XIIIX\n" +
            "XXXXX\n";
    parser.toCard(invalidNumbers);
  }
}