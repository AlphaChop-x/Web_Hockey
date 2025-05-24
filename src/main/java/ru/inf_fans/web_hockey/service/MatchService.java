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
import ru.inf_fans.web_hockey.mapper.MicroMatchMapper;
import ru.inf_fans.web_hockey.repository.MatchRepository;
import ru.inf_fans.web_hockey.repository.TeamRepository;
import ru.inf_fans.web_hockey.repository.UserRepository;
import ru.inf_fans.web_hockey.service.tournament.TournamentServiceImpl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MatchService {

    private final TournamentServiceImpl tournamentService;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final MatchPlayerMapper matchPlayerMapper;
    private final MicroMatchMapper microMatchMapper;
    private final MatchStatusService matchStatusService;
    int teamSize = 4;

    public List<MatchDto> generateMatches(TournamentApiDto tournament) {
        List<Match> matches = new ArrayList<>();
        List<MatchPlayerDto> availablePlayers = tournamentService.findAllTournamentPlayersEntity(tournament.getId());
        Tournament tournamentEntity = tournamentService.findTournamentById(tournament.getId());

        // Сортировка игроков по рейтингу
        availablePlayers.sort(Comparator.comparingDouble(MatchPlayerDto::getRating));

        // Начальное время первого матча (через 1 день от начала турнира в 00:00)
        LocalDateTime currentMatchTime = tournamentEntity.getStartDate().plusDays(1);
        // Длительность матча (1 минута)
        Duration matchDuration = Duration.ofMinutes(1);
        // Перерыв между матчами (1.5 минуты)
        Duration breakDuration = Duration.ofMillis(90_000);

        while (availablePlayers.size() >= 2 * teamSize) {
            // Берем первых N игроков (чтобы избежать сильного дисбаланса)
            List<MatchPlayerDto> candidates = new ArrayList<>(availablePlayers.subList(0, 2 * teamSize));

            // Разбиваем на две команды с близким рейтингом
            List<Team> teams = balanceTeams(candidates);

            for (Team team : teams) {
                team.setTournament(tournamentEntity);
                teamRepository.save(team);
            }

            // Создаем матч
            Match match = new Match(tournamentEntity, teams.getFirst(), teams.getLast());
            match.setStatus(MatchStatus.SCHEDULED);

            // Устанавливаем время начала и окончания матча
            match.setStartDate(currentMatchTime);
            match.setEndDate(currentMatchTime.plus(matchDuration));

            matchRepository.save(match);
            matches.add(match);

            // Удаляем игроков из доступных (чтобы не повторялись)
            availablePlayers.removeAll(
                    teams.getFirst().getPlayers().stream()
                            .map(matchPlayerMapper::toDto)
                            .toList());
            availablePlayers.removeAll(
                    teams.getLast().getPlayers().stream()
                            .map(matchPlayerMapper::toDto)
                            .toList()
            );

            // Увеличиваем время для следующего матча
            currentMatchTime = currentMatchTime.plus(matchDuration).plus(breakDuration);
        }

        return matches.stream()
                .map(microMatchMapper::toDto)
                .collect(Collectors.toList());
    }

    // Балансировка команд (жадный алгоритм)
    private List<Team> balanceTeams(List<MatchPlayerDto> players) {
        players.sort(Comparator.comparingDouble(MatchPlayerDto::getRating).reversed());

        Team teamA = new Team();
        Team teamB = new Team();

        int sumA = 0, sumB = 0;

        for (MatchPlayerDto player : players) {
            if (sumA <= sumB && teamA.size() < teamSize) {
                User entity = userRepository.findUserByEmail(player.getEmail());
                teamA.add(entity);
                sumA += player.getRating();
            } else {
                User entity = userRepository.findUserByEmail(player.getEmail());
                teamB.add(entity);
                sumB += player.getRating();
            }
        }

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        return List.of(teamA, teamB);
    }

    public List<MatchDto> getMatchDtos(
            Long tournamentId
    ) {
        List<Match> matches = matchRepository.getMatchesByTournament_Id(tournamentId);
        List<MatchDto> dtos = new ArrayList<>();
        for (Match match : matches) {
            dtos.add(microMatchMapper.toDto(match));
        }
        return dtos;
    }

    public MatchDto getCurrentMatch(Long tournamentId) {
        Match match = matchRepository.findFirstByStatusAndTournamentIdOrderByStartDate(MatchStatus.IN_PROGRESS, tournamentId);
        if (match != null) {
            return microMatchMapper.toDto(match);
        }
        try {
            return microMatchMapper.toDto(matchRepository.findFirstByStatusAndTournamentIdOrderByStartDate(MatchStatus.SCHEDULED, tournamentId));
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

        return microMatchMapper.toDto(match);
    }
}
