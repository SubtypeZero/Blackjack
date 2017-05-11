package me.subtypezero.games.api;

import me.subtypezero.games.api.card.Deck;
import me.subtypezero.games.api.event.Result;

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
		// reveal hidden card
		// draw to 16 and stand on 17
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
			player.addCard(deck.takeCard());
		}
	}

	/**
	 * Get the result of the current hand
	 * @param player the player to compare with
	 * @return the player's result
	 */
	public Result getResult(Player player) {
		int dealerValue = getHighest(this.getHandValues());
		int playerValue = getHighest(player.getHandValues());

		boolean dealerBust = isBust(dealerValue);
		boolean playerBust = isBust(playerValue);

		// Check for bust
		if (playerBust) {
			return Result.LOSE;
		} else if (dealerBust) {
			return Result.WIN;
		}

		// Check for push
		if (dealerValue == playerValue) {
			return Result.PUSH;
		}

		// Check hand value
		if (playerValue == 21) {
			return Result.BLACKJACK;
		} else if (playerValue > dealerValue) {
			return Result.WIN;
		} else {
			return Result.LOSE;
		}
	}

	/**
	 * Get the highest (non-bust) value from a list of values
	 * @param values the list of values
	 * @return the highest value or -1
	 */
	private int getHighest(ArrayList<Integer> values) {
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
	 * Check if a value represents a bust
	 * @param value the value to check
	 * @return true if the value is negative
	 */
	private boolean isBust(int value) {
		return value < 0;
	}
}
