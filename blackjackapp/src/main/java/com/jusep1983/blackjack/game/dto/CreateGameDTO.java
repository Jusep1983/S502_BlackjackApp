package com.jusep1983.blackjack.game.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateGameDTO {
    @NotBlank(message = "Player name is required")
    private String playerName;

    public CreateGameDTO() {
    }

    public CreateGameDTO(String playerName) {
        this.playerName = playerName;
    }

}
