package ru.inf_fans.web_hockey.dto;

import lombok.Data;
import ru.inf_fans.web_hockey.entity.enums.Gender;

import java.util.Date;

@Data
public class RegistrationRequestDto {

    private String firstName;
    private String surname;
    private String patronymic;
    private String email;
    private String password;
    private String phoneNumber;
    private Gender gender;
    private Date born;
}
