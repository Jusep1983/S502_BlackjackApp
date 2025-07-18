package com.jusep1983.blackjack.player;

import com.jusep1983.blackjack.shared.enums.GameResult;
import com.jusep1983.blackjack.shared.exception.FieldEmptyException;
import com.jusep1983.blackjack.shared.exception.PlayerNotFoundException;
import com.jusep1983.blackjack.shared.exception.UsernameAlreadyExistsException;
import lombok.Data;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Data
@Service
public class PlayerServiceImpl implements PlayerService {
    private final PlayerRepository playerRepository;
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public Mono<Player> createPlayer(Player player) {
        String trimmedName = player.getName() != null ? player.getName().trim() : "";
        if (trimmedName.isEmpty()) {
            return Mono.error(new FieldEmptyException("The field cannot be empty or have only spaces."));
        }
        return playerRepository.findByName(trimmedName)
                .flatMap(existing -> Mono.error(new UsernameAlreadyExistsException("There is already a player with name " + trimmedName)))
                .switchIfEmpty(Mono.defer(() -> {
                    Player newPlayer = new Player();
                    newPlayer.setId(null);
                    newPlayer.setName(trimmedName);
                    newPlayer.setGamesPlayed(0);
                    newPlayer.setGamesWon(0);
                    newPlayer.setGamesTied(0);
                    newPlayer.setGamesLost(0);
                    newPlayer.setCreatedAt(LocalDateTime.now());
                    return Mono.just(newPlayer);
                }))
                .cast(Player.class)
                .flatMap(playerRepository::save); // Guardar el nuevo player
    }

    @Override
    public Mono<Player> updateName(Long id, String newName) {
        return playerRepository.findById(id)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Player not found with id: " + id)))
                .flatMap(player -> {
                    player.setName(newName.trim());
                    return playerRepository.save(player);
                });
    }

    @Override
    public Mono<Player> getById(long id) {
        return playerRepository.findById(id)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Player not found with id: " + id)));
    }

    @Override
    public Mono<Player> getByName(String name) {
        return playerRepository.findByName(name)
                .switchIfEmpty(Mono.empty());
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

}
