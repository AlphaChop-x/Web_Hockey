package ru.inf_fans.web_hockey.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.inf_fans.web_hockey.dto.MicroMatchDto;
import ru.inf_fans.web_hockey.dto.TournamentApiDto;
import ru.inf_fans.web_hockey.service.MicroMatchService;
import ru.inf_fans.web_hockey.service.tournament.TournamentServiceImpl;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class MicroMatchController {

    private final TournamentServiceImpl tournamentService;
    private final MicroMatchService microMatchService;

    @Operation(
            summary = "Сгенерировать микро матчи",
            description = "Принимает id турнира в пути {tournamentId}"
    )
    @PostMapping("/tournaments/{tournamentId}/micro-matches")
    public ResponseEntity<?> generateMicroMatches(
            @PathVariable Long tournamentId
    ) {
        TournamentApiDto tournament = tournamentService.findTournamentApiDtoById(tournamentId);

        int countOfPlayers = tournamentService.findAllTournamentPlayers(tournamentId).size();
        List<MicroMatchDto> microMatches = microMatchService.generateMatches(tournament);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Количество игроков турнира: " + countOfPlayers +
                        "\n" + microMatches);
    }

    @Operation(
            summary = "Вернуть микро матчи",
            description = "По id турнира в пути {tournamentId} возвращает список микром матчей"
    )
    @GetMapping("/tournaments/{tournamentId}/micro-matches")
    public ResponseEntity<?> getMicroMatches(
            @PathVariable Long tournamentId
    ) {
        List<MicroMatchDto> microMatchDtos = microMatchService.getMicroMatchDtos(tournamentId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(microMatchDtos);
    }
}
