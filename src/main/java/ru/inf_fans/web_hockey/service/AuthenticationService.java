package ru.inf_fans.web_hockey.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.inf_fans.web_hockey.controller.controllerAdvice.AlreadyRegisteredEmailException;
import ru.inf_fans.web_hockey.controller.controllerAdvice.AlreadyRegisteredPhoneNumberException;
import ru.inf_fans.web_hockey.controller.controllerAdvice.NotFoundUserException;
import ru.inf_fans.web_hockey.dto.AuthenticationResponseDto;
import ru.inf_fans.web_hockey.dto.LoginRequestDto;
import ru.inf_fans.web_hockey.dto.RegistrationRequestDto;
import ru.inf_fans.web_hockey.entity.Token;
import ru.inf_fans.web_hockey.entity.User;
import ru.inf_fans.web_hockey.entity.enums.Role;
import ru.inf_fans.web_hockey.repository.TokenRepository;
import ru.inf_fans.web_hockey.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;

    public void register(RegistrationRequestDto request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AlreadyRegisteredEmailException("Email " + request.getEmail() + " already registered");
        }
        if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new AlreadyRegisteredPhoneNumberException("Phone " + request.getPhoneNumber() + " already registered");
        }

        User user = new User();

        user.setName(request.getFirstName());
        user.setSurname(request.getSurname());
        user.setPatronymic(request.getPatronymic());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setGender(request.getGender());
        user.setBorn(request.getBorn());
        user.setRole(Role.USER);
        user.setRating(100.0f * ChronoUnit.YEARS.between(request.getBorn().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now()));

        user = userRepository.save(user);
    }

    public AuthenticationResponseDto authenticate(LoginRequestDto request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException(e);
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        revokeAllToken(user);

        saveUserToken(accessToken, refreshToken, user);

        return new AuthenticationResponseDto(accessToken, refreshToken);
    }

    public ResponseEntity<AuthenticationResponseDto> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authorizationHeader.substring(7);
        String email = jwtService.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user found"));

        if (jwtService.isValidRefresh(token, user)) {

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            revokeAllToken(user);

            saveUserToken(accessToken, refreshToken, user);

            return new ResponseEntity<>(new AuthenticationResponseDto(accessToken, refreshToken), HttpStatus.OK);

        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


    public void revokeAllToken(User user) {
        List<Token> validTokens = tokenRepository.findAllAccessTokenByUser(user.getId());

        if (!validTokens.isEmpty()) {
            validTokens.forEach(t -> {
                t.setLoggedOut(true);
            });
        }

        tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(String accessToken, String refreshToken, User user) {

        Token token = new Token();

        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setLoggedOut(false);
        token.setUser(user);

        tokenRepository.save(token);
    }
}
