package me.subtypezero.games.api.net;

public class Message {
	private int type;
	private String data;

	/**
	 * Create a message
	 * @param type the type of message
	 * @param data the data to send
	 */
	public Message(int type, String data) {
		this.type = type;
		this.data = data;
	}

	/**
	 * Create a message with no data
	 * @param type the type of message
	 */
	public Message(int type) {
		this.type = type;
		this.data = "";
	}

	/**
	 * Get the type of message
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * Get the data from the message
	 * @return the data
	 */
	public String getData() {
		return data;
	}
}
