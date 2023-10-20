package com.tictactoe.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;

import com.tictactoe.exception.InvalidGameException;
import com.tictactoe.exception.InvalidParamException;
import com.tictactoe.exception.NotFoundException;
import com.tictactoe.model.Game;
import com.tictactoe.model.GamePlay;
import com.tictactoe.model.GameStatus;
import com.tictactoe.model.Player;
import com.tictactoe.model.TicTacToe;
import com.tictactoe.storage.GameStorage;

@Service
@AllArgsConstructor
public class GameService {
	public Game createGame(Player player) {
		Game game = new Game();
		game.setBoard(new int[3][3]);
		game.setGameID(UUID.randomUUID().toString());
		game.setPlayer1(player);
		game.setStatus(GameStatus.NEW);
		GameStorage.getInstance().setGame(game);
		return game;
	}
	
	public Game connectToGame(Player player2, String gameID) throws InvalidParamException, InvalidGameException {
		if (!GameStorage.getInstance().getGames().containsKey(gameID)) {
			throw new InvalidParamException("Game with provided ID doesnt exist");
		}
		Game game = GameStorage.getInstance().getGames().get(gameID);
		
		if (game.getPlayer2() != null) {
			throw new InvalidGameException("Game is not valid anymore");
		}	
		game.setPlayer2(player2);
		game.setStatus(GameStatus.IN_PROGRESS);
		GameStorage.getInstance().setGame(game);
		return game;
	}
	
	public Game connectToRandomGame(Player player2) throws NotFoundException {
		Game game = GameStorage.getInstance().getGames().values().stream()
				.filter(it->it.getStatus().equals(GameStatus.NEW))
				.findFirst().orElseThrow(()-> new NotFoundException("Game not found"));
		game.setPlayer2(player2);
		game.setStatus(GameStatus.IN_PROGRESS);
		GameStorage.getInstance().setGame(game);
		
		return game;
	}
	
	public Game gamePlay(GamePlay gamePlay) throws NotFoundException, InvalidGameException {
		if(!GameStorage .getInstance().getGames().containsKey(gamePlay.getGameID())) {
			throw new NotFoundException("Game not found");
		}
		Game game  = GameStorage .getInstance().getGames().get(gamePlay.getGameID());
		if (game.getStatus().equals(GameStatus.FINISHED)) {
			throw new InvalidGameException("Game is already finished");
		}
		
		int [][] board = game.getBoard();
		board[gamePlay.getCoordinateX()][gamePlay.getCoordinateY()] = gamePlay.getType().getValue(); 

		checkWinner(game.getBoard(), TicTacToe.X);
		checkWinner(game.getBoard(), TicTacToe.O);
		GameStorage.getInstance().setGame(game);
		
		return game;
	}
	
	private Boolean checkWinner(int[][] board, TicTacToe ttt) {
		int [] boardArray = new int[9];
		int counterIndex = 0;
		for (int i=0; i<board.length; i++) {
			for (int j=0; j<board[i].length; j++) {
				boardArray[counterIndex] = board[i][j];
				counterIndex++;
			}
		}
		
		int [][] winningCombinations = {{0,1,2}, {3,4,5}, {6,7,8}, {0,3,6}, {1,4,7}, {2,5,8}, {0,4,8}, {2,4,6}};
		for (int i=0; i<winningCombinations.length; i++) {
			int counter =0;
			for (int j=0; j<winningCombinations[i].length; j++) {
				if (boardArray[winningCombinations[i][j]] == ttt.getValue()) {
					counter++;
					if (counter==3) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
