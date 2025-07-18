package com.jusep1983.blackjack.game;

import com.jusep1983.blackjack.game.dto.CreateGameDTO;

import com.jusep1983.blackjack.shared.response.MyApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @Operation(summary = "Create a new Blackjack game")
    @ApiResponse(responseCode = "201", description = "Game created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "DTO with the player name to start the game",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CreateGameDTO.class),
                    examples = @ExampleObject(
                            value = """
            {
              "playerName": "Pasqual"
            }
            """
                    )
            )
    )
    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<MyApiResponse<Game>>> createGame(
            @Parameter(description = "DTO with the player name to start the game", required = true, example = "Pasqual")
            @Valid @RequestBody CreateGameDTO dto
    ) {
        return gameService.createGame(dto.getPlayerName())
                .map(game -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(new MyApiResponse<>(HttpStatus.CREATED.value(), "Game created", game))
                );
    }

    @Operation(summary = "Get game details by ID")
    @ApiResponse(responseCode = "200", description = "Game found")
    @ApiResponse(responseCode = "404", description = "Game not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MyApiResponse<Game>>> getGameById(
            @Parameter(description = "ID of the game to retrieve", required = true, example = "686643d6e250756520d26adb")
            @PathVariable String id
    ) {
        return gameService.getGameById(id)
                .map(game -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(new MyApiResponse<>(HttpStatus.OK.value(), "Game " + id + " found", game))
                );
    }

    @Operation(summary = "Delete game by ID")
    @ApiResponse(responseCode = "204", description = "Game deleted successfully")
    @ApiResponse(responseCode = "404", description = "Game not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @DeleteMapping("/{id}/delete")
    public Mono<ResponseEntity<Void>> deleteById(
            @Parameter(description = "ID of the game to delete", required = true, example = "1234")
            @PathVariable String id
    ) {
        return gameService.deleteGameById(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @PostMapping("/{id}/hit")
    public Mono<ResponseEntity<MyApiResponse<Game>>> hit(@PathVariable String id) {
        return gameService.playerHit(id)
                .map(game -> ResponseEntity.ok(new MyApiResponse<>(200, "Card drawn", game)));
    }

    @PostMapping("/{id}/stand")
    public Mono<ResponseEntity<MyApiResponse<Game>>> stand(@PathVariable String id) {
        return gameService.playerStand(id)
                .map(game -> ResponseEntity.ok(new MyApiResponse<>(200, "Player stands. Dealer's turn.", game)));
    }

}
