package com.jusep1983.blackjack.player;
import com.jusep1983.blackjack.admin.AdminService;
import com.jusep1983.blackjack.player.dto.PlayerRankingDTO;
import com.jusep1983.blackjack.player.dto.PlayerWithGamesDTO;
import com.jusep1983.blackjack.player.dto.UpdateAliasDTO;
import com.jusep1983.blackjack.shared.enums.Role;
import com.jusep1983.blackjack.shared.response.MyApiResponse;
import com.jusep1983.blackjack.shared.response.ResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/player")
@SecurityRequirement(name = "bearerAuth")
public class PlayerController {
    private final PlayerService playerService;
    private final AdminService adminService;
    public PlayerController(PlayerService playerService,AdminService adminService) {
        this.playerService = playerService;
        this.adminService = adminService;
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
    @Operation(summary = "Get information of the authenticated player, including their games")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Player with games retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Player not found")
    })
    public Mono<ResponseEntity<MyApiResponse<PlayerWithGamesDTO>>> getCurrentPlayer() {
        return playerService.getCurrentPlayerWithGames()
                .map(dto -> ResponseBuilder.ok("Player with games loaded", dto));
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

//    @PreAuthorize("hasRole('SUPERUSER')")
//    @PatchMapping("/set-role/{playerId}")
//    public Mono<ResponseEntity<String>> updateRole(
//            @PathVariable String playerId,
//            @RequestParam Role newRole) {
//        return adminService.setRole(playerId, newRole)
//                .map(p -> ResponseEntity.ok("Rol de " + p.getUserName() + " actualizado a " + newRole));
//    }

//    @GetMapping("/me/games")
//    @Operation(summary = "Get current player's profile and game history")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Player profile and games retrieved successfully"),
//            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
//            @ApiResponse(responseCode = "404", description = "Player not found"),
//            @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    public Mono<ResponseEntity<MyApiResponse<PlayerWithGamesDTO>>> getMyGames() {
//        return playerService.getCurrentPlayerWithGames()
//                .map(playerWithGames -> ResponseBuilder.ok("Player profile and games retrieved", playerWithGames));
//    }

}


