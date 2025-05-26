package ru.inf_fans.web_hockey.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.inf_fans.web_hockey.entity.enums.Gender;
import ru.inf_fans.web_hockey.validation.PhoneNumberValid;

import java.util.Date;

@Schema(description = "DTO для регистрации пользователя")
@Data
public class RegistrationRequestDto {
    @Schema(description = "Имя пользователя", example = "Иван")
    private String firstName;
    @Schema(description = "Фамилия пользователя", example = "Иванов")
    private String surname;
    @Schema(description = "Отчество пользователя", example = "Иванович")
    private String patronymic;
    @Schema(description = "Почта пользователя", example = "ivan.ivanov@gmail.com")
    private String email;
    @Schema(description = "Пароль пользователя", example = "password1234")
    private String password;
    @Schema(description = "Телефон пользователя", example = "+79012345678")
    @PhoneNumberValid
    private String phoneNumber;
    @Schema(description = "Пол пользователя", allowableValues = {"MALE", "FEMALE"})
    private Gender gender;
    @Schema(description = "Дата рождения", example = "2004-02-04")
    private Date born;
}
