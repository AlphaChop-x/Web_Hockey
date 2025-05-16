package ru.inf_fans.web_hockey.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.inf_fans.web_hockey.entity.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {

    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByEmail(String email);

    User getUserById(int id);

    @Modifying
    @Query("UPDATE User u SET u.rating = :rating WHERE u.id = :playerId")
    @Transactional
    void updateUser_RatingById(int playerId, float rating);

    @Transactional
    User findUserById(int user_id);

    @Query("SELECT u FROM User u WHERE  u.email = :email")
    User findByEmailIgnoreCase(String email);

    User findUserByEmail(String email);

    boolean existsByEmail(String email);
}
