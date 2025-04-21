package ru.inf_fans.web_hockey.service.tournament;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.inf_fans.web_hockey.dto.UserDto;
import ru.inf_fans.web_hockey.entity.tournament.MicroMatch;
import ru.inf_fans.web_hockey.entity.tournament.Tournament;
import ru.inf_fans.web_hockey.entity.user.UserEntity;
import ru.inf_fans.web_hockey.mapper.UserMapper;
import ru.inf_fans.web_hockey.repository.TournamentRepository;
import ru.inf_fans.web_hockey.repository.UserRepository;

import java.util.*;

@Service
public class TournamentServiceImpl implements TournamentService {

    private UserMapper userMapper;
    private final UserRepository userRepository;
    TournamentRepository tournamentRepository;
    private final PlatformTransactionManager transactionManager;

    @Autowired
    public TournamentServiceImpl(TournamentRepository tournamentRepository,
                                 UserRepository userRepository,
                                 PlatformTransactionManager transactionManager
    ) {
        this.tournamentRepository = tournamentRepository;
        this.userRepository = userRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    public List<MicroMatch> generateMicroMatches(Long tournamentId) {

        List<MicroMatch> matches = new ArrayList<>();

        //Возвращаем объект турнира, список игроков, а также формат турнира
        Tournament tournament = tournamentRepository.findTournamentsById(tournamentId);
        List<UserEntity> players = tournamentRepository.findPlayersById(tournamentId);
        int teamSize = 4 * 2;

        //Сортировка игроков по рейтингу (от большего к меньшему)
        List<UserEntity> sortedPlayers = players.stream()
                .sorted(Comparator.comparingDouble(UserEntity::getRating).reversed())
                .toList();

        Set<UserEntity> usedPlayers = new HashSet<>(); //Для отслеживания уже участвовавших игроков

        //Проходим по списку сверху вниз
        for (int i = 0; i < sortedPlayers.size(); i++) {
            UserEntity currentPlayer = sortedPlayers.get(i);

            //Проверка на то, участвовал ли игрок в выборе
            if (usedPlayers.contains(currentPlayer)) {
                continue;
            }

            List<UserEntity> team = new ArrayList<>();
            team.add(currentPlayer);
            usedPlayers.add(currentPlayer);

            for (int j = 0; j < teamSize - 1; j++) {
                UserEntity closestPlayer = findClosestAvailablePlayer(currentPlayer, sortedPlayers, usedPlayers);

                if (closestPlayer != null) {
                    team.add(closestPlayer);
                    usedPlayers.add(closestPlayer);
                } else {
                    break;
                }
            }

            List<UserEntity> opposingTeam = new ArrayList<>();

            for (int j = 0; j < teamSize; j++) {
                UserEntity closestOpponent = findClosestAvailablePlayer(currentPlayer, sortedPlayers, usedPlayers);

                if (closestOpponent != null) {
                    opposingTeam.add(closestOpponent);
                    usedPlayers.add(closestOpponent);
                } else {
                    break;
                }

                //Если успешно набраны обе команды, то создаём микро матч
                if (team.size() == teamSize && opposingTeam.size() == teamSize) {
                    matches.add(new MicroMatch(team, opposingTeam, tournament));
                } else {
                    team.forEach(usedPlayers::remove);
                    opposingTeam.forEach(usedPlayers::remove);
                }
            }
        }
        return matches;
    }

    private static UserEntity findClosestAvailablePlayer(UserEntity target, List<UserEntity> players, Set<UserEntity> usedPlayers) {
        return players.stream()
                .filter(p -> !usedPlayers.contains(p) && !p.equals(target))
                .min(Comparator.comparingDouble(p -> Math.abs(p.getRating() - target.getRating())))
                .orElse(null);
    }

    public ResponseEntity<String> createTournament(Tournament tournament) {
        tournamentRepository.save(tournament);

        return new ResponseEntity<>("Tournament created", HttpStatus.CREATED);
    }

    public UserDto AddUserToTournament(Long tournament_id, int user_id) {
        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            tournamentRepository.addPlayerToTournament(tournament_id, user_id);
            UserEntity userEntity = userRepository.getUserById(user_id);
            transactionManager.commit(transaction);
            return userMapper.toUserDto(userEntity);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<UserDto> findAllTournamentPlayers(Long tournamentId) {

        List<UserEntity> userEntities = tournamentRepository.findPlayersById(tournamentId);
        List<UserDto> usersDto = new ArrayList<>();
        for (UserEntity userEntity : userEntities) {
            usersDto.add(userMapper.toUserDto(userEntity));
        }
        return usersDto;
    }

    public void DeletePlayerFromTournament(Long tournament_id, int user_id) {
        Tournament tournament = tournamentRepository.findTournamentsById(tournament_id);
        UserEntity userEntity = userRepository.findUserById(user_id);
        tournament.removeUser(userEntity);
        tournamentRepository.save(tournament);
    }

    public UserDto findUserByTournament_IdAndUserId(Long tournament_id, int user_id) {
        try {
            return userMapper.toUserDto(tournamentRepository.findUserByTournament_IdAndUserId(tournament_id, user_id));
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
}
