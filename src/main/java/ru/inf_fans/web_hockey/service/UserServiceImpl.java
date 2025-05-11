package ru.inf_fans.web_hockey.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.inf_fans.web_hockey.dto.UserDto;
import ru.inf_fans.web_hockey.entity.tournament.Tournament;
import ru.inf_fans.web_hockey.entity.user.UserEntity;
import ru.inf_fans.web_hockey.mapper.UserEntityMapper;
import ru.inf_fans.web_hockey.repository.TournamentRepository;
import ru.inf_fans.web_hockey.repository.UserRepository;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final UserEntityMapper userEntityMapper;
    private final PlatformTransactionManager transactionManager;
    private final TournamentRepository tournamentRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            UserEntity userEntity = userEntityMapper.toUserEntity(userDto);
            userEntity.setPassword(encoder.encode(userEntity.getPassword()));

            int userAgeInYears = calculateAge(userEntity.getBorn());
            userEntity.setRating(100.0f * userAgeInYears);

            userRepository.save(userEntity);
            transactionManager.commit(transaction);
        } catch (DataAccessException e) {
            transactionManager.rollback(transaction);
            throw e;
        }
        return userDto;
    }

    @Override
    public UserDto getUser(int userId) {
        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            UserEntity userEntity = userRepository.getUserById(userId);
            UserDto userDto = userEntityMapper.toUserDto(userEntity);
            transactionManager.commit(transaction);
            return userDto;
        } catch (DataAccessException e) {
            transactionManager.rollback(transaction);
            throw e;
        }
    }

    @Override
    public void deleteUser(int userId) {
        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            //Удаляем UserEntity-а из всех таблиц
            UserEntity userEntity = userRepository.getUserById(userId);
            List<Tournament> tournaments = tournamentRepository.findAllTournaments();
            for (Tournament tournament : tournaments) {
                tournament.removeUser(userEntity);
                System.out.println("UserEntity " + userId + " has been deleted from tournament: " + tournament.getName());
            }
            //Если всё в порядке, завершаем транзакт
            transactionManager.commit(transaction);
        } catch (DataAccessException e) {
            transactionManager.rollback(transaction);
            throw e;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserEntity userEntity = userRepository.findByEmailIgnoreCase(email);

        if (userEntity == null) {
            throw new UsernameNotFoundException("UserEntity with email " + email + " not found");
        }

        return new User(userEntity.getEmail(), userEntity.getPassword(), extractRoles(userEntity));
    }

    private Collection<? extends GrantedAuthority> extractRoles(UserEntity userEntity) {
        return userEntity.getRole().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());
    }

    public void updateUserRating(int userId, float rating) {
        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            userRepository.updateUser_RatingById(userId, rating);
            transactionManager.commit(transaction);
        } catch (DataAccessException e) {
            transactionManager.rollback(transaction);
        }
    }

    public UserDto getUserDtoByUserEmail(String email) {
        UserEntity entity = userRepository.findUserByEmail(email);

        return userEntityMapper.toUserDto(entity);
    }

    public int getUserIdByName(String name) {
        return userRepository.findUserByEmail(name).getId();
    }

    /**
     * Вспомогательный метод, подсчитывающий текущий возраст игрока (Кол-во полных лет)
     *
     * @return int Возраст (полных лет) игрока
     */
    public int calculateAge(Date born) {
        if (born == null) {
            throw new IllegalArgumentException("born is null");
        }

        LocalDate birthDate = born.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        LocalDate currentDate = LocalDate.now();

        return Period.between(birthDate, currentDate).getYears();
    }
}
