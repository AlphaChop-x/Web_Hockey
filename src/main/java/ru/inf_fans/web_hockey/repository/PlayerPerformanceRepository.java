package ru.inf_fans.web_hockey.repository;

import org.springframework.data.repository.CrudRepository;
import ru.inf_fans.web_hockey.entity.tournament.PlayerPerformance;

public interface PlayerPerformanceRepository extends CrudRepository<PlayerPerformance, Long> {
    /**
     * Удаляет запись перформанса игрока, по playerId (int)
     *
     * @param playerId - id игрока из таблицы UserEntity
     */
    void deleteByPlayerId(int playerId);
}
