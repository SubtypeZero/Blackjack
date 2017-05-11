package me.subtypezero.games;

import me.subtypezero.games.api.Player;
import me.subtypezero.games.api.card.Deck;

import java.net.Socket;
import java.util.LinkedList;

public class BlackJack implements Runnable {
	private LinkedList<Player> players;
	private Deck deck;
	private boolean running = true;

	private final int INIT_BAL;
	private final int MIN_BET;
	private final int MAX_BET;

	private final int MAX_PLAYERS = 3;

	public BlackJack(int initBal, int minBet, int maxBet, int decks) {
		players = new LinkedList<>();
		deck = new Deck();

		if (decks > 1) {
			deck.addDecks(decks - 1);
		}

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
				// deal cards to players, update the clients
			}

			// Players take turns
			for (Player player : players) {
				// player.takeTurn();
			}

			// Dealer takes turn
			// reveal hidden card
			// draw to 16 and stand on 17

			// calculate results
		}
		// End of game
	}

	public void stop() {
		running = false;
	}

	public boolean connect(Socket clientSock) {
		if (isOpen()) {
			// First sits in middle, second sits on left, third sits on right
			if (getPlayerCount() < MAX_PLAYERS - 1) {
				players.addLast(new Player(clientSock, INIT_BAL));
			} else {
				players.addFirst(new Player(clientSock, INIT_BAL));
			}
			// send server information to client
			return true;
		}
		return false;
	}

	public int getPlayerCount() {
		return players.size();
	}

	public boolean isOpen() {
		return getPlayerCount() < MAX_PLAYERS;
	}
}
