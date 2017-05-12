package me.subtypezero.games.api.event;

public enum Result {
	WIN(8), LOSE(9), PUSH(10), BLACKJACK(11);

	private int value;

	Result(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
