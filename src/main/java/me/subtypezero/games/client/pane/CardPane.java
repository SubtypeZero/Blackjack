package me.subtypezero.games.client.pane;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import me.subtypezero.games.api.card.Card;

public class CardPane extends TitledPane {
	Pane pane;

	public CardPane(String title) {
		super(title, new Pane());

		pane = new Pane();
		pane.setPrefSize(91, 320);
		setContent(pane);
		setCollapsible(false);
	}

	public void addCard(Card card) {
		CardView view = new CardView(card);
		view.setLayoutX(9);
		view.setLayoutY(20 + (pane.getChildren().size() * 32));
		pane.getChildren().add(view);
	}

	public void clearCards() {
		pane.getChildren().clear();
	}
}
