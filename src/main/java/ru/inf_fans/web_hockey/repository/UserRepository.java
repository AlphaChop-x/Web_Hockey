package ru.inf_fans.web_hockey.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.inf_fans.web_hockey.dto.LoginResponseDto;
import ru.inf_fans.web_hockey.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    @Transactional
    User findUserById(Long id);

    @Query("SELECT new ru.inf_fans.web_hockey.dto.LoginResponseDto(u.id, u.name, u.surname, u.role)" +
            "FROM User u WHERE u.email = :email")
    LoginResponseDto findResponseDtoByEmail(@Param("email") String email);

    User findUserByEmail(String email);
}
