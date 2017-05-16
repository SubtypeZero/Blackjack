package me.subtypezero.games.client;

import me.subtypezero.games.api.net.Message;
import me.subtypezero.games.api.net.Messenger;
import me.subtypezero.games.api.net.Type;
import me.subtypezero.games.client.entity.Player;

public class Handler implements Runnable {
	private Client client;
	private Player player;
	private boolean running;

	private final int MIN_BET;
	private final int MAX_BET;
	private int bet;

	public Handler(Client client) {
		this.client = client;
		running = true;

		// TODO Get min and max values from server
		MIN_BET = 5;
		MAX_BET = 100;
		bet = MIN_BET;
	}

	private void init() {
		Message msg = Messenger.getResponse(client.getSocket());

		System.out.println("Message received");

		if (msg.getType() == Type.JOIN) {
			switch (msg.getData()) {
				case "SUCCESS":
					System.out.println("Connected to game!");
					break;
				case "FULL":
					System.out.println("Server is full, closing application");
					System.exit(0);
					break;
			}
		}
	}

	public void run() {
		init();

		while(running) {
			// TODO Handle messages from server
		}
	}

	public int getMinBet() {
		return MIN_BET;
	}

	public int getMaxBet() {
		return MAX_BET;
	}

	public int getBet() {
		return bet;
	}

	public void setBet(int value) {
		if (value < MIN_BET) {
			bet = MIN_BET;
		} else if (value > MAX_BET) {
			bet = MAX_BET;
		} else {
			bet = value;
		}
	}
}
