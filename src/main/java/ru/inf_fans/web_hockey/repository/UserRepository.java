package ru.inf_fans.web_hockey.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;
import ru.inf_fans.web_hockey.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    @Transactional
    User findUserById(Long id);

    User findUserByEmail(String email);
}
