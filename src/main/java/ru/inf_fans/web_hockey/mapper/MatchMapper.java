package ru.inf_fans.web_hockey.mapper;

import org.springframework.stereotype.Component;
import ru.inf_fans.web_hockey.dto.MatchDto;
import ru.inf_fans.web_hockey.dto.MatchPlayerDto;
import ru.inf_fans.web_hockey.entity.Match;
import ru.inf_fans.web_hockey.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MatchMapper {

    private final MatchPlayerMapper matchPlayerMapper;

    public MatchMapper(MatchPlayerMapper matchPlayerMapper) {
        this.matchPlayerMapper = matchPlayerMapper;
    }

    public MatchDto toDto(Match match) {
        MatchDto dto = new MatchDto();
        dto.id = Math.toIntExact(match.getId());

        dto.firstTeam = toMatchPlayerDtoList(
                new ArrayList<>(match.getFirstTeam().getPlayers())
        );

        dto.secondTeam = toMatchPlayerDtoList(
                new ArrayList<>(match.getSecondTeam().getPlayers())
        );

        dto.startTime = match.getStartDate();
        dto.endTime = match.getEndDate();
        dto.teamAscore = match.getFirstTeamScore();
        dto.teamBscore = match.getSecondTeamScore();

        return dto;
    }

    private List<MatchPlayerDto> toMatchPlayerDtoList(List<User> players) {
        return players.stream()
                .map(matchPlayerMapper::toDto)
                .collect(Collectors.toList());
    }
}
