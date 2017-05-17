package me.subtypezero.games.api.net.update;

public class Value {
	private String type;
	private final String uuid;
	private Object data;

	public Value(String type, String uuid, Object data) {
		this.type = type;
		this.uuid = uuid;
		this.data = data;
	}

	/**
	 * Get the type of data
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
	 * Get the data
	 * @return the data
	 */
	public Object getData() {
		return data;
	}
}
