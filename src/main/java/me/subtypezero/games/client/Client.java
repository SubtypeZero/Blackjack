package me.subtypezero.games.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.subtypezero.games.api.net.update.Type;
import me.subtypezero.games.client.gui.Display;
import me.subtypezero.games.client.gui.Dialog;

import java.io.IOException;
import java.net.Socket;

public class Client extends Application {
	private Socket socket;
	private Display display;
	private Handler handler;
	private Dialog dialog;
	private String id;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		boolean connected = connect("localhost", 8103);

		if (!connected) {
			System.out.println("Connection to server failed, closing application");
			System.exit(-1);
		}

		display = new Display(this);
		handler = new Handler(this);
		dialog = new Dialog(handler);
		new Thread(handler).start();

		Scene scene = new Scene(display, 362, 382);
		scene.getStylesheets().add("style.css");

		primaryStage.setTitle("Blackjack");
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.show();

		while (handler.getMinBet() == 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		dialog.show();
	}

	public void onAction(int type) {
		switch (type) {
			case Type.DEAL:
				break;
			case Type.CLEAR:
				dialog.show();
				break;
			case Type.DOUBLE:
				break;
			case Type.HIT:
				break;
			case Type.STAND:
				break;
		}
	}

	private boolean connect(String host, int port) {
		boolean connected = false;
		int count = 0;

		while (!connected && count < 3) {
			try {
				count++;
				socket = new Socket(host, port);
				connected = true;
			} catch (IOException ex) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return connected;
	}

	public Socket getSocket() {
		return socket;
	}

	public Display getDisplay() {
		return display;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
