package ru.inf_fans.web_hockey.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.inf_fans.web_hockey.entity.tournament.Team;

public interface TeamRepository extends CrudRepository<Team, String> {
    void deleteByPlayersId(int playerId);
}
