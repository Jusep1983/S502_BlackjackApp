package com.jusep1983.blackjack.auth;

import com.jusep1983.blackjack.auth.dto.AuthRequest;
import com.jusep1983.blackjack.auth.dto.AuthResponse;
import com.jusep1983.blackjack.player.Player;
import com.jusep1983.blackjack.player.PlayerRepository;
import com.jusep1983.blackjack.shared.enums.Role;
import com.jusep1983.blackjack.shared.response.MyApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final PlayerRepository playerRepository;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "Register a new player (public)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Player registered and token returned"),
            @ApiResponse(responseCode = "409", description = "Username already exists"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/register")
    public Mono<ResponseEntity<MyApiResponse<AuthResponse>>> register(@Valid @RequestBody AuthRequest request) {
        return playerRepository.findByUserName(request.getUserName())
                .flatMap(existing -> {
                    log.warn("Registration attempt failed: username '{}' already exists", request.getUserName());
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.CONFLICT)
                            .body(new MyApiResponse<AuthResponse>(HttpStatus.CONFLICT.value(), "Username already exists", null)));
                })
                .switchIfEmpty(
                        Mono.defer(() -> {
                            Player newPlayer = new Player();
                            newPlayer.setUserName(request.getUserName());
                            newPlayer.setAlias(request.getUserName());
                            newPlayer.setPassword(passwordEncoder.encode(request.getPassword()));
                            newPlayer.setRole(Role.USER);
                            newPlayer.setCreatedAt(LocalDateTime.now());

                            log.info("Registering new player '{}'", newPlayer.getUserName());

                            return playerRepository.save(newPlayer)
                                    .map(saved -> {
                                        String token = authService.generateToken(
                                                saved.getUserName(), saved.getRole()
                                        );
                                        log.info("Player '{}' registered successfully", saved.getUserName());
                                        return ResponseEntity.status(HttpStatus.CREATED)
                                                .body(new MyApiResponse<>(HttpStatus.CREATED.value(), "Player created successfully", new AuthResponse(token)));
                                    });
                        })
                );
    }

    @Operation(summary = "Authenticate a player and return a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/login")
    public Mono<ResponseEntity<MyApiResponse<AuthResponse>>> login(@Valid @RequestBody AuthRequest request) {
        return playerRepository.findByUserName(request.getUserName())
                .filter(player -> passwordEncoder.matches(request.getPassword(), player.getPassword()))
                .map(validPlayer -> {
                    log.info("Login successful for user '{}'", validPlayer.getUserName());
                    String token = authService.generateToken(validPlayer.getUserName(), Role.valueOf(validPlayer.getRole().name()));
                    return ResponseEntity.ok(new MyApiResponse<>(200, "Login successful", new AuthResponse(token)));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Login failed: invalid credentials for user '{}'", request.getUserName());
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.UNAUTHORIZED)
                            .body(new MyApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Invalid credentials", null)));
                }));
    }

}
