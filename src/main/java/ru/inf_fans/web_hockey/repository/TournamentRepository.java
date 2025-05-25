package ru.inf_fans.web_hockey.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.inf_fans.web_hockey.dto.MatchPlayerDto;
import ru.inf_fans.web_hockey.dto.TournamentApiDto;
import ru.inf_fans.web_hockey.entity.Tournament;
import ru.inf_fans.web_hockey.entity.User;

import java.util.List;

@Repository
public interface TournamentRepository extends CrudRepository<Tournament, Long> {
    @Transactional
    @Modifying
    @Query(value =
            "INSERT INTO app_user_tournament (tournament_id, user_id) " +
                    "VALUES (:tournamentId, :playerId)", nativeQuery = true)
    void addPlayerToTournament(@Param("tournamentId") Long tournamentId,
                               @Param("playerId") Long playerId);

    @Transactional
    Tournament findTournamentsById(Long tournamentId);

    @Transactional
    @Query("SELECT new ru.inf_fans.web_hockey.dto.TournamentApiDto(t.id, t.name, t.location, t.startDate, t.endDate) " +
            "FROM Tournament t WHERE  t.id = :tournamentId")
    TournamentApiDto findTournamentDtoById(@Param("tournamentId") Long tournamentId);

    @Query("SELECT u FROM Tournament t JOIN t.players u WHERE t.id = :tournamentId")
    List<User> findPlayersById(Long tournamentId);

    @Query("SELECT new ru.inf_fans.web_hockey.dto.MatchPlayerDto(u.id,u.name, u.surname, u.email, u.born, u.rating) " +
            "FROM Tournament t JOIN t.players u WHERE t.id = :tournamentId")
    List<MatchPlayerDto> findPlayersDtoById(@Param("tournamentId") Long tournamentId);

    @Modifying
    @Query("DELETE FROM Tournament t WHERE t.id = :tournamentId")
    void deleteTournamentById(Long tournamentId);

    @Query("SELECT t FROM Tournament t")
    List<Tournament> findAllTournaments();

    @Query("SELECT t.id FROM Tournament t WHERE t.name = :tournamentName")
    String findTournament_IdByName(String tournamentName);

    @Query("SELECT u FROM Tournament t JOIN t.players u WHERE t.id = :tournamentId AND u.id = :userEntityId")
    User findUserByTournament_IdAndUserId(Long tournamentId, Long userEntityId);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM Tournament t JOIN t.players p " +
            "WHERE t.id = :tournamentId AND p.id = :userId")
    boolean isUserRegistered(
            @Param("tournamentId") Long tournamentId,
            @Param("userId") Long userId);

}
