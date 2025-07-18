package com.jusep1983.blackjack.player;

import org.junit.jupiter.api.Assertions;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PlayerControllerTest {

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private PlayerController playerController;

    @Test
    void givenValidPlayer_whenCreatePlayer_thenReturnsCreatedPlayer() {
        Player input = new Player(null, "Jose", 0, 0, 0, 0, null);
        Player saved = new Player(1L, "Jose", 0, 0, 0, 0, LocalDateTime.now());

        Mockito.when(playerService.createPlayer(input)).thenReturn(Mono.just(saved));

        StepVerifier.create(playerController.creatPlayer(input))
                .assertNext(response -> {
                    assertThat(response.getStatusCode().value()).isEqualTo(201);
                    assertThat(response.getBody()).isNotNull();
                    assertThat(response.getBody().getName()).isEqualTo("Jose");
                })
                .verifyComplete();
    }

    @Test
    void givenValidIdAndName_whenUpdatePlayerName_thenReturnsUpdatedPlayer() {
        Player updated = new Player(1L, "New", 0, 0, 0, 0, null);

        Mockito.when(playerService.updateName(1L, "New")).thenReturn(Mono.just(updated));

        StepVerifier.create(playerController.updatePlayerName(1L, updated))
                .assertNext(response -> {
                    assertThat(response.getStatusCodeValue()).isEqualTo(200);
                    Assertions.assertNotNull(response.getBody());
                    assertThat(response.getBody().getName()).isEqualTo("New");
                })
                .verifyComplete();
    }

    @Test
    void givenValidId_whenGetPlayerById_thenReturnsPlayer() {
        Player player = new Player(1L, "Jose", 0, 0, 0, 0, LocalDateTime.now());

        Mockito.when(playerService.getById(1L)).thenReturn(Mono.just(player));

        StepVerifier.create(playerController.getPlayerById(1L))
                .assertNext(response -> {
                    assertThat(response.getStatusCodeValue()).isEqualTo(200);
                    Assertions.assertNotNull(response.getBody());
                    assertThat(response.getBody().getName()).isEqualTo("Jose");
                })
                .verifyComplete();
    }

    @Test
    void givenRankingDataExists_whenGetRanking_thenReturnsListOfRankings() {
        PlayerRankingDTO p1 = new PlayerRankingDTO(1, new Player(1L, "Anne", 10, 7, 2, 1, null));
        PlayerRankingDTO p2 = new PlayerRankingDTO(2, new Player(2L, "Lois", 10, 6, 1, 3, null));

        Mockito.when(playerService.getRanking()).thenReturn(Flux.just(p1, p2));

        StepVerifier.create(playerController.getRanking())
                .assertNext(response -> {
                    List<PlayerRankingDTO> list = response.getBody();
                    assertThat(list).hasSize(2);
                    assertThat(list.get(0).getName()).isEqualTo("Anne");
                    assertThat(list.get(1).getName()).isEqualTo("Lois");
                })
                .verifyComplete();
    }
}