package me.subtypezero.games.client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import me.subtypezero.games.api.card.Card;
import me.subtypezero.games.api.card.Suit;
import me.subtypezero.games.client.gui.CardPane;

public class Client extends Application {

	@Override
	public void start(Stage primaryStage) {
		BorderPane borderPane = new BorderPane();
		HBox gamePane = new HBox();

		// Player panes
		CardPane dealer = new CardPane("Dealer");
		dealer.addCard(new Card(Suit.SPADES, 1));
		dealer.addCard(new Card(Suit.HEARTS, 12));
		dealer.addCard(new Card(Suit.CLUBS, 13));
		dealer.addCard(new Card(Suit.DIAMONDS, 10));

		CardPane player1 = new CardPane("Player 1");
		CardPane player2 = new CardPane("Player 2");
		CardPane player3 = new CardPane("Player 3");

		gamePane.getChildren().add(dealer);
		gamePane.getChildren().add(player1);
		gamePane.getChildren().add(player2);
		gamePane.getChildren().add(player3);

		borderPane.setCenter(gamePane);


		// Buttons (double, hit, stand)
		HBox options = new HBox(10);
		options.setPadding(new Insets(10));
		options.setAlignment(Pos.CENTER);

		Button btnDouble = new Button("Double");
		Button btnHit = new Button("Hit");
		Button btnStand = new Button("Stand");

		options.getChildren().add(btnDouble);
		options.getChildren().add(btnHit);
		options.getChildren().add(btnStand);

		borderPane.setBottom(options);


		Scene scene = new Scene(borderPane, 362, 382);
		scene.getStylesheets().add("style.css");

		primaryStage.setTitle("Blackjack");
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
