package ru.inf_fans.web_hockey.mapper;

import org.mapstruct.Mapper;
import ru.inf_fans.web_hockey.dto.TournamentApiDto;
import ru.inf_fans.web_hockey.entity.tournament.Tournament;

@Mapper(componentModel = "spring")
public interface TournamentToDtoMapper {
    TournamentApiDto toApiDto(Tournament tournament);
}
