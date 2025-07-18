package com.jusep1983.blackjack.player;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/player")
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new player", description = "Creates a new player with initial stats")
    @ApiResponse(responseCode = "201", description = "Player created successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Player.class),
                    examples = @ExampleObject(value = """
                            {
                              "id": 1,
                              "name": "Jose",
                              "gamesPlayed": 10,
                              "gamesWon": 5,
                              "createdAt": "2025-07-05T03:26:09.863Z"
                            }
                            """)))
    @ApiResponse(responseCode = "400", description = "Invalid player data", content = @Content)
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    public Mono<ResponseEntity<Player>> creatPlayer(@RequestBody Player player) {
        return playerService.createPlayer(player)
                .map(savedPlayer -> ResponseEntity.status(HttpStatus.CREATED).body(savedPlayer));
    }

    @Operation(summary = "Update player by id")
    @ApiResponse(responseCode = "200", description = "Player updated")
    @ApiResponse(responseCode = "404", description = "Player not found")
    @PutMapping("/{id}/updateName")
    public Mono<ResponseEntity<Player>> updatePlayerName(
            @PathVariable Long id,
            @RequestBody Player player) {
        return playerService.updateName(id, player.getName())
                .map(updatedPlayer -> ResponseEntity.status(HttpStatus.OK)
                        .body(updatedPlayer));

    }

    @Operation(summary = "Get player by id")
    @ApiResponse(responseCode = "200", description = "Player found")
    @ApiResponse(responseCode = "404", description = "Player not found")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Player>> getPlayerById(@PathVariable Long id) {
        return playerService.getById(id)
                .map(player -> ResponseEntity.status(HttpStatus.OK)
                        .body(player));
    }

    @GetMapping("/ranking")
    public Mono<ResponseEntity<List<PlayerRankingDTO>>> getRanking() {
        return playerService.getRanking()
                .collectList()
                .map(ResponseEntity::ok);
    }

}
