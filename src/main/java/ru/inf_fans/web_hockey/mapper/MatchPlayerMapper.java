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
        dto.id = user.getId();
        dto.name = user.getName();
        dto.surname = user.getSurname();
        dto.email = user.getEmail();
        dto.born = user.getBorn();
        dto.rating = user.getRating();

        return dto;
    }

    public User toEntity(MatchPlayerDto dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setId(dto.id);
        user.setName(dto.name);
        user.setSurname(dto.surname);
        user.setEmail(dto.email);
        user.setBorn(dto.born);
        user.setRating(dto.rating);

        return user;
    }
}
