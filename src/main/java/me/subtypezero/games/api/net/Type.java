package me.subtypezero.games.api.net;

public class Type {
	// Update
	public static final int JOIN = 1;
	public static final int LEAVE = 2;
	public static final int VALUE = 3;
	public static final int ACTION = 4;

	// Action
	public static final int BET = 5;
	public static final int DEAL = 6;
	public static final int CLEAR = 7;
	public static final int DOUBLE = 8;
	public static final int HIT = 9;
	public static final int STAND = 10;
	public static final int REPEAT = 11;

	// Result
	public static final int WIN = 12;
	public static final int LOSE = 13;
	public static final int PUSH = 14;
	public static final int BLACKJACK = 15;
}
