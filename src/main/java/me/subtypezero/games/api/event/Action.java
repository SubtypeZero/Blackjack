package me.subtypezero.games.api.event;

public enum Action {
	DOUBLE(1), SPLIT(2), CLEAR(3), DEAL(4), STAND(5), HIT(6), REPEAT(7);

	private int value;

	Action(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
