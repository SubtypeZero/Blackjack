package me.subtypezero.games.api.net.type;

import java.util.ArrayList;

public class Action {
	private ArrayList<Integer> actions;

	/**
	 * Create a list of actions
	 */
	public Action() {
		actions = new ArrayList<>();
	}

	/**
	 * Get a list of actions
	 * @return the actions
	 */
	public ArrayList<Integer> getActions() {
		return (ArrayList<Integer>) actions.clone();
	}

	/**
	 * Add an action to the list of actions
	 * @param action the action to add
	 */
	public void addAction(int action) {
		actions.add(action);
	}
}
