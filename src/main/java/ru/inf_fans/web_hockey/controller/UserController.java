package ru.inf_fans.web_hockey.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.inf_fans.web_hockey.dto.RegistrationRequestDto;
import ru.inf_fans.web_hockey.service.AuthenticationService;
import ru.inf_fans.web_hockey.service.UserServiceImpl;

import java.util.List;

@Tag(name = "Контроллер пользователей", description = "Контроллер для работы с пользователями, удаление, регистрация, получения списка и т.д")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final AuthenticationService authenticationService;
    private final UserServiceImpl userService;

    @Operation(
            summary = "Зарегистрировать множество пользователей за раз"
    )
    @PostMapping("/addMany")
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<?> addMany(
            @RequestBody List<RegistrationRequestDto> users
    ) {
        for (RegistrationRequestDto user : users) {
            authenticationService.register(user);
        }
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Получить всех зарегистрированных пользователей",
            description = "Возвращает List<CompactUserDto>, где CompactUserDto - {Long id, String name, String surname, Float rating}"
    )

    @GetMapping("")
    public ResponseEntity<?> getUsers() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getAllCompactUsers());
    }
}
