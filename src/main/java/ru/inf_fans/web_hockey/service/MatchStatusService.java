package ru.inf_fans.web_hockey.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.inf_fans.web_hockey.entity.Match;
import ru.inf_fans.web_hockey.entity.enums.MatchStatus;
import ru.inf_fans.web_hockey.repository.MatchRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;

@Service
        @RequiredArgsConstructor
        public class MatchStatusService {
            private final MatchRepository matchRepository;
            private final TaskScheduler taskScheduler;
            private final ConcurrentMap<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

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
                            taskScheduler.schedule(
                                    () -> updateMatchScore(match),
                                    Instant.now().plusSeconds(10)
                            );
                        },
                        match.getEndDate().atZone(ZoneId.systemDefault()).toInstant()
                );

                scheduledTasks.put(match.getId(), startTask);
                scheduledTasks.put(match.getId() + 1_000_000L, endTask);

            }

            private void startMatch(Match match) {
                match.setStatus(MatchStatus.IN_PROGRESS);
                matchRepository.save(match);
            }

            private void completeMatch(Match match) {
                match.setStatus(MatchStatus.FINISHED);
                matchRepository.save(match);

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

    @Scheduled(fixedRate = 60_000) // Проверка каждую минуту
    public void checkMissedMatches() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("Checking missed matches for " + now);
        matchRepository.findMatchesToProcess(now).forEach(match -> {
            if (now.isAfter(match.getEndDate())) {
                completeMatch(match);
            } else if (now.isAfter(match.getStartDate())) {
                startMatch(match);
            }
        });
    }
}
