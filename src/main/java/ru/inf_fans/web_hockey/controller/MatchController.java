package ru.inf_fans.web_hockey.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import ru.inf_fans.web_hockey.dto.MatchResultDto;
import ru.inf_fans.web_hockey.dto.MatchDto;
import ru.inf_fans.web_hockey.dto.TournamentApiDto;
import ru.inf_fans.web_hockey.service.MatchStatusService;
import ru.inf_fans.web_hockey.service.MatchService;
import ru.inf_fans.web_hockey.service.tournament.TournamentServiceImpl;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tournaments/{tournamentId}/matches")
public class MatchController {

    private final TournamentServiceImpl tournamentService;
    private final MatchService matchService;
    private final MatchStatusService matchStatusService;

//    @Operation(
//            summary = "Сгенерировать микро матчи",
//            description = "Принимает id турнира в пути {tournamentId}"
//    )
//    @PostMapping("")
//    public ResponseEntity<?> generateMatches(
//            @PathVariable Long tournamentId
//    ) {
//        TournamentApiDto tournament = tournamentService.findTournamentApiDtoById(tournamentId);
//
//        int countOfPlayers = tournamentService.findAllTournamentPlayers(tournamentId).size();
//        List<MatchDto> matches = matchService.generateMatches(tournament);
//
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body("Количество игроков турнира: " + countOfPlayers +
//                        "\n" + matches);
//    }

    @Operation(
            summary = "Вернуть микро матчи",
            description = "По id турнира в пути {tournamentId} возвращает список микром матчей"
    )
    @GetMapping("")
    public ResponseEntity<?> getMatches(
            @PathVariable Long tournamentId
    ) {
        List<MatchDto> matchDtos = matchService.getMatchDtos(tournamentId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(matchDtos);
    }

    @Operation(
            summary = "Вернуть текущий матч",
            description = "Возвращает текущий матч, если в данный момент нет идущих матчей, то возвращает ближайший"
    )
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentMatch(
            @PathVariable Long tournamentId
    ) {
        MatchDto dto = matchService.getCurrentMatch(tournamentId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    @Operation(
            summary = "Обновление счёта",
            description = "Автоматическое завершение через MatchStatusService."
    )
    @PostMapping("/{matchId}/update")
    public ResponseEntity<?> updateMatchScore(
            @PathVariable Long matchId,
            @RequestBody MatchResultDto matchResult
    ) {
        matchService.updateMatchScore(matchId, matchResult);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}