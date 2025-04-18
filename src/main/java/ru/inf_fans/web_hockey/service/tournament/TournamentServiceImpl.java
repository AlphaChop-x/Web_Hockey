package ru.inf_fans.web_hockey.service.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.inf_fans.web_hockey.dto.UserDto;
import ru.inf_fans.web_hockey.entity.tournament.MicroMatch;
import ru.inf_fans.web_hockey.entity.tournament.Tournament;
import ru.inf_fans.web_hockey.entity.user.User;
import ru.inf_fans.web_hockey.repository.TournamentRepository;
import ru.inf_fans.web_hockey.repository.UserRepository;
import ru.inf_fans.web_hockey.mapper.MappingUtils;

import java.util.*;

@Service
public class TournamentServiceImpl implements TournamentService {

    private final UserRepository userRepository;
    TournamentRepository tournamentRepository;
    private final PlatformTransactionManager transactionManager;

    @Autowired
    public TournamentServiceImpl(TournamentRepository tournamentRepository,
                                 UserRepository userRepository,
                                 PlatformTransactionManager transactionManager) {
        this.tournamentRepository = tournamentRepository;
        this.userRepository = userRepository;
        this.transactionManager = transactionManager;
    }

    @Override
    public List<MicroMatch> generateMicroMatches(Long tournamentId) {

        List<MicroMatch> matches = new ArrayList<>();

        //Возвращаем объект турнира, список игроков, а также формат турнира
        Tournament tournament = tournamentRepository.findTournamentsById(tournamentId);
        List<User> players = tournamentRepository.findPlayersById(tournamentId);
        int teamSize = 4 * 2;

        //Сортировка игроков по рейтингу (от большего к меньшему)
        List<User> sortedPlayers = players.stream()
                .sorted(Comparator.comparingDouble(User::getRating).reversed())
                .toList();

        Set<User> usedPlayers = new HashSet<>(); //Для отслеживания уже участвовавших игроков

        //Проходим по списку сверху вниз
        for (int i = 0; i < sortedPlayers.size(); i++) {
            User currentPlayer = sortedPlayers.get(i);

            //Проверка на то, участвовал ли игрок в выборе
            if (usedPlayers.contains(currentPlayer)) {
                continue;
            }

            List<User> team = new ArrayList<>();
            team.add(currentPlayer);
            usedPlayers.add(currentPlayer);

            for (int j = 0; j < teamSize - 1; j++) {
                User closestPlayer = findClosestAvailablePlayer(currentPlayer, sortedPlayers, usedPlayers);

                if (closestPlayer != null) {
                    team.add(closestPlayer);
                    usedPlayers.add(closestPlayer);
                } else {
                    break;
                }
            }

            List<User> opposingTeam = new ArrayList<>();

            for (int j = 0; j < teamSize; j++) {
                User closestOpponent = findClosestAvailablePlayer(currentPlayer, sortedPlayers, usedPlayers);

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

    private static User findClosestAvailablePlayer(User target, List<User> players, Set<User> usedPlayers) {
        return players.stream()
                .filter(p -> !usedPlayers.contains(p) && !p.equals(target))
                .min(Comparator.comparingDouble(p -> Math.abs(p.getRating() - target.getRating())))
                .orElse(null);
    }

    public UserDto AddUserToTournament(Long tournament_id, int user_id) {
        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            tournamentRepository.addPlayerToTournament(tournament_id, user_id);
            User user = userRepository.getUserById(user_id);
            MappingUtils mappingUtils = new MappingUtils();
            transactionManager.commit(transaction);
            return mappingUtils.mapToUserDto(user);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<UserDto> findAllTournamentPlayers(Long tournamentId) {

        MappingUtils mappingUtils = new MappingUtils();

        List<User> users = tournamentRepository.findPlayersById(tournamentId);
        List<UserDto> usersDto = new ArrayList<>();
        for (User user : users) {
            usersDto.add(mappingUtils.mapToUserDto(user));
        }
        return usersDto;
    }

    public void DeletePlayerFromTournament(Long tournament_id, int user_id) {
        Tournament tournament = tournamentRepository.findTournamentsById(tournament_id);
        User user = userRepository.findUserById(user_id);
        tournament.removeUser(user);
        tournamentRepository.save(tournament);
    }

    public UserDto findUserByTournament_IdAndUserId(Long tournament_id, int user_id) {
        MappingUtils mappingUtils = new MappingUtils();
        try {
            return mappingUtils.mapToUserDto(tournamentRepository.findUserByTournament_IdAndUserId(tournament_id, user_id));
        } catch (NullPointerException e) {
            return null;
        }
    }

    public void save(Tournament tournament) {
        tournamentRepository.save(tournament);
    }
}
