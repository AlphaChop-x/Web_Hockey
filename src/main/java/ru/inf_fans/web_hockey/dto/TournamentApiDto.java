package ru.inf_fans.web_hockey.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record TournamentApiDto(
        Long id,
        String name,
        String location,
        LocalDate startDate,
        LocalDate endDate
) {
    public Long getId() {
        return id;
    }
}
