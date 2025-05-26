package ru.inf_fans.web_hockey.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.inf_fans.web_hockey.entity.enums.MatchStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MatchDto {
    public Long id;
    public LocalDateTime startTime;
    public LocalDateTime endTime;
    public List<MatchPlayerDto> firstTeam;
    public List<MatchPlayerDto> secondTeam;
    public int teamAscore;
    public int teamBscore;
    public MatchStatus status;

    @Override
    public String toString() {
        return "\nMatch: " + id + "\nStartTime=" + startTime +
                "\nEndTime=" + endTime + "\nFirstTeam: " + firstTeam
                + "\nSecondTeam=" + secondTeam +
                "\nTeamAscore=" + teamAscore + "\nTeamBscore=" + teamBscore + "\nStatus=" + status;
    }

    public List<MatchPlayerDto> getPlayers() {
        return Stream.concat(firstTeam.stream(), secondTeam.stream()).toList();
    }

}
