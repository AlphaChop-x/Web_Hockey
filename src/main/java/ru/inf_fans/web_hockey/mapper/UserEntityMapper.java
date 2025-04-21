package ru.inf_fans.web_hockey.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.inf_fans.web_hockey.dto.UserDto;
import ru.inf_fans.web_hockey.entity.user.UserEntity;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {

    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "rating", constant = "2200.0f")
    UserEntity toUserEntity(UserDto userDto);
}
