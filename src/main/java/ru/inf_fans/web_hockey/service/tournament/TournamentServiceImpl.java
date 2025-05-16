package ru.inf_fans.web_hockey.service.tournament;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import ru.inf_fans.web_hockey.controller.controllerAdvice.NotFoundTournamentException;
import ru.inf_fans.web_hockey.controller.controllerAdvice.NotFoundUserException;
import ru.inf_fans.web_hockey.controller.controllerAdvice.UserNotRegisterException;
import ru.inf_fans.web_hockey.dto.*;
import ru.inf_fans.web_hockey.entity.Tournament;
import ru.inf_fans.web_hockey.entity.User;
import ru.inf_fans.web_hockey.mapper.TournamentMapper;
import ru.inf_fans.web_hockey.mapper.UserApiDtoMapper;
import ru.inf_fans.web_hockey.repository.TournamentRepository;
import ru.inf_fans.web_hockey.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TournamentServiceImpl implements TournamentService {

    private final UserApiDtoMapper userApiDtoMapper;
    private final UserRepository userRepository;
    private final TournamentMapper tournamentMapper;
    private final TournamentRepository tournamentRepository;
    private final PlatformTransactionManager transactionManager;


    public void createTournament(TournamentRequestDto tournamentDto) {

        Tournament tournament = tournamentMapper.fromTournamentRequestToEntity(tournamentDto);
        tournamentRepository.save(tournament);

        new ResponseEntity<>("Tournament created", HttpStatus.CREATED);
    }

    @Transactional
    public void deleteTournament(
            Long tournamentId
    ) {
        if (tournamentRepository.findTournamentsById(tournamentId) == null) {
            throw new NotFoundTournamentException(
                    "Tournament with id " + tournamentId + " not found"
            );
        }
        tournamentRepository.deleteById(tournamentId);
    }

    @Transactional
    public void registerUserToTournament(Long tournament_id, String email) {

        Long user_id = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email))
                .getId();

        if (tournamentRepository.isUserRegistered(tournament_id, user_id)) {
            throw new DuplicateKeyException(
                    "User " + user_id + " is already registered to tournament " + tournament_id
            );
        }
        tournamentRepository.addPlayerToTournament(tournament_id, user_id);
    }


    public List<UserApiDto> findAllTournamentPlayers(Long tournamentId) {

        if (tournamentRepository.findTournamentsById(tournamentId) == null) {
            throw new NotFoundTournamentException(
                    "Tournament with id " + tournamentId + " not found"
            );
        }
        List<User> userEntities = tournamentRepository.findPlayersById(tournamentId);
        List<UserApiDto> usersDto = new ArrayList<>();
        for (User user : userEntities) {
            usersDto.add(userApiDtoMapper.toUserApiDto(user));
        }
        return usersDto;
    }

    public List<MatchPlayerDto> findAllTournamentPlayersEntity(Long tournamentId) {

        if (tournamentRepository.findTournamentsById(tournamentId) == null) {
            throw new NotFoundTournamentException(
                    "Tournament with id " + tournamentId + " not found"
            );
        }
        return tournamentRepository.findPlayersDtoById(tournamentId);
    }

    @Transactional
    public void removePlayerFromTournament(
            Long tournament_id,
            int user_id
    ) {
        Tournament tournament = tournamentRepository.findTournamentsById(tournament_id);
        if (tournament == null) {
            throw new NotFoundTournamentException("Tournament with id " + tournament_id + " not found!");
        }
        User user = userRepository.findUserById(user_id);
        if (user == null) {
            throw new NotFoundUserException("User with id " + user_id + " not found!");
        }
        if (!tournament.getPlayers().contains(user)) {
            throw new UserNotRegisterException("Игрок и так не зарегистрирован на турнир!");
        }

        //Удаляем турнир из турниров игрока и игрока из списка участников
        tournament.getPlayers().remove(user);
        user.getTournament().remove(tournament);

        tournamentRepository.save(tournament);
        userRepository.save(user);
    }

    public Tournament save(Tournament tournament) {
        try {
            tournamentRepository.save(tournament);
        } catch (Exception e) {
            throw new RuntimeException(
                    e
            );
        }
        return tournament;
    }

    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAllTournaments();
    }

    public TournamentApiDto findTournamentApiDtoById(Long tournament_id) {
        return tournamentMapper.toApiDto(tournamentRepository.findTournamentsById(tournament_id));
    }

    public Tournament findTournamentById(Long tournament_id) {
        return tournamentRepository.findTournamentsById(tournament_id);
    }

    public TournamentResponseDto findTournamentResponseById(Long tournament_id) {

        Tournament tournament = null;
        try {
            tournament = tournamentRepository.findTournamentsById(tournament_id);
        } catch (Exception e) {
            throw new NotFoundTournamentException("Турнир с id: " + tournament_id + " не найден!");
        }

        TournamentResponseDto dto = new TournamentResponseDto();
        UserApiDtoMapper userApiDtoMapper = new UserApiDtoMapper();


        dto.setTournamentName(tournament.getName());
        dto.setLocation(tournament.getLocation());
        dto.setParticipants(tournament.getPlayers().stream()
                .map(userApiDtoMapper::toUserApiDto)
                .collect(Collectors.toList())
        );
        dto.setTournamentStartDate(tournament.getStartDate());
        dto.setTournamentEndDate(tournament.getEndDate());

        return dto;
    }
}
