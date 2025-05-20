package ru.inf_fans.web_hockey.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(description = "DTO для создания турнира")
@Getter
@Setter
public class TournamentRequestDto {
    @NotNull @Schema(description = "Название турнира", example = "Первый турнир Спартаковец")
    private String tournamentName;
    @NotNull @Schema(description = "Время начала турнира", example = "2025-01-01Т19:00:00")
    private LocalDateTime tournamentStartDate;
    @NotNull @Schema(description = "Время окончания турнира", example = "2025-01-01Т19:00:00")
    private LocalDateTime tournamentEndDate;
    @NotNull @Schema(description = "Место проведения турнира", example = "ДИВС Екатеринбург")
    private String location;
}
