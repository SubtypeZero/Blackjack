package me.subtypezero.games.api;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
	private ArrayList<Card> cards;

	/**
	 * Load and shuffle a standard deck of cards
	 */
	public Deck() {
		cards = loadDeck();
		shuffle();
	}

	/**
	 * Load a standard deck of cards
	 */
	private ArrayList<Card> loadDeck() {
		ArrayList<Card> deck = new ArrayList<Card>();
		addSuit(deck, Suit.CLUBS);
		addSuit(deck, Suit.DIAMONDS);
		addSuit(deck, Suit.HEARTS);
		addSuit(deck, Suit.SPADES);
		return deck;
	}

	/**
	 * Add a suit to a deck of cards
	 * @param deck the deck of cards
	 * @param suit the suit to add
	 */
	private void addSuit(ArrayList<Card> deck, Suit suit) {
		for (int i = 1; i <= 13; i++) {
			deck.add(new Card(suit, i)); // 11-13 represent Jack, King, and Queen
		}
	}

	/**
	 * Add more decks to the deck of cards
	 * Note: Remember to shuffle the deck later
	 * @param amount the number to decks to add
	 */
	public void addDecks(int amount) {
		if (amount < 1) {
			return;
		}
		ArrayList<Card> deck = loadDeck();

		for (int i = 0; i < amount; i++) {
			cards.addAll(deck);
		}
	}

	/**
	 * Take a card from the top of the deck
	 * Note: Remember to put the card back later
	 * @return a card from the deck
	 */
	public Card takeCard() {
		Card card = cards.get(0); // Get the first card
		cards.remove(0); // Remove the card from the deck
		return card;
	}

	/**
	 * Place a card at the bottom of the deck
	 * @param card the card to put
	 */
	public void putCard(Card card) {
		cards.add(card);
	}

	/**
	 * Shuffle the deck of cards
	 */
	public void shuffle() {
		shuffle(cards);
	}

	/**
	 * Shuffle a deck of cards
	 * @param deck the deck to shuffle
	 */
	private void shuffle(ArrayList<Card> deck) {
		Collections.shuffle(deck);
	}
}
