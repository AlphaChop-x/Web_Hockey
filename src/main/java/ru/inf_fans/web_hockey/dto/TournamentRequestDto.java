package ru.inf_fans.web_hockey.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TournamentRequestDto {
    private String tournamentName;
    private LocalDate tournamentStartDate;
    private LocalDate tournamentEndDate;
    private String location;
}
