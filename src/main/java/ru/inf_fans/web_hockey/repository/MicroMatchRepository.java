package ru.inf_fans.web_hockey.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.inf_fans.web_hockey.entity.MicroMatch;

import java.util.List;

public interface MicroMatchRepository extends CrudRepository<MicroMatch, Long> {

    @Query("SELECT mm FROM MicroMatch mm WHERE mm.tournament.id = :tournamentId")
    List<MicroMatch> getMicroMatchesByTournament_Id(@Param("tournamentId") Long tournamentId);
}
