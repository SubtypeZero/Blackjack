package me.subtypezero.games.api.card;

import java.util.ArrayList;

public class Hand {
	private ArrayList<Card> cards;

	/**
	 * Create a hand of cards
	 */
	public Hand() {
		cards = new ArrayList<>();
	}

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
}
