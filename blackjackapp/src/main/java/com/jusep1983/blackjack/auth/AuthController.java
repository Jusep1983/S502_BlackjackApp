package com.jusep1983.blackjack.auth;

import com.jusep1983.blackjack.auth.dto.AuthRequest;
import com.jusep1983.blackjack.auth.dto.AuthResponse;
import com.jusep1983.blackjack.player.Player;
import com.jusep1983.blackjack.player.PlayerRepository;
import com.jusep1983.blackjack.shared.enums.Role;
import com.jusep1983.blackjack.shared.response.MyApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final PlayerRepository playerRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;


//    @PostMapping("/register")
//    public Mono<ResponseEntity<AuthResponse>> register(@Valid @RequestBody AuthRequest request) {
//        return playerRepository.findByUserName(request.getUserName())
//                .flatMap(existing ->
//                        Mono.<ResponseEntity<AuthResponse>>just(
//                                ResponseEntity.status(HttpStatus.CONFLICT).build()
//                        )
//                )
//                .switchIfEmpty(
//                        Mono.defer(() -> {
//                            Player newPlayer = new Player();
//                            newPlayer.setUserName(request.getUserName());
//                            newPlayer.setAlias(request.getUserName()); // por defecto
//                            newPlayer.setPassword(passwordEncoder.encode(request.getPassword()));
//                            newPlayer.setRole(Role.USER);
//                            newPlayer.setCreatedAt(LocalDateTime.now());
//
//                            return playerRepository.save(newPlayer)
//                                    .map(saved -> {
//                                        String token = jwtService.generateToken(saved.getUserName(), Role.valueOf(saved.getRole().name()));
//                                        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token));
//                                    });
//                        })
//                );
//    }
@PostMapping("/register")
public Mono<ResponseEntity<MyApiResponse<AuthResponse>>> register(@Valid @RequestBody AuthRequest request) {
    return playerRepository.findByUserName(request.getUserName())
            .flatMap(existing ->
                    Mono.just(ResponseEntity
                            .status(HttpStatus.CONFLICT)
                            .body(new MyApiResponse<AuthResponse>(HttpStatus.CONFLICT.value(), "Username already exists", null))) // Error con respuesta estandarizada
            )
            .switchIfEmpty(
                    Mono.defer(() -> {
                        Player newPlayer = new Player();
                        newPlayer.setUserName(request.getUserName());
                        newPlayer.setAlias(request.getUserName()); // por defecto
                        newPlayer.setPassword(passwordEncoder.encode(request.getPassword()));
                        newPlayer.setRole(Role.USER);
                        newPlayer.setCreatedAt(LocalDateTime.now());

                        return playerRepository.save(newPlayer)
                                .map(saved -> {
                                    String token = jwtService.generateToken(
                                            saved.getUserName(), saved.getRole()
                                    );
                                    return ResponseEntity.status(HttpStatus.CREATED)
                                            .body(new MyApiResponse<>(HttpStatus.CREATED.value(), "Player created successfully", new AuthResponse(token))); // Respuesta exitosa
                                });
                    })
            );
}

    @PostMapping("/login")
    public Mono<ResponseEntity<MyApiResponse<AuthResponse>>> login(@Valid @RequestBody AuthRequest request) {
        return playerRepository.findByUserName(request.getUserName())
                .filter(player -> passwordEncoder.matches(request.getPassword(), player.getPassword()))
                .map(validPlayer -> {
                    String token = jwtService.generateToken(validPlayer.getUserName(), Role.valueOf(validPlayer.getRole().name()));
                    return ResponseEntity.ok(new MyApiResponse<>(200, "Login successful", new AuthResponse(token)));
                })
                .switchIfEmpty(Mono.just(ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new MyApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Invalid credentials", null)))
                );
    }

}
