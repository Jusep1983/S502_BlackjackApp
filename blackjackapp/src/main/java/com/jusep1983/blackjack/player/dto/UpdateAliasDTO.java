package com.jusep1983.blackjack.player.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateAliasDTO(
        @NotBlank(message = "Alias cannot be blank")
        @Size(min = 3, max = 25, message = "Alias must be between 3 and 25 characters")
        String alias
) {}