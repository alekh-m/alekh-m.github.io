package com.alekh.tictactoe.controller;

import com.alekh.tictactoe.model.GameState;
import com.alekh.tictactoe.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/new")
    public ResponseEntity<?> newGame(@RequestBody Map<String, String> request) {
        String mode = request.getOrDefault("mode", "multiplayer");
        return ResponseEntity.ok(gameService.createGame(mode));
    }

    @PostMapping("/move")
    public ResponseEntity<?> move(@RequestBody Map<String, Object> request) {
        try {
            String gameId = (String) request.get("gameId");
            Integer index = (Integer) request.get("index");
            if (gameId == null || index == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "gameId and index are required."));
            }
            GameState game = gameService.makeMove(gameId, index);
            return ResponseEntity.ok(game);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
