package me.subtypezero.games;

import com.google.gson.Gson;
import me.subtypezero.games.api.net.Action;
import me.subtypezero.games.api.Dealer;
import me.subtypezero.games.api.Player;
import me.subtypezero.games.api.net.Message;
import me.subtypezero.games.api.net.Messenger;
import me.subtypezero.games.api.net.Type;

import java.net.Socket;
import java.util.LinkedList;

public class Game implements Runnable {
	private Gson gson;

	private LinkedList<Player> players;
	private Dealer dealer;
	private boolean running = true;

	private final int INIT_BAL;
	private final int MIN_BET;
	private final int MAX_BET;

	private final int MAX_PLAYERS = 3;

	/**
	 * Create a new game instance
	 * @param initBal the initial player balance
	 * @param minBet the minimum bet amount
	 * @param maxBet the maximum bet amount
	 * @param decks the number of decks to use
	 */
	public Game(int initBal, int minBet, int maxBet, int decks) {
		gson = new Gson();

		players = new LinkedList<>();
		dealer = new Dealer(decks);

		this.INIT_BAL = initBal;
		this.MIN_BET = minBet;
		this.MAX_BET = maxBet;
	}

	public void run() {
		while (running) {
			// Get players
			LinkedList<Player> players = (LinkedList<Player>) this.players.clone();

			// Deal cards
			for (Player player : players) {
				dealer.dealCards(player, 2);
			}
			dealer.takeCards(2);

			// Send hand data to clients
			for (Player player : players) {
				for (Player other : players) {
					// Convert the other player's hand to JSON and send it to the client
					Messenger.sendMessage(player.getSocket(), new Message(Type.HAND, gson.toJson(other.getHand())));
				}
				// Convert the dealer's hand to JSON and send it to the client
				Messenger.sendMessage(player.getSocket(), new Message(Type.HAND, gson.toJson(dealer.getHand())));
			}

			// Players take turns
			for (Player player : players) {
				player.takeTurn(dealer);
				// TODO Send data to clients
			}

			// Dealer takes turn (card reveal can be handled by the client)
			dealer.takeTurn();

			// Calculate results
			for (Player player : players) {
				int result = dealer.getResult(player);
				int bet = player.getBet();
				player.setBet(0);

				switch (result) {
					case Type.WIN:
						// return bet and give reward
						player.giveMoney(bet * 2);
						break;
					case Type.LOSE:
						// lose bet
						break;
					case Type.PUSH:
						// return bet
						player.giveMoney(bet);
						break;
					case Type.BLACKJACK:
						// return bet and give bonus reward
						player.giveMoney((int)(bet * 2.5));
						break;
				}

				// Update client
				String data = player.getBalance() + "," + player.getBet();
				Messenger.sendMessage(player.getSocket(), new Message(result, data));

				// Send actions to client
				Action actions = new Action();
				actions.addAction(Type.CLEAR);
				actions.addAction(Type.REPEAT);

				Messenger.sendMessage(player.getSocket(), new Message(Type.ACTION, gson.toJson(actions)));

				// Get response from client
				Message reply = Messenger.getResponse(player.getSocket());

				switch (reply.getType()) {
					case Type.CLEAR:
					case Type.REPEAT:
						player.setBet(Integer.parseInt(reply.getData())); // Get the player's next bet
						break;
				}
			}
		}
		// End of game
	}

	/**
	 * Stop the game
	 */
	public void stop() {
		running = false;
	}

	/**
	 * Connect a new player to the game
	 * @param clientSock the client's socket
	 * @return true if the connection was successful
	 */
	public boolean connect(Socket clientSock) {
		if (isOpen()) {
			Player player = new Player(clientSock, INIT_BAL);

			// Tell the client they have been connected
			Messenger.sendMessage(clientSock, new Message(Type.JOIN, "SUCCESS"));

			// Ask the client to place a bet
			Action actions = new Action();
			actions.addAction(Type.BET);
			Messenger.sendMessage(player.getSocket(), new Message(Type.ACTION, gson.toJson(actions)));

			// Get response from client
			Message reply = Messenger.getResponse(player.getSocket());

			switch (reply.getType()) {
				case Type.BET:
					player.setBet(Integer.parseInt(reply.getData())); // Get the player's next bet
					break;
			}

			// First player sits in middle, second sits on left, third sits on right
			if (getPlayerCount() < MAX_PLAYERS - 1) {
				players.addLast(player);
			} else {
				players.addFirst(player);
			}
			return true;
		}
		return false;
	}

	/**
	 * Get the number of players in the game
	 * @return the player count
	 */
	public int getPlayerCount() {
		return players.size();
	}

	/**
	 * Check if the game is open for players to join
	 * @return the result
	 */
	public boolean isOpen() {
		return getPlayerCount() < MAX_PLAYERS;
	}
}
