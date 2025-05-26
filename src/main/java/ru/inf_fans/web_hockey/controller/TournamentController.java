package ru.inf_fans.web_hockey.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.inf_fans.web_hockey.dto.TournamentApiDto;
import ru.inf_fans.web_hockey.dto.TournamentRequestDto;
import ru.inf_fans.web_hockey.dto.TournamentResponseDto;
import ru.inf_fans.web_hockey.entity.Tournament;
import ru.inf_fans.web_hockey.service.tournament.TournamentServiceImpl;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class TournamentController {

    private final TournamentServiceImpl tournamentService;

    @Operation(
            summary = "Получить список всех турниров",
            description = "В сущностях отсутствуют зарегистрированные пользователи"
    )
    @GetMapping("/tournaments")
    public ResponseEntity<?> tournaments(
    ) {
        List<TournamentApiDto> tournaments = tournamentService.getAllTournaments();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tournaments);
    }

    @Operation(
            summary = "Добавить турнир"
    )
    @PostMapping(value = "/tournaments")
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<?> addTournament(@RequestBody TournamentRequestDto tournament) {
        tournamentService.createTournament(tournament);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tournament);
    }

    @Operation(
            summary = "Получить информацию о турнире"
    )
    @GetMapping(value = "/tournaments/{tournamentId}")
    public ResponseEntity<?> getTournament(
            @PathVariable(name = "tournamentId") Long tournamentId
    ) {
        TournamentResponseDto tournamentResponse = tournamentService.findTournamentResponseById(tournamentId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tournamentResponse);
    }

    @Operation(
            summary = "Зарегистрировать текущего аутентифицированного пользователя на турнир"
    )
    @PostMapping(value = "/tournament/{tournamentId}/register")
    public ResponseEntity<?> registerOnTournament(
            @PathVariable Long tournamentId,
            Authentication authentication
    ) {
        String email = (String) authentication.getName();
        tournamentService.registerUserToTournament(tournamentId, email);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @Operation(
            summary = "Зарегистрировать группу пользователей на турнир",
            description = "Нужен больше для тестирования, на вход получает List<String> - список почт зарегистрированных в системе пользователей"
    )
    @PostMapping("/tournament/{tournamentId}/registerMany")
    public ResponseEntity<?> registerManyOnTournament(
            @PathVariable Long tournamentId,
            @RequestBody List<String> emailsToRegisterOnTournament
    ) {
        for (String email : emailsToRegisterOnTournament) {
            tournamentService.registerUserToTournament(tournamentId, email);
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
