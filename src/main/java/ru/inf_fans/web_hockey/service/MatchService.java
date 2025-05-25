package ru.inf_fans.web_hockey.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.inf_fans.web_hockey.controller.controllerAdvice.NotFoundMatchException;
import ru.inf_fans.web_hockey.dto.MatchDto;
import ru.inf_fans.web_hockey.dto.MatchPlayerDto;
import ru.inf_fans.web_hockey.dto.MatchResultDto;
import ru.inf_fans.web_hockey.dto.TournamentApiDto;
import ru.inf_fans.web_hockey.entity.Match;
import ru.inf_fans.web_hockey.entity.Team;
import ru.inf_fans.web_hockey.entity.Tournament;
import ru.inf_fans.web_hockey.entity.enums.MatchStatus;
import ru.inf_fans.web_hockey.entity.User;
import ru.inf_fans.web_hockey.mapper.MatchPlayerMapper;
import ru.inf_fans.web_hockey.mapper.MatchMapper;
import ru.inf_fans.web_hockey.repository.MatchRepository;
import ru.inf_fans.web_hockey.repository.TeamRepository;
import ru.inf_fans.web_hockey.repository.UserRepository;
import ru.inf_fans.web_hockey.service.tournament.TournamentServiceImpl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MatchService {

    private final TournamentServiceImpl tournamentService;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final MatchPlayerMapper matchPlayerMapper;
    private final MatchMapper matchMapper;
    private final MatchStatusService matchStatusService;
    int teamSize = 4;

    // Класс для хранения пар игроков (неупорядоченных)
    private static class PlayerPair {
        final MatchPlayerDto player1;
        final MatchPlayerDto player2;

        PlayerPair(MatchPlayerDto p1, MatchPlayerDto p2) {
            // Всегда сохраняем пару в одном порядке (для избежания дубликатов)
            this.player1 = p1.getId() < p2.getId() ? p1 : p2;
            this.player2 = p1.getId() < p2.getId() ? p2 : p1;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PlayerPair that = (PlayerPair) o;
            return Objects.equals(player1, that.player1) && Objects.equals(player2, that.player2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(player1, player2);
        }
    }

    // Мапа для хранения количества совместных матчей
    private final Map<PlayerPair, Integer> playerPairMatches = new HashMap<>();

    public List<MatchDto> generateMatches(TournamentApiDto tournament) {
        List<Match> matches = new ArrayList<>();
        List<MatchPlayerDto> allPlayers = tournamentService.findAllTournamentPlayersEntity(tournament.getId());
        Tournament tournamentEntity = tournamentService.findTournamentById(tournament.getId());

        // Инициализация счетчика матчей для каждого игрока
        Map<MatchPlayerDto, Integer> playerMatchCounts = new HashMap<>();
        allPlayers.forEach(player -> playerMatchCounts.put(player, 0));

        // Настройка времени матчей
        LocalDateTime currentMatchTime = tournamentEntity.getStartDate();
        Duration matchDuration = Duration.ofMinutes(1);
        Duration breakDuration = Duration.ofMillis(90_000);

        boolean hasEnoughPlayers;
        do {
            // Фильтрация игроков, которые еще не сыграли 10 матчей, и сортировка
            List<MatchPlayerDto> candidates = allPlayers.stream()
                    .filter(player -> playerMatchCounts.getOrDefault(player, 0) < 10)
                    .sorted(Comparator.comparingDouble(MatchPlayerDto::getRating))
                    .collect(Collectors.toList());

            if (candidates.size() < 2 * teamSize) {
                hasEnoughPlayers = false;
            } else {
                List<MatchPlayerDto> selected = candidates.subList(0, 2 * teamSize);

                // Балансировка команд
                List<Team> teams = balanceTeams(selected);

                // Сохранение команд
                teams.forEach(team -> {
                    team.setTournament(tournamentEntity);
                    teamRepository.save(team);
                });

                // Создание матча
                Match match = new Match(tournamentEntity, teams.get(0), teams.get(1));
                match.setStatus(MatchStatus.SCHEDULED);
                match.setStartDate(currentMatchTime);
                match.setEndDate(currentMatchTime.plus(matchDuration));
                matchRepository.save(match);
                matchStatusService.scheduleMatch(match);
                matches.add(match);

                // Обновление счетчиков
                selected.forEach(player ->
                        playerMatchCounts.put(player, playerMatchCounts.get(player) + 1));

                // Обновление времени следующего матча
                currentMatchTime = currentMatchTime.plus(matchDuration).plus(breakDuration);
                hasEnoughPlayers = true;

                // В цикле генерации матчей:
                List<MatchPlayerDto> team1Players = teams.get(0).getPlayers().stream().map(matchPlayerMapper::toDto).collect(Collectors.toList());
                List<MatchPlayerDto> team2Players = teams.get(1).getPlayers().stream().map(matchPlayerMapper::toDto).collect(Collectors.toList());

// Обновляем счётчик пар внутри команд
                updatePlayerPairs(team1Players);
                updatePlayerPairs(team2Players);
            }
        } while (hasEnoughPlayers);

        return matches.stream()
                .map(matchMapper::toDto)
                .collect(Collectors.toList());
    }

    // Метод для обновления:
    private void updatePlayerPairs(List<MatchPlayerDto> team) {
        for (int i = 0; i < team.size(); i++) {
            for (int j = i + 1; j < team.size(); j++) {
                PlayerPair pair = new PlayerPair(team.get(i), team.get(j));
                playerPairMatches.merge(pair, 1, Integer::sum);
            }
        }
    }

    // Балансировка команд (жадный алгоритм)
    private List<Team> balanceTeams(List<MatchPlayerDto> players) {
        // 1. Сортируем игроков по рейтингу (для баланса силы)
        players.sort(Comparator.comparingDouble(MatchPlayerDto::getRating).reversed());

        // 2. Разделяем на две команды, минимизируя повторы
        List<MatchPlayerDto> team1 = new ArrayList<>();
        List<MatchPlayerDto> team2 = new ArrayList<>();

        for (MatchPlayerDto player : players) {
            if (shouldAddToTeam1(player, team1, team2)) {
                team1.add(player);
            } else {
                team2.add(player);
            }
        }
        Set<User> teamA = new HashSet<>();

        for (MatchPlayerDto player : team1) {
            teamA.add(userRepository.findUserById(player.id));
        }

        Set<User> teamB = new HashSet<>();

        for (MatchPlayerDto player : team2) {
            teamB.add(userRepository.findUserById(player.id));
        }

        // 3. Создаём объекты команд
        return List.of(
                new Team(teamA),
                new Team(teamB)
        );
    }

    private boolean shouldAddToTeam1(MatchPlayerDto player, List<MatchPlayerDto> team1, List<MatchPlayerDto> team2) {
        // Если команды разного размера, добавляем в меньшую
        if (team1.size() < team2.size()) return true;
        if (team1.size() > team2.size()) return false;

        // Считаем "стоимость" добавления в каждую команду
        int costTeam1 = calculatePairCost(player, team1);
        int costTeam2 = calculatePairCost(player, team2);

        // Выбираем команду с минимальной "стоимостью" повторных пар
        return costTeam1 <= costTeam2;
    }

    private int calculatePairCost(MatchPlayerDto player, List<MatchPlayerDto> team) {
        return team.stream()
                .mapToInt(teammate -> playerPairMatches.getOrDefault(
                        new PlayerPair(player, teammate), 1))
                .sum();
    }

    public List<MatchDto> getMatchDtos(
            Long tournamentId
    ) {
        List<Match> matches = matchRepository.getMatchesByTournament_Id(tournamentId);
        List<MatchDto> dtos = new ArrayList<>();
        for (Match match : matches) {
            dtos.add(matchMapper.toDto(match));
        }
        return dtos;
    }

    public MatchDto getCurrentMatch(Long tournamentId) {
        Match match = matchRepository.findFirstByStatusAndTournamentIdOrderByStartDate(MatchStatus.IN_PROGRESS, tournamentId);
        if (match != null) {
            return matchMapper.toDto(match);
        }
        try {
            return matchMapper.toDto(matchRepository.findFirstByStatusAndTournamentIdOrderByStartDate(MatchStatus.SCHEDULED, tournamentId));
        } catch (Exception e) {
            throw new NotFoundMatchException("Нет матчей для этого турнира!");
        }
    }

    @Transactional
    public Match updateMatchTiming(Long matchId, LocalDateTime newStartDate, LocalDateTime newEndDate) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        match.setStartDate(newStartDate);
        match.setEndDate(newEndDate);

        if (match.getStatus() == MatchStatus.SCHEDULED) {
            matchStatusService.scheduleMatch(match);
        }

        return matchRepository.save(match);
    }

    public MatchDto updateMatchScore(MatchResultDto dto) {
        Match match = matchRepository.findById(dto.getMatchId()).orElseThrow(() -> new NotFoundMatchException("Матч с id: " + dto.getMatchId() + " не найден"));
        match.setFirstTeamScore(dto.getScoreA());
        match.setSecondTeamScore(dto.getScoreB());
        matchRepository.save(match);

        return matchMapper.toDto(match);
    }
}
