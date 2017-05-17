package me.subtypezero.games.api.net.update;

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
	 * Get value by type
	 * @param type the value type
	 * @return the first value found or null
	 */
	public Value getValue(String type) {
		for (Value value : values) {
			if (type.equals(value.getType())) {
				return value;
			}
		}
		return null;
	}

	/**
	 * Add a value to the list of values
	 * @param value the value to add
	 */
	public void addValue(Value value) {
		values.add(value);
	}
}
