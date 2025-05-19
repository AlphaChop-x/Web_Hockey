package ru.inf_fans.web_hockey.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.inf_fans.web_hockey.entity.MicroMatch;
import ru.inf_fans.web_hockey.entity.enums.MatchStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface MicroMatchRepository extends CrudRepository<MicroMatch, Long> {

    @Query("SELECT mm FROM MicroMatch mm WHERE mm.tournament.id = :tournamentId")
    List<MicroMatch> getMicroMatchesByTournament_Id(@Param("tournamentId") Long tournamentId);

    List<MicroMatch> findScheduledMatchesByTournament_Id(Long tournamentId);

    List<MicroMatch> findAllByStatus(MatchStatus matchStatus);

    @Query("SELECT m FROM MicroMatch m WHERE m.status = ru.inf_fans.web_hockey.entity.enums.MatchStatus.SCHEDULED " +
            "AND (:now BETWEEN m.startDate AND m.endDate OR :now > m.startDate)")
    List<MicroMatch> findMatchesToProcess(LocalDateTime now);
}
