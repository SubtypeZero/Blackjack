package me.subtypezero.games.server;

import com.google.gson.Gson;
import me.subtypezero.games.api.card.Card;
import me.subtypezero.games.api.net.type.Action;
import me.subtypezero.games.server.entity.Dealer;
import me.subtypezero.games.server.entity.Player;
import me.subtypezero.games.api.net.Message;
import me.subtypezero.games.api.net.Messenger;
import me.subtypezero.games.api.net.Type;
import me.subtypezero.games.api.net.type.Update;
import me.subtypezero.games.api.net.type.Value;

import java.net.Socket;
import java.util.ArrayList;

public class Game implements Runnable {
	private Gson gson;

	private ArrayList<Player> players;
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

		players = new ArrayList<>();
		dealer = new Dealer(decks);

		this.INIT_BAL = initBal;
		this.MIN_BET = minBet;
		this.MAX_BET = maxBet;
	}

	public void run() {
		while (running) {
			ArrayList<Player> players = (ArrayList<Player>) this.players.clone(); // Get players

			dealCards(players); // Deal cards
			takeTurns(players); // Take turns
			showResults(players); // Calculate results
			resetCards(players); // Reset cards
		}

		// Game closed, disconnect all players
		for (Player player : players) {
			Messenger.sendMessage(player.getSocket(), new Message(Type.LEAVE, "CLOSED"));
		}
	}

	private void dealCards(ArrayList<Player> players) {
		Update update = new Update();

		// Deal cards
		for (Player player : players) {
			dealer.dealCards(player, 2);
			update.addValue(new Value("HAND", player.getId(), player.getHand()));
		}
		dealer.takeCards(2);
		update.addValue(new Value("HAND", "DEALER", dealer.getHand()));

		// Send hand data to clients
		sendData(players, Type.VALUE, update);
	}

	private void takeTurns(ArrayList<Player> players) {
		Update update = new Update();

		// Take turns
		for (Player player : players) {
			player.takeTurn(dealer);
			update.addValue(new Value("HAND", player.getId(), player.getHand()));
		}
		dealer.takeTurn(); // Dealer goes last
		update.addValue(new Value("HAND", "DEALER", dealer.getHand()));

		// Send hand data to clients
		sendData(players, Type.VALUE, update);
	}

	private void showResults(ArrayList<Player> players) {
		Update update = new Update();

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
			actions.addAction(Type.DEAL);
			actions.addAction(Type.CLEAR);

			Messenger.sendMessage(player.getSocket(), new Message(Type.ACTION, gson.toJson(actions)));

			// Get response from client
			Message reply = Messenger.getResponse(player.getSocket());

			switch (reply.getType()) {
				case Type.DEAL:
				case Type.CLEAR:
					player.setBet(Integer.parseInt(reply.getData())); // Get the player's next bet
					break;
			}

			// Tell other clients
			update.addValue(new Value("BET", player.getId(), bet));
		}

		// Send bet data to clients
		sendData(players, Type.VALUE, update);
	}

	private void resetCards(ArrayList<Player> players) {
		ArrayList<Card> cards = new ArrayList<>();

		for (Player player : players) {
			cards.addAll(player.getHand().clearCards()); // Take cards from all players
		}
		cards.addAll(dealer.getHand().clearCards()); // Take cards from the dealer

		dealer.putCards(cards); // Put cards back in the deck
	}

	/**
	 * Send data to clients
	 * @param players list of players
	 * @param type type of message
	 * @param data data to send
	 */
	private void sendData(ArrayList<Player> players, int type, Object data) {
		for (Player player : players) {
			Messenger.sendMessage(player.getSocket(), new Message(type, gson.toJson(data)));
		}
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
			players.add(player);
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
