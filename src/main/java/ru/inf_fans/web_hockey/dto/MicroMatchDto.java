package ru.inf_fans.web_hockey.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class MicroMatchDto {
    public int id;
    public List<MatchPlayerDto> firstTeam;
    public List<MatchPlayerDto> secondTeam;

    @Override
    public String toString() {
        return "\nMatch: " + id + "\nFirstTeam: " + firstTeam + "\nSecondTeam=" + secondTeam;
    }
}
