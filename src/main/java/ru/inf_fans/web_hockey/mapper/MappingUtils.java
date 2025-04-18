package ru.inf_fans.web_hockey.mapper;

import org.springframework.stereotype.Service;
import ru.inf_fans.web_hockey.dto.UserDto;
import ru.inf_fans.web_hockey.entity.user.User;

@Service
public class MappingUtils {
    public UserDto mapToUserDto(User userEntity) {
        UserDto userDto = new UserDto();
        userDto.setId(userEntity.getId());
        userDto.setName(userEntity.getName());
        userDto.setSurname(userEntity.getSurname());
        userDto.setEmail(userEntity.getEmail());
        userDto.setPhoneNumber(userEntity.getPhoneNumber());
        userDto.setGender(userEntity.getGender());
        userDto.setRank(userEntity.getRank());
        userDto.setRole(userEntity.getRole());
        return userDto;
    }

    public User mapToUserEntity(UserDto userDto) {
        User userEntity = new User();
        userEntity.setId(userDto.getId());
        userEntity.setName(userDto.getName());
        userEntity.setSurname(userDto.getSurname());
        userEntity.setEmail(userDto.getEmail());
        userEntity.setPhoneNumber(userEntity.getPhoneNumber());
        userEntity.setGender(userEntity.getGender());
        userEntity.setRank(userEntity.getRank());
        userEntity.setRole(userEntity.getRole());
        return userEntity;
    }
}
