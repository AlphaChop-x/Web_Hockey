package ru.inf_fans.web_hockey.mapper;

import org.springframework.stereotype.Component;
import ru.inf_fans.web_hockey.dto.CompactUserDto;
import ru.inf_fans.web_hockey.dto.UserApiDto;
import ru.inf_fans.web_hockey.dto.UserDto;
import ru.inf_fans.web_hockey.entity.User;
import ru.inf_fans.web_hockey.entity.enums.Gender;

import java.util.Date;

@Component
public class UserEntityMapper {

    public CompactUserDto toCompactUserDto(User user) {
        CompactUserDto compactUserDto = new CompactUserDto();

        compactUserDto.setId(user.getId());
        compactUserDto.setName(user.getName());
        compactUserDto.setSurname(user.getSurname());
        compactUserDto.setRating(user.getRating());

        return compactUserDto;
    }

    public User toUserEntity(UserDto userDto) {
        User user = new User();

        user.setName(userDto.name());
        user.setSurname(userDto.surname());
        user.setPatronymic(userDto.patronymic());
        user.setPassword(userDto.password());
        user.setEmail(userDto.email());
        user.setPhoneNumber(userDto.phoneNumber());
        user.setGender(userDto.gender());
        user.setBorn(userDto.born());
        user.setRating(userDto.rating());

        return user;
    }

    public UserDto toUserDto(User user) {

        String name = user.getName();
        String surname = user.getSurname();
        String patronymic = user.getPatronymic();
        String email = user.getEmail();
        String password = user.getPassword();
        String phoneNumber = user.getPhoneNumber();
        Gender gender = user.getGender();
        Date born = user.getBorn();
        Float rating = user.getRating();

        return new UserDto(name, surname, patronymic, email, password, phoneNumber, gender, born, rating);
    }
}
