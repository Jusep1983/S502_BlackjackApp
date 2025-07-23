package com.jusep1983.blackjack.player;

import com.jusep1983.blackjack.shared.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("players")
public class Player {
    @Id
    @Schema(description = "Unique identifier of the player", example = "1")
    @Column("id")
    private Long id;

    @NotBlank
    @Size(min = 3, max = 20)
    @Column("user_name")
    @Schema(description = "Name of the player", example = "Jose")
    private String userName;

    @NotBlank
    @Size(min = 3, max = 30)
    @Column("alias")
    @Schema(description = "Public alias of the player, editable", example = "JusepTheGreat")
    private String alias;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column("password")
    @Schema(description = "Password of the player", example = "secret123")
    private String password;

    @NotNull
    @Column("role")
    @Schema(description = "Role of the player (USER or ADMIN)", example = "USER")
    private Role role;

    @Min(0)
    @Column("games_played")
    @Schema(description = "Number of games the player has played", example = "10")
    private int gamesPlayed;

    @Min(0)
    @Column("games_won")
    @Schema(description = "Number of games the player has won", example = "6")
    private int gamesWon;

    @Min(0)
    @Column("games_lost")
    @Schema(description = "Number of games the player has loss", example = "6")
    private int gamesLost;

    @Min(0)
    @Column("games_tied")
    @Schema(description = "Number of games tied", example = "2")
    private int gamesTied;

    @PastOrPresent
    @Column("created_at")
    @Schema(description = "Date and time when the player was created", example = "2025-07-05T14:30:00")
    private LocalDateTime createdAt;
}
