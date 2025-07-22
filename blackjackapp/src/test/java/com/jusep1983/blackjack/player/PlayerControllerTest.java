package com.jusep1983.blackjack.player;

import com.jusep1983.blackjack.player.dto.CreatePlayerDTO;
import com.jusep1983.blackjack.player.dto.PlayerRankingDTO;
import com.jusep1983.blackjack.shared.enums.Role;
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
//
//    @Mock
//    private PlayerService playerService;
//
//    @InjectMocks
//    private PlayerController playerController;
//
//    private Player buildPlayer(Long id, String userName) {
//        return new Player(
//                id,
//                userName,
//                "alias_" + userName,
//                "password123",
//                Role.USER,
//                0, 0, 0, 0,
//                LocalDateTime.now()
//        );
//    }
//
//    private CreatePlayerDTO buildDto() {
//        CreatePlayerDTO dto = new CreatePlayerDTO();
//        dto.setUserName("Jose");
//        dto.setPassword("password123");
//        return dto;
//    }
//
//    @Test
//    void givenValidPlayer_whenCreatePlayer_thenReturnsCreatedPlayer() {
//        //Player input = buildPlayer(null, "Jose");
//        CreatePlayerDTO dto = buildDto();
//        Player saved = buildPlayer(1L, "Jose");
//
//        Mockito.when(playerService.createPlayer(dto)).thenReturn(Mono.just(saved));
//
//        StepVerifier.create(playerController.createPlayer(dto))
//                .assertNext(response -> {
//                    assertThat(response.getStatusCode().value()).isEqualTo(201);
//                    assertThat(response.getBody()).isNotNull();
//                    assertThat(response.getBody().getData()).isNotNull();
//                    assertThat(response.getBody().getData().getUserName()).isEqualTo("Jose");
//                    assertThat(response.getBody().getMessage()).isEqualTo("Player created");
//                })
//                .verifyComplete();
//    }
//
//    @Test
//    void givenValidIdAndName_whenUpdatePlayerName_thenReturnsUpdatedPlayer() {
//        Player updated = buildPlayer(1L, "New");
//
//        Mockito.when(playerService.updateName(1L, "New")).thenReturn(Mono.just(updated));
//
//        StepVerifier.create(playerController.updatePlayerName(1L, updated))
//                .assertNext(response -> {
//                    assertThat(response.getStatusCodeValue()).isEqualTo(200);
//                    Assertions.assertNotNull(response.getBody());
//                    assertThat(response.getBody().getUserName()).isEqualTo("New");
//                })
//                .verifyComplete();
//    }
//
//    @Test
//    void givenValidId_whenGetPlayerById_thenReturnsPlayer() {
//        Player player = buildPlayer(1L, "Jose");
//
//        Mockito.when(playerService.getById(1L)).thenReturn(Mono.just(player));
//
//        StepVerifier.create(playerController.getPlayerById(1L))
//                .assertNext(response -> {
//                    assertThat(response.getStatusCodeValue()).isEqualTo(200);
//                    Assertions.assertNotNull(response.getBody());
//                    assertThat(response.getBody().getUserName()).isEqualTo("Jose");
//                })
//                .verifyComplete();
//    }
//
//    @Test
//    void givenRankingDataExists_whenGetRanking_thenReturnsListOfRankings() {
//        PlayerRankingDTO p1 = new PlayerRankingDTO(1, buildPlayer(1L, "Anne"));
//        PlayerRankingDTO p2 = new PlayerRankingDTO(2, buildPlayer(2L, "Lois"));
//
//        Mockito.when(playerService.getRanking()).thenReturn(Flux.just(p1, p2));
//
//        StepVerifier.create(playerController.getRanking())
//                .assertNext(response -> {
//                    Assertions.assertNotNull(response.getBody());
//                    List<PlayerRankingDTO> list = response.getBody().getData();
//                    assertThat(list).hasSize(2);
//                    assertThat(list.get(0).getUserName()).isEqualTo("Anne");
//                    assertThat(list.get(1).getUserName()).isEqualTo("Lois");
//                })
//                .verifyComplete();
//    }
}