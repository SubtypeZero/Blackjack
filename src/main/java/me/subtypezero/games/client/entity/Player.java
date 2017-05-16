package me.subtypezero.games.client.entity;

import me.subtypezero.games.api.entity.Gambler;

public class Player extends Gambler {
	private final String uuid;
	private int balance;
	private int bet;

	/**
	 * Create a new player
	 * @param uuid the client id
	 * @param balance the starting balance
	 */
	public Player(String uuid, int balance) {
		this.uuid = uuid;
		this.balance = balance;
	}

	/**
	 * Get the player's UUID
	 * @return the id
	 */
	public String getId() {
		return uuid;
	}

	/**
	 * Get the player's balance
	 * @return the balance
	 */
	public int getBalance() {
		return balance;
	}

	/**
	 * Set the player's balance
	 * @param amount the amount to set
	 */
	public void setBalance(int amount) {
		this.balance = amount;
	}

	/**
	 * Get the player's bet
	 * @return the bet
	 */
	public int getBet() {
		return bet;
	}

	/**
	 * Set the player's bet
	 * @param amount the amount to set
	 */
	public void setBet(int amount) {
		this.bet = amount;
	}
}
