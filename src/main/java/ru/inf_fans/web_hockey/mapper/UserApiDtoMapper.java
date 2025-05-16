package ru.inf_fans.web_hockey.mapper;

import org.springframework.stereotype.Component;
import ru.inf_fans.web_hockey.dto.UserApiDto;
import ru.inf_fans.web_hockey.entity.User;

import java.util.Date;

@Component
public class UserApiDtoMapper {
    public UserApiDto toUserApiDto(User user) {

        Long id = user.getId();
        String name = user.getName();
        String email = user.getEmail();
        Float rating = user.getRating();
        Date born = user.getBorn();

        return new UserApiDto(id, name, email, rating, born);
    }
}
