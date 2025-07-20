package com.jusep1983.blackjack.game;

import com.jusep1983.blackjack.game.dto.CreateGameDTO;
import com.jusep1983.blackjack.shared.response.MyApiResponse;
import com.jusep1983.blackjack.shared.response.ResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @Operation(summary = "Create a new Blackjack game for the logged-in user")
    @ApiResponse(responseCode = "201", description = "Game created successfully")
    @ApiResponse(responseCode = "403", description = "User not authenticated or access denied")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PostMapping("/new")
    public Mono<ResponseEntity<MyApiResponse<Game>>> createGame(
            @Parameter(hidden = true) @AuthenticationPrincipal String userName
    ) {
        return gameService.createGame(userName)
                .map(game -> ResponseBuilder.created("Game created", game));
    }

    @Operation(summary = "Get game details by ID")
    @ApiResponse(responseCode = "200", description = "Game found")
    @ApiResponse(responseCode = "404", description = "Game not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MyApiResponse<Game>>> getGameById(
            @Parameter(description = "ID of the game to retrieve", required = true)
            @PathVariable String id,
            @Parameter(hidden = true) @AuthenticationPrincipal String userName
    ) {
        return gameService.getGameById(id, userName)
                .map(game -> ResponseBuilder.ok("Game " + id + " found", game));
    }

    @Operation(summary = "Delete game by ID")
    @ApiResponse(responseCode = "204", description = "Game deleted successfully")
    @ApiResponse(responseCode = "404", description = "Game not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @DeleteMapping("/{id}/delete")
    public Mono<ResponseEntity<MyApiResponse<Game>>> deleteById(
            @PathVariable String id,
            @AuthenticationPrincipal String userName
    ) {
        return gameService.deleteGameById(id, userName)
                .then(Mono.just((ResponseBuilder.ok("Game " + id + " deleted successfully", null))));
    }

    @Operation(summary = "Draw a card (hit)")
    @ApiResponse(responseCode = "200", description = "Card drawn")
    @PostMapping("/{id}/hit")
    public Mono<ResponseEntity<MyApiResponse<Game>>> hit(
            @PathVariable String id,
            @Parameter(hidden = true) @AuthenticationPrincipal String userName
    ) {
        return gameService.playerHit(id, userName)
                .map(game -> ResponseBuilder.ok("Card drawn", game));
    }

    @Operation(summary = "Stand and let dealer play")
    @ApiResponse(responseCode = "200", description = "Player stands")
    @PostMapping("/{id}/stand")
    public Mono<ResponseEntity<MyApiResponse<Game>>> stand(
            @PathVariable String id,
            @Parameter(hidden = true) @AuthenticationPrincipal String userName
    ) {
        return gameService.playerStand(id, userName)
                .map(game -> ResponseBuilder.ok("Player stands", game));
    }
}
