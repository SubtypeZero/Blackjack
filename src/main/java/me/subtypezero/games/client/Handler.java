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
		Message info = getMessage(); // Get player info

		if (info.getType() == Type.UPDATE) {
			Update update = getUpdate(info.getData());

			for (Value value : update.getValues()) {
				if (value.getId().equals(client.getId())) {
					continue;
				}

				switch (value.getType()) {
					case "ID":
						String id = value.getId();

						if (!clientIds.contains(id)) {
							clientIds.add(id);
							players.add(new Player(id, initBal));
						}
						break;
					case "BET":
						Player player = getPlayerById(value.getId());

						if (player != null) {
							player.setBet(Integer.parseInt((String) value.getData()));
						}
						break;
					case "BALANCE":
						// TODO Add this feature so balance doesn't reset every round
						break;
				}
			}
		}

		System.out.println("Waiting...");
		suspend();
		System.out.println("Dealing cards");
		dealCards();
	}

	private void dealCards() {
		System.out.println("Dealing cards");
		Messenger.sendMessage(client.getSocket(), new Message(Type.BET, "" + getBet())); // Send bet to server
		Message info = getMessage();

		if (info.getType() == Type.UPDATE) {
			Update update = getUpdate(info.getData());

			for (Value value : update.getValues()) {
				if (value.getType().equals("HAND")) {
					getPlayerById(value.getId()).getHand().setCards(getCardsFromString((String) value.getData())); // Get hand data
				}
			}
		}
		updateCards(); // Update the display
		takeTurns();
	}

	private void takeTurns() {
		boolean done = false;

		while (!done) {
			Message msg = getMessage();

			if (msg.getType() == Type.UPDATE) {
				Update update = getUpdate(msg.getData());

				String action = (String) update.getValues().get(0).getData(); // TODO Fix null pointer exception

				Platform.runLater(new Runnable() {
					@Override
					public void run() {

						switch (action) {
							case "DOUBLE":
								// double, hit, stand
								client.getDisplay().showActions(true);
								break;
							case "NORMAL":
								// hit, stand
								client.getDisplay().showActions(false);
								break;
							case "BUST":
								// bust
								client.getDisplay().showOptions();
								break;
						}
					}
				});

				// TODO Wait for selection, send choice to server (double, hit, stand)
				// Messenger.sendMessage(client.getSocket(), new Message(Type.STAND, null));
			}
		}
		showResults();
	}

	private void showResults() {

	}

	public void clearTable() {
		System.out.println("Clearing table");
		clearCards();
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
	private Message getMessage() {
		return Messenger.getResponse(client.getSocket());
	}

	/**
	 * Get an update from JSON
	 * @param data the JSON string
	 * @return the update
	 */
	private Update getUpdate(String data) {
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

	/**
	 * Clear all cards in the display
	 */
	private void clearCards() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < players.size(); i++) {
					client.getDisplay().clearCards(i);
				}
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
