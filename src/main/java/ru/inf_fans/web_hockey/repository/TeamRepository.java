package ru.inf_fans.web_hockey.repository;

import org.springframework.data.repository.CrudRepository;
import ru.inf_fans.web_hockey.entity.Team;

public interface TeamRepository extends CrudRepository<Team, String> {
    void deleteByPlayersId(int playerId);
}
