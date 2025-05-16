package ru.inf_fans.web_hockey.mapper;

import org.springframework.stereotype.Component;
import ru.inf_fans.web_hockey.dto.MatchPlayerDto;
import ru.inf_fans.web_hockey.entity.User;

@Component
public class MatchPlayerMapper {

    public MatchPlayerDto toDto(User user) {
        if (user == null) {
            return null;
        }

        MatchPlayerDto dto = new MatchPlayerDto();
        dto.name = user.getName();
        dto.surname = user.getSurname();
        dto.email = user.getEmail();
        dto.born = user.getBorn();
        dto.rating = user.getRating();

        return dto;
    }
}
