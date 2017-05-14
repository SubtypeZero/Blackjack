package me.subtypezero.games.api;

import com.google.gson.Gson;
import me.subtypezero.games.api.net.type.Action;
import me.subtypezero.games.api.net.Message;
import me.subtypezero.games.api.net.Messenger;
import me.subtypezero.games.api.net.Type;

import java.net.Socket;
import java.util.UUID;

public class Player extends Gambler {
	private Socket socket;
	private final String uuid;
	private int balance;
	private int bet;

	/**
	 * Create a new player
	 * @param socket the client socket
	 * @param balance the starting balance
	 */
	public Player(Socket socket, int balance) {
		this.socket = socket;
		this.uuid = UUID.randomUUID().toString();
		this.balance = balance;
	}

	public void takeTurn(Dealer dealer) {
		boolean start = true;
		boolean done = false;

		while (!done) {
			int highest = dealer.getHighest(getHandValues());
			int lowest = dealer.getLowest(getHandValues());

			Action actions = new Action();

			if (highest < 0 && lowest > 21) {
				return; // bust
			}

			// hit or stand
			actions.addAction(Type.HIT);
			actions.addAction(Type.STAND);

			if (start) {
				for (int value : getHandValues()) {
					if (9 <= value && value <= 11) {
						// double (only available on opening hand of 9 to 11)
						actions.addAction(Type.DOUBLE);
					}
				}
				start = false;
			}

			// Send actions to client
			Gson gson = new Gson();
			Messenger.sendMessage(socket, new Message(Type.ACTION, gson.toJson(actions)));

			// Get response from client
			Message msg = Messenger.getResponse(socket);

			switch (msg.getType()) {
				case Type.DOUBLE:
					bet = bet * 2; // double the bet
					break;
				case Type.HIT:
					dealer.dealCards(this, 1); // take another card
					break;
				case Type.STAND:
					done = true;
					break;
			}
		}
	}

	/**
	 * Get the client's socket
	 * @return the socket
	 */
	public Socket getSocket() {
		return socket;
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
}
