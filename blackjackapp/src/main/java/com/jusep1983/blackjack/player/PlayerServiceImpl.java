package com.jusep1983.blackjack.player;

import com.jusep1983.blackjack.game.GameRepository;
import com.jusep1983.blackjack.player.dto.CreatePlayerDTO;
import com.jusep1983.blackjack.player.dto.GameSummaryDTO;
import com.jusep1983.blackjack.player.dto.PlayerRankingDTO;
import com.jusep1983.blackjack.player.dto.PlayerWithGamesDTO;
import com.jusep1983.blackjack.shared.enums.GameResult;
import com.jusep1983.blackjack.shared.enums.Role;
import com.jusep1983.blackjack.shared.exception.FieldEmptyException;
import com.jusep1983.blackjack.shared.exception.PlayerNotFoundException;
import com.jusep1983.blackjack.shared.exception.UsernameAlreadyExistsException;
import com.jusep1983.blackjack.shared.utils.AuthUtils;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Data
@Service
public class PlayerServiceImpl implements PlayerService {
    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;
    private final GameRepository gameRepository;

    public PlayerServiceImpl(PlayerRepository playerRepository, PasswordEncoder passwordEncoder,GameRepository gameRepository) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
        this.gameRepository = gameRepository;
    }

    @Override
    public Mono<Player> createPlayer(CreatePlayerDTO dto) {
        String trimmedName = dto.getUserName() != null ? dto.getUserName().trim() : "";

        if (trimmedName.isEmpty()) {
            return Mono.error(new FieldEmptyException("Username cannot be empty or only spaces"));
        }
        return playerRepository.findByUserName(trimmedName)
                .flatMap(existing -> Mono.<Player>error(
                        new UsernameAlreadyExistsException("Username already taken: " + trimmedName)
                ))
                .switchIfEmpty(createAndSaveNewPlayer(dto, trimmedName));
    }

    private Mono<Player> createAndSaveNewPlayer(CreatePlayerDTO dto, String trimmedName) {
        Player newPlayer = new Player();
        newPlayer.setUserName(trimmedName);
        newPlayer.setAlias(trimmedName);
        newPlayer.setPassword(passwordEncoder.encode(dto.getPassword()));
        newPlayer.setRole(Role.USER);
        newPlayer.setGamesPlayed(0);
        newPlayer.setGamesWon(0);
        newPlayer.setGamesTied(0);
        newPlayer.setGamesLost(0);
        newPlayer.setCreatedAt(LocalDateTime.now());
        return playerRepository.save(newPlayer);
    }

    //    @Override
//    public Mono<Player> updateName(Long id, String newName) {
//        return playerRepository.findById(id)
//                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Player not found with id: " + id)))
//                .flatMap(player -> {
//                    player.setUserName(newName.trim());
//                    return playerRepository.save(player);
//                });
//    }
    @Override
    public Mono<Player> updateAlias(String newAlias) {
        if (newAlias == null || newAlias.trim().isEmpty()) {
            return Mono.error(new FieldEmptyException("Alias cannot be empty"));
        }

        String trimmedAlias = newAlias.trim();

        return AuthUtils.getCurrentUserName()
                .flatMap(userName ->
                        playerRepository.findByUserName(userName)
                                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Player not found: " + userName)))
                                .flatMap(player -> {
                                    player.setAlias(trimmedAlias);
                                    return playerRepository.save(player);
                                })
                );
    }

    @Override
    public Mono<Player> getById(long id) {
        return playerRepository.findById(id)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Player not found with id: " + id)));
    }

    @Override
    public Mono<Player> getByName(String name) {
        return playerRepository.findByUserName(name)
                .switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<Player> getCurrentPlayer() {
        return AuthUtils.getCurrentUserName()
                .flatMap(userName -> playerRepository.findByUserName(userName)
                        .switchIfEmpty(Mono.error(new PlayerNotFoundException("Player not found: " + userName))));
    }

    @Override
    public Mono<Player> updateStats(String playerName, GameResult result) {
        return getByName(playerName)
                .flatMap(player -> {
                    player.setGamesPlayed(player.getGamesPlayed() + 1);
                    updatePlayerStats(player, result);
                    return playerRepository.save(player);
                });
    }

    private void updatePlayerStats(Player player, GameResult result) {
        switch (result) {
            case PLAYER_WIN -> player.setGamesWon(player.getGamesWon() + 1);
            case DEALER_WIN -> player.setGamesLost(player.getGamesLost() + 1);
            case TIE -> player.setGamesTied(player.getGamesTied() + 1);
            default -> throw new IllegalStateException("Unexpected value: " + result);
        }
    }

    @Override
    public Flux<PlayerRankingDTO> getRanking() {
        return playerRepository.findAllByOrderByGamesWonDesc()
                .index() // añade índice (posición 0-based)
                .map(tuple -> new PlayerRankingDTO(
                        tuple.getT1().intValue() + 1, tuple.getT2()
                ));
    }

    @Override
    public Mono<PlayerWithGamesDTO> getCurrentPlayerWithGames() {
        return getCurrentPlayer()
                .flatMap(player ->
                        gameRepository.findAllByUserNameOrderByCreatedAtAsc(player.getUserName())
                                .index()
                                .map(tuple -> new GameSummaryDTO(
                                        tuple.getT1().intValue() + 1,     // número de partida (1-based)
                                        tuple.getT2().getId(),
                                        tuple.getT2().getGameStatus(),
                                        tuple.getT2().getGameResult(),
                                        tuple.getT2().getCreatedAt()
                                ))
                                .collectList()
                                .map(gameSummaries -> new PlayerWithGamesDTO(
                                        player.getUserName(),
                                        player.getAlias(),
                                        player.getGamesPlayed(),
                                        player.getGamesWon(),
                                        player.getGamesLost(),
                                        player.getGamesTied(),
                                        gameSummaries
                                ))
                );
    }


}
