package com.jusep1983.blackjack.admin;

import com.jusep1983.blackjack.shared.enums.Role;
import com.jusep1983.blackjack.shared.response.MyApiResponse;
import com.jusep1983.blackjack.shared.response.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @PreAuthorize("hasRole('SUPER_USER')")
    @PatchMapping("/set-role/{playerId}")
    public Mono<ResponseEntity<MyApiResponse<String>>> updateRole(
            @PathVariable String playerId,
            @RequestParam Role newRole) {
        return adminService.setRole(playerId, newRole)
                .map(p -> ResponseBuilder.ok("Role of player updated to " + newRole.name(),null));
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    @DeleteMapping("/delete-player/{playerId}")
    public Mono<ResponseEntity<MyApiResponse<String>>> deletePlayer(@PathVariable String playerId) {
        return adminService.deletePlayerAndGames(playerId)
                .thenReturn(ResponseBuilder.ok("Player and related games deleted successfully",null));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_USER')")
    @DeleteMapping("/delete-player/by-username/{userName}")
    public Mono<ResponseEntity<MyApiResponse<String>>> deletePlayerByUserName(@PathVariable String userName) {
        return adminService.deletePlayerAndGames(userName)
                .thenReturn(ResponseBuilder.ok("Player and related games deleted successfully",null));
    }
}

