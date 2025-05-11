package ru.inf_fans.web_hockey.mapper;

import org.springframework.stereotype.Component;
import ru.inf_fans.web_hockey.dto.UserDto;
import ru.inf_fans.web_hockey.entity.user.UserEntity;
import ru.inf_fans.web_hockey.entity.user.enums.Gender;
import ru.inf_fans.web_hockey.entity.user.enums.RussianHockeyRank;

import java.util.Date;

@Component
public class UserEntityMapper {

    public UserEntity toUserEntity(UserDto userDto) {
        UserEntity userEntity = new UserEntity();

        userEntity.setName(userDto.name());
        userEntity.setSurname(userDto.surname());
        userEntity.setPatronymic(userDto.patronymic());
        userEntity.setPassword(userDto.password());
        userEntity.setEmail(userDto.email());
        userEntity.setPhoneNumber(userDto.phoneNumber());
        userEntity.setGender(userDto.gender());
        userEntity.setBorn(userDto.born());
        userEntity.setRank(userDto.rank());
        userEntity.setRating(userDto.rating());

        return userEntity;
    }

    public UserDto toUserDto(UserEntity userEntity) {

        String name = userEntity.getName();
        String surname = userEntity.getSurname();
        String patronymic = userEntity.getPatronymic();
        String email = userEntity.getEmail();
        String password = userEntity.getPassword();
        String phoneNumber = userEntity.getPhoneNumber();
        Gender gender = userEntity.getGender();
        Date born = userEntity.getBorn();
        RussianHockeyRank rank = userEntity.getRank();
        Float rating = userEntity.getRating();

        return new UserDto(name, surname, patronymic, email, password, phoneNumber, gender, born, rank, rating);
    }
}
