package ru.inf_fans.web_hockey.mapper;

import org.mapstruct.Mapper;
import ru.inf_fans.web_hockey.dto.UserApiDto;
import ru.inf_fans.web_hockey.entity.user.UserEntity;

@Mapper(componentModel = "spring")
public interface UserApiDtoMapper {
    UserApiDto toUserApiDto(UserEntity userEntity);
}
