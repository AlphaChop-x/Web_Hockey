package ru.inf_fans.web_hockey.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.inf_fans.web_hockey.dto.TournamentApiDto;
import ru.inf_fans.web_hockey.dto.TournamentRequestDto;
import ru.inf_fans.web_hockey.dto.TournamentResponseDto;
import ru.inf_fans.web_hockey.entity.Tournament;
import ru.inf_fans.web_hockey.entity.User;
import ru.inf_fans.web_hockey.service.UserServiceImpl;
import ru.inf_fans.web_hockey.service.tournament.TournamentServiceImpl;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class TournamentController {

    private final UserServiceImpl userService;
    private final TournamentServiceImpl tournamentService;

    @GetMapping("/tournaments")
    public ResponseEntity<?> tournaments(
    ) {
        List<Tournament> tournaments = tournamentService.getAllTournaments();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tournaments);
    }

    @PostMapping(value = "/tournaments")
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<?> addTournament(
            @RequestBody TournamentRequestDto tournament
    ) {
        tournamentService.createTournament(tournament);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tournament);
    }

    @GetMapping(value = "/tournaments/{tournamentId}")
    public ResponseEntity<?> getTournament(
            @PathVariable(name = "tournamentId") Long tournamentId
    ) {
        TournamentResponseDto tournamentResponse = tournamentService.findTournamentResponseById(tournamentId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tournamentResponse);
    }

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
}
