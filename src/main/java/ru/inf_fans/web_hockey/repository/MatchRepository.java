package ru.inf_fans.web_hockey.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.inf_fans.web_hockey.entity.Match;
import ru.inf_fans.web_hockey.entity.enums.MatchStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface MatchRepository extends CrudRepository<Match, Long> {

    @Query("SELECT mm FROM Match mm WHERE mm.tournament.id = :tournamentId")
    List<Match> getMatchesByTournament_Id(@Param("tournamentId") Long tournamentId);

    List<Match> findScheduledMatchesByTournament_Id(Long tournamentId);

    List<Match> findAllByStatus(MatchStatus matchStatus);

    @Query("SELECT m FROM Match m WHERE m.status = ru.inf_fans.web_hockey.entity.enums.MatchStatus.SCHEDULED " +
            "OR m.status = ru.inf_fans.web_hockey.entity.enums.MatchStatus.IN_PROGRESS " +
            "AND (:now BETWEEN m.startDate AND m.endDate OR :now > m.startDate)")
    List<Match> findMatchesToProcess(LocalDateTime now);

    Match findFirstByStatusAndTournamentIdOrderByStartDate(MatchStatus matchStatus, Long tournamentId);
}
