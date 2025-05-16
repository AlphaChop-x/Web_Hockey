package ru.inf_fans.web_hockey.dto;

import lombok.Getter;
import lombok.Setter;
import ru.inf_fans.web_hockey.entity.User;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class TournamentResponseDto {
    private String tournamentName;
    private LocalDate tournamentStartDate;
    private LocalDate tournamentEndDate;
    private String location;
    private List<UserApiDto> participants;
}
