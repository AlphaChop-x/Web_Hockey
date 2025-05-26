package ru.inf_fans.web_hockey.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.inf_fans.web_hockey.entity.enums.Role;

@AllArgsConstructor
@Getter
@Setter
public class LoginResponseDto {
    Long id;
    String name;
    String surname;
    Role role;
    private String accessToken;
    private String refreshToken;

    public LoginResponseDto(Long id, String name, String surname, Role role) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.role = role;
    }
}
