package me.subtypezero.games.client.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import me.subtypezero.games.api.card.Card;
import me.subtypezero.games.api.net.Type;
import me.subtypezero.games.client.Client;

public class GUI extends BorderPane {
	private Client client;
	private HBox gamePane;
	private HBox optionPane;

	public GUI(Client client) {
		this.client = client;

		gamePane = new HBox();
		optionPane = new HBox(10);
		optionPane.setPadding(new Insets(10));
		optionPane.setAlignment(Pos.CENTER);

		// Setup card panes
		gamePane.getChildren().add(new CardPane("Dealer"));
		gamePane.getChildren().add(new CardPane("Player 1"));
		gamePane.getChildren().add(new CardPane("Player 2"));
		gamePane.getChildren().add(new CardPane("Player 3"));

		setCenter(gamePane);
		setBottom(optionPane);
		// Setup info view

		showOptions();
	}

	public void showOptions() {
		optionPane.getChildren().clear(); // Clear previous options

		Button btnDeal = new Button("Deal");
		btnDeal.setOnAction(e -> client.onAction(Type.DEAL));
		optionPane.getChildren().add(btnDeal);

		Button btnClear = new Button("Clear");
		btnClear.setOnAction(e -> client.onAction(Type.CLEAR));
		optionPane.getChildren().add(btnClear);
	}

	public void showActions(boolean canDouble) {
		optionPane.getChildren().clear(); // Clear previous options

		if (canDouble) {
			Button btnDouble = new Button("Double");
			btnDouble.setOnAction(e -> client.onAction(Type.DOUBLE));
			optionPane.getChildren().add(btnDouble);
		}

		Button btnHit = new Button("Hit");
		btnHit.setOnAction(e -> client.onAction(Type.HIT));
		optionPane.getChildren().add(btnHit);

		Button btnStand = new Button("Stand");
		btnStand.setOnAction(e -> client.onAction(Type.STAND));
		optionPane.getChildren().add(btnStand);
	}

	public void showTableInfo(int maxBet, int minBet) {

	}

	public void showPlayerInfo(int balance, int bet) {

	}

	public CardPane getCardPane(int index) {
		return (CardPane) gamePane.getChildren().get(index);
	}

	public void addCard(int index, Card card) {
		getCardPane(index).addCard(card);
	}

	public void clearCards(int index) {
		getCardPane(index).clearCards();
	}
}
