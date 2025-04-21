package ru.inf_fans.web_hockey.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;
import ru.inf_fans.web_hockey.entity.user.enums.Gender;
import ru.inf_fans.web_hockey.entity.user.enums.RussianHockeyRank;

import java.util.Date;

@Builder
public record UserDto(
        @NotNull(message = " is mandatory") String name,
        @NotNull(message = " is mandatory") String surname,
        String patronymic,
        @NotNull(message = " is mandatory") String email,
        @NotNull(message = "Password is mandatory") String password,
        @NotNull(message = " is mandatory") String phoneNumber,
        @NotNull(message = " is mandatory") Gender gender,
        @NotNull(message = " is mandatory") @DateTimeFormat(pattern = "yyyy-MM-dd") Date born,
        @NotNull(message = " is mandatory") RussianHockeyRank rank) {
}
