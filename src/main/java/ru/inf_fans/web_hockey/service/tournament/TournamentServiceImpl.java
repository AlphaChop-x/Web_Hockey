package ru.inf_fans.web_hockey.service.tournament;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.inf_fans.web_hockey.dto.UserApiDto;
import ru.inf_fans.web_hockey.dto.UserDto;
import ru.inf_fans.web_hockey.entity.tournament.Tournament;
import ru.inf_fans.web_hockey.entity.user.UserEntity;
import ru.inf_fans.web_hockey.mapper.UserApiDtoMapper;
import ru.inf_fans.web_hockey.mapper.UserMapper;
import ru.inf_fans.web_hockey.repository.TournamentRepository;
import ru.inf_fans.web_hockey.repository.UserRepository;

import java.util.*;

@RequiredArgsConstructor
@Service
public class TournamentServiceImpl implements TournamentService {

    private final UserApiDtoMapper userApiDtoMapper;
    private final UserRepository userRepository;
    private final TournamentRepository tournamentRepository;
    private final PlatformTransactionManager transactionManager;


    public ResponseEntity<String> createTournament(Tournament tournament) {
        tournamentRepository.save(tournament);

        return new ResponseEntity<>("Tournament created", HttpStatus.CREATED);
    }

    public ResponseEntity<?> deleteTournament(
            Long tournamentId
    ) {
        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            tournamentRepository.deleteById(tournamentId);
            transactionManager.commit(transaction);
            return new ResponseEntity<>("Турнир удалён успешно", HttpStatus.OK);
        } catch (Exception e) {
            transactionManager.rollback(transaction);
            return new ResponseEntity<>("Что-то пошло не так", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public UserApiDto addUserToTournament(Long tournament_id, int user_id) {
        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            tournamentRepository.addPlayerToTournament(tournament_id, user_id);
            UserEntity userEntity = userRepository.getUserById(user_id);
            UserApiDto dto = userApiDtoMapper.toUserApiDto(userEntity);
            transactionManager.commit(transaction);
            return dto;
        } catch (Exception e) {
            transactionManager.rollback(transaction);
            throw new RuntimeException("При регистрации пользователя на турнир что-то пошло не так");
        }
    }

    public List<UserApiDto> findAllTournamentPlayers(Long tournamentId) {

        List<UserEntity> userEntities = tournamentRepository.findPlayersById(tournamentId);
        List<UserApiDto> usersDto = new ArrayList<>();
        for (UserEntity userEntity : userEntities) {
            usersDto.add(userApiDtoMapper.toUserApiDto(userEntity));
        }
        return usersDto;
    }

    public void DeletePlayerFromTournament(Long tournament_id, int user_id) {
        Tournament tournament = tournamentRepository.findTournamentsById(tournament_id);
        UserEntity userEntity = userRepository.findUserById(user_id);
        tournament.removeUser(userEntity);
        tournamentRepository.save(tournament);
    }

    public UserApiDto findUserByTournament_IdAndUserId(Long tournament_id, int user_id) {
        try {
            return userApiDtoMapper.toUserApiDto(tournamentRepository.findUserByTournament_IdAndUserId(tournament_id, user_id));
        } catch (NullPointerException e) {
            return null;
        }
    }

    public void save(Tournament tournament) {
        tournamentRepository.save(tournament);
    }

    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAllTournaments();
    }

    public Tournament findTournamentById(Long tournament_id) {
        return tournamentRepository.findTournamentsById(tournament_id);
    }
}
