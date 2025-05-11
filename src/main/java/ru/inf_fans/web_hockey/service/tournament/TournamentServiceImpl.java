package ru.inf_fans.web_hockey.service.tournament;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.inf_fans.web_hockey.controller.controllerAdvice.NotFoundTournamentException;
import ru.inf_fans.web_hockey.controller.controllerAdvice.NotFoundUserException;
import ru.inf_fans.web_hockey.controller.controllerAdvice.UserNotRegisterException;
import ru.inf_fans.web_hockey.dto.MatchPlayerDto;
import ru.inf_fans.web_hockey.dto.TournamentApiDto;
import ru.inf_fans.web_hockey.dto.UserApiDto;
import ru.inf_fans.web_hockey.entity.tournament.Tournament;
import ru.inf_fans.web_hockey.entity.user.UserEntity;
import ru.inf_fans.web_hockey.mapper.TournamentMapper;
import ru.inf_fans.web_hockey.mapper.UserApiDtoMapper;
import ru.inf_fans.web_hockey.repository.TournamentRepository;
import ru.inf_fans.web_hockey.repository.UserRepository;

import java.util.*;

@RequiredArgsConstructor
@Service
public class TournamentServiceImpl implements TournamentService {

    private final UserApiDtoMapper userApiDtoMapper;
    private final UserRepository userRepository;
    private final TournamentMapper tournamentToDtoMapper;
    private final TournamentRepository tournamentRepository;
    private final PlatformTransactionManager transactionManager;


    public void createTournament(Tournament tournament) {
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
    public void registerUserToTournament(Long tournament_id, int user_id) {
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
        List<UserEntity> userEntities = tournamentRepository.findPlayersById(tournamentId);
        List<UserApiDto> usersDto = new ArrayList<>();
        for (UserEntity userEntity : userEntities) {
            usersDto.add(userApiDtoMapper.toUserApiDto(userEntity));
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
        UserEntity userEntity = userRepository.findUserById(user_id);
        if (userEntity == null) {
            throw new NotFoundUserException("User with id " + user_id + " not found!");
        }
        if (!tournament.getPlayers().contains(userEntity)) {
            throw new UserNotRegisterException("Игрок и так не зарегистрирован на турнир!");
        }

        //Удаляем турнир из турниров игрока и игрока из списка участников
        tournament.getPlayers().remove(userEntity);
        userEntity.getTournament().remove(tournament);

        tournamentRepository.save(tournament);
        userRepository.save(userEntity);
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
        return tournamentToDtoMapper.toApiDto(tournamentRepository.findTournamentsById(tournament_id));
    }

    public Tournament findTournamentById(Long tournament_id) {
        return tournamentRepository.findTournamentsById(tournament_id);
    }
}
