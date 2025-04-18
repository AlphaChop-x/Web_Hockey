package ru.inf_fans.web_hockey.service.tournament;

import org.springframework.stereotype.Service;
import ru.inf_fans.web_hockey.entity.tournament.MicroMatch;

import java.util.List;

@Service
public interface TournamentService {
    List<MicroMatch> generateMicroMatches(Long tournamentId);
}
