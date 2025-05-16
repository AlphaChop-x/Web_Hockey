//package ru.inf_fans.web_hockey.controller;
//
//import io.swagger.v3.oas.annotations.Operation;
//import jakarta.persistence.EntityNotFoundException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//import ru.inf_fans.web_hockey.controller.controllerAdvice.ErrorMessage;
//import ru.inf_fans.web_hockey.dto.TournamentApiDto;
//import ru.inf_fans.web_hockey.dto.UserApiDto;
//import ru.inf_fans.web_hockey.entity.Tournament;
//import ru.inf_fans.web_hockey.mapper.TournamentMapper;
//import ru.inf_fans.web_hockey.service.UserServiceImpl;
//import ru.inf_fans.web_hockey.service.MicroMatchService;
//import ru.inf_fans.web_hockey.service.tournament.TournamentServiceImpl;
//
//import java.util.List;
//import java.util.NoSuchElementException;
//
//@RequiredArgsConstructor
//@RestController()
//@RequestMapping("/api/tournaments")
//public class TournamentRestController {
//
//    private final TournamentServiceImpl tournamentService;
//    private final TournamentMapper tournamentToDtoMapper;
//    private final UserServiceImpl userServiceImpl;
//    private final MicroMatchService microMatchService;
//
//    @Operation(
//            summary = "Создать турнир",
//            description = "Принимает tournamentDto и создаёт турнир"
//    )
//    @PostMapping()
//    public ResponseEntity<?> createTournament(
//            @RequestBody TournamentApiDto tournamentDto
//    ) {
//
//        try {
//            Tournament tournament = tournamentService.save(tournamentToDtoMapper.toTournament(tournamentDto));
//            return ResponseEntity
//                    .status(HttpStatus.CREATED)
//                    .body(tournament);
//        } catch (Exception e) {
//            return ResponseEntity
//                    .status(HttpStatus.CONFLICT)
//                    .body(new ErrorMessage("Ошибка при создании турнира", e.getMessage()));
//        }
//    }
//
//    @Operation(
//            summary = "Получить информацию о турнире",
//            description = "Возвращает поля tournamentApiDto "
//    )
//    @GetMapping("/{tournamentId}")
//    public ResponseEntity<?> getTournamentInfo(
//            @PathVariable Long tournamentId
//    ) {
//        try {
//            TournamentApiDto tournamentApiDto = tournamentService.findTournamentApiDtoById(tournamentId);
//            return ResponseEntity
//                    .status(HttpStatus.FOUND)
//                    .body(tournamentApiDto);
//        } catch (NullPointerException e) {
//            return ResponseEntity
//                    .status(HttpStatus.NOT_FOUND)
//                    .body(new ErrorMessage("Турнир с id: " + tournamentId + " не существует", e.getMessage()));
//        }
//    }
//
//    @Operation(
//            summary = "Удалить турнир",
//            description = "Удаляет турнир по заданному id "
//    )
//    @DeleteMapping("/{tournamentId}")
//    public ResponseEntity<?> deleteTournament(
//            @PathVariable Long tournamentId
//    ) {
//        try {
//            tournamentService.deleteTournament(tournamentId);
//            return ResponseEntity
//                    .status(HttpStatus.OK)
//                    .build();
//        } catch (NoSuchElementException e) {
//            return ResponseEntity
//                    .status(HttpStatus.NOT_FOUND)
//                    .body(new ErrorMessage("Попытка удалить несуществующий турнир", e.getMessage()));
//        }
//    }
//
//    @Operation(
//            summary = "Зарегистрировать пользователя на турнир",
//            description = "Регистрирует пользователя "
//    )
//    @PostMapping("{tournamentId}/register")
//    public ResponseEntity<?> addPlayerToTournament(
//            @PathVariable Long tournamentId,
//            Authentication authentication
//    ) {
//        String email = authentication.getName();
//        Long userId = userServiceImpl.getUserIdByName(email);
//
//        try {
//            tournamentService.registerUserToTournament(tournamentId, userId);
//            return ResponseEntity
//                    .status(HttpStatus.OK)
//                    .build();
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity
//                    .status(HttpStatus.NOT_FOUND)
//                    .build();
//        }
//    }
//
//    @Operation(
//            summary = "Получить список игроков",
//            description = "Возвращает список игроков по id турнира "
//    )
//    @GetMapping("/{tournamentId}/players")
//    public ResponseEntity<?> getPlayers(
//            @PathVariable Long tournamentId
//    ) {
//        List<UserApiDto> tournamentPlayers = tournamentService.findAllTournamentPlayers(tournamentId);
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(tournamentPlayers);
//
//    }
//
//    @Operation(
//            summary = "Снять игрока с турнира",
//            description = "Удаляет игрока с заданного турнира "
//    )
//    @DeleteMapping("/{tournamentId}/players/{playerId}")
//    public ResponseEntity<String> deletePlayerFromTournament(
//            @PathVariable Long tournamentId,
//            @PathVariable int playerId
//    ) {
//        tournamentService.removePlayerFromTournament(tournamentId, playerId);
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body("Игрок с id: " + playerId + " успешно снят с турнира!");
//
//    }
//
////    /**
////     * Метод для генерации микро матчей для заданного турнира
////     *
////     * @param tournamentId id турнира
////     */
////    @GetMapping("/{tournamentId}/micro-matches")
////    public ResponseEntity<String> generateMatches(
////            @PathVariable Long tournamentId
////    ) {
////        Tournament tournament = tournamentService.findTournamentById(tournamentId);
////        microMatchService.generateMatches(tournament);
////        return ResponseEntity
////                .status(HttpStatus.OK)
////                .body("Micro-matches generated!");
////    }
//
//}
