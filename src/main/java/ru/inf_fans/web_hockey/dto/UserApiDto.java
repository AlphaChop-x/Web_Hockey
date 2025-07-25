package ru.inf_fans.web_hockey.dto;

import lombok.Builder;
import ru.inf_fans.web_hockey.entity.enums.Role;

import java.util.Date;
import java.util.List;

@Builder
public record UserApiDto(
        Long id,
        String name,
        String email,
        Float rating,
//        List<Role> role,
        Date born
) {

}
