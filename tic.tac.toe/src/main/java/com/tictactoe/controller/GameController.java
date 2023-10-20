package com.tictactoe.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.tictactoe.controller.dto.ConnectRequest;
import com.tictactoe.exception.InvalidGameException;
import com.tictactoe.exception.InvalidParamException;
import com.tictactoe.exception.NotFoundException;
import com.tictactoe.model.Game;
import com.tictactoe.model.GamePlay;
import com.tictactoe.model.Player;
import com.tictactoe.service.GameService;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/game")
public class GameController {
	private final GameService gameService;
	private final SimpMessagingTemplate simpMessagingTemplate;
	
	@PostMapping("/Start")
	public ResponseEntity<Game> start(@RequestBody Player player) {
		log.info("start game request: {}", player);
		return ResponseEntity.ok(gameService.createGame(player));
	}
	
	@PostMapping("/connect")
	public ResponseEntity<Game> connect(@RequestBody ConnectRequest request) throws InvalidParamException, InvalidGameException {
		log.info("connect request: {}", request);
		return ResponseEntity.ok(gameService.connectToGame(request.getPlayer(), request.getGameID()));	
	}
	
	@PostMapping("/connect/random")
	public ResponseEntity<Game> connectRandom(@RequestBody Player player) throws NotFoundException {
		log.info("Connect random: {}", player);
		return ResponseEntity.ok(gameService.connectToRandomGame(player));	
	}
	
	@PostMapping("/gameplay")
	public ResponseEntity<Game> gamePlay(@RequestBody GamePlay request) throws InvalidParamException, InvalidGameException, NotFoundException {
		log.info("gameplay: {}", request);
		Game game = gameService.gamePlay(request);
		simpMessagingTemplate.convertAndSend("/topic/game-progress" +game.getGameID(), game);
		return ResponseEntity.ok(game);
	}
	
	
}
