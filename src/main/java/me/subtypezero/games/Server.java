package me.subtypezero.games;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	private ServerSocket serverSocket;
	private ArrayList<Game> games;
	private ExecutorService executor;

	private final int MAX_GAMES;

	/**
	 * Create a game server
	 * @param max the maximum number of games
	 */
	public Server(int max) {
		this.MAX_GAMES =  max;
		executor = Executors.newFixedThreadPool(MAX_GAMES);
		games = new ArrayList<>(); // Create a list of Game sessions

		try {
			serverSocket = new ServerSocket(8103); // Create a server socket
		} catch (IOException e) {
			e.printStackTrace();
		}

		start();
	}

	/**
	 * Start accepting connections
	 */
	private void start() {
		while (true) {
			try {
				Socket clientSock = serverSocket.accept();
				Game game = getOpenGame(); // find available game

				if (game != null) {
					game.connect(clientSock); // connect player to game
				}

				// TODO send error message, server is full
				clientSock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Stop all games and close the server
	 */
	public void stop() {
		for (Game game : games) {
			game.stop();
		}
		// TODO wait, force games to stop, close server
	}

	/**
	 * Get an open game for players to join
	 * @return an available game, null if server is full
	 */
	private Game getOpenGame() {
		// Find first available game
		for (Game game : games) {
			if (game.isOpen()) {
				return game;
			}
		}

		// Create a new game
		if (games.size() < MAX_GAMES) {
			Game game = new Game(1000, 5, 100, 1);
			executor.execute(game); // start the game
			games.add(game);
			return game;
		}

		// Server is full
		return null;
	}
}
