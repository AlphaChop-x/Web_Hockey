package ru.inf_fans.web_hockey.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.inf_fans.web_hockey.dto.UserDto;
import ru.inf_fans.web_hockey.entity.user.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "rating", target = "rating")
    UserDto toUserDto(UserEntity userEntity);
}
