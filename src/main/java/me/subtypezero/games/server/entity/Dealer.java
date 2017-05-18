package me.subtypezero.games.server.entity;

import me.subtypezero.games.api.card.Card;
import me.subtypezero.games.api.card.Deck;
import me.subtypezero.games.api.entity.Gambler;
import me.subtypezero.games.api.net.update.Type;

import java.util.ArrayList;
import java.util.Collections;

public class Dealer extends Gambler {
	private Deck deck;

	/**
	 * Create a new dealer
	 * @param decks the number of decks to use
	 */
	public Dealer(int decks) {
		deck = new Deck();

		if (decks > 1) {
			deck.addDecks(decks - 1);
		}
	}

	public void takeTurn() {
		boolean done = false;

		while (!done) {
			int highest = getHighest(getHandValues());
			int lowest = getLowest(getHandValues());

			if (highest < 0 && lowest > 21) {
				return; // bust
			}

			if (highest <= 16 || lowest <= 16) {
				// draw a card
				getHand().addCard(deck.takeCard());
			} else {
				done = true;
			}
		}
	}

	/**
	 * Deal cards to a player
	 * @param player the player
	 * @param amount the number of cards
	 */
	public void dealCards(Player player, int amount) {
		if (amount < 1) {
			return;
		}

		for (int i = 0; i < amount; i++) {
			player.getHand().addCard(deck.takeCard());
		}
	}

	/**
	 * Deal a card to a player
	 * @param player the player
	 * @return the card dealt
	 */
	public Card dealCard(Player player) {
		dealCards(player, 1);
		ArrayList<Card> cards = player.getHand().getCards();
		return cards.get(cards.size() - 1);
	}

	/**
	 * Make the dealer take cards from the deck
	 * @param amount the number of cards to take
	 */
	public void takeCards(int amount) {
		for (int i = 0; i < amount; i++) {
			getHand().addCard(deck.takeCard());
		}
	}

	/**
	 * Put cards back in the deck
	 * @param cards cards to put back
	 */
	public void putCards(ArrayList<Card> cards) {
		for (Card card : cards) {
			deck.putCard(card);
		}
	}

	/**
	 * Get the result of the current hand
	 * @param player the player to compare with
	 * @return the player's result
	 */
	public int getResult(Player player) {
		int dealerValue = getHighest(this.getHandValues());
		int playerValue = getHighest(player.getHandValues());

		boolean dealerBust = isBust(dealerValue);
		boolean playerBust = isBust(playerValue);

		// Check for bust
		if (playerBust) {
			return Type.LOSE;
		} else if (dealerBust) {
			return Type.WIN;
		}

		// Check for push
		if (dealerValue == playerValue) {
			return Type.PUSH;
		}

		// Check hand value
		if (playerValue == 21) {
			return Type.BLACKJACK;
		} else if (playerValue > dealerValue) {
			return Type.WIN;
		} else {
			return Type.LOSE;
		}
	}

	/**
	 * Get the highest (non-bust) value from a list of values
	 * @param values the list of values
	 * @return the highest value or -1
	 */
	public int getHighest(ArrayList<Integer> values) {
		Collections.sort(values); // sort values in ascending order

		// remove values that are too high
		for (int i = 0; i < values.size(); i++) {
			if (values.get(i) > 21) {
				values.remove(i);
			}
		}

		if (values.isEmpty()) {
			return -1;
		}

		int highest = -1; // find highest (non-bust) value

		for (int i = 0; i < values.size(); i++) {
			int value = values.get(i);

			if (value > highest) {
				highest = value;
			}
		}

		return highest;
	}

	/**
	 * Get the lowest value from a list of values
	 * @param values the list of values
	 * @return the lowest value
	 */
	public int getLowest(ArrayList<Integer> values) {
		int lowest = Integer.MAX_VALUE;

		for (int value : values) {
			if (value < lowest) {
				lowest = value;
			}
		}
		return lowest;
	}

	/**
	 * Check if a value represents a bust
	 * @param value the value to check
	 * @return true if the value is negative
	 */
	private boolean isBust(int value) {
		return value < 0;
	}
}
