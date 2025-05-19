package ru.inf_fans.web_hockey.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TournamentRequestDto {
    @NotNull
    private String tournamentName;
    @NotNull
    private LocalDate tournamentStartDate;
    @NotNull
    private LocalDate tournamentEndDate;
    @NotNull
    private String location;
}
