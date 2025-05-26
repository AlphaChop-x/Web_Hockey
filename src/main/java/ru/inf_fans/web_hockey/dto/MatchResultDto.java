package ru.inf_fans.web_hockey.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MatchResultDto {
    private int scoreA;
    private int scoreB;
}
