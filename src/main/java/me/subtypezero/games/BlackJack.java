package me.subtypezero.games;

import me.subtypezero.games.api.Dealer;
import me.subtypezero.games.api.Player;

import java.util.ArrayList;

public class BlackJack {
	private ArrayList<Player> players; // Use HashMap instead?
	private Dealer dealer;
	private boolean running = true;

	private final int min;
	private final int max;

	public BlackJack(int min, int max) {
		players = new ArrayList<Player>();
		dealer = new Dealer();

		this.min = min;
		this.max = max;

		start();
	}

	public void start() {
		while (running) {
			// for all waiting, connect player (if space is available)

			// dealer deals cards

			// for all players, take action

			// dealer takes action

			// display results
		}

		// game over
	}

	public void stop() {
		running = false;
	}

	public void addPlayer(int id) {

	}

	public void removePlayer(int id) {

	}
}
