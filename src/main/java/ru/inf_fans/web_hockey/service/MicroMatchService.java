package ru.inf_fans.web_hockey.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.inf_fans.web_hockey.dto.MatchPlayerDto;
import ru.inf_fans.web_hockey.dto.MicroMatchDto;
import ru.inf_fans.web_hockey.dto.TournamentApiDto;
import ru.inf_fans.web_hockey.entity.MicroMatch;
import ru.inf_fans.web_hockey.entity.Team;
import ru.inf_fans.web_hockey.entity.Tournament;
import ru.inf_fans.web_hockey.entity.enums.MatchStatus;
import ru.inf_fans.web_hockey.entity.User;
import ru.inf_fans.web_hockey.mapper.MatchPlayerMapper;
import ru.inf_fans.web_hockey.mapper.MicroMatchMapper;
import ru.inf_fans.web_hockey.repository.MicroMatchRepository;
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
public class MicroMatchService {

    private final TournamentServiceImpl tournamentService;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final MicroMatchRepository microMatchRepository;
    private final MatchPlayerMapper matchPlayerMapper;
    private final MicroMatchMapper microMatchMapper;
    private final MatchStatusService matchStatusService;
    int teamSize = 4;

    public List<MicroMatchDto> generateMatches(TournamentApiDto tournament) {
        List<MicroMatch> matches = new ArrayList<>();
        List<MatchPlayerDto> availablePlayers = tournamentService.findAllTournamentPlayersEntity(tournament.getId());
        Tournament tournamentEntity = tournamentService.findTournamentById(tournament.getId());

        // Сортировка игроков по рейтингу
        availablePlayers.sort(Comparator.comparingDouble(MatchPlayerDto::getRating));

        // Начальное время первого матча (через 1 день от начала турнира в 00:00)
        LocalDateTime currentMatchTime = tournamentEntity.getStartDate().plusDays(1).atStartOfDay();
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
            MicroMatch match = new MicroMatch(tournamentEntity, teams.getFirst(), teams.getLast());
            match.setStatus(MatchStatus.SCHEDULED);

            // Устанавливаем время начала и окончания матча
            match.setStartDate(currentMatchTime);
            match.setEndDate(currentMatchTime.plus(matchDuration));

            microMatchRepository.save(match);
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

    public List<MicroMatchDto> getMicroMatchDtos(
            Long tournamentId
    ) {
        List<MicroMatch> microMatches = microMatchRepository.getMicroMatchesByTournament_Id(tournamentId);
        List<MicroMatchDto> dtos = new ArrayList<>();
        for (MicroMatch microMatch : microMatches) {
            dtos.add(microMatchMapper.toDto(microMatch));
        }
        return dtos;
    }

    @Transactional
    public MicroMatch updateMatchTiming(Long matchId, LocalDateTime newStartDate, LocalDateTime newEndDate) {
        MicroMatch match = microMatchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        match.setStartDate(newStartDate);
        match.setEndDate(newEndDate);

        if (match.getStatus() == MatchStatus.SCHEDULED) {
            matchStatusService.scheduleMatch(match);
        }

        return microMatchRepository.save(match);
    }
}
