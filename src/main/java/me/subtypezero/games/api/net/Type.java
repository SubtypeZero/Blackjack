package me.subtypezero.games.api.net;

public class Type {
	// Update
	public static final int JOIN = 1;
	public static final int LEAVE = 2;
	public static final int HAND = 3;
	public static final int BALANCE = 4;
	public static final int ACTION = 5;

	// Action
	public static final int BET = 6;
	public static final int DEAL = 7;
	public static final int CLEAR = 8;
	public static final int DOUBLE = 9;
	public static final int HIT = 10;
	public static final int STAND = 11;
	public static final int REPEAT = 12;

	// Result
	public static final int WIN = 13;
	public static final int LOSE = 14;
	public static final int PUSH = 15;
	public static final int BLACKJACK = 16;
}
