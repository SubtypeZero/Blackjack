package me.subtypezero.games;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	private ServerSocket serverSocket;
	private ArrayList<BlackJack> games;
	private ExecutorService executor;

	private final int MAX_GAMES;

	public Server(int max) {
		this.MAX_GAMES =  max;
		executor = Executors.newFixedThreadPool(MAX_GAMES);

		// Create a list of BlackJack sessions
		games = new ArrayList<>();

		// Create a server socket
		try {
			serverSocket = new ServerSocket(8103);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Start accepting connections
		start();
	}

	private void start() {
		while (true) {
			try {
				Socket clientSock = serverSocket.accept();

				// find available game
				BlackJack game = getOpenGame();

				if (game != null) {
					// connect player to game
					// game.connect(clientSock);
				}

				// Server is full, send error message
				clientSock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void stop() {
		// for all games, stop
		// wait for games to stop
		// force games to stop
		// close server
	}

	private BlackJack getOpenGame() {
		// Find first available game
		for (BlackJack game : games) {
			if (game.isOpen()) {
				return game;
			}
		}

		// Create a new game
		if (games.size() < MAX_GAMES) {
			BlackJack game = new BlackJack(0, 0, 0, 0); // TODO Add real values
			executor.execute(game); // start the game
			games.add(game);
			return game;
		}

		// Server is full
		return null;
	}
}
