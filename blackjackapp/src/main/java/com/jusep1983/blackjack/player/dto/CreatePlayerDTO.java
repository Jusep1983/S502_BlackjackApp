package com.jusep1983.blackjack.player.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePlayerDTO {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be 3-20 characters long")
    private String userName;

    @NotBlank(message = "Password is required")
    @Size(min = 4, max = 20, message = "Password must be 4-20 characters long")
    private String password;

}
