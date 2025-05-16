package ru.inf_fans.web_hockey.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.inf_fans.web_hockey.dto.LoginRequestDto;
import ru.inf_fans.web_hockey.dto.RegistrationRequestDto;
import ru.inf_fans.web_hockey.service.AuthenticationService;
import ru.inf_fans.web_hockey.service.UserServiceImpl;

@RequiredArgsConstructor
@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserServiceImpl userService;

    @PostMapping("/registration")
    public ResponseEntity<?> register(
            @RequestBody RegistrationRequestDto registrationDto
    ) {
        authenticationService.register(registrationDto);
        return ResponseEntity.ok("Регистрация прошла успешно");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(
            @RequestBody LoginRequestDto request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<?> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return authenticationService.refreshToken(request, response);
    }
}
