package ru.inf_fans.web_hockey.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.inf_fans.web_hockey.dto.UserDto;
import ru.inf_fans.web_hockey.entity.tournament.Tournament;
import ru.inf_fans.web_hockey.entity.user.User;

import java.util.Collections;
import java.util.List;

@Repository
public interface TournamentRepository extends CrudRepository<Tournament, Long> {
    @Transactional
    @Modifying
    @Query(value =
            "INSERT INTO app_user_tournament (tournament_id, user_id) " +
                    "VALUES (:tournamentId, :playerId)", nativeQuery = true)
    void addPlayerToTournament(@Param("tournamentId") Long tournamentId,
                               @Param("playerId") int playerId);

    @Transactional
    Tournament findTournamentsById(Long tournamentId);

    @Query("SELECT u FROM Tournament t JOIN t.players u WHERE t.id = :tournamentId")
    List<User> findPlayersById(Long tournamentId);

    @Modifying
    @Query("DELETE FROM Tournament t WHERE t.id = :tournamentId")
    void deleteTournamentById(Long tournamentId);

    @Query("SELECT t FROM Tournament t")
    List<Tournament> findAllTournaments();

    @Query("SELECT t.id FROM Tournament t WHERE t.name = :tournamentName")
    String findTournament_IdByName(String tournamentName);

    @Query("SELECT u FROM Tournament t JOIN t.players u WHERE t.id = :tournamentId AND u.id = :userId")
    User findUserByTournament_IdAndUserId(Long tournamentId, int userId);
}
