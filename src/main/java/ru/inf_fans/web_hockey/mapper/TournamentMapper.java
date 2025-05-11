package ru.inf_fans.web_hockey.mapper;

import org.springframework.stereotype.Component;
import ru.inf_fans.web_hockey.dto.TournamentApiDto;
import ru.inf_fans.web_hockey.entity.tournament.Tournament;

import java.time.LocalDate;

@Component
public class TournamentMapper {
    public TournamentApiDto toApiDto(Tournament tournament) {
        Long id = tournament.getId();
        String name = tournament.getName();
        String location = tournament.getLocation();
        LocalDate startDate = tournament.getStartDate();
        LocalDate endDate = tournament.getEndDate();

        return new TournamentApiDto(id, name, location, startDate, endDate);
    }

    public Tournament toTournament(TournamentApiDto tournamentDto) {

        Tournament tournament = new Tournament();

        tournament.setName(tournamentDto.name());
        tournament.setStartDate(tournamentDto.startDate());
        tournament.setEndDate(tournamentDto.endDate());
        tournament.setLocation(tournamentDto.location());

        return tournament;
    }
}
