package ru.inf_fans.web_hockey.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;
import ru.inf_fans.web_hockey.entity.user.enums.Gender;
import ru.inf_fans.web_hockey.entity.user.enums.RussianHockeyRank;
import ru.inf_fans.web_hockey.validation.EmailValid;
import ru.inf_fans.web_hockey.validation.PhoneNumberValid;

import java.util.Date;

@Builder
@Schema(description = "Пользователь")
public record UserDto(
        @NotBlank(message = "Имя обязательно к заполнению") String name,
        @NotBlank(message = "Фамилия обязательна к заполнению") String surname,
        String patronymic,
        @NotBlank(message = "Почта обязательна к заполнению") @EmailValid(message = "Некорректная почта!\n") @Email String email,
        @NotBlank(message = "Пароль обязателен к заполнению") String password,
        @NotBlank(message = "Телефон обязателен к заполнению")
        @PhoneNumberValid(message = "Номер должен начинаться с +7 и заполняться без пробелов и '(', ')'!\n")
        String phoneNumber,
        @NotNull(message = "Пол обязателен к заполнению") Gender gender,
        @NotNull(message = "Дата рождения обязательна") @DateTimeFormat(pattern = "yyyy-MM-dd") Date born,
        @NotNull(message = "Ранг обязателен") RussianHockeyRank rank,
        Float rating) {
}
