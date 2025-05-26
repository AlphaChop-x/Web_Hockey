package ru.inf_fans.web_hockey.service;

import org.apache.commons.math3.linear.*;
import org.springframework.stereotype.Service;
import ru.inf_fans.web_hockey.dto.MatchDto;
import ru.inf_fans.web_hockey.dto.MatchPlayerDto;

import java.util.*;

@Service
public class RatingCalculator {
    private static final float DEFAULT_RATING = 2200.0f;
    private static final float BALANCE_CONSTANT = 5500.0f;
    private static final float K_FACTOR = 32.0f;
    private static final float REGULARIZATION = 0.001f;

    public void processMatch(MatchDto match) {
        // Проверка на null и инициализация рейтингов
        initializeRatingsIfNull(match);

        // 1. Собираем всех игроков
        List<MatchPlayerDto> allPlayers = new ArrayList<>();
        allPlayers.addAll(match.firstTeam);
        allPlayers.addAll(match.secondTeam);

        // 2. Рассчитываем балансы
        Map<Long, Float> balances = calculateBalances(match);

        if (balances.isEmpty()) {
            return;
        }

        // 3. Строим и решаем систему уравнений
        updatePlayersRatings(allPlayers, balances, match);
    }

    private void initializeRatingsIfNull(MatchDto match) {
        for (MatchPlayerDto player : match.firstTeam) {
            if (player.rating == null) player.rating = DEFAULT_RATING;
        }
        for (MatchPlayerDto player : match.secondTeam) {
            if (player.rating == null) player.rating = DEFAULT_RATING;
        }
    }

    private Map<Long, Float> calculateBalances(MatchDto match) {
        Map<Long, Float> balances = new HashMap<>();

        if (match.teamAscore == match.teamBscore) {
            return balances;
        }

        boolean team1Won = match.teamAscore > match.teamBscore;

        // Для первой команды
        for (MatchPlayerDto player : match.firstTeam) {
            float balance = BALANCE_CONSTANT * (team1Won ? 1 : -1);
            balances.put(player.id, balance);
        }

        // Для второй команды
        for (MatchPlayerDto player : match.secondTeam) {
            float balance = BALANCE_CONSTANT * (team1Won ? -1 : 1);
            balances.put(player.id, balance);
        }

        return balances;
    }

    private void updatePlayersRatings(List<MatchPlayerDto> players,
                                      Map<Long, Float> balances, MatchDto match) {
        int n = players.size();
        RealMatrix coefficients = new Array2DRowRealMatrix(n + 1, n + 1);
        RealVector constants = new ArrayRealVector(n + 1);

        // Заполняем уравнения для игроков
        for (int i = 0; i < n; i++) {
            MatchPlayerDto player = players.get(i);

            // δ_I * Rt_I
            coefficients.setEntry(i, i, 1.0 + REGULARIZATION);

            // Σ δ_IJ * Rt_J
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    float delta = calculateDelta(players.get(i), players.get(j), match);
                    coefficients.addToEntry(i, j, delta);
                }
            }

            // Δ_I
            constants.setEntry(i, balances.get(player.id));
        }

        // Уравнение среднего рейтинга
        for (int j = 0; j < n; j++) {
            coefficients.setEntry(n, j, 1.0f / n);
        }
        constants.setEntry(n, calculateAverageRating(players));

        // Решение системы
        try {
            DecompositionSolver solver = new LUDecomposition(coefficients).getSolver();
            RealVector newRatings = solver.solve(constants);
            // Обновление рейтингов
            for (int i = 0; i < n; i++) {
                players.get(i).rating = (float) newRatings.getEntry(i);
            }
        } catch (SingularMatrixException e) {
            applyFallbackUpdate(players, balances); // Новый метод
        }
    }

    private float calculateDelta(MatchPlayerDto player1, MatchPlayerDto player2, MatchDto match) {
        // Добавлена проверка на null
        if (player1.rating == null || player2.rating == null) return 0.0f;

        boolean playedAgainst = (match.firstTeam.contains(player1) && (match.secondTeam.contains(player2)) ||
                (match.secondTeam.contains(player1) && (match.firstTeam.contains(player2))));

        if (!playedAgainst) return 0.0f;

        float ratingDiff = Math.abs(player1.rating - player2.rating);
        return 0.1f * (1 - Math.min(ratingDiff, 1000.0f) / 1000.0f); // Добавлен Math.min
    }

    private float calculateAverageRating(List<MatchPlayerDto> players) {
        if (players.isEmpty()) return DEFAULT_RATING;

        float sum = 0.0f;
        int count = 0;
        for (MatchPlayerDto player : players) {
            if (player.rating != null) {
                sum += player.rating;
                count++;
            }
        }
        return count > 0 ? sum / count : DEFAULT_RATING;
    }

    private void applyFallbackUpdate(List<MatchPlayerDto> players, Map<Long, Float> balances) {
        for (MatchPlayerDto player : players) {
            float change = balances.get(player.id) * 0.01f;
            player.rating += change;
        }
    }
}