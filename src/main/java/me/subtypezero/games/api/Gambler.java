package me.subtypezero.games.api;

import me.subtypezero.games.api.card.Card;

import java.util.ArrayList;

public abstract class Gambler {
	private ArrayList<Card> cards = new ArrayList<>();

	abstract void takeTurn();

	/**
	 * Get a list of the gambler's cards
	 * @return the cards in the hand
	 */
	public ArrayList<Card> getCards() {
		return (ArrayList<Card>) cards.clone();
	}

	/**
	 * Add a card to the gambler's hand
	 * @param card the card to add
	 */
	public void addCard(Card card) {
		cards.add(card);
	}

	/**
	 * Clear the gambler's hand
	 * @return the cards from the hand
	 */
	public ArrayList<Card> clearCards() {
		ArrayList<Card> cards = getCards();
		this.cards.clear();
		return cards;
	}

	/**
	 * Get a list of possible values of the gambler's hand
	 * @return the hand values
	 */
	public ArrayList<Integer> getHandValues() {
		ArrayList<Integer> values = new ArrayList<>();

		int aces = 0;
		int min = 0;
		int max = 0;

		for (Card card : cards) {
			if (card.getValue() == 1) {
				aces++;
			}
			min += card.getFaceValue(); // add the value of the card (aces count as 1)
		}

		max = min; // max is at least as big as min

		for (int i = 0; i < aces; i++) {
			if (max + 10 <= 21) {
				max += 10; // use as many aces as possible
			}
		}

		values.add(min);

		if (max > min) {
			values.add(max); // add the max value if necessary
		}

		return values;
	}
}
