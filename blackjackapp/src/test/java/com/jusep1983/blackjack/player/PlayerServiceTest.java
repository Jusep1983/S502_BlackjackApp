package com.jusep1983.blackjack.player;

import com.jusep1983.blackjack.player.dto.CreatePlayerDTO;
import com.jusep1983.blackjack.shared.enums.GameResult;
import com.jusep1983.blackjack.shared.enums.Role;
import com.jusep1983.blackjack.shared.exception.FieldEmptyException;
import com.jusep1983.blackjack.shared.exception.PlayerNotFoundException;
import com.jusep1983.blackjack.shared.exception.UsernameAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerServiceImpl playerService;

    private Player buildPlayer(Long id, String name) {
        return new Player(
                id,
                name,
                "alias_" + name,
                "password123",
                Role.USER,
                0, 0, 0, 0,
                LocalDateTime.now()
        );
    }

    @Test
    void givenPlayerExists_whenGetById_thenReturnsPlayer() {
        Player mockPlayer = buildPlayer(1L, "Jose");
        Mockito.when(playerRepository.findById(1L)).thenReturn(Mono.just(mockPlayer));

        StepVerifier.create(playerService.getById(1L))
                .expectNextMatches(p -> p.getUserName().equals("Jose"))
                .verifyComplete();
    }

    @Test
    void givenValidPlayerData_whenCreatePlayer_thenPlayerIsCreated() {
        CreatePlayerDTO dto = new CreatePlayerDTO();
        dto.setUserName("  Manolito  "); // con espacios
        dto.setPassword("1234");

        // Mock del encoder
        Mockito.when(passwordEncoder.encode("1234")).thenReturn("encrypted_1234");

        // Simular que no existe el player
        Mockito.when(playerRepository.findByUserName("Manolito")).thenReturn(Mono.empty());

        // Simular guardado correcto
        Mockito.when(playerRepository.save(Mockito.any())).thenAnswer(inv -> {
            Player p = inv.getArgument(0);
            p.setId(1L);
            return Mono.just(p);
        });

        StepVerifier.create(playerService.createPlayer(dto))
                .assertNext(player -> {
                    assertThat(player.getUserName()).isEqualTo("Manolito");
                    assertThat(player.getPassword()).isEqualTo("encrypted_1234"); // opcional
                    assertThat(player.getGamesPlayed()).isEqualTo(0);
                    assertThat(player.getCreatedAt()).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    void givenEmptyPlayerName_whenCreatePlayer_thenThrowsException() {
        CreatePlayerDTO dto = new CreatePlayerDTO();
        dto.setUserName("   ");
        dto.setPassword("1234");


        StepVerifier.create(playerService.createPlayer(dto))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(FieldEmptyException.class);
                    assertThat(error.getMessage()).isEqualTo("The field cannot be empty or have only spaces.");
                });
        Mockito.verify(playerRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void givenExistingPlayerName_whenCreatePlayer_thenThrowsException() {
        CreatePlayerDTO dto = new CreatePlayerDTO();
        dto.setUserName("Pepe"); // con espacios
        dto.setPassword("1234");

        Mockito.when(playerRepository.findByUserName("Pepe")).thenReturn(Mono.just(new Player()));

        StepVerifier.create(playerService.createPlayer(dto))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(UsernameAlreadyExistsException.class);
                    assertThat(error.getMessage()).isEqualTo("There is already a player with name " + dto.getUserName());
                });
        Mockito.verify(playerRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void givenPlayerExists_whenUpdateNameWithValidName_thenNameIsUpdated() {
        Player existing = buildPlayer(1L, "Antiguo");
        Mockito.when(playerRepository.findById(1L)).thenReturn(Mono.just(existing));
        Mockito.when(playerRepository.save(Mockito.any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(playerService.updateName(1L, "NewName"))
                .assertNext(p -> assertEquals("NewName", p.getUserName()))
                .verifyComplete();
    }

    @Test
    void givenPlayerDoesNotExist_whenUpdateName_thenThrowsException() {
        Mockito.when(playerRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(playerService.updateName(99L, "Name"))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(PlayerNotFoundException.class);
                    assertThat(error.getMessage()).isEqualTo("Player not found with id: 99");
                });
    }

    @Test
    void givenPlayerExists_whenUpdateStatsWithWin_thenStatsAreUpdated() {
        Player player = buildPlayer(null, "Player");
        player.setGamesPlayed(3);
        player.setGamesWon(1);
        player.setGamesLost(1);
        player.setGamesTied(1);

        Mockito.when(playerRepository.findByUserName("Player")).thenReturn(Mono.just(player));
        Mockito.when(playerRepository.save(Mockito.any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(playerService.updateStats("Player", GameResult.PLAYER_WIN))
                .assertNext(p -> {
                    assertThat(p.getGamesPlayed()).isEqualTo(4);
                    assertThat(p.getGamesWon()).isEqualTo(2);
                })
                .verifyComplete();
    }

    @Test
    void givenPlayerDoesNotExist_whenGetById_thenThrowsException() {
        Mockito.when(playerRepository.findById(55L)).thenReturn(Mono.empty());

        StepVerifier.create(playerService.getById(55L))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(PlayerNotFoundException.class);
                    assertThat(error.getMessage()).isEqualTo("Player not found with id: 55");
                })
                .verify();
    }

    @Test
    void givenPlayersExist_whenGetRanking_thenReturnsOrderedRanking() {
        Player p1 = buildPlayer(1L, "Alice");
        p1.setGamesPlayed(10);
        p1.setGamesWon(5);
        p1.setGamesLost(2);
        p1.setGamesTied(3);

        Player p2 = buildPlayer(2L, "Bob");
        p2.setGamesPlayed(8);
        p2.setGamesWon(3);
        p2.setGamesLost(1);
        p2.setGamesTied(4);

        Mockito.when(playerRepository.findAllByOrderByGamesWonDesc())
                .thenReturn(Flux.just(p1, p2));

        StepVerifier.create(playerService.getRanking())
                .expectNextMatches(dto -> dto.getPosition() == 1 && dto.getUserName().equals("Alice"))
                .expectNextMatches(dto -> dto.getPosition() == 2 && dto.getUserName().equals("Bob"))
                .verifyComplete();
    }
}

