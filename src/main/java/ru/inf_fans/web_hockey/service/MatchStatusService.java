package ru.inf_fans.web_hockey.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import ru.inf_fans.web_hockey.dto.MatchDto;
import ru.inf_fans.web_hockey.entity.Match;
import ru.inf_fans.web_hockey.entity.enums.MatchStatus;
import ru.inf_fans.web_hockey.event.MatchEvent;
import ru.inf_fans.web_hockey.mapper.MatchPlayerMapper;
import ru.inf_fans.web_hockey.mapper.MatchMapper;
import ru.inf_fans.web_hockey.repository.MatchRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
public class MatchStatusService {
    private final MatchRepository matchRepository;
    private final TaskScheduler taskScheduler;
    private final ApplicationEventPublisher eventPublisher;
    private final ConcurrentMap<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private final Map<Long,
            DeferredResult<ResponseEntity<MatchDto>>> pendingUpdates = new ConcurrentHashMap<>();
    private final MatchPlayerMapper matchPlayerMapper;
    private final MatchMapper matchMapper;

    @PostConstruct
    public void init() {
        scheduleAllMatches();
    }

    public DeferredResult<ResponseEntity<MatchDto>> waitForMatchUpdate(Long matchId, Long timeout) {
        DeferredResult<ResponseEntity<MatchDto>> deferredResult = new DeferredResult<>(timeout);

        deferredResult.onTimeout(() -> {
            pendingUpdates.remove(matchId);
            deferredResult.setResult(ResponseEntity.noContent().build());
        });

        pendingUpdates.put(matchId, deferredResult);
        return deferredResult;
    }

    private void notifySubscribers(Match match) {
        DeferredResult<ResponseEntity<MatchDto>> result = pendingUpdates.get(match.getId());
        if (result != null) {
            result.setResult(ResponseEntity.ok(matchMapper.toDto(match)));
            pendingUpdates.remove(match.getId());
        }
        eventPublisher.publishEvent(new MatchEvent(this, match));
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

    // Модифицируем методы обновления статусов
    private void startMatch(Match match) {
        match.setStatus(MatchStatus.IN_PROGRESS);
        matchRepository.save(match);
        notifySubscribers(match);
    }

    private void completeMatch(Match match) {
        match.setStatus(MatchStatus.FINISHED);
        matchRepository.save(match);
        notifySubscribers(match);
        cleanUpTasks(match.getId());
    }

    private void updateMatchScore(Match match) {
        matchRepository.save(match);
        notifySubscribers(match);
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
