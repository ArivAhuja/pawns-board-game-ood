package cs3500.pawnsboard.view;

import cs3500.pawnsboard.model.Card;
import cs3500.pawnsboard.provider.model.CustomCard;
import cs3500.pawnsboard.provider.model.Player;

/**
 * Adapts our internal Card (cs3500.pawnsboard.model.Card)
 * to a provider CustomCard (cs3500.pawnsworld.model.CustomCard).
 */
public class CardAdapter extends CustomCard {

  /**
   * Constructs a CardAdapter.
   *
   * @param card       the internal Card to adapt.
   * @param playertype the provider’s Player (for instance, cs3500.pawnsworld.model.Player.BLUE)
   */
  public CardAdapter(Card card, Player playertype) {
    // Delegate to the provider’s CustomCard constructor
    super(card.getName(), card.getCost(), card.getValue(), card.getInfluenceGrid(), playertype);
  }

  // No additional methods are needed because the provider’s CustomCard already implements
  // influenceToString() as specified.
}