package ru.inf_fans.web_hockey.service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.inf_fans.web_hockey.dto.MatchDto;
import ru.inf_fans.web_hockey.dto.MatchPlayerDto;
import ru.inf_fans.web_hockey.entity.Match;
import ru.inf_fans.web_hockey.entity.enums.MatchStatus;
import ru.inf_fans.web_hockey.mapper.MatchMapper;
import ru.inf_fans.web_hockey.repository.MatchRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchStatusService {
    private final MatchRepository matchRepository;
    private final RatingCalculator ratingCalculator;
    private final TaskScheduler taskScheduler;
    private final ConcurrentMap<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private final MatchMapper matchMapper;

    @PostConstruct
    public void init() {
        scheduleAllMatches();
    }

    public void scheduleMatch(Match match) {
        cancelExistingTasks(match.getId());

        if (match.getStatus() != MatchStatus.SCHEDULED) {
            return;
        }

        ScheduledFuture<?> startTask = taskScheduler.schedule(
                () -> startMatch(match),
                match.getStartDate().atZone(ZoneId.systemDefault()).toInstant());

        ScheduledFuture<?> endTask = taskScheduler.schedule(
                () -> {
                    completeMatch(match);
                },
                match.getEndDate().atZone(ZoneId.systemDefault()).toInstant()
        );

        scheduledTasks.put(match.getId(), startTask);
        scheduledTasks.put(match.getId() + 1_000_000L, endTask);

    }

    private void startMatch(Match match) {
        match.setStatus(MatchStatus.IN_PROGRESS);
        log.info("Match started: {}", match.getId());
        matchRepository.save(match);
    }

    private void completeMatch(Match match) {
        match = matchRepository.findById(match.getId()).orElseThrow();
        match.setStatus(MatchStatus.FINISHED);
        MatchDto matchDto = matchMapper.toDto(match);

        log.info("Match completed: {}", match.getId());

        log.info("Team 1 players:");
        for (MatchPlayerDto player : matchDto.firstTeam) {
            log.info("{} {}: {}", player.name, player.surname, player.rating);
        }

        log.info("Team 2 players:");
        for (MatchPlayerDto player : matchDto.secondTeam) {
            log.info("{} {}: {}", player.name, player.surname, player.rating);
        }

        ratingCalculator.processMatch(matchDto);

        log.info("Team 1 players:");
        for (MatchPlayerDto player : matchDto.firstTeam) {
            log.info("{} {}: {}", player.name, player.surname, player.rating);
        }

        log.info("Team 2 players:");
        for (MatchPlayerDto player : matchDto.secondTeam) {
            log.info("{} {}: {}", player.name, player.surname, player.rating);
        }

        match = updateRating(match, matchDto);

        cleanUpTasks(match.getId());
    }

    private void updateMatchScore(Match match) {
        matchRepository.save(match);
    }

    private void cancelExistingTasks(Long matchId) {
        Optional.ofNullable(scheduledTasks.get(matchId)).ifPresent(task -> task.cancel(false));
        Optional.ofNullable(scheduledTasks.get(matchId + 1_000_000L)).ifPresent(task -> task.cancel(false));
    }

    private void cleanUpTasks(Long matchId) {
        scheduledTasks.remove(matchId);
        scheduledTasks.remove(matchId + 1_000_000L);
    }

    public void scheduleAllMatches() {
        matchRepository.findAllByStatus(MatchStatus.SCHEDULED)
                .forEach(this::scheduleMatch);
    }

    @Scheduled(fixedRate = 30_000) // Проверка каждую минуту
    public void checkMissedMatches() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("Checking missed matches for " + now);
        matchRepository.findMatchesToProcess(now).forEach(match -> {
            if (now.isAfter(match.getEndDate()) && (match.getStatus() == MatchStatus.IN_PROGRESS || match.getStatus() == MatchStatus.SCHEDULED)) {
                completeMatch(match);
            } else if (now.isAfter(match.getStartDate()) && match.getStatus() == MatchStatus.SCHEDULED) {
                startMatch(match);
            }
        });
    }

    protected Match updateRating(Match match, MatchDto dto) {
        // Обновляем рейтинги игроков первой команды
        match.getFirstTeam().getPlayers().forEach(player -> {
            dto.getFirstTeam().stream()
                    .filter(dtoPlayer -> dtoPlayer.getId().equals(player.getId()))
                    .findFirst()
                    .ifPresent(dtoPlayer -> player.setRating(dtoPlayer.getRating()));
        });

        // Обновляем рейтинги игроков второй команды
        match.getSecondTeam().getPlayers().forEach(player -> {
            dto.getSecondTeam().stream()
                    .filter(dtoPlayer -> dtoPlayer.getId().equals(player.getId()))
                    .findFirst()
                    .ifPresent(dtoPlayer -> player.setRating(dtoPlayer.getRating()));
        });

        matchRepository.save(match);
        return match;
    }
}
