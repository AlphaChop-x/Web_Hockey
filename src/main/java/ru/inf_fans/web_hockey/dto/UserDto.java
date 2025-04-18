package ru.inf_fans.web_hockey.dto;

import lombok.Data;
import ru.inf_fans.web_hockey.entity.user.enums.Gender;
import ru.inf_fans.web_hockey.entity.user.enums.Role;
import ru.inf_fans.web_hockey.entity.user.enums.RussianHockeyRank;

@Data
public class UserDto {
    private int id;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private Gender gender;
    private RussianHockeyRank rank;
    private Role role;
    private int rating;
}
