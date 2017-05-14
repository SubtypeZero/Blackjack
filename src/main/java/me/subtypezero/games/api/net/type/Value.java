package me.subtypezero.games.api.net.type;

public class Value {
	private String type;
	private final String uuid;
	private Object value;

	public Value(String type, String uuid, Object value) {
		this.type = type;
		this.uuid = uuid;
		this.value = value;
	}

	/**
	 * Get the type of value
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Get the player's UUID
	 * @return the id
	 */
	public String getId() {
		return uuid;
	}

	/**
	 * Get the value
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}
}
