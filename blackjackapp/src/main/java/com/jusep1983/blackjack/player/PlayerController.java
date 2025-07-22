package com.jusep1983.blackjack.player;

import com.jusep1983.blackjack.player.dto.CreatePlayerDTO;
import com.jusep1983.blackjack.player.dto.PlayerRankingDTO;
import com.jusep1983.blackjack.player.dto.UpdateAliasDTO;
import com.jusep1983.blackjack.shared.response.MyApiResponse;
import com.jusep1983.blackjack.shared.response.ResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

//@RestController
//@RequestMapping("/player")
//@SecurityRequirement(name = "bearerAuth")
//public class PlayerController {
//    private final PlayerService playerService;
//
//    public PlayerController(PlayerService playerService) {
//        this.playerService = playerService;
//    }
//
//    @Operation(summary = "Create a new player")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "201", description = "Player created successfully"),
//            @ApiResponse(responseCode = "400", description = "Invalid input"),
//            @ApiResponse(responseCode = "500", description = "Server error")
//    })
//    @PostMapping("/new")
//    @SecurityRequirement(name = "bearerAuth")
//    public Mono<ResponseEntity<MyApiResponse<Player>>> createPlayer(@Valid @RequestBody CreatePlayerDTO dto) {
//        return playerService.createPlayer(dto)
//                .map(player -> ResponseBuilder.created("Player created", player));
//    }
//
//    @Operation(summary = "Update player by id")
//    @ApiResponse(responseCode = "200", description = "Player updated")
//    @ApiResponse(responseCode = "404", description = "Player not found")
//    @PutMapping("/{id}/updateName")
//    @SecurityRequirement(name = "bearerAuth")
//    public Mono<ResponseEntity<Player>> updatePlayerName(
//            @PathVariable Long id,
//            @RequestBody Player player) {
//        return playerService.updateName(id, player.getUserName())
//                .map(updatedPlayer -> ResponseEntity.status(HttpStatus.OK)
//                        .body(updatedPlayer));
//
//    }
//
//    @Operation(summary = "Get player by id")
//    @ApiResponse(responseCode = "200", description = "Player found")
//    @ApiResponse(responseCode = "404", description = "Player not found")
//    @GetMapping("/{id}")
//    public Mono<ResponseEntity<Player>> getPlayerById(@PathVariable Long id) {
//        return playerService.getById(id)
//                .map(player -> ResponseEntity.status(HttpStatus.OK)
//                        .body(player));
//    }
//
//    @Operation(summary = "Get public ranking of all players")
//    @ApiResponse(responseCode = "200", description = "Ranking retrieved")
//    @ApiResponse(responseCode = "500", description = "Internal server error")
//    @GetMapping("/ranking")
//    public Mono<ResponseEntity<MyApiResponse<List<PlayerRankingDTO>>>> getRanking() {
//        return playerService.getRanking()
//                .collectList()
//                .map(ranking -> ResponseEntity.ok(
//                        new MyApiResponse<>(200, "Ranking loaded", ranking)
//                ));
//    }
//
//}
@RestController
@RequestMapping("/player")
@SecurityRequirement(name = "bearerAuth")
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("/new")
    @Operation(summary = "Create a new player")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Player created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    public Mono<ResponseEntity<MyApiResponse<Player>>> createPlayer(@Valid @RequestBody CreatePlayerDTO dto) {
        return playerService.createPlayer(dto)
                .map(player -> ResponseBuilder.created("Player created", player));
    }

    @PutMapping("/updateAlias")
    @Operation(summary = "Update alias of the current authenticated player")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alias updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid alias"),
            @ApiResponse(responseCode = "401", description = "Unauthorized – JWT missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Player not found")
    })
    public Mono<ResponseEntity<MyApiResponse<Player>>> updateAlias(@RequestBody @Valid UpdateAliasDTO dto) {
        return playerService.updateAlias(dto.alias())
                .map(updated -> ResponseBuilder.ok("Alias updated", updated));
    }

    @GetMapping("/me")
    @Operation(summary = "Get information of the authenticated player")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Player retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Player not found")
    })
    public Mono<ResponseEntity<MyApiResponse<Player>>> getCurrentPlayer() {
        return playerService.getCurrentPlayer()
                .map(player -> ResponseBuilder.ok("Player loaded", player));
    }

    @GetMapping("/ranking")
    @Operation(summary = "Get ranking of all players")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ranking retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<MyApiResponse<List<PlayerRankingDTO>>>> getRanking() {
        return playerService.getRanking()
                .collectList()
                .map(ranking -> ResponseBuilder.ok("Ranking loaded", ranking));
    }
}

