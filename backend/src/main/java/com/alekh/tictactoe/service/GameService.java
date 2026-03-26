package com.alekh.tictactoe.service;

import com.alekh.tictactoe.model.GameState;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {
    private final Map<String, GameState> games = new ConcurrentHashMap<>();

    public GameState createGame(String mode) {
        GameState game = new GameState();
        game.setGameId(UUID.randomUUID().toString());
        game.setMode("computer".equalsIgnoreCase(mode) ? "computer" : "multiplayer");
        game.setCurrentPlayer("X");
        games.put(game.getGameId(), game);
        return game;
    }

    public GameState makeMove(String gameId, int index) {
        GameState game = games.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found.");
        }
        if (game.isFinished()) {
            throw new IllegalArgumentException("Game is already finished.");
        }
        if (index < 0 || index > 8) {
            throw new IllegalArgumentException("Move index must be 0-8.");
        }
        if (!game.getBoard().get(index).isEmpty()) {
            throw new IllegalArgumentException("Cell already occupied.");
        }

        game.getBoard().set(index, game.getCurrentPlayer());
        updateResult(game);
        if (game.isFinished()) {
            return game;
        }

        if ("computer".equals(game.getMode()) && "X".equals(game.getCurrentPlayer())) {
            game.setCurrentPlayer("O");
            applyComputerMove(game);
            updateResult(game);
            if (!game.isFinished()) {
                game.setCurrentPlayer("X");
            }
            return game;
        }

        game.setCurrentPlayer("X".equals(game.getCurrentPlayer()) ? "O" : "X");
        return game;
    }

    private void applyComputerMove(GameState game) {
        int computerMove = pickBestMove(game.getBoard(), "O", "X");
        if (computerMove >= 0) {
            game.getBoard().set(computerMove, "O");
        }
    }

    private int pickBestMove(List<String> board, String aiPlayer, String humanPlayer) {
        for (int i = 0; i < 9; i++) {
            if (board.get(i).isEmpty()) {
                board.set(i, aiPlayer);
                if (isWinner(board, aiPlayer)) {
                    board.set(i, "");
                    return i;
                }
                board.set(i, "");
            }
        }

        for (int i = 0; i < 9; i++) {
            if (board.get(i).isEmpty()) {
                board.set(i, humanPlayer);
                if (isWinner(board, humanPlayer)) {
                    board.set(i, "");
                    return i;
                }
                board.set(i, "");
            }
        }

        int[] preference = {4, 0, 2, 6, 8, 1, 3, 5, 7};
        for (int idx : preference) {
            if (board.get(idx).isEmpty()) {
                return idx;
            }
        }
        return -1;
    }

    private void updateResult(GameState game) {
        List<String> board = new ArrayList<>(game.getBoard());
        if (isWinner(board, "X")) {
            game.setWinner("X");
            game.setFinished(true);
            return;
        }
        if (isWinner(board, "O")) {
            game.setWinner("O");
            game.setFinished(true);
            return;
        }
        if (board.stream().noneMatch(String::isEmpty)) {
            game.setDraw(true);
            game.setFinished(true);
        }
    }

    private boolean isWinner(List<String> b, String player) {
        int[][] lines = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
        };
        for (int[] line : lines) {
            if (player.equals(b.get(line[0])) &&
                player.equals(b.get(line[1])) &&
                player.equals(b.get(line[2]))) {
                return true;
            }
        }
        return false;
    }
}
