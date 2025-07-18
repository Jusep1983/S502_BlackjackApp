package com.jusep1983.blackjack.player;

import com.jusep1983.blackjack.shared.enums.GameResult;
import com.jusep1983.blackjack.shared.exception.FieldEmptyException;
import com.jusep1983.blackjack.shared.exception.PlayerNotFoundException;
import com.jusep1983.blackjack.shared.exception.UsernameAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerServiceImpl playerService;

    @Test
    void givenPlayerExists_whenGetById_thenReturnsPlayer() {
        Player mockPlayer = new Player(1L, "Jose", 0, 0, 0, 0, LocalDateTime.now());
        Mockito.when(playerRepository.findById(1L)).thenReturn(Mono.just(mockPlayer));

        StepVerifier.create(playerService.getById(1L))
                .expectNextMatches(p -> p.getName().equals("Jose"))
                .verifyComplete();
    }

    @Test
    void givenValidPlayerData_whenCreatePlayer_thenPlayerIsCreated() {
        Player input = new Player(null, "  Manolito  ", 0, 0, 0, 0, null);
        Mockito.when(playerRepository.findByName("Manolito")).thenReturn(Mono.empty());
        Mockito.when(playerRepository.save(Mockito.any())).thenAnswer(inv -> {
            Player p = inv.getArgument(0);
            p.setId(1L);
            return Mono.just(p);
        });

        StepVerifier.create(playerService.createPlayer(input))
                .assertNext(player -> {
                    assert player.getName().equals("Manolito");
                    assert player.getGamesPlayed() == 0;
                    assert player.getCreatedAt() != null;
                })
                .verifyComplete();
    }

    @Test
    void givenEmptyPlayerName_whenCreatePlayer_thenThrowsException() {
        Player input = new Player();
        input.setName("   ");

        StepVerifier.create(playerService.createPlayer(input))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(FieldEmptyException.class);
                    assertThat(error.getMessage()).isEqualTo("The field cannot be empty or have only spaces.");
                });
        Mockito.verify(playerRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void givenExistingPlayerName_whenCreatePlayer_thenThrowsException() {
        Player input = new Player();
        input.setName("Pepe");

        Mockito.when(playerRepository.findByName("Pepe")).thenReturn(Mono.just(new Player()));

        StepVerifier.create(playerService.createPlayer(input))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(UsernameAlreadyExistsException.class);
                    assertThat(error.getMessage()).isEqualTo("There is already a player with name " + input.getName());
                });
        Mockito.verify(playerRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void givenPlayerExists_whenUpdateNameWithValidName_thenNameIsUpdated() {
        Player existing = new Player(1L, "Antiguo", 0, 0, 0, 0, null);
        Mockito.when(playerRepository.findById(1L)).thenReturn(Mono.just(existing));
        Mockito.when(playerRepository.save(Mockito.any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(playerService.updateName(1L, "NewName"))
                .assertNext(p -> assertEquals("NewName", p.getName()))
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
        Player player = new Player(null, "Player", 3, 1, 1, 1, null);

        Mockito.when(playerRepository.findByName("Player")).thenReturn(Mono.just(player));
        Mockito.when(playerRepository.save(Mockito.any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(playerService.updateStats("Player", GameResult.PLAYER_WIN))
                .assertNext(p -> {
                    assert p.getGamesPlayed() == 4;
                    assert p.getGamesWon() == 2;
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
        Player p1 = new Player(1L, "Alice", 10, 5, 2, 3, LocalDateTime.now());
        Player p2 = new Player(2L, "Bob", 8, 3, 1, 4, LocalDateTime.now());

        // Simulamos que el repositorio devuelve jugadores ordenados por gamesWon desc
        Mockito.when(playerRepository.findAllByOrderByGamesWonDesc())
                .thenReturn(Flux.just(p1, p2));

        StepVerifier.create(playerService.getRanking())
                .expectNextMatches(dto -> dto.getPosition() == 1 && dto.getName().equals("Alice"))
                .expectNextMatches(dto -> dto.getPosition() == 2 && dto.getName().equals("Bob"))
                .verifyComplete();
    }

}

