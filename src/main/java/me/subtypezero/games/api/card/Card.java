package me.subtypezero.games.api.card;

public class Card {
	private Suit suit;
	private int value;

	/**
	 * Create a new playing card
	 * Note: 11-13 represent Jack, Queen, and King
	 * @param suit the suit
	 * @param value the value of the card
	 */
	public Card(Suit suit, int value) {
		this.suit = suit;
		this.value = value;
	}

	/**
	 * Get the suit that the card belongs to
	 * @return the suit
	 */
	public Suit getSuit() {
		return suit;
	}

	/**
	 * Get the actual value of the card
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Get the face value of the card
	 * @return the face value
	 */
	public int getFaceValue() {
		if (value > 10) {
			return 10;
		}
		return value;
	}
}
