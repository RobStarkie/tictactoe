package com.tictactoe.model;

import lombok.Data;

@Data
public class Game {
	
	private String gameID;
	private Player player1;
	private Player player2;
	private GameStatus status;
	private int [][] board;
	private TicTacToe winner;
	
}
