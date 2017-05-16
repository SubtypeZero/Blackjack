package me.subtypezero.games.api.entity;

import me.subtypezero.games.api.card.Card;
import me.subtypezero.games.api.card.Hand;

import java.util.ArrayList;

public class Gambler {
	private Hand hand = new Hand();

	/**
	 * Get the gambler's hand
	 * @return the hand
	 */
	public Hand getHand() {
		return hand;
	}

	/**
	 * Get a list of possible values of the gambler's hand
	 * @return the hand values
	 */
	public ArrayList<Integer> getHandValues() {
		ArrayList<Integer> values = new ArrayList<>();

		int aces = 0;
		int min = 0;
		int max = 0;

		for (Card card : hand.getCards()) {
			if (card.getValue() == 1) {
				aces++;
			}
			min += card.getFaceValue(); // add the value of the card (aces count as 1)
		}

		max = min; // max is at least as big as min

		for (int i = 0; i < aces; i++) {
			if (max + 10 <= 21) {
				max += 10; // use as many aces as possible
			}
		}

		values.add(min);

		if (max > min) {
			values.add(max); // add the max value if necessary
		}

		return values;
	}
}
