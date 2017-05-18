package me.subtypezero.games.client;

import com.google.gson.Gson;
import javafx.application.Platform;
import me.subtypezero.games.api.card.Card;
import me.subtypezero.games.api.card.Suit;
import me.subtypezero.games.api.net.Message;
import me.subtypezero.games.api.net.Messenger;
import me.subtypezero.games.api.net.update.Type;
import me.subtypezero.games.api.net.update.Update;
import me.subtypezero.games.api.net.update.Value;
import me.subtypezero.games.client.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Handler extends Thread {
	private Gson gson;

	private Client client;
	private ArrayList<String> clientIds;
	private ArrayList<Player> players; // 0 = Dealer, 1 = Client
	private boolean running;

	private int initBal;
	private int minBet;
	private int maxBet;

	public Handler(Client client) {
		gson = new Gson();
		this.client = client;
		clientIds = new ArrayList<>();
		players = new ArrayList<>();
		running = true;
	}

	private void init() {
		Message msg = getMessage();

		if (msg.getType() == Type.JOIN) {
			if (msg.getData().equals("FULL")) {
				System.out.println("Server is full, closing application");
				System.exit(0);
			}

			Update info = getUpdate(msg.getData());

			client.setId(info.getValue("ID").getId());
			initBal = (int)(double) info.getValue("BALANCE").getData(); // The value is received as a double, JSON issue?
			minBet = (int)(double) info.getValue("MIN").getData();
			maxBet = (int)(double) info.getValue("MAX").getData();
			System.out.println("Connected to server!");
		}
	}

	public void run() {
		init(); // Get server info
		clearTable(); // Clear table and get bet

		while(running) {
			startRound(); // Start round
		}
	}

	private void startRound() {
		System.out.println("Starting round");
		Update update = getUpdate(getMessage().getData()); // Get player info

		for (Value value : update.getValues()) {
			if (value.getId().equals(client.getId())) {
				continue;
			}

			String id = value.getId();
			Player player = getPlayerById(id);

			switch (value.getType()) {
				case "ID":
					if (!clientIds.contains(id)) {
						clientIds.add(id);
						players.add(new Player(id, initBal));
					}
					break;
				case "BET":
					if (player != null) {
						player.setBet(Integer.parseInt((String) value.getData()));
					}
					break;
				case "BALANCE":
					if (player != null) {
						player.setBalance(Integer.parseInt((String) value.getData()));
					}
					break;
			}
		}
		suspend();
		dealCards();
	}

	private void dealCards() {
		System.out.println("Dealing cards");
		updateHands();
		updateCards(); // Update the display
		takeTurns();
	}

	private void takeTurns() {
		System.out.println("Taking turns");
		boolean done = false;
		boolean cardsUpdated = false;
		Message msg = getMessage();

		System.out.println("Message: " + msg.getType() + ":" + msg.getData());

		if (msg.getData().equals("NONE")) {
			clearOptions(); // blackjack or bust
			// updateHands();
			done = true;
		}

		System.out.println("Entering loop");

		while (!done) {
			System.out.println("DATA - " + msg.getData());

			switch (msg.getData()) {
				case "DOUBLE":
					System.out.println("DOUBLE");
					showActions(true); // double, hit, stand
					done = true;
					suspend();

					ArrayList<Card> cards = getCardsFromString(getMessage().getData());
					for (Card card : cards) {
						players.get(1).getHand().getCards().add(card);
					}
					updateCards();
					break;
				case "NORMAL":
					System.out.println("NORMAL");
					showActions(false); // hit, stand
					suspend();
					break;
				case "NONE":
					System.out.println("NONE");
					clearOptions(); // blackjack or bust
					done = true;
					break;
				default:
					System.out.println("DEFAULT");
					Update update = getUpdate(msg.getData());

					System.out.println(msg.getData());

					for (Value value : update.getValues()) {
						if (value.getType().equals("HAND")) {
							getPlayerById(value.getId()).getHand().setCards(getCardsFromString((String) value.getData())); // Get hand data
						}
					}
					cardsUpdated = true;
					done = true;
					break;
			}

			if (!cardsUpdated && !done) {
				msg = getMessage();
			}
		}
		showResults();
	}

	private void showResults() {
		System.out.println("Showing results");
		HashMap<String, Integer> results = new HashMap<>();

		Update update = getUpdate(getMessage().getData()); // Get results

		for (Value value : update.getValues()) {
			Player player = getPlayerById(value.getId());
			String[] values = ((String) update.getValues().get(0).getData()).split(",");

			int result = Integer.parseInt(values[0]);
			int balance = Integer.parseInt(values[1]);

			results.put(value.getId(), result);
			player.setBalance(balance);
		}

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				for (Entry<String, Integer> entry : results.entrySet()) {
					Player player = getPlayerById(entry.getKey());
					int result = entry.getValue();

					switch (result) {
						case Type.WIN:
							client.getDisplay().getResultPane().addResult(player, "Win");
							break;
						case Type.LOSE:
							client.getDisplay().getResultPane().addResult(player, "Lose");
							break;
						case Type.PUSH:
							client.getDisplay().getResultPane().addResult(player, "Push");
							break;
						case Type.BLACKJACK:
							client.getDisplay().getResultPane().addResult(player, "Blackjack");
							break;
					}
				}
				client.getDisplay().showOptions(true); // Allow the client to start a new game
			}
		});
		showOptions(true);
		System.out.println("Showing options");
	}

	public void clearTable() {
		System.out.println("Clearing table");
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < players.size(); i++) {
					client.getDisplay().clearCards(i); // Clear all cards
				}
				client.getDisplay().getResultPane().clearResults();
			}
		});

		clientIds.clear();
		players.clear();

		clientIds.add("DEALER");
		clientIds.add(client.getId());
		players.add(new Player("DEALER", -1));
		players.add(new Player(client.getId(), initBal));

		// Have client place bet
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				client.showDialog();
			}
		});
		suspend();
		Messenger.sendMessage(client.getSocket(), new Message(Type.BET, "" + getBet())); // Send bet to server
	}

	/**
	 * Get a message from the server
	 * @return
	 */
	public Message getMessage() {
		return Messenger.getResponse(client.getSocket());
	}

	/**
	 * Get an update from JSON
	 * @param data the JSON string
	 * @return the update
	 */
	public Update getUpdate(String data) {
		return gson.fromJson(data, Update.class);
	}

	/**
	 * Get the minimum bet amount
	 * @return the amount
	 */
	public int getMinBet() {
		return minBet;
	}

	/**
	 * Get the maximum bet amount
	 * @return the amount
	 */
	public int getMaxBet() {
		return maxBet;
	}

	/**
	 * Get the client's bet
	 * @return
	 */
	public int getBet() {
		return players.get(1).getBet();
	}

	/**
	 * Set the client's bet
	 * @param value the amount to set
	 */
	public void setBet(int value) {
		if (value < minBet) {
			players.get(1).setBet(minBet);
		} else if (value > maxBet) {
			players.get(1).setBet(maxBet);
		} else {
			players.get(1).setBet(value);
		}
	}

	private void updateHands() {
		Update update = getUpdate(getMessage().getData());

		for (Value value : update.getValues()) {
			if (value.getType().equals("HAND")) {
				getPlayerById(value.getId()).getHand().setCards(getCardsFromString((String) value.getData())); // Get hand data
			}
		}
	}

	/**
	 * Update all cards in the display
	 */
	private void updateCards() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < players.size(); i++) {
					ArrayList<Card> cards = players.get(i).getHand().getCards();
					client.getDisplay().setCards(i, cards);
				}
			}
		});
	}

	private void clearOptions() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				client.getDisplay().clearOptions();
			}
		});
	}

	private void showOptions(boolean canClear) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				client.getDisplay().showOptions(canClear);
			}
		});
	}

	private void showActions(boolean canDouble) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				client.getDisplay().showActions(canDouble);
			}
		});
	}

	/**
	 * Get a player by their clientId
	 * @param id the clientId
	 * @return the player
	 */
	private Player getPlayerById(String id) {
		for (Player player : players) {
			if (player.getId().equals(id)) {
				return player;
			}
		}
		return null;
	}

	/**
	 * Get a list of cards from a String
	 * @param str the String
	 * @return the list of cards
	 */
	private ArrayList<Card> getCardsFromString(String str) {
		ArrayList<Card> cards = new ArrayList<>();
		String[] parts = str.trim().split(" "); // CLUBS:10 HEARTS:8 SPADES:1

		for (int i = 0; i < parts.length; i++) {
			String[] values = parts[i].split(":");
			cards.add(new Card(Suit.valueOf(values[0]), Integer.parseInt(values[1])));
		}
		return cards;
	}
}
