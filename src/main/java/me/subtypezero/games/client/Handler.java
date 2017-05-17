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

public class Handler implements Runnable {
	private Gson gson;

	private Client client;
	private ArrayList<String> clientIds;
	private ArrayList<Player> players; // 0 = Dealer, 1 = Client
	private boolean running;
	private boolean betPlaced;

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

	private void start() {
		Message msg = getMessage();

		if (msg.getType() == Type.JOIN) {
			switch (msg.getData()) {
				case "FULL":
					System.out.println("Server is full, closing application");
					System.exit(0);
					break;
				default:
					Update update = getUpdate(msg.getData());
					client.setId(update.getValue("ID").getId());
					initBal = (int)(double) update.getValue("BALANCE").getData(); // The value is received as a double, JSON issue?
					System.out.println("Connected to server!");
					break;
			}
		}
	}

	public void run() {
		start();

		while(running) {
			startRound(); // Start round
			dealCards(); // Deal cards
			takeTurns(); // Take turns
			showResults(); // Show results
			// TODO resetTable(); // Reset table
		}
	}

	private void startRound() {
		clientIds.add("DEALER");
		clientIds.add(client.getId());
		players.add(new Player("DEALER", -1));
		players.add(new Player(client.getId(), initBal));

		Message info = getMessage(); // Get game info

		if (info.getType() == Type.DEAL) {
			Update update = getUpdate(info.getData());
			minBet = Integer.parseInt((String) update.getValue("MIN").getData());
			maxBet = Integer.parseInt((String) update.getValue("MAX").getData());

			while (!betPlaced) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Messenger.sendMessage(client.getSocket(), new Message(Type.BET, "" + getBet())); // Send bet to server
		}

		info = getMessage(); // Get player info

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
				}
			}
		}
	}

	private void dealCards() {
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
	}

	private void takeTurns() {

	}

	private void showResults() {

	}

	private void resetTable() {
		System.out.println("Resetting table");
		clearTable();
		clientIds.clear();
		players.clear();
		betPlaced = false;
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
	private void clearTable() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < players.size(); i++) {
					client.getDisplay().clearCards(i);
				}
			}
		});
	}

	public void setBetPlaced(boolean betPlaced) {
		this.betPlaced = betPlaced;
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
