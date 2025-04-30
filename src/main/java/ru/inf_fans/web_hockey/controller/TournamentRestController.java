package ru.inf_fans.web_hockey.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.inf_fans.web_hockey.dto.TournamentApiDto;
import ru.inf_fans.web_hockey.dto.UserApiDto;
import ru.inf_fans.web_hockey.dto.UserDto;
import ru.inf_fans.web_hockey.entity.tournament.Tournament;
import ru.inf_fans.web_hockey.mapper.TournamentToDtoMapper;
import ru.inf_fans.web_hockey.repository.TournamentRepository;
import ru.inf_fans.web_hockey.service.tournament.TournamentServiceImpl;

import java.util.List;

@RequiredArgsConstructor
@RestController()
@RequestMapping("/api/tournaments")
public class TournamentRestController {

    final TournamentServiceImpl tournamentService;
    final TournamentToDtoMapper tournamentToDtoMapper;

    /**
     * Метод, служащий для создания турниров в системе, в теле принимает экземпляр турнира
     *
     * @param tournament турнир
     */
    @PostMapping()
    public ResponseEntity<String> createTournament(
            @RequestBody Tournament tournament
    ) {
        tournamentService.save(tournament);
        String tournamentName = tournament.getName();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Tournament with name: " + tournamentName + " created!\n");
    }

    @GetMapping("/{tournamentId}")
    public ResponseEntity<?> getTournamentInfo(
            @PathVariable Long tournamentId
    ) {
        Tournament tournament = tournamentService.findTournamentById(tournamentId);
        TournamentApiDto dto = tournamentToDtoMapper.toApiDto(tournament);

        if (tournament == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Tournament with id: " + tournamentId + " not found!");
        }
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(dto);
    }

    @DeleteMapping("/{tournamentId}")
    public ResponseEntity<?> deleteTournament(
            @PathVariable Long tournamentId
    ) {
        return tournamentService.deleteTournament(tournamentId);
    }

    /**
     * Метод для регистрации игрока на турнир, на вход получается id турнира (path variable) и id пользователя (request body)
     *
     * @param tournamentId id турнира
     * @param userId       id пользователя
     */
    @PostMapping("{tournamentId}/register")
    public ResponseEntity<?> addPlayerToTournament(
            @PathVariable Long tournamentId,
            @RequestBody int userId
    ) {

        try {
            UserApiDto dto = tournamentService.addUserToTournament(tournamentId, userId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(dto);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /**
     * Метод, возвращающий список игроков для определённого турнира
     *
     * @param tournamentId id турнира
     */
    @GetMapping("/{tournamentId}/players")
    public ResponseEntity<?> getPlayers(
            @PathVariable Long tournamentId
    ) {
        List<UserApiDto> tournamentPlayers = tournamentService.findAllTournamentPlayers(tournamentId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tournamentPlayers);
    }

    /**
     * Метод для удаления игрока с турнира
     *
     * @param tournamentId id турнира
     * @param playerId     id игрока
     */
    @DeleteMapping("/{tournamentId}/players/{playerId}")
    public ResponseEntity<String> deletePlayerFromTournament(
            @PathVariable Long tournamentId,
            @PathVariable int playerId
    ) {
        tournamentService.DeletePlayerFromTournament(tournamentId, playerId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Players with id - " + playerId + " deleted from tournament!");

    }

    /**
     * Метод для генерации микро матчей для заданного турнира
     *
     * @param tournamentId id турнира
     */
    @GetMapping("/{tournamentId}/micro-matches")
    public ResponseEntity<String> generateMatches(
            @PathVariable Long tournamentId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Micro-matches generated!");
    }

}
