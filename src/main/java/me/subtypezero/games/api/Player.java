package me.subtypezero.games.api;

import me.subtypezero.games.api.card.Card;

import java.net.Socket;
import java.util.ArrayList;

public class Player {
	private Socket socket;
	private ArrayList<Card> cards;
	private int balance;
	private int bet;

	/**
	 * Create a new player
	 * @param socket the client socket
	 * @param balance the starting balance
	 */
	public Player(Socket socket, int balance) {
		this.socket = socket;
		cards = new ArrayList();
		this.balance = balance;
	}

	/**
	 * Get the player's balance
	 * @return the balance
	 */
	public int getBalance() {
		return balance;
	}

	/**
	 * Give money to the player
	 * @param amount the amount to give
	 */
	public void giveMoney(int amount) {
		this.balance += amount;
	}

	/**
	 * Take money from the player
	 * Note: Taking too much will result in negative values
	 * @param amount the amount to take
	 */
	public void takeMoney(int amount) {
		this.balance -= amount;
	}

	/**
	 * Get the player's bet
	 * @return the bet
	 */
	public int getBet() {
		return bet;
	}

	/**
	 * Add to the player's bet
	 * @param amount the amount to add
	 */
	public void addBet(int amount) {
		this.bet += amount;
	}

	/**
	 * Subtract from the player's bet
	 * Note: Subtracting too much will result in negative values
	 * @param amount the amount to subtract
	 */
	public void subBet(int amount) {
		this.bet -= amount;
	}

	/**
	 * Set the player's bet
	 * @param amount the amount to set
	 */
	public void setBet(int amount) {
		this.bet = amount;
	}

	/**
	 * Get a list of the player's cards
	 * @return the cards in the hand
	 */
	public ArrayList<Card> getCards() {
		return (ArrayList<Card>) cards.clone();
	}

	/**
	 * Add a card to the player's hand
	 * @param card the card to add
	 */
	public void addCard(Card card) {
		cards.add(card);
	}

	/**
	 * Clear the player's hand
	 * @return the cards from the hand
	 */
	public ArrayList<Card> clearCards() {
		ArrayList<Card> cards = getCards();
		this.cards.clear();
		return cards;
	}
}
