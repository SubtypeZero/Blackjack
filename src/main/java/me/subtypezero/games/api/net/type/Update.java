package me.subtypezero.games.api.net.type;

import java.util.ArrayList;

public class Update {
	private ArrayList<Value> values;

	/**
	 * Create a list of updates
	 */
	public Update() {
		values = new ArrayList<>();
	}

	/**
	 * Get a list of values
	 * @return the values
	 */
	public ArrayList<Value> getValues() {
		return (ArrayList<Value>) values.clone();
	}

	/**
	 * Add a value to the list of values
	 * @param value the value to add
	 */
	public void addValue(Value value) {
		values.add(value);
	}
}
