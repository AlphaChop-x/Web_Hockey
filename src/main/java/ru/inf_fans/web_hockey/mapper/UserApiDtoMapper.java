package ru.inf_fans.web_hockey.mapper;

import org.springframework.stereotype.Component;
import ru.inf_fans.web_hockey.dto.UserApiDto;
import ru.inf_fans.web_hockey.entity.user.UserEntity;
import ru.inf_fans.web_hockey.entity.user.enums.Role;

import java.util.Date;
import java.util.List;

@Component
public class UserApiDtoMapper {
    public UserApiDto toUserApiDto(UserEntity userEntity) {

        int id = userEntity.getId();
        String name = userEntity.getName();
        String email = userEntity.getEmail();
        Float rating = userEntity.getRating();
        List<Role> role = userEntity.getRole();
        Date born = userEntity.getBorn();

        return new UserApiDto(id, name, email, rating, role, born);
    }
}
