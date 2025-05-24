package ru.inf_fans.web_hockey.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class MatchDto {
    public int id;
    public LocalDateTime startTime;
    public LocalDateTime endTime;
    public List<MatchPlayerDto> firstTeam;
    public List<MatchPlayerDto> secondTeam;
    public int teamAscore;
    public int teamBscore;

    @Override
    public String toString() {
        return "\nMatch: " + id + "\nStartTime=" + startTime +
                "\nEndTime=" + endTime + "\nFirstTeam: " + firstTeam
                + "\nSecondTeam=" + secondTeam +
                "\nTeamAscore=" + teamAscore + "\nTeamBscore=" + teamBscore;
    }
}
