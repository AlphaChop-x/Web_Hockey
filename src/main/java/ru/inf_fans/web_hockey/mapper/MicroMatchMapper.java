package ru.inf_fans.web_hockey.mapper;

import org.springframework.stereotype.Component;
import ru.inf_fans.web_hockey.dto.MicroMatchDto;
import ru.inf_fans.web_hockey.dto.MatchPlayerDto;
import ru.inf_fans.web_hockey.mapper.MatchPlayerMapper;
import ru.inf_fans.web_hockey.entity.tournament.MicroMatch;
import ru.inf_fans.web_hockey.entity.user.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MicroMatchMapper {

    private final MatchPlayerMapper matchPlayerMapper;

    public MicroMatchMapper(MatchPlayerMapper matchPlayerMapper) {
        this.matchPlayerMapper = matchPlayerMapper;
    }

    public MicroMatchDto toDto(MicroMatch microMatch) {
        MicroMatchDto dto = new MicroMatchDto();
        dto.id = Math.toIntExact(microMatch.getId());

        dto.firstTeam = toMatchPlayerDtoList(
                new ArrayList<>(microMatch.getFirstTeam().getPlayers())
        );

        dto.secondTeam = toMatchPlayerDtoList(
                new ArrayList<>(microMatch.getSecondTeam().getPlayers())
        );


        return dto;
    }

    private List<MatchPlayerDto> toMatchPlayerDtoList(List<UserEntity> players) {
        return players.stream()
                .map(matchPlayerMapper::toDto)
                .collect(Collectors.toList());
    }
}
