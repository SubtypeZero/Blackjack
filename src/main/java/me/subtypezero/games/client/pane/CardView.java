package me.subtypezero.games.client.pane;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import me.subtypezero.games.api.card.Card;

public class CardView extends ImageView {

	/**
	 * Create a card view
	 * @param card the card
	 */
	public CardView(Card card) {
		int row = 0;

		switch (card.getSuit()) {
			case SPADES:
				break;
			case DIAMONDS:
				row = 1;
				break;
			case CLUBS:
				row = 2;
				break;
			case HEARTS:
				row = 3;
				break;
		}

		Rectangle2D clip = new Rectangle2D((card.getValue() - 1) * 71, row * 96, 71, 96);
		setImage(new Image("cards.gif", false));
		setViewport(clip);
	}
}
