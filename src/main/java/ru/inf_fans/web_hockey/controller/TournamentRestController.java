package ru.inf_fans.web_hockey.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.inf_fans.web_hockey.dto.UserDto;
import ru.inf_fans.web_hockey.entity.tournament.Tournament;
import ru.inf_fans.web_hockey.repository.TournamentRepository;
import ru.inf_fans.web_hockey.service.tournament.TournamentServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/main/tournaments")
public class TournamentRestController {

    final TournamentServiceImpl tournamentService;

    @Autowired
    public TournamentRestController(TournamentServiceImpl tournamentService) {
        this.tournamentService = tournamentService;
    }

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

    /**
     * Метод для регистрации игрока на турнир, на вход получается id турнира (path variable) и id пользователя (request body)
     *
     * @param tournamentId id турнира
     * @param userId       id пользователя
     */
    @PostMapping("{tournamentId}/players")
    public ResponseEntity<String> addPlayerToTournament(
            @PathVariable Long tournamentId,
            @RequestBody int userId
    ) {

        try {
            UserDto userDto = tournamentService.AddUserToTournament(tournamentId, userId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("UserEntity successfully added to tournament with id: "
                            + tournamentId + "!\n" + userDto);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
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
        tournamentService.generateMicroMatches(tournamentId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Micro-matches generated!");
    }

    /**
     * Метод, возвращающий список игроков для определённого турнира
     *
     * @param tournamentId id турнира
     */
    @GetMapping("/{tournamentId}/players")
    public ResponseEntity<List<UserDto>> getPlayers(
            @PathVariable Long tournamentId
    ) {
        List<UserDto> tournamentPlayers = tournamentService.findAllTournamentPlayers(tournamentId);
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
}
