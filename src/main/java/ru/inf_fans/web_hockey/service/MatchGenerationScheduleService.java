package ru.inf_fans.web_hockey.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import ru.inf_fans.web_hockey.dto.TournamentApiDto;
import ru.inf_fans.web_hockey.entity.Tournament;
import ru.inf_fans.web_hockey.mapper.TournamentMapper;
import ru.inf_fans.web_hockey.repository.MatchRepository;
import ru.inf_fans.web_hockey.repository.TournamentRepository;
import ru.inf_fans.web_hockey.service.tournament.TournamentServiceImpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchGenerationScheduleService {

    private final MatchService matchService;
    private final TaskScheduler taskScheduler;
    private final ConcurrentMap<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private final TournamentRepository tournamentRepository;
    private final TournamentMapper tournamentMapper;

    @PostConstruct
    public void init() {
        scheduleAllGenerations();
    }

    public void scheduleAllGenerations() {
        log.info("Schedule all generations");
        tournamentRepository.findByStartDateBefore(LocalDateTime.now())
                .forEach(this::scheduleMatchGeneration);
    }

    public void scheduleMatchGeneration(Tournament tournament) {
        log.info("Schedule match generation for tournament {}", tournament.getId());
        cancelExistingTasks(tournament.getId());

        if (tournamentRepository.existMatches(tournament.getId())) {
            return;
        }

        TournamentApiDto dto = tournamentMapper.toApiDto(tournament);

        ScheduledFuture<?> generateTask = taskScheduler.schedule(
                () -> matchService.generateMatches(dto),
                tournament.getStartDate().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant());

        scheduledTasks.put(tournament.getId(), generateTask);
    }

    private void cancelExistingTasks(Long tournamentId) {
        Optional.ofNullable(scheduledTasks.get(tournamentId)).ifPresent(task -> task.cancel(false));
    }
}

