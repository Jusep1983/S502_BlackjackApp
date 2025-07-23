package com.jusep1983.blackjack.game;

import com.jusep1983.blackjack.game.dto.CreateGameDTO;
import com.jusep1983.blackjack.shared.response.MyApiResponse;
import com.jusep1983.blackjack.shared.response.ResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/game")
@SecurityRequirement(name = "bearerAuth")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /* ---------------------------------------------------------------------
     * Crear partida
     * ------------------------------------------------------------------ */
    @Operation(summary = "Create a new game for the authenticated player")
    @ApiResponse(responseCode = "201", description = "Game created")
    @ApiResponse(responseCode = "404", description = "Player not found")
    @PostMapping("/new")
    public Mono<ResponseEntity<MyApiResponse<Game>>> createGame(
            @RequestBody(required = false) CreateGameDTO ignored // opcional; el user viene del token
    ) {
        return gameService.createGame()
                .map(game -> ResponseBuilder.created("Game created", game));
    }

    /* ---------------------------------------------------------------------
     * Obtener partida por ID
     * ------------------------------------------------------------------ */
    @Operation(summary = "Get game details by ID")
    @ApiResponse(responseCode = "200", description = "Game found")
    @ApiResponse(responseCode = "404", description = "Game not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MyApiResponse<Game>>> getGameById(
            @Parameter(description = "ID of the game to retrieve", required = true)
            @PathVariable String id
    ) {
        return gameService.getGameById(id)
                .map(game -> ResponseBuilder.ok("Game " + id + " found", game));
    }

    /* ---------------------------------------------------------------------
     * Player HIT
     * ------------------------------------------------------------------ */
    @Operation(summary = "Player hits (draws a card)")
    @ApiResponse(responseCode = "200", description = "Card drawn / Game updated")
    @ApiResponse(responseCode = "404", description = "Game not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @PostMapping("/{id}/hit")
    public Mono<ResponseEntity<MyApiResponse<Game>>> playerHit(
            @PathVariable("id") String gameId
    ) {
        return gameService.playerHit(gameId)
                .map(game -> ResponseBuilder.ok("Card drawn", game));
    }

    /* ---------------------------------------------------------------------
     * Player STAND
     * ------------------------------------------------------------------ */
    @Operation(summary = "Player stands (dealer plays, game resolves)")
    @ApiResponse(responseCode = "200", description = "Game finished")
    @ApiResponse(responseCode = "404", description = "Game not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @PostMapping("/{id}/stand")
    public Mono<ResponseEntity<MyApiResponse<Game>>> playerStand(
            @PathVariable("id") String gameId
    ) {
        return gameService.playerStand(gameId)
                .map(game -> ResponseBuilder.ok("Game finished", game));
    }

    /* ---------------------------------------------------------------------
     * Delete game
     * ------------------------------------------------------------------ */
    @Operation(summary = "Delete a game (owner only)")
    @ApiResponse(responseCode = "204", description = "Game deleted")
    @ApiResponse(responseCode = "404", description = "Game not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @DeleteMapping("/{id}/delete")
    public Mono<ResponseEntity<MyApiResponse<Void>>> deleteGame(@PathVariable String id) {
        return gameService.deleteGameById(id)
                .thenReturn(ResponseBuilder.ok("Game deleted", null));
    }

}
