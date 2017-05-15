import me.subtypezero.games.api.card.Card;
import me.subtypezero.games.api.card.Suit;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CardTest {

	@Test
	public void testCards() {
		Card ace = new Card(Suit.SPADES, 1);
		assertEquals(Suit.SPADES, ace.getSuit());

		Card king = new Card(Suit.DIAMONDS, 13);
		assertEquals(13, king.getValue());
		assertEquals(10, king.getFaceValue());
	}
}
