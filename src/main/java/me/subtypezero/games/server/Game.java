package me.subtypezero.games.server;

import com.google.gson.Gson;
import me.subtypezero.games.api.card.Card;
import me.subtypezero.games.server.entity.Dealer;
import me.subtypezero.games.server.entity.Player;
import me.subtypezero.games.api.net.Message;
import me.subtypezero.games.api.net.Messenger;
import me.subtypezero.games.api.net.update.Type;
import me.subtypezero.games.api.net.update.Update;
import me.subtypezero.games.api.net.update.Value;

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
			if (players.size() > 0) {
				System.out.println("Starting");
				ArrayList<Player> players = (ArrayList<Player>) this.players.clone(); // Get players
				startRound(players); // Start the round
				dealCards(players); // Deal cards
				takeTurns(players); // Take turns
				showResults(players); // Calculate results
				resetCards(players); // Reset cards
			} else {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		// TODO Close game and disconnect all players
		for (Player player : players) {
			Messenger.sendMessage(player.getSocket(), new Message(Type.LEAVE, "CLOSED"));
		}
	}

	private void startRound(ArrayList<Player> players) {
		System.out.println("Starting round");
		Update update = new Update();

		for (Player player : players) {
			Message reply = Messenger.getResponse(player.getSocket()); // Get bet from client
			System.out.println(reply.getType() + ":" + reply.getData());

			switch (reply.getType()) {
				case Type.BET:
					setBet(player, Integer.parseInt(reply.getData())); // Check and set player's bet
					break;
			}
			update.addValue(new Value("ID", player.getId(), null)); // Add UUID to update
			update.addValue(new Value("BET", player.getId(), player.getBet())); // Add bet to update
			update.addValue(new Value("BALANCE", player.getId(), player.getBalance())); // Add balance to update
		}
		sendUpdate(players, update); // Send player data to clients
	}

	private void dealCards(ArrayList<Player> players) {
		System.out.println("Dealing cards");
		Update update = new Update();

		for (Player player : players) {
			dealer.dealCards(player, 2);
			update.addValue(new Value("HAND", player.getId(), player.getHand().toString()));
		}
		dealer.takeCards(2);
		update.addValue(new Value("HAND", "DEALER", dealer.getHand().toString()));

		sendUpdate(players, update); // Send hand data to clients
	}

	private void takeTurns(ArrayList<Player> players) {
		System.out.println("Taking turns");
		Update update = new Update();

		// Take turns
		for (Player player : players) {
			player.takeTurn(dealer);
			update.addValue(new Value("HAND", player.getId(), player.getHand().toString()));
		}
		dealer.takeTurn(); // Dealer goes last
		update.addValue(new Value("HAND", "DEALER", dealer.getHand().toString()));

		sendUpdate(players, update); // Send hand data to clients
	}

	private void showResults(ArrayList<Player> players) {
		System.out.println("Showing results");
		Update update = new Update();

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
			update.addValue(new Value("RESULT", player.getId(), result + "," + player.getBalance())); // RESULT,BALANCE
		}
		sendUpdate(players, update); // Send results to clients
	}

	private void resetCards(ArrayList<Player> players) {
		System.out.println("Resetting cards");
		ArrayList<Card> cards = new ArrayList<>();

		for (Player player : players) {
			cards.addAll(player.getHand().clearCards()); // Take cards from all players
		}
		cards.addAll(dealer.getHand().clearCards()); // Take cards from the dealer
		dealer.putCards(cards); // Put cards back in the deck
	}

	/**
	 * Send updates to clients
	 * @param players list of players
	 * @param data data to send
	 */
	private void sendUpdate(ArrayList<Player> players, Object data) {
		for (Player player : players) {
			Messenger.sendMessage(player.getSocket(), new Message(Type.UPDATE, gson.toJson(data)));
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
			players.add(player);

			// The client has been connected, send their UUID
			Update update = new Update();
			update.addValue(new Value("ID", player.getId(), null));
			update.addValue(new Value("BALANCE", "", player.getBalance()));
			update.addValue(new Value("MIN", "", MIN_BET));
			update.addValue(new Value("MAX", "", MAX_BET));
			Messenger.sendMessage(clientSock, new Message(Type.JOIN, gson.toJson(update)));
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

	private void setBet(Player player, int value) {
		if (value < MIN_BET) {
			player.setBet(MIN_BET);
		} else if (value > MAX_BET) {
			player.setBet(MAX_BET);
		} else {
			player.setBet(value);
		}
	}
}
