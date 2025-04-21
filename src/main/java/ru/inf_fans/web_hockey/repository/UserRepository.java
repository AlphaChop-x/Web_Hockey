package ru.inf_fans.web_hockey.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.inf_fans.web_hockey.entity.user.UserEntity;

public interface UserRepository extends CrudRepository<UserEntity, String> {
    UserEntity getUserById(int id);

    @Modifying
    @Query("UPDATE UserEntity u SET u.rating = :rating WHERE u.id = :playerId")
    @Transactional
    void updateUser_RatingById(int playerId, float rating);

    @Transactional
    UserEntity findUserById(int user_id);

    @Query("SELECT u FROM UserEntity u WHERE  u.email = :email")
    UserEntity findByEmailIgnoreCase(String email);
}
