package me.subtypezero.games.api.cards;

public class Card {
	private Suit suit;
	private int value;

	/**
	 * Create a new playing cards
	 * Note: 11-13
	 * @param suit the suit
	 * @param value the value of the cards
	 */
	public Card(Suit suit, int value) {
		this.suit = suit;
		this.value = value;
	}

	/**
	 * Get the suit that the cards belongs to
	 * @return the suit
	 */
	public Suit getSuit() {
		return suit;
	}

	/**
	 * Get the actual value of the cards
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Get the face value of the cards
	 * @return the face value
	 */
	public int getFaceValue() {
		if (value > 10) {
			return 10;
		}
		return value;
	}
}
