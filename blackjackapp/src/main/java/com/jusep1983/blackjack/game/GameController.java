package com.jusep1983.blackjack.game;

import com.jusep1983.blackjack.game.dto.CreateGameDTO;
import com.jusep1983.blackjack.shared.response.MyApiResponse;
import com.jusep1983.blackjack.shared.response.ResponseBuilder;
import com.jusep1983.blackjack.shared.utils.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/game")
@SecurityRequirement(name = "bearerAuth")
public class GameController {
    private final GameService gameService;

    @Operation(summary = "Create a new game for the authenticated player")
    @ApiResponse(responseCode = "201", description = "Game created")
    @ApiResponse(responseCode = "404", description = "Player not found")
    @PostMapping("/new")
    public Mono<ResponseEntity<MyApiResponse<Game>>> createGame(
            @RequestBody(required = false) CreateGameDTO ignored // opcional; el user viene del token
    ) {
        return AuthUtils.getCurrentUserName()
                .doOnNext(user -> log.info("User '{}' requested a new game", user))
                .then(gameService.createGame())
                .doOnSuccess(game -> log.info("Game '{}' created for user '{}'", game.getId(), game.getUserName()))
                .map(game -> ResponseBuilder.created("Game created", game));
    }

    @Operation(summary = "Get game details by ID")
    @ApiResponse(responseCode = "200", description = "Game found")
    @ApiResponse(responseCode = "404", description = "Game not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MyApiResponse<Game>>> getGameById(
            @Parameter(description = "ID of the game to retrieve", required = true)
            @PathVariable String id
    ) {
        log.debug("Request to retrieve game '{}'", id);
        return gameService.getGameById(id)
                .doOnSuccess(game -> log.info("Game '{}' successfully retrieved", id))
                .map(game -> ResponseBuilder.ok("Game " + id + " found", game));
    }

    @Operation(summary = "Player hits (draws a card)")
    @ApiResponse(responseCode = "200", description = "Card drawn / Game updated")
    @ApiResponse(responseCode = "404", description = "Game not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @PostMapping("/{id}/hit")
    public Mono<ResponseEntity<MyApiResponse<Game>>> playerHit(
            @PathVariable("id") String gameId
    ) {
        log.info("Hit requested for game '{}'", gameId);
        return gameService.playerHit(gameId)
                .doOnSuccess(game -> log.info("Card drawn for game '{}'", gameId))
                .map(game -> ResponseBuilder.ok("Card drawn", game));
    }

    @Operation(summary = "Player stands (dealer plays, game resolves)")
    @ApiResponse(responseCode = "200", description = "Game finished")
    @ApiResponse(responseCode = "404", description = "Game not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @PostMapping("/{id}/stand")
    public Mono<ResponseEntity<MyApiResponse<Game>>> playerStand(
            @PathVariable("id") String gameId
    ) {
        log.info("Stand requested for game '{}'", gameId);
        return gameService.playerStand(gameId)
                .doOnSuccess(game -> log.info("Game '{}' finished with result: {}", gameId, game.getGameResult()))
                .map(game -> ResponseBuilder.ok("Game finished", game));
    }

    @Operation(summary = "Delete a game (owner only)")
    @ApiResponse(responseCode = "204", description = "Game deleted")
    @ApiResponse(responseCode = "404", description = "Game not found")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @DeleteMapping("/{id}/delete")
    public Mono<ResponseEntity<MyApiResponse<Void>>> deleteGame(@PathVariable String id) {
        log.warn("Delete requested for game '{}'", id);
        return gameService.deleteGameById(id)
                .doOnSuccess(unused -> log.info("Game '{}' deleted", id))
                .thenReturn(ResponseBuilder.ok("Game deleted", null));
    }

}
