package ru.inf_fans.web_hockey.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TournamentApiDto(
        Long id,
        String name,
        String location,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
    public Long getId() {
        return id;
    }
}
