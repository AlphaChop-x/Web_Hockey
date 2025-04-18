package ru.inf_fans.web_hockey.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.inf_fans.web_hockey.entity.tournament.Tournament;
import ru.inf_fans.web_hockey.entity.user.User;
import ru.inf_fans.web_hockey.repository.PlayerPerformanceRepository;
import ru.inf_fans.web_hockey.repository.TeamRepository;
import ru.inf_fans.web_hockey.repository.TournamentRepository;
import ru.inf_fans.web_hockey.repository.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PlatformTransactionManager transactionManager;
    private final TournamentRepository tournamentRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PlatformTransactionManager transactionManager, PlayerPerformanceRepository playerPerformanceRepository, TournamentRepository tournamentRepository, TeamRepository teamRepository) {
        this.userRepository = userRepository;
        this.transactionManager = transactionManager;
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public User createUser(User user) {
        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            userRepository.save(user);
            transactionManager.commit(transaction);
        } catch (DataAccessException e) {
            transactionManager.rollback(transaction);
            throw e;
        }
        return user;
    }

    @Override
    public User getUser(int userId) {
        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            User user = userRepository.getUserById(userId);
            transactionManager.commit(transaction);
            return user;
        } catch (DataAccessException e) {
            transactionManager.rollback(transaction);
            throw e;
        }
    }

    @Override
    public void deleteUser(int userId) {
        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            //Удаляем User-а из всех таблиц
            User user = userRepository.getUserById(userId);
            List<Tournament> tournaments = tournamentRepository.findAllTournaments();
            for (Tournament tournament : tournaments) {
                tournament.removeUser(user);
                System.out.println("User " + userId + " has been deleted from tournament: " + tournament.getName());
            }
            //Если всё в порядке, завершаем транзакт
            transactionManager.commit(transaction);
        } catch (DataAccessException e) {
            transactionManager.rollback(transaction);
            throw e;
        }
    }


    public void updateUserRating(int userId, float rating) {
        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
//            User user = userRepository.getUserById(userId);
//            user.setRating(rating);
//            userRepository.save(user);
            userRepository.updateUser_RatingById(userId, rating);
            transactionManager.commit(transaction);
        } catch (DataAccessException e) {
            transactionManager.rollback(transaction);
        }
    }
}
