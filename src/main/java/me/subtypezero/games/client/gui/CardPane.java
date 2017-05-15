package me.subtypezero.games.client.gui;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import me.subtypezero.games.api.card.Card;

import java.util.ArrayList;

public class CardPane extends TitledPane {
	ArrayList<Card> cards;

	Pane pane;

	public CardPane(String title) {
		super(title, new Pane());

		cards = new ArrayList<>();

		pane = new Pane();
		pane.setPrefSize(91, 320);
		setContent(pane);
		setCollapsible(false);
	}

	public ArrayList<Card> getCards() {
		return (ArrayList<Card>) cards.clone();
	}

	public void addCard(Card card) {
		CardView view = new CardView(card);

		view.setLayoutX(9);
		view.setLayoutY(20 + (cards.size() * 32));

		pane.getChildren().add(view);

		cards.add(card);
	}

	public void clearCards() {
		cards.clear();
	}
}
