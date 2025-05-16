package ru.inf_fans.web_hockey.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.inf_fans.web_hockey.entity.Token;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends CrudRepository<Token, Long> {
    Optional<Token> findByAccessToken(String accessToken);

    Optional<Token> findByRefreshToken(String refreshToken);

    @Query("""
            SELECT t FROM Token t INNER JOIN User u
            ON t.user.id = u.id
            WHERE t.user.id = :userId and t.loggedOut = false
            """)
    List<Token> findAllAccessTokenByUser(Long userId);
}
